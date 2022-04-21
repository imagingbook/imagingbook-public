/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.corners;

import static imagingbook.common.math.Arithmetic.isZero;

import ij.process.ImageProcessor;


/**
 * This is an implementation of the corner detector described in
 * <blockquote>
 * M. Brown, R. Szeliski, and S. Winder, Multi-image matching using multi-scale oriented
 * patches, in Proc. of the IEEE Computer Society Conference on Computer Vision and Pattern Recognition
 * (CVPR'05), 2005, pp. 510–517.
 * </blockquote>
 * This class extends {@link GradientCornerDetector} (where most
 * of the work is done) by defining a specific corner score function
 * and associated threshold.
 * The corner score is defined as the harmonic mean of the local structure tensor's eigenvalues 
 * lambda_1, lambda_2.
 * 
 * @author W. Burger
 * @version 2022/03/30
 */
public class MopsCornerDetector extends GradientCornerDetector {
	
	public static double DEFAULT_THRESHOLD = 90;
	
//	public static class Parameters extends GradientCornerDetector.Parameters {
//		
//		public Parameters() {
//			scoreThreshold = DEFAULT_THRESHOLD;	// individual default threshold
//		}
//	}

	public MopsCornerDetector(ImageProcessor ip, GradientCornerDetector.Parameters params) {
		super(ip, params);
	}
	
	// --------------------------------------------------------------

	@Override
	protected float getCornerScore(float A, float B, float C) {
		float trace = A + B;
		if (isZero(trace)) {
			return UndefinedScoreValue;
		}
		float det = A * B - C * C;
		return det / trace;
	}

}
