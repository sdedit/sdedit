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

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.sdedit.drawable.Note;
import net.sf.sdedit.error.SyntaxError;
import net.sf.sdedit.message.Message;
import net.sf.sdedit.util.Pair;

public class NoteManager {
	
	private final LinkedList<Note> notes;

	private final Map<Integer, List<Message>> messageAssociation;

	private final Map<Integer, List<Pair<Lifeline, Integer>>> eventAssociation;

	private final Map<String, Note> pendingNotes;

	private int freeNoteNumber;

	private final Diagram diagram;

	public NoteManager(Diagram diagram) {
		this.diagram = diagram;
		notes = new LinkedList<Note>();
		messageAssociation = new HashMap<Integer, List<Message>>();
		eventAssociation = new HashMap<Integer, List<Pair<Lifeline, Integer>>>();
		freeNoteNumber = 0;
		pendingNotes = new HashMap<String, Note>();
	}

	public void associateMessage(int number, Message msg) {
		List<Message> msgs = messageAssociation.get(number);
		if (msgs == null) {
			msgs = new LinkedList<Message>();
			messageAssociation.put(number, msgs);
		}
		msgs.add(msg);
	}

	public void computeArrowAssociations() {
		for (Note description : notes) {
			List<Message> msgs = messageAssociation
					.get(description.getNumber());
			if (msgs != null) {
				for (Message msg : msgs) {
					// This can be a pseudo-message ("_") that returns
					// the control flow to an object.
					// We just ignore the attempt to uselessly associate a
					// note to this.
					if (msg.getArrow() != null) {
						description.addTarget(msg.getArrow().getAnchor());
					}
				}
			}
			List<Pair<Lifeline, Integer>> pairs = eventAssociation
					.get(description.getNumber());
			if (pairs != null) {
				for (Pair<Lifeline, Integer> pair : pairs) {
					int x = pair.getFirst().getView().getLeft()
							+ pair.getFirst().getView().getWidth() / 2;
					Point p = new Point(x, pair.getSecond());
					description.addTarget(p);
				}
			}
		}
	}

	public int getNextFreeNoteNumber() {
		return freeNoteNumber;
	}

	private void associateEvent(Lifeline line, int descriptionNumber) {
		List<Pair<Lifeline, Integer>> pairs = eventAssociation
				.get(descriptionNumber);
		if (pairs == null) {
			pairs = new LinkedList<Pair<Lifeline, Integer>>();
			eventAssociation.put(descriptionNumber, pairs);
		}
		pairs.add(new Pair<Lifeline, Integer>(line, diagram
				.getVerticalPosition()));
	}

	/**
	 * 
	 * @param lifeline
	 *            the name of a lifeline that may have a non-consuming note box
	 *            associated to it
	 */
	void closeNote(String lifeline) {
		Note note = pendingNotes.get(lifeline);
		if (note != null) {
			int diff = note.getTop() + note.getHeight()
					- diagram.getVerticalPosition();
			if (diff > 0) {
				diagram.extendLifelines(diff);
			}
			pendingNotes.remove(lifeline);
		}
	}

	public boolean step() throws SyntaxError {
		Note note = diagram.getDataProvider().getNote();
		if (note != null) {
			freeNoteNumber = Math.max(freeNoteNumber, note.getNumber() + 1);
			diagram.getPaintDevice().addSequenceElement(note);
			notes.add(note);
			closeNote(note.getLocation().getName());
			diagram.getFragmentManager().openFragments();
			diagram.getPaintDevice().announce(note.getHeight());
			note.setTop(diagram.getVerticalPosition());
			if (note.isConsuming()) {
				diagram.extendLifelines(note.getHeight());
			} else {
				pendingNotes.put(note.getLocation().getName(), note);
			}
			if (diagram.getDataProvider().getState() != null) {
				diagram.addToStateMap(note, diagram.getDataProvider()
						.getState());
			}
			diagram.getFragmentManager().clearLabels();
			return true;
		}
		Pair<Lifeline, Integer> eventAssoc = diagram.getDataProvider()
				.getEventAssociation();
		if (eventAssoc != null) {
			associateEvent(eventAssoc.getFirst(), eventAssoc.getSecond());
			return true;
		}
		return false;
	}
}
//{{core}}
