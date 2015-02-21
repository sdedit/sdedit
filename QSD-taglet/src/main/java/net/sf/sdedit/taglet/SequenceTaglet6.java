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

import net.sf.sdedit.util.Utilities;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletOutput;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;

/**
 * 
 * 
 * 
 */
public class SequenceTaglet6 extends SequenceTaglet implements Taglet {
	
	/**
	 * Registers an instance of this taglet class.
	 * 
	 * @param tagletMap
	 *            used for registering (maps tag names onto taglets)
	 */
	public static void register(Map<String, Taglet> tagletMap) {
		SequenceTaglet6 tag = new SequenceTaglet6();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}
	
	private static void warn(TagletWriter writer, String type, String warning,
			String name) {
		Object conf = Utilities.invoke("configuration", writer);
		Object message = Utilities.getField(conf, "message");
		Utilities.invoke("warning", message, type, warning, name);
	}

	private String toString(Tag tag, TagletWriter writer) {
		String output;
		try {
			output = makeString(tag);
		} catch (SequenceTagletException ste) {
			warn(writer, "doclet.in", ste.getMessage(), tag.holder().name());
			return ste.output;
		}
		return output;
	}

	private String toString(Tag[] tags, TagletWriter writer) {
		String output = "";
		for (Tag tag : tags) {
			try {
				output += makeString(tag);
			} catch (SequenceTagletException ste) {
				warn(writer, "doclet.in", ste.getMessage(), tags[0].holder()
						.name());
				output += ste.output;
			}
		}
		return output;
	}

	public TagletOutput getTagletOutput(Tag tag, TagletWriter writer)
			throws IllegalArgumentException {
		TagletOutput out = writer.getTagletOutputInstance();
		out.setOutput(toString(tag, writer));
		return out;
	}

	public TagletOutput getTagletOutput(Doc holder, TagletWriter writer)
			throws IllegalArgumentException {
		TagletOutput out = writer.getTagletOutputInstance();
		Tag[] tags = holder.tags(getName());
		if (tags.length == 0) {
			return null;
		}
		out.setOutput(toString(tags, writer));
		return out;
	}
}
