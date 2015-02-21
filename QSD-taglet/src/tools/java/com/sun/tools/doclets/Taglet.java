package com.sun.tools.doclets;

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

	public abstract String toString(Tag paramTag);

	public abstract String toString(Tag[] paramArrayOfTag);
	
}
