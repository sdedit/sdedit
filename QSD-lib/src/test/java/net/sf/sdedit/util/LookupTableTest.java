package net.sf.sdedit.util;

import net.sf.sdedit.util.LookupTable.TooManyMatches;

public class LookupTableTest {
	
	static class Fact {
		
		private String k;
		
		private Long i;
		
		private Long l;

		public String getK() {
			return k;
		}

		public void setK(String k) {
			this.k = k;
		}

		public Long getI() {
			return i;
		}

		public void setI(Long i) {
			this.i = i;
		}

		public Long getL() {
			return l;
		}

		public void setL(Long l) {
			this.l = l;
		}
		
		
		
	}
	
	static class Entry {
		
		@LookupTable.Column(isKey=true)
		public String getK() {
			return k;
		}

		public void setK(String k) {
			this.k = k;
		}

		@LookupTable.Column(isKey=true)
		public String getX() {
			return x;
		}

		public void setX(String x) {
			this.x = x;
		}

		@LookupTable.Column(isKey=true)
		public Integer getI() {
			return i;
		}

		public void setI(Integer i) {
			this.i = i;
		}

		@LookupTable.Column(isKey=true)
		public Long getL() {
			return l;
		}

		public void setL(Long l) {
			this.l = l;
		}

		@LookupTable.Column(isKey=false)
		public String getValue() {
			return value;
		}

		@LookupTable.Column(isKey=false)
		public void setValue(String value) {
			this.value = value;
		}

		private String k;
		
		private String x;
		
		private Integer i;
		
		private Long l;
		
		private String value;
		
	}
	
	public static void main(String... a) throws TooManyMatches {
		LookupTable<Entry> l = new LookupTable<Entry>(Entry.class);
		l.add("k","A","i","10","l","100","value","first");
		l.add("k","B","l","100","value","another");
		System.out.println(l);
		l.add("k","A","i","10","l","100","value","second");
		l.add("i","10");
		l.add("l","100");
		System.out.println(l);
		Fact f = new Fact();
		//f.setK("A");
		f.setI(10L);
		f.setL(100L);
		System.out.println(Utilities.toMap(l.getBestMatch(f)));
	}

}
