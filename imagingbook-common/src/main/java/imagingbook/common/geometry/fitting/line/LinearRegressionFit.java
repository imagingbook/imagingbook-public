/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.line;

import static imagingbook.common.math.Arithmetic.sqr;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.geometry.line.SlopeInterceptLine;

/**
 * <p>
 * This class implements line fitting by linear regression to a set of 2D points.
 * See Sec. 10.2.1 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/09/29
 */
public class LinearRegressionFit implements LineFit {
	
	private final int n;
	private final double[] p;	// line parameters A,B,C
	private double k, d;
	
	/**
	 * Constructor, performs a linear regression fit to the specified points.
	 * At least two different points are required.
	 * 
	 * @param points an array with at least 2 points
	 */
	public LinearRegressionFit(Pnt2d[] points) {
		if (points.length < 2) {
			throw new IllegalArgumentException("line fit requires at least 2 points");
		}
		this.n = points.length;
		this.p = fit(points);
	}
	
	@Override
	public int getSize() {
		return n;
	}

	@Override
	public double[] getLineParameters() {
		return p;
	}
	
	/**
	 * Returns the slope parameter k for the fitted line
	 * y = k * x + d.
	 * @return line parameter k
	 */
	public double getK() {
		return k;
	}
	
	/**
	 * Returns the intercept parameter d for the fitted line
	 * y = k * x + d.
	 * @return line parameter d
	 */
	public double getD() {
		return d;
	}
	
	// ----------------------------------------------------------------------
	
	private double[] fit(Pnt2d[] points) {
		final int n = points.length;
	
		double Sx = 0, Sy = 0, Sxx = 0, Sxy = 0;
		
		for (int i = 0; i < n; i++) {
			final double x = points[i].getX();
			final double y = points[i].getY();
			Sx += x;
			Sy += y;
			Sxx += sqr(x);
			Sxy += x * y;
		}
			
		double den = sqr(Sx) - n * Sxx;
		this.k = (Sx * Sy - n * Sxy) / den;
		this.d = (Sx * Sxy - Sxx * Sy) / den;
		
		AlgebraicLine line = AlgebraicLine.from(new SlopeInterceptLine(k, d));
		return line.getParameters();
	}


	/**
	 * Calculates and returns the sum of the squared differences between
	 * the y-coordinates of the data points (xi, yi) and the associated y-value
	 * of the regression line (y = k x + d).
	 * 
	 * @param points an array of 2D points
	 * @return the squared regression error
	 */
	public double getSquaredRegressionError(Pnt2d[] points) {
		double s2 = 0;
		for (Pnt2d p : points) {
			double y = k * p.getX() + d;
			s2 = s2 + sqr(y - p.getY());
		}
		return s2;
	}
	
}
