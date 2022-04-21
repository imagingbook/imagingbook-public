/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.corners;

import static imagingbook.common.math.Arithmetic.sqr;

import ij.process.ImageProcessor;


/**
 * This is an implementation of the Harris corner detector, as described in
 * <blockquote>
 *  C. G. Harris and M. Stephens. A combined corner and edge
 *  detector. In C. J. Taylor, editor, 4th Alvey Vision Conference,
 *  pp. 147–151, Manchester (1988).
 *  </blockquote>
 * This class extends {@link GradientCornerDetector} (where most
 * of the work is done) by defining a specific corner score function.
 *
 * @author W. Burger
 * @version 2022/03/30
 */
public class HarrisCornerDetector extends GradientCornerDetector {
	
	public static final double DefaultAlpha = 0.05;

	private double alpha = DefaultAlpha;

	// ---------------------------------------------------------------------------
	
	public HarrisCornerDetector(ImageProcessor ip, Parameters params) {
		super(ip, params);
	}
	
	/**
	 * Set the sensitivity parameter &alpha; (alpha) for the corner score function.
	 * @param alpha sensitivity parameter (default is 0.05)
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	// ----------------------------------------------------------------------

	@Override
	protected float getCornerScore(float a, float b, float c) {
		double det = a * b - sqr(c);
		double trace = a + b;
		double score = det - alpha * sqr(trace);
		return (float) Math.sqrt(0.5 * score);	// returns 100 for default threshold = 20000
	}
	
}
