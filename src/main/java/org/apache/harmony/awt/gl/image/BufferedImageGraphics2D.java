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
 * @author Alexey A. Petrenko
 */
package org.apache.harmony.awt.gl.image;

import org.apache.harmony.awt.gl.CommonGraphics2D;
import org.teavm.classlib.java.awt.TGraphics;
import org.teavm.classlib.java.awt.TGraphicsConfiguration;
import org.teavm.classlib.java.awt.TRectangle;
import org.teavm.classlib.java.awt.TShape;
import org.teavm.classlib.java.awt.font.TGlyphVector;
import org.teavm.classlib.java.awt.image.TBufferedImage;
import org.teavm.classlib.java.awt.image.TColorModel;
import org.teavm.classlib.java.awt.image.TWritableRaster;

import org.apache.harmony.awt.gl.Surface;
import org.apache.harmony.awt.gl.render.JavaBlitter;
import org.apache.harmony.awt.gl.render.NativeImageBlitter;

/**
 * BufferedImageGraphics2D is implementation of CommonTGraphics2D for
 * drawing on buffered images. 
 */
public class BufferedImageGraphics2D extends CommonGraphics2D {
    private TBufferedImage bi = null;
    private TRectangle bounds = null;

    public BufferedImageGraphics2D(TBufferedImage bi) {
        super();
        this.bi = bi;
        this.bounds = new TRectangle(0, 0, bi.getWidth(), bi.getHeight());
        clip(bounds);
        dstSurf = Surface.getImageSurface(bi);
        if(dstSurf.isNativeDrawable()){
            blitter = NativeImageBlitter.getInstance();
        }else{
            blitter = JavaBlitter.getInstance();
        }
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    }

    @Override
    public TGraphics create() {
        BufferedImageGraphics2D res = new BufferedImageGraphics2D(bi);
        copyInternalFields(res);
        return res;
    }

    @Override
    public TGraphicsConfiguration getDeviceConfiguration() {
        return null;
    }

    public TColorModel getTColorModel() {
        return bi.getColorModel();
    }

    public TWritableRaster getTWritableRaster() {
        return bi.getRaster();
    }
    
    @Override
    public void drawString(String str, float x, float y) {
        TShape sh = font.createGlyphVector(this.getFontRenderContext(), str).getOutline(x, y);
        fill(sh);
    }

    @Override
    public void drawGlyphVector(TGlyphVector gv, float x, float y) {
        TShape sh = gv.getOutline(x, y);
        this.fill(sh);
    }
}