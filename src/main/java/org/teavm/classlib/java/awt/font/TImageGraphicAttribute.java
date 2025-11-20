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

import org.teavm.classlib.java.awt.TGraphics2D;
import org.teavm.classlib.java.awt.TImage;
import org.teavm.classlib.java.awt.geom.TRectangle2D;

import org.apache.harmony.misc.HashCode;


public final class TImageGraphicAttribute extends TGraphicAttribute {

    // TImage object rendered by this TImageGraphicAttribute
    private TImage fTImage;

    // X coordinate of the origin point
    private float fOriginX;

    // Y coordinate of the origin point
    private float fOriginY;

    // the width of the image object
    private float fImgWidth;

    // the height of the image object
    private float fImgHeight;

    public TImageGraphicAttribute(TImage image, int alignment, float originX, 
            float originY) {
        super(alignment);

        this.fTImage = image;
        this.fOriginX = originX;
        this.fOriginY = originY;

        this.fImgWidth = fTImage.getWidth(null);
        this.fImgHeight = fTImage.getHeight(null);

    }

    public TImageGraphicAttribute(TImage image, int alignment) {
        this(image, alignment, 0, 0);
    }

    @Override
    public int hashCode() {
        HashCode hash = new HashCode();

        hash.append(fTImage.hashCode());
        hash.append(getAlignment());
        return hash.hashCode();
    }

    public boolean equals(TImageGraphicAttribute iga) {
        if (iga == null) {
            return false;
        }

        if (iga == this) {
            return true;
        }

        return (fOriginX == iga.fOriginX &&
                fOriginY == iga.fOriginY &&
                getAlignment() == iga.getAlignment() &&
                fTImage.equals(iga.fTImage));
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return equals((TImageGraphicAttribute) obj);
        }
        catch(ClassCastException e) {
            return false;
        }

    }

    @Override
    public void draw(TGraphics2D g2, float x, float y) {
        g2.drawImage(fTImage, (int)(x - fOriginX), (int)(y - fOriginY), null);
    }

    @Override
    public float getAdvance() {
        return Math.max(0, fImgWidth - fOriginX);
    }

    @Override
    public float getAscent() {
        return Math.max(0, fOriginY);
    }

    @Override
    public TRectangle2D getBounds() {
        return new TRectangle2D.TFloat(-fOriginX, -fOriginY, fImgWidth, fImgHeight);
    }

    @Override
    public float getDescent() {
        return Math.max(0, fImgHeight - fOriginY);
    }

}

