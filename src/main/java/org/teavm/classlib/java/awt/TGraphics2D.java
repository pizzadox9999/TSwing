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

package org.teavm.classlib.java.awt;

import java.text.AttributedCharacterIterator;
import java.util.Map;
import org.teavm.classlib.java.awt.image.TBufferedImageOp;

public abstract class TGraphics2D extends TGraphics {
    protected TGraphics2D() {
        super();
    }

    public abstract void addRenderingHints(Map<?, ?> hints);

    public abstract void clip(TShape s);

    public abstract void draw(TShape s);

    public abstract void drawGlyphVector(TGlyphVector g, float x, float y);

    public abstract void drawImage(TBufferedImage img, TBufferedImageOp op, int x, int y);

    public abstract boolean drawImage(TImage img, TAffineTransform xform, TImageObserver obs);

    public abstract void drawRenderableImage(TRenderableImage img, TAffineTransform xform);

    public abstract void drawRenderedImage(TRenderedImage img, TAffineTransform xform);

    public abstract void drawString(AttributedCharacterIterator iterator, float x, float y);

    @Override
    public abstract void drawString(AttributedCharacterIterator iterator, int x, int y);

    public abstract void drawString(String s, float x, float y);

    @Override
    public abstract void drawString(String str, int x, int y);

    public abstract void fill(TShape s);

    public abstract TColor getBackground();

    public abstract TComposite getComposite();

    public abstract TGraphicsConfiguration getDeviceConfiguration();

    public abstract TFontRenderContext getFontRenderContext();

    public abstract TPaint getPaint();

    public abstract Object getRenderingHint(RenderingHints.Key key);

    public abstract TRenderingHints getRenderingHints();

    public abstract TStroke getStroke();

    public abstract TAffineTransform getTransform();

    public abstract boolean hit(TRectangle rect, TShape s, boolean onStroke);

    public abstract void rotate(double theta);

    public abstract void rotate(double theta, double x, double y);

    public abstract void scale(double sx, double sy);

    public abstract void setBackground(TColor color);

    public abstract void setComposite(TComposite comp);

    public abstract void setPaint(TPaint paint);

    public abstract void setRenderingHint(TRenderingHints.Key key, Object value);

    public abstract void setRenderingHints(Map<?, ?> hints);

    public abstract void setStroke(TStroke s);

    public abstract void setTransform(TAffineTransform Tx);

    public abstract void shear(double shx, double shy);

    public abstract void transform(TAffineTransform Tx);

    public abstract void translate(double tx, double ty);

    @Override
    public abstract void translate(int x, int y);

    @Override
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        // According to the spec, color should be used instead of paint,
        // so Graphics.fill3DRect resets paint and
        // it should be restored after the call
        TPaint savedPaint = getPaint();
        super.fill3DRect(x, y, width, height, raised);
        setPaint(savedPaint);
    }

    @Override
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        // According to the spec, color should be used instead of paint,
        // so Graphics.draw3DRect resets paint and
        // it should be restored after the call
        TPaint savedPaint = getPaint();
        super.draw3DRect(x, y, width, height, raised);
        setPaint(savedPaint);
    }
}