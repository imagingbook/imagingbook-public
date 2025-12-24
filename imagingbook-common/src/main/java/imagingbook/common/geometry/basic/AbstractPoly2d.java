/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.basic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import static imagingbook.common.math.Arithmetic.sqr;

public abstract class AbstractPoly2d implements Iterable<Pnt2d> {

    final Pnt2d[] pnts;


    public AbstractPoly2d(List<Pnt2d> pnts) {
        if (pnts.size() < 2)
            throw new IllegalArgumentException("at least 2 points required");
        this.pnts = pnts.toArray(new Pnt2d[0]);
    }

    public AbstractPoly2d(Pnt2d[] pnts) {
        if (pnts.length < 2)
            throw new IllegalArgumentException("at least 2 points required");
        this.pnts = pnts.clone();
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the number of points.
     * @return the number of points
     */
    public int length() {
        return pnts.length;
    }

    public List<Pnt2d> getPnts() {
        return Arrays.asList(pnts);
    }

    public Pnt2d getPnt(int idx) {
        return pnts[idx];
    }

    // package private!
    void reversePoints() {
        Collections.reverse(Arrays.asList(pnts));
    }

    // // non-public!!
    // Pnt2d[] getPntsArray() {
    //     return pnts;
    // }

    // ------------------------------------------------------------------------

    /**
     * The centroid of all point coordinates
     * @return centroid of points
     */
    public Pnt2d getCentroid() {
        int n = pnts.length;
        double cx = 0, cy = 0;
        for (var p : pnts) {
            cx += p.getX();
            cy += p.getY();
        }
        return Pnt2d.from(cx / n,  cy / n);
    }

    // ------------------------------------------------------------------------

    @Override
    public Iterator<Pnt2d> iterator() {
        return Arrays.stream(pnts).iterator();
    }

    // ------------------------------------------------------------------------

    /**
     *
     * @param rotatedPoly rotated polygon
     * @param tol
     * @return
     */
    static List<Pnt2d> simplify(List<Pnt2d> rotatedPoly, double tol, boolean closed) {
        // TODO: convert to array access!
        final double tol2 = tol * tol;
        final int n = rotatedPoly.size();
        if (n <= 3)
            return new ArrayList<>(rotatedPoly);

        // // Rotate the polygon such that the designated start point comes first:
        // List<Pnt2d> rotatedPoly = new ArrayList<>(n + 1);
        // for (int i = 0; i < n; i++)
        //     rotatedPoly.add(pts.get((startPt + i) % n));

        // Standard DP stack
        boolean[] keep = new boolean[n];
        keep[0] = true;             // always keep the first point
        keep[n - 1] = !closed;      // keep last point if open (polyline)

        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{0, n - 1});

        while (!stack.isEmpty()) {
            int[] seg = stack.pop();
            int i0 = seg[0], i1 = seg[1];

            Pnt2d A = rotatedPoly.get(i0);
            Pnt2d B = rotatedPoly.get(i1);

            double maxDist2 = -1;
            int indexMax = -1;

            for (int i = i0 + 1; i < i1; i++) {
                double d2 = perpDistSq(rotatedPoly.get(i), A, B);
                if (d2 > maxDist2) {
                    maxDist2 = d2;
                    indexMax = i;
                }
            }

            if (maxDist2 > tol2) {
                keep[indexMax] = true;
                stack.push(new int[]{i0, indexMax});
                stack.push(new int[]{indexMax, i1});
            }
        }

        // Assemble the simplified rotated polygon
        List<Pnt2d> simp = new ArrayList<>();
        for (int i = 0; i < n; i++)
            if (keep[i]) {
                simp.add(rotatedPoly.get(i));
            }

        // At this moment the first point on the contour is likely a corner,
        // but this is not guaranteed.

        // Rotate back
        List<Pnt2d> out = new ArrayList<>();

        // find index of first corner in original point sequence
        int offset = simp.indexOf(rotatedPoly.get(0));

        int m = simp.size();
        for (int i = 0; i < m; i++) {
            out.add(simp.get((offset + i) % m));
        }

        return out;
    }

    // Squared perpendicular distance from P to line AB
    private static double perpDistSq(Pnt2d P, Pnt2d A, Pnt2d B) {
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


}
