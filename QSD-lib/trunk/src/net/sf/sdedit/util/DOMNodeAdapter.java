package net.sf.sdedit.util;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.sf.sdedit.util.tree.Traversal;
import net.sf.sdedit.util.tree.TraversalControl;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DOMNodeAdapter implements DOMNode, TraversalControl<Node> {

	private Node node;

	private static XPathFactory xPathFactory = XPathFactory.newInstance();
	
	private List<Pair<Integer,Node>> descendants;
	
	private Map<String,Object> userObjects;
	
	DOMNodeAdapter(Node node) {
		this.node = node;
		descendants = new LinkedList<Pair<Integer,Node>>();
	}

	@Override
	public String getAttribute(String name) {
		if (!(node instanceof Element)) {
			return null;
		}
		return DocUtil.getAttribute((Element) node, name);
	}
	
	public List<String> getAttributeNames () {
		List<String> result = new LinkedList<String>();
		NamedNodeMap nnm = node.getAttributes();
		for (int i = 0; i < nnm.getLength(); i++) {
			result.add(nnm.item(i).getNodeName());
		}
		return result;
	}

	public DOMNode findNode(String exp) {
		XPath xpath = xPathFactory.newXPath();
		try {
			Node result = (Node) xpath.evaluate(exp, node, XPathConstants.NODE);
			if (result == null) {
				return null;
			}
			return new DOMNodeAdapter(result);
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	public List<DOMNode> findNodes(String exp) {
		XPath xpath = xPathFactory.newXPath();
		List<DOMNode> result = new LinkedList<DOMNode>();
		try {
			NodeList nodes = (NodeList) xpath.evaluate(exp, node,
					XPathConstants.NODESET);
			for (int i = 0; nodes != null && i < nodes.getLength(); i++) {
				result.add(new DOMNodeAdapter(nodes.item(i)));
			}
			return result;
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	public String toString() {
		return DocUtil.toString(node);
	}

	public int getLevel() {
		int level = 0;
		Node n = node;
		while (n != node.getOwnerDocument().getDocumentElement()) {
			level++;
			n = n.getParentNode();
		}
		return level;
	}
	
	private void computeDescendants () {
		DocumentTree<Node> tree = new DocumentTree<Node>(node, Node.class);
		Traversal<Node> traversal = new Traversal<Node>(this);
		descendants.clear();
		traversal.traverse(tree);
	}
	
	public List<DOMNode> getDescendantsDFS () {
		computeDescendants();
		List<DOMNode> result = new LinkedList<DOMNode>();
		for (Pair<Integer,Node> pair : descendants) {
			result.add(new DOMNodeAdapter(pair.getSecond()));
		}
		return result;
	}

	public String toTreeString () {
	   computeDescendants();
	   PrintWriter pw = Utilities.createPrintWriter();
		for (Pair<Integer,Node> pair : descendants) {
			pw.println(Utilities.pad(' ', 2*pair.getFirst()) + DocUtil.toString(pair.getSecond()) );
		}
		return Utilities.toString(pw);
	}

	@Override
	public List<DOMNode> getChildren() {
		List<DOMNode> list = new LinkedList<DOMNode>();
		for (Node n : DocUtil.iterate(node.getChildNodes())) {
			list.add(new DOMNodeAdapter(n));
		}
		return list;
	}

	public List<DOMNode> getChildren(String name) {
		List<DOMNode> list = new LinkedList<DOMNode>();
		for (Node n : DocUtil.iterate(node.getChildNodes())) {
			if (name.equals(n.getNodeName())) {
				list.add(new DOMNodeAdapter(n));
			}

		}
		return list;
	}

	@Override
	public DOMNode getParent() {
		return new DOMNodeAdapter(node.getParentNode());
	}

	public String getText() {
		return node.getTextContent();
	}

	@Override
	public DOMNode getChild(String name) {
		List<DOMNode> children = getChildren(name);
		if (children.size() == 0) {
			return null;
		}
		return children.get(0);
	}

	@Override
	public String getName() {
		return node.getNodeName();
	}

	@Override
	public void beginTraversal(Node t, int level) {
		descendants.add(new Pair<Integer,Node> (level, t));				
	}

	@Override
	public boolean doTraverse(Node t, int level) {
		return true;
	}

	@Override
	public void endTraversal(Node t, boolean leaf) {
				
	}

	@Override
	public boolean isLeaf() {
		return node.getChildNodes().getLength() == 0;
	}

	@Override
	public Object getUserObject(String name) {
		if (userObjects == null) {
			return null;
		}
		return userObjects.get(name);
	}

	@Override
	public void setUserObject(String name, Object object) {
		if (userObjects == null) {
			userObjects = new HashMap<String,Object>();
		}
		userObjects.put(name, object);
		
	}

	@Override
	public <T extends Node> List<DOMNode> getChildren(Class<T> nodeClass) {
		List<DOMNode> result = new LinkedList<DOMNode>();
		for (DOMNodeAdapter child : Utilities.castIterable(getChildren(),DOMNodeAdapter.class)) {
			if (nodeClass.isInstance(child.node)) {
				result.add(child);
			}
		}
		return result;
	}

}
