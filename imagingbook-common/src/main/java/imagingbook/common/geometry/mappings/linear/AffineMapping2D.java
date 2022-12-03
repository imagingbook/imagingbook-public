/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package imagingbook.common.geometry.mappings.linear;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.fitting.points.AffineFit2d;
import imagingbook.common.math.Arithmetic;
import imagingbook.common.math.Matrix;

/**
 * <p>
 * This class represents an affine transformation in 2D, which can be defined by
 * three pairs of corresponding points. It can be assumed that every instance of
 * this class is indeed an affine mapping. Instances are immutable. See Secs.
 * 21.1.3 and 21.3.1 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 */
public class AffineMapping2D extends ProjectiveMapping2D {

	/**
	 * Creates an affine 2D mapping from two sequences of corresponding points.
	 * If 3 point pairs are specified, the mapping is exact, otherwise a
	 * minimum least-squares fit is calculated.
	 * @param P the source points
	 * @param Q the target points
	 * @return a new {@link AffineMapping2D} instance for the two point sets
	 */
	public static AffineMapping2D fromPoints(Pnt2d[] P, Pnt2d[] Q) {
		if (P.length != Q.length) {
			throw new IllegalArgumentException("point sets P, Q must have the same size");
		}
		if (P.length < 3) {
			throw new IllegalArgumentException("at least 3 point pairs are required");
		}
		if (P.length == 3) {
			// exact fit
			return fromPoints(P[0], P[1], P[2], Q[0], Q[1], Q[2]);
		}
		else {
			// minimum least-squares fit
			AffineFit2d fit = new AffineFit2d(P, Q);
			return new AffineMapping2D(fit.getTransformationMatrix());
		}
	}
	
	/**
	 * <p>
	 * Creates an affine mapping from 3 pairs of corresponding 2D points
	 * (p0, p1, p2) &rarr; (q0, q1, q2).
	 * The solution is found in closed form (see [1], Sec. 21.1.3, eq. 21.31).
	 * </p>
	 * <p>
	 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
	 * </p>
	 * 
	 * @param p0 point 1 of source triangle A
	 * @param p1 point 2 of source triangle A
	 * @param p2 point 3 of source triangle A
	 * @param q0 point 1 of source triangle B
	 * @param q1 point 2 of source triangle B
	 * @param q2 point 3 of source triangle B
	 * @return a new {@link AffineMapping2D} instance
	 */
	public static AffineMapping2D fromPoints(Pnt2d p0, Pnt2d p1, Pnt2d p2, Pnt2d q0, Pnt2d q1, Pnt2d q2) {
		final double px0 = p0.getX(), px1 = p1.getX(), px2 = p2.getX();
		final double py0 = p0.getY(), py1 = p1.getY(), py2 = p2.getY();
		final double qx0 = q0.getX(), qx1 = q1.getX(), qx2 = q2.getX();
		final double qy0 = q0.getY(), qy1 = q1.getY(), qy2 = q2.getY();

		final double d = px0 * (py2 - py1) + px1 * (py0 - py2) + px2 * (py1 - py0); 
		if (Arithmetic.isZero(d)) {
			throw new ArithmeticException("affine mapping is undefined (d=0)");
		}
		double a00 = (py0 * (qx1 - qx2) + py1 * (qx2 - qx0) + py2 * (qx0 - qx1)) / d;
		double a01 = (px0 * (qx2 - qx1) + px1 * (qx0 - qx2) + px2 * (qx1 - qx0)) / d;
		double a10 = (py0 * (qy1 - qy2) + py1 * (qy2 - qy0) + py2 * (qy0 - qy1)) / d;
		double a11 = (px0 * (qy2 - qy1) + px1 * (qy0 - qy2) + px2 * (qy1 - qy0)) / d;
		
		double a02 = (px0*(py2*qx1-py1*qx2) + px1*(py0*qx2-py2*qx0) + px2*(py1*qx0-py0*qx1)) / d;
		double a12 = (px0*(py2*qy1-py1*qy2) + px1*(py0*qy2-py2*qy0) + px2*(py1*qy0-py0*qy1)) / d;
		
		return new AffineMapping2D(a00, a01, a02, a10, a11, a12);
	}
	
//	/**
//	 * Creates an affine mapping from an arbitrary 2D triangle A to another triangle B.
//	 * @param A the first triangle
//	 * @param B the second triangle
//	 * @return a new affine mapping
//	 * @deprecated
//	 */
//	public static AffineMapping2D from3Points(Point[] A, Point[] B) {
//		return AffineMapping2D.from3Points(A[0], A[1], A[2], B[0], B[1], B[2]);
//	}
	// ---------------------------------------------------------------------------
	
	/**
	 * Creates the identity mapping.
	 */
	public AffineMapping2D() {
		super();
	}
	
	/**
	 * Creates a linear mapping from a transformation matrix A,
	 * which must be at least of size 2 x 3.
	 * The elements of A are copied into a 3x3 identity matrix.
	 * If A is larger than 2 x 3, the remaining elements are ignored.
	 * @param A a 2x3 (or larger) matrix
	 */
	public AffineMapping2D(double[][] A) {
		//super(A[0][0], A[0][1], A[0][2], A[1][0], A[1][1], A[1][2], 0, 0);
		super(extractAffineMatrix(A));
	}
	
	private static double[][] extractAffineMatrix(double[][] A) {
		double[][] M = Matrix.idMatrix(3);
		final int m = Math.min(2, A.length);	// max. 2 rows
		for (int i = 0; i < m; i++) {
			final int n = Math.min(3, A[i].length);	// max. 3 columns
			for (int j = 0; j < n; j++) {
				M[i][j] = A[i][j];
			}
		}
		return M;
	}

	/**
	 * Creates an affine mapping from the specified matrix elements.
	 * @param a00 matrix element A_00
	 * @param a01 matrix element A_01
	 * @param a02 matrix element A_02
	 * @param a10 matrix element A_10
	 * @param a11 matrix element A_11
	 * @param a12 matrix element A_12
	 */
	public AffineMapping2D (
			double a00, double a01, double a02, 
			double a10, double a11, double a12) {
		super(a00, a01, a02, a10, a11, a12, 0, 0);
	}

	/**
	 * Creates a new affine mapping from an existing affine mapping.
	 * @param m an affine mapping
	 */
	public AffineMapping2D(AffineMapping2D m) {
		this(m.a00, m.a01, m.a02, m.a10, m.a11, m.a11);
	}
	
	// ----------------------------------------------------------
	

	/**
	 * Checks if the given linear mapping could be affine, i.e. if the
	 * bottom row of its transformation matrix is (0, 0, 1). 
	 * Note that this is a necessary but not sufficient requirement.
	 * The threshold {@link Arithmetic#EPSILON_DOUBLE} is used in this check.
	 * @param map a linear mapping
	 * @return true if the mapping could be affine
	 */
	public static boolean isAffine(LinearMapping2D map) {
		final double tol = Arithmetic.EPSILON_DOUBLE; // max. deviation for 0/1 values
		if (Math.abs(map.a20) > tol) return false;
		if (Math.abs(map.a21) > tol) return false;
		if (Math.abs(map.a22 - 1.0) > tol) return false;
		return true;
	}

	/**
	 * Concatenates this mapping A with another affine mapping B and returns
	 * a new mapping C, such that C(x) = B(A(x)).
	 * @param B the second mapping
	 * @return the concatenated affine mapping
	 */
	public AffineMapping2D concat(AffineMapping2D B) {	// use some super method instead?
		double[][] C = Matrix.multiply(B.getTransformationMatrix(), this.getTransformationMatrix());
		return new AffineMapping2D(C[0][0], C[0][1], C[0][2], C[1][0], C[1][1], C[1][2]);
	}
	
	@Override	// more efficient than generic version
	public Pnt2d applyTo(Pnt2d pnt) {
		final double x = pnt.getX();
		final double y = pnt.getY();
		double x1 = (a00 * x + a01 * y + a02);
		double y1 = (a10 * x + a11 * y + a12);
		return PntDouble.from(x1, y1);
	}

	/**
	 * {@inheritDoc}
	 * Note that inverting an affine transformation always yields
	 * another affine transformation.
	 */
	@Override
	public AffineMapping2D getInverse() {
		double det = a00 * a11 - a01 * a10;
		double b00 = a11 / det;
		double b01 = -a01 / det;
		double b02 = (a01 * a12 - a02 * a11) / det;
		double b10 = -a10 / det;
		double b11 = a00 / det;
		double b12 = (a02 * a10 - a00 * a12) / det;
		return new AffineMapping2D(b00, b01, b02, b10, b11, b12);
	}

	/**
	 * {@inheritDoc}
	 * @return a new affine mapping
	 */
	@Override
	public AffineMapping2D duplicate() {
		return new AffineMapping2D(this);
	}

	@Override
	public double[][] getJacobian(Pnt2d xy) {
		final double x = xy.getX();
		final double y = xy.getY();
		return new double[][]
			{{x, y, 0, 0, 1, 0},
			 {0, 0, x, y, 0, 1}};
	}
	
	// ----------------------------------------------------------------------
	
//	/**
//	 * For testing only.
//	 * @param args ignored
//	 */
//	public static void main(String[] args) {
//		PrintPrecision.set(6);
//		double[][] A = 
//			{{-2, 4, -3}, 
//			{3, 7, 2}, 
//			{0, 0, 1}};
//		System.out.println("a = \n" + Matrix.toString(A));
//		System.out.println();
//		double[][] ai = Matrix.inverse(A);
//		System.out.println("ai = \n" + Matrix.toString(ai));
//		
//		LinearMapping2D Ai = new LinearMapping2D(ai);
//		System.out.println("Ai is affine: " + isAffine(Ai));
//		
//		double[][] I = Matrix.multiply(A, ai);
//		System.out.println("\ntest: should be the  identity matrix: = \n" + Matrix.toString(I));
//		
//		double[][] B = 
//			{{-2, 4, -3}, 
//			{3, 7, 2}};
//		
//		LinearMapping2D am = new AffineMapping2D(B);
//		System.out.println("an = \n" + Matrix.toString(am.getTransformationMatrix()));
//	}

}




