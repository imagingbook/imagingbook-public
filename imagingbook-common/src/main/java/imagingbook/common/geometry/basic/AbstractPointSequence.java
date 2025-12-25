/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.basic;

import imagingbook.common.geometry.shape.ShapeProducer;
import imagingbook.common.util.bits.BitVector;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * An ordered, immutable sequence of 2D points. Mainly serves as super class to
 * {@link PolyLine2d} and {@link Polygon2d}.
 */
public abstract class AbstractPointSequence implements Iterable<Pnt2d>, ShapeProducer {

    final Pnt2d[] pnts;

    public AbstractPointSequence(List<Pnt2d> pnts) {
        if (pnts.size() < 2)
            throw new IllegalArgumentException("at least 2 points required");
        this.pnts = pnts.toArray(new Pnt2d[0]);
    }

    public AbstractPointSequence(Pnt2d[] pnts) {
        if (pnts.length < 2)
            throw new IllegalArgumentException("at least 2 points required");
        this.pnts = pnts.clone();
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the number of points in this sequence.
     * @return the number of points
     */
    public int length() {
        return pnts.length;
    }

    /**
     * Returns the points in this sequence as list of {@link Pnt2d} instances.
     * @return the list of points
     */
    public List<Pnt2d> getPntList() {
        return Arrays.asList(pnts);
    }

    /**
     * Returns a list of only those points whose indexes are specified, in the
     * order of the indexes.
     * @param idxs the indexes of the requested points (duplicates allowed)
     * @return a list of selected points.
     */
    public List<Pnt2d> getPntList(List<Integer> idxs) {
        List<Pnt2d> selectedPts = new ArrayList<>();
        for (int i : idxs) {
            selectedPts.add(pnts[i]);
        }
        return selectedPts;
    }

    /**
     * Returns a reference to the specified point.
     * @param idx the point index
     * @return a {@link Pnt2d} instance
     */
    public Pnt2d getPnt(int idx) {
        return pnts[idx];
    }

    /**
     * Reverses this point sequence destructively. Non-public, only used by internal methods.
     */
    void reverseD() {
        Collections.reverse(Arrays.asList(pnts));
    }

    /**
     * Rotate this point sequence destructively. Non-public, only used by internal methods.
     */
    void rotateD(int distance) {
        Collections.rotate(Arrays.asList(pnts), distance);     // modifies the underlying array!
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the 2D centroid of all point coordinates.
     * @return centroid of all points as a {@link Pnt2d} instance
     */
    public Pnt2d getCentroid() {
        return PntUtils.centroid(pnts);
    }

    // ------------------------------------------------------------------------

    @Override
    public Iterator<Pnt2d> iterator() {
        return Arrays.stream(pnts).iterator();
    }

    // ------------------------------------------------------------------------

    /**
     * Simplifies the supplied polyline or closed polygon and returns a list of point indexes for
     * the simplified sequence. Indexes (and not the points themselves) are returned for more
     * flexible use. Indexes are used to extract the final point sequence from {@code poly}.
     *
     * @param tol the allowed point distance from the current segment
     * @return indexes of points in the simplified sequence
     */
    List<Integer> getSimplifiedCorners(double tol, boolean closed) {
        record Segment(int start, int end) {}
        double tol2 = sqr(tol);
        int n = pnts.length;
        if (n <= 3) {
            throw new IllegalArgumentException("at least 4 points required");
        }

        // Standard DP stack
        BitVector keep = new BitVector(n);  // mark surviving points
        keep.setBit(0, true);           // always keep the first point
        keep.setBit(n-1, !closed);      // keep last point if open (polyline)

        Deque<Segment> segmentStack = new ArrayDeque<>();
        segmentStack.push(new Segment(0, n-1));

        while (!segmentStack.isEmpty()) {
            Segment seg = segmentStack.pop();
            int i0 = seg.start, i1 = seg.end;
            Pnt2d A = pnts[i0];
            Pnt2d B = pnts[i1];
            double maxDist2 = -1;
            int maxIndex = -1;

            for (int i = i0 + 1; i < i1; i++) {
                double d2 = perpDistSq(A, B, pnts[i]);
                if (d2 > maxDist2) {
                    maxDist2 = d2;
                    maxIndex = i;
                }
            }

            if (maxDist2 > tol2) {
                // keep[maxIndex] = true;
                keep.setBit(maxIndex, true);
                segmentStack.push(new Segment(i0, maxIndex));
                segmentStack.push(new Segment(maxIndex, i1));
            }
        }

        // Assemble the list of simplified point indexes
        List<Integer> simplIdxs = new ArrayList<>();
        for (int i = 0; i < n; i++)
            if (keep.getBit(i)) {              // (keep[i])
                simplIdxs.add(i);
            }

        return simplIdxs;
    }

    // Squared perpendicular distance from P to line AB
    private static double perpDistSq(Pnt2d A, Pnt2d B, Pnt2d P) {
        final double ax = A.getX(), ay = A.getY();
        final double bx = B.getX(), by = B.getY();
        final double px = P.getX(), py = P.getY();
        double dx = bx - ax;
        double dy = by - ay;
        if (dx == 0 && dy == 0) {
            return P.distanceSq(A);
        }
        // Project point onto line segment, clamped to [0,1]
        double t = ((px - ax) * dx + (py - ay) * dy) / (dx*dx + dy*dy);
        t = Math.max(0, Math.min(1, t));
        double projX = ax + t * dx;
        double projY = ay + t * dy;
        // return Math.hypot(px - projX, py - projY);
        return sqr(px - projX) + sqr(py - projY);
    }

    // ------------------------------------------------------------------------

    /**
     * Shared internal method (non-public).
     * @param scale
     * @param closed
     * @return
     */
    Shape getShape(double scale, boolean closed) {
        // scale is ignored
        Path2D path = new Path2D.Float();
        if (pnts.length > 1) {
            path.moveTo(pnts[0].getX(), pnts[0].getY());
            for (int i = 1; i < pnts.length; i++) {
                path.lineTo(pnts[i].getX(),  pnts[i].getY());
            }
            if (closed) path.closePath();
        }
        else {	// special case: mark a single point region "X"
            double x = pnts[0].getX();
            double y = pnts[0].getY();
            path.moveTo(x - 0.5, y - 0.5);
            path.lineTo(x + 0.5, y + 0.5);
            path.moveTo(x - 0.5, y + 0.5);
            path.lineTo(x + 0.5, y - 0.5);
        }
        return path;
    }

    // ------------------------------------------------------------------------


    /**
     * For testing.
     * @param coords a sequence of x/y coordinate pairs
     * @return
     */
    public static List<Pnt2d> makePntList(double... coords) {
        List<Pnt2d> pntList = new ArrayList<>();
        for (int i = 0; i < coords.length; i+=2) {
            pntList.add(Pnt2d.from(coords[i], coords[i + 1]));
        }
        return pntList;
    }

    /**
     * For testing.
     * @param coords a Nx2 array of x/y coordinate pairs
     * @return
     */
    public static List<Pnt2d> makePntList(double[][] coords) {
        List<Pnt2d> pntList = new ArrayList<>();
        for (int i = 0; i < coords.length; i++) {
            pntList.add(Pnt2d.from(coords[i][0], coords[i][1]));
        }
        return pntList;
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("[");
        for (Pnt2d p : pnts) {
            sb.append(String.format(Locale.US, "[%.2f, %.2f], ", p.getX(), p.getY()));
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AbstractPointSequence other) {
            if (this.length() != other.length())
                return false;
            for (int i = 0; i < this.length(); i++) {
                if (!this.pnts[i].isCloseTo(other.pnts[i]))
                    return false;
            }
            return true;
        }
        return false;
    }

}
