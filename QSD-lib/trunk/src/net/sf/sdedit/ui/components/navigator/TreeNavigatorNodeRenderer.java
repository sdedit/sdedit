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

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.ui.components.Stainable;

public class TreeNavigatorNodeRenderer extends JPanel implements TreeCellRenderer  {
	
	private boolean stained;
	
	private DefaultTreeCellRenderer renderer;
	
	private JLabel saveIconLabel;
	
	public TreeNavigatorNodeRenderer() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		renderer = new DefaultTreeCellRenderer();
		setOpaque(false);
		saveIconLabel = new JLabel(Icons.getIcon("save"));

	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		renderer.getTreeCellRendererComponent(
				tree, value, sel, expanded, leaf, row, hasFocus);
		TreeNavigatorNode node = (TreeNavigatorNode) value;
		stained = false;
		if (Stainable.class.isInstance(node.getComponent())) {
			if (!Stainable.class.cast(node.getComponent()).isClean()) {
				stained = true;
				// rendererComponent.setForeground(Color.RED);
			}
		}
		removeAll();
		
		((JLabel) renderer).setText(node.getTitle());
		((JLabel) renderer).setIcon(node.getIcon());
		
		renderer.setOpaque(false);
		
		add(renderer);
		add(Box.createRigidArea(new Dimension(3,1)));
		if (stained) {
			add(saveIconLabel);
		} else {
			add(Box.createRigidArea(saveIconLabel.getPreferredSize()));
		}
		
		return this;
	}
}
