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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

public class BeanComparator<T> implements Comparator<T> {

    public static int COMPARABLE = 0;

    public static int TO_STRING = 1;

    public static int FALL_BACK_TO_STRING = 2;

    private Method[] readMethods;

    private boolean[] toStringFlags;

    public BeanComparator(Class<T> beanClass, int mode, String... properties) {
        if (properties.length == 0) {
            throw new IllegalArgumentException(
                    "Cannot create a BeanComparator without properties");
        }
        if (mode < 0 || mode > 2) {
            throw new IllegalArgumentException("Illegal BeanComparator mode: "
                    + mode);
        }
        this.readMethods = new Method[properties.length];
        toStringFlags = new boolean [properties.length];
        Map<String, PropertyDescriptor> propMap = new HashMap<String, PropertyDescriptor>();
        for (PropertyDescriptor prop : Utilities.getProperties(beanClass)) {
            if (propMap.containsKey(prop.getName().toUpperCase())) {
                throw new IllegalArgumentException("Duplicate property name: "
                        + beanClass.getName() + "#" + prop.getName());
            }
            propMap.put(prop.getName().toUpperCase(), prop);
        }

        for (int i = 0; i < properties.length; i++) {
            PropertyDescriptor prop = propMap.get(properties[i].toUpperCase());
            if (prop == null) {
                throw new IllegalArgumentException("Property not found: "
                        + properties[i]);
            }
            Method readMethod = prop.getReadMethod();
            if (readMethod == null) {
                throw new IllegalArgumentException("Write-only property: "
                        + properties[i]);
            }
            
            if (!prop.getPropertyType().isPrimitive() && !Comparable.class.isAssignableFrom(prop.getPropertyType())) {
                if (mode == COMPARABLE) {
                    throw new IllegalArgumentException(
                            "Property not comparable: " + properties[i]);
                }
                toStringFlags[i] = true;
            } else {
                toStringFlags[i] = mode == TO_STRING;
            }
            readMethods[i] = readMethod;
        }
    }

    
    @SuppressWarnings("unchecked")
    public int compare(T t1, T t2) {
        Object o1;
        Object o2;
        Comparable c1;
        Comparable c2;
        int result = 0;
        for (int i = 0; result == 0 && i < readMethods.length; i++) {
            o1 = null;
            o2 = null;
            c1 = null;
            c2 = null;
            
            try {
                o1 = readMethods[i].invoke(t1);
            } catch (IllegalAccessException e) {
                /* ignored */
            } catch (InvocationTargetException e) {
                /* ignored */
            }
            try {
                o2 = readMethods[i].invoke(t2);
            } catch (IllegalAccessException e) {
                /* ignored */
            } catch (InvocationTargetException e) {
                /* ignored */
            }
            if (o1 != null && o2 == null) {
                result = 1;
            } else if (o1 == null && o2 != null) {
                result = -1;
            } else if (o1 != null && o2 != null) {
                if (toStringFlags[i]) {
                    c1 = o1.toString();
                    c2 = o2.toString();
                } else {
                    c1 = Comparable.class.cast(o1);
                    c2 = Comparable.class.cast(o2);
                }
                result = c1.compareTo(c2);
            }
            
        }
        return result;
    }
    
    public static class TestBean {
        
        private int num1;
        
        private int num2;
        
        public TestBean(int num1, int num2) {
            this.num1 = num1;
            this.num2 = num2;
        }

        public void setNum1(int num1) {
            this.num1 = num1;
        }

        public int getNum1() {
            return num1;
        }

        public void setNum2(int num2) {
            this.num2 = num2;
        }

        public int getNum2() {
            return num2;
        }
        
        public String toString () {
            return num1 + "/" + num2;
        }
        
        
        
    }
    
    public static void main (String [] argv) {
        Random rnd = new Random();
        BeanComparator<TestBean> bc = new BeanComparator<TestBean>(TestBean.class,BeanComparator.TO_STRING,"num2","num1");
        TreeSet<TestBean> set = new TreeSet<TestBean>(bc);
        for (int i = 0; i < 20; i++) {
            set.add(new TestBean(rnd.nextInt(),rnd.nextInt()));
        }
        System.out.println(set);
    }

}
