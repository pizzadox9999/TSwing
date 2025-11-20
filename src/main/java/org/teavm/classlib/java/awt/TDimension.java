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
 * @author Denis M. Kishenko
 */
package org.teavm.classlib.java.awt;

import org.teavm.classlib.java.awt.geom.TDimension2D;

import java.io.Serializable;

import org.apache.harmony.misc.HashCode;


public class TDimension extends TDimension2D implements Serializable {

    private static final long serialVersionUID = 4723952579491349524L;

    public int width;
    public int height;

    public TDimension(TDimension d) {
        this(d.width, d.height);
    }

    public TDimension() {
        this(0, 0);
    }

    public TDimension(int width, int height) {
        setSize(width, height);
    }

    @Override
    public int hashCode() {
        HashCode hash = new HashCode();
        hash.append(width);
        hash.append(height);
        return hash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof TDimension) {
            TDimension d = (TDimension)obj;
            return (d.width == width && d.height == height);
        }
        return false;
    }

    @Override
    public String toString() {
        // The output format based on 1.5 release behaviour. It could be obtained in the following way
        // System.out.println(new Dimension().toString())
        return getClass().getName() + "[width=" + width + ",height=" + height + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setSize(TDimension d) {
        setSize(d.width, d.height);
    }

    @Override
    public void setSize(double width, double height) {
        setSize((int)Math.ceil(width), (int)Math.ceil(height));
    }

    public TDimension getSize() {
        return new TDimension(width, height);
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public double getWidth() {
        return width;
    }

}

