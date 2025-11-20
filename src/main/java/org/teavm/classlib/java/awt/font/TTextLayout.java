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

package org.teavm.classlib.java.awt.font;


import org.teavm.classlib.java.awt.TFont;
import org.teavm.classlib.java.awt.TGraphics2D;
import org.teavm.classlib.java.awt.TShape;
import org.teavm.classlib.java.awt.geom.TAffineTransform;
import org.teavm.classlib.java.awt.geom.TRectangle2D;
import org.teavm.classlib.java.awt.geom.TGeneralPath;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Map;

import org.apache.harmony.awt.gl.font.BasicMetrics;
import org.apache.harmony.awt.gl.font.CaretManager;
import org.apache.harmony.awt.gl.font.TextMetricsCalculator;
import org.apache.harmony.awt.gl.font.TextRunBreaker;
import org.apache.harmony.awt.internal.nls.Messages;

public final class TTextLayout implements Cloneable {

    public static class CaretPolicy {

        public CaretPolicy() {
            // Nothing to do
        }

        public TTextHitInfo getStrongCaret(TTextHitInfo hit1, TTextHitInfo hit2, TTextLayout layout) {
            // Stronger hit is the one with greater level.
            // If the level is same, leading edge is stronger.

            int level1 = layout.getCharacterLevel(hit1.getCharIndex());
            int level2 = layout.getCharacterLevel(hit2.getCharIndex());

            if (level1 == level2) {
                return (hit2.isLeadingEdge() && (!hit1.isLeadingEdge())) ? hit2 : hit1;
            }
            return level1 > level2 ? hit1 : hit2;
        }

    }

    public static final TTextLayout.CaretPolicy DEFAULT_CARET_POLICY = new CaretPolicy();

    private TextRunBreaker breaker;
    private boolean metricsValid = false;
    private TextMetricsCalculator tmc;
    private BasicMetrics metrics;
    private CaretManager caretManager;
    float justificationWidth = -1;

    public TTextLayout(String string, TFont font, TFontRenderContext frc) {
        if (string == null){
            // awt.01='{0}' parameter is null
            throw new IllegalArgumentException(Messages.getString("awt.01", "string")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if (font == null){
            // awt.01='{0}' parameter is null
            throw new IllegalArgumentException(Messages.getString("awt.01", "font")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (string.length() == 0){
            // awt.02='{0}' parameter has zero length
            throw new IllegalArgumentException(Messages.getString("awt.02", "string")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        AttributedString as = new AttributedString(string);
        as.addAttribute(TTextAttribute.FONT, font);
        this.breaker = new TextRunBreaker(as.getIterator(), frc);
        caretManager = new CaretManager(breaker);
    }

    public TTextLayout(
            String string,
            Map<? extends java.text.AttributedCharacterIterator.Attribute, ?> attributes,
            TFontRenderContext frc ) {
        if (string == null){
            // awt.01='{0}' parameter is null
            throw new IllegalArgumentException(Messages.getString("awt.01", "string")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if (attributes == null){
            // awt.01='{0}' parameter is null
            throw new IllegalArgumentException(Messages.getString("awt.01", "attributes")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if (string.length() == 0){
            // awt.02='{0}' parameter has zero length
            throw new IllegalArgumentException(Messages.getString("awt.02", "string")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        
        AttributedString as = new AttributedString(string);
        as.addAttributes(attributes, 0, string.length());
        this.breaker = new TextRunBreaker(as.getIterator(), frc);
        caretManager = new CaretManager(breaker);
    }

    public TTextLayout(AttributedCharacterIterator text, TFontRenderContext frc) {
        if (text == null){
            // awt.03='{0}' iterator parameter is null
            throw new IllegalArgumentException(Messages.getString("awt.03", "text")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if (text.getBeginIndex() == text.getEndIndex()){
            // awt.04='{0}' iterator parameter has zero length
            throw new IllegalArgumentException(Messages.getString("awt.04", "text")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        this.breaker = new TextRunBreaker(text, frc);
        caretManager = new CaretManager(breaker);
    }

    TTextLayout(TextRunBreaker breaker) {
        this.breaker = breaker;
        caretManager = new CaretManager(this.breaker);
    }

    @Override
    public int hashCode() {
        return breaker.hashCode();
    }

    @Override
    protected Object clone() {
        TTextLayout res = new TTextLayout((TextRunBreaker) breaker.clone());

        if (justificationWidth >= 0) {
            res.handleJustify(justificationWidth);
        }

        return res;
    }

    public boolean equals(TTextLayout layout) {
        if (layout == null) {
            return false;
        }
        return this.breaker.equals(layout.breaker);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TTextLayout ? equals((TTextLayout) obj) : false;
    }

    @Override
    public String toString() { // what for?
        return super.toString();
    }

    public void draw(TGraphics2D g2d, float x, float y) {
        updateMetrics();
        breaker.drawSegments(g2d, x ,y);
    }

    private void updateMetrics() {
        if (!metricsValid) {
            breaker.createAllSegments();
            tmc = new TextMetricsCalculator(breaker);
            metrics = tmc.createMetrics();
            metricsValid = true;
        }
    }

    public float getAdvance() {
        updateMetrics();
        return metrics.getAdvance();
    }

    public float getAscent() {
        updateMetrics();
        return metrics.getAscent();
    }

    public byte getBaseline() {
        updateMetrics();
        return (byte) metrics.getBaseLineIndex();
    }

    public float[] getBaselineOffsets() {
        updateMetrics();
        return tmc.getBaselineOffsets();
    }

    public TShape getBlackBoxBounds(int firstEndpoint, int secondEndpoint) {
        updateMetrics();
        if (firstEndpoint < secondEndpoint) {
            return breaker.getBlackBoxBounds(firstEndpoint, secondEndpoint);
        }
        return breaker.getBlackBoxBounds(secondEndpoint, firstEndpoint);
    }

    public TRectangle2D getBounds() {
        updateMetrics();
        return breaker.getVisualBounds();
    }

    public float[] getCaretInfo(TTextHitInfo hitInfo) {
        updateMetrics();
        return caretManager.getCaretInfo(hitInfo);
    }

    public float[] getCaretInfo(TTextHitInfo hitInfo, TRectangle2D bounds) {
        updateMetrics();
        return caretManager.getCaretInfo(hitInfo);
    }

    public TShape getCaretTShape(TTextHitInfo hitInfo, TRectangle2D bounds) {
        updateMetrics();
        return caretManager.getCaretShape(hitInfo, this);
    }

    public TShape getCaretTShape(TTextHitInfo hitInfo) {
        updateMetrics();
        return caretManager.getCaretShape(hitInfo, this);
    }

    public TShape[] getCaretTShapes(int offset) {
        return getCaretTShapes(offset, null, TTextLayout.DEFAULT_CARET_POLICY);
    }

    public TShape[] getCaretTShapes(int offset, TRectangle2D bounds) {
        return getCaretTShapes(offset, bounds, TTextLayout.DEFAULT_CARET_POLICY);
    }

    public TShape[] getCaretTShapes(int offset, TRectangle2D bounds, TTextLayout.CaretPolicy policy) {
        if (offset < 0 || offset > breaker.getCharCount()) {
            // awt.195=Offset is out of bounds
            throw new IllegalArgumentException(Messages.getString("awt.195")); //$NON-NLS-1$
        }

        updateMetrics();
        return caretManager.getCaretShapes(offset, bounds, policy, this);
    }

    public int getCharacterCount() {
        return breaker.getCharCount();
    }

    public byte getCharacterLevel(int index) {
        if (index == -1 || index == getCharacterCount()) {
            return (byte) breaker.getBaseLevel();
        }
        return breaker.getLevel(index);
    }

    public float getDescent() {
        updateMetrics();
        return metrics.getDescent();
    }

    public TTextLayout getJustifiedLayout(float justificationWidth) throws Error {
        float justification = breaker.getJustification();

        if (justification < 0) {
            // awt.196=Justification impossible, layout already justified
            throw new Error(Messages.getString("awt.196")); //$NON-NLS-1$
        } else if (justification == 0) {
            return this;
        }

        TTextLayout justifiedLayout = new TTextLayout((TextRunBreaker) breaker.clone());
        justifiedLayout.handleJustify(justificationWidth);
        return justifiedLayout;
    }

    public float getLeading() {
        updateMetrics();
        return metrics.getLeading();
    }

    public TShape getLogicalHighlightTShape(int firstEndpoint, int secondEndpoint) {
        updateMetrics();
        return getLogicalHighlightTShape(firstEndpoint, secondEndpoint, breaker.getLogicalBounds());
    }

    public TShape getLogicalHighlightTShape(
            int firstEndpoint,
            int secondEndpoint,
            TRectangle2D bounds
    ) {
        updateMetrics();

        if (firstEndpoint > secondEndpoint) {
            if (secondEndpoint < 0 || firstEndpoint > breaker.getCharCount()) {
                // awt.197=Endpoints are out of range
                throw new IllegalArgumentException(Messages.getString("awt.197")); //$NON-NLS-1$
            }
            return caretManager.getLogicalHighlightShape(
                    secondEndpoint,
                    firstEndpoint,
                    bounds,
                    this
            );
        }
        if (firstEndpoint < 0 || secondEndpoint > breaker.getCharCount()) {
            // awt.197=Endpoints are out of range
            throw new IllegalArgumentException(Messages.getString("awt.197")); //$NON-NLS-1$
        }
        return caretManager.getLogicalHighlightShape(
                firstEndpoint,
                secondEndpoint,
                bounds,
                this
        );
    }

    public int[] getLogicalRangesForVisualSelection(TTextHitInfo hit1, TTextHitInfo hit2) {
        return caretManager.getLogicalRangesForVisualSelection(hit1, hit2);
    }

    public TTextHitInfo getNextLeftHit(int offset) {
        return getNextLeftHit(offset, DEFAULT_CARET_POLICY);
    }

    public TTextHitInfo getNextLeftHit(TTextHitInfo hitInfo) {
        breaker.createAllSegments();
        return caretManager.getNextLeftHit(hitInfo);
    }

    public TTextHitInfo getNextLeftHit(int offset, TTextLayout.CaretPolicy policy) {
        if (offset < 0 || offset > breaker.getCharCount()) {
            // awt.195=Offset is out of bounds
            throw new IllegalArgumentException(Messages.getString("awt.195")); //$NON-NLS-1$
        }

        TTextHitInfo hit = TTextHitInfo.afterOffset(offset);
        TTextHitInfo strongHit = policy.getStrongCaret(hit, hit.getOtherHit(), this);
        TTextHitInfo nextLeftHit = getNextLeftHit(strongHit);

        if (nextLeftHit != null) {
            return policy.getStrongCaret(getVisualOtherHit(nextLeftHit), nextLeftHit, this);
        }
        return null;
    }

    public TTextHitInfo getNextRightHit(TTextHitInfo hitInfo) {
        breaker.createAllSegments();
        return caretManager.getNextRightHit(hitInfo);
    }

    public TTextHitInfo getNextRightHit(int offset) {
        return getNextRightHit(offset, DEFAULT_CARET_POLICY);
    }

    public TTextHitInfo getNextRightHit(int offset, TTextLayout.CaretPolicy policy) {
        if (offset < 0 || offset > breaker.getCharCount()) {
            // awt.195=Offset is out of bounds
            throw new IllegalArgumentException(Messages.getString("awt.195")); //$NON-NLS-1$
        }

        TTextHitInfo hit = TTextHitInfo.afterOffset(offset);
        TTextHitInfo strongHit = policy.getStrongCaret(hit, hit.getOtherHit(), this);
        TTextHitInfo nextRightHit = getNextRightHit(strongHit);

        if (nextRightHit != null) {
            return policy.getStrongCaret(getVisualOtherHit(nextRightHit), nextRightHit, this);
        }
        return null;
    }

    public TShape getOutline(TAffineTransform xform) {
        breaker.createAllSegments();

        TGeneralPath outline = breaker.getOutline();

        if (outline != null && xform != null) {
            outline.transform(xform);
        }

        return outline;
    }

    public float getVisibleAdvance() {
        updateMetrics();

        // Trailing whitespace _SHOULD_ be reordered (Unicode spec) to
        // base direction, so it is also trailing
        // in logical representation. We use this fact.
        int lastNonWhitespace = breaker.getLastNonWhitespace();

        if (lastNonWhitespace < 0) {
            return 0;
        } else if (lastNonWhitespace == getCharacterCount()-1) {
            return getAdvance();
        } else if (justificationWidth >= 0) { // Layout is justified
            return justificationWidth;
        } else {
            breaker.pushSegments(
                    breaker.getACI().getBeginIndex(),
                    lastNonWhitespace + breaker.getACI().getBeginIndex() + 1
            );

            breaker.createAllSegments();

            float visAdvance = tmc.createMetrics().getAdvance();

            breaker.popSegments();
            return visAdvance;
        }
    }

    public TShape getVisualHighlightTShape(TTextHitInfo hit1, TTextHitInfo hit2, TRectangle2D bounds) {
        return caretManager.getVisualHighlightShape(hit1, hit2, bounds, this);
    }

    public TShape getVisualHighlightTShape(TTextHitInfo hit1, TTextHitInfo hit2) {
        breaker.createAllSegments();
        return caretManager.getVisualHighlightShape(hit1, hit2, breaker.getLogicalBounds(), this);
    }

    public TTextHitInfo getVisualOtherHit(TTextHitInfo hitInfo) {
        return caretManager.getVisualOtherHit(hitInfo);
    }

    protected void handleJustify(float justificationWidth) {
        float justification = breaker.getJustification();

        if (justification < 0) {
            // awt.196=Justification impossible, layout already justified
            throw new IllegalStateException(Messages.getString("awt.196")); //$NON-NLS-1$
        } else if (justification == 0) {
            return;
        }

        float gap = (justificationWidth - getVisibleAdvance()) * justification;
        breaker.justify(gap);
        this.justificationWidth = justificationWidth;

        // Correct metrics
        tmc = new TextMetricsCalculator(breaker);
        tmc.correctAdvance(metrics);
    }

    public TTextHitInfo hitTestChar(float x, float y) {
        return hitTestChar(x, y, getBounds());
    }

    public TTextHitInfo hitTestChar(float x, float y, TRectangle2D bounds) {
        if (x > bounds.getMaxX()) {
            return breaker.isLTR() ?
                    TTextHitInfo.trailing(breaker.getCharCount() - 1) : TTextHitInfo.leading(0);
        }

        if (x < bounds.getMinX()) {
            return breaker.isLTR() ?
                    TTextHitInfo.leading(0) : TTextHitInfo.trailing(breaker.getCharCount() - 1);
        }

        return breaker.hitTest(x, y);
    }

    public boolean isLeftToRight() {
        return breaker.isLTR();
    }

    public boolean isVertical() {
        return false;
    }
}

