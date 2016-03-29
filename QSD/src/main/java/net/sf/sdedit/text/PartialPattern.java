package net.sf.sdedit.text;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import net.sf.sdedit.util.Utilities;

enum PartialPattern {

	LEVELS,

	PREFIX,

	COLON,

	SPAWN,

	DOT,

	EQ;

	private static Map<PartialPattern, String> patterns = new HashMap<PartialPattern, String>();

	static {
		URL resource = PartialPattern.class.getResource("patterns");
		try {
			for (String line : Utilities.readLines(resource, Charset.forName("utf-8"))) {
				line = line.trim();
				if (!line.startsWith("#")) {
					int i = line.indexOf('=');
					if (i > 0) {
						String key = line.substring(0, i);
						String value = line.substring(i + 1);
						patterns.put(PartialPattern.valueOf(key.trim()), value.trim());					
					}					
				}

			}
		} catch (IOException e) {
			throw new IllegalStateException("cannot read patterns from " + resource);
		}
	}

	public String toString() {
		return patterns.get(this);
	}

}
