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
package org.apache.harmony.awt.gl;


import org.teavm.classlib.java.awt.TAlphaComposite;
import org.teavm.classlib.java.awt.TBasicStroke;
import org.teavm.classlib.java.awt.TColor;
import org.teavm.classlib.java.awt.TComposite;
import org.teavm.classlib.java.awt.TFont;
import org.teavm.classlib.java.awt.TFontMetrics;
import org.teavm.classlib.java.awt.TGraphics2D;
import org.teavm.classlib.java.awt.TGraphicsConfiguration;
import org.teavm.classlib.java.awt.TImage;
import org.teavm.classlib.java.awt.TPaint;
import org.teavm.classlib.java.awt.TPaintContext;
import org.teavm.classlib.java.awt.TPoint;
import org.teavm.classlib.java.awt.TPolygon;
import org.teavm.classlib.java.awt.TRectangle;
import org.teavm.classlib.java.awt.TRenderingHints;
import org.teavm.classlib.java.awt.TShape;
import org.teavm.classlib.java.awt.TStroke;
import org.teavm.classlib.java.awt.TToolkit;
import org.teavm.classlib.java.awt.font.TFontRenderContext;
import org.teavm.classlib.java.awt.font.TGlyphVector;
import org.teavm.classlib.java.awt.geom.TAffineTransform;
import org.teavm.classlib.java.awt.geom.TArc2D;
import org.teavm.classlib.java.awt.geom.TEllipse2D;
import org.teavm.classlib.java.awt.geom.TLine2D;
import org.teavm.classlib.java.awt.geom.TPathIterator;
import org.teavm.classlib.java.awt.geom.TRoundRectangle2D;
import org.teavm.classlib.java.awt.image.TAffineTransformOp;
import org.teavm.classlib.java.awt.image.TBufferedImage;
import org.teavm.classlib.java.awt.image.TBufferedImageOp;
import org.teavm.classlib.java.awt.image.TImageObserver;
import org.teavm.classlib.java.awt.image.TRaster;
import org.teavm.classlib.java.awt.image.TWritableRaster;
import java.text.AttributedCharacterIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.harmony.awt.gl.font.TFontManager;
import org.apache.harmony.awt.gl.font.fontlib.FLTextRenderer;
import org.apache.harmony.awt.gl.image.OffscreenImage;
import org.apache.harmony.awt.gl.render.Blitter;
import org.apache.harmony.awt.gl.render.JavaArcRasterizer;
import org.apache.harmony.awt.gl.render.JavaLineRasterizer;
import org.apache.harmony.awt.gl.render.JavaShapeRasterizer;
import org.apache.harmony.awt.gl.render.JavaTextRenderer;
import org.apache.harmony.awt.gl.render.NullBlitter;

/*
 * List of abstract methods to implement in subclusses
 * Graphics.copyArea(int x, int y, int width, int height, int dx, int dy)
 * Graphics.create()
 * TGraphics2D.getDeviceConfiguration()
 * CommonGraphics2D.fillMultiRectAreaTColor(MultiRectArea mra);
 * CommonGraphics2D.fillMultiRectAreaTPaint(MultiRectArea mra);
 */

/**
 * CommonGraphics2D class is a super class for all system-dependent
 * implementations. It implements major part of Graphics and TGraphics2D
 * abstract methods.
 * <h2>CommonGraphics2D Class Internals</h2>
 * <h3>Line and TShape TRasterizers</h3>
 * <p>
 * The CommonGraphics2D class splits all shapes into a set of rectangles 
 * to unify the drawing process for different operating systems and architectures. 
 * For this purpose Java 2D* uses the JavaShapeRasterizer and the JavaLineRasterizer 
 * classes from the org.apache.harmony.awt.gl.render package. The JavaShapeRasterizer 
 * class splits an object implementing a TShape interface into a set of rectangles and 
 * produces a MultiRectArea object. The JavaLineRasterizer class makes line drawing 
 * more accurate and processes lines with strokes, which are instances of the TBasicStroke 
 * class.
 * </p>
 * <p>
 * To port the shape drawing to another platform you just need to override 
 * rectangle-drawing methods. However, if your operating system has functions to draw 
 * particular shapes, you can optimize your subclass of the CommonGraphics2D class by 
 * using this functionality in overridden methods.
 * </p>

 * <h3>Blitters</h3>
 * <p>
 * Blitter classes draw images on the display or buffered images. All blitters inherit 
 * the org.apache.harmony.awt.gl.render.Blitter interface.
 * </p>
 * <p>Blitters are divided into:
 * <ul>
 * <li>Native blitters for simple types of images, which the underlying native library 
 * can draw.</li> 
 * <li>Java* blitters for those types of images, which the underlying native library 
 * cannot handle.</li>
 * </ul></p>
 * <p>
 * DRL Java 2D* also uses blitters to fill the shapes and the user-defined subclasses 
 * of the java.awt.TPaint class with paints, which the system does not support.
 * </p>
 *
 *<h3>Text Renderers</h3>
 *<p>
 *Text renderers draw strings and glyph vectors. All text renderers are subclasses 
 *of the org.apache.harmony.awt.gl.TextRenderer class.
 *</p>
 *
 */
public abstract class CommonGraphics2D extends TGraphics2D {
	
	private static final Map<TRenderingHints.Key, Object> DEFAULT_RENDERING_HINTS;

	static {
		final Map<TRenderingHints.Key, Object> m = new HashMap<TRenderingHints.Key, Object>();

		m.put(TRenderingHints.KEY_TEXT_ANTIALIASING,
				TRenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		m.put(TRenderingHints.KEY_ANTIALIASING,
				TRenderingHints.VALUE_ANTIALIAS_OFF);
		m.put(TRenderingHints.KEY_STROKE_CONTROL,
				TRenderingHints.VALUE_STROKE_DEFAULT);

		DEFAULT_RENDERING_HINTS = Collections.unmodifiableMap(m);
	}
	
    protected Surface dstSurf = null;
    protected Blitter blitter = NullBlitter.getInstance();
    protected TRenderingHints hints = new TRenderingHints(DEFAULT_RENDERING_HINTS);

    // Clipping things
    protected MultiRectArea clip = null;

    protected TPaint paint = TColor.WHITE;
    protected TColor fgTColor = TColor.WHITE;
    protected TColor bgTColor = TColor.BLACK;

    protected TComposite composite = TAlphaComposite.SrcOver;

    protected TStroke stroke = new TBasicStroke();

    //TODO: Think more about TFontRenderContext
    protected TFontRenderContext frc = null;

    protected JavaShapeRasterizer jsr = new JavaShapeRasterizer();

    protected TFont font = new TFont("Dialog", TFont.PLAIN, 12);; //$NON-NLS-1$

    protected TextRenderer jtr = 
        TFontManager.IS_FONTLIB ? 
                FLTextRenderer.getInstance() : 
                    JavaTextRenderer.inst;

    // Current graphics transform
    protected TAffineTransform transform = new TAffineTransform();
    protected double[] matrix = new double[6];

    // Original user->device translation as transform and point
    //public TAffineTransform origTransform = new TAffineTransform();
    public TPoint origTPoint = new TPoint(0, 0);


    // Print debug output or not
    protected static final boolean debugOutput = "1".equals(org.apache.harmony.awt.Utils.getSystemProperty("g2d.debug")); //$NON-NLS-1$ //$NON-NLS-2$

    // Constructors
    protected CommonGraphics2D() {
    }

    protected CommonGraphics2D(int tx, int ty) {
        this(tx, ty, null);
    }

    protected CommonGraphics2D(int tx, int ty, MultiRectArea clip) {
        setTransform(TAffineTransform.getTranslateInstance(tx, ty));
        //origTransform = TAffineTransform.getTranslateInstance(tx, ty);
        origTPoint = new TPoint(tx, ty);
        setClip(clip);
    }

    // Public methods
    @Override
    public void addRenderingHints(Map<?,?> hints) {
        this.hints.putAll(hints);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        TColor c = getColor();
        TPaint p = getPaint();
        setColor(getBackground());
        fillRect(x, y, width, height);
        setColor(c);
        setPaint(p);
        if (debugOutput) {
            System.err.println("CommonGraphics2D.clearRect("+x+", "+y+", "+width+", "+height+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        }
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        clip(new TRectangle(x, y, width, height));
    }


    @Override
    public void clip(TShape s) {
        if (s == null) {
            clip = null;
            return;
        }

        MultiRectArea mra = null;
        if (s instanceof MultiRectArea) {
            mra = new MultiRectArea((MultiRectArea)s);
            mra.translate((int)transform.getTranslateX(), (int)transform.getTranslateY());
        } else {
            int type = transform.getType();
            if(s instanceof TRectangle && (type == TAffineTransform.TYPE_IDENTITY ||
                type == TAffineTransform.TYPE_TRANSLATION )) {
                    mra = new MultiRectArea((TRectangle)s);
                    if(type == TAffineTransform.TYPE_TRANSLATION){
                        mra.translate((int)transform.getTranslateX(), (int)transform.getTranslateY());
                    }
            } else {
                s = transform.createTransformedShape(s);
                mra = jsr.rasterize(s, 0.5);
            }
        }

        if (clip == null) {
            setTransformedClip(mra);
        } else {
            clip.intersect(mra);
            setTransformedClip(clip);
        }
    }

    @Override
    public void dispose() {
        // Do nothing for Java only classes
    }




    /***************************************************************************
     *
     *  Draw methods
     *
     ***************************************************************************/

    @Override
    public void draw(TShape s) {
        if (stroke instanceof TBasicStroke && ((TBasicStroke)stroke).getLineWidth() <= 1) {
            //TODO: Think about drawing the shape in one fillMultiRectArea call
            TBasicStroke bstroke = (TBasicStroke)stroke;
            JavaLineRasterizer.LineDasher ld = (bstroke.getDashArray() == null)?null:new JavaLineRasterizer.LineDasher(bstroke.getDashArray(), bstroke.getDashPhase());
            TPathIterator pi = s.getPathIterator(transform, 0.5);
            float []points = new float[6];
            int x1 = Integer.MIN_VALUE;
            int y1 = Integer.MIN_VALUE;
            int cx1 = Integer.MIN_VALUE;
            int cy1 = Integer.MIN_VALUE;
            while (!pi.isDone()) {
                switch (pi.currentSegment(points)) {
                    case TPathIterator.SEG_MOVETO:
                        x1 = (int)Math.floor(points[0]);
                        y1 = (int)Math.floor(points[1]);
                        cx1 = x1;
                        cy1 = y1;
                        break;
                    case TPathIterator.SEG_LINETO:
                        int x2 = (int)Math.floor(points[0]);
                        int y2 = (int)Math.floor(points[1]);
                        fillMultiRectArea(JavaLineRasterizer.rasterize(x1, y1, x2, y2, null, ld, false));
                        x1 = x2;
                        y1 = y2;
                        break;
                    case TPathIterator.SEG_CLOSE:
                        x2 = cx1;
                        y2 = cy1;
                        fillMultiRectArea(JavaLineRasterizer.rasterize(x1, y1, x2, y2, null, ld, false));
                        x1 = x2;
                        y1 = y2;
                        break;
                }
                pi.next();
            }
        } else {
            s = stroke.createStrokedShape(s);
            s = transform.createTransformedShape(s);
            fillMultiRectArea(jsr.rasterize(s, 0.5));
        }
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int sa, int ea) {
        if (stroke instanceof TBasicStroke && ((TBasicStroke)stroke).getLineWidth() <= 1 &&
                ((TBasicStroke)stroke).getDashArray() == null && 
                (transform.isIdentity() || transform.getType() == TAffineTransform.TYPE_TRANSLATION)) {
            TPoint p = new TPoint(x, y);
            transform.transform(p, p);
            MultiRectArea mra = JavaArcRasterizer.rasterize(x, y, width, height, sa, ea, clip);
            fillMultiRectArea(mra);
            return;
        }
        draw(new TArc2D.TFloat(x, y, width, height, sa, ea, TArc2D.OPEN));
    }


    @Override
    public boolean drawImage(TImage image, int x, int y, TColor bgcolor,
            TImageObserver imageObserver) {

        if(image == null) {
            return true;
        }

        boolean done = false;
        boolean somebits = false;
        Surface srcSurf = null;
        if(image instanceof OffscreenImage){
            OffscreenImage oi = (OffscreenImage) image;
            if((oi.getState() & TImageObserver.ERROR) != 0) {
                return false;
            }
            done = oi.prepareImage(imageObserver);
            somebits = (oi.getState() & TImageObserver.SOMEBITS) != 0;
            srcSurf = oi.getImageSurface();
        }else{
            done = true;
            srcSurf = Surface.getImageSurface(image);
        }

        if(done || somebits) {
            int w = srcSurf.getWidth();
            int h = srcSurf.getHeight();
            blitter.blit(0, 0, srcSurf, x, y, dstSurf, w, h, (TAffineTransform) transform.clone(),
                    composite, bgcolor, clip);
        }
        return done;
    }

    @Override
    public boolean drawImage(TImage image, int x, int y, TImageObserver imageObserver) {
        return drawImage(image, x, y, null, imageObserver);
    }

    @Override
    public boolean drawImage(TImage image, int x, int y, int width, int height,
            TColor bgcolor, TImageObserver imageObserver) {

        if(image == null) {
            return true;
        }
        if(width == 0 || height == 0) {
            return true;
        }

        boolean done = false;
        boolean somebits = false;
        Surface srcSurf = null;

        if(image instanceof OffscreenImage){
            OffscreenImage oi = (OffscreenImage) image;
            if((oi.getState() & TImageObserver.ERROR) != 0) {
                return false;
            }
            done = oi.prepareImage(imageObserver);
            somebits = (oi.getState() & TImageObserver.SOMEBITS) != 0;
            srcSurf = oi.getImageSurface();
        }else{
            done = true;
            srcSurf = Surface.getImageSurface(image);
        }

        if(done || somebits) {
            int w = srcSurf.getWidth();
            int h = srcSurf.getHeight();
            if(w == width && h == height){
                blitter.blit(0, 0, srcSurf, x, y, dstSurf, w, h,
                        (TAffineTransform) transform.clone(),
                        composite, bgcolor, clip);
            }else{
                TAffineTransform xform = new TAffineTransform();
                xform.setToScale((float)width / w, (float)height / h);
                blitter.blit(0, 0, srcSurf, x, y, dstSurf, w, h,
                        (TAffineTransform) transform.clone(),
                        xform, composite, bgcolor, clip);
            }
        }
        return done;
    }

    @Override
    public boolean drawImage(TImage image, int x, int y, int width, int height,
            TImageObserver imageObserver) {
        return drawImage(image, x, y, width, height, null, imageObserver);
    }

    @Override
    public boolean drawImage(TImage image, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, TColor bgcolor,
            TImageObserver imageObserver) {

        if(image == null) {
            return true;
        }
        if(dx1 == dx2 || dy1 == dy2 || sx1 == sx2 || sy1 == sy2) {
            return true;
        }

        boolean done = false;
        boolean somebits = false;
        Surface srcSurf = null;
        if(image instanceof OffscreenImage){
            OffscreenImage oi = (OffscreenImage) image;
            if((oi.getState() & TImageObserver.ERROR) != 0) {
                return false;
            }
            done = oi.prepareImage(imageObserver);
            somebits = (oi.getState() & TImageObserver.SOMEBITS) != 0;
            srcSurf = oi.getImageSurface();
        }else{
            done = true;
            srcSurf = Surface.getImageSurface(image);
        }

        if(done || somebits) {

            int dstX = dx1;
            int dstY = dy1;
            int srcX = sx1;
            int srcY = sy1;

            int dstW = dx2 - dx1;
            int dstH = dy2 - dy1;
            int srcW = sx2 - sx1;
            int srcH = sy2 - sy1;

            if(srcW == dstW && srcH == dstH){
                blitter.blit(srcX, srcY, srcSurf, dstX, dstY, dstSurf, srcW, srcH,
                        (TAffineTransform) transform.clone(),
                        composite, bgcolor, clip);
            }else{
                TAffineTransform xform = new TAffineTransform();
                xform.setToScale((float)dstW / srcW, (float)dstH / srcH);
                blitter.blit(srcX, srcY, srcSurf, dstX, dstY, dstSurf, srcW, srcH,
                        (TAffineTransform) transform.clone(),
                        xform, composite, bgcolor, clip);
            }
        }
        return done;
    }

    @Override
    public boolean drawImage(TImage image, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, TImageObserver imageObserver) {

        return drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null,
                imageObserver);
     }

    @Override
    public void drawImage(BufferedTImage bufImage, TBufferedImageOp op,
            int x, int y) {

        if(bufImage == null) {
            return;
        }

        if(op == null) {
            drawImage(bufImage, x, y, null);
        } else if(op instanceof TAffineTransformOp){
            TAffineTransformOp atop = (TAffineTransformOp) op;
            TAffineTransform xform = atop.getTransform();
            Surface srcSurf = Surface.getImageSurface(bufImage);
            int w = srcSurf.getWidth();
            int h = srcSurf.getHeight();
            blitter.blit(0, 0, srcSurf, x, y, dstSurf, w, h,
                    (TAffineTransform) transform.clone(), xform,
                    composite, null, clip);
        } else {
            bufImage = op.filter(bufImage, null);
            Surface srcSurf = Surface.getImageSurface(bufImage);
            int w = srcSurf.getWidth();
            int h = srcSurf.getHeight();
            blitter.blit(0, 0, srcSurf, x, y, dstSurf, w, h,
                    (TAffineTransform) transform.clone(),
                    composite, null, clip);
        }
    }

    @Override
    public boolean drawImage(TImage image, TAffineTransform trans,
            TImageObserver imageObserver) {

        if(image == null) {
            return true;
        }
        if(trans == null || trans.isIdentity()) {
            return drawImage(image, 0, 0, imageObserver);
        }

        boolean done = false;
        boolean somebits = false;
        Surface srcSurf = null;
        if(image instanceof OffscreenImage){
            OffscreenImage oi = (OffscreenImage) image;
            if((oi.getState() & TImageObserver.ERROR) != 0) {
                return false;
            }
            done = oi.prepareImage(imageObserver);
            somebits = (oi.getState() & TImageObserver.SOMEBITS) != 0;
            srcSurf = oi.getImageSurface();
        }else{
            done = true;
            srcSurf = Surface.getImageSurface(image);
        }

        if(done || somebits) {
            int w = srcSurf.getWidth();
            int h = srcSurf.getHeight();
            TAffineTransform xform = (TAffineTransform) transform.clone();
            xform.concatenate(trans);
            blitter.blit(0, 0, srcSurf, 0, 0, dstSurf, w, h, xform, composite,
                    null, clip);
        }
        return done;
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        if (debugOutput) {
            System.err.println("CommonGraphics2D.drawLine("+x1+", "+y1+", "+x2+", "+y2+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        }

        if (stroke instanceof TBasicStroke && ((TBasicStroke)stroke).getLineWidth() <= 1) {
            TBasicStroke bstroke = (TBasicStroke)stroke;
            TPoint p1 = new TPoint(x1, y1);
            TPoint p2 = new TPoint(x2, y2);
            transform.transform(p1, p1);
            transform.transform(p2, p2);
            JavaLineRasterizer.LineDasher ld = (bstroke.getDashArray() == null)?null:new JavaLineRasterizer.LineDasher(bstroke.getDashArray(), bstroke.getDashPhase());
            MultiRectArea mra = JavaLineRasterizer.rasterize(p1.x, p1.y, p2.x, p2.y, null, ld, false);
            fillMultiRectArea(mra);
            return;
        }
        draw(new TLine2D.TFloat(x1, y1, x2, y2));
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        if (stroke instanceof TBasicStroke && ((TBasicStroke)stroke).getLineWidth() <= 1 &&
                ((TBasicStroke)stroke).getDashArray() == null && 
                (transform.isIdentity() || transform.getType() == TAffineTransform.TYPE_TRANSLATION)) {
            TPoint p = new TPoint(x, y);
            transform.transform(p, p);
            MultiRectArea mra = JavaArcRasterizer.rasterize(p.x, p.y, width, height, 0, 360, clip);
            fillMultiRectArea(mra);
            return;
        }
        draw(new TEllipse2D.TFloat(x, y, width, height));
    }

    @Override
    public void drawPolygon(int[] xpoints, int[] ypoints, int npoints) {
        draw(new TPolygon(xpoints, ypoints, npoints));
    }

    @Override
    public void drawPolygon(TPolygon polygon) {
        draw(polygon);
    }

    @Override
    public void drawPolyline(int[] xpoints, int[] ypoints, int npoints) {
        for (int i = 0; i < npoints-1; i++) {
            drawLine(xpoints[i], ypoints[i], xpoints[i+1], ypoints[i+1]);
        }
    }

    @Override
    public void drawRenderableImage(RenderableTImage img, TAffineTransform xform) {
        if (img == null) {
            return;
        }

        double scaleX = xform.getScaleX();
        double scaleY = xform.getScaleY();
        if (scaleX == 1 && scaleY == 1) {
            drawRenderedImage(img.createDefaultRendering(), xform);
        } else {
            int width = (int)Math.round(img.getWidth()*scaleX);
            int height = (int)Math.round(img.getHeight()*scaleY);
            xform = (TAffineTransform)xform.clone();
            xform.scale(1, 1);
            drawRenderedImage(img.createScaledRendering(width, height, null), xform);
        }
    }

    @Override
    public void drawRenderedImage(RenderedTImage rimg, TAffineTransform xform) {
        if (rimg == null) {
            return;
        }

        TImage img = null;

        if (rimg instanceof TImage) {
            img = (TImage)rimg;
        } else {
            //TODO: Create new class to provide TImage interface for RenderedTImage or rewrite this method
            img = new TBufferedImage(rimg.getColorModel(), rimg.copyData(null), false, null);
        }

        drawImage(img, xform, null);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (debugOutput) {
            System.err.println("CommonGraphics2D.drawRoundRect("+x+", "+y+", "+width+", "+height+","+arcWidth+", "+arcHeight+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        }

        draw(new TRoundRectangle2D.TFloat(x, y, width, height, arcWidth, arcHeight));
    }





    /***************************************************************************
     *
     *  String methods
     *
     ***************************************************************************/

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        TGlyphVector gv = font.createGlyphVector(frc, iterator);
        drawGlyphVector(gv, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float)x, (float)y);
    }

    @Override
    public void drawString(String str, int x, int y) {
        drawString(str, (float)x, (float)y);
    }


    /***************************************************************************
     *
     *  Fill methods
     *
     ***************************************************************************/

    @Override
    public void fill(TShape s) {
        s = transform.createTransformedShape(s);
        MultiRectArea mra = jsr.rasterize(s, 0.5);
        fillMultiRectArea(mra);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int sa, int ea) {
        fill(new TArc2D.TFloat(x, y, width, height, sa, ea, TArc2D.PIE));
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        fill(new TEllipse2D.TFloat(x, y, width, height));
    }

    @Override
    public void fillPolygon(int[] xpoints, int[] ypoints, int npoints) {
        fill(new TPolygon(xpoints, ypoints, npoints));
    }

    @Override
    public void fillPolygon(TPolygon polygon) {
        fill(polygon);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        if (debugOutput) {
            System.err.println("CommonGraphics2D.fillRect("+x+", "+y+", "+width+", "+height+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        }

        fill(new TRectangle(x, y, width, height));
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (debugOutput) {
            System.err.println("CommonGraphics2D.fillRoundRect("+x+", "+y+", "+width+", "+height+","+arcWidth+", "+arcHeight+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        }

        fill(new TRoundRectangle2D.TFloat(x, y, width, height, arcWidth, arcHeight));
    }




    /***************************************************************************
     *
     *  Get methods
     *
     ***************************************************************************/

    @Override
    public TColor getBackground() {
        return bgTColor;
    }

    @Override
    public TShape getClip() {
        if (clip == null) {
            return null;
        }

        MultiRectArea res = new MultiRectArea(clip);
        res.translate(-Math.round((float)transform.getTranslateX()), -Math.round((float)transform.getTranslateY()));
        return res;
    }

    @Override
    public TRectangle getClipBounds() {
        if (clip == null) {
            return null;
        }

        TRectangle res = (TRectangle) clip.getBounds().clone();
        res.translate(-Math.round((float)transform.getTranslateX()), -Math.round((float)transform.getTranslateY()));
        return res;
    }

    @Override
    public TColor getColor() {
        return fgTColor;
    }

    @Override
    public TComposite getComposite() {
        return composite;
    }

    @Override
    public TFont getFont() {
        return font;
    }

    @SuppressWarnings("deprecation")
    @Override
    public TFontMetrics getFontMetrics(TFont font) {
        return TToolkit.getDefaultToolkit().getFontMetrics(font);
    }

    @Override
    public TFontRenderContext getFontRenderContext() {
        TAffineTransform at;
        if (frc == null){
            TGraphicsConfiguration gc = getDeviceConfiguration();
            if (gc != null){
                at = gc.getDefaultTransform();
                at.concatenate(gc.getNormalizingTransform());
            }
            else 
                at = null;

            boolean isAa = (hints.get(TRenderingHints.KEY_TEXT_ANTIALIASING) == 
                TRenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            boolean isFm = (hints.get(TRenderingHints.KEY_FRACTIONALMETRICS) == 
                TRenderingHints.VALUE_FRACTIONALMETRICS_ON);
            frc = new TFontRenderContext(at,isAa,isFm);
        }
        return frc;
    }

    @Override
    public TPaint getPaint() {
        return paint;
    }

    @Override
    public Object getRenderingHint(TRenderingHints.Key key) {
        return hints.get(key);
    }

    @Override
    public TRenderingHints getRenderingHints() {
        return hints;
    }

    @Override
    public TStroke getStroke() {
        return stroke;
    }

    @Override
    public TAffineTransform getTransform() {
        return (TAffineTransform)transform.clone();
    }

    @Override
    public boolean hit(TRectangle rect, TShape s, boolean onTStroke) {
        //TODO: Implement method....
        return false;
    }




    /***************************************************************************
     *
     *  Transformation methods
     *
     ***************************************************************************/

    @Override
    public void rotate(double theta) {
        transform.rotate(theta);
        transform.getMatrix(matrix);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        transform.rotate(theta, x, y);
        transform.getMatrix(matrix);
    }

    @Override
    public void scale(double sx, double sy) {
        transform.scale(sx, sy);
        transform.getMatrix(matrix);
    }

    @Override
    public void shear(double shx, double shy) {
        transform.shear(shx, shy);
        transform.getMatrix(matrix);
    }

    @Override
    public void transform(TAffineTransform at) {
        transform.concatenate(at);
        transform.getMatrix(matrix);
    }

    @Override
    public void translate(double tx, double ty) {
        if (debugOutput) {
            System.err.println("CommonGraphics2D.translate("+tx+", "+ty+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        transform.translate(tx, ty);
        transform.getMatrix(matrix);
    }

    @Override
    public void translate(int tx, int ty) {
        if (debugOutput) {
            System.err.println("CommonGraphics2D.translate("+tx+", "+ty+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        transform.translate(tx, ty);
        transform.getMatrix(matrix);
    }




    /***************************************************************************
     *
     *  Set methods
     *
     ***************************************************************************/

    @Override
    public void setBackground(TColor color) {
        bgTColor = color;
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        setClip(new TRectangle(x, y, width, height));
    }

    @Override
    public void setClip(TShape s) {
        if (s == null) {
            setTransformedClip(null);
            if (debugOutput) {
                System.err.println("CommonGraphics2D.setClip(null)"); //$NON-NLS-1$
            }
            return;
        }

        if (debugOutput) {
            System.err.println("CommonGraphics2D.setClip("+s.getBounds()+")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (s instanceof MultiRectArea) {
            MultiRectArea nclip = new MultiRectArea((MultiRectArea)s);
            nclip.translate(Math.round((float)transform.getTranslateX()), Math.round((float)transform.getTranslateY()));
            setTransformedClip(nclip);
        } else {
            int type = transform.getType();
            if(s instanceof TRectangle && (type == TAffineTransform.TYPE_IDENTITY ||
                type == TAffineTransform.TYPE_TRANSLATION )) {
                    MultiRectArea nclip = new MultiRectArea((TRectangle)s);
                    if(type == TAffineTransform.TYPE_TRANSLATION){
                        nclip.translate((int)transform.getTranslateX(), (int)transform.getTranslateY());
                    }
                    setTransformedClip(nclip);
            } else {
                s = transform.createTransformedShape(s);
                setTransformedClip(jsr.rasterize(s, 0.5));
            }
        }
    }

    @Override
    public void setColor(TColor color) {
        if (color != null) {
            fgTColor = color;
            paint = color;
        }
    }

    @Override
    public void setComposite(TComposite composite) {
        this.composite = composite;
    }

    @Override
    public void setFont(TFont font) {
        this.font = font;
    }

    @Override
    public void setPaint(TPaint paint) {
        if (paint == null)
            return;
            
        this.paint = paint;
        if (paint instanceof TColor) {
            fgTColor = (TColor)paint;
        }
    }

    @Override
    public void setPaintMode() {
        composite = TAlphaComposite.SrcOver;
    }

    @Override
    public void setRenderingHint(TRenderingHints.Key key, Object value) {
        hints.put(key, value);
    }

    @Override
    public void setRenderingHints(Map<?,?> hints) {
        this.hints.clear();
        this.hints.putAll(DEFAULT_RENDERING_HINTS);
        this.hints.putAll(hints);
    }

    @Override
    public void setStroke(TStroke stroke) {
        this.stroke = stroke;
    }

    @Override
    public void setTransform(TAffineTransform transform) {
        this.transform = transform;

        transform.getMatrix(matrix);
    }

    @Override
    public void setXORMode(TColor color) {
        composite = new XORComposite(color);
    }


    // Protected methods
    protected void setTransformedClip(MultiRectArea clip) {
        this.clip = clip;
    }

    /**
     * This method fills the given MultiRectArea with current paint.
     * It calls fillMultiRectAreaTColor and fillMultiRectAreaTPaint 
     * methods depending on the type of current paint.
     * @param mra MultiRectArea to fill
     */
    protected void fillMultiRectArea(MultiRectArea mra) {
        if (clip != null) {
            mra.intersect(clip);
        }

        // Return if all stuff is clipped
        if (mra.rect[0] < 5) {
            return;
        }

        if (debugOutput) {
            System.err.println("CommonGraphics2D.fillMultiRectArea("+mra+")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (paint instanceof TColor){
            fillMultiRectAreaTColor(mra);
        }else{
            fillMultiRectAreaTPaint(mra);
        }
    }

    /**
     * This method fills the given MultiRectArea with solid color.
     * @param mra MultiRectArea to fill
     */
    protected void fillMultiRectAreaTColor(MultiRectArea mra) {
        fillMultiRectAreaTPaint(mra);
    }

    /**
     * This method fills the given MultiRectArea with any paint.
     * @param mra MultiRectArea to fill
     */
    protected void fillMultiRectAreaTPaint(MultiRectArea mra) {
        TRectangle rec = mra.getBounds();
        int x = rec.x;
        int y = rec.y;
        int w = rec.width;
        int h = rec.height;
        if(w <= 0 || h <= 0) {
            return;
        }
        TPaintContext pc = paint.createContext(null, rec, rec, transform, hints);
        TRaster r = pc.getRaster(x, y, w, h);
        TWritableRaster wr;
        if(r instanceof TWritableRaster){
            wr = (TWritableRaster) r;
        }else{
            wr = r.createCompatibleWritableRaster();
            wr.setRect(r);
        }
        Surface srcSurf = new ImageSurface(pc.getColorModel(), wr);
        blitter.blit(0, 0, srcSurf, x, y, dstSurf, w, h,
                composite, null, mra);
        srcSurf.dispose();
    }

    /**
     * Copies graphics class fields. 
     * Used in create method
     * 
     * @param copy Graphics class to copy
     */
    protected void copyInternalFields(CommonGraphics2D copy) {
        if (clip == null) {
            copy.setTransformedClip(null);
        } else {
            copy.setTransformedClip(new MultiRectArea(clip));
        }
        copy.setBackground(bgTColor);
        copy.setColor(fgTColor);
        copy.setPaint(paint);
        copy.setComposite(composite);
        copy.setStroke(stroke);
        copy.setFont(font);
        copy.setTransform(new TAffineTransform(transform));
        //copy.origTransform = new TAffineTransform(origTransform);
        copy.origTPoint = new TPoint(origTPoint);
    }

    public void flush(){}
}
