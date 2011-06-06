// Copyright (c) 2006 - 2011, Markus Strauch.
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, 
// this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
// this list of conditions and the following disclaimer in the documentation 
// and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
// THE POSSIBILITY OF SUCH DAMAGE.

package net.sf.sdedit.diagram;

import java.util.LinkedList;
import java.util.ListIterator;

import net.sf.sdedit.drawable.Arrow;
import net.sf.sdedit.drawable.Fragment;
import net.sf.sdedit.drawable.SequenceElement;
import net.sf.sdedit.error.SyntaxError;
import net.sf.sdedit.message.Answer;

/**
 * A <tt>FragmentManager</tt> is responsible for processing the fragments
 * occurring in a diagram. During the process, the <tt>FragmentManager</tt>
 * sets a fragment in three states, successively:
 * <ol>
 * <li><strong>open</strong>: sequence elements are added to the fragment</li>
 * <li><strong>closing</strong>: answers to included messages are still being
 * added to the fragment</li>
 * <li><strong>finished</strong>: nothing is being added anymore</li>
 * </ol>
 * 
 * @author Markus Strauch
 * 
 */
public final class FragmentManager {
	private final Diagram diagram;

	/**
	 * This list contains the fragments that have been opened and for which we
	 * have not yet seen a close command (see {@linkplain
	 * DiagramDataProvider#closeFragment()}). When a sequence element is added
	 * via {@linkplain #addSequenceElement(SequenceElement)} it will be added to
	 * all fragments in this list.
	 */
	private final LinkedList<Fragment> openFragments;

	/**
	 * This list contains the fragments for which we have already seen a close
	 * command (see {@linkplain DiagramDataProvider#closeFragment()}). Sequence
	 * elements added via {@linkplain #addSequenceElement(SequenceElement)} will
	 * not be added to the comments in this list. When an answer is processed,
	 * {@linkplain #finishFragmentsNotIncluding(Answer)} is called and all
	 * fragments that do not include the message to which the given answer is
	 * the answer will be finished and removed from the
	 * <tt>closingFragments</tt> list.
	 */
	private final LinkedList<Fragment> closingFragments;

	/**
	 * The list of fragment labels that have currently been opened. The list is
	 * emptied when something else than a fragment label (for instance, a
	 * message) is delivered by the DiagramDataProvider.
	 */
	private final LinkedList<String> fragmentLabels;

	/**
	 * If this string is not null, it contains the label of the next section of
	 * the current fragment.
	 */
	private String fragmentSectionLabel;

	/**
	 * Creates a new <tt>FragmentManager</tt> responsible for the fragments
	 * occurring in the given diagram.
	 * 
	 * @param diagram
	 *            the given diagram
	 */
	public FragmentManager(Diagram diagram) {
		this.diagram = diagram;
		openFragments = new LinkedList<Fragment>();
		closingFragments = new LinkedList<Fragment>();
		fragmentLabels = new LinkedList<String>();
	}

	/**
	 * Returns the labels for the fragments for which we have already seen an
	 * open command (see {@linkplain DiagramDataProvider#openFragment()}) but
	 * for which we have not yet created a {@linkplain Fragment} representation.
	 * 
	 * @return the labels for the fragments for which we have already seen an
	 *         open command, but for which there is not yet a <tt>Fragment</tt>
	 *         object
	 */
	public LinkedList<String> getFragmentLabels() {
		return fragmentLabels;
	}

	/**
	 * When we have just seen the beginning of a new fragment section, this
	 * method returns its label, otherwise <tt>null</tt>.
	 * 
	 * @return the label of a new fragment section or <tt>null</tt>
	 */
	public String getFragmentSectionLabel() {
		return fragmentSectionLabel;
	}

	/**
	 * This method should be called when for all fragments corresponding to
	 * {@linkplain #getFragmentLabels()} there has been a <tt>Fragment</tt>
	 * object created. The list is cleared then.
	 */
	public void clearLabels() {
		fragmentLabels.clear();
	}

	/**
	 * This method should be called when a new section has been just created.
	 */
	public void clearSectionLabel() {
		fragmentSectionLabel = null;
	}

	/**
	 * This method uses the provider of the diagram data (see
	 * {@linkplain DiagramDataProvider}, {@linkplain Diagram#getDataProvider()}
	 * and takes some action if the provider says that a fragment is opened or
	 * closed, or if the beginning of a new section is specified.
	 * 
	 * @return true if the diagram data provider currently specifies something
	 *         that has to do with a fragment
	 * @throws SyntaxError
	 *             if the fragment specification is syntactically wrong
	 */
	public boolean readFragments() throws SyntaxError {
		DiagramDataProvider provider = diagram.getDataProvider();
		if (provider.closeFragment()) {
			closeRecentFragment();
			return true;
		}

		String comment = provider.openFragment();
		if (comment != null) {
			fragmentLabels.add(comment);
			return true;
		}
		String newSeparator = provider.getFragmentSeparator();
		if (newSeparator != null) {
			if (fragmentSectionLabel != null) {
				throw new SyntaxError(provider, "double separator");
			}
			if (getRecentFragment() == null) {
				throw new SyntaxError(provider, "no comment open");
			}
			fragmentSectionLabel = newSeparator;
			return true;
		}
		return false;
	}

	/**
	 * Creates {@linkplain Fragment} objects for all pending fragment labels in
	 * {@linkplain #getFragmentLabels()} and adds them to the list of open
	 * fragments.
	 */
	public void openFragments() {

		finishFragments();

		for (String fragment : fragmentLabels) {
			if (fragment.startsWith("[c")) {
				fragment = fragment.substring(2, fragment.length() - 1).trim();
			}
			if (fragment.length() > 0 && fragment.charAt(0) == ':') {
				fragment = fragment.substring(1);
				int s = fragment.indexOf(' ');
				if (s == -1) {
					openFragment(fragment, "");
				} else {
					String type = fragment.substring(0, s);
					String name = fragment.substring(s + 1);
					openFragment(type, name);
				}
			} else {
				openFragment("", fragment);
			}
		}

		if (fragmentSectionLabel != null) {
			Fragment recent = getRecentFragment();
			if (recent != null) {
				recent.addSection(fragmentSectionLabel);
			}
		}
	}

	private void openFragment(String type, String text) {
		Fragment fragment = new Fragment(type, text, diagram);
		int textHeight = diagram.getPaintDevice().getTextHeight();
		int extension = 5 + (fragment.getCondition().length() > 0 ? textHeight * 2
				: textHeight);
		diagram.getPaintDevice().announce(
				diagram.getConfiguration().getFragmentMargin() + extension);
		diagram.extendLifelines(diagram.getConfiguration().getFragmentMargin());

		fragment.setTop(diagram.getVerticalPosition());

		diagram.extendLifelines(extension);
		openFragments.addLast(fragment);
		diagram.getPaintDevice().addOtherDrawable(fragment);
		int l = openFragments.size() - 1;
		for (Fragment open : openFragments) {
			open.setLevel(l);
			l--;
		}
	}

	/**
	 * Returns a flag indicating if there are fragments for which we have not
	 * seen a close command yet (see
	 * {@linkplain DiagramDataProvider#closeFragment()}).
	 * 
	 * @return a flag indicating if there are fragments for which we have not
	 *         seen a close command
	 */
	public boolean openFragmentsExist() {
		return openFragments.size() > 0;
	}

	/**
	 * Sets all the closing fragments into finished state.
	 */
	public void finishFragments() {
		for (Fragment comment : closingFragments) {
			finishFragment(comment);
		}
		closingFragments.clear();
	}

	/**
	 * Sets the bottom line of a closing fragment when there will never be added
	 * anything to it.
	 * 
	 * @param comment
	 *            a closing fragment such that there never will be anything
	 *            added to it
	 */
	private void finishFragment(Fragment comment) {
		diagram
				.extendLifelines(diagram.getConfiguration()
						.getFragmentPadding());
		comment.setBottom(diagram.getVerticalPosition());
		diagram.extendLifelines(diagram.getConfiguration().getFragmentMargin());
	}

	/**
	 * Sets the closing fragments that do not include the message to which the
	 * given answer is the answer into finished state.
	 * 
	 * @param answer
	 *            an answer
	 */
	public void finishFragmentsNotIncluding(Answer answer) {
		ListIterator<Fragment> lic = closingFragments.listIterator();
		Arrow arrow = answer.getForwardMessage().getArrow();
		while (lic.hasNext()) {
			Fragment comment = lic.next();
			if (!comment.containsElement(arrow)) {
				lic.remove();
				finishFragment(comment);
			}
		}
	}

	/**
	 * Adds the given sequence element to all open and closing fragments.
	 * 
	 * @param elem
	 *            a sequence element to be added to all open and closing
	 *            fragments
	 */
	public void addSequenceElement(SequenceElement elem) {
		for (Fragment comment : openFragments) {
			comment.addElement(elem);
		}
		for (Fragment comment : closingFragments) {
			comment.addElement(elem);
		}
	}

	private void closeRecentFragment() throws SyntaxError {
		if (openFragments.isEmpty()) {
			throw new SyntaxError(diagram.getDataProvider(),
					"There is no open comment");
		}
		Fragment comment = openFragments.removeLast();
		closingFragments.addLast(comment);
	}

	private Fragment getRecentFragment() {
		if (openFragments.isEmpty()) {
			return null;
		}
		return openFragments.getLast();
	}

}
//{{core}}
