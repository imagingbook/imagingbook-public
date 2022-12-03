/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.geometry.mappings.nonlinear;


import static imagingbook.common.math.Arithmetic.mod;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.hypot;
import static java.lang.Math.log1p;
import static java.lang.Math.sin;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.Inversion;
import imagingbook.common.geometry.mappings.Mapping2D;

/**
 * <p>
 * This class implements a 2D log-polar mapping transformation. Simple version
 * (Version 1), maps radius [0,rmax] to [0,nr]). See Sec. 21.1.6 (Eq. 21.65 -
 * 21.70) of [1] for additional details and examples.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/16
 */
public class LogPolarMapping1 implements Mapping2D, Inversion {
	
	private static final double PI2 = 2 * Math.PI;
	
	private final double xc, yc;			// center point in source image
	private final int P;					// # of radial steps
	private final double c1, c2, c3, c4; 	// pre-calculated constants
	
	/**
	 * Constructor.
	 * @param xc x-coordinate of center point in source image
	 * @param yc y-coordinate of center point in source image
	 * @param P number of radial steps
	 * @param Q number of angular steps
	 * @param rmax maximum radius
	 */
	public LogPolarMapping1(double xc, double yc, int P, int Q, double rmax) {
		this.P = P;
		this.xc = xc;
		this.yc = yc;
		this.c1 = this.P / log1p(rmax);
		this.c2 = Q / PI2;
		this.c3 = PI2 / Q;
		this.c4 = log1p(rmax) / P;
	}
	
	// --------------------------------------------------------------------
	
	@Override
	public Pnt2d applyTo(Pnt2d xy) {					// image -> log-polar
		double dx = xy.getX() - xc;
		double dy = xy.getY() - yc;
		double r = hypot(dx, dy);
		double rho = c1 * log1p(r);						// = Math.log(r + 1)	
		double theta = mod(atan2(dy, dx), PI2);			// theta in [0, 2 PI)
		double omega = c2 * theta;
		return Pnt2d.from(rho, omega);
	}
	
	@Override
	public Mapping2D getInverse() {						// log-polar -> image
		return new Mapping2D() {
			@Override
			public Pnt2d applyTo(Pnt2d ra) {
				double rho = ra.getX();
				double omega = ra.getY();
				double r = exp(rho * c4) - 1;
				double theta = c3 * omega;
				double x = r * cos(theta);
				double y = r * sin(theta);
				return Pnt2d.from(xc + x, yc + y);
			}
		};
	}

}
