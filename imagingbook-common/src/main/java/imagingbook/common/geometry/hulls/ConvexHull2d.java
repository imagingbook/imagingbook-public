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
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.geometry.shape.ShapeProducer;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Stack;


/**
 * <p>
 * This convex hull implementation uses the Graham Scan algorithm, which has O(n log n) time complexity.
 * Here's what it does:
 *
 * 1. Finds the pivot point: The point with the lowest y-coordinate (leftmost if there's a tie)
 * 2. Sorts by polar angle: All other points are sorted by their angle relative to the pivot
 * 3. Builds the hull: Uses a stack to build the hull by:
 * - Adding points one by one:
 * - Removing points that create a clockwise turn (using cross product)
 * - Keeping only points that maintain a counter-clockwise orientation
 * </p>
 *
 * @author WB (with quite some help from Claude/Sonnet 4.5)
 * @version 2025/10/29
 */
public class ConvexHull2d implements ShapeProducer {

    private final List<Pnt2d> vertices;

    /**
     * Constructor, creates a {@link ConvexHull2d} instance from an array of {@link Pnt2d} points. At least
     * one point is required.
     *
     * @param points an array of 2D points
     */
    public ConvexHull2d(Pnt2d[] points) {
        this(() -> Arrays.stream(points).iterator());
    }

    /**
     * Constructor, creates a {@link ConvexHull2d} instance from an {@link Iterable} over {@link Pnt2d}. At least one
     * distinct point is required.
     *
     * @param points an iterator over 2D points
     */
    public ConvexHull2d(Iterable<Pnt2d> points) {
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

    // --------------------------------------------------------------------

    @Override
    public Shape getShape(double scale) {
        Pnt2d p = vertices.get(0);
        if (vertices.size() < 2) {	// degenerate case (single point)
            return p.getShape(scale);
        }
        else {
            Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);

            path.moveTo(p.getX(), p.getY());
            for (int i = 1; i < vertices.size(); i++) {
                p = vertices.get(i);
                path.lineTo(p.getX(), p.getY());
            }
            path.closePath();
            return path;
        }
    }

    // --------------------------------------------------------------------

    public static final double DefaultContainsTolerance = 1e-12;

    public boolean contains(Pnt2d p) {
        if (vertices.size() == 1 && p.equals(vertices.get(0))) {
            return true;
        }
        else {
            return contains(p, DefaultContainsTolerance);
        }
    }

    /**
     * Checks if this convex hull contains the specified point. This method is used instead of
     * {@link Path2D#contains(double, double)} to avoid false results due to roundoff errors.
     *
     * @param p some 2D point
     * @param tolerance positive quantity for being outside
     * @return true if the point is inside the hull
     */
    public boolean contains(Pnt2d p, double tolerance) {
        final int n = vertices.size();
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            AlgebraicLine line = AlgebraicLine.from(vertices.get(i), vertices.get(j));
            double dist = line.getSignedDistance(p);
            // positive signed distance means that the point is to the left
            if (dist + tolerance < 0) {
                return false;
            }
        }
        return true;
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

        ConvexHull2d hullA = new ConvexHull2d(points.toArray(new Pnt2d[0]));

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
