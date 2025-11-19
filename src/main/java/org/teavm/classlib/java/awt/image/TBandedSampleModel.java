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

import org.apache.harmony.awt.internal.nls.Messages;

public final class TBandedSampleModel extends TComponentSampleModel {

    private static int[] createIndices(int numBands) {
        int indices[] = new int[numBands];
        for (int i = 0; i < numBands; i++) {
            indices[i] = i;
        }
        return indices;
    }

    private static int[] createOffsets(int numBands) {
        int offsets[] = new int[numBands];
        for (int i = 0; i < numBands; i++) {
            offsets[i] = 0;
        }
        return offsets;
    }

    public TBandedSampleModel(int dataType, int w, int h, int numBands) {
        this(dataType, w, h, w, TBandedSampleModel.createIndices(numBands),
                TBandedSampleModel.createOffsets(numBands));
    }

    public TBandedSampleModel(int dataType, int w, int h, int scanlineStride,
            int bankIndices[], int bandOffsets[]) {
        super(dataType, w, h, 1, scanlineStride, bankIndices, bandOffsets);
    }

    @Override
    public TSampleModel createCompatibleSampleModel(int w, int h) {
        return new TBandedSampleModel(dataType, w, h, w, bankIndices,
                bandOffsets);
    }

    @Override
    public TDataBuffer createDataBuffer() {
        TDataBuffer data = null;
        int size = scanlineStride * height;

        switch (dataType) {
        case TDataBuffer.TYPE_BYTE:
            data = new TDataBufferByte(size, numBanks);
            break;
        case TDataBuffer.TYPE_SHORT:
        case TDataBuffer.TYPE_USHORT:
            data = new TDataBufferShort(size, numBanks);
            break;
        case TDataBuffer.TYPE_INT:
            data = new TDataBufferInt(size, numBanks);
            break;
        case TDataBuffer.TYPE_FLOAT:
            data = new TDataBufferFloat(size, numBanks);
            break;
        case TDataBuffer.TYPE_DOUBLE:
            data = new TDataBufferDouble(size, numBanks);
            break;
        }

        return data;

    }

    @Override
    public TSampleModel createSubsetSampleModel(int[] bands) {
        if (bands.length > numBands) {
            // awt.64=The number of the bands in the subset is greater than the number of bands in the sample model
            throw new TRasterFormatException(Messages.getString("awt.64")); //$NON-NLS-1$
        }

        int indices[] = new int[bands.length];
        int offsets[] = new int[bands.length];

        for (int i = 0; i < bands.length; i++) {
            indices[i] = bankIndices[bands[i]];
            offsets[i] = bandOffsets[bands[i]];
        }

        return new TBandedSampleModel(dataType, width, height, scanlineStride,
                indices, offsets);
    }

    @Override
    public Object getDataElements(int x, int y, Object obj, TDataBuffer data) {
        switch (dataType) {
        case TDataBuffer.TYPE_BYTE: {
            byte bdata[];

            if (obj == null) {
                bdata = new byte[numBands];
            } else {
                bdata = (byte[]) obj;
            }

            for (int i = 0; i < numBands; i++) {
                bdata[i] = (byte) getSample(x, y, i, data);
            }

            obj = bdata;
            break;
        }
        case TDataBuffer.TYPE_SHORT:
        case TDataBuffer.TYPE_USHORT: {
            short sdata[];

            if (obj == null) {
                sdata = new short[numBands];
            } else {
                sdata = (short[]) obj;
            }

            for (int i = 0; i < numBands; i++) {
                sdata[i] = (short) getSample(x, y, i, data);
            }

            obj = sdata;
            break;
        }
        case TDataBuffer.TYPE_INT: {
            int idata[];

            if (obj == null) {
                idata = new int[numBands];
            } else {
                idata = (int[]) obj;
            }

            for (int i = 0; i < numBands; i++) {
                idata[i] = getSample(x, y, i, data);
            }

            obj = idata;
            break;
        }
        case TDataBuffer.TYPE_FLOAT: {
            float fdata[];

            if (obj == null) {
                fdata = new float[numBands];
            } else {
                fdata = (float[]) obj;
            }

            for (int i = 0; i < numBands; i++) {
                fdata[i] = getSampleFloat(x, y, i, data);
            }

            obj = fdata;
            break;
        }
        case TDataBuffer.TYPE_DOUBLE: {
            double ddata[];

            if (obj == null) {
                ddata = new double[numBands];
            } else {
                ddata = (double[]) obj;
            }

            for (int i = 0; i < numBands; i++) {
                ddata[i] = getSampleDouble(x, y, i, data);
            }

            obj = ddata;
            break;
        }
        }

        return obj;
    }

    @Override
    public int[] getPixel(int x, int y, int iArray[], TDataBuffer data) {
        int pixel[];
        if (iArray == null) {
            pixel = new int[numBands];
        } else {
            pixel = iArray;
        }

        for (int i = 0; i < numBands; i++) {
            pixel[i] = getSample(x, y, i, data);
        }

        return pixel;
    }

    @Override
    public int getSample(int x, int y, int b, TDataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        return data.getElem(bankIndices[b], y * scanlineStride + x +
               bandOffsets[b]);
    }

    @Override
    public double getSampleDouble(int x, int y, int b, TDataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        return data.getElemDouble(bankIndices[b], y * scanlineStride + x +
               bandOffsets[b]);
    }

    @Override
    public float getSampleFloat(int x, int y, int b, TDataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        return data.getElemFloat(bankIndices[b], y * scanlineStride + x +
               bandOffsets[b]);
    }

    @Override
    public int[] getSamples(int x, int y, int w, int h, int b, int iArray[],
            TDataBuffer data) {
        int samples[];
        int idx = 0;

        if (iArray == null) {
            samples = new int[w * h];
        } else {
            samples = iArray;
        }

        for (int i = y; i < y + h; i++) {
            for (int j = x; j < x + w; j++) {
                samples[idx++] = getSample(j, i, b, data);
            }
        }

        return samples;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        int tmp = hash >>> 8;
        hash <<= 8;
        hash |= tmp;

        return hash ^ 0x55;
    }

    @Override
    public void setDataElements(int x, int y, Object obj, TDataBuffer data) {
        switch (dataType) {
        case TDataBuffer.TYPE_BYTE:
            byte bdata[] = (byte[]) obj;
            for (int i = 0; i < numBands; i++) {
                setSample(x, y, i, bdata[i] & 0xff, data);
            }
            break;

        case TDataBuffer.TYPE_SHORT:
        case TDataBuffer.TYPE_USHORT:
            short sdata[] = (short[]) obj;
            for (int i = 0; i < numBands; i++) {
                setSample(x, y, i, sdata[i] & 0xffff, data);
            }
            break;

        case TDataBuffer.TYPE_INT:
            int idata[] = (int[]) obj;
            for (int i = 0; i < numBands; i++) {
                setSample(x, y, i, idata[i], data);
            }
            break;

        case TDataBuffer.TYPE_FLOAT:
            float fdata[] = (float[]) obj;
            for (int i = 0; i < numBands; i++) {
                setSample(x, y, i, fdata[i], data);
            }
            break;

        case TDataBuffer.TYPE_DOUBLE:
            double ddata[] = (double[]) obj;
            for (int i = 0; i < numBands; i++) {
                setSample(x, y, i, ddata[i], data);
            }
            break;
        }
    }

    @Override
    public void setPixel(int x, int y, int iArray[], TDataBuffer data) {
        for (int i = 0; i < numBands; i++) {
            setSample(x, y, i, iArray[i], data);
        }
    }

    @Override
    public void setPixels(int x, int y, int w, int h, int iArray[],
            TDataBuffer data) {
        int idx = 0;

        for (int i = y; i < y + h; i++) {
            for (int j = x; j < x + w; j++) {
                for (int n = 0; n < numBands; n++) {
                    setSample(j, i, n, iArray[idx++], data);
                }
            }
        }
    }

    @Override
    public void setSample(int x, int y, int b, double s, TDataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        data.setElemDouble(bankIndices[b], y * scanlineStride + x +
               bandOffsets[b], s);
    }

    @Override
    public void setSample(int x, int y, int b, float s, TDataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        data.setElemFloat(bankIndices[b], y * scanlineStride + x +
               bandOffsets[b], s);
    }

    @Override
    public void setSample(int x, int y, int b, int s, TDataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        data.setElem(bankIndices[b], y * scanlineStride + x +
                       bandOffsets[b], s);
    }

    @Override
    public void setSamples(int x, int y, int w, int h, int b, int iArray[],
            TDataBuffer data) {
        int idx = 0;

        for (int i = y; i < y + h; i++) {
            for (int j = x; j < x + w; j++) {
                setSample(j, i, b, iArray[idx++], data);
            }
        }

    }

}

