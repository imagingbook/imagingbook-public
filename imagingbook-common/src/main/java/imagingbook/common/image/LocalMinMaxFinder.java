/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image;

import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.util.ClassUtils;
import imagingbook.common.util.SortedVector;

import java.util.Comparator;
import java.util.Locale;

/**
 * <p>
 * Provides methods for detecting local maxima or minima in images or 2D data arrays. A local maximum (minimum) is
 * defined as a pixel position whose value is larger (smaller) than all its eight neighboring values. The detection is
 * controlled by a 'threshold' parameter specifying the minimum difference between the center value and the surrounding
 * values. There is also a 'borderWidth' parameter specifying the outer image margins to be ignored. This implementation
 * deliberately provides separate methods {@link #getMaxima(int)} and {@link #getMinima(int)} for detecting maxima and
 * minim, respectively. An alternative would be to have only one method and invert the data to obtain the other, but
 * inversion of float-data is not well defined and we wanted the original data values preserved with the returned point
 * sets. (This may change eventually.)
 * </p>
 * <p>
 * Note that this form of local extremum detection is simple but may fail on ridge points or locally flat spots. See
 * {@link ij.plugin.filter.MaximumFinder} for a more robust (but also more complicated) implementation.
 * </p>
 */
public class LocalMinMaxFinder {

    private final int W, H;
    private final float threshold;
    private final int borderWidth;
    private final float[][] fpixels;
    private final float[] nh = new float[9];

    /**
     * Constructor accepting a 2D data array of type float.
     *
     * @param fpixels the 2D data array
     * @param threshold the minimum difference between a center value and its 8 surrounding values to be accepted as a
     * local extremum
     * @param borderWidth width of border margin to be ignored (&gt; 0)
     */
    public LocalMinMaxFinder(float[][] fpixels, double threshold, int borderWidth) {
        this.fpixels = fpixels;
        this.W = fpixels.length;
        this.H = fpixels[0].length;
        this.threshold = (float) threshold;
        this.borderWidth = Math.max(1, borderWidth);
    }

    /**
     * Constructor accepting an {@link ImageProcessor} instance. Color images are converted to scalar float values for
     * extremum detection.
     *
     * @param ip the input image
     * @param threshold the minimum difference between a center value and its 8 surrounding values to be accepted as a
     * local extremum
     * @param borderWidth width of border margin to be ignored
     */
    public LocalMinMaxFinder(ImageProcessor ip, double threshold, int borderWidth) {
        this(ip.getFloatArray(), threshold, borderWidth);
    }

    /**
     * Constructor accepting an {@link ImageProcessor} instance using default parameters.
     * @param ip the input image
     */
    public LocalMinMaxFinder(ImageProcessor ip) {
        this(ip, 0.0, 0);
    }


    // --------------------------------------------------------------------

    /**
     * Returns the detected local maxima as an array of type {@link ExtremalPoint}. The resulting array is sorted such
     * that the position with the maximum score value ({@link ExtremalPoint#q}) comes first. The array is never larger
     * than the specified count.
     *
     * @param maxCount the maximum number of extrema to search for
     * @return a sorted array of {@link ExtremalPoint} instances
     */
    public ExtremalPoint[] getMaxima(int maxCount) {
        Comparator<ExtremalPoint> cprt = ClassUtils.getComparator(ExtremalPoint.class);
        SortedVector<ExtremalPoint> sv = new SortedVector(new ExtremalPoint[maxCount], cprt);
        for (int v = borderWidth; v < H - borderWidth; v++) {
            for (int u = borderWidth; u < W - borderWidth; u++) {
                if (getNeighborhood(u, v) && isLocalMax(threshold)) {
                    ExtremalPoint p = new ExtremalPoint(u, v, nh[0]);
                    sv.add(p);
                    // System.out.println("adding max " + p + ": " + Arrays.toString(sv.getArray()));
                }
            }
        }
        return sv.getArray();
    }

    /**
     * Returns the detected local minima as an array of type {@link ExtremalPoint}. The resulting array is sorted such
     * that the position with the minimum score value ({@link ExtremalPoint#q}) comes first. The array is never larger
     * than the specified count.
     *
     * @param maxCount the maximum number of extrema to search for
     * @return a sorted array of {@link ExtremalPoint} instances
     */
    public ExtremalPoint[] getMinima(int maxCount) {
        Comparator<ExtremalPoint> cprt = ClassUtils.getComparator(ExtremalPoint.class).reversed();
        SortedVector<ExtremalPoint> sv = new SortedVector(new ExtremalPoint[maxCount], cprt);
        for (int v = borderWidth; v < H - borderWidth; v++) {
            for (int u = borderWidth; u < W - borderWidth; u++) {
                if (getNeighborhood(u, v) && isLocalMin(threshold)) {
                    ExtremalPoint p = new ExtremalPoint(u, v, nh[0]);
                    sv.add(p);
                    // System.out.println("adding min " + p + ": " + Arrays.toString(sv.getArray()));
                }
            }
        }
        return sv.getArray();
    }

    /**
     * Fills the neighborhood array s as follows:
     * <pre>
     * 	s4 s3 s2
     *  s5 s0 s1
     *  s6 s7 s8
     * </pre>
     * Returns true of the neighborhood could be filled completely, false otherwise.
     */
    private boolean getNeighborhood(int u, int v) {
        if (u <= 0 || u >= W - 1 || v <= 0 || v >= H - 1) {
            return false;
        }
        else {
            final float[][] q = this.fpixels;
            nh[0] = q[u][v];
            nh[1] = q[u+1][v];
            nh[2] = q[u+1][v-1];
            nh[3] = q[u][v-1];
            nh[4] = q[u-1][v-1];
            nh[5] = q[u-1][v];
            nh[6] = q[u-1][v+1];
            nh[7] = q[u][v+1];
            nh[8] = q[u+1][v+1];
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

    // ----------------------------------------------------------------------

    /**
     * A 2D point with integer coordinates and a score value ({@link #q}) attached.
     */
    public static class ExtremalPoint extends Pnt2d.PntInt implements Comparable<ExtremalPoint> {

        /** The score value. */
        public final float q;

        /**
         * Constructor.
         * @param x horizontal position
         * @param y vertical position
         * @param q score value
         */
        public ExtremalPoint(int x, int y, float q) {
            super(x, y);
            this.q = q;
        }

        @Override
        public int compareTo(ExtremalPoint other) {
            // sort by increasing score value
            return Float.compare(this.q, other.q);
        }

        private static final String ClassName = ExtremalPoint.class.getSimpleName();

        @Override
        public String toString() {
            return String.format(Locale.US, "%s[%d, %d, q=%.3f]", ClassName, x, y, q);
        }
    }

}
