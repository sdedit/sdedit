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

package net.sf.sdedit.drawable;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import net.sf.sdedit.util.Pair;
import net.sf.sdedit.util.Utilities;

public class Strokes {
    
    public static enum StrokeType {
        
        SOLID,
        
        EMPTY,
        
        DASHED,
        
        DOTTED
        
    }
    
    
    
    private static Map<Pair<StrokeType, Integer>,Stroke> strokeCache;
    
    private static float [] [] dashes;
    
    
    static {
        dashes = new float[4][2];
        dashes[StrokeType.SOLID.ordinal()] = null;
        dashes[StrokeType.EMPTY.ordinal()] = new float [] {0,1};
        dashes[StrokeType.DASHED.ordinal()] = new float [] {5,5};
        dashes[StrokeType.DOTTED.ordinal()] = new float [] {1,2};
        strokeCache = new HashMap<Pair<StrokeType,Integer>, Stroke>();
    }
    
    public static Stroke getStroke (StrokeType type, int width) {
        Pair<StrokeType, Integer> key = Utilities.pair(type, width);
        Stroke stroke = strokeCache.get(key);
        if (stroke == null) {
            int cap = BasicStroke.CAP_BUTT;
            int join = BasicStroke.JOIN_ROUND;
            float miterlimit = 1F;
            float dash_phase = 0F;
            stroke = new BasicStroke(width, cap, join, miterlimit, dashes[type.ordinal()], dash_phase);
            synchronized (strokeCache) {
                strokeCache.put(key, stroke);
            }
        }
        return stroke;
    }
    
    public static Stroke defaultStroke () {
        return getStroke(StrokeType.SOLID, 1);
    }
    
    private Strokes () {
        
    }

}
