/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.ellipse.utils;

import static imagingbook.common.math.Arithmetic.mod;
import static java.lang.Math.PI;

import java.util.Random;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.GeometricEllipse;

/**
 * Utility class for picking random points on a given ellipse. 
 * @author WB
 *
 */
public class EllipseSampler {
	
	private final Random rg;
	
	private final GeometricEllipse ellipse;
	
	public EllipseSampler(GeometricEllipse ellipse) {
		this.ellipse = ellipse;
		this.rg = new Random();
	}
	
	public EllipseSampler(GeometricEllipse ellipse, long seed) {
		this.ellipse = ellipse;
		this.rg = new Random(seed);
	}
	
	/**
	 * Creates and returns an array of 2D points sampled on 
	 * the ellipse associated with this {@link EllipseSampler}.
	 * Random Gaussian noise (with standard deviation sigma)
	 * is added to the individual x/y coordinates.
	 * 
	 * @param n number of points
	 * @param startAngle initial angle (radians)
	 * @param arcAngle arc angle (radians)
	 * @param sigma amount of random noise
	 * @return an array of sample points
	 */
	public Pnt2d[] getPoints(int n, double startAngle, double arcAngle, double sigma) {
		Pnt2d[] points = new Pnt2d[n];
				
		double xc = ellipse.xc;
		double yc = ellipse.yc;
		double ra = ellipse.ra;
		double rb = ellipse.rb;
		double theta = ellipse.theta;
		
		startAngle = mod(startAngle, 2 * PI);	
		arcAngle   = mod(arcAngle, 2 * PI);
		if (arcAngle == 0)
			arcAngle = 2 * PI;

//		double dAngle;
//		if (endAngle > startAngle) {
//			dAngle = endAngle - startAngle;
//		}
//		else if (endAngle < startAngle) {
//			dAngle = endAngle + 2 * PI - startAngle;
//		}
//		else {	// endAngle == startAngle
//			dAngle = 2 * PI;
//		}

		final double cosTh = Math.cos(theta);
		final double sinTh = Math.sin(theta);
		
		for (int i = 0; i < n; i++) {
			double alpha = startAngle + arcAngle * i / n;
			double x0 = ra * Math.cos(alpha) + sigma * rg.nextGaussian();
			double y0 = rb * Math.sin(alpha) + sigma * rg.nextGaussian();
			double x = x0 * cosTh - y0 * sinTh + xc;
			double y = x0 * sinTh + y0 * cosTh + yc;
			points[i] = Pnt2d.from(x, y);
		}
		return points;
	}

}
