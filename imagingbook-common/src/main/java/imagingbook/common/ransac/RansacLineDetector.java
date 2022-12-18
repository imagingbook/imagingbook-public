/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.ransac;

import static imagingbook.common.math.Arithmetic.sqr;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.fitting.line.LineFit;
import imagingbook.common.geometry.fitting.line.OrthogonalLineFitEigen;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.ij.DialogUtils;

/**
 * <p>
 * RANSAC detector for straight lines. See Sec. 12.1.2 - 12.1.3 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/19
 * @see AlgebraicLine
 * @see RansacDetector
 */
public class RansacLineDetector extends RansacDetector<AlgebraicLine>{
	
	private final Parameters params;
	
	/**
	 * Nested class extending {@link RansacDetector.RansacParameters} 
	 * to specify additional RANSAC parameters.
	 */
	public static class Parameters extends RansacDetector.RansacParameters {
		
		/** The minimum distance between two random sample points. */
		@DialogUtils.DialogLabel("Min. distance between sample points")
		public int minPairDistance;
		
		/**
		 * Constructor used to define default parameter values.
		 */
		public Parameters() {
			this.randomPointDraws = 1000;
			this.maxInlierDistance = 2.0;
			this.minInlierCount = 100;
			this.minPairDistance = 25;
		}	
	}
	
	// constructors ------------------------------------
	
	/**
	 * Constructor using specific parameters.
	 * @param params RANSAC parameters
	 */
	public RansacLineDetector(Parameters params) {
		super(2, params);
		this.params = params;
	}
	
	/**
	 * Constructor using default parameters.
	 */
	public RansacLineDetector() {
		this(new Parameters());
	}
	
	// ----------------------------------------------------------------
	
	@Override // override default method to check for min pair distance
	Pnt2d[] drawRandomPoints(Pnt2d[] points) {
		final int MaxTries = 20;
		int i = 0;
		Pnt2d[] draw = super.drawRandomPoints(points);
		while (draw[0].distanceSq(draw[1]) < sqr(params.minPairDistance) && i < MaxTries) {
			draw = super.drawRandomPoints(points);
			i++;
		}
		return (i < MaxTries) ? draw : null;
	}

	@Override
	AlgebraicLine fitInitial(Pnt2d[] points) {
		return AlgebraicLine.from(points[0], points[1]);
	}
	
	@Override
	AlgebraicLine fitFinal(Pnt2d[] inliers) {
		LineFit fit = new OrthogonalLineFitEigen(inliers);
		return fit.getLine();
	}

}
