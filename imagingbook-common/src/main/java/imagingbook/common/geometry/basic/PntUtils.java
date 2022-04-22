/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.basic;

public abstract class PntUtils {
	
	public static Pnt2d centroid(Pnt2d[] pts) {
		final int n = pts.length;
		double sx = 0;
		double sy = 0;
		for (int i = 0; i < n; i++) {
			sx = sx + pts[i].getX();
			sy = sy + pts[i].getY();
		}
		return Pnt2d.from(sx/n, sy/n);
	}
	
	public static double[][] toDoubleArray(Pnt2d[] pts) {
		final int n = pts.length;
		double[][] pa = new double[n][2];
		for (int i = 0; i < n; i++) {
			pa[i][0] = pts[i].getX();
			pa[i][1] = pts[i].getY();
		}
		return pa;
	}
	
	public static Pnt2d[] fromDoubleArray(double[][] pa) {
		final int n = pa.length;
		Pnt2d[] pts = new Pnt2d[n];
		for (int i = 0; i < n; i++) {
			pts[i] = Pnt2d.from(pa[i]);
		}
		return pts;
	}
	
}
