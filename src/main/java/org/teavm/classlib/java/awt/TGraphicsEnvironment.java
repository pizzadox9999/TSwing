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
 * @author Oleg V. Khaschansky
 */

package org.teavm.classlib.java.awt;

import java.util.Locale;

import org.apache.harmony.awt.ContextStorage;
import org.apache.harmony.awt.gl.CommonGraphics2DFactory;


public abstract class TGraphicsEnvironment {

    static Boolean isHeadless;

    protected TGraphicsEnvironment() {}

    public static TGraphicsEnvironment getLocalGraphicsEnvironment() {
        synchronized(ContextStorage.getContextLock()) {
            if (ContextStorage.getGraphicsEnvironment() == null) {
                if (isHeadless()) {
                    ContextStorage.setGraphicsEnvironment(new THeadlessGraphicsEnvironment());
                } else {
                    final CommonGraphics2DFactory g2df =
                        (CommonGraphics2DFactory) TToolkit.getDefaultToolkit().getGraphicsFactory();

                    ContextStorage.setGraphicsEnvironment(
                            g2df.createGraphicsEnvironment(ContextStorage.getWindowFactory())
                    );
                }
            }

            return ContextStorage.getGraphicsEnvironment();
        }
    }

    public boolean isHeadlessInstance() {
        return false;
    }

    public static boolean isHeadless() {
        if (isHeadless == null) {
            isHeadless = Boolean.valueOf(org.apache.harmony.awt.Utils.getSystemProperty("java.awt.headless")); //$NON-NLS-1$ 
        }

        return isHeadless.booleanValue();
    }

    public TRectangle getMaximumWindowBounds() throws THeadlessException {
        return getDefaultScreenDevice().getDefaultConfiguration().getBounds();
    }

    public TPoint getCenterPoint() throws THeadlessException {
        final TRectangle mwb = getMaximumWindowBounds();
        return new TPoint(mwb.width >> 1, mwb.height >> 1);
    }

    public void preferLocaleFonts() {
        // Note: API specification says following:
        // "The actual change in font rendering behavior resulting
        // from a call to this method is implementation dependent;
        // it may have no effect at all." So, doing nothing is an
        // acceptable behavior for this method.

        // For now FontManager uses 1.4 font.properties scheme for font mapping, so
        // this method doesn't make any sense. The implementation of this method
        // which will influence font mapping is postponed until
        // 1.5 mapping scheme not implemented.

        // todo - Implement non-default behavior with 1.5 font mapping scheme
    }

    public void preferProportionalFonts() {
        // Note: API specification says following:
        // "The actual change in font rendering behavior resulting
        // from a call to this method is implementation dependent;
        // it may have no effect at all." So, doing nothing is an
        // acceptable behavior for this method.

        // For now FontManager uses 1.4 font.properties scheme for font mapping, so
        // this method doesn't make any sense. The implementation of this method
        // which will influence font mapping is postponed until
        // 1.5 mapping scheme not implemented.

        // todo - Implement non-default behavior with 1.5 font mapping scheme
    }

    public abstract TGraphics2D createGraphics(BufferedImage bufferedImage);

    public abstract TFont[] getAllFonts();

    public abstract String[] getAvailableFontFamilyNames();

    public abstract String[] getAvailableFontFamilyNames(Locale locale);

    public abstract TGraphicsDevice getDefaultScreenDevice() throws THeadlessException;

    public abstract TGraphicsDevice[] getScreenDevices() throws THeadlessException;
}
