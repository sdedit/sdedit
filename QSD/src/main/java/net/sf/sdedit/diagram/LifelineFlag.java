package net.sf.sdedit.diagram;

import java.util.HashSet;
import java.util.Set;

public enum LifelineFlag {

	ANONYMOUS('a'),
	
	ROLE('r'),
	
	/**
	 * Flag denoting a lifeline belongs to a process (a passive actor)
	 */
	PROCESS('p'),
	
	/**
	 * Flag denoting if a thread is statically spawned.
	 */	
	THREAD('t'),
	
	/**
	 * Flag denoting if a lifeline is to be destroyed when it has performed
	 * its last activity
	 */
	AUTOMATIC('x'),
	
	EXTERNAL('e'),
	
	VARIABLE('v');
	
	private char c;
	
	LifelineFlag(char c) {
		this.c = c;
	}
	
	public static Set<LifelineFlag> getFlags (String str) {
		if (str == null) {
			return null;
		}
		Set<LifelineFlag> flags = new HashSet<LifelineFlag>();
		for (int i = 0; i < str.length(); i++) {
			for (LifelineFlag flag : LifelineFlag.values()) {
				if (flag.c == str.charAt(i)) {
					flags.add(flag);
				}
			}
		}
		return flags;
	}
	
	public boolean in(Set<LifelineFlag> flags) {
		if (flags == null) {
			return false;
		}
		return flags.contains(this);
	}
	
	public static String toString(Set<LifelineFlag> flags) {
		String str = "";
		for (LifelineFlag flag : flags) {
			str+=flag.c;
		}
		return str;
	}
	
}
