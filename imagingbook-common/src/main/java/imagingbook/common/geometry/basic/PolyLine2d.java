/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an open-ended, immutable sequence of 2D points.
 */
public class PolyLine2d extends AbstractPoly2d {

    public PolyLine2d(List<Pnt2d> pnts) {
        super(pnts);
    }

    public PolyLine2d(Pnt2d... pnts) {
        super(pnts);
    }

    public PolyLine2d duplicate() {
        return new PolyLine2d(pnts);
    }

    public PolyLine2d reverse() {
        PolyLine2d poly = new PolyLine2d(pnts);
        poly.reversePoints();
        return poly;
    }

    // -----------------------------------------------------------------------

    /**
     * For testing.
     * @param coords a sequence of x/y coordinate pairs
     * @return
     */
    public static PolyLine2d makePolygon(double... coords) {
        List<Pnt2d> pntList = new ArrayList<>();
        for (int i = 0; i < coords.length; i+=2) {
            pntList.add(Pnt2d.from(coords[i], coords[i + 1]));
        }
        return new PolyLine2d(pntList);
    }
    /**
     * For testing.
     * @param coords a Nx2 array of x/y coordinate pairs
     * @return
     */
    public static PolyLine2d makePolygon(double[][] coords) {
        List<Pnt2d> pntList = new ArrayList<>();
        for (int i = 0; i < coords.length; i++) {
            pntList.add(Pnt2d.from(coords[i][0], coords[i][1]));
        }
        return new PolyLine2d(pntList);
    }

    // -----------------------------------------------------------------------

    public double getLength() {
        final int n = pnts.length;
        double len = 0;
        for (int i = 1; i < n; i++) {
            len += pnts[i-1].distance(pnts[i]);
        }
        return len;
    }

    // --------------------------------------------------------------------------------------------

    public Polygon2d simplify(double tol) {
        List<Pnt2d> pts = this.getPnts();
        final double tol2 = tol * tol;
        final int n = pts.size();
        if (n <= 3)
            return new Polygon2d(this);

        // idxs is the list of simplified point indexes:
        List<Integer> idxs = simplify(tol, false);

        // Collect points of the simplified polygon
        List<Pnt2d> simpl = new ArrayList<>();
        for (int i : idxs) {
            simpl.add(pts.get(i));
        }

        return new Polygon2d(simpl);
    }


    // -----------------------------------------------------------------------
    //
    // public static void main(String[] args) {
    //     PolyLine2d poly = new PolyLine2d(Pnt2d.from(1, 4), Pnt2d.from(23, -6));
    //      for (Pnt2d pnt : poly) {
    //          System.out.println(pnt);
    //      }
    // }
}
