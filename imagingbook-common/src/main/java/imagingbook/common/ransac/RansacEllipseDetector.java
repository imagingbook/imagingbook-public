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
import imagingbook.common.geometry.ellipse.AlgebraicEllipse;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.geometry.fitting.ellipse.algebraic.EllipseFit5Points;
import imagingbook.common.geometry.fitting.ellipse.algebraic.EllipseFitAlgebraic;
import imagingbook.common.geometry.fitting.ellipse.algebraic.EllipseFitFitzgibbonStable;

/**
 * <p>
 * RANSAC detector for ellipses. See Sec. 12.1.5 of [1] for additional
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
 * @see GeometricEllipse
 * @see RansacDetector
 */
public class RansacEllipseDetector extends RansacDetector<GeometricEllipse> {
	
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
			this.minInlierCount = 100;
		}
	}
	
	// constructors ------------------------------------
	
	/**
	 * Constructor using specific parameters.
	 * @param params RANSAC parameters
	 */
	public RansacEllipseDetector(Parameters params) {
		super(5, params);
	}
	
	/**
	 * Constructor using default parameters.
	 */
	public RansacEllipseDetector() {
		this(new Parameters());
	}
	
	// ----------------------------------------------------------------

	@Override
	GeometricEllipse fitInitial(Pnt2d[] points) {
		EllipseFitAlgebraic fit = new EllipseFit5Points(points);
		AlgebraicEllipse ellipse = fit.getEllipse();
		return (ellipse == null) ?  null : new GeometricEllipse(ellipse);
	}
	
	@Override
	GeometricEllipse fitFinal(Pnt2d[] inliers) {
		EllipseFitAlgebraic fit2 = new EllipseFitFitzgibbonStable(inliers);
		AlgebraicEllipse ellipse = fit2.getEllipse();
		return (ellipse == null) ?  null : new GeometricEllipse(ellipse);
	}

}
