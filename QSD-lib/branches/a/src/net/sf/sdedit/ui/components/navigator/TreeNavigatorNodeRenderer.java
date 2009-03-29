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
