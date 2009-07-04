package net.sf.sdedit.util;

import java.util.LinkedList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DOMNodeAdapter implements DOMNode {
    
    private Node node;
    
    private static XPathFactory xPathFactory = XPathFactory.newInstance();
    
    DOMNodeAdapter (Node node) {
        this.node = node;
    }

    @Override
    public String getAttribute(String name) {
        if (!(node instanceof Element)) {
            return null;
        }
        return DocUtil.getAttribute((Element) node, name);
    }
    
    public DOMNode findNode (String exp) {
        XPath xpath = xPathFactory.newXPath();
        try {
            Node result = (Node) xpath.evaluate(exp, node,
                    XPathConstants.NODE);
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
    
    public List<DOMNode> findNodes (String exp) {
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
    
    public String toString () {
        return DocUtil.toString(node);
    }

    @Override
    public List<DOMNode> getChildren() {
        List<DOMNode> list = new LinkedList<DOMNode>();
        for (Node n : DocUtil.iterate(node.getChildNodes())) {
            list.add(new DOMNodeAdapter(n));
        }
        return list;
    }
    
    public List<DOMNode> getChildren (String name) {
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
    
    public String getText () {
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

}
