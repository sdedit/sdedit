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
