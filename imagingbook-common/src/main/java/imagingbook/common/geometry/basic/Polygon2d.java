/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.basic;

import java.awt.Shape;
import java.util.Collections;
import java.util.List;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;

/**
 * Represents a closed, immutable sequence of 2D points, its last point
 * connecting to its first. Most functionality is identical to parent class
 * {@link PolyLine2d} except for methods assuming closedness.
 */
public class Polygon2d extends AbstractPointSequence {

    public Polygon2d(List<Pnt2d> pnts) {
        super(pnts);
    }

    public Polygon2d(Pnt2d... pnts) {
        super(pnts);
    }

    public Polygon2d(AbstractPointSequence poly) {
        this(poly.pnts); // array is cloned!
    }

    public Polygon2d duplicate() {
        return new Polygon2d(pnts);
    }

    /**
     * Reverses the order of vertices but keeps the first point in place.
     * That is, if the initial polygon points were (0, 1, 2, ..., n-1) the points of the reversed
     * polygon are (0, n-1, ..., 2, 1).
     * @return a new polygon with point order reversed
     */
    public Polygon2d reverse() {
        Polygon2d copy = this.duplicate();
        for (int i = 1; i < pnts.length; i++) {
            copy.pnts[i] = pnts[pnts.length - i];
        }
        //dup.reverseD();
        return copy;
    }

    /**
     * Rotates the vertices of this polygon by the specified distance.
     * See {@link Collections#rotate(List, int)}.
     * @param distance the distance to be rotated
     * @return the rotated polygon
     */
    public Polygon2d rotate(int distance) {
        Polygon2d rotated  = this.duplicate();
        rotated.rotateD(distance);
        return rotated;
    }

    // -----------------------------------------------------------------------

    public double getLength() {
        final int n = pnts.length;
        double len = 0;
        for (int i = 0; i < n; i++) {
            len += pnts[i].distance(pnts[(i + 1) % n]);
        }
        return len;
    }

    /**
     * Calculates the signed area using the Shoelace (Gauss) formula.
     * Positive = Counter-Clockwise, Negative = Clockwise.
     * See also {@link #getArea()}.
     * @return the signed area of this closed polygon
     */
    public double getSignedArea() {
        double area = 0.0;
        int n = pnts.length;
        for (int i = 0; i < n; i++) {
            Pnt2d cur = pnts[i];
            Pnt2d nxt = pnts[(i + 1) % n];
            area += (cur.getX() * nxt.getY()) - (nxt.getX() * cur.getY());
        }
        return area / 2.0;
    }

    /**
     * Polygon area calculation from vertices (Gaussian formula).
     * See also {@link #getSignedArea()}.
     * @return the area of this closed polygon
     */
    public double getArea() {
        return Math.abs(getSignedArea());
    }

    /**
     * Returns true if the vertices of this polygon are arranged in clockwise (CW) order
     * when viewed in <strong>screen coordinates</strong> (Y-axis running downward).
     * This means that it runs counter-clockwise (CCW) in the standard Cartesian coordinates.
     * @return true if clockwise (in screen coordinates)
     */
    public boolean isClockwiseOnScreen() {
        return getSignedArea() > 0;
    }

    /**
     * Checks if the supplied closed polygon is convex.
     * If not convex, 0 is returned. Otherwise, the associated winding order is
     * returned, that is, -1 for CCW and 1 for CW order.
     * @return 0 if non-convex, 1 or -1 otherwise
     */
    public int getConvexity() {
        List<Pnt2d> polygon = this.getPntList();
        int n = polygon.size();
        // if (n < 4) return true; // triangles always convex (but we may want to know winding rule)
        if (n < 2) return 0;    // single points and lines are not convex

        double sign = 0;

        for (int i = 0; i < n; i++) {
            Pnt2d a = polygon.get(i);
            Pnt2d b = polygon.get((i + 1) % n);
            Pnt2d c = polygon.get((i + 2) % n);
            double cross =
                    (b.getX() - a.getX()) * (c.getY() - b.getY()) -
                    (b.getY() - a.getY()) * (c.getX() - b.getX());

            if (cross == 0) continue; // collinear → ignore

            if (sign == 0) { // sign still undetermined
                sign = Math.signum(cross);
            } else if (Math.signum(cross) != sign) {
                return 0; // turn direction changed → concave
            }
        }
        return (int) sign;
    }

    public double getCircularity() {
        double area = getArea();
        double len =  this.getLength();
        if (isZero(len)) {
            throw new ArithmeticException("zero polygon length encountered");
        }
        return 4 * Math.PI * area / sqr(len);
    }

    // --------------------------------------------------------------------------------------------

    public List<Integer> getSimplifiedCorners(double tol) {
        return getSimplifiedCorners(tol, true);
    }

    public Polygon2d simplify(double tol) {
        List<Integer> idxs = this.getSimplifiedCorners(tol);
        return new Polygon2d(getPntList(idxs));
    }

    // -----------------------------------------------------------------------

    @Override
    public Shape getShape(double scale) {
        // scale is ignored
        return getShape(scale, true);
    }

    // equality -----------------------------------

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Polygon2d other) {
            return super.equals(other);
        }
        return false;
    }


}
