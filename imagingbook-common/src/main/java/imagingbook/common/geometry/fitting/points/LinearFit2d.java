/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.geometry.fitting.points;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.linear.LinearMapping2D;

/**
 * Describes a fitter based on a linear transformation model.
 * @author WB
 */
public interface LinearFit2d {
	
	/**
	 * Returns the (3,3) or (2,3) transformation matrix A for this fit, such that
	 * {@code y_i ~ A * x_i} (with {@code x_i} in homogeneous coordinates).
	 * 
	 * @return the transformation matrix for this fit
	 */
	double[][] getTransformationMatrix();
	
	/**
	 * Returns the total error for this fit.
	 * @return the fitting error
	 */
	double getError();
	
	/**
	 * Calculates and returns the sum of squared fitting errors for two associated
	 * point sequences (P, Q) under a linear transformation specified by a 3x3
	 * matrix A.
	 * 
	 * @param P the point sequence to be fitted
	 * @param Q the reference point sequence
	 * @param A a 3x3 transformation matrix
	 * @return the
	 */
	public static double getSquaredError(Pnt2d[] P, Pnt2d[] Q, double[][] A) {
		final int m = Math.min(P.length,  Q.length);
		LinearMapping2D map = new LinearMapping2D(A);
		double errSum = 0;
		for (int i = 0; i < m; i++) {
			Pnt2d p = P[i];
			Pnt2d q = Q[i];
			Pnt2d pp = map.applyTo(p);
			errSum = errSum + q.distanceSq(pp);
		}
		return errSum;
	}
	
}