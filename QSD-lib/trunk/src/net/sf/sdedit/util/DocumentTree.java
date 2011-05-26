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
