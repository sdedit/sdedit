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
