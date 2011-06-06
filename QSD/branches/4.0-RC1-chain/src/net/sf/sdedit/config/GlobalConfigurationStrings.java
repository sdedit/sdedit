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

package net.sf.sdedit.config;

import java.nio.charset.Charset;
import java.util.LinkedList;

import javax.swing.UIManager;

import net.sf.sdedit.ui.components.configuration.StringSelectionProvider;

public class GlobalConfigurationStrings implements
		StringSelectionProvider {

	public GlobalConfigurationStrings() {
		super();
	}

	public String[] getStringSelection(String property) {
		if (property.equals("fileEncoding")) {
			return Charset.availableCharsets().keySet().toArray(new String[0]);
		}
		if (property.equals("lookAndFeel")) {

			LinkedList<String> names = new LinkedList<String>();

			for (UIManager.LookAndFeelInfo info : UIManager
					.getInstalledLookAndFeels()) {
				names.add(info.getName());
			}
			return names.toArray(new String[names.size()]);
		}

		throw new IllegalArgumentException();
	}

}
//{{core}}
