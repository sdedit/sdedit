//Copyright (c) 2006 - 2015, Markus Strauch.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.sdedit.cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Option {

    String longOpt() default "";

    /**
     * @return true if the option's argument(s) is/are optional
     */
    boolean isArgOptional() default false;

    /**
     * The character separating arguments. Options with multiple arguments
     * correspond to methods with array return type.
     * 
     * @return the character separating arguments of an option
     */
    char separator() default ',';

    String description() default "";

    /**
     * Whether the option is required. Note that options for methods returning
     * primitive types (except boolean) are always required, regardless of the
     * annotation parameter. Options for methods returning primitive booleans
     * are always not required, with the returned value representing the
     * existence of the option.
     * 
     * @return
     */
    boolean required() default true;

    /**
     * The name of the option. If not specified, the name of the option equals
     * the name of the property.
     * 
     * @return
     */
    String name() default "";

    /**
     * The name (see {@linkplain #name()} of an option whose value is to be
     * returned when the option itself is omitted.
     * 
     * @return the name (see {@linkplain #name()} of an option whose value is to
     *         be returned when the option itself is omitted.
     */
    String inherit() default "";

    String dflt() default "";
    
    int numArgs() default 0;
    
    String group() default "";
    


}
