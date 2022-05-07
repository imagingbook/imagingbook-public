/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.circle;

import static imagingbook.common.math.Arithmetic.mod;
import static java.lang.Math.PI;

import java.util.Random;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;

public class CircleSampler {
	
	private final Random rg;
	
	private final GeometricCircle circle;
	
	public CircleSampler(GeometricCircle circle) {
		this.circle = circle;
		this.rg = new Random();
	}
	
	public CircleSampler(GeometricCircle circle, long seed) {
		this.circle = circle;
		this.rg = new Random(seed);
	}
	
	/**
	 * Generate point coordinates on a circular arc.
	 * Sampled x/y positions are disturbed by additive Gaussian noise.
	 *
	 * @param n          number of points to sample
	 * @param startAngle initial angle (in radians)
	 * @param endAngle   final angle (in radians)
	 * @param sigma      sigma of Gaussian noise
	 *
	 * @return an array of sample points
	 */
	public Pnt2d[] getPoints(int n, double startAngle, double endAngle, double sigma) {
		double xc = circle.getXc();
		double yc = circle.getYc();
		double r = circle.getR();
		Pnt2d[] pts = new Pnt2d[n];
		
		startAngle = mod(startAngle, 2 * PI);
		endAngle = mod(endAngle, 2 * PI);
		double dAngle = (endAngle >= startAngle) ? 
				(endAngle - startAngle) :
				(endAngle + 2 * PI - startAngle);
		
		for (int i = 0; i < n; i++) {
			double alpha = startAngle + dAngle * i / n;
			double x = xc + r * Math.cos(alpha) + sigma * rg.nextGaussian();
			double y = yc + r * Math.sin(alpha) + sigma * rg.nextGaussian();
			pts[i] = Pnt2d.from(x, y);
		}
		return pts;
	}

}
