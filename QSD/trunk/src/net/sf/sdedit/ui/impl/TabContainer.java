package net.sf.sdedit.ui.impl;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import net.sf.sdedit.ui.Tab;

public interface TabContainer {
	
	public JComponent getComponent();
	
	public int getTabCount ();
	
	public boolean select (Tab tab);
	
	public void remove (Tab tab);
	
	public Tab getSelectedTab ();
	
	public Tab[] getSelectedTabs ();
	
	public List<Tab> getTabs ();
	
	public void addCategory (String category, ImageIcon icon);
	
	public String addTabToCategory (Tab tab, String category);
	
	public String addChildTab (Tab tab, Tab parent);
	
	public String addTab (Tab tab);
	
	public boolean exists (Tab tab);
	
	public boolean existsCategory (String title);
	
	public void addListener (TabContainerListener listener);
	
	public void removeListener (TabContainerListener listener);
	
	public List<Tab> getSuccessors(Tab tab);
	
	public List<Tab> getDescendants(Tab root);
	
	public Tab getParentTab (Tab tab);
	
	
	
	public void goToNextTab ();
	
	public void goToPreviousTab ();
	
	public boolean nextTabExists ();
	
	public boolean previousTabExists ();
	

}
