// Copyright (c) 2006 - 2008, Markus Strauch.
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

package net.sf.sdedit.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import net.sf.sdedit.util.Browser;

public class AdvancedHelpPanel extends JPanel implements MouseListener,
		MouseMotionListener, TreeCellRenderer {

	private static final long serialVersionUID = 421111139685691723L;

	private JScrollPane navigatorScrollPane;

	private JScrollPane contentScrollPane;

	private JEditorPane editorPane;

	private JTree navigator;

	private DefaultTreeModel navigatorModel;

	private DefaultMutableTreeNode navigatorRoot;

	private static Pattern pattern = Pattern
			.compile(".*<a name=\"(.+?)\">(.*?)<\\/a.*");

	private Map<String, DefaultMutableTreeNode> anchorMap;

	private String url;

	private static Font[] fonts;

	private static Cursor HAND = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	private static Cursor DEFAULT = Cursor
			.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	static {
		fonts = new Font[3];
		String fontName = "Dialog";
		fonts[0] = new Font(fontName, Font.BOLD, 13);
		fonts[1] = new Font(fontName, Font.PLAIN, 13);
		fonts[2] = new Font(fontName, Font.PLAIN, 12);
	}

	public AdvancedHelpPanel(URL url, HyperlinkListener listener) {
		init();
		this.url = url.toString();
		anchorMap = new HashMap<String, DefaultMutableTreeNode>();
		try {
			readAnchors(url);
			editorPane.setPage(url);
		} catch (RuntimeException re) {
			throw re;
		} catch (IOException e) {
			e.printStackTrace();
		}
		navigatorModel.nodeStructureChanged(navigatorRoot);
		expandNavigator();
		navigator.addMouseListener(this);
		navigator.addMouseMotionListener(this);
		editorPane.addHyperlinkListener(Browser.getBrowser());
		editorPane.addHyperlinkListener(listener);
	}

	private void readAnchors(URL url) throws IOException {
		InputStream stream = url.openStream();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line = br.readLine();
			while (line != null) {
				searchAnchors(line);
				line = br.readLine();
			}
		} finally {
			stream.close();
		}
	}

	private void searchAnchors(String string) {
		Matcher matcher = pattern.matcher(string);
		if (matcher.matches()) {
			String id = matcher.group(1);
			String content = matcher.group(2);
			addAnchor(id, content);
		}
	}

	private DefaultMutableTreeNode addAnchor(String id, String content) {
		int dot = id.lastIndexOf('.');
		DefaultMutableTreeNode parent;
		if (dot == -1) {
			parent = navigatorRoot;
		} else {
			String parentId = id.substring(0, dot);
			parent = anchorMap.get(parentId);
			if (parent == null) {
				parent = addAnchor(parentId, "");
			}
		}
		UserObject obj = new UserObject(id, content);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(obj);
		parent.add(node);
		anchorMap.put(id, node);
		return node;
	}

	@SuppressWarnings("serial")
	private void init() {
		setLayout(new BorderLayout());
		navigatorScrollPane = new JScrollPane();
		contentScrollPane = new JScrollPane();
		editorPane = new JEditorPane() {
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				super.paintComponent(g);
			}
		};
		editorPane.setContentType("text/html; charset=ISO-8859-1");
		editorPane.setEditable(false);
		JViewport viewPort = new GrabbableViewport();
		viewPort.setView(editorPane);
		contentScrollPane.setViewport(viewPort);
		Border emptyBorder1 = BorderFactory.createEmptyBorder(5, 5, 0, 10);

		add(navigatorScrollPane, BorderLayout.WEST);
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		navigatorScrollPane.setBorder(border);
		add(contentScrollPane, BorderLayout.CENTER);
		navigatorRoot = new DefaultMutableTreeNode();
		navigatorModel = new DefaultTreeModel(navigatorRoot);
		navigator = new JTree() {
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				super.paintComponent(g);
			}
		};
		navigator.setBorder(emptyBorder1);
		navigator.setCellRenderer(this);
		navigator.setModel(navigatorModel);
		navigator.setRootVisible(false);
		navigatorScrollPane.setViewportView(navigator);
	}

	private void expandNavigator() {
		int numberOfRows = 0;
		while (navigator.getRowCount() != numberOfRows) {
			numberOfRows = navigator.getRowCount();
			for (int i = 0; i < numberOfRows; i++) {
				navigator.expandRow(i);
			}
		}
	}

	private static class UserObject {
		private String id;

		private String content;

		UserObject(String id, String content) {
			this.id = id;
			this.content = content;
		}

		public String toString() {
			return content;
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		TreePath path = navigator.getSelectionPath();
		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			UserObject obj = (UserObject) node.getUserObject();
			String id = obj.id;
			try {
				editorPane.setPage(url + "#" + id);
			} catch (RuntimeException re) {
				throw re;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		e.consume();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	private static int countDots(String string) {
		int count = 0;
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '.') {
				count++;
			}
		}
		return count;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Object o = ((DefaultMutableTreeNode) value).getUserObject();
		JLabel label = new JLabel();
		if (!(o instanceof UserObject)) {
			return label;
		}
		UserObject obj = (UserObject) o;
		int f = Math.min(countDots(obj.id), fonts.length - 1);
		label.setFont(fonts[f]);
		label.setText("<html><u>" + obj.content + "</u>");
		return label;
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		TreePath path = navigator.getPathForLocation(x, y);
		if (path != null) {
			navigator.setCursor(HAND);
		} else {
			navigator.setCursor(DEFAULT);
		}
	}
}
