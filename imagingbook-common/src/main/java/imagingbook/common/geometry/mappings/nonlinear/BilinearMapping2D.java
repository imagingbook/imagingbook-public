/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.geometry.mappings.nonlinear;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.Mapping2D;
import imagingbook.common.math.Matrix;

/**
 * This class represents a bilinear transformation in 2D with 8
 * parameters a0, ..., a3, b0, ..., b3 of the form
 * <pre>
 * x' = a0 * x + a1 * y + a2 * x * y + a3
 * y' = b0 * x + b1 * y + b2 * x * y + b3
 * </pre>
 * Note that this is a non-linear transformation because of the mixed term.
 */
public class BilinearMapping2D implements Mapping2D {
	
	private final double a0, a1, a2, a3;
	private final double b0, b1, b2, b3;
	
	public BilinearMapping2D(
			double a0, double a1, double a2, double a3,
			double b0, double b1, double b2, double b3) {
		this.a0 = a0;   this.a1 = a1;   this.a2 = a2;   this.a3 = a3;
		this.b0 = b0;   this.b1 = b1;   this.b2 = b2;   this.b3 = b3;		
	}
	
	/**
	 * Calculates and returns the bilinear mapping M between two point
	 * sets P, Q, with 4 points each, such that q_i = M(p_i).
	 * The inverse mapping is obtained by simply swapping the two point sets.
	 * @param P the first point sequence
	 * @param Q the second point sequence
	 * @return a new bilinear mapping
	 */
	public static BilinearMapping2D fromPoints(Pnt2d[] P, Pnt2d[] Q) {	
		//define column vectors x, y
		double[] x = {Q[0].getX(), Q[1].getX(), Q[2].getX(), Q[3].getX()};
		double[] y = {Q[0].getY(), Q[1].getY(), Q[2].getY(), Q[3].getY()};		
		// mount matrix M
		double[][] M = new double[][]
			{{P[0].getX(), P[0].getY(), P[0].getX() * P[0].getY(), 1},
			 {P[1].getX(), P[1].getY(), P[1].getX() * P[1].getY(), 1},
			 {P[2].getX(), P[2].getY(), P[2].getX() * P[2].getY(), 1},
			 {P[3].getX(), P[3].getY(), P[3].getX() * P[3].getY(), 1}};
		double[] a = Matrix.solve(M, x);		// solve x = M * a = x (a is unknown)
		double[] b = Matrix.solve(M, y);		// solve y = M * b = y (b is unknown)		
		double a1 = a[0];		double b1 = b[0];
		double a2 = a[1];		double b2 = b[1];
		double a3 = a[2];		double b3 = b[2];
		double a4 = a[3];		double b4 = b[3];
		return new BilinearMapping2D(a1, a2, a3, a4, b1, b2, b3, b4);
	}

	@Override
	public Pnt2d applyTo(Pnt2d pnt) {
		final double x = pnt.getX();
		final double y = pnt.getY();
		double xx = a0 * x + a1 * y + a2 * x * y + a3;
		double yy = b0 * x + b1 * y + b2 * x * y + b3;
		return PntDouble.from(xx, yy);
	}
	
	public String toString() {
		return String.format(
				"BilinearMapping[A = (%.3f, %.3f, %.3f, %.3f) / B = (%.3f, %.3f, %.3f, %.3f)]",
				a0, a1, a2, a3, b0, b1, b2, b3);
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * For testing only.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		Pnt2d[] P = {
				PntInt.from(2,5),
				PntInt.from(4,6),
				PntInt.from(7,9),
				PntInt.from(5,9),
				};
		
		Pnt2d[] Q = {
				PntInt.from(4,3),
				PntInt.from(5,2),
				PntInt.from(9,3),
				PntInt.from(7,5),
				};
		
		BilinearMapping2D bm = fromPoints(P, Q);
		System.out.println("\nbilinear mapping = \n" + bm.toString());
		
		for (int i = 0; i < P.length; i++) {
			Pnt2d Qi = bm.applyTo(P[i]);
			System.out.println(P[i].toString() + " -> " + Qi.toString());
		}
		
	}


}
