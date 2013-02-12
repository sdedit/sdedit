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
package net.sf.sdedit.ui.components.navigator;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import net.sf.sdedit.ui.components.Stainable;
import net.sf.sdedit.util.Utilities;
import net.sf.sdedit.util.collection.IndexedList;
import net.sf.sdedit.util.tree.BreadthFirstSearch;

/**
 * A <tt>TreeNavigatorPane</tt> allows to switch between a set of components,
 * similar to a <tt>JTabbedPane</tt>. In constrast to this class, it has a more
 * sophisticated navigation component - a tree instead of a strip - , allowing
 * to define parent-child relationships between components, represented by the
 * tree used for choosing a component for display.
 * 
 * @author Markus Strauch
 * 
 */
public class TreeNavigatorPane extends JPanel {

    private static final long serialVersionUID = -610209631378617019L;

    private final JSplitPane splitPane;

    private final JScrollPane navigationScrollPane;

    private final JTree navigationTree;

    private JComponent selected;

    private final JPanel contentPanel;

    private final TreeNavigatorNodeRenderer renderer;

    private final TreeNavigatorModel treeModel;

    private final TreeNavigatorControl ctrl;

    private final LinkedList<TreeNavigatorPaneListener> listeners;

    private final IndexedList<JComponent> componentHistory;

    private final Set<String> categories;

    private boolean historyEnabled;

    public TreeNavigatorPane(double resizeWeight) {
        setLayout(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOneTouchExpandable(true);
        treeModel = new TreeNavigatorModel();
        navigationTree = new JTree(treeModel);
        navigationTree.setRootVisible(false);
        ctrl = new TreeNavigatorControl(this, treeModel);
        navigationTree.addMouseListener(ctrl);
        navigationTree.addTreeSelectionListener(ctrl);
        renderer = new TreeNavigatorNodeRenderer();
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        listeners = new LinkedList<TreeNavigatorPaneListener>();
        navigationTree.setCellRenderer(renderer);
        navigationScrollPane = new JScrollPane(navigationTree);
        add(splitPane, BorderLayout.CENTER);
        splitPane.setLeftComponent(navigationScrollPane);
        splitPane.setRightComponent(contentPanel);
        splitPane.setResizeWeight(resizeWeight);
        categories = new HashSet<String>();
        componentHistory = new IndexedList<JComponent>();
        historyEnabled = true;
    }

    protected JTree getTree() {
        return navigationTree;
    }

    public void addListener(TreeNavigatorPaneListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TreeNavigatorPaneListener listener) {
        listeners.remove(listener);
    }

    public void addRootCategory(String category, ImageIcon icon) {
        TreeNavigatorNode rootNode = new TreeNavigatorNode(category, icon, null);
        categories.add(category);
        treeModel.addChild(treeModel.getRoot(), rootNode, null);
    }

    public TreeNavigatorNode getNode(JComponent component) {
        if (component == null) {
            return null;
        }
        return treeModel.find(component);
    }

    public void addComponent(String title, JComponent comp, Icon icon,
            String rootCategory, boolean selectIt, JComponent previousSibling) {
        TreeNavigatorNode node = new TreeNavigatorNode(title, icon, comp);
        TreeNavigatorNode categoryNode = treeModel
                .getCategoryNode(rootCategory);
        treeModel.addChild(categoryNode, node, getNode(previousSibling));
        if (comp instanceof Stainable) {
            ((Stainable) comp).addStainedListener(ctrl);
        }
        if (selectIt) {
            setSelectedComponent(comp);
        }

    }

    public void addComponent(String title, JComponent child, Icon icon,
            JComponent parent, boolean selectIt, JComponent previousSibling) {
        TreeNavigatorNode node = new TreeNavigatorNode(title, icon, child);
        TreeNavigatorNode parentNode;
        if (parent != null) {
            parentNode = treeModel.find(parent);
        } else {
            parentNode = treeModel.getRoot();
        }
        treeModel.addChild(parentNode, node, getNode(previousSibling));
        if (child instanceof Stainable) {
            ((Stainable) child).addStainedListener(ctrl);
        }
        if (selectIt) {
            setSelectedComponent(child);
        }

    }

    public JComponent[] removeComponent(JComponent comp,
            boolean removeDescendants) {
        JComponent[] selected = getSelectedComponents();
        List<JComponent> list = new LinkedList<JComponent>();
        TreeNavigatorNode node = treeModel.find(comp);
        if (node != null) {
            if (removeDescendants) {
                BreadthFirstSearch bfs = new BreadthFirstSearch(treeModel, node);
                LinkedList<Object> nodes = new LinkedList<Object>();
                TreePath current;
                do {
                    current = bfs.next();
                    if (current != null) {
                        nodes.add(current.getLastPathComponent());
                    }
                } while (current != null);
                while (!nodes.isEmpty()) {
                    TreeNavigatorNode last = (TreeNavigatorNode) nodes
                            .removeLast();
                    list.add(last.getComponent());
                    removeNode(last);
                }
            } else {
                removeNode(node);
                list.add(comp);
            }
        }
        boolean isSelected = false;

        for (JComponent cmp : list) {
            isSelected |= Utilities.in(cmp, selected);
            componentHistory.remove(cmp);

        }
        if (isSelected && !componentHistory.isEmpty()) {
            setSelectedComponent(componentHistory.getLast());
        }
        return list.toArray(new JComponent[list.size()]);
    }

    private void removeNode(TreeNavigatorNode node) {
        if (node.getChildCount() > 0) {
            TreeNavigatorNode parent = node.getParent();
            for (TreeNavigatorNode child : node.getChildren()) {
                treeModel.addChild(parent, child, null);
                treeModel.removeChild(node, child);
            }
        }
        treeModel.removeChild(node.getParent(), node);
    }

    public boolean existsCategory(String category) {
        return categories.contains(category);
    }

    public boolean setSelectedComponent(JComponent comp) {
        return setSelectedComponent(comp, true, true);
    }

    public void setContextActionsProvider(ContextActionsProvider provider) {
        ctrl.setContextActionsProvider(provider);
    }

    private boolean setSelectedComponentEntered = false;

    protected boolean setSelectedComponent(final JComponent comp,
            boolean selectInTree, boolean updateHistory) {
        if (setSelectedComponentEntered) {
            return true;
        }
        if (historyEnabled && updateHistory) {
            componentHistory.remove(comp);
            componentHistory.add(comp);
        }
        setSelectedComponentEntered = true;
        try {
            this.selected = comp;
            TreeNavigatorNode node = treeModel.find(comp);
            if (node == null) {
                return false;
            }
            if (selectInTree) {
                TreePath path = node.getTreePath();
                // this would lead to another invocation of this method
                // (avoided by the setSelectedComponentEntered flag)
                navigationTree.setSelectionPath(path);
            }
            contentPanel.removeAll();
            contentPanel.add(comp, BorderLayout.CENTER);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    navigationTree.revalidate();
                    contentPanel.revalidate();
                    repaint();
                    for (TreeNavigatorPaneListener listener : listeners) {
                        listener.componentSelected(comp);
                    }
                }
            });
            return true;
        } finally {
            setSelectedComponentEntered = false;
        }
    }

    public JComponent getSelectedComponent() {
        return selected;
    }

    public JComponent[] getSelectedComponents() {
        List<JComponent> list = new LinkedList<JComponent>();
        TreePath[] paths = navigationTree.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                TreeNavigatorNode node = (TreeNavigatorNode) path
                        .getLastPathComponent();
                if (node.getComponent() != null) {
                    list.add(node.getComponent());
                }
            }
        }
        return list.toArray(new JComponent[list.size()]);
    }

    public void setTitle(JComponent comp, String title) {
        TreeNavigatorNode node = treeModel.find(comp);
        if (node != null) {
            treeModel.setTitle(node, title);
        }
        navigationTree.repaint();
    }

    public List<String> getAllTitles() {
        return treeModel.getAllTitles();
    }

    public int getCompCount() {
        return treeModel.getComponents().size();
    }

    public List<JComponent> getAllComponents() {
        return treeModel.getComponents();
    }

    public List<JComponent> getSuccessors(JComponent comp) {
        TreeNavigatorNode node = treeModel.find(comp);
        TreeNavigatorNode[] children = node.getChildren();
        List<JComponent> succs = new LinkedList<JComponent>();
        for (TreeNavigatorNode child : children) {
            succs.add(child.getComponent());
        }
        return succs;
    }

    public List<JComponent> getDescendants(JComponent root) {
        TreeNavigatorNode rootNode = treeModel.find(root);
        BreadthFirstSearch bfs = new BreadthFirstSearch(treeModel, rootNode);
        List<JComponent> descendants = new LinkedList<JComponent>();
        TreePath desc;
        while ((desc = bfs.next()) != null) {
            TreeNavigatorNode current = (TreeNavigatorNode) desc
                    .getLastPathComponent();
            if (current != rootNode) {
                descendants.add(current.getComponent());
            }
        }
        return descendants;
    }

    public JComponent getParentComponent(JComponent comp) {
        TreeNavigatorNode node = treeModel.find(comp);
        return node.getParent().getComponent();
    }

    public void goToNextComponent() {
        JComponent next = componentHistory.next(selected);
        if (next != null) {
            setSelectedComponent(next, true, false);
        }
    }

    public boolean nextComponentExists() {
        return componentHistory.contains(selected)
                && componentHistory.next(selected) != null;
    }

    public void goToPreviousComponent() {
        JComponent previous = componentHistory.previous(selected);
        if (previous != null) {
            setSelectedComponent(previous, true, false);
        }
    }

    public boolean previousComponentExists() {
        return componentHistory.contains(selected)
                && componentHistory.previous(selected) != null;
    }

    public void enableHistory() {
        historyEnabled = true;
    }

    public void disableHistory() {
        historyEnabled = false;
    }

}
