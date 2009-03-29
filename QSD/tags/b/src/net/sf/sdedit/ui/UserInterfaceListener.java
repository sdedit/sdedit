package net.sf.sdedit.ui;


/**
 * An interface for receivers of call-backs from a UserInterface.
 * 
 * @author Markus Strauch
 */
public interface UserInterfaceListener
{
//    /**
//     * The code has changed and a new diagram must be drawn.
//     * 
//     * @param checkSyntaxOnly flag denoting if only syntax should be checked
//     * and no diagram should be drawn yet
//     */
//    public void codeChanged(boolean checkSyntaxOnly);

    
    public void tabChanged (Tab previousTab, Tab currentTab);

    /**
     * A hyperlink has been clicked. The argument is a string containing a
     * colon, the part before the colon denotes the type of the hyperlink, the
     * part after the colon denotes its name.
     * <p>
     * <ul>
     * <li>example:file.sd The example file file.sd is to be loaded from the
     * examples package</li>
     * </ul>
     * 
     * @param hyperlink
     *            a string containing a colon, the part before the colon denotes
     *            the type of the hyperlink, the part after the colon denotes
     *            its name
     */
    public void hyperlinkClicked(String hyperlink);
    
}
