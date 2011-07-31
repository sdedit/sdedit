package net.sf.sdedit.util;

import java.awt.Component;
import java.awt.Container;

import com.zookitec.layout.ComponentEF;
import com.zookitec.layout.ContainerEF;
import com.zookitec.layout.ExplicitConstraints;
import com.zookitec.layout.Expression;

public class EF {
    
    public static Expression centeredX (Container cont, Component comp) {
        return ContainerEF.centerX(cont).subtract(ComponentEF.width(comp).divide(2));
    }
    
    public static Expression underY (Component comp, int gap) {
        return ComponentEF.bottom(comp).add(gap);
    }
    
    public static Expression underHeight (Component comp, int gap) {
        Container cont = comp.getParent();
        return ContainerEF.height(cont).subtract(ComponentEF.height(comp)).subtract(gap);
    }
    
    public static ExplicitConstraints inheritBounds (Component comp, Container cont) {
        ExplicitConstraints c = new ExplicitConstraints(comp);
        c.setX(ContainerEF.left(cont));
        c.setY(ContainerEF.top(cont));
        c.setWidth(ContainerEF.width(cont));
        c.setHeight(ContainerEF.height(cont));
        return c;
        
    }
    

}
