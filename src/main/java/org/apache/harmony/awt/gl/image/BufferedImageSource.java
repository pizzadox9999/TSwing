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

package org.apache.harmony.awt.gl.image;

import java.util.Hashtable;
import org.teavm.classlib.java.awt.image.TBufferedImage;
import org.teavm.classlib.java.awt.image.TColorModel;
import org.teavm.classlib.java.awt.image.TComponentColorModel;
import org.teavm.classlib.java.awt.image.TDataBuffer;
import org.teavm.classlib.java.awt.image.TDataBufferByte;
import org.teavm.classlib.java.awt.image.TDataBufferInt;
import org.teavm.classlib.java.awt.image.TDirectColorModel;
import org.teavm.classlib.java.awt.image.TImageConsumer;
import org.teavm.classlib.java.awt.image.TImageProducer;
import org.teavm.classlib.java.awt.image.TIndexColorModel;
import org.teavm.classlib.java.awt.image.TWritableRaster;

public class BufferedImageSource implements TImageProducer {

    private Hashtable<?, ?> properties;
    private TColorModel cm;
    private TWritableRaster raster;
    private int width;
    private int height;

    private TImageConsumer ic;

    public BufferedImageSource(TBufferedImage image, Hashtable<?, ?> properties){
        if(properties == null) {
            this.properties = new Hashtable<Object, Object>();
        } else {
            this.properties = properties;
        }

        width = image.getWidth();
        height = image.getHeight();
        cm = image.getColorModel();
        raster = image.getRaster();
    }

    public BufferedImageSource(TBufferedImage image){
        this(image, null);
    }

    public boolean isConsumer(TImageConsumer ic) {
        return (this.ic == ic);
    }

    public void startProduction(TImageConsumer ic) {
        addConsumer(ic);
    }

    public void requestTopDownLeftRightResend(TImageConsumer ic) {
    }

    public void removeConsumer(TImageConsumer ic) {
        if (this.ic == ic) {
            this.ic = null;
        }
    }

    public void addConsumer(TImageConsumer ic) {
        this.ic = ic;
        startProduction();
    }

    private void startProduction(){
        try {
            ic.setDimensions(width, height);
            ic.setProperties(properties);
            ic.setColorModel(cm);
            ic.setHints(TImageConsumer.TOPDOWNLEFTRIGHT |
                    TImageConsumer.COMPLETESCANLINES |
                    TImageConsumer.SINGLEFRAME |
                    TImageConsumer.SINGLEPASS);
            if(cm instanceof TIndexColorModel &&
                    raster.getTransferType() == TDataBuffer.TYPE_BYTE ||
                    cm instanceof TComponentColorModel &&
                    raster.getTransferType() == TDataBuffer.TYPE_BYTE &&
                    raster.getNumDataElements() == 1){
                TDataBufferByte dbb = (TDataBufferByte) raster.getDataBuffer();
                byte data[] = dbb.getData();
                int off = dbb.getOffset();
                ic.setPixels(0, 0, width, height, cm, data, off, width);
            }else if(cm instanceof TDirectColorModel &&
                    raster.getTransferType() == TDataBuffer.TYPE_INT){
                TDataBufferInt dbi = (TDataBufferInt) raster.getDataBuffer();
                int data[] = dbi.getData();
                int off = dbi.getOffset();
                ic.setPixels(0, 0, width, height, cm, data, off, width);
            }else if(cm instanceof TDirectColorModel &&
                    raster.getTransferType() == TDataBuffer.TYPE_BYTE){
                TDataBufferByte dbb = (TDataBufferByte) raster.getDataBuffer();
                byte data[] = dbb.getData();
                int off = dbb.getOffset();
                ic.setPixels(0, 0, width, height, cm, data, off, width);
            }else{
                TColorModel rgbCM = TColorModel.getRGBdefault();
                int pixels[] = new int[width];
                Object pix = null;
                for(int y = 0; y < height; y++){
                    for(int x = 0 ; x < width; x++){
                        pix = raster.getDataElements(x, y, pix);
                        pixels[x] = cm.getRGB(pix);
                    }
                    ic.setPixels(0, y, width, 1, rgbCM, pixels, 0, width);
                }
            }
            ic.imageComplete(TImageConsumer.STATICIMAGEDONE);
        }catch (NullPointerException e){
            if (ic != null) {
                ic.imageComplete(TImageConsumer.IMAGEERROR);
            }
        }
    }

}
