/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.mappings.linear;

import imagingbook.common.geometry.basic.Pnt2d;
import org.apache.commons.math4.legacy.linear.ArrayRealVector;
import org.apache.commons.math4.legacy.linear.DecompositionSolver;
import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.RealVector;
import org.apache.commons.math4.legacy.linear.SingularValueDecomposition;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Linear similarity mapping which consists of a rotation, uniform scaling
 * and translation. The transformation matrix is (in homogeneous coordinates)
 * <pre>
 *     | a -b  tx |
 * H = | b  a  ty |
 *     | 0  0  1  |
 * </pre>
 * with a = s * cos(theta), b = s * sin(theta).
 * Two corresponding point pairs are sufficient to specify
 * to 4 parameters a, b, ty, ty.
 *
 */
public class SimilarityMapping2D  extends AffineMapping2D {

    /**
     * Constructor
     * @param a =  a00 = s * cos(theta)
     * @param b =  a10 = s * sin(theta)
     * @param tx = a02
     * @param ty = a12
     */
    public SimilarityMapping2D(double a, double b, double tx, double ty) {
        super(a, -b, tx, b, a, ty);
    }

    public static SimilarityMapping2D fromScaleAndAngle(double s, double theta, double tx, double ty) {
        return new SimilarityMapping2D(s * cos(theta), s * sin(theta), tx, ty);
    }

    /**
     * Creates a 2D similarity mapping from two sequences of corresponding points.
     * If 2 point pairs are specified, the mapping is exact, otherwise a minimum
     * least-squares fit is calculated.
     * @param P the source points
     * @param Q the target points
     * @return a new {@link SimilarityMapping2D} instance for the two point sets
     */
    public static SimilarityMapping2D fromPoints(Pnt2d[] P, Pnt2d[] Q) {
        int m = Math.min(P.length, Q.length);
        if (m < 2) {
            throw new IllegalArgumentException("at least two points pairs required");
        }

        RealMatrix M = MatrixUtils.createRealMatrix(2 * m, 4);
        RealVector b = new ArrayRealVector(2 * m);

        // solve M * w = b, with w = (a, b, tx, ty)
        // mount matrix M
        int row = 0;
        for (Pnt2d p : P) {
            M.setEntry(row, 0, p.getX());
            M.setEntry(row, 1, -p.getY());
            M.setEntry(row, 2, 1);
            M.setEntry(row, 3, 0);
            row++;
            M.setEntry(row, 0, p.getY());
            M.setEntry(row, 1, p.getX());
            M.setEntry(row, 2, 0);
            M.setEntry(row, 3, 1);
            row++;
        }

        // mount vector b =
        row = 0;
        for (Pnt2d q : Q) {
            b.setEntry(row, q.getX());
            row++;
            b.setEntry(row, q.getY());
            row++;
        }
        DecompositionSolver solver = new SingularValueDecomposition(M).getSolver();
        RealVector w = solver.solve(b);

        return new SimilarityMapping2D(
                w.getEntry(0), w.getEntry(1),
                w.getEntry(2), w.getEntry(3));   // a, b, tx, ty
    }

    public double getScale() {
        return Math.hypot(a00, a10);
    }

    public double getAngle() {
        return Math.atan2(a10, a00);
    }

    // TODO: make sure getInverse() returns a SimilarityMapping2D instance!
}
