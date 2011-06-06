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

import java.util.Properties;

/**
 * Utility class providing the
 * {@linkplain #getIntegerProperty(int, Properties, String)} method.
 * 
 * @author Markus Strauch
 * 
 */
public final class IntegerProperty {

    private IntegerProperty() {

    }

    /**
     * Returns the value of a property, converted to an int. If the property is
     * not set or is not convertable to an int, it it set to a default value.
     * 
     * @param dflt
     *            a default value of the property
     * @param properties
     *            a <tt>Properties</tt> object
     * @param propertyName
     *            a name of a property in <tt>properties</tt>
     * @return the value of the property, converted to <tt>int</tt>, or
     *         <tt>dflt</tt>, if the value is not set or cannot be converted
     */
    public static int getIntegerProperty(int dflt, Properties properties,
            String propertyName) {
        String valueString = properties.getProperty(propertyName);
        if (valueString != null) {
            try {
                dflt = Integer.parseInt(valueString);
            } catch (NumberFormatException nfe) {
                properties.setProperty(propertyName, String.valueOf(dflt));
            }
        } else {
            properties.setProperty(propertyName, String.valueOf(dflt));
        }
        return dflt;
    }

}
//{{core}}
