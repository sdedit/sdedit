package com.sun.javadoc;

public interface ProgramElementDoc {
	
	public abstract ClassDoc containingClass();

	public abstract PackageDoc containingPackage();

	public abstract String qualifiedName();

	public abstract int modifierSpecifier();

	public abstract String modifiers();

	public abstract boolean isPublic();

	public abstract boolean isProtected();

	public abstract boolean isPrivate();

	public abstract boolean isPackagePrivate();

	public abstract boolean isStatic();

	public abstract boolean isFinal();
	
}
