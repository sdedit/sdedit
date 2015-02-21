package com.sun.javadoc;

public interface Tag {
	
	public abstract String name();

	public abstract Doc holder();

	public abstract String kind();

	public abstract String text();

	public abstract String toString();

	public abstract Tag[] inlineTags();

	public abstract Tag[] firstSentenceTags();

}
