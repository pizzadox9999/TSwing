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

import org.apache.harmony.awt.gl.color.LUTColorConverter;
import org.apache.harmony.awt.gl.image.DataBufferListener;
import org.apache.harmony.awt.internal.nls.Messages;
import org.teavm.classlib.java.awt.TRectangle;
import org.teavm.classlib.java.awt.color.TColorSpace;
import org.teavm.classlib.java.awt.image.TBandedSampleModel;
import org.teavm.classlib.java.awt.image.TBufferedImage;
import org.teavm.classlib.java.awt.image.TColorModel;
import org.teavm.classlib.java.awt.image.TComponentColorModel;
import org.teavm.classlib.java.awt.image.TComponentSampleModel;
import org.teavm.classlib.java.awt.image.TDataBuffer;
import org.teavm.classlib.java.awt.image.TDirectColorModel;
import org.teavm.classlib.java.awt.image.TIndexColorModel;
import org.teavm.classlib.java.awt.image.TMultiPixelPackedSampleModel;
import org.teavm.classlib.java.awt.image.TPixelInterleavedSampleModel;
import org.teavm.classlib.java.awt.image.TSampleModel;
import org.teavm.classlib.java.awt.image.TSinglePixelPackedSampleModel;
import org.teavm.classlib.java.awt.image.TWritableRaster;


/**
 * This class represent Surface for different types of Images (BufferedImage, 
 * OffscreenImage and so on) 
 */
public class ImageSurface extends Surface implements DataBufferListener {

    boolean nativeDrawable = true;
    int surfaceType;
    int csType;
    TColorModel cm;
    TWritableRaster raster;
    Object data;
    
    boolean needToRefresh = true;
    boolean dataTaken = false;
    
    private long cachedDataPtr;       // Pointer for cached Image Data
    private boolean alphaPre;         // Cached Image Data alpha premultiplied 
    
    AwtImageBackdoorAccessor ba = AwtImageBackdoorAccessor.getInstance();

    public ImageSurface(TColorModel cm, TWritableRaster raster){
        this(cm, raster, Surface.getType(cm, raster));
    }

    public ImageSurface(TColorModel cm, TWritableRaster raster, int type){
        if (!cm.isCompatibleRaster(raster)) {
            // awt.4D=The raster is incompatible with this TColorModel
            throw new IllegalArgumentException(Messages.getString("awt.4D")); //$NON-NLS-1$
        }
        this.cm = cm;
        this.raster = raster;
        surfaceType = type;

        TDataBuffer db = raster.getDataBuffer();
        data = ba.getData(db);
        ba.addTDataBufferListener(db, this);
        TColorSpace cs = cm.getColorSpace();
        transparency = cm.getTransparency();
        width = raster.getWidth();
        height = raster.getHeight();
        addDirtyRegion(new TRectangle(0, 0, width, height));

        // For the moment we can build natively only images which have 
        // sRGB, Linear_RGB, Linear_Gray Color Space and type different
        // from BufferedImage.TYPE_CUSTOM
        if(cs == LUTColorConverter.sRGB_CS){
            csType = sRGB_CS;
        }else if(cs == LUTColorConverter.LINEAR_RGB_CS){
            csType = Linear_RGB_CS;
        }else if(cs == LUTColorConverter.LINEAR_GRAY_CS){
            csType = Linear_Gray_CS;
        }else{
            csType = Custom_CS;
            nativeDrawable = false;
        }

        if(type == TBufferedImage.TYPE_CUSTOM){
            nativeDrawable = false;
        }
        
    }

    @Override
    public TColorModel getTColorModel() {
        return cm;
    }

    @Override
    public TWritableRaster getRaster() {
        return raster;
    }

    @Override
    public long getSurfaceDataPtr() {
        if(surfaceDataPtr == 0L && nativeDrawable){
            createSufaceStructure();
        }
        return surfaceDataPtr;
    }

    @Override
    public Object getData(){
        return data;
    }

    @Override
    public boolean isNativeDrawable(){
        return nativeDrawable;
    }

    @Override
    public int getSurfaceType() {
        return surfaceType;
    }

    /**
     * Creates native Surface structure which used for native blitting
     */
    private void createSufaceStructure(){
        int cmType = 0;
        int numComponents = cm.getNumComponents();
        boolean hasAlpha = cm.hasAlpha();
        boolean isAlphaPre = cm.isAlphaPremultiplied();
        int transparency = cm.getTransparency();
        int bits[] = cm.getComponentSize();
        int pixelStride = cm.getPixelSize();
        int masks[] = null;
        int colorMap[] = null;
        int colorMapSize = 0;
        int transpPixel = -1;
        boolean isGrayPallete = false;
        TSampleModel sm = raster.getSampleModel();
        int smType = 0;
        int dataType = sm.getDataType();
        int scanlineStride = 0;
        int bankIndeces[] = null;
        int bandOffsets[] = null;
        int offset = raster.getDataBuffer().getOffset();

        if(cm instanceof TDirectColorModel){
            cmType = DCM;
            TDirectColorModel dcm = (TDirectColorModel) cm;
            masks = dcm.getMasks();
            smType = SPPSM;
            TSinglePixelPackedSampleModel sppsm = (TSinglePixelPackedSampleModel) sm;
            scanlineStride = sppsm.getScanlineStride();

        }else if(cm instanceof TIndexColorModel){
            cmType = ICM;
            TIndexColorModel icm = (TIndexColorModel) cm;
            colorMapSize = icm.getMapSize();
            colorMap = new int[colorMapSize];
            icm.getRGBs(colorMap);
            transpPixel = icm.getTransparentPixel();
            isGrayPallete = Surface.isGrayPallete(icm);

            if(sm instanceof TMultiPixelPackedSampleModel){
                smType = MPPSM;
                TMultiPixelPackedSampleModel mppsm =
                    (TMultiPixelPackedSampleModel) sm;
                scanlineStride = mppsm.getScanlineStride();
            }else if(sm instanceof TComponentSampleModel){
                smType = CSM;
                TComponentSampleModel csm =
                    (TComponentSampleModel) sm;
                scanlineStride = csm.getScanlineStride();
            }else{
                // awt.4D=The raster is incompatible with this TColorModel
                throw new IllegalArgumentException(Messages.getString("awt.4D")); //$NON-NLS-1$
            }

        }else if(cm instanceof TComponentColorModel){
            cmType = CCM;
            if(sm instanceof TComponentSampleModel){
                TComponentSampleModel csm = (TComponentSampleModel) sm;
                scanlineStride = csm.getScanlineStride();
                bankIndeces = csm.getBankIndices();
                bandOffsets = csm.getBandOffsets();
                if(sm instanceof TPixelInterleavedSampleModel){
                    smType = PISM;
                }else if(sm instanceof TBandedSampleModel){
                    smType = BSM;
                }else{
                    smType = CSM;
                }
            }else{
                // awt.4D=The raster is incompatible with this TColorModel
                throw new IllegalArgumentException(Messages.getString("awt.4D")); //$NON-NLS-1$
            }

        }else{
            surfaceDataPtr = 0L;
            return;
        }
        surfaceDataPtr = createSurfStruct(surfaceType, width, height, cmType, csType, smType, dataType,
                numComponents, pixelStride, scanlineStride, bits, masks, colorMapSize,
                colorMap, transpPixel, isGrayPallete, bankIndeces, bandOffsets,
                offset, hasAlpha, isAlphaPre, transparency);
    }

    @Override
    public synchronized void dispose() {
        ba.removeTDataBufferListener(raster.getDataBuffer());
        if(surfaceDataPtr != 0L){
            dispose(surfaceDataPtr);
            surfaceDataPtr = 0L;
        }
    }
    
    public long getCachedData(boolean alphaPre){
        if(nativeDrawable){
            if(cachedDataPtr == 0L || needToRefresh || this.alphaPre != alphaPre){
                cachedDataPtr = updateCache(getSurfaceDataPtr(), data, alphaPre);
                this.alphaPre = alphaPre;
                validate(); 
            }
        }
        return cachedDataPtr;
    }

    private native long createSurfStruct(int surfaceType, int width, int height, 
            int cmType, int csType, int smType, int dataType,
            int numComponents, int pixelStride, int scanlineStride,
            int bits[], int masks[], int colorMapSize, int colorMap[],
            int transpPixel, boolean isGrayPalette, int bankIndeces[], 
            int bandOffsets[], int offset, boolean hasAlpha, boolean isAlphaPre,
            int transparency);

    private native void dispose(long structPtr);

    private native void setImageSize(long structPtr, int width, int height);

    private native long updateCache(long structPtr, Object data, boolean alphaPre);
    
    /**
     * Supposes that new raster is compatible with an old one
     * @param r
     */
    public void setRaster(TWritableRaster r) {
        raster = r;
        TDataBuffer db = r.getDataBuffer();
        data = ba.getData(db);
        ba.addTDataBufferListener(db, this);
        if (surfaceDataPtr != 0) {
            setImageSize(surfaceDataPtr, r.getWidth(), r.getHeight());
        }
        this.width = r.getWidth();
        this.height = r.getHeight();
    }

    @Override
    public long lock() {
        // TODO
        return 0;
    }

    @Override
    public void unlock() {
        //TODO
    }

    @Override
    public Surface getImageSurface() {
        return this;
    }

    public void dataChanged() {
        needToRefresh = true;
        clearValidCaches();
    }

    public void dataTaken() {
        dataTaken = true;
        needToRefresh = true;
        clearValidCaches();
    }
    
    public void dataReleased(){
        dataTaken = false;
        needToRefresh = true;
        clearValidCaches();
    }
    
    @Override
    public void invalidate(){
        needToRefresh = true;
        clearValidCaches();
    }
    
    @Override
    public void validate(){
        if(!needToRefresh) {
            return;
        }
        if(!dataTaken){
            needToRefresh = false;
            AwtImageBackdoorAccessor ba = AwtImageBackdoorAccessor.getInstance();
            ba.validate(raster.getDataBuffer());
        }
        releaseDurtyRegions();
        
    }
    
    @Override
    public boolean invalidated(){
        return needToRefresh | dataTaken;
    }
}
