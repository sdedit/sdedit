package net.sf.sdedit.util;

import net.sf.sdedit.util.tree.Tree;

import org.w3c.dom.Node;

public class DOMNodeTree implements Tree<DOMNode> {
    
    private static final long serialVersionUID = 7284101176716294736L;

    private DOMNode root;
    
    private Class<? extends Node> nodeClass;
    
    public DOMNodeTree (DOMNode root, Class<? extends Node> nodeClass) {
        this.root = root;
        this.nodeClass = nodeClass;
    }

    public DOMNode[] getChildren(DOMNode node) {
        if (node == null) {
            return new DOMNode [] {root};
        }
        return node.getChildren(nodeClass).toArray(new DOMNode[0]);
    }

    public DOMNode getParent(DOMNode node) {
        return node.getParent();
    }

}
