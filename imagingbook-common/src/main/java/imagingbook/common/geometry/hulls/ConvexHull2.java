/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.hulls;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import static imagingbook.common.math.Arithmetic.sqr;

public class ConvexHull2 {

    private final List<Pnt2d> vertices;

    /**
     * Constructor, creates a {@link ConvexHull2} instance from an array of {@link Pnt2d} points. At least
     * one point is required.
     *
     * @param points an array of 2D points
     */
    public ConvexHull2(Pnt2d[] points) {
        this(() -> Arrays.stream(points).iterator());
    }

    /**
     * Constructor, creates a {@link ConvexHull2} instance from an {@link Iterable} over {@link Pnt2d}. At least one
     * distinct point is required.
     *
     * @param points an iterator over 2D points
     */
    public ConvexHull2(Iterable<Pnt2d> points) {
        this.vertices = makeConvexHull(points);
    }

    // Compute convex hull using Graham Scan algorithm
    private static List<Pnt2d> makeConvexHull(Iterable<Pnt2d> points) {
        if (!points.iterator().hasNext()) {
            throw new IllegalArgumentException("empty point sequence, at least one input point required");
        }
        // Create a copy to avoid modifying original array
        Pnt2d[] pts;
        if (points instanceof Collection<Pnt2d> collection) {
            pts = collection.toArray(new Pnt2d[0]);
        }
        else {
            List<Pnt2d> list = new ArrayList<>();
            for (Pnt2d p : points) {
                list.add(p);
            }
            pts = list.toArray(new Pnt2d[0]);
        }

        if (pts.length < 3) {
            return Arrays.asList(pts);
        }

        // Find the point with lowest y-coordinate (and leftmost if tie)
        Pnt2d pivot = pts[0];
        int pivotIdx = 0;
        for (int i = 1; i < pts.length; i++) {
            Pnt2d p = pts[i];
            final double pivX = pivot.getX();
            final double pivY = pivot.getY();
            if (p.getY() < pivY || (p.getY() == pivY && p.getX() < pivX)) {
                pivot = p;
                pivotIdx = i;
            }
        }

        // Move pivot to the front
        Pnt2d temp = pts[0];
        pts[0] = pts[pivotIdx];
        pts[pivotIdx] = temp;
        final Pnt2d finalPivot = pivot;

        // Sort points by polar angle with respect to pivot
        Arrays.sort(pts, 1, pts.length, (a, b) -> {
            double cross = crossProduct(finalPivot, a, b);
            if (cross == 0) {
                // Collinear points - sort by distance
                double distA = a.distanceSq(finalPivot);
                double distB = b.distanceSq(finalPivot);;
                return Double.compare(distA, distB);
            }
            return cross > 0 ? -1 : 1;
        });

        // Build the hull using a stack
        Stack<Pnt2d> hull = new Stack<>();
        hull.push(pts[0]);
        hull.push(pts[1]);

        for (int i = 2; i < pts.length; i++) {
            Pnt2d top = hull.pop();

            // Remove points that make clockwise turn
            while (!hull.isEmpty() && crossProduct(hull.peek(), top, pts[i]) <= 0) {
                top = hull.pop();
            }

            hull.push(top);
            hull.push(pts[i]);
        }

        return new ArrayList<>(hull);
    }

    // Compute the cross product of vectors OA and OB
    // Positive = counter-clockwise, Negative = clockwise, 0 = collinear
    private static double crossProduct(Pnt2d O, Pnt2d A, Pnt2d B) {
        return (A.getX() - O.getX()) * (B.getY() - O.getY()) - (A.getY() - O.getY()) * (B.getX() - O.getX());
    }

    /**
     * Returns an array of 2D points on the convex hull (in counter-clockwise order).
     * @return array of 2D points on the convex hull
     */
    public Pnt2d[] getVertices() {
        return vertices.toArray(new Pnt2d[0]);
    }

    // -----------------------------------------------------------------------

    // Example usage
    public static void main(String[] args) {
        List<Pnt2d> points = Arrays.asList(
                PntDouble.from(0, 3),
                PntDouble.from(1, 1),
                PntDouble.from(2, 2),
                PntDouble.from(4, 4),
                PntDouble.from(0, 0),
                PntDouble.from(1, 2),
                PntDouble.from(3, 1),
                PntDouble.from(3, 3)
        );

        System.out.println("Input points:");
        points.forEach(System.out::println);

        ConvexHull2 hullA = new ConvexHull2(points.toArray(new Pnt2d[0]));

        // List<Pnt2d> hull = convexHull2(points));
        Pnt2d[] hull = hullA.getVertices();

        System.out.println("\nConvex hull vertices (counter-clockwise):");
        for(Pnt2d p : hull) {
            System.out.println(p);
        }

    }
}


/*
Input points:
PntDouble[0.000, 3.000]
PntDouble[1.000, 1.000]
PntDouble[2.000, 2.000]
PntDouble[4.000, 4.000]
PntDouble[0.000, 0.000]
PntDouble[1.000, 2.000]
PntDouble[3.000, 1.000]
PntDouble[3.000, 3.000]

Convex hull vertices (counter-clockwise):
PntDouble[0.000, 0.000]
PntDouble[3.000, 1.000]
PntDouble[4.000, 4.000]
PntDouble[0.000, 3.000]
*/
