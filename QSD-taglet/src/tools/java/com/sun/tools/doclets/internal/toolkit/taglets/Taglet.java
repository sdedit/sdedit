package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;

public interface Taglet {
	
	public abstract boolean inField();

	public abstract boolean inConstructor();

	public abstract boolean inMethod();

	public abstract boolean inOverview();

	public abstract boolean inPackage();

	public abstract boolean inType();

	public abstract boolean isInlineTag();

	public abstract String getName();

	public abstract TagletOutput getTagletOutput(Tag paramTag,
			TagletWriter paramTagletWriter) throws IllegalArgumentException;

	public abstract TagletOutput getTagletOutput(Doc paramDoc,
			TagletWriter paramTagletWriter) throws IllegalArgumentException;

}
