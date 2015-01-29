// Copyright (c) 2006 - 2011, Markus Strauch.
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, 
// this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
// this list of conditions and the following disclaimer in the documentation 
// and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
// THE POSSIBILITY OF SUCH DAMAGE.
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
