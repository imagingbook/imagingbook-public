/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package imagingbook.common.image.matching;

import ij.process.ImageProcessor;

import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

/**
 * <p>
 * Instances of this class perform matching on scalar-valued images based on the correlation coefficient. The "search"
 * image I (to be searched for matches of the "reference" image R) is initially associated with the
 * {@link CorrCoeffMatcher}. The assumption is, that the search image I is fixed and the matcher tries to match multiple
 * reference images R. See Sec. 23.1.1 (Alg. 23.1) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/09/16
 */
public class CorrCoeffMatcher {

	private final float[][] fI;		// search image (copied to a float array)
	private final int wI, hI; 		// width/height of image

	private float[][] fR;			// reference image (copied to a float array)
	private int wR, hR; 			// width/height of reference

	// modified variables:
	private int K;					// number of pixels in reference image
	private float meanR; 			// mean value of reference
	private float varR;  			// square root of reference variance

	/**
	 * Constructor, accepts an {@link ImageProcessor} instance. Color images are converted to grayscale.
	 * @param I the search image (to be matched to)
	 */
	public CorrCoeffMatcher(ImageProcessor I) {
		this(I.getFloatArray());
	}

	/**
	 * Constructor, accepts a 2D float array.
	 * @param fI the search image (to be matched to)
	 */
	public CorrCoeffMatcher(float[][] fI) {
		this.fI = fI;
		this.wI = fI.length;
		this.hI = fI[0].length;
	}

	/**
	 * Matches the specified reference image R to the (fixed) search image I. Resulting score values are in [-1, 1], the
	 * score for the optimal match is +1. The returned score matrix has the size of the search image I reduced by the
	 * size of the reference image R.
	 *
	 * @param R a scalar-valued reference image
	 * @return a 2D array Q[r][s] of match scores in [-1,1]
	 */
	public float[][] getMatch(ImageProcessor R) {
		return getMatch(R.getFloatArray());
	}

	/**
	 * Matches the specified reference image R to the (fixed) search image I. Resulting score values are in [-1, 1], the
	 * score for the optimal match is +1. The returned score matrix has the size of the search image I reduced by the
	 * size of the reference image R.
	 *
	 * @param fR a scalar-valued reference image
	 * @return a 2D array Q[r][s] of match scores in [-1,1]
	 */
	public float[][] getMatch(float[][] fR) {
		this.fR = fR;
		this.wR = fR.length;
		this.hR = fR[0].length;
		this.K = wR * hR;

		// calculate the mean and variance of R
		float sumR = 0;
		float sumR2 = 0;
		for (int j = 0; j < hR; j++) {
			for (int i = 0; i < wR; i++) {
				float b = fR[i][j];
				sumR  += b;
				sumR2 += sqr(b);
			}
		}
		
		this.meanR = sumR / K;
		this.varR = (float) sqrt(sumR2 - K * sqr(meanR));
		
		float[][] C = new float[wI - wR + 1][hI - hR + 1];
		for (int r = 0; r < C.length; r++) {
			for (int s = 0; s < C[r].length; s++) {
				C[r][s] = getMatchValue(r, s);
			}	
		}
		this.fR = null;
		return C;
	}
	
	private float getMatchValue(int r, int s) {
		float sumI = 0, sumI2 = 0, sumIR = 0;
		for (int j = 0; j < hR; j++) {
			for (int i = 0; i < wR; i++) {
				float a = fI[r + i][s + j];
				float b = fR[i][j];
				sumI  += a;
				sumI2 += sqr(a);
				sumIR += a * b;
			}
		}
		float meanI = sumI / K;
		return (sumIR - K * meanI * meanR) / 
			   (1f + (float) sqrt(sumI2 - K * sqr(meanI)) * varR);
			// added 1 in denominator to handle flat image regions (w. zero variance)
	}  
	
}
