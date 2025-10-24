/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math.nonlinear;

import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.sin;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Assert;
import org.junit.Test;

public class NonlinearLeastSquaresTest {
	
	// 1D fitting example in book (Appendix C): model function f(x) = exp(-a * x) * sin(b * x) + c
	private static double[][] data = {{3, 2.5}, {6, 1.7}, {8, 2.5}, {8, 2.3}, {15, 2.1}}; // (xi, yi)
	private static final int n = data.length;
	private static final int k = 3; 				// number of parameters
	
	private static double[] poptExp = {0.114993, 0.862426, 2.130214};	// expected solution (a, b, c)
	
	// value function V
	private static MultivariateVectorFunction V = new MultivariateVectorFunction() {
		@Override
		public double[] value(double[] p) {
			double[] V = new double[n];
			double a = p[0];
			double b = p[1];
			double c = p[2];
			for (int i = 0; i < n; i++) {
				double x = data[i][0];
				V[i] = exp(-a * x) * sin(b * x) + c;
			}
			return V;
		}
	};
	
	// Jacobian function J
	private static MultivariateMatrixFunction J = new MultivariateMatrixFunction() {
		@Override
		public double[][] value(double[] p) {
			double[][] J = new double[n][k];
			double a = p[0];
			double b = p[1];
//			double c = p[2];
			for (int i = 0; i < n; i++) {			
				double x = data[i][0];
				J[i][0] = -exp(-a * x) * x * sin(b * x); // df(x)/da
				J[i][1] =  exp(-a * x) * x * cos(b * x); // df(x)/db
				J[i][2] = 1; 							 //	df(x)/dc
			}
			return J;
		}
	};
	
	
	private static RealVector makeTargetVector(double[][] data) {
		int n = data.length;
		RealVector target = new ArrayRealVector(n);
		for (int i = 0; i < n; i++) {
			target.setEntry(i, data[i][1]);
		}
		return target;
	}
	
	// -----------------------------------

	@Test
	public void testSolveNLS() {		// same as solveLevenvergMarquardt()
		RealVector z = makeTargetVector(data);
		RealVector p0 = new ArrayRealVector(new double[] {0,1,2});
		RealVector popt = NonlinearLeastSquares.solveNLS(V, J, z, p0);
		Assert.assertArrayEquals("wrong optimal solution popt", poptExp, popt.toArray(), 1e-3);
	}

	@Test
	public void testSolveLevenvergMarquardt() {
		RealVector z = makeTargetVector(data);
		RealVector p0 = new ArrayRealVector(new double[] {0,1,2});
		RealVector popt = NonlinearLeastSquares.solveLevenvergMarquardt(V, J, z, p0);
		Assert.assertArrayEquals("wrong optimal solution popt", poptExp, popt.toArray(), 1e-3);
	}

	@Test
	public void testSolveGaussNewton() {
		RealVector z = makeTargetVector(data);
		RealVector p0 = new ArrayRealVector(new double[] {0,1,2});
		RealVector popt = NonlinearLeastSquares.solveGaussNewton(V, J, z, p0);
		Assert.assertArrayEquals("wrong optimal solution popt", poptExp, popt.toArray(), 1e-3);
	}

	@Test
	public void testSolveNLS2() {
		RealVector z = makeTargetVector(data);
		RealVector p0 = new ArrayRealVector(new double[] {0,1,2});
		RealVector popt = NonlinearLeastSquares.solveNLS2(V, J, z, p0);
		Assert.assertArrayEquals("wrong optimal solution popt", poptExp, popt.toArray(), 1e-3);
	}

}
