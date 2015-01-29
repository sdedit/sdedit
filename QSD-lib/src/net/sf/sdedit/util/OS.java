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

package net.sf.sdedit.util;

public final class OS {

	public static final Type TYPE;

	private OS() {
		/* empty */
	}

	static {
		String os_name = System.getProperty("os.name");
		if (os_name == null) {
			TYPE = Type.UNKNOWN;
		} else {
			os_name = os_name.toLowerCase();
			if (System.getProperty("mrj.version") != null) {
				TYPE = Type.MAC;
			} else if (os_name.contains("windows")) {
				TYPE = Type.WINDOWS;
			} else {
				TYPE = Type.UNKNOWN;
			}
		}
	}

	public static String getUserDirectory() {
		if (TYPE == Type.WINDOWS) {
			String dir = WindowsRegistry
					.getValue(
							"HKCU/Software/Microsoft/Windows/CurrentVersion/Explorer/Shell Folders",
							"AppData");
			if (dir != null) {
				return dir;
			}
		}
		return System.getProperty("user.home");
	}

	public enum Type {

		WINDOWS,

		MAC,

		UNKNOWN
		
	}
}
//{{core}}
