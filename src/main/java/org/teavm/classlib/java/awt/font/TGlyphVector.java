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

import org.teavm.classlib.java.awt.TFont;

import org.teavm.classlib.java.awt.TRectangle;
import org.teavm.classlib.java.awt.TShape;
import org.teavm.classlib.java.awt.geom.TAffineTransform;
import org.teavm.classlib.java.awt.geom.TPoint2D;
import org.teavm.classlib.java.awt.geom.TRectangle2D;

public abstract class TGlyphVector implements Cloneable {

    public static final int FLAG_HAS_TRANSFORMS = 1;

    public static final int FLAG_HAS_POSITION_ADJUSTMENTS = 2;

    public static final int FLAG_RUN_RTL = 4;

    public static final int FLAG_COMPLEX_GLYPHS = 8;

    public static final int FLAG_MASK = 15; // (|) mask of other flags

    public TGlyphVector() {
    }

    public TRectangle getPixelBounds(TFontRenderContext frc, float x, float y) {
        // default implementation - integer TRectangle, that encloses visual 
        // bounds rectangle
        TRectangle2D visualRect = getVisualBounds();

        int minX = (int)Math.floor(visualRect.getMinX() + x);
        int minY = (int)Math.floor(visualRect.getMinY() + y);
        int width = (int)Math.ceil(visualRect.getMaxX() + x) - minX;
        int height = (int)Math.ceil(visualRect.getMaxY() + y) - minY;

        return new TRectangle(minX, minY, width, height);
    }

    public TRectangle getGlyphPixelBounds(int index, TFontRenderContext frc, 
            float x, float y) {
        TRectangle2D visualRect = getGlyphVisualBounds(index).getBounds2D();

        int minX = (int)Math.floor(visualRect.getMinX() + x);
        int minY = (int)Math.floor(visualRect.getMinY() + y);
        int width = (int)Math.ceil(visualRect.getMaxX() + x) - minX;
        int height = (int)Math.ceil(visualRect.getMaxY() + y) - minY;

        return new TRectangle(minX, minY, width, height);
    }

    public abstract TRectangle2D getVisualBounds();

    public abstract TRectangle2D getLogicalBounds();

    public abstract void setGlyphPosition(int glyphIndex, TPoint2D newPos);

    public abstract TPoint2D getGlyphPosition(int glyphIndex);

    public abstract void setGlyphTransform(int glyphIndex, 
            TAffineTransform trans);

    public abstract TAffineTransform getGlyphTransform(int glyphIndex);

    public abstract boolean equals(TGlyphVector glyphVector);

    public abstract TGlyphMetrics getTGlyphMetrics(int glyphIndex);

    public abstract TGlyphJustificationInfo getTGlyphJustificationInfo(
            int glyphIndex);

    public abstract TFontRenderContext getTFontRenderContext();

    public TShape getGlyphOutline(int glyphIndex, float x, float y) {
        TShape initialTShape = getGlyphOutline(glyphIndex);
        TAffineTransform trans = TAffineTransform.getTranslateInstance(x, y);
        return trans.createTransformedShape(initialTShape);
    }

    public abstract TShape getGlyphVisualBounds(int glyphIndex);

    public abstract TShape getGlyphOutline(int glyphIndex);

    public abstract TShape getGlyphLogicalBounds(int glyphIndex);

    public abstract TShape getOutline(float x, float y);

    public abstract TShape getOutline();

    public abstract TFont getTFont();

    public abstract int[] getGlyphCodes(int beginGlyphIndex, int numEntries, 
            int[] codeReturn);

    public int[] getGlyphCharIndices(int beginGlyphIndex, int numEntries, 
            int[] codeReturn) {
        if (codeReturn == null) {
            codeReturn = new int[numEntries];
        }

        for (int i = 0; i < numEntries; i++){
            codeReturn[i] = getGlyphCharIndex(i+beginGlyphIndex);
        }
        return codeReturn;
    }

    public abstract float[] getGlyphPositions(int beginGlyphIndex, 
            int numEntries, float[] positionReturn);

    public abstract int getGlyphCode(int glyphIndex);

    public int getGlyphCharIndex(int glyphIndex){
        // default implementation one-to-one
        return glyphIndex;
    }

    public abstract void performDefaultLayout();

    public abstract int getNumGlyphs();

    public int getLayoutFlags(){
        // default implementation - returned value is 0
        return 0;
    }

}

