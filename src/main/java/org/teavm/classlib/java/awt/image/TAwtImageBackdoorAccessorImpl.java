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
 * Created on 23.11.2005
 *
 */
package org.teavm.classlib.java.awt.image;

import org.apache.harmony.awt.gl.AwtImageBackdoorAccessor;
import org.apache.harmony.awt.gl.GLVolatileImage;
import org.apache.harmony.awt.gl.Surface;
import org.apache.harmony.awt.gl.image.DataBufferListener;
import org.apache.harmony.awt.internal.nls.Messages;
import org.teavm.classlib.java.awt.TImage;

/**
 * This class not part of public API. It useful for receiving package private
 * data from other packages.
 */
class TAwtImageBackdoorAccessorImpl extends AwtImageBackdoorAccessor {

    static void init(){
        inst = new TAwtImageBackdoorAccessorImpl();
    }

    @Override
    public Surface getImageSurface(TImage image) {
        if (image instanceof TBufferedImage){
            return ((TBufferedImage)image).getImageSurface();
        } else if (image instanceof GLVolatileImage){
            return ((GLVolatileImage)image).getImageSurface();
        }
        return null;
    }

    @Override
    public boolean isGrayPallete(TIndexColorModel icm){
        return icm.isGrayPallete();
    }

    @Override
    public Object getData(TDataBuffer db) {
        if (db instanceof TDataBufferByte){
            return ((TDataBufferByte)db).getData();
        } else if (db instanceof TDataBufferUShort){
            return ((TDataBufferUShort)db).getData();
        } else if (db instanceof TDataBufferShort){
            return ((TDataBufferShort)db).getData();
        } else if (db instanceof TDataBufferInt){
            return ((TDataBufferInt)db).getData();
        } else if (db instanceof TDataBufferFloat){
            return ((TDataBufferFloat)db).getData();
        } else if (db instanceof TDataBufferDouble){
            return ((TDataBufferDouble)db).getData();
        } else {
            // awt.235=Wrong Data Buffer type : {0}
            throw new IllegalArgumentException(Messages.getString("awt.235", //$NON-NLS-1$
                    db.getClass()));
        }
    }

    @Override
    public int[] getDataInt(TDataBuffer db) {
        if (db instanceof TDataBufferInt){
            return ((TDataBufferInt)db).getData();
        }
        return null;
    }

    @Override
    public byte[] getDataByte(TDataBuffer db) {
        if (db instanceof TDataBufferByte){
            return ((TDataBufferByte)db).getData();
        }
        return null;
    }

    @Override
    public short[] getDataShort(TDataBuffer db) {
        if (db instanceof TDataBufferShort){
            return ((TDataBufferShort)db).getData();
        }
        return null;
    }

    @Override
    public short[] getDataUShort(TDataBuffer db) {
        if (db instanceof TDataBufferUShort){
            return ((TDataBufferUShort)db).getData();
        }
        return null;
    }

    @Override
    public double[] getDataDouble(TDataBuffer db) {
        if (db instanceof TDataBufferDouble){
            return ((TDataBufferDouble)db).getData();
        }
        return null;
    }

    @Override
    public float[] getDataFloat(TDataBuffer db) {
        if (db instanceof TDataBufferFloat){
            return ((TDataBufferFloat)db).getData();
        }
        return null;
    }

    @Override
    public void addTDataBufferListener(TDataBuffer db, DataBufferListener listener) {
        db.addDataBufferListener(listener);
    }

    @Override
    public void removeTDataBufferListener(TDataBuffer db) {
        db.removeDataBufferListener();
    }

    @Override
    public void validate(TDataBuffer db) {
        db.validate();
    }

    @Override
    public void releaseData(TDataBuffer db) {
        db.releaseData();
    }
}
