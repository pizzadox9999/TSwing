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
 */
/*
 * Created on 22.12.2004
 *
 */
package org.apache.harmony.awt.gl.image;

import org.teavm.classlib.java.awt.TGraphics;
import org.teavm.classlib.java.awt.TImage;
import org.teavm.classlib.java.awt.TRectangle;
import org.teavm.classlib.java.awt.image.TBufferedImage;
import org.teavm.classlib.java.awt.image.TColorModel;
import org.teavm.classlib.java.awt.image.TComponentColorModel;
import org.teavm.classlib.java.awt.image.TDataBuffer;
import org.teavm.classlib.java.awt.image.TDataBufferByte;
import org.teavm.classlib.java.awt.image.TDataBufferInt;
import org.teavm.classlib.java.awt.image.TDirectColorModel;
import org.teavm.classlib.java.awt.image.TImageObserver;
import org.teavm.classlib.java.awt.image.TImageProducer;
import org.teavm.classlib.java.awt.image.TIndexColorModel;
import org.teavm.classlib.java.awt.image.TWritableRaster;

import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.apache.harmony.awt.gl.AwtImageBackdoorAccessor;

import org.apache.harmony.awt.gl.ImageSurface;
import org.apache.harmony.awt.internal.nls.Messages;
import org.teavm.classlib.java.awt.image.TImageConsumer;


/**
 * This class represent implementation of abstact TImage class
 */
public class OffscreenImage extends TImage implements TImageConsumer {

    static final TColorModel rgbCM = TColorModel.getRGBdefault();
    TImageProducer src;
    TBufferedImage image;
    TColorModel cm;
    TWritableRaster raster;
    boolean isIntRGB;
    Hashtable<?, ?> properties;
    Vector<TImageObserver> observers;
    int width;
    int height;
    int imageState;
    int hints;
    private boolean producing;
    private boolean done;
    private ImageSurface imageSurf;
    AwtImageBackdoorAccessor ba = AwtImageBackdoorAccessor.getInstance();


    public OffscreenImage(TImageProducer ip){
        imageState = 0;
        src = ip;
        width = -1;
        height = -1;
        observers = new Vector<TImageObserver>();
        producing = false;
        done = false;
    }

    @Override
    public Object getProperty(String name, TImageObserver observer) {
        if(name == null) {
            // awt.38=Property name is not defined
            throw new NullPointerException(Messages.getString("awt.38")); //$NON-NLS-1$
        }
        if(!done && properties == null){
            startProduction(observer);
        }
        if(properties == null) {
            return null;
        }
        Object prop = properties.get(name);
        if(prop == null) {
            prop = UndefinedProperty;
        }
        return prop;
    }

    @Override
    public TImageProducer getSource() {
        return src;
    }

    @Override
    public int getWidth(TImageObserver observer) {
        if(!done && (imageState & TImageObserver.WIDTH) == 0){
            startProduction(observer);
        }
        return width;
    }

    @Override
    public int getHeight(TImageObserver observer) {
        if(!done && (imageState & TImageObserver.HEIGHT) == 0){
            startProduction(observer);
        }
        return height;
    }

    @Override
    public TGraphics getGraphics() {
        // awt.39=This method is not implemented for image obtained from TImageProducer
        throw new UnsupportedOperationException(Messages.getString("awt.39")); //$NON-NLS-1$
    }

    @Override
    public void flush() {
        imageUpdate(TImageObserver.ABORT, -1, -1, -1, -1);
        synchronized (this) {
            imageState = 0;
            image = null;
            imageSurf = null;
            cm = null;
            raster = null;
            hints = 0;
            width = -1;
            height = -1;
        }
    }

    public  void setProperties(Hashtable<?, ?> properties) {
        synchronized (this) {
            this.properties = properties;
        }
        imageUpdate(TImageObserver.PROPERTIES);
    }

    public synchronized void setColorModel(TColorModel cm) {
        this.cm = cm;
    }

    /*
     * We suppose what in case loading JPEG image then image has TDirectColorModel
     * and for infill image Raster will use setPixels method with int array.
     *
     * In case loading GIF image, for raster infill, is used setPixels method with
     * byte array and Color Model is TIndexColorModel. But Color Model may
     * be changed during this process. Then is called setPixels method with
     * int array and image force to default color model - int ARGB. The rest
     * pixels are sending in TDirectColorModel.
     */
    public void setPixels(int x, int y, int w, int h, TColorModel model,
            int[] pixels, int off, int scansize) {

        if(raster == null){
            if(cm == null){
                if(model == null) {
                    // awt.3A=Color Model is null
                    throw new NullPointerException(Messages.getString("awt.3A")); //$NON-NLS-1$
                }
                cm = model;
            }
            createRaster();
        }

        if(model == null) {
            model = cm;
        }
        if(cm != model){
            forceToIntARGB();
        }

        TDataBuffer db = raster.getDataBuffer();
        Object surfData = ba.getData(db);

        synchronized(surfData){
            if(cm == model && model.getTransferType() == TDataBuffer.TYPE_INT &&
                    raster.getNumDataElements() == 1){

                int data[] = (int[])surfData;
                int scanline = raster.getWidth();
                TDataBufferInt dbi = (TDataBufferInt) db;
                int rof = dbi.getOffset() + y * scanline + x;
                for(int lineOff = off, line = y; line < y + h;
                    line++, lineOff += scansize, rof += scanline){

                    System.arraycopy(pixels, lineOff, data, rof, w);
                }

            }else if(isIntRGB){
                int buff[] = new int[w];
                int data[] = (int[])surfData;
                int scanline = raster.getWidth();
                TDataBufferInt dbi = (TDataBufferInt) db;
                int rof = dbi.getOffset() + y * scanline + x;
                for (int sy = y, sOff = off; sy < y + h; sy++, sOff += scansize,
                    rof += scanline) {

                    for (int sx = x, idx = 0; sx < x + w; sx++, idx++) {
                        buff[idx] = model.getRGB(pixels[sOff + idx]);
                    }
                    System.arraycopy(buff, 0, data, rof, w);
                }
            }else{
                Object buf = null;
                for (int sy = y, sOff = off; sy < y + h; sy++, sOff += scansize) {
                    for (int sx = x, idx = 0; sx < x + w; sx++, idx++) {
                        int rgb = model.getRGB(pixels[sOff + idx]);
                        buf = cm.getDataElements(rgb, buf);
                        raster.setDataElements(sx, sy, buf);
                    }
                }
            }
        }
        
        ba.releaseData(db);
        if (imageSurf != null) {
            imageSurf.addDirtyRegion(new TRectangle(x, y, w, h));
        }

        imageUpdate(TImageObserver.SOMEBITS);
    }

    public void setPixels(int x, int y, int w, int h, TColorModel model,
            byte[] pixels, int off, int scansize) {
        if(raster == null){
            if(cm == null){
                if(model == null) {
                    // awt.3A=Color Model is null
                    throw new NullPointerException(Messages.getString("awt.3A")); //$NON-NLS-1$
                }
                cm = model;
            }
            createRaster();
        }
        if(model == null) {
            model = cm;
        }
        if(model != cm && cm != rgbCM){
            forceToIntARGB();
        }

        TDataBuffer db = raster.getDataBuffer();
        Object surfData = ba.getData(db);

        synchronized(surfData){
            if(isIntRGB){
                int buff[] = new int[w];
                int data[] = (int[])surfData;
                int scanline = raster.getWidth();
                TDataBufferInt dbi = (TDataBufferInt) db;
                int rof = dbi.getOffset() + y * scanline + x;
                if(model instanceof TIndexColorModel){

                    TIndexColorModel icm = (TIndexColorModel) model;
                    int colorMap[] = new int[icm.getMapSize()];
                    icm.getRGBs(colorMap);

                    for (int sy = y, sOff = off; sy < y + h; sy++, sOff += scansize,
                        rof += scanline) {
                        for (int sx = x, idx = 0; sx < x + w; sx++, idx++) {
                            buff[idx] = colorMap[pixels[sOff + idx] & 0xff];
                        }
                        System.arraycopy(buff, 0, data, rof, w);
                    }
                }else{

                    for (int sy = y, sOff = off; sy < y + h; sy++, sOff += scansize,
                        rof += scanline) {
                        for (int sx = x, idx = 0; sx < x + w; sx++, idx++) {
                            buff[idx] = model.getRGB(pixels[sOff + idx] & 0xff);
                        }
                        System.arraycopy(buff, 0, data, rof, w);
                    }
                }
            }else if(model == cm && model.getTransferType() == TDataBuffer.TYPE_BYTE &&
                    raster.getNumDataElements() == 1){

                byte data[] = (byte[])surfData;
                int scanline = raster.getWidth();
                TDataBufferByte dbb = (TDataBufferByte) db;
                int rof = dbb.getOffset() + y * scanline + x;
                for(int lineOff = off, line = y; line < y + h;
                    line++, lineOff += scansize, rof += scanline){
                    System.arraycopy(pixels, lineOff, data, rof, w);
                }
            }else if(model == cm && model.getTransferType() == TDataBuffer.TYPE_BYTE &&
                    cm instanceof TComponentColorModel){

                byte stride[] = new byte[scansize];
                for (int sy = y, sOff = off; sy < y + h; sy++, sOff += scansize) {
                    System.arraycopy(pixels, sOff, stride, 0, scansize);
                    
                    raster.setDataElements(x, sy, w, 1, stride);
                }
            }else {
                for (int sy = y, sOff = off; sy < y + h; sy++, sOff += scansize) {
                    for (int sx = x, idx = 0; sx < x + w; sx++, idx++) {
                        int rgb = model.getRGB(pixels[sOff + idx] & 0xff);
                        raster.setDataElements(sx, sy, cm.getDataElements(rgb, null));
                    }
                }
            }
        }

        ba.releaseData(db);
        if (imageSurf != null) {
            imageSurf.addDirtyRegion(new TRectangle(x, y, w, h));
        }

        imageUpdate(TImageObserver.SOMEBITS);
    }

    public void setDimensions(int width, int height) {
        if(width <= 0 || height <= 0){
            imageComplete(IMAGEERROR);
            return;
        }
        synchronized (this) {
            this.width = width;
            this.height = height;
        }
        imageUpdate(TImageObserver.WIDTH | TImageObserver.HEIGHT);

    }

    public void setHints(int hints) {
        synchronized (this) {
            this.hints = hints;
        }
    }

    public void imageComplete(int state) {
        int flag;
        switch(state){
        case IMAGEABORTED:
            flag = TImageObserver.ABORT;
            break;
        case IMAGEERROR:
            flag = TImageObserver.ERROR | TImageObserver.ABORT;
            break;
        case SINGLEFRAMEDONE:
            flag = TImageObserver.FRAMEBITS;
            break;
        case STATICIMAGEDONE:
            flag = TImageObserver.ALLBITS;
            break;
        default:
            // awt.3B=Incorrect TImageConsumer completion status
            throw new IllegalArgumentException(Messages.getString("awt.3B")); //$NON-NLS-1$
        }

        imageUpdate(flag);
        if((imageState & (TImageObserver.ERROR | TImageObserver.ABORT |
                TImageObserver.ALLBITS)) != 0 ) {
            
            stopProduction();
        }

    }

    public TBufferedImage getBufferedImage(){
        if(image == null){
            TColorModel model = getColorModel();
            TWritableRaster wr = getRaster();
            if(model != null && wr != null) {
                image = new TBufferedImage(model, wr, model.isAlphaPremultiplied(), null);
            }
        }
        return image;
    }

    public int checkTImage(TImageObserver observer){
        synchronized (this) {
            addObserver(observer);
        }
        return imageState;
    }

    public boolean prepareTImage(TImageObserver observer){
        if(!done){
            if((imageState & TImageObserver.ERROR) != 0){
                if(observer != null){
                    observer.imageUpdate(this, TImageObserver.ERROR |
                            TImageObserver.ABORT, -1, -1, -1, -1);
                }
                return false;
            }
            startProduction(observer);
        }
        
        return ((imageState & TImageObserver.ALLBITS) != 0);
    }

    public TColorModel getColorModel(){
        if(cm == null) {
            startProduction(null);
        }
        return cm;
    }

    public TWritableRaster getRaster(){
        if(raster == null) {
            startProduction(null);
        }
        return raster;
    }

    public int getState(){
        return imageState;
    }

    private void addObserver(TImageObserver observer){
        if(observer != null){
            if(observers.contains(observer)) return;

            if((imageState & TImageObserver.ERROR) != 0){
                observer.imageUpdate(this, TImageObserver.ERROR |
                    TImageObserver.ABORT, -1, -1, -1, -1);
          
                return;
            }

            if((imageState & TImageObserver.ALLBITS) != 0){
                observer.imageUpdate(this, imageState, 0, 0, width, height);

                return;
            }
            synchronized (observers) {
                observers.add(observer);
            }
        }
    }

    private void startProduction(TImageObserver observer){
        addObserver(observer);
        if(!producing && !done){
            synchronized(this){
                imageState &= ~TImageObserver.ABORT;
                producing = true;
                src.startProduction(this);
            }
        }
    }

    private synchronized void stopProduction(){
        producing = false;
        src.removeConsumer(this);
        synchronized (observers) {
            observers.clear();
        }
    }

    private void createRaster(){
        try{
            raster = cm.createCompatibleWritableRaster(width, height);
            isIntRGB = false;
            if(cm instanceof TDirectColorModel){
                TDirectColorModel dcm = (TDirectColorModel) cm;
                if(dcm.getTransferType() == TDataBuffer.TYPE_INT &&
                        dcm.getRedMask() == 0xff0000 &&
                        dcm.getGreenMask() == 0xff00 &&
                        dcm.getBlueMask() == 0xff){
                    isIntRGB = true;
                }
            }
        }catch(Exception e){
            cm = TColorModel.getRGBdefault();
            raster = cm.createCompatibleWritableRaster(width, height);
            isIntRGB = true;
        }
    }

    private void imageUpdate(int state){
        imageUpdate(state, 0, 0, width, height);
    }
    
    private void imageUpdate(int state, int x, int y, int width, int height){
        synchronized(this){
            imageState |= state;
            if((imageState & (TImageObserver.ALLBITS)) != 0 ) {
                done = true;
            }
        }
        TImageObserver observer = null;

        for (Iterator<TImageObserver> i = observers.iterator(); i.hasNext();) {
            try {
                observer = i.next();
            } catch (ConcurrentModificationException e) {
                i = observers.iterator();
                continue;
            }
            observer.imageUpdate(this, imageState, x, y, width, height);
        }

    }

    private void forceToIntARGB(){

        int w = raster.getWidth();
        int h = raster.getHeight();

        TWritableRaster destRaster = rgbCM.createCompatibleWritableRaster(w, h);

        Object obj = null;
        int pixels[] = new int[w];

        if(cm instanceof TIndexColorModel){
            TIndexColorModel icm = (TIndexColorModel) cm;
            int colorMap[] = new int[icm.getMapSize()];
            icm.getRGBs(colorMap);

            for (int y = 0; y < h; y++) {
                obj = raster.getDataElements(0, y, w, 1, obj);
                byte ba[] = (byte[]) obj;
                for (int x = 0; x < ba.length; x++) {
                    pixels[x] = colorMap[ba[x] & 0xff];
                }
                destRaster.setDataElements(0, y, w, 1, pixels);
            }

        }else{
            for(int y = 0; y < h; y++){
                for(int x = 0; x < w; x++){
                    obj = raster.getDataElements(x, y, obj);
                    pixels[x] = cm.getRGB(obj);
                }
                destRaster.setDataElements(0, y, w, 1, pixels);
            }
        }

        synchronized(this){
            if(imageSurf != null){
                imageSurf.dispose();
                imageSurf = null;
            }
            if(image != null){
                image.flush();
                image = null;
            }
            cm = rgbCM;
            raster = destRaster;
            isIntRGB = true;
        }
    }

    public ImageSurface getImageSurface() {
        if (imageSurf == null) {
            TColorModel model = getColorModel();
            TWritableRaster wr = getRaster();
            if(model != null && wr != null) {
                imageSurf = new ImageSurface(model, wr);
            }
        }
        return imageSurf;
    }
}
