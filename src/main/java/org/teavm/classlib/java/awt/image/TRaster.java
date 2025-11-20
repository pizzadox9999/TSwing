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
package org.teavm.classlib.java.awt.image;

import org.apache.harmony.awt.gl.image.OrdinaryWritableRaster;
import org.apache.harmony.awt.internal.nls.Messages;
import org.teavm.classlib.java.awt.TPoint;
import org.teavm.classlib.java.awt.TRectangle;


public class TRaster {

    protected TDataBuffer dataBuffer;

    protected int height;

    protected int minX;

    protected int minY;

    protected int numBands;

    protected int numDataElements;

    protected TRaster parent;

    protected TSampleModel sampleModel;

    protected int sampleModelTranslateX;

    protected int sampleModelTranslateY;

    protected int width;

    public static TWritableRaster createBandedRaster(TDataBuffer dataBuffer,
            int w, int h, int scanlineStride, int bankIndices[],
            int bandOffsets[], TPoint location) {

        if (w <= 0 || h <= 0) {
            // awt.22E=w or h is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        if ((long) location.x + w > Integer.MAX_VALUE
                || (long) location.y + h > Integer.MAX_VALUE) {
            // awt.276=location.x + w or location.y + h results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
        }

        if (bankIndices == null || bandOffsets == null) {
            // awt.277=bankIndices or bandOffsets is null
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.277")); //$NON-NLS-1$
        }

        if (dataBuffer == null) {
            // awt.278=dataBuffer is null
            throw new NullPointerException(Messages.getString("awt.278")); //$NON-NLS-1$
        }

        int dataType = dataBuffer.getDataType();

        if (dataType != TDataBuffer.TYPE_BYTE
                && dataType != TDataBuffer.TYPE_USHORT
                && dataType != TDataBuffer.TYPE_INT) {
            // awt.230=dataType is not one of the supported data types
            throw new IllegalArgumentException(Messages.getString("awt.230")); //$NON-NLS-1$
        }

        TBandedSampleModel sampleModel = new TBandedSampleModel(dataType, w, h,
                scanlineStride, bankIndices, bandOffsets);

        return new OrdinaryWritableRaster(sampleModel, dataBuffer, location);
    }

    public static TWritableRaster createBandedRaster(int dataType, int w, int h,
            int scanlineStride, int bankIndices[], int bandOffsets[],
            TPoint location) {

        if (w <= 0 || h <= 0) {
            // awt.22E=w or h is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        if ((long) location.x + w > Integer.MAX_VALUE
                || (long) location.y + h > Integer.MAX_VALUE) {
            // awt.276=location.x + w or location.y + h results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
        }

        if (bankIndices == null || bandOffsets == null) {
            // awt.277=bankIndices or bandOffsets is null
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.277")); //$NON-NLS-1$
        }

        if (dataType != TDataBuffer.TYPE_BYTE
                && dataType != TDataBuffer.TYPE_USHORT
                && dataType != TDataBuffer.TYPE_INT) {
            // awt.230=dataType is not one of the supported data types
            throw new IllegalArgumentException(Messages.getString("awt.230")); //$NON-NLS-1$
        }

        int maxOffset = bandOffsets[0];
        int maxBank = bankIndices[0];

        for (int i = 0; i < bankIndices.length; i++) {
            if (bandOffsets[i] > maxOffset) {
                maxOffset = bandOffsets[i];
            }
            if (bankIndices[i] > maxBank) {
                maxBank = bankIndices[i];
            }
        }

        int numBanks = maxBank + 1;
        int dataSize = scanlineStride * (h - 1) + w + maxOffset;

        TDataBuffer data = null;

        switch (dataType) {
        case TDataBuffer.TYPE_BYTE:
            data = new TDataBufferByte(dataSize, numBanks);
            break;
        case TDataBuffer.TYPE_USHORT:
            data = new TDataBufferUShort(dataSize, numBanks);
            break;
        case TDataBuffer.TYPE_INT:
            data = new TDataBufferInt(dataSize, numBanks);
            break;
        }
        return createBandedRaster(data, w, h, scanlineStride, bankIndices,
                bandOffsets, location);
    }

    public static TWritableRaster createBandedRaster(int dataType, int w, int h,
            int bands, TPoint location) {

        if (w <= 0 || h <= 0) {
            // awt.22E=w or h is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        if ((long) location.x + w > Integer.MAX_VALUE
                || (long) location.y + h > Integer.MAX_VALUE) {
            // awt.276=location.x + w or location.y + h results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
        }

        if (bands < 1) {
            // awt.279=bands is less than 1
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.279")); //$NON-NLS-1$
        }

        int bandOffsets[] = new int[bands];
        int bankIndices[] = new int[bands];

        for (int i = 0; i < bands; i++) {
            bandOffsets[i] = 0;
            bankIndices[i] = i;
        }
        return createBandedRaster(dataType, w, h, w, bankIndices, bandOffsets,
                location);
    }

    public static TWritableRaster createInterleavedRaster(TDataBuffer dataBuffer,
            int w, int h, int scanlineStride, int pixelStride,
            int bandOffsets[], TPoint location) {

        if (w <= 0 || h <= 0) {
            // awt.22E=w or h is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        if ((long) location.x + w > Integer.MAX_VALUE
                || (long) location.y + h > Integer.MAX_VALUE) {
            // awt.276=location.x + w or location.y + h results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
        }

        if (dataBuffer == null) {
            // awt.278=dataBuffer is null
            throw new NullPointerException(Messages.getString("awt.278")); //$NON-NLS-1$
        }

        int dataType = dataBuffer.getDataType();
        if (dataType != TDataBuffer.TYPE_BYTE
                && dataType != TDataBuffer.TYPE_USHORT) {
            // awt.230=dataType is not one of the supported data types
            throw new IllegalArgumentException(Messages.getString("awt.230")); //$NON-NLS-1$
        }

        if (dataBuffer.getNumBanks() > 1) {
            // awt.27A=dataBuffer has more than one bank
            throw new TRasterFormatException(Messages.getString("awt.27A")); //$NON-NLS-1$
        }

        if (bandOffsets == null) {
            // awt.27B=bandOffsets is null
            throw new NullPointerException(Messages.getString("awt.27B")); //$NON-NLS-1$
        }

        TPixelInterleavedSampleModel sampleModel = 
            new TPixelInterleavedSampleModel(dataType, w, h, 
                    pixelStride, scanlineStride, bandOffsets);

        return new OrdinaryWritableRaster(sampleModel, dataBuffer, location);
    }

    public static TWritableRaster createInterleavedRaster(int dataType, int w,
            int h, int scanlineStride, int pixelStride, int bandOffsets[],
            TPoint location) {

        if (w <= 0 || h <= 0) {
            // awt.22E=w or h is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        if ((long) location.x + w > Integer.MAX_VALUE
                || (long) location.y + h > Integer.MAX_VALUE) {
            // awt.276=location.x + w or location.y + h results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
        }

        if (dataType != TDataBuffer.TYPE_BYTE
                && dataType != TDataBuffer.TYPE_USHORT) {
            // awt.230=dataType is not one of the supported data types
            throw new IllegalArgumentException(Messages.getString("awt.230")); //$NON-NLS-1$
        }

        if (bandOffsets == null) {
            // awt.27B=bandOffsets is null
            throw new NullPointerException(Messages.getString("awt.27B")); //$NON-NLS-1$
        }

        int minOffset = bandOffsets[0];
        for (int i = 1; i < bandOffsets.length; i++) {
            if (bandOffsets[i] < minOffset) {
                minOffset = bandOffsets[i];
            }
        }
        int size = (h - 1) * scanlineStride + w * pixelStride + minOffset;
        TDataBuffer data = null;

        switch (dataType) {
        case TDataBuffer.TYPE_BYTE:
            data = new TDataBufferByte(size);
            break;
        case TDataBuffer.TYPE_USHORT:
            data = new TDataBufferUShort(size);
            break;
        }

        return createInterleavedRaster(data, w, h, scanlineStride, pixelStride,
                bandOffsets, location);
    }

    public static TWritableRaster createInterleavedRaster(int dataType, int w,
            int h, int bands, TPoint location) {

        if (w <= 0 || h <= 0) {
            // awt.22E=w or h is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        if ((long) location.x + w > Integer.MAX_VALUE
                || (long) location.y + h > Integer.MAX_VALUE) {
            // awt.276=location.x + w or location.y + h results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
        }

        if (dataType != TDataBuffer.TYPE_BYTE
                && dataType != TDataBuffer.TYPE_USHORT) {
            // awt.230=dataType is not one of the supported data types
            throw new IllegalArgumentException(Messages.getString("awt.230")); //$NON-NLS-1$
        }

        int bandOffsets[] = new int[bands];
        for (int i = 0; i < bands; i++) {
            bandOffsets[i] = i;
        }

        return createInterleavedRaster(dataType, w, h, w * bands, bands,
                bandOffsets, location);
    }

    public static TWritableRaster createPackedRaster(TDataBuffer dataBuffer,
            int w, int h, int scanlineStride, int bandMasks[], TPoint location) {
        if (dataBuffer == null) {
            // awt.278=dataBuffer is null
            throw new NullPointerException(Messages.getString("awt.278")); //$NON-NLS-1$
        }

        if (w <= 0 || h <= 0) {
            // awt.22E=w or h is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        if ((long) location.x + w > Integer.MAX_VALUE
                || (long) location.y + h > Integer.MAX_VALUE) {
            // awt.276=location.x + w or location.y + h results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
        }

        if (bandMasks == null) {
            // awt.27C=bandMasks is null
            throw new TRasterFormatException(Messages.getString("awt.27C")); //$NON-NLS-1$
        }

        if (dataBuffer.getNumBanks() > 1) {
            // awt.27A=dataBuffer has more than one bank
            throw new TRasterFormatException(Messages.getString("awt.27A")); //$NON-NLS-1$
        }

        int dataType = dataBuffer.getDataType();
        if (dataType != TDataBuffer.TYPE_BYTE
                && dataType != TDataBuffer.TYPE_USHORT
                && dataType != TDataBuffer.TYPE_INT) {
            // awt.230=dataType is not one of the supported data types
            throw new IllegalArgumentException(Messages.getString("awt.230")); //$NON-NLS-1$
        }

        TSinglePixelPackedSampleModel sampleModel = 
            new TSinglePixelPackedSampleModel(dataType, w, h, 
                    scanlineStride, bandMasks);

        return new OrdinaryWritableRaster(sampleModel, dataBuffer, location);
    }

    public static TWritableRaster createPackedRaster(TDataBuffer dataBuffer,
            int w, int h, int bitsPerPixel, TPoint location) {

        if (w <= 0 || h <= 0) {
            // awt.22E=w or h is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        if ((long) location.x + w > Integer.MAX_VALUE
                || (long) location.y + h > Integer.MAX_VALUE) {
            // awt.276=location.x + w or location.y + h results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
        }

        if (dataBuffer == null) {
            // awt.278=dataBuffer is null
            throw new NullPointerException(Messages.getString("awt.278")); //$NON-NLS-1$
        }

        if (dataBuffer.getNumBanks() > 1) {
            // awt.27A=dataBuffer has more than one bank
            throw new TRasterFormatException(Messages.getString("awt.27A")); //$NON-NLS-1$
        }

        int dataType = dataBuffer.getDataType();
        if (dataType != TDataBuffer.TYPE_BYTE
                && dataType != TDataBuffer.TYPE_USHORT
                && dataType != TDataBuffer.TYPE_INT) {
            // awt.230=dataType is not one of the supported data types
            throw new IllegalArgumentException(Messages.getString("awt.230")); //$NON-NLS-1$
        }

        TMultiPixelPackedSampleModel sampleModel = 
            new TMultiPixelPackedSampleModel(dataType, w, h, bitsPerPixel);

        return new OrdinaryWritableRaster(sampleModel, dataBuffer, location);
    }

    public static TWritableRaster createPackedRaster(int dataType, int w, int h,
            int bands, int bitsPerBand, TPoint location) {

        if (w <= 0 || h <= 0) {
            // awt.22E=w or h is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        if ((long) location.x + w > Integer.MAX_VALUE
                || (long) location.y + h > Integer.MAX_VALUE) {
            // awt.276=location.x + w or location.y + h results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
        }

        if (bands < 1 || bitsPerBand < 1) {
            // awt.27D=bitsPerBand or bands is not greater than zero
            throw new IllegalArgumentException(Messages.getString("awt.27D")); //$NON-NLS-1$
        }

        if (dataType != TDataBuffer.TYPE_BYTE
                && dataType != TDataBuffer.TYPE_USHORT
                && dataType != TDataBuffer.TYPE_INT) {
            // awt.230=dataType is not one of the supported data types
            throw new IllegalArgumentException(Messages.getString("awt.230")); //$NON-NLS-1$
        }

        if (bitsPerBand * bands > TDataBuffer.getDataTypeSize(dataType)) {
            // awt.27E=The product of bitsPerBand and bands is greater than the number of bits held by dataType
            throw new IllegalArgumentException(Messages.getString("awt.27E")); //$NON-NLS-1$
        }

        if (bands > 1) {

            int bandMasks[] = new int[bands];
            int mask = (1 << bitsPerBand) - 1;

            for (int i = 0; i < bands; i++) {
                bandMasks[i] = mask << (bitsPerBand * (bands - 1 - i));
            }

            return createPackedRaster(dataType, w, h, bandMasks, location);
        }
        TDataBuffer data = null;
        int size = ((bitsPerBand * w + 
                TDataBuffer.getDataTypeSize(dataType) - 1) / 
                TDataBuffer.getDataTypeSize(dataType)) * h;

        switch (dataType) {
        case TDataBuffer.TYPE_BYTE:
            data = new TDataBufferByte(size);
            break;
        case TDataBuffer.TYPE_USHORT:
            data = new TDataBufferUShort(size);
            break;
        case TDataBuffer.TYPE_INT:
            data = new TDataBufferInt(size);
            break;
        }
        return createPackedRaster(data, w, h, bitsPerBand, location);
    }

    public static TWritableRaster createPackedRaster(int dataType, int w, int h,
            int bandMasks[], Point location) {
        
        if (dataType != TDataBuffer.TYPE_BYTE
                && dataType != TDataBuffer.TYPE_USHORT
                && dataType != TDataBuffer.TYPE_INT) {
            // awt.230=dataType is not one of the supported data types
            throw new IllegalArgumentException(Messages.getString("awt.230")); //$NON-NLS-1$
        }

        if (w <= 0 || h <= 0) {
            // awt.22E=w or h is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        if ((long) location.x + w > Integer.MAX_VALUE
                || (long) location.y + h > Integer.MAX_VALUE) {
            // awt.276=location.x + w or location.y + h results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
        }

        if (bandMasks == null) {
            // awt.27C=bandMasks is null
            throw new NullPointerException(Messages.getString("awt.27C")); //$NON-NLS-1$
        }

        TDataBuffer data = null;

        switch (dataType) {
        case TDataBuffer.TYPE_BYTE:
            data = new TDataBufferByte(w * h);
            break;
        case TDataBuffer.TYPE_USHORT:
            data = new TDataBufferUShort(w * h);
            break;
        case TDataBuffer.TYPE_INT:
            data = new TDataBufferInt(w * h);
            break;
        }

        return createPackedRaster(data, w, h, w, bandMasks, location);
    }

    public static TRaster createRaster(TSampleModel sm, TDataBuffer db,
            Point location) {

        if (sm == null || db == null) {
            // awt.27F=SampleModel or DataBuffer is null
            throw new NullPointerException(Messages.getString("awt.27F")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        return new TRaster(sm, db, location);
    }

    public static TWritableRaster createWritableRaster(TSampleModel sm,
            TDataBuffer db, Point location) {

        if (sm == null || db == null) {
            // awt.27F=SampleModel or DataBuffer is null
            throw new NullPointerException(Messages.getString("awt.27F")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        return new OrdinaryWritableRaster(sm, db, location);
    }

    public static TWritableRaster createWritableRaster(TSampleModel sm,
            Point location) {

        if (sm == null) {
            // awt.280=SampleModel is null
            throw new NullPointerException(Messages.getString("awt.280")); //$NON-NLS-1$
        }

        if (location == null) {
            location = new TPoint(0, 0);
        }

        return createWritableRaster(sm, sm.createDataBuffer(), location);
    }

    protected TRaster(TSampleModel sampleModel, TDataBuffer dataBuffer,
            Point origin) {

        this(sampleModel, dataBuffer, new TRectangle(origin.x, origin.y,
                sampleModel.getWidth(), sampleModel.getHeight()), origin, null);
    }

    protected TRaster(TSampleModel sampleModel, TDataBuffer dataBuffer,
            Rectangle aRegion, Point sampleModelTranslate, TRaster parent) {

        if (sampleModel == null || dataBuffer == null || aRegion == null
                || sampleModelTranslate == null) {
            // awt.281=sampleModel, dataBuffer, aRegion or sampleModelTranslate is null
            throw new NullPointerException(Messages.getString("awt.281")); //$NON-NLS-1$
        }

        if (aRegion.width <= 0 || aRegion.height <= 0) {
            // awt.282=aRegion has width or height less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.282")); //$NON-NLS-1$
        }

        if ((long) aRegion.x + (long) aRegion.width > Integer.MAX_VALUE) {
            // awt.283=Overflow X coordinate of Raster
            throw new TRasterFormatException(Messages.getString("awt.283")); //$NON-NLS-1$
        }

        if ((long) aRegion.y + (long) aRegion.height > Integer.MAX_VALUE) {
            // awt.284=Overflow Y coordinate of Raster
            throw new TRasterFormatException(Messages.getString("awt.284")); //$NON-NLS-1$
        }
        
        validateDataBuffer(dataBuffer, aRegion.width, aRegion.height,
                sampleModel);

        this.sampleModel = sampleModel;
        this.dataBuffer = dataBuffer;
        this.minX = aRegion.x;
        this.minY = aRegion.y;
        this.width = aRegion.width;
        this.height = aRegion.height;
        this.sampleModelTranslateX = sampleModelTranslate.x;
        this.sampleModelTranslateY = sampleModelTranslate.y;
        this.parent = parent;
        this.numBands = sampleModel.getNumBands();
        this.numDataElements = sampleModel.getNumDataElements();

    }

    protected TRaster(TSampleModel sampleModel, TPoint origin) {
        this(sampleModel, sampleModel.createDataBuffer(), new TRectangle(
                origin.x, origin.y, sampleModel.getWidth(), sampleModel
                        .getHeight()), origin, null);
    }

    public TRaster createChild(int parentX, int parentY, int width, int height,
            int childMinX, int childMinY, int bandList[]) {
        if (width <= 0 || height <= 0) {
            // awt.285=Width or Height of child Raster is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.285")); //$NON-NLS-1$
        }

        if (parentX < this.minX || parentX + width > this.minX + this.width) {
            // awt.286=parentX disposes outside Raster
            throw new TRasterFormatException(Messages.getString("awt.286")); //$NON-NLS-1$
        }

        if (parentY < this.minY || parentY + height > this.minY + this.height) {
            // awt.287=parentY disposes outside Raster
            throw new TRasterFormatException(Messages.getString("awt.287")); //$NON-NLS-1$
        }

        if ((long) parentX + width > Integer.MAX_VALUE) {
            // awt.288=parentX + width results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.288")); //$NON-NLS-1$
        }

        if ((long) parentY + height > Integer.MAX_VALUE) {
            // awt.289=parentY + height results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.289")); //$NON-NLS-1$
        }

        if ((long) childMinX + width > Integer.MAX_VALUE) {
            // awt.28A=childMinX + width results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.28A")); //$NON-NLS-1$
        }

        if ((long) childMinY + height > Integer.MAX_VALUE) {
            // awt.28B=childMinY + height results in integer overflow
            throw new TRasterFormatException(Messages.getString("awt.28B")); //$NON-NLS-1$
        }

        TSampleModel childModel;

        if (bandList == null) {
            childModel = sampleModel;
        } else {
            childModel = sampleModel.createSubsetSampleModel(bandList);
        }

        int childTranslateX = childMinX - parentX;
        int childTranslateY = childMinY - parentY;

        return new TRaster(childModel, dataBuffer, new TRectangle(childMinX,
                childMinY, width, height), new TPoint(childTranslateX
                + sampleModelTranslateX, childTranslateY
                + sampleModelTranslateY), this);
    }

    public TWritableRaster createCompatibleWritableRaster() {
        return new OrdinaryWritableRaster(sampleModel, new TPoint(0, 0));
    }

    public TWritableRaster createCompatibleWritableRaster(int w, int h) {
        if (w <= 0 || h <= 0) {
            // awt.22E=w or h is less than or equal to zero
            throw new TRasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
        }

        TSampleModel sm = sampleModel.createCompatibleSampleModel(w, h);

        return new OrdinaryWritableRaster(sm, new TPoint(0, 0));
    }

    public TWritableRaster createCompatibleWritableRaster(int x, int y, int w,
            int h) {

        TWritableRaster raster = createCompatibleWritableRaster(w, h);

        return raster.createWritableChild(0, 0, w, h, x, y, null);
    }

    public TWritableRaster createCompatibleWritableRaster(Rectangle rect) {
        if (rect == null) {
            // awt.28C=Rect is null
            throw new NullPointerException(Messages.getString("awt.28C")); //$NON-NLS-1$
        }

        return createCompatibleWritableRaster(rect.x, rect.y, rect.width,
                rect.height);
    }

    public TRaster createTranslatedChild(int childMinX, int childMinY) {
        return createChild(minX, minY, width, height, childMinX, childMinY,
                null);
    }

    public TRectangle getBounds() {
        return new TRectangle(minX, minY, width, height);
    }

    public TDataBuffer getDataBuffer() {
        return dataBuffer;
    }

    public Object getDataElements(int x, int y, int w, int h, Object outData) {
        return sampleModel.getDataElements(x - sampleModelTranslateX, y
                - sampleModelTranslateY, w, h, outData, dataBuffer);
    }

    public Object getDataElements(int x, int y, Object outData) {
        return sampleModel.getDataElements(x - sampleModelTranslateX, y
                - sampleModelTranslateY, outData, dataBuffer);
    }

    public final int getHeight() {
        return height;
    }

    public final int getMinX() {
        return minX;
    }

    public final int getMinY() {
        return minY;
    }

    public final int getNumBands() {
        return numBands;
    }

    public final int getNumDataElements() {
        return numDataElements;
    }

    public TRaster getParent() {
        return parent;
    }

    public double[] getPixel(int x, int y, double dArray[]) {
        return sampleModel.getPixel(x - sampleModelTranslateX, y
                - sampleModelTranslateY, dArray, dataBuffer);
    }

    public float[] getPixel(int x, int y, float fArray[]) {
        return sampleModel.getPixel(x - sampleModelTranslateX, y
                - sampleModelTranslateY, fArray, dataBuffer);
    }

    public int[] getPixel(int x, int y, int iArray[]) {
        return sampleModel.getPixel(x - sampleModelTranslateX, y
                - sampleModelTranslateY, iArray, dataBuffer);
    }

    public double[] getPixels(int x, int y, int w, int h, double dArray[]) {
        return sampleModel.getPixels(x - sampleModelTranslateX, y
                - sampleModelTranslateY, w, h, dArray, dataBuffer);
    }

    public float[] getPixels(int x, int y, int w, int h, float fArray[]) {
        return sampleModel.getPixels(x - sampleModelTranslateX, y
                - sampleModelTranslateY, w, h, fArray, dataBuffer);
    }

    public int[] getPixels(int x, int y, int w, int h, int iArray[]) {
        return sampleModel.getPixels(x - sampleModelTranslateX, y
                - sampleModelTranslateY, w, h, iArray, dataBuffer);
    }

    public int getSample(int x, int y, int b) {
        return sampleModel.getSample(x - sampleModelTranslateX, y
                - sampleModelTranslateY, b, dataBuffer);
    }

    public double getSampleDouble(int x, int y, int b) {
        return sampleModel.getSampleDouble(x - sampleModelTranslateX, y
                - sampleModelTranslateY, b, dataBuffer);
    }

    public float getSampleFloat(int x, int y, int b) {
        return sampleModel.getSampleFloat(x - sampleModelTranslateX, y
                - sampleModelTranslateY, b, dataBuffer);
    }

    public TSampleModel getSampleModel() {
        return sampleModel;
    }

    public final int getSampleModelTranslateX() {
        return sampleModelTranslateX;
    }

    public final int getSampleModelTranslateY() {
        return sampleModelTranslateY;
    }

    public double[] getSamples(int x, int y, int w, int h, int b,
            double dArray[]) {

        return sampleModel.getSamples(x - sampleModelTranslateX, y
                - sampleModelTranslateY, w, h, b, dArray, dataBuffer);
    }

    public float[] getSamples(int x, int y, int w, int h, int b, float fArray[]) {

        return sampleModel.getSamples(x - sampleModelTranslateX, y
                - sampleModelTranslateY, w, h, b, fArray, dataBuffer);
    }

    public int[] getSamples(int x, int y, int w, int h, int b, int iArray[]) {
        return sampleModel.getSamples(x - sampleModelTranslateX, y
                - sampleModelTranslateY, w, h, b, iArray, dataBuffer);
    }

    public final int getTransferType() {
        return sampleModel.getTransferType();
    }

    public final int getWidth() {
        return width;
    }

    private static void validateDataBuffer(final TDataBuffer dataBuffer, final int w,
            final int h, final TSampleModel sampleModel) {

        int size = 0;
        
        if (sampleModel instanceof TComponentSampleModel) {
            TComponentSampleModel csm = (TComponentSampleModel) sampleModel;
            int offsets[] = csm.getBandOffsets();
            int maxOffset = offsets[0];
            for (int i = 1; i < offsets.length; i++) {
                if (offsets[i] > maxOffset) {
                    maxOffset = offsets[i];
                }
            }
            int scanlineStride = csm.getScanlineStride();
            int pixelStride = csm.getPixelStride();
            
            size = (h - 1) * scanlineStride +
            (w - 1) * pixelStride + maxOffset + 1;

        } else if (sampleModel instanceof TMultiPixelPackedSampleModel) {
            TMultiPixelPackedSampleModel mppsm = 
                (TMultiPixelPackedSampleModel) sampleModel;
            
            int scanlineStride = mppsm.getScanlineStride();
            int dataBitOffset = mppsm.getDataBitOffset();
            int dataType = dataBuffer.getDataType();
            
            size = scanlineStride * h;

            switch (dataType) {
            case TDataBuffer.TYPE_BYTE:
                size += (dataBitOffset + 7) / 8;
                break;
            case TDataBuffer.TYPE_USHORT:
                size += (dataBitOffset + 15) / 16;
                break;
            case TDataBuffer.TYPE_INT:
                size += (dataBitOffset + 31) / 32;
                break;
            }
        } else if (sampleModel instanceof TSinglePixelPackedSampleModel) {
            TSinglePixelPackedSampleModel sppsm = 
                (TSinglePixelPackedSampleModel) sampleModel;
            
            int scanlineStride = sppsm.getScanlineStride();
            size = (h - 1) * scanlineStride + w;
        }
        if (dataBuffer.getSize() < size) {
            // awt.298=dataBuffer is too small
            throw new TRasterFormatException(Messages.getString("awt.298")); //$NON-NLS-1$
        }
    }
}


