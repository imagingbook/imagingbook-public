/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.moments;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Complex;

/**
 * <p>
 * Naive implementation of Flusser's complex invariant moments [1]. See Sec.
 * 8.6.5 (Eq. 8.51 - 8.54) of [2] for additional details.
 * This abstract class defines static methods only.
 * </p>
 * <p>
 * [1] J. Flusser, B. Zitova, and T. Suk. "Moments and Moment Invariants in
 * Pattern Recognition". John Wiley and Sons (2009). 
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/17
 */
public abstract class FlusserMoments {
	
	private FlusserMoments() {}
	
	/** Length of moment vector returned by {@link #getInvariantMoments(Iterable)}. */
	public static final int N = 11;	// 11 moment invariants
	
	/**
	 * Calculates and returns a vector of 11 invariant moments
	 * for the specified set of 2D points.
	 * 
	 * @param points a set of 2D points
	 * @return the vector of invariant moments
	 */
	public static double[] getInvariantMoments(Iterable<Pnt2d> points) {
		Complex c11 = getScaleNormalizedMoment(points, 1, 1);
		Complex c12 = getScaleNormalizedMoment(points, 1, 2);
		Complex c21 = getScaleNormalizedMoment(points, 2, 1);
		Complex c20 = getScaleNormalizedMoment(points, 2, 0);
		Complex c22 = getScaleNormalizedMoment(points, 2, 2);
		Complex c30 = getScaleNormalizedMoment(points, 3, 0);
		Complex c31 = getScaleNormalizedMoment(points, 3, 1);
		Complex c40 = getScaleNormalizedMoment(points, 4, 0);
		
		double p1 = c11.getRe(); 
		double p2 = c21.multiply(c12).getRe();
		double p3 = c20.multiply(c12.pow(2)).getRe();
		double p4 = c20.multiply(c12.pow(2)).getIm();
		double p5 = c30.multiply(c12.pow(3)).getRe();
		double p6 = c30.multiply(c12.pow(3)).getIm();
		double p7 = c22.getRe();
		double p8 = c31.multiply(c12.pow(2)).getRe();
		double p9 = c31.multiply(c12.pow(2)).getIm();
		double p10 = c40.multiply(c12.pow(4)).getRe();
		double p11 = c40.multiply(c12.pow(4)).getIm();
		
		return new double[] {p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11};
	}
	
	/**
	 * Returns the scale-normalizes complex moment of order (p,q)
	 * for the specified set of 2D points.
	 * 
	 * @param points a set of 2D points
	 * @param p order index p
	 * @param q order index q
	 * @return the moment value
	 */
	public static Complex getScaleNormalizedMoment(Iterable<Pnt2d> points, int p, int q) {
		int xS = 0;
		int yS = 0;
		int n = 0;
		for (Pnt2d pnt : points) {
			xS = xS + pnt.getXint();
			yS = yS + pnt.getYint();
			n++;
		}
		
		double a = n;
		double xc = xS / n;
		double yc = yS / n;
		Complex sum = new Complex(0, 0);
		for (Pnt2d pnt : points) {
			double x = pnt.getX() - xc;
			double y = pnt.getY() - yc;
			Complex z1 = (x == 0.0 && y == 0 && p == 0) ? 
							Complex.ZERO : new Complex(x, y).pow(p);	// beware: 0^0 is undefined!
			Complex z2 = (x == 0.0 && y == 0 && q == 0) ? 
							Complex.ZERO : new Complex(x, -y).pow(q);
			sum = sum.add(z1.multiply(z2));
		}
		checkForNaN(sum);
		return sum.multiply(1.0 / Math.pow(a, 0.5 * (p + q) + 1));
	}
	
	private static void checkForNaN(Complex z) {
		if (z.isNaN()) {
			throw new RuntimeException("NaN encountered in complex quantity");
		}
	}
	
}
