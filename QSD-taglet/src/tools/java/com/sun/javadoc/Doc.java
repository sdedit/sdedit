package com.sun.javadoc;

public interface Doc {
	
	public abstract String commentText();

	public abstract Tag[] tags();

	public abstract Tag[] tags(String paramString);

	public abstract Tag[] inlineTags();

	public abstract Tag[] firstSentenceTags();

	public abstract String getRawCommentText();

	public abstract void setRawCommentText(String paramString);

	public abstract String name();

	public abstract int compareTo(Object paramObject);

	public abstract boolean isField();

	public abstract boolean isEnumConstant();

	public abstract boolean isConstructor();

	public abstract boolean isMethod();

	public abstract boolean isAnnotationTypeElement();

	public abstract boolean isInterface();

	public abstract boolean isException();

	public abstract boolean isError();

	public abstract boolean isEnum();

	public abstract boolean isAnnotationType();

	public abstract boolean isOrdinaryClass();

	public abstract boolean isClass();

	public abstract boolean isIncluded();

}
