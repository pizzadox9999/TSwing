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


package org.apache.harmony.awt.gl;

import org.apache.harmony.awt.gl.image.DataBufferListener;
import org.teavm.classlib.java.awt.TImage;
import org.teavm.classlib.java.awt.image.TDataBuffer;
import org.teavm.classlib.java.awt.image.TDataBufferInt;
import org.teavm.classlib.java.awt.image.TIndexColorModel;

/**
 * This class give an opportunity to get access to private data of 
 * some java.awt.image classes 
 * Implementation of this class placed in java.awt.image package
 */

public abstract class AwtImageBackdoorAccessor {

    static protected AwtImageBackdoorAccessor inst;

    public static AwtImageBackdoorAccessor getInstance(){
        // First we need to run the static initializer in the TDataBuffer class to resolve inst.
        new TDataBufferInt(0);
        return inst;
    }

    public abstract Surface getImageSurface(TImage image);
    public abstract boolean isGrayPallete(TIndexColorModel icm);

    public abstract Object getData(TDataBuffer db);
    public abstract int[] getDataInt(TDataBuffer db);
    public abstract byte[] getDataByte(TDataBuffer db);
    public abstract short[] getDataShort(TDataBuffer db);
    public abstract short[] getDataUShort(TDataBuffer db);
    public abstract double[] getDataDouble(TDataBuffer db);
    public abstract float[] getDataFloat(TDataBuffer db);
    public abstract void releaseData(TDataBuffer db);
    
    public abstract void addTDataBufferListener(TDataBuffer db, DataBufferListener listener);
    public abstract void removeTDataBufferListener(TDataBuffer db);
    public abstract void validate(TDataBuffer db);
}
