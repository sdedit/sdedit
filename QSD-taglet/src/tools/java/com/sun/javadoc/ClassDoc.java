package com.sun.javadoc;

public interface ClassDoc {
	
	public abstract boolean isAbstract();

	public abstract boolean isSerializable();

	public abstract boolean isExternalizable();

	//public abstract MethodDoc[] serializationMethods();

	//public abstract FieldDoc[] serializableFields();

	public abstract boolean definesSerializableFields();

	public abstract ClassDoc superclass();

	//public abstract Type superclassType();

	public abstract boolean subclassOf(ClassDoc paramClassDoc);

	public abstract ClassDoc[] interfaces();

	//public abstract Type[] interfaceTypes();

	//public abstract TypeVariable[] typeParameters();

	//public abstract ParamTag[] typeParamTags();

	//public abstract FieldDoc[] fields();

	//public abstract FieldDoc[] fields(boolean paramBoolean);

	//public abstract FieldDoc[] enumConstants();

	//public abstract MethodDoc[] methods();

	//public abstract MethodDoc[] methods(boolean paramBoolean);

	//public abstract ConstructorDoc[] constructors();

	//public abstract ConstructorDoc[] constructors(boolean paramBoolean);

	public abstract ClassDoc[] innerClasses();

	public abstract ClassDoc[] innerClasses(boolean paramBoolean);

	public abstract ClassDoc findClass(String paramString);

	public abstract String qualifiedName();

}
