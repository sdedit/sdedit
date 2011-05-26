package net.sf.sdedit.util.xl;

import java.util.HashSet;
import java.util.Set;

import net.sf.sdedit.util.Ref;
import net.sf.sdedit.util.Utilities;

public abstract class XLTypeChecker {
	
	protected static class DataAccess {
		
		private String accessType;
		
		private int index;
		
		private String className;
		
		public DataAccess(String accessType, int index, String className) {
			super();
			this.accessType = accessType;
			this.index = index;
			this.className = className;
		}

		public String getAccessType() {
			return accessType;
		}

		public int getIndex() {
			return index;
		}

		public String getClassName() {
			return className;
		}
		
		public boolean equals(Object o) {
			DataAccess other = (DataAccess) o;
			return accessType.equals(other.accessType) && index == other.index && className.equals(other.className);
		}
		
		public int hashCode () {
			return accessType.hashCode() + index * className.hashCode();
		}
		
		public String toString () {
			return accessType + "[" + index + "] of " + className;
		}
		
	}
	
	private Set<DataAccess> declaredAccesses;
	
	private Set<DataAccess> implementedAccesses;
	
	protected XLTypeChecker () {
		declaredAccesses = new HashSet<DataAccess>();
		implementedAccesses = new HashSet<DataAccess>();
	}
	
	protected void addAccess (char dOrI, String type, int index, String className) {
		DataAccess acc = new DataAccess(type, index, className);
		if (dOrI == 'd') {
			declaredAccesses.add(acc);
		} else if (dOrI == 'i') {
			implementedAccesses.add(acc);
		} else {
			throw new IllegalArgumentException ("illegal type, must be 'd' or 'i': " + dOrI);
		}
	}
	
	public void setType (XLType type) {
		declaredAccesses.clear();
		implementedAccesses.clear();
		int i = 0;
		for (Class<?> cls : type.getInputTypes()) {
			addAccess('d', "input", i++, cls.getName());
		}
		i = 0;
		for (Class<?> cls : type.getOutputTypes()) {
			addAccess('d', "output", i++, cls.getName());
		}
		i = 0;
		for (Class<?> cls : type.getPassedTypes()) {
			addAccess('d', "pass", i++, cls.getName());
		}
		i = 0;
		for (Class<?> cls : type.getReceivedTypes()) {
			addAccess('d', "receive", i++, cls.getName());
		}
	}
	
	protected abstract void setImplementedAccesses (Class<?> unitClass);
	

	
	
	
	public void check(Class<?> unitClass, XLType type) {
		setType (type);
		setImplementedAccesses(unitClass);
		Ref<Set<DataAccess>> declaredOnly = new Ref<Set<DataAccess>>();
		Ref<Set<DataAccess>> implementedOnly = new Ref<Set<DataAccess>>();
		if (!Utilities.computeDifference(declaredAccesses, implementedAccesses, declaredOnly, implementedOnly)) {
			System.err.println("Type-checking of " + type.getName() + " failed");
			System.err.println();
			System.err.println("declared, but not implemented: " + declaredOnly.t);
			System.err.println();
			System.err.println("implemented, but not declared: " + implementedOnly.t);
			throw new IllegalArgumentException("unsound XLType: " + type.getName());
		}
		
		
		
	}

}
