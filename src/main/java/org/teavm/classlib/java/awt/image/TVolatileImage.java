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
package org.teavm.classlib.java.awt.image;

import org.teavm.classlib.java.awt.TGraphics;
import org.teavm.classlib.java.awt.TGraphics2D;
import org.teavm.classlib.java.awt.TGraphicsConfiguration;
import org.teavm.classlib.java.awt.TImageCapabilities;
import org.teavm.classlib.java.awt.TTransparency;
import org.teavm.classlib.java.awt.TImage;

/**
 * Volatile image implementation
 */
public abstract class TVolatileImage extends TImage /* Volatile image implements TTransparency since 1.5 */ implements TTransparency {
    /***************************************************************************
    *
    *  Constants
    *
    ***************************************************************************/

    public static final int IMAGE_INCOMPATIBLE = 2;

    public static final int IMAGE_OK = 0;

    public static final int IMAGE_RESTORED = 1;

    protected int transparency = OPAQUE;

    /***************************************************************************
    *
    *  Constructors
    *
    ***************************************************************************/

    public TVolatileImage() {
        super();
    }



    /***************************************************************************
    *
    *  Abstract methods
    *
    ***************************************************************************/

    public abstract boolean contentsLost();

    public abstract TGraphics2D createTGraphics();

    public abstract TImageCapabilities getCapabilities();

    public abstract int getHeight();

    public abstract TBufferedImage getSnapshot();

    public abstract int getWidth();

    public abstract int validate(TGraphicsConfiguration gc);


    /***************************************************************************
    *
    *  Public methods
    *
    ***************************************************************************/

    @Override
    public void flush() {
    }

    @Override
    public TGraphics getGraphics() {
        return createTGraphics();
    }

    @Override
    public TImageProducer getSource() {
        return getSnapshot().getSource();
    }

    public int getTTransparency() {
        return transparency;
    }
}
