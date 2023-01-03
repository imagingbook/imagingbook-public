/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math;

import static imagingbook.common.math.Arithmetic.sqr;
import static imagingbook.testutils.NumericTestUtils.assert2dArrayEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;

public class MahalanobisDistanceTest {
	
	static double TOL = 1e-6;

	/*
	 * Test example from Burger/Burge 3rd edition (Appendix G.2.1):
	 * N = 4 samples, K = 3 dimensions
	 */
	private static double[] X1 = {75, 37, 12};
	private static double[] X2 = {41, 27, 20};
	private static double[] X3 = {93, 81, 11};
	private static double[] X4 = {12, 48, 52};
	private static double[][] Samples = {X1, X2, X3, X4};
	
	static {
		PrintPrecision.set(15);
	}

	@Test
	public void test1() {
		MahalanobisDistance mhd = MahalanobisDistance.fromSamples(Samples);

		// covariance matrix cov (3x3)
		double[][] cov = mhd.getCovarianceMatrix();
		//		System.out.println("cov = \n" + Matrix.toString(cov));
		//		System.out.println();
		double[][] covExp =
				{{972.1875, 331.9375, -470.4375},
				{331.9375, 412.6875,  -53.1875},
				{-470.4375, -53.1875, 278.1875}};
		assert2dArrayEquals(covExp, cov, TOL);

		// check dist(x, x) = 0
		for (double[] x : Samples) {
			assertEquals(0, mhd.distance(x, x), TOL);
		}

		// all pairwise distances
		// System.out.format("MH-dist(X1,X1) = %.9f\n", mhd.distance(X1, X1));
		// System.out.format("MH-dist(X1,X2) = %.9f\n", mhd.distance(X1, X2));
		// System.out.format("MH-dist(X1,X3) = %.9f\n", mhd.distance(X1, X3));
		// System.out.format("MH-dist(X1,X4) = %.9f\n", mhd.distance(X1, X4));
		// System.out.format("MH-dist(X1,X4) = %.9f\n", mhd.distance(X2, X3));
		// System.out.format("MH-dist(X1,X4) = %.9f\n", mhd.distance(X2, X4));
		// System.out.format("MH-dist(X1,X4) = %.9f\n", mhd.distance(X3, X4));
		// System.out.println();
		double distExp = 2.828427125;
		assertEquals(distExp, mhd.distance(X1, X2), TOL);
		assertEquals(distExp, mhd.distance(X2, X1), TOL);
		assertEquals(distExp, mhd.distance(X1, X3), TOL);
		assertEquals(distExp, mhd.distance(X3, X1), TOL);
		assertEquals(distExp, mhd.distance(X1, X4), TOL);
		assertEquals(distExp, mhd.distance(X4, X1), TOL);
		assertEquals(distExp, mhd.distance(X2, X3), TOL);
		assertEquals(distExp, mhd.distance(X3, X2), TOL);
		assertEquals(distExp, mhd.distance(X2, X4), TOL);
		assertEquals(distExp, mhd.distance(X4, X2), TOL);
		assertEquals(distExp, mhd.distance(X3, X4), TOL);
		assertEquals(distExp, mhd.distance(X4, X3), TOL);

		// check whitening transformation
		double[][] U = mhd.getWhiteningTransformation();
		double[][] Uexp =
				{{0.179160778913998, -0.107711047143490, 0.282381730345214},
				{0.0, 0.049843337951337, 0.009529696831406},
				{0.0, 0.0, 0.059955798891471}};
		// System.out.println("whitening transformation U = \n" + Matrix.toString(U));
		assert2dArrayEquals(Uexp, U, TOL);

		// get whitened sample vectors
		double[] Y1 = Matrix.multiply(U, X1);
		double[] Y2 = Matrix.multiply(U, X2);
		double[] Y3 = Matrix.multiply(U, X3);
		double[] Y4 = Matrix.multiply(U, X4);
		// System.out.println("Y1 = " + Matrix.toString(Y1));
		// System.out.println("Y2 = " + Matrix.toString(Y2));
		// System.out.println("Y3 = " + Matrix.toString(Y3));
		// System.out.println("Y4 = " + Matrix.toString(Y4));

		// Euclidean distance between whitened vectors equals Mahalanobis distance between non-whitened vectors
		// System.out.println();
		// System.out.format("whitened L2-distance(Y1,Y2) = %.3f\n", Matrix.distL2(Y1, Y2));
		// System.out.format("whitened L2-distance(Y1,Y3) = %.3f\n", Matrix.distL2(Y1, Y3));
		// System.out.format("whitened L2-distance(Y1,Y4) = %.3f\n", Matrix.distL2(Y1, Y4));
		assertEquals(distExp, Matrix.distL2(Y1, Y2), TOL);
		assertEquals(distExp, Matrix.distL2(Y1, Y3), TOL);
		assertEquals(distExp, Matrix.distL2(Y1, Y4), TOL);
		assertEquals(distExp, Matrix.distL2(Y2, Y3), TOL);
		assertEquals(distExp, Matrix.distL2(Y2, Y4), TOL);
		assertEquals(distExp, Matrix.distL2(Y3, Y4), TOL);
	}
	
	
	@Test		// using repeatable random samples (with fixed seed)
	public void test2() {
		double[] Sigma = {2, 5, 3};
		double[] Mu = {1000, -1, 1};
		int N = 100000;

		double[][] samples = makeSamples2(Sigma, Mu, N);
		MahalanobisDistance mhd = MahalanobisDistance.fromSamples(samples);
	
		// double[] mu = Statistics.meanVector(samples);
		// System.out.println("mu = (sample mu)\n" + Matrix.toString(mu));
		// System.out.println();

		// covariance matrix cov (3x3)
		double[][] cov = mhd.getCovarianceMatrix();
		// System.out.println("cov = (sample covariance matrix)\n" + Matrix.toString(cov));
		// System.out.println();
		double[][] covExp =
				{{4.016304652976096, -0.033179602887449, 0.031831991520127},
				{-0.033179602887449, 25.198363643604146, 0.002204769505204},
				{0.031831991520127, 0.002204769505204, 8.992313277033302}};
		assert2dArrayEquals(cov, covExp, 1e-3);

		double[][] icov =  mhd.getInverseCovarianceMatrix();
		// System.out.println("icov = (inverse covariance matrix)\n" + Matrix.toString(icov));
		// System.out.println();
		
		double[][] U = mhd.getWhiteningTransformation();
		// System.out.println("U = (whitening transformation)\n" + Matrix.toString(U));
		// System.out.println();
		double[][] UExp =
				{{0.498993779286084, 0.000657197845087, -0.001766554859824},
				{0.000000000000000, 0.199211238332873, -0.000048843367645},
				{0.000000000000000, 0.000000000000000, 0.333475771301536}};
		assert2dArrayEquals(UExp, U, 1e-3);

		// UT*U must be same as icov:
		RealMatrix Um = MatrixUtils.createRealMatrix(U);
		RealMatrix UTU = Um.transpose().multiply(Um);
		assert2dArrayEquals(UTU.getData(), icov, TOL);
		
		// covariance matrix of whitened samples must be close to identity:
		double[][] whitened = whiten(samples, U);
		double[][] covW = Statistics.covarianceMatrix(whitened, false);
		assert2dArrayEquals(Matrix.idMatrix(3), covW, 1e-3);
	}
	
	private double[][] makeSamples2(double[] Sigma, double[] Mu, int N) {
		int K = Sigma.length;
		Random rd = new Random(77);
		double[][] s = new double[N][K];
		for (int i = 0; i < N; i++) {
			for (int k = 0; k < K; k++) {
				s[i][k] = Sigma[k] * rd.nextGaussian() + Mu[k];
			}
		}
		return s;
	}
	
	private double[][] whiten(double[][] samples, double[][] U) {
		double[][] whitened = new double[samples.length][];
		for (int i = 0; i < samples.length; i++) {
			whitened[i] = Matrix.multiply(U, samples[i]);
		}
		return whitened;
	}

	@Test		// using repeatable random samples (with fixed seed)
	public void test3() {
		double[] sigma = {3, 2, 4};
		double[][] cov =        // sigma_ii^2, sigma ) 2, 3, 4
				{{sqr(sigma[0]), 0, 0},
				 {0, sqr(sigma[1]), 0},
				 {0, 0, sqr(sigma[2])}};

		MahalanobisDistance mhd = new MahalanobisDistance(cov);

		double[][] Uexp =
				{{1/sigma[0], 0, 0},
				{0, 1/sigma[1], 0},
				{0, 0, 1/sigma[2]}};
		double[][] U = mhd.getWhiteningTransformation();
		// System.out.println("U = \n" + Matrix.toString(U));
		assert2dArrayEquals(Uexp, U, TOL);

		double[] x0w = Matrix.multiply(U, new double[] {sigma[0], 0, 0});
		double[] x1w = Matrix.multiply(U, new double[] {0, sigma[1], 0});
		double[] x2w = Matrix.multiply(U, new double[] {0, 0, sigma[2]});

		// System.out.println("x0w = " + Matrix.toString(x0w));
		// System.out.println("x1w = " + Matrix.toString(x1w));
		// System.out.println("x2w = " + Matrix.toString(x2w));
		assertArrayEquals(new double[] {1, 0, 0}, x0w, TOL);
		assertArrayEquals(new double[] {0, 1, 0}, x1w, TOL);
		assertArrayEquals(new double[] {0, 0, 1}, x2w, TOL);

	}

}
