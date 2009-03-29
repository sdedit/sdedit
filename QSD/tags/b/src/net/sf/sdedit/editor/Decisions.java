package net.sf.sdedit.editor;

import java.util.TreeSet;

import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.util.Utilities;


public class Decisions {
	
	public static final int CLOSE_THREAD_TREE_PANEL = 0;
	
	private static final String [] decisionStrings;
	
	static {
		decisionStrings = new String [1];
		decisionStrings [CLOSE_THREAD_TREE_PANEL] = "Do not show this message again";
	}
	
	private Decisions () {
		/* empty */		
	}
	
	@SuppressWarnings("unchecked")
	public static String getDecisionString (int decisionKey) {
		if (decisionKey < 0 || decisionKey >= decisionStrings.length) {
			throw new IllegalArgumentException ("Illegal decision key: " + decisionKey);
		}
		String unused = ConfigurationManager.getGlobalConfiguration().getUnusedDecisions();
		TreeSet<String> set = (TreeSet<String>) (Utilities.toCollection(TreeSet.class, unused.split(",")));
		if (set.contains(String.valueOf(decisionKey))) {
			return null;
		}
		return decisionStrings [decisionKey];
	}
	
	@SuppressWarnings("unchecked")
	public static void setUnused (int decisionKey) {
		if (decisionKey < 0 || decisionKey >= decisionStrings.length) {
			throw new IllegalArgumentException ("Illegal decision key: " + decisionKey);
		}
		String unused = ConfigurationManager.getGlobalConfiguration().getUnusedDecisions();
		TreeSet<String> set = (TreeSet<String>) (Utilities.toCollection(TreeSet.class, unused.split(",")));
		set.add(String.valueOf(decisionKey));
		ConfigurationManager.getGlobalConfiguration().setUnusedDecisions(Utilities.join(",", set));
	}
}
