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

package net.sf.sdedit.taglet;

import java.util.Map;

import net.sf.sdedit.util.PWriter;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * This is a taglet that generates sequence diagrams from the contents of
 * <tt>@sequence.diagram</tt> tags. The diagrams are saved as PNG files in a
 * child directory of the javadoc destination directory (as specified by the -d
 * option). Inside the html pages generated by javadoc, the files are referenced
 * using a relative path, so the references remain valid if the pages are moved
 * - for example, uploaded to a webserver.
 * <p>
 * If the first line of the tag content is "quoted" it will be used as title for
 * the diagram instead of the default "Sequence Diagram:"
 * <p>
 * <tt>@sequence.diagram</tt> are no inline tags and they can be used inside
 * classes, and inside package documentation.
 * 
 * @sequence.diagram 
 * 				     "Processing of <tt>@sequence.diagram</tt> tags" 
 *                   user:Actor
 *                   javadoc:Javadoc[a]
 * 
 *                   user:javadoc.generateDocumentation()
 *                   
 * 
 * @author Markus Strauch
 * @author Øystein Lunde
 */
public class SequenceTaglet7 extends SequenceTaglet implements Taglet {

	/**
	 * Registers an instance of this taglet class.
	 * 
	 * @param tagletMap
	 *            used for registering (maps tag names onto taglets)
	 */
	public static void register(Map<String, Taglet> tagletMap) {
		SequenceTaglet7 tag = new SequenceTaglet7();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}

	public String toString(Tag tag) {
		try {
			return makeString(tag);
		} catch (SequenceTagletException e) {
			return e.output;
		}
	}

	public String toString(Tag[] tags) {
		PWriter p = PWriter.create();
		for (Tag tag : tags) {
			try {
				p.println(makeString(tag));
			} catch (SequenceTagletException e) {
				p.println(e.output);
			}
		}
		return p.toString();
	}

}