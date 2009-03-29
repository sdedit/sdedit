package net.sf.sdedit.ui.components.navigator;

import java.util.LinkedList;
import java.util.List;

abstract class SimpleTreeTab {
	
	private SimpleTreeTab parent;

	private List<SimpleTreeTab> children;
	
	protected SimpleTreeTab (SimpleTreeTab parent) {
		this.parent = parent;
		children = new LinkedList<SimpleTreeTab>();
	}
	
	protected SimpleTreeTab [] getChildren () {
		return children.toArray(new SimpleTreeTab[children.size()]);
	}
	
	protected SimpleTreeTab _getParent () {
		return parent;
	}
	
	protected void _removeChild (SimpleTreeTab child) {
		children.remove(child);
	}
	
	protected void _addChild (SimpleTreeTab child) {
		children.add(child);
	}
	
	

}
