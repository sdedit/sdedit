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
