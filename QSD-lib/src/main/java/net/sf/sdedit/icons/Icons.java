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

package net.sf.sdedit.icons;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import net.sf.sdedit.util.UIUtilities;
import net.sf.sdedit.util.Utilities;

public final class Icons {

	private static Map<String, ImageIcon> cache = new HashMap<String, ImageIcon>();

	private static List<String> bases = new LinkedList<String>();

	private static Set<String> notFound = new HashSet<String>();

	private Icons() {
		/* empty */
	}

	public final static ImageIcon getIcon(String name) {
		ImageIcon icon = cache.get(name);
		if (icon != null) {
			return icon;
		}
		if (notFound.contains(name)) {
			return null;
		}
		try {
			if (name.indexOf('\\') > 0) {
				for (String subName : name.split("\\\\")) {
					ImageIcon subIcon = getIcon(subName);
					if (subIcon == null) {
						icon = null;
						break;
					}
					if (icon == null) {
						icon = subIcon;
					} else {
						Image join = UIUtilities.joinImages(icon.getImage(),
								subIcon.getImage(), 2, BufferedImage.TYPE_INT_ARGB);
						icon = new ImageIcon(join);
					}
				}
			} else {

				URL res = findRes(name);
				if (res == null) {
					notFound.add(name);
					return null;
				}
				icon = new ImageIcon(res);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		cache.put(name, icon);
		return icon;
	}

	public static void addBase(String base) {
		bases.add(base);
	}

	private static URL findRes(String name) {
		URL res = Utilities.getResource(name + ".png");
		if (res == null) {
			for (String base : bases) {
				res = Icons.class.getResource(base + "/" + name + ".png");
				if (res != null) {
					return res;
				}
			}
		}
		return res;
	}

	public final static ImageIcon getEmptyIcon(int width, int height) {
		String code = "EMPTY_" + width + "x" + height;
		ImageIcon icon = cache.get(code);
		if (icon != null) {
			return icon;
		}
		Image img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		img.getGraphics().setColor(Color.WHITE);
		img.getGraphics().fillRect(0, 0, width, height);
		icon = new ImageIcon(img);
		cache.put(code, icon);
		return icon;
	}

}
