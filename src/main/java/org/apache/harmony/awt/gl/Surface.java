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
 * @author Igor V. Stolyarov
 * Created on 10.11.2005
 *
 */
package org.apache.harmony.awt.gl;

import java.util.ArrayList;

import org.teavm.classlib.java.awt.TRectangle;
import org.teavm.classlib.java.awt.TTransparency;
import org.teavm.classlib.java.awt.color.TColorSpace;
import org.apache.harmony.awt.gl.color.LUTColorConverter;
import org.teavm.classlib.java.awt.TImage;
import org.teavm.classlib.java.awt.image.TBufferedImage;
import org.teavm.classlib.java.awt.image.TColorModel;
import org.teavm.classlib.java.awt.image.TComponentColorModel;
import org.teavm.classlib.java.awt.image.TComponentSampleModel;
import org.teavm.classlib.java.awt.image.TDataBuffer;
import org.teavm.classlib.java.awt.image.TDirectColorModel;
import org.teavm.classlib.java.awt.image.TIndexColorModel;
import org.teavm.classlib.java.awt.image.TMultiPixelPackedSampleModel;
import org.teavm.classlib.java.awt.image.TSampleModel;
import org.teavm.classlib.java.awt.image.TWritableRaster;


/**
 * This class is super class for others types of Surfaces. 
 * Surface is storing data and data format description, that are using
 * in blitting operations    
 */
public abstract class Surface implements TTransparency {

    // Color Space Types
    public static final int sRGB_CS = 1;
    public static final int Linear_RGB_CS = 2;
    public static final int Linear_Gray_CS = 3;
    public static final int Custom_CS = 0;
    
    // Color Model Types
    public static final int DCM = 1;  // Direct Color Model
    public static final int ICM = 2;  // Index Color Model
    public static final int CCM = 3;  // Component Color Model

    // Sample Model Types
    public static final int SPPSM = 1;  // Single Pixel Packed Sample Model
    public static final int MPPSM = 2;  // Multi Pixel Packed Sample Model
    public static final int CSM   = 3;  // Component Sample Model
    public static final int PISM  = 4;  // Pixel Interleaved Sample Model
    public static final int BSM   = 5;  // Banded Sample Model

    // Surface Types
    private static final int ALPHA_MASK = 0xff000000;
    private static final int RED_MASK = 0x00ff0000;
    private static final int GREEN_MASK = 0x0000ff00;
    private static final int BLUE_MASK = 0x000000ff;
    private static final int RED_BGR_MASK = 0x000000ff;
    private static final int GREEN_BGR_MASK = 0x0000ff00;
    private static final int BLUE_BGR_MASK = 0x00ff0000;
    private static final int RED_565_MASK = 0xf800;
    private static final int GREEN_565_MASK = 0x07e0;
    private static final int BLUE_565_MASK = 0x001f;
    private static final int RED_555_MASK = 0x7c00;
    private static final int GREEN_555_MASK = 0x03e0;
    private static final int BLUE_555_MASK = 0x001f;

    static{
        org.apache.harmony.awt.Utils.loadLibrary("gl"); //$NON-NLS-1$
        initIDs();
    }


    protected long surfaceDataPtr;        // Pointer for Native Surface data
    protected int transparency = OPAQUE;
    protected int width;
    protected int height;
    
    protected MultiRectArea dirtyRegions;

    /**
     * This list contains caches with the data of this surface that are valid at the moment.
     * Surface should clear this list when its data is updated.
     * Caches may check if they are still valid using isCacheValid method.
     * When cache gets data from the surface, it should call addValidCache method of the surface.
     */
    private final ArrayList<Object> validCaches = new ArrayList<Object>();

    public abstract TColorModel getTColorModel();
    public abstract TWritableRaster getRaster();
    public abstract int getSurfaceType(); // Syrface type. It is equal 
                                          // BufferedImge type
    /**
     * Lock Native Surface data
     */
    public abstract long lock();     
    
    /**
     * Unlock Native Surface data 
     */
    public abstract void unlock();
    
    /**
     * Dispose Native Surface data
     */
    public abstract void dispose();
    public abstract Surface getImageSurface();

    public long getSurfaceDataPtr(){
        return surfaceDataPtr;
    }

    public final boolean isCaheValid(Object cache) {
        return validCaches.contains(cache);
    }

    public final void addValidCache(Object cache) {
        validCaches.add(cache);
    }

    protected final void clearValidCaches() {
        validCaches.clear();
    }

    /**
     * Returns could or coldn't the Surface be blit by Native blitter 
     * @return - true if the Surface could be blit by Native blitter, 
     *           false in other case
     */
    public boolean isNativeDrawable(){
        return true;
    }

    public int getTransparency() {
        return transparency;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }
    
    /**
     * If Surface has Raster, this method returns data array of Raster's TDataBuffer
     * @return - data array
     */
    public Object getData(){
        return null;
    }
    
    public boolean invalidated(){
        return true;
    }
    
    public void validate(){}
    
    public void invalidate(){}
    
    public void addDirtyRegion(TRectangle r){
        if (dirtyRegions == null) {
            dirtyRegions = new MultiRectArea(r);
        } else {
            TRectangle rects[] = dirtyRegions.getRectangles();
            if (rects.length == 1){
                if (rects[0].contains(r)) return;
            }
            dirtyRegions.add(r);
        }
        invalidate();
    }
    
    public void releaseDurtyRegions(){
        dirtyRegions = null;
    }
    
    public int[] getDirtyRegions(){
        return (dirtyRegions == null ? null : dirtyRegions.rect);
    }

    /**
     * Computation type of TBufferedImage or Surface
     * @param cm - TColorModel
     * @param raster - WritableRaste
     * @return - type of TBufferedImage
     */
    public static int getType(TColorModel cm, TWritableRaster raster){
        int transferType = cm.getTransferType();
        boolean hasAlpha = cm.hasAlpha();
        TColorSpace cs = cm.getColorSpace();
        int csType = cs.getType();
        TSampleModel sm = raster.getSampleModel();

        if(csType == TColorSpace.TYPE_RGB){
            if(cm instanceof TDirectColorModel){
                TDirectColorModel dcm = (TDirectColorModel) cm;
                switch (transferType) {
                case TDataBuffer.TYPE_INT:
                    if (dcm.getRedMask() == RED_MASK &&
                            dcm.getGreenMask() == GREEN_MASK &&
                            dcm.getBlueMask() == BLUE_MASK) {
                        if (!hasAlpha) {
                            return TBufferedImage.TYPE_INT_RGB;
                        }
                        if (dcm.getAlphaMask() == ALPHA_MASK) {
                            if (dcm.isAlphaPremultiplied()) {
                                return TBufferedImage.TYPE_INT_ARGB_PRE;
                            }
                            return TBufferedImage.TYPE_INT_ARGB;
                        }
                        return TBufferedImage.TYPE_CUSTOM;
                    } else if (dcm.getRedMask() == RED_BGR_MASK &&
                            dcm.getGreenMask() == GREEN_BGR_MASK &&
                            dcm.getBlueMask() == BLUE_BGR_MASK) {
                        if (!hasAlpha) {
                            return TBufferedImage.TYPE_INT_BGR;
                        }
                    } else {
                        return TBufferedImage.TYPE_CUSTOM;
                    }
                case TDataBuffer.TYPE_USHORT:
                    if (dcm.getRedMask() == RED_555_MASK &&
                            dcm.getGreenMask() == GREEN_555_MASK &&
                            dcm.getBlueMask() == BLUE_555_MASK && !hasAlpha) {
                        return TBufferedImage.TYPE_USHORT_555_RGB;
                    } else if (dcm.getRedMask() == RED_565_MASK &&
                            dcm.getGreenMask() == GREEN_565_MASK &&
                            dcm.getBlueMask() == BLUE_565_MASK) {
                        return TBufferedImage.TYPE_USHORT_565_RGB;
                    }
                default:
                    return TBufferedImage.TYPE_CUSTOM;
                }
            }else if(cm instanceof TIndexColorModel){
                TIndexColorModel icm = (TIndexColorModel) cm;
                int pixelBits = icm.getPixelSize();
                if(transferType == TDataBuffer.TYPE_BYTE){
                    if(sm instanceof TMultiPixelPackedSampleModel && !hasAlpha &&
                        pixelBits < 5){
                            return TBufferedImage.TYPE_BYTE_BINARY;
                    }else if(pixelBits == 8){
                        return TBufferedImage.TYPE_BYTE_INDEXED;
                    }
                }
                return TBufferedImage.TYPE_CUSTOM;
            }else if(cm instanceof TComponentColorModel){
                TComponentColorModel ccm = (TComponentColorModel) cm;
                if(transferType == TDataBuffer.TYPE_BYTE &&
                        sm instanceof TComponentSampleModel){
                    TComponentSampleModel csm =
                        (TComponentSampleModel) sm;
                    int[] offsets = csm.getBandOffsets();
                    int[] bits = ccm.getComponentSize();
                    boolean isCustom = false;
                    for (int i = 0; i < bits.length; i++) {
                        if (bits[i] != 8 ||
                               offsets[i] != offsets.length - 1 - i) {
                            isCustom = true;
                            break;
                        }
                    }
                    if (!isCustom) {
                        if (!ccm.hasAlpha()) {
                            return TBufferedImage.TYPE_3BYTE_BGR;
                        } else if (ccm.isAlphaPremultiplied()) {
                            return TBufferedImage.TYPE_4BYTE_ABGR_PRE;
                        } else {
                            return TBufferedImage.TYPE_4BYTE_ABGR;
                        }
                    }
                }
                return TBufferedImage.TYPE_CUSTOM;
            }
            return TBufferedImage.TYPE_CUSTOM;
        }else if(cs == LUTColorConverter.LINEAR_GRAY_CS){
            if(cm instanceof TComponentColorModel &&
                    cm.getNumComponents() == 1){
                int bits[] = cm.getComponentSize();
                if(transferType == TDataBuffer.TYPE_BYTE &&
                        bits[0] == 8){
                    return TBufferedImage.TYPE_BYTE_GRAY;
                }else if(transferType == TDataBuffer.TYPE_USHORT &&
                        bits[0] == 16){
                    return TBufferedImage.TYPE_USHORT_GRAY;
                }else{
                    return TBufferedImage.TYPE_CUSTOM;
                }
            }
            return TBufferedImage.TYPE_CUSTOM;
        }
        return TBufferedImage.TYPE_CUSTOM;
    }

    public static Surface getImageSurface(TImage image){
        return AwtImageBackdoorAccessor.getInstance().getImageSurface(image);
    }

    @Override
    protected void finalize() throws Throwable{
        dispose();
    }

    public static boolean isGrayPallete(TIndexColorModel icm){
        return AwtImageBackdoorAccessor.getInstance().isGrayPallete(icm);
    }

    /**
     * Initialization of Native data
     * 
     */
    private static native void initIDs();
}
