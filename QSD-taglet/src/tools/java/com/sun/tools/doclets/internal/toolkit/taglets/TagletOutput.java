package com.sun.tools.doclets.internal.toolkit.taglets;

public interface TagletOutput {
	
	public abstract void setOutput(Object paramObject);

	public abstract void appendOutput(TagletOutput paramTagletOutput);

	public abstract boolean hasInheritDocTag();

}
