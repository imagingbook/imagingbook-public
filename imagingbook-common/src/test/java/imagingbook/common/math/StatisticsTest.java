package imagingbook.common.math;

import org.junit.Test;

import imagingbook.testutils.ArrayTests;

public class StatisticsTest extends Statistics {
	
	// example: n = 4 samples of dimension m = 3:
	// samples[i][j], i = column (sample index), j = row (dimension index).
	private static double[][] samples = { 
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
		ArrayTests.assertArrayEquals(covExpected, cov, 1E-3);
	}
	
	@Test
	public void testCovarianceBias() {
		boolean BIAS_CORRECT = true;
		
		double[][] covExpected = {
				{1296.250, 442.583, -627.250}, 
				{442.583, 550.250, -70.917}, 
				{-627.250, -70.917, 370.917}};
		
		double[][] cov = covarianceMatrix(samples, BIAS_CORRECT);
		ArrayTests.assertArrayEquals(covExpected, cov, 1E-3);
	}

}
