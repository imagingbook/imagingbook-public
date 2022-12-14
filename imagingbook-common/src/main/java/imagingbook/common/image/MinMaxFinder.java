/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.image;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.util.ClassUtils;
import imagingbook.common.util.SortedVector;
import imagingbook.core.resource.ImageResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MinMaxFinder {

    private final int W, H;
    private final float threshold;
    private final int borderWidth;
    private final FloatProcessor fp;
    private final float[] fpixels;
    private final float[] nh = new float[9];

    public MinMaxFinder(ImageProcessor ip, double threshold, int borderWidth) {
        this.W = ip.getWidth();
        this.H = ip.getHeight();
        this.fp = ip.convertToFloatProcessor();
        this.fpixels = (float[]) this.fp.getPixels();
        this.threshold = (float) threshold;
        this.borderWidth = Math.max(1, borderWidth);
    }

    public Pnt2d[] getLocalMaxima() {
        return null;
    }

    public ExtremalPoint[] getLocalMaxima(int maxCount) {
        Comparator<ExtremalPoint> cprt = ClassUtils.getComparator(ExtremalPoint.class);
        SortedVector<ExtremalPoint> ep = new SortedVector(new ExtremalPoint[maxCount], cprt);
        for (int v = borderWidth; v < H - borderWidth; v++) {
            for (int u = borderWidth; u < W - borderWidth; u++) {
                if (getNeighborhood(u, v) && isLocalMax(threshold)) {
                    ep.insert(new ExtremalPoint(u, v, nh[0]));
                }
            }
        }
        return ep.getArray();
    }

    public ExtremalPoint[] getLocalMinima(int maxCount) {
        Comparator<ExtremalPoint> cprt = ClassUtils.getComparator(ExtremalPoint.class).reversed();
        SortedVector<ExtremalPoint> ep = new SortedVector(new ExtremalPoint[maxCount], cprt);
        for (int v = borderWidth; v < H - borderWidth; v++) {
            for (int u = borderWidth; u < W - borderWidth; u++) {
                if (getNeighborhood(u, v) && isLocalMin(threshold)) {
                    ep.insert(new ExtremalPoint(u, v, nh[0]));
                }
            }
        }
        return ep.getArray();
    }

    /**
     * Fills the neighborhood array s as follows:
     * 	s4 s3 s2
     *  s5 s0 s1
     *  s6 s7 s8
     *  Returns true of the neighborhood could be filled completely,
     *  false otherwise.
     */
    private boolean getNeighborhood(int u, int v) {
        if (u <= 0 || u >= W - 1 || v <= 0 || v >= H - 1) {
            return false;
        }
        else {
            final float[] q = this.fpixels;
            final int i0 = (v - 1) * W + u;
            final int i1 = v * W + u;
            final int i2 = (v + 1) * W + u;
            nh[0] = q[i1];
            nh[1] = q[i1 + 1];
            nh[2] = q[i0 + 1];
            nh[3] = q[i0];
            nh[4] = q[i0 - 1];
            nh[5] = q[i1 - 1];
            nh[6] = q[i2 - 1];
            nh[7] = q[i2];
            nh[8] = q[i2 + 1];
            return true;
        }
    }

    private boolean isLocalMax(float threshold) {
        if (nh == null) {
            return false;
        }
        else {
            final float nh0 = nh[0] - threshold;
            return	// check 8 neighbors of q0
                    nh0 > nh[4] && nh0 > nh[3] && nh0 > nh[2] &&
                    nh0 > nh[5] &&                nh0 > nh[1] &&
                    nh0 > nh[6] && nh0 > nh[7] && nh0 > nh[8] ;
        }
    }

    private boolean isLocalMin(float threshold) {
        if (nh == null) {
            return false;
        }
        else {
            final float nh0 = nh[0] + threshold;
            return	// check 8 neighbors of q0
                    nh0 < nh[4] && nh0 < nh[3] && nh0 < nh[2] &&
                    nh0 < nh[5] &&                nh0 < nh[1] &&
                    nh0 < nh[6] && nh0 < nh[7] && nh0 < nh[8] ;
        }
    }

    // A point with a score value (q) attached.
    class ExtremalPoint extends Pnt2d.PntInt implements Comparable<ExtremalPoint> {
        private final float q;

        protected ExtremalPoint(int x, int y, float q) {
            super(x, y);
            this.q = q;
        }

        @Override
        public int compareTo(ExtremalPoint other) {
            // sort by decreasing score, strongest first
            return Float.compare(other.q, this.q);
        }

        @Override
        public String toString() {
            return super.toString() + " q=" + this.q;
        }
    }

    class MaxComparator implements Comparator<ExtremalPoint> {
        @Override
        public int compare(ExtremalPoint o1, ExtremalPoint o2) {
            return Float.compare(o2.q, o1.q);
        }
    }

    class MinComparator implements Comparator<ExtremalPoint> {
        @Override
        public int compare(ExtremalPoint o1, ExtremalPoint o2) {
            return Float.compare(o1.q, o1.q);
        }
    }

    // ----------------------------------------------------------------




}
