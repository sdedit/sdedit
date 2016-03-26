package net.sf.sdedit.text;

enum PartialPattern {
	
	LEVELS("(" + "\\[(\\d*),(\\d+)\\]" + "|"
			+ "\\[(\\d+)\\]" + ")"),
	
	PREFIX("(\\(\\d*(,\\d+)?\\))?\\s*(.+)"),
	
	COLON("(?<!\\\\):(?!>)"),
	
	SPAWN("(?<!\\\\):>"),
	
	DOT("(?<!\\\\)\\."),
	
	EQ("(?<!\\\\)=");
	
	private String pattern;
	
	PartialPattern (String pattern) {
		this.pattern = pattern;
	}
	
	public String toString() {
		return pattern;
	}
	
}
