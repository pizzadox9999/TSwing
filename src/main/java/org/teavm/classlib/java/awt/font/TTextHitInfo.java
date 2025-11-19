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
/*
 * @author Oleg V. Khaschansky
 */

package org.teavm.classlib.java.awt.font;

import org.apache.harmony.misc.HashCode;


public final class TTextHitInfo {
    private int charIdx; // Represents character index in the line
    private boolean isTrailing;

    private TTextHitInfo(int idx, boolean isTrailing) {
        charIdx = idx;
        this.isTrailing = isTrailing;
    }

    @Override
    public String toString() {
        return new String(
                "TextHitInfo[" + charIdx + ", " + //$NON-NLS-1$ //$NON-NLS-2$
                (isTrailing?"Trailing":"Leading") + "]" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TTextHitInfo) {
            return equals((TTextHitInfo) obj);
        }
        return false;
    }

    public boolean equals(TTextHitInfo thi) {
        return
                thi != null &&
                thi.charIdx == charIdx &&
                thi.isTrailing == isTrailing;
    }

    public TTextHitInfo getOffsetHit(int offset) {
        return new TTextHitInfo(charIdx + offset, isTrailing);
    }

    public TTextHitInfo getOtherHit() {
        return isTrailing ?
                new TTextHitInfo(charIdx+1, false) :
                new TTextHitInfo(charIdx-1, true);
    }

    public boolean isLeadingEdge() {
        return !isTrailing;
    }

    @Override
    public int hashCode() {
        return HashCode.combine(charIdx, isTrailing);
    }

    public int getInsertionIndex() {
        return isTrailing ? charIdx+1 : charIdx;
    }

    public int getCharIndex() {
        return charIdx;
    }

    public static TTextHitInfo trailing(int charIndex) {
        return new TTextHitInfo(charIndex, true);
    }

    public static TTextHitInfo leading(int charIndex) {
        return new TTextHitInfo(charIndex, false);
    }

    public static TTextHitInfo beforeOffset(int offset) {
        return new TTextHitInfo(offset-1, true);
    }

    public static TTextHitInfo afterOffset(int offset) {
        return new TTextHitInfo(offset, false);
    }
}
