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
package org.teavm.classlib.java.awt;

import org.teavm.classlib.java.awt.geom.TPoint2D;
import org.teavm.classlib.java.awt.geom.TRectangle2D;
import org.teavm.classlib.java.awt.geom.TAffineTransform;
import org.teavm.classlib.java.awt.geom.TPathIterator;

/**
 * Shape
 *
 */
public interface TShape {
    public boolean contains(double x, double y);

    public boolean contains(double x, double y, double w, double h);

    public boolean contains(TPoint2D point);

    public boolean contains(TRectangle2D r);

    public TRectangle getBounds();

    public TRectangle2D getBounds2D();

    public TPathIterator getPathIterator(TAffineTransform at);

    public TPathIterator getPathIterator(TAffineTransform at, double flatness);

    public boolean intersects(double x, double y, double w, double h);

    public boolean intersects(TRectangle2D r);
}
