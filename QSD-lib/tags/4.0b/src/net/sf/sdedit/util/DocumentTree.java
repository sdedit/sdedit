package net.sf.sdedit.util;

import java.util.LinkedList;
import java.util.List;

import net.sf.sdedit.util.tree.Tree;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class DocumentTree<T extends Node> implements Tree<T> {

    private Node root;

    private Class<? extends Node> nodeClass;

    public DocumentTree(Node root, Class<T> nodeClass) {
        this.root = root;
        this.nodeClass = nodeClass;
    }

    @SuppressWarnings("unchecked")
    public T[] getChildren(Node node) {
        Node[] nodeArray;
        if (node == null) {
            nodeArray = new Node[] { root };
        } else {
            NodeList nodeList = node.getChildNodes();

                List<Node> children = new LinkedList<Node>();

                for (int i = 0; i < nodeList.getLength(); i++) {
                    if (nodeClass.isInstance(nodeList.item(i))) {
                        children.add(nodeList.item(i));
                    }
                }
                nodeArray = children.toArray(new Node[children.size()]);
            
        }

        return (T[]) Utilities.castArray(nodeArray, nodeClass);

    }

    @SuppressWarnings("unchecked")
    public T getParent(Node node) {
        return (T) node.getParentNode();
    }

}
