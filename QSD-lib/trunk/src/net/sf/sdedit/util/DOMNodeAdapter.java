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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

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

    private static XPathFactory xPathFactory = XPathFactory.newInstance();

    private static WeakHashMap<Node, DOMNode> nodeMap = new WeakHashMap<Node, DOMNode>();

    static DOMNode makeNode(Node node) {
        DOMNode dnode = nodeMap.get(node);
        if (dnode != null) {
            return dnode;
        }
        dnode = new DOMNodeAdapter(node);
        nodeMap.put(node, dnode);
        return dnode;

    }

    private final Node node;

    private List<Pair<Integer, Node>> descendants;

    private Map<String, Object> userObjects;

    private DOMNodeAdapter(Node node) {
        if (node == null) {
            throw new NullPointerException();
        }
        this.node = node;
        
    }

    public String getAttribute(String name) {
        if (!(node instanceof Element)) {
            return null;
        }
        return DocUtil.getAttribute((Element) node, name);
    }
    
    public void setAttribute(String name, String value) {
    	NamedNodeMap nnm = node.getAttributes();
    	for (int i = 0; i < nnm.getLength(); i++) {
    		if (nnm.item(i).getNodeName().equalsIgnoreCase(name)) {
    			nnm.item(i).setNodeValue(value);
    			return;
    		}
    	}
    	addAttribute(name, value);
    }
    
    private void addAttribute (String name, String value) {
        ((Element) node).setAttribute(name, value);
    }

    public List<String> getAttributeNames() {
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
            return makeNode(result);
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
                result.add(makeNode(nodes.item(i)));
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

    private void computeDescendants() {
        DocumentTree<Node> tree = new DocumentTree<Node>(node, Node.class);
        Traversal<Node> traversal = new Traversal<Node>(this);
        descendants = new LinkedList<Pair<Integer, Node>>();
        traversal.traverse(tree);
    }

    public <T extends Node> DOMNode getPreviousSibling(Class<T> nodeClass) {
        Node n = node;
        while (n != null) {
            n = n.getPreviousSibling();
            if (nodeClass.isInstance(n)) {
                return makeNode(n);
            }
        }
        return null;
    }

    public <T extends Node>DOMNode getNextSibling(Class<T> nodeClass) {
        Node n = node;
        while (n != null) {
            n = n.getNextSibling();
            if (nodeClass.isInstance(n)) {
                return makeNode(n);
            }
        }
        return null;
    }
    

    public List<DOMNode> getDescendantsDFS() {
        computeDescendants();
        List<DOMNode> result = new LinkedList<DOMNode>();
        for (Pair<Integer, Node> pair : descendants) {
            result.add(makeNode(pair.getSecond()));
        }
        return result;
    }

    public String toTreeString() {
        computeDescendants();
        PrintWriter pw = Utilities.createPrintWriter();
        for (Pair<Integer, Node> pair : descendants) {
            pw.println(Utilities.pad(' ', 2 * pair.getFirst())
                    + DocUtil.toString(pair.getSecond()));
        }
        return Utilities.toString(pw);
    }

    public List<DOMNode> getChildren() {
        List<DOMNode> list = new LinkedList<DOMNode>();
        for (Node n : DocUtil.iterate(node.getChildNodes())) {
            list.add(makeNode(n));
        }
        return list;
    }

    public List<DOMNode> getChildren(String name) {
        List<DOMNode> list = new LinkedList<DOMNode>();
        for (Node n : DocUtil.iterate(node.getChildNodes())) {
            if (name.equals(n.getNodeName())) {
                list.add(makeNode(n));
            }

        }
        return list;
    }

    public DOMNode getParent() {
        if (node == node.getOwnerDocument().getDocumentElement()) {
            return null;
        }
        return makeNode(node.getParentNode());
    }

    public String getText() {
        return node.getTextContent();
    }

    public DOMNode getChild(String name) {
        List<DOMNode> children = getChildren(name);
        if (children.size() == 0) {
            return null;
        }
        return children.get(0);
    }

    public String getName() {
        return node.getNodeName();
    }

    public void beginTraversal(Node t, int level) {
        descendants.add(new Pair<Integer, Node>(level, t));
    }

    public boolean doTraverse(Node t, int level) {
        return true;
    }

    public void endTraversal(Node t, boolean leaf) {

    }

    public boolean isLeaf() {
        return node.getChildNodes().getLength() == 0;
    }

    public Object getUserObject(String name) {
        if (userObjects == null) {
            return null;
        }
        return userObjects.get(name);
    }

    public void setUserObject(String name, Object object) {
        if (userObjects == null) {
            userObjects = new HashMap<String, Object>();
        }
        userObjects.put(name, object);

    }

    public <T extends Node> List<DOMNode> getChildren(Class<T> nodeClass) {
        List<DOMNode> result = new LinkedList<DOMNode>();
        for (DOMNodeAdapter child : Utilities.castIterable(getChildren(),
                DOMNodeAdapter.class)) {
            if (nodeClass.isInstance(child.node)) {
                result.add(child);
            }
        }
        return result;
    }

}
