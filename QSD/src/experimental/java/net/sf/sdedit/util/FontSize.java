package net.sf.sdedit.util;

import java.util.HashSet;
import java.util.Set;

public enum FontSize {

	F33(33.2F, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'd', 'e', 'g', 'h', 'n', 'o', 'p', 'q', 'u'),

	F52(56.5F, 'W'),

	F50(50F, 'M', 'm'),

	F45(45.5F, 'G', 'O', 'Q'),

	F43(43.1F, 'H', 'N', 'R', 'w', 'C', 'D', 'U'),

	F40(40F, 'A', 'B', 'E', 'K', 'P', 'S', 'V', 'X', 'Y'),

	F35(35.5F, 'F', 'T', 'Z'),

	F30(30F, 'c', 'k', 's', 'v', 'x', 'y', 'z', 'J'),

	F16(16.6F, 'f', 't', 'I',':'),

	F13(13.1F, 'i', 'j', 'l'),
	
	F17(17F, ' '),
	
	DEFAULT(17F);

	private Set<Character> chars;
	
	private float width;

	FontSize(float width, char... chars) {
		this.chars = new HashSet<Character>();
		this.width = width;
		for (char c : chars) {
			this.chars.add(c);
		}
	}
	
	public float getWidth() {
		return 10 * width / 50;
	}
	
	public static float getWidth (String str) {
		float total = 0;
		outer:
		for (char c : str.toCharArray()) {
			for (FontSize fs : FontSize.values()) {
				if (fs.chars.contains(c)) {
					total+=fs.getWidth();
					continue outer;
				}
			}
			total+=DEFAULT.getWidth();
		}
		return total;
	}

}
