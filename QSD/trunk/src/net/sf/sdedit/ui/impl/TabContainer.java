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
	
	public String addTabToCategory (Tab tab, String category, boolean selectIt);
	
	public String addTabToCategory (Tab tab, String category);
	
	public String addChildTab (Tab tab, Tab parent, boolean selectIt, Tab previousTab);
	
	   public String addChildTab(Tab tab, Tab parent) ;
	
	public String addTab (Tab tab, boolean selectIt);
	
	public boolean exists (Tab tab);
	
	public boolean existsCategory (String title);
	
	public void addListener (TabContainerListener listener);
	
	public void removeListener (TabContainerListener listener);
	
	public List<Tab> getSuccessors(Tab tab);
	
	public List<Tab> getDescendants(Tab root);
	
	public Tab getParentTab (Tab tab);
	
	public void disableTabHistory ();
	
	public void enableTabHistory ();
	
	public void goToNextTab ();
	
	public void goToPreviousTab ();
	
	public boolean nextTabExists ();
	
	public boolean previousTabExists ();
	

}
