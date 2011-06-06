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

import net.sf.sdedit.drawable.Note;
import net.sf.sdedit.error.SyntaxError;
import net.sf.sdedit.util.Pair;

/**
 * A <tt>DiagramDataProvider</tt> reads object and message specifications from
 * some source and creates {@linkplain Lifeline} and {@linkplain MessageData}
 * representations from them.
 * <p>
 * It should be used in such a way:
 * 
 * <pre>
 * 
 * DiagramDataProvider provider = new ...;
 * while (provider.advance()) {
 *   Lifeline lifeline = provider.nextObject();
 *   // do something with the lifeline
 * }
 * 
 * while (provider.advance()) {
 *   
 *   if (provider.pass()) {
 *     continue;
 *   }
 * 
 *   String comment = provider.openComment();
 *   if (comment != null) {
 *     // handle the comment, if you are interested
 *     continue;
 *   }
 *   if (provider.closeComment()) {
 *     // the comment most recently returned by openComment() is
 *     // closed, so react to it, if you are interested
 *     continue;
 *   }
 *   
 *   MessageData data = provider.nextMessage();
 *   // do something with the message data
 * }
 * </pre>
 * 
 * @author Markus Strauch
 * 
 */

public interface DiagramDataProvider {
	
    /**
     * Returns the diagram's title or <tt>null</tt>, if no title is defined
     * 
     * @return the diagram's title or <tt>null</tt>, if no title is defined
     */
    public String getTitle();

    /**
     * Returns an array of strings to be used as a description of the diagram.
     * 
     * @return an array of strings to be used as a description of the diagram
     */
    public String[] getDescription();

    /**
     * Advances to the next data specification in the current section and
     * returns true if there is one. If the current section is the object
     * section and this method returns <tt>false</tt>, the provider advances
     * to the message section.
     * 
     * @return true if there is more data specified in the current section
     */
    public boolean advance();

    /**
     * Returns the next Lifeline object specifying an object or actor appearing
     * on the diagram.
     * 
     * @return the next Lifeline object specifying an object or actor appearing
     *         on the diagram or <tt>null</tt> if there is not another object
     *         specified
     * @throws SyntaxError
     *             if the object is specified syntactically wrong
     */
    public Lifeline nextObject() throws SyntaxError;

    /**
     * Returns the next MessageData object specifying a message appearing on the
     * diagram.
     * 
     * @return next MessageData object specifying a message appearing on the
     *         diagram, or <tt>null</tt> if there is no more message specified
     * @throws SyntaxError
     *             if the message is specified syntactically wrong
     */
    public MessageData nextMessage() throws SyntaxError;

    /**
     * Gets the current state of the provider. The state depends on how far it
     * has advanced when reading the data.
     * 
     * @return the current state of the provider or <tt>null</tt> if the
     *         provider does not provide information about its state
     */
    public Object getState();

    /**
     * If at the current position in the data the beginning of a fragment is
     * specified, the title of the fragment is returned, otherwise
     * <tt>null</tt>.
     * 
     * @return a fragment title, if one is specified currently, otherwise <tt>null</tt>
     */
    public String openFragment();

    /**
     * Returns true if the last fragment returned by
     * {@linkplain #openFragment()} is to be closed now (so it is a comment on
     * the messages that were read between the last time
     * {@linkplain #openFragment()} did not return null and now.
     * 
     * @return true if the last comment returned by {@linkplain #openFragment()}
     *         is to be closed now
     */
    public boolean closeFragment();

    /**
     * If at the current position in the data a note is specified,
     * returns it, otherwise <tt>null</tt>.
     * 
     * @return a note, if one is specified at the current position,
     *         otherwise <tt>null</tt>
     * @throws SyntaxError
     */
    public Note getNote() throws SyntaxError;

    /**
     * Sets the diagram that uses this DiagramDataProvider.
     * 
     * @param diagram
     *            the diagram that uses this DiagramDataProvider
     */
    public void setDiagram(Diagram diagram);
    
    public Diagram getDiagram();

    /**
     * Returns a pair consisting of a lifeline and the number of a description
     * if the description is to be associated to the lifeline at the current
     * position.
     * 
     * @return a pair as described or <tt>null</tt> if no such association is
     *         currently specified
     * @throws SyntaxError
     */
    public Pair<Lifeline, Integer> getEventAssociation() throws SyntaxError;

    /**
     * If the following messages belong to a new section of the most recently
     * opened fragment, this method returns the title of the section, otherwise
     * <tt>null</tt>
     * 
     * @return the title of a fragment section or <tt>null</tt>
     */
    public String getFragmentSeparator();
    
    
}
//{{core}}
