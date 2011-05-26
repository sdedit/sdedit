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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import net.sf.sdedit.util.PopupActions.ContextHandler;
import net.sf.sdedit.util.collection.DistinctObjectsMap;
import net.sf.sdedit.util.collection.DistinctObjectsSet;
import net.sf.sdedit.util.tree.BreadthFirstSearch;

public class JTreeFacade implements PopupActions.Provider,
		PopupActions.ContextHandler, TreeCellRenderer {

	public interface NodeConversion<T> {

		public T convert(Object o);

	}

	public interface PathIdenter {

		public String identify(TreePath path);

		public TreePath getPath(String ident);

	}
	
	@SuppressWarnings("serial")
	public static abstract class JTreeAction extends AbstractAction {

		protected abstract boolean beforePopup(TreePath[] paths);

	}

	private JTree tree;

	private PopupActions popupActions;

	private TreeCellRenderer existingRenderer;

	private Icon nodeIcon;

	private Icon leafIcon;
	
	public JTreeFacade(JTree tree) {
		this.tree = tree;
	}
	
	public JTree getTree () {
		return tree;
	}

	public PopupActions getPopupActions(ContextHandler ch) {
		if (popupActions == null) {
			if (ch == null) {
				popupActions = new PopupActions(tree, this);
			} else {
				popupActions = new PopupActions(tree, ch);
			}

		}
		return popupActions;
	}

	public PopupActions getPopupActions() {
		return getPopupActions(this);
	}
	
	public void deselectAll() {
		if (tree.getSelectionPaths() != null) {
			for (TreePath path : tree.getSelectionPaths()) {
				tree.removeSelectionPath(path);
			}
		}
	}

	public List<String> getSelections(PathIdenter identer) {
		List<String> identList = new LinkedList<String>();
		if (tree.getSelectionPaths() != null) {
			for (TreePath treePath : tree.getSelectionPaths()) {
				identList.add(identer.identify(treePath));
			}
		}
		return identList;
	}

	public void expandSelectedPaths() {
		if (tree.getSelectionPaths() != null) {
			for (TreePath path : tree.getSelectionPaths()) {
				tree.expandPath(path);
			}
		}
	}
	
	public void expandAll (TreePath path) {
		BreadthFirstSearch bfs = new BreadthFirstSearch(tree.getModel(),
				path.getLastPathComponent());
		
		while ((path = bfs.next()) != null
				 ) {
			tree.expandPath(path);			
		}
	}
	
	public void restoreSelections(PathIdenter identer, List<String> selections) {
		tree.removeSelectionPaths(tree.getSelectionPaths());
		for (String selection : selections) {
			TreePath path = identer.getPath(selection);
			if (path != null) {
				tree.addSelectionPath(path);
			}
		}
	}

	private DistinctObjectsMap<Object, Integer> computeIndices() {
		DistinctObjectsMap<Object, Integer> map = new DistinctObjectsMap<Object, Integer>();
		BreadthFirstSearch bfs = new BreadthFirstSearch(tree.getModel());
		int i = 0;
		TreePath path = bfs.next();
		while (path != null) {
			map.put(path.getLastPathComponent(), i);
			i++;
			path = bfs.next();
		}
		return map;

	}

	public List<TreePath> getVisibleNodes() {
		List<TreePath> result = new LinkedList<TreePath>();
		Enumeration<TreePath> paths = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
		if (paths != null) {
			while (paths.hasMoreElements()) {
				result.add(paths.nextElement());
			}
		}
		return result;
	}
	
	public Object [] getSelectedObjects () {
		TreePath [] paths = tree.getSelectionPaths();
		Object [] objects = new Object[paths.length];
		for (int i = 0; i < paths.length; i++) {
			objects [i] = paths[i].getLastPathComponent();
		}
		return objects;
	}
	


	public int deselectAncestors(TreePath selectedNode) {
		DistinctObjectsSet<Object> ancestors = new DistinctObjectsSet<Object>();
		Object[] path = selectedNode.getPath();
		int n = 0;
		if (tree.getSelectionPaths() != null) {
			for (int i = 0; i < path.length - 1; i++) {
				ancestors.add(path[i]);
			}
			for (TreePath selection : tree.getSelectionPaths()) {
				if (ancestors.contains(selection.getLastPathComponent())) {
					tree.removeSelectionPath(selection);
					n++;
				}
			}
		}
		return n;
	}

	public int deselectDescendants(TreePath selectedNode) {
		int n = 0;
		Object node = selectedNode.getLastPathComponent();
		if (tree.getSelectionPaths() != null) {
			for (TreePath selection : tree.getSelectionPaths()) {
				Object current = selection.getLastPathComponent();
				if (current != node) {
					DistinctObjectsSet<Object> ancestors = DistinctObjectsSet
							.createFromArray(selection.getPath());
					if (ancestors.contains(node)) {
						tree.removeSelectionPath(selectedNode);
						n++;
					}
				}
			}
		}
		return n;
	}

	public <T> List<T> convertSelectedNodes(Class<T> nodeClass,
			NodeConversion<T> conversion) {
		List<T> list = new LinkedList<T>();
		if (tree.getSelectionPath() != null) {
			for (TreePath selection : tree.getSelectionPaths()) {
				Object obj = selection.getLastPathComponent();
				T t = conversion.convert(obj);
				if (t != null) {
					list.add(t);
				}
			}
		}
		return list;
	}

	public void selectAndGoToRoot() {
		TreePath path = new TreePath(tree.getModel().getRoot());
		tree.scrollPathToVisible(path);
	}
	
	public void selectAndGoTo (TreePath path) {
	    tree.setSelectionPath(path);
	    tree.scrollPathToVisible(path);
	}
	
	/**
	 * @see net.sf.sdedit.util.PopupActions.ContextHandler#getObjectForCurrentContext()
	 */
	public Object getObjectForCurrentContext(JComponent comp) {
		TreePath[] paths = tree.getSelectionPaths();
		if (paths == null) {
			paths = new TreePath[0];
		}
		return paths;
	}

	public Icon getNodeIcon() {
		return nodeIcon;
	}

	public void setNodeIcon(Icon nodeIcon) {
		this.nodeIcon = nodeIcon;
	}

	public Icon getLeafIcon() {
		return leafIcon;
	}

	public void setLeafIcon(Icon leafIcon) {
		this.leafIcon = leafIcon;
	}

	public void useAsRenderer() {
		existingRenderer = tree.getCellRenderer();
		tree.setCellRenderer(this);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		JLabel label = (JLabel) existingRenderer.getTreeCellRendererComponent(
				tree, value, selected, expanded, leaf, row, hasFocus);
		if (leaf) {
			label.setIcon(leafIcon);
		} else {
			label.setIcon(nodeIcon);
		}
		return label;
	}
	
	@SuppressWarnings("serial")
	public PopupActions.Action getExpandAllAction () {
		return new PopupActions.Action() {
	

		{
			putValue(Action.NAME, "Expand all");
		}

		private TreePath treePath;

		
		protected boolean beforePopup(Object context) {
			treePath = (TreePath) context;

			return true;
		}

		
		public void actionPerformed(ActionEvent e) {
			expandAll(treePath);
		}

	};}
}
