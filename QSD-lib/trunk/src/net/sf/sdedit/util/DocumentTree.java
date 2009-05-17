package net.sf.sdedit.util;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

import net.sf.sdedit.util.tree.Tree;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class DocumentTree<T extends Node> implements Tree<T> {

	private Document document;

	private Class<? extends Node> nodeClass;

	private T[] emptyArray;

	@SuppressWarnings("unchecked")
	public DocumentTree(Document document, Class<T> nodeClass) {
		this.document = document;
		this.nodeClass = nodeClass;
		emptyArray = (T[]) Array.newInstance(nodeClass, 0);
	}

	@SuppressWarnings("unchecked")
	public T[] getChildren(Node node) {
		if (node == null) {
			return emptyArray;
		}
		NodeList nodeList = node.getChildNodes();
		Node[] nodeArray;
		if (nodeList == null) {
			nodeArray = new Node[] { document };

		} else {
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
