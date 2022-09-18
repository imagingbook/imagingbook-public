/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math;

import static imagingbook.common.math.Statistics.covarianceMatrix;

import org.junit.Test;

import imagingbook.testutils.NumericTestUtils;

public class StatisticsTest {
	
	private static final double TOL = 1E-3;
	
	// example: n = 4 samples of dimension m = 3:
	// samples[i][j], i = column (sample index), j = row (dimension index).
	private static final double[][] samples = { 
			{75, 37, 12},	// i = 0
			{41, 27, 20},	// i = 1
			{93, 81, 11},	// i = 2
			{12, 48, 52}	// i = 3
	};

	@Test
	public void testCovarianceNoBias() {
		boolean BIAS_CORRECT = false;
		
		double[][] covExpected = {
				{972.1875, 331.9375, -470.4375}, 
				{331.9375, 412.6875, -53.1875}, 
				{-470.4375, -53.1875, 278.1875}};
		
		double[][] cov = covarianceMatrix(samples, BIAS_CORRECT);
		NumericTestUtils.assertArrayEquals(covExpected, cov, TOL);
	}
	
	@Test
	public void testCovarianceBias() {
		boolean BIAS_CORRECT = true;
		
		double[][] covExpected = {
				{1296.250, 442.583, -627.250}, 
				{442.583, 550.250, -70.917}, 
				{-627.250, -70.917, 370.917}};
		
		double[][] cov = covarianceMatrix(samples, BIAS_CORRECT);
		NumericTestUtils.assertArrayEquals(covExpected, cov, TOL);
	}

}
