/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * @author Ilya S. Okomin
 */
package org.teavm.classlib.java.awt.font;

import org.teavm.classlib.java.awt.TBasicStroke;
import org.teavm.classlib.java.awt.TGraphics2D;
import org.teavm.classlib.java.awt.TShape;
import org.teavm.classlib.java.awt.TStroke;
import org.teavm.classlib.java.awt.geom.TAffineTransform;
import org.teavm.classlib.java.awt.geom.TRectangle2D;

import org.apache.harmony.misc.HashCode;


public final class TShapeGraphicAttribute extends TGraphicAttribute {

    // shape to render
    private TShape fTShape;
    
    // flag, if the shape should be stroked (true) or filled (false)
    private boolean fTStroke;

    // bounds of the shape
    private TRectangle2D fBounds;
    
    // X coordinate of the origin point
    private float fOriginX;
    
    // Y coordinate of the origin point
    private float fOriginY;

    // width of the shape
    private float fTShapeWidth;
    
    // height of the shape
    private float fTShapeHeight;

    public static final boolean STROKE = true;

    public static final boolean FILL = false;

    public TShapeGraphicAttribute(TShape shape, int alignment, boolean stroke) {
        super(alignment);

        this.fTShape = shape;
        this.fTStroke = stroke;

        this.fBounds  = fTShape.getBounds2D();

        this.fOriginX = (float)fBounds.getMinX();
        this.fOriginY = (float)fBounds.getMinY();

        this.fTShapeWidth = (float)fBounds.getWidth();
        this.fTShapeHeight = (float)fBounds.getHeight();
    }

    @Override
    public int hashCode() {
        HashCode hash = new HashCode();

        hash.append(fTShape.hashCode());
        hash.append(getAlignment());
        return hash.hashCode();
    }

    public boolean equals(TShapeGraphicAttribute sga) {
        if (sga == null) {
            return false;
        }

        if (sga == this) {
            return true;
        }

        return ( fTStroke == sga.fTStroke &&
                getAlignment() == sga.getAlignment() &&
                fTShape.equals(sga.fTShape));

    }

    @Override
    public boolean equals(Object obj) {
        try {
            return equals((TShapeGraphicAttribute) obj);
        }
        catch(ClassCastException e) {
            return false;
        }
    }

    @Override
    public void draw(TGraphics2D g2, float x, float y) {
        TAffineTransform at = TAffineTransform.getTranslateInstance(x, y);
        if (fTStroke == STROKE){
            TStroke oldTStroke = g2.getStroke();
            g2.setStroke(new TBasicStroke());
            g2.draw(at.createTransformedShape(fTShape));
            g2.setStroke(oldTStroke);
        } else {
            g2.fill(at.createTransformedShape(fTShape));
        }

    }

    @Override
    public float getAdvance() {
        return Math.max(0, fTShapeWidth + fOriginX);
    }

    @Override
    public float getAscent() {
        return Math.max(0, -fOriginY);
    }

    @Override
    public TRectangle2D getBounds() {
        return (TRectangle2D)fBounds.clone();
    }

    @Override
    public float getDescent() {
        return Math.max(0, fTShapeHeight + fOriginY);
    }

}

