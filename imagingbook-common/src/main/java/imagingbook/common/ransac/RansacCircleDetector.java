/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.ransac;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFit3Points;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitHyperSimple;

/**
 * <p>
 * RANSAC detector for circles. See Sec. 12.1.4 of [1] for additional
 * details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/19
 * 
 * @see GeometricCircle
 * @see RansacDetector
 */
public class RansacCircleDetector extends RansacDetector<GeometricCircle>{
	
	/**
	 * Nested class extending {@link RansacDetector.RansacParameters} 
	 * to specify additional RANSAC parameters.
	 */
	public static class Parameters extends RansacDetector.RansacParameters {
		/**
		 * Constructor used to define default parameter values.
		 */
		public Parameters() {
			this.randomPointDraws = 1000;
			this.maxInlierDistance = 2.0;
			this.minInlierCount = 70;
		}
	}
	
	// constructors ------------------------------------

	/**
	 * Constructor using specific parameters.
	 * @param params RANSAC parameters
	 */
	public RansacCircleDetector(Parameters params) {
		super(3, params);
	}
	
	/**
	 * Constructor using default parameters.
	 */
	public RansacCircleDetector() {
		this(new Parameters());
	}
	
	// ----------------------------------------------------------------

	@Override
	GeometricCircle fitInitial(Pnt2d[] points) {
		CircleFitAlgebraic fit = new CircleFit3Points(points);
		return fit.getGeometricCircle();
	}
	
	@Override
	GeometricCircle fitFinal(Pnt2d[] inliers) {
//		CircleFitAlgebraic fit2 = new CircleFitPratt(inliers);	// TODO: fails, check why
		CircleFitAlgebraic fit2 = new CircleFitHyperSimple(inliers);
		if (fit2.getParameters() == null) 
			throw new RuntimeException("circle fitFinal() failed!");
		return fit2.getGeometricCircle();
	}
}
