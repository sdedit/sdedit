// Copyright (c) 2006 - 2008, Markus Strauch.
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

package net.sf.sdedit.log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This is a simple yet effective logger class. There is only one logging
 * method, {@linkplain #log(Object)}. This method finds out the caller method's
 * signature by exploring the stack trace, and prints out the signature and
 * message. This is of course a costly operation, but all calls to
 * <tt>_LOG_.log</tt> can be stripped when a project is shipped. Also, you can
 * specify the patterns of the caller method signatures whose calls you want to
 * log (see {@linkplain #addPattern(String)} and {@linkplain #loadPatterns(URL)}). If
 * no pattern is specified, nothing at all will be logged and no costly
 * operation devoted to exploring a stack trace will be performed.
 * 
 * @author Markus Strauch
 * 
 */
public final class _LOG_ {
	

	

    private _LOG_() {
        /* empty */
    }
    
    private static PrintStream out = System.out;

    private static List<Pattern> patterns = new LinkedList<Pattern>();

    /**
     * Adds a pattern describing the signatures of methods whose calls to
     * {@linkplain #log(Object)} shall actually be logged. In a signature, the
     * fully qualified name of the class is followed by a period and the name of
     * the method. No distinction is made between methods with different
     * parameter lists. The given pattern is interpreted as a regular
     * expression, but first all occurrences of &quot;*&quot; are replaced by
     * &quot;.*&quot;. So patterns could have a form such as
     * 
     * <pre>
     * java.net*
     * </pre>
     * 
     * with the meaning: log all calls from classes in the package java.net and
     * its sub-packages
     * 
     * @param string a pattern describing signatures of methods whose calls
     * are to be logged
     */
    public static void addPattern(String string) {
        String patternString = string.replaceAll("\\*", ".*");
        patterns.add(Pattern.compile(patternString));
    }
    
    public static void setOut (PrintStream out) {
    	_LOG_.out = out;
    }

    /**
     * Loads a file from the given URL, the lines therein are all used as
     * arguments of {@linkplain #addPattern(String)}, unless they are empty
     * or start with '#'.
     * 
     * @param patternURL a URL of a file of lines that are interpreted as
     * signature patterns
     */
    public static void loadPatterns(URL patternURL) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    patternURL.openStream()));
            for (;;) {
                String line = reader.readLine();
                if (line == null) {
                    return;
                }
                line = line.trim();
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }
                addPattern(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String indent (int n) {
    	StringBuffer buffer = new StringBuffer();
    	for (int i = 0; i < n; i++) {
    		buffer.append(" ");
    		
    	}
    	return buffer.toString();
    }
    
    public static void log(Object msg) {
    	log (msg, 0);
    }

    public static void log(Object msg, int indentation) {
    	
        if (patterns.isEmpty()) {
            return;
        }
        String className = null;
        String methodName = null;
        String t = null;
        for (StackTraceElement trace : new Exception().getStackTrace())
        {
        	className = trace.getClassName();
        	if (className.indexOf("_LOG_") == -1) {
        		methodName = trace.getMethodName();
        		t= trace.toString();
        		break;
        	}
        }
        if (methodName == null) {
        	return;
        }
        String signature = className + "." + methodName;
        for (Pattern pattern : patterns) {
            if (pattern.matcher(signature).matches()) {
                t = t.substring(t.indexOf('('));
                int l = t.length();
                for (int i = l; i < 30; i++) {
                    t = " " + t;
                }
                out.println(indent(indentation) + t + "#" + methodName + "..." + msg);
                return;
            }
        }
    }
}
