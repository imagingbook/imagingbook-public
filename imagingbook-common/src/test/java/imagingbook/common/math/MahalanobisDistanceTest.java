/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math;

import static imagingbook.testutils.NumericTestUtils.assertArrayEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;

public class MahalanobisDistanceTest {
	
	static double TOL = 1e-6;
	
	static {
		PrintPrecision.set(15);
	}

	@Test
	public void test1() {
		/*
		 * Test example from Burger/Burge 3rd edition (Appendix G.2.1):
		 * N = 4 samples, K = 3 dimensions
		 */
		double[] X1 = {75, 37, 12};
		double[] X2 = {41, 27, 20};
		double[] X3 = {93, 81, 11};
		double[] X4 = {12, 48, 52};
		
		double[] muExp = {55.25, 48.25, 23.75};	
		double[][] covExp =
			{{972.1875, 331.9375, -470.4375}, 
			 {331.9375, 412.6875,  -53.1875}, 
			 {-470.4375, -53.1875, 278.1875}};

//		System.out.println("X1 = " + Matrix.toString(X1));
//		System.out.println("X2 = " + Matrix.toString(X2));
//		System.out.println("X3 = " + Matrix.toString(X3));
//		System.out.println("X4 = " + Matrix.toString(X4));
//		System.out.println();

		double[][] samples = {X1, X2, X3, X4};
		MahalanobisDistance mhd = new MahalanobisDistance(samples);

		double[] mu = mhd.getMeanVector();
		assertArrayEquals("", muExp, mu, 1e-6);
//		System.out.println("mu = " + Matrix.toString(mu));
//		System.out.println();

		// covariance matrix cov (3x3)
		double[][] cov = mhd.getCovarianceMatrix();
		assertArrayEquals(covExp, cov, 1e-6);
//		System.out.println("cov = \n" + Matrix.toString(cov));
//		System.out.println();

//		double[][] icov =  mhd.getInverseCovarianceMatrix();
//		System.out.println("icov = \n" + Matrix.toString(icov)); 
//		System.out.println();
		
		for (double[] x : samples) {
			assertEquals(0, mhd.distance(x, x), TOL);
		}
		
		double distExp = 2.828427125;
		assertEquals(distExp, mhd.distance(X1, X2), TOL);
		assertEquals(distExp, mhd.distance(X2, X1), TOL);
		assertEquals(distExp, mhd.distance(X1, X3), TOL);
		assertEquals(distExp, mhd.distance(X1, X4), TOL);

//		System.out.format("MH-dist(X1,X1) = %.9f\n", mhd.distance(X1, X1));
//		System.out.format("MH-dist(X1,X2) = %.9f\n", mhd.distance(X1, X2));
//		System.out.format("MH-dist(X2,X1) = %.9f\n", mhd.distance(X2, X1));
//		System.out.format("MH-dist(X1,X3) = %.9f\n", mhd.distance(X1, X3));
//		System.out.format("MH-dist(X1,X4) = %.9f\n", mhd.distance(X1, X4));
//		System.out.println();

		double distMuExp = 1.732050808;
		assertEquals(distMuExp, mhd.distance(X1, mu), TOL);
		assertEquals(distMuExp, mhd.distance(X1), TOL);
		assertEquals(distMuExp, mhd.distance(X2, mu), TOL);
		assertEquals(distMuExp, mhd.distance(X2), 1e-6);
		assertEquals(distMuExp, mhd.distance(X3, mu), TOL);
		assertEquals(distMuExp, mhd.distance(X3), TOL);
		assertEquals(distMuExp, mhd.distance(X4, mu), TOL);
		assertEquals(distMuExp, mhd.distance(X4), 1e-6);
		
//		System.out.format("MH-dist(X1,mu) = %.9f\n", mhd.distance(X1, mu));
//		System.out.format("MH-dist(X1)    = %.3f\n", mhd.distance(X1));
//		System.out.format("MH-dist(X2,mu) = %.3f\n", mhd.distance(X2, mu));
//		System.out.format("MH-dist(X2)    = %.3f\n", mhd.distance(X2));
//		System.out.format("MH-dist(X3,mu) = %.3f\n", mhd.distance(X3, mu));
//		System.out.format("MH-dist(X3)    = %.3f\n", mhd.distance(X3));
//		System.out.format("MH-dist(X4,mu) = %.3f\n", mhd.distance(X4, mu));
//		System.out.format("MH-dist(X4)    = %.3f\n", mhd.distance(X4));
//		System.out.println();

//		System.out.format("L2-distance(X1,X2) = %.3f\n", L2.distance(X1, X2));
//		System.out.format("L2-distance(X2,X1) = %.3f\n", L2.distance(X2, X1));

		double[][] UExp = 
			{{0.179160778913998, -0.107711047143490, 0.282381730345214}, 
			{0.0, 0.049843337951337, 0.009529696831406}, 
			{0.0, 0.0, 0.059955798891471}};
		
//		System.out.println();
//		System.out.println("Testing pre-transformed Mahalanobis distances:");
		double[][] U = mhd.getWhiteningTransformation();
		assertArrayEquals(UExp, U, TOL);
//		System.out.println("whitening transformation U = \n" + Matrix.toString(U));

		// calculate whitened sample vectors
		double[] Y1 = Matrix.multiply(U, X1);
		double[] Y2 = Matrix.multiply(U, X2);//U.operate(X2);
		double[] Y3 = Matrix.multiply(U, X3);//U.operate(X3);
		double[] Y4 = Matrix.multiply(U, X4);//U.operate(X4);
		double[] muT = Matrix.multiply(U, mu);//U.operate(mu);

//		System.out.println("Y1 = " + Matrix.toString(Y1));
//		System.out.println("Y2 = " + Matrix.toString(Y2));
//		System.out.println("Y3 = " + Matrix.toString(Y3));
//		System.out.println("Y4 = " + Matrix.toString(Y4));
		
		assertEquals(distExp, Matrix.distL2(Y1, Y2), TOL);
		assertEquals(distExp, Matrix.distL2(Y1, Y3), TOL);
		assertEquals(distExp, Matrix.distL2(Y1, Y4), TOL);
//		System.out.println();
//		System.out.format("whitened L2-distance(Y1,Y2) = %.3f\n", L2.distance(Y1, Y2));
//		System.out.format("whitened L2-distance(Y1,Y3) = %.3f\n", L2.distance(Y1, Y3));
//		System.out.format("whitened L2-distance(Y1,Y4) = %.3f\n", L2.distance(Y1, Y4));
		
		assertEquals(distMuExp, Matrix.distL2(Y1, muT), TOL);
		assertEquals(distMuExp, Matrix.distL2(Y2, muT), TOL);
		assertEquals(distMuExp, Matrix.distL2(Y3, muT), TOL);
		assertEquals(distMuExp, Matrix.distL2(Y4, muT), TOL);
//		System.out.println();
//		System.out.format("whitened L2-distance(Y1,muT) = %.3f\n", L2.distance(Y1, muT));
//		System.out.format("whitened L2-distance(Y2,muT) = %.3f\n", L2.distance(Y2, muT));
//		System.out.format("whitened L2-distance(Y3,muT) = %.3f\n", L2.distance(Y3, muT));
//		System.out.format("whitened L2-distance(Y4,muT) = %.3f\n", L2.distance(Y4, muT));
	}
	
	
	@Test		// using repeatable random samples (with fixed seed)
	public void test2() {
		
		double[][] samples = makeSamples2();
		MahalanobisDistance mhd = new MahalanobisDistance(samples);
	
		double[] mu = mhd.getMeanVector();
		double[] muExp = {0.056703421178732, -1.043681140842644, 1.026688699943630};
		assertArrayEquals(muExp, mu, TOL);

//		System.out.println("mu = (sample mu)\n" + Matrix.toString(mu));
//		System.out.println();

		// covariance matrix cov (3x3)
		double[][] cov = mhd.getCovarianceMatrix();
		double[][] covExp =
			{{3.530730722470281, -0.273104974267673, 0.090141865415068}, 
			{-0.273104974267673, 24.437361659979956, -1.000379795885254}, 
			{0.090141865415068, -1.000379795885254, 9.564611057974654}};
		assertArrayEquals(cov, covExp, TOL);
		
//		System.out.println("cov = (sample covariance matrix)\n" + Matrix.toString(cov));
//		System.out.println();
		
		double[][] icov =  mhd.getInverseCovarianceMatrix();
//		System.out.println("icov = (inverse covariance matrix)\n" + Matrix.toString(icov)); 
//		System.out.println();
		
		double[][] U = mhd.getWhiteningTransformation();
		double[][] UExp = 
			{{0.532470802886470, 0.005770015800939, -0.004414785291958}, 
			{0.000000000000000, 0.202723726146212, 0.021203237492251}, 
			{0.000000000000000, 0.000000000000000, 0.323345143830134}};
		assertArrayEquals(UExp, U, TOL);
//		System.out.println("U = (whitening transformation)\n" + Matrix.toString(U));
//		System.out.println();
		
		// UT*U must be same as icov:
		RealMatrix Um = MatrixUtils.createRealMatrix(U);
		RealMatrix UTU = Um.transpose().multiply(Um);
		assertArrayEquals(UTU.getData(), icov, TOL);
//		System.out.println("UT*U = (must be same as icov)\n" + Matrix.toString(UTU.getData()));
//		System.out.println();
		
		// covariance matrix of whitened samples must be close to identity:
		double[][] whitened = whiten(samples, Um);
		double[][] covW = Statistics.covarianceMatrix(whitened, false);
		assertArrayEquals(Matrix.idMatrix(3), covW, TOL);
//		System.out.println("covW = (covariance matrix of whitened samples must be close to identity)\n" + Matrix.toString(covW));
	}
	
	private double[][] makeSamples2() {
		int N = 1000;
		double[] Sigma = {2, 5, 3};
		double[] Mu = {0, -1, 1};
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
	
	private double[][] whiten(double[][] samples, RealMatrix U) {
		double[][] whitened = new double[samples.length][];
		for (int i = 0; i < samples.length; i++) {
			whitened[i] = U.operate(samples[i]);
		}
		return whitened;
	}

}
