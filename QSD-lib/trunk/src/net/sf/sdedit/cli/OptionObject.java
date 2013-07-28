package net.sf.sdedit.cli;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import net.sf.sdedit.util.ObjectFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class OptionObject {

    private final PropertyDescriptor property;

    private final Option option;

    public OptionObject(PropertyDescriptor property) {
        this.property = property;
        this.option = makeOption();
    }

    private net.sf.sdedit.cli.Option option() {
        return method().getAnnotation(net.sf.sdedit.cli.Option.class);
    }

    public Method method() {
        return property.getReadMethod();
    }

    public Option getOption() {
        return option;
    }

    private boolean isArray() {
        return method().getReturnType().isArray();
    }

    private boolean isBoolean() {
        return Boolean.TYPE == getType();
    }

    private Class<?> getType() {
        if (isArray()) {
            return method().getReturnType().getComponentType();
        }
        return method().getReturnType();
    }

    public String getName() {
        String name = option().name();
        if ("".equals(name)) {
            name = property.getName();
        }
        return name;
    }

    private String getArgName() {
        String argName;
        if (isArray()) {
            String type = method().getReturnType().getComponentType()
                    .getSimpleName();
            argName = type + option().separator() + type + option().separator()
                    + "...";
        } else {
            argName = getType().getSimpleName();
        }
        return argName;
    }

    private String getDescription() {
        String description = null;
        String dflt = option().dflt();
        if ("".equals(dflt) && !"".equals(option().inherit())) {
            dflt = "<" + option().inherit() + ">";
        }
        if (!"".equals(option().description()) || !"".equals(dflt)) {
            description = option().description();
            if (!"".equals(dflt)) {
                if (!"".equals(description)) {
                    description += ". Default is " + dflt;
                } else {
                    description = "Default is " + dflt;
                }
            }
            description += ".";
        }
        return description;
    }

    private org.apache.commons.cli.Option makeOption() {
        String opt = getName();
        String longOpt = option().longOpt();
        if ("".equals(longOpt)) {
            longOpt = null;
        }
        Option option = new Option(opt, getDescription());
        option.setLongOpt(longOpt);
        option.setRequired(option().required());
        option.setArgName(getArgName());
        if (isBoolean()) {
            option.setArgs(0);
            option.setRequired(false);
            option.setOptionalArg(false);
        } else if (isArray()) {
            int numArgs = option().numArgs();
            if (numArgs <= 0) {
                numArgs = Option.UNLIMITED_VALUES;
            }
            option.setArgs(numArgs);
            option.setValueSeparator(option().separator());
            option.setOptionalArg(option().isArgOptional());
        } else {
            option.setArgs(1);
            if (getType().isPrimitive()) {
                // method returning primitive types are required options
                // since they cannot return null for indicating non-existence
                option.setRequired(true);
            }
            
        }
        return option;
    }

    private Object createFromString(Class<?> cls, String string) {
        try {
            return ObjectFactory.createFromString(cls, string);
        } catch (RuntimeException re) {
            throw new IllegalArgumentException("cannot convert value " + string
                    + " to type " + cls.getSimpleName());
        }
    }

    private Object makeScalar(CommandLine line) {
        String value = line.getOptionValue(getName());
        if (value == null) {
            value = dflt();
        }
        if (value == null) {
            return null;
        }
        return createFromString(getType(), value);
    }

    private Object makeArray(CommandLine line) {
        String[] values = line.getOptionValues(getName());
        if (values == null && dflt() != null) {
            values = new String[] { dflt() };
        }
        if (values == null) {
            values = new String[0];
        }
        Object array = null;
        if (values != null) {
            Class<?> type = getType();
            array = Array.newInstance(type, values.length);
            for (int i = 0; i < values.length; i++) {
                Array.set(array, i, createFromString(type, values[i]));
            }
        }
        return array;
    }
    
    public String inherit() {
        String inh = option().inherit();
        if ("".equals(inh)) {
            return null;
        }
        return inh;        
    }

    private String dflt() {
        String dflt = option().dflt();
        if ("".equals(dflt)) {
            return null;
        }
        return dflt;
    }

    public Object getValue(CommandLine commandLine) {
        boolean has = commandLine.hasOption(getName());
        if (isBoolean()) {
            return has;
        }
        if (!has && dflt() == null) {
            return null;
        }
        if (isArray()) {
            return makeArray(commandLine);
        }
        return makeScalar(commandLine);
    }
}
