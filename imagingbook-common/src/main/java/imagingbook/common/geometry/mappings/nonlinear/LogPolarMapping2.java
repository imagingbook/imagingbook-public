/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.mappings.nonlinear;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.Inversion;
import imagingbook.common.geometry.mappings.Mapping2D;
import imagingbook.common.math.Arithmetic;

import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.hypot;
import static java.lang.Math.log;
import static java.lang.Math.sin;

/**
 * <p>
 * This class implements a 2D log-polar mapping transformation. Improved version (Version 2), maps radius [rmin,rmax] to
 * [0,nr]). See Sec. 21.1.6 (Eq 21.71 - 21.74, Alg. 21.1) of [1] for additional details and examples.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/16
 */
public class LogPolarMapping2 implements Mapping2D, Inversion {
	
	private static final double PI2 = 2 * Math.PI;
	
	private final double rmin;		// min. radius
	private final double xc, yc;	// center point in source image
	private final double c1, c2;	// pre-calculated constants
	
	/**
	 * Constructor.
	 * @param xc x-coordinate of center point in source image
	 * @param yc y-coordinate of center point in source image
	 * @param P number of radial steps
	 * @param Q number of angular steps
	 * @param rmax maximum radius
	 * @param rmin minimum radius
	 */
	public LogPolarMapping2(double xc, double yc, int P, int Q, double rmax, double rmin) {
		this.xc = xc;
		this.yc = yc;
		this.rmin = (rmin > 0) ? rmin : rmax / exp(2 * Math.PI * (P - 1) / Q);
		this.c1 = P / log(rmax / rmin);
		this.c2 = Q / PI2;
	}
		
	// --------------------------------------------------------------------
	
	@Override
	public Pnt2d applyTo(Pnt2d xy) { 	// image -> log-polar
		double dx = xy.getX() - xc;
		double dy = xy.getY() - yc;
		double r = hypot(dx, dy); 		// = Math.sqrt(sqr(dx) + sqr(dy));
		if (r < rmin) {
			throw new IllegalArgumentException("radius < rmin for xy = " + xy);	// this should not happen!
		}
		double rho = c1 * log(r / rmin);
		double theta = Arithmetic.mod(Math.atan2(dy, dx), PI2); // theta in [0, 2 PI)
		double omega = c2 * theta;
		return Pnt2d.from(rho, omega);
	}
	
	// --------------------------------------------------------------------
	
	@Override
	public Mapping2D getInverse() {
		return new Mapping2D() {	
			@Override
			public Pnt2d applyTo(Pnt2d rw) {	// log-polar -> image, rw = (rho, omega)
				double rho   = rw.getX();
				double omega = rw.getY();
				double r =  exp(rho / c1) * rmin;
				double theta = omega / c2;
				double x = xc + r * cos(theta);
				double y = yc + r * sin(theta);
				return Pnt2d.from(x, y);
			}
		};
	}
	
}
