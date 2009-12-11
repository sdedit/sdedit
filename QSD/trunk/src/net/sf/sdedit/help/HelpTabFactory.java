package net.sf.sdedit.help;

import javax.swing.JComponent;

import net.sf.sdedit.ui.impl.UserInterfaceImpl;

public interface HelpTabFactory {
	
	public JComponent getHelpTabComponent (UserInterfaceImpl ui, String title, boolean advanced);

}
