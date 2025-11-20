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
package org.teavm.classlib.java.awt.geom;

import java.util.NoSuchElementException;

import org.apache.harmony.awt.gl.Crossing;
import org.apache.harmony.awt.internal.nls.Messages;
import org.teavm.classlib.java.awt.TRectangle;
import org.teavm.classlib.java.awt.TShape;

public abstract class TQuadCurve2D implements TShape, Cloneable {

    public static class TFloat extends TQuadCurve2D {

        public float x1;
        public float y1;
        public float ctrlx;
        public float ctrly;
        public float x2;
        public float y2;

        public TFloat() {
        }

        public TFloat(float x1, float y1, float ctrlx, float ctrly, float x2, float y2) {
            setCurve(x1, y1, ctrlx, ctrly, x2, y2);
        }

        @Override
        public double getX1() {
            return x1;
        }

        @Override
        public double getY1() {
            return y1;
        }

        @Override
        public double getCtrlX() {
            return ctrlx;
        }

        @Override
        public double getCtrlY() {
            return ctrly;
        }

        @Override
        public double getX2() {
            return x2;
        }

        @Override
        public double getY2() {
            return y2;
        }

        @Override
        public TPoint2D getP1() {
            return new TPoint2D.TFloat(x1, y1);
        }

        @Override
        public TPoint2D getCtrlPt() {
            return new TPoint2D.TFloat(ctrlx, ctrly);
        }

        @Override
        public TPoint2D getP2() {
            return new TPoint2D.TFloat(x2, y2);
        }

        @Override
        public void setCurve(double x1, double y1, double ctrlx, double ctrly, double x2, double y2) {
            this.x1 = (float)x1;
            this.y1 = (float)y1;
            this.ctrlx = (float)ctrlx;
            this.ctrly = (float)ctrly;
            this.x2 = (float)x2;
            this.y2 = (float)y2;
        }

        public void setCurve(float x1, float y1, float ctrlx, float ctrly, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.ctrlx = ctrlx;
            this.ctrly = ctrly;
            this.x2 = x2;
            this.y2 = y2;
        }

        public TRectangle2D getBounds2D() {
            float rx0 = Math.min(Math.min(x1, x2), ctrlx);
            float ry0 = Math.min(Math.min(y1, y2), ctrly);
            float rx1 = Math.max(Math.max(x1, x2), ctrlx);
            float ry1 = Math.max(Math.max(y1, y2), ctrly);
            return new TRectangle2D.TFloat(rx0, ry0, rx1 - rx0, ry1 - ry0);
        }
    }

    public static class TDouble extends TQuadCurve2D {

        public double x1;
        public double y1;
        public double ctrlx;
        public double ctrly;
        public double x2;
        public double y2;

        public TDouble() {
        }

        public TDouble(double x1, double y1, double ctrlx, double ctrly, double x2, double y2) {
            setCurve(x1, y1, ctrlx, ctrly, x2, y2);
        }

        @Override
        public double getX1() {
            return x1;
        }

        @Override
        public double getY1() {
            return y1;
        }

        @Override
        public double getCtrlX() {
            return ctrlx;
        }

        @Override
        public double getCtrlY() {
            return ctrly;
        }

        @Override
        public double getX2() {
            return x2;
        }

        @Override
        public double getY2() {
            return y2;
        }

        @Override
        public TPoint2D getP1() {
            return new TPoint2D.TDouble(x1, y1);
        }

        @Override
        public TPoint2D getCtrlPt() {
            return new TPoint2D.TDouble(ctrlx, ctrly);
        }

        @Override
        public TPoint2D getP2() {
            return new TPoint2D.TDouble(x2, y2);
        }

        @Override
        public void setCurve(double x1, double y1, double ctrlx, double ctrly, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.ctrlx = ctrlx;
            this.ctrly = ctrly;
            this.x2 = x2;
            this.y2 = y2;
        }

        public TRectangle2D getBounds2D() {
            double rx0 = Math.min(Math.min(x1, x2), ctrlx);
            double ry0 = Math.min(Math.min(y1, y2), ctrly);
            double rx1 = Math.max(Math.max(x1, x2), ctrlx);
            double ry1 = Math.max(Math.max(y1, y2), ctrly);
            return new TRectangle2D.TDouble(rx0, ry0, rx1 - rx0, ry1 - ry0);
        }
    }

    /*
     * QuadCurve2D path iterator 
     */
    class Iterator implements TPathIterator {

        /**
         * The source QuadCurve2D object
         */
        TQuadCurve2D c;

        /**
         * The path iterator transformation
         */
        TAffineTransform t;

        /**
         * The current segmenet index
         */
        int index;

        /**
         * Constructs a new QuadCurve2D.Iterator for given line and transformation
         * @param q - the source QuadCurve2D object
         * @param at - the AffineTransform object to apply rectangle path
         */
        Iterator(TQuadCurve2D q, TAffineTransform t) {
            this.c = q;
            this.t = t;
        }

        public int getWindingRule() {
            return WIND_NON_ZERO;
        }

        public boolean isDone() {
            return (index > 1);
        }

        public void next() {
            index++;
        }

        public int currentSegment(double[] coords) {
            if (isDone()) {
                // awt.4B=Iterator out of bounds
                throw new NoSuchElementException(Messages.getString("awt.4B")); //$NON-NLS-1$
            }
            int type;
            int count;
            if (index == 0) {
                type = SEG_MOVETO;
                coords[0] = c.getX1();
                coords[1] = c.getY1();
                count = 1;
            } else {
                type = SEG_QUADTO;
                coords[0] = c.getCtrlX();
                coords[1] = c.getCtrlY();
                coords[2] = c.getX2();
                coords[3] = c.getY2();
                count = 2;
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, count);
            }
            return type;
        }

        public int currentSegment(float[] coords) {
            if (isDone()) {
                // awt.4B=Iterator out of bounds
                throw new NoSuchElementException(Messages.getString("awt.4B")); //$NON-NLS-1$
            }
            int type;
            int count;
            if (index == 0) {
                type = SEG_MOVETO;
                coords[0] = (float)c.getX1();
                coords[1] = (float)c.getY1();
                count = 1;
            } else {
                type = SEG_QUADTO;
                coords[0] = (float)c.getCtrlX();
                coords[1] = (float)c.getCtrlY();
                coords[2] = (float)c.getX2();
                coords[3] = (float)c.getY2();
                count = 2;
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, count);
            }
            return type;
        }

    }

    protected TQuadCurve2D() {
    }

    public abstract double getX1();

    public abstract double getY1();

    public abstract TPoint2D getP1();

    public abstract double getCtrlX();

    public abstract double getCtrlY();

    public abstract TPoint2D getCtrlPt();

    public abstract double getX2();

    public abstract double getY2();

    public abstract TPoint2D getP2();

    public abstract void setCurve(double x1, double y1, double ctrlx, double ctrly, double x2, double y2);

    public void setCurve(TPoint2D p1, TPoint2D cp, TPoint2D p2) {
        setCurve(p1.getX(), p1.getY(), cp.getX(), cp.getY(), p2.getX(), p2.getY());
    }

    public void setCurve(double[] coords, int offset) {
        setCurve(
                coords[offset + 0], coords[offset + 1],
                coords[offset + 2], coords[offset + 3],
                coords[offset + 4], coords[offset + 5]);
    }

    public void setCurve(TPoint2D[] points, int offset) {
        setCurve(
                points[offset + 0].getX(), points[offset + 0].getY(),
                points[offset + 1].getX(), points[offset + 1].getY(),
                points[offset + 2].getX(), points[offset + 2].getY());
    }

    public void setCurve(TQuadCurve2D curve) {
        setCurve(
                curve.getX1(), curve.getY1(),
                curve.getCtrlX(), curve.getCtrlY(),
                curve.getX2(), curve.getY2());
    }

    public double getFlatnessSq() {
        return TLine2D.ptSegDistSq(
                getX1(), getY1(),
                getX2(), getY2(),
                getCtrlX(), getCtrlY());
    }

    public static double getFlatnessSq(double x1, double y1, double ctrlx, double ctrly, double x2, double y2) {
        return TLine2D.ptSegDistSq(x1, y1, x2, y2, ctrlx, ctrly);
    }

    public static double getFlatnessSq(double coords[], int offset) {
        return TLine2D.ptSegDistSq(
                coords[offset + 0], coords[offset + 1],
                coords[offset + 4], coords[offset + 5],
                coords[offset + 2], coords[offset + 3]);
    }

    public double getFlatness() {
        return TLine2D.ptSegDist(getX1(), getY1(), getX2(), getY2(), getCtrlX(), getCtrlY());
    }

    public static double getFlatness(double x1, double y1, double ctrlx,
            double ctrly, double x2, double y2)
    {
        return TLine2D.ptSegDist(x1, y1, x2, y2, ctrlx, ctrly);
    }

    public static double getFlatness(double coords[], int offset) {
        return TLine2D.ptSegDist(
                coords[offset + 0], coords[offset + 1],
                coords[offset + 4], coords[offset + 5],
                coords[offset + 2], coords[offset + 3]);
    }

    public void subdivide(TQuadCurve2D left, TQuadCurve2D right) {
        subdivide(this, left, right);
    }

    public static void subdivide(TQuadCurve2D src, TQuadCurve2D left, TQuadCurve2D right) {
        double x1 = src.getX1();
        double y1 = src.getY1();
        double cx = src.getCtrlX();
        double cy = src.getCtrlY();
        double x2 = src.getX2();
        double y2 = src.getY2();
        double cx1 = (x1 + cx) / 2.0;
        double cy1 = (y1 + cy) / 2.0;
        double cx2 = (x2 + cx) / 2.0;
        double cy2 = (y2 + cy) / 2.0;
        cx = (cx1 + cx2) / 2.0;
        cy = (cy1 + cy2) / 2.0;
        if (left != null) {
            left.setCurve(x1, y1, cx1, cy1, cx, cy);
        }
        if (right != null) {
            right.setCurve(cx, cy, cx2, cy2, x2, y2);
        }
    }

    public static void subdivide(double src[], int srcoff, double left[],
            int leftOff, double right[], int rightOff)
    {
        double x1 = src[srcoff + 0];
        double y1 = src[srcoff + 1];
        double cx = src[srcoff + 2];
        double cy = src[srcoff + 3];
        double x2 = src[srcoff + 4];
        double y2 = src[srcoff + 5];
        double cx1 = (x1 + cx) / 2.0;
        double cy1 = (y1 + cy) / 2.0;
        double cx2 = (x2 + cx) / 2.0;
        double cy2 = (y2 + cy) / 2.0;
        cx = (cx1 + cx2) / 2.0;
        cy = (cy1 + cy2) / 2.0;
        if (left != null) {
            left[leftOff + 0] = x1;
            left[leftOff + 1] = y1;
            left[leftOff + 2] = cx1;
            left[leftOff + 3] = cy1;
            left[leftOff + 4] = cx;
            left[leftOff + 5] = cy;
        }
        if (right != null) {
            right[rightOff + 0] = cx;
            right[rightOff + 1] = cy;
            right[rightOff + 2] = cx2;
            right[rightOff + 3] = cy2;
            right[rightOff + 4] = x2;
            right[rightOff + 5] = y2;
        }
    }

    public static int solveQuadratic(double eqn[]) {
        return solveQuadratic(eqn, eqn);
    }

    public static int solveQuadratic(double eqn[], double res[]) {
        return Crossing.solveQuad(eqn, res);
    }

    public boolean contains(double px, double py) {
        return Crossing.isInsideEvenOdd(Crossing.crossShape(this, px, py));
    }

    public boolean contains(double rx, double ry, double rw, double rh) {
        int cross = Crossing.intersectShape(this, rx, ry, rw, rh);
        return cross != Crossing.CROSSING && Crossing.isInsideEvenOdd(cross);
    }

    public boolean intersects(double rx, double ry, double rw, double rh) {
        int cross = Crossing.intersectShape(this, rx, ry, rw, rh);
        return cross == Crossing.CROSSING || Crossing.isInsideEvenOdd(cross);
    }

    public boolean contains(TPoint2D p) {
        return contains(p.getX(), p.getY());
    }

    public boolean intersects(TRectangle2D r) {
        return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public boolean contains(TRectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public TRectangle getBounds() {
        return getBounds2D().getBounds();
    }

    public TPathIterator getPathIterator(TAffineTransform t) {
        return new Iterator(this, t);
    }

    public TPathIterator getPathIterator(TAffineTransform t, double flatness) {
        return new TFlatteningPathIterator(getPathIterator(t), flatness);
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

}

