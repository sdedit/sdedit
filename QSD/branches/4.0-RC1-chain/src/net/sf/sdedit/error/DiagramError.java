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

package net.sf.sdedit.error;

import net.sf.sdedit.diagram.DiagramDataProvider;

public class DiagramError extends Exception {

    private DiagramDataProvider provider;
    
    /**
     * Constructor.
     * 
     * @param provider
     *            the DiagramDataProvider that delivered the wrong
     *            specification
     * @param msg
     *            a message describing the DiagramError
     */
    protected DiagramError(DiagramDataProvider provider, String msg) {
        super(msg);
        this.provider = provider;
    }

    /**
     * Returns the <tt>DiagramDataProvider</tt> that was used when the
     * error occurred.
     * 
     * @return the <tt>DiagramDataProvider</tt> that was used when the
     * error occurred
     */
    public DiagramDataProvider getProvider() {
        return provider;
    }
    
    public void setProvider (DiagramDataProvider provider) {
    	this.provider = provider;
    }
}
//{{core}}
