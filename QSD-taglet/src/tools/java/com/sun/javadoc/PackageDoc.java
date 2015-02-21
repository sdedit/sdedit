package com.sun.javadoc;

public interface PackageDoc {
	
	public abstract ClassDoc[] allClasses(boolean paramBoolean);

	public abstract ClassDoc[] allClasses();

	public abstract ClassDoc[] ordinaryClasses();

	public abstract ClassDoc[] exceptions();

	public abstract ClassDoc[] errors();

	public abstract ClassDoc[] enums();

	public abstract ClassDoc[] interfaces();

	public abstract ClassDoc findClass(String paramString);

}
