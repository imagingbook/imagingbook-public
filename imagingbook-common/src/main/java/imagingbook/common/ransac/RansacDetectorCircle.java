/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.ransac;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFit3Points;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitHyperSimple;

/**
 * RANSAC detector for circles.
 * 
 * @author WB
 * 
 * @see GeometricCircle
 * @see GenericRansacDetector
 */
public class RansacDetectorCircle extends GenericRansacDetector<GeometricCircle>{
	
	// default parameters can be set here
	public static class Parameters extends RansacParameters {
		public Parameters() {
			this.maxIterations = 1000;
			this.distanceThreshold = 2.0;
			this.minSupportCount = 70;
		}
	}
	
	// constructors ------------------------------------

	public RansacDetectorCircle(Parameters params) {
		super(3, params);
	}
	
	public RansacDetectorCircle() {
		this(new Parameters());
	}
	
	// ----------------------------------------------------------------

	@Override
	protected GeometricCircle fitInitial(Pnt2d[] points) {
		CircleFitAlgebraic fit = new CircleFit3Points(points);
		return fit.getGeometricCircle();
	}
	
	@Override
	protected GeometricCircle fitFinal(Pnt2d[] inliers) {
//		CircleFitAlgebraic fit2 = new CircleFitPratt(inliers);	// TODO: fails, check why
		CircleFitAlgebraic fit2 = new CircleFitHyperSimple(inliers);
		if (fit2.getParameters() == null) 
			throw new RuntimeException("circle fitFinal() failed!");
		return fit2.getGeometricCircle();
	}
}
