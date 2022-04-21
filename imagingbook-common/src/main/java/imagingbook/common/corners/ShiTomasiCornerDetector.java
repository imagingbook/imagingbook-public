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
import static java.lang.Math.sqrt;

import ij.process.ImageProcessor;


/**
 * This is an implementation of the Shi-Tomasi corner detector, as 
 * described in
 * <blockquote>
 *  J. Shi and C. Tomasi. Good features to track. In Proceedings
 *  of IEEE Conference on Computer Vision and Pattern Recognition,
 *  CVPR’94, pp. 593–600, Seattle, WA, USA (1994).
 * </blockquote>
 * This class extends {@link GradientCornerDetector} (where most
 * of the work is done) by defining a specific corner score function
 * and associated threshold.
 * 
 * @author W. Burger
 * @version 2022/03/30
 */
public class ShiTomasiCornerDetector extends GradientCornerDetector {
	
//	public static double DEFAULT_THRESHOLD = 100;
//	
//	public static class Parameters extends GradientCornerDetector.Parameters {
//		
//		public Parameters() {
//			scoreThreshold = DEFAULT_THRESHOLD;	// individual default threshold
//		}
//	}
	
	public ShiTomasiCornerDetector(ImageProcessor ip, GradientCornerDetector.Parameters params) {
		super(ip, params);
	}
	
	// --------------------------------------------------------------

	@Override
	protected float getCornerScore(float A, float B, float C) {
		double rootExpr = sqr((A - B) / 2) + sqr(C);
		if (rootExpr < 0) {
			return UndefinedScoreValue;
		}
		double lambda2 = (A + B) / 2 - sqrt(rootExpr);
		return (float) lambda2;
	}

}
