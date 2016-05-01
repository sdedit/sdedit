package com.sun.javadoc;

public interface ClassDoc {
	
	public abstract boolean isAbstract();

	public abstract boolean isSerializable();

	public abstract boolean isExternalizable();

	public abstract boolean definesSerializableFields();

	public abstract ClassDoc superclass();

	public abstract boolean subclassOf(ClassDoc paramClassDoc);

	public abstract ClassDoc[] interfaces();

	public abstract ClassDoc[] innerClasses();

	public abstract ClassDoc[] innerClasses(boolean paramBoolean);

	public abstract ClassDoc findClass(String paramString);

	public abstract String qualifiedName();

}
