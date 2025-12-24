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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
