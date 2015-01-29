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

package net.sf.sdedit.ui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import net.sf.sdedit.util.JTreeFacade;
import net.sf.sdedit.util.tree.BreadthFirstSearch;

/**
 * A <tt>JTreeSearcher</tt> is a panel, equipped with a text field and a button,
 * that lets you search through a {@linkplain JTree}. If a node exists in the
 * tree such that its textual representation (obtained via
 * {@linkplain Object#toString()}) matches the text entered into the text field,
 * <tt>JTreeSearcher</tt> will navigate to that node, i. e., select it. If more
 * nodes with that textual representation exist in the tree, then on
 * successively hitting the button <tt>JTreeSearcer</tt> will navigate (in a
 * breadth-first fashion) to those nodes.
 * 
 * @author Markus Strauch
 */

public class JTreeSearcher implements ActionListener {

    private ArrayList<JTree> tree;

    private ArrayList<BreadthFirstSearch> bfs;

    private boolean isCaseSensitive;

    private ArrayList<JTreeFacade> facade;

    private String text;

    private int currentTree;

    private List<ActionListener> actionListeners;

    private int eventId;

    private Map<JTree, JTreeSearcherUI> uis;

    public JTreeSearcher(JTree... tree) {
        this.tree = new ArrayList<JTree>(Arrays.asList(tree));
        facade = new ArrayList<JTreeFacade>();
        bfs = new ArrayList<BreadthFirstSearch>();
        for (int i = 0; i < tree.length; i++) {
            facade.add(new JTreeFacade(tree[i]));
            bfs.add(new BreadthFirstSearch(tree[i].getModel()));
        }

        isCaseSensitive = false;
        currentTree = 0;
        actionListeners = new LinkedList<ActionListener>();
        eventId = 0;
        uis = new HashMap<JTree, JTreeSearcherUI>();
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    protected void fireGoToTree() {
        ActionEvent actionEvent = new ActionEvent(currTree(), eventId, text);
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(actionEvent);
        }
    }

    private JTree currTree() {
        return tree.get(currentTree);
    }

    private JTreeFacade currFacade() {
        return facade.get(currentTree);
    }

    public void register(JTreeSearcherUI ui) {
        if (ui.getTree() != null) {
            uis.put(ui.getTree(), ui);
        }

    }

    public void deregister(JTreeSearcherUI ui) {
        if (ui.getTree() != null) {
            uis.remove(ui.getTree());
        }

    }

    public void actionPerformed(ActionEvent e) {
        JTreeSearcherUI ui = (JTreeSearcherUI) e.getSource();
        if (ui.getTree() != null) {
            currentTree = tree.indexOf(ui.getTree());
        }
        text = ui.getSearchText();
        TreePath next = findNext();
        if (next != null) {
            currFacade().deselectAll();
            TreePath path = next.getParentPath();
            if (path != null) {
                currTree().expandPath(path);
            }
            currTree().setSelectionPath(next);
            currTree().scrollPathToVisible(next);
            fireGoToTree();
            JTreeSearcherUI sui = uis.get(currTree());
            sui.getTextField().requestFocusInWindow();
        }
    }

    /**
     * Tries to find the next node that matches the text of the text field. If
     * the text is not found, the search will wrap around, i. e. start at the
     * root again. If the text is not found again then, this method will return
     * <tt>null</tt>. Otherwise this method returns a path leading to a node
     * that matches the text.
     * 
     * @return
     */
    public TreePath findNext() {
        int state = 0;
        TreePath next;
        while (state < 2) {
            while ((next = next()) != null) {
                String str = currTree().convertValueToText(
                        next.getLastPathComponent(), false, false, false, 0,
                        false);
                if (matches(str)) {
                    return next;
                }
            }
            restart();
            state++;
        }
        return null;
    }

    private TreePath next() {
        TreePath next;
        do {
            next = bfs.get(currentTree).next();
            if (next == null) {
                currentTree++;
            }
        } while (next == null && currentTree < tree.size());
        return next;
    }

    public void remove(JTree tree) {
        int i = this.tree.indexOf(tree);
        this.tree.remove(i);
        bfs.remove(i);
        this.facade.remove(i);
        if (i >= 0 && i < this.tree.size()) {
            currentTree = i;
        } else {
            restart();
        }
    }

    public JTree[] getTrees() {
        return tree.toArray(new JTree[tree.size()]);
    }

    private void restart() {
        for (BreadthFirstSearch b : bfs) {
            b.restart();
        }
        currentTree = 0;
    }

    private boolean matches(String string) {
        int i;
        if (isCaseSensitive) {
            i = string.indexOf(text);
        } else {
            i = string.toUpperCase().indexOf(text.toUpperCase());
        }
        return i >= 0;
    }
}
