package net.sf.sdedit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Tooltips {

	private static Tooltips instance;

	public static void addFile(URL tooltipFile) {
		if (instance == null) {
			instance = new Tooltips();
		}
		try {
			instance.addTooltipFile(tooltipFile);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot read tooltips from " + tooltipFile.toString(), e);
			
		}
	}

	private Map<String, String> tooltips;

	private Tooltips() {
		tooltips = new HashMap<String, String>();
	}

	private void addTooltipFile(URL tooltipFile) throws IOException {
		InputStream fis = tooltipFile.openStream();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(fis,
					"ISO-8859-1"));
			String line;
			String name = null;
			StringBuffer content = new StringBuffer();
			while ((line = br.readLine()) != null) {
				if (line.length() > 2 && line.substring(0, 2).equals("::")) {
					if (name != null) {
						addTooltip(name, content);

					}
					content.setLength(0);
					content.append("<html>");
					name = line.substring(2).trim();
				} else {
					if (content.length() > 6) {
						content.append("<br>");
					}
					content.append(line.trim());
				}
			}
			if (name != null) {
				addTooltip(name, content);
			}
		} finally {
			fis.close();
		}
	}
	
	private void addTooltip (String name, StringBuffer content) {
		String c = content.toString();
		c = c.replaceAll("(<br>)*$","");
		tooltips.put(name, c);

	}

	public static String getTooltip(String name) {
		if (instance == null) {
			return null;
		}
		String tt = instance.tooltips.get(name);
		return tt;
	}
}
