/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.geometry.mappings.nonlinear;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.mappings.Mapping2D;

/*
 * Note: This class has been deprecated and substituted by an ImageJ plugin.
 */
@Deprecated
public class TwirlMapping implements Mapping2D {
	
	private final double xc, yc, angle, rad;
   
	public TwirlMapping (double xc, double yc, double angle, double rad) {
		this.xc = xc;
		this.yc = yc;
		this.angle = angle;
		this.rad = rad;
	}

	public Pnt2d applyTo(Pnt2d pnt) {
		final double x = pnt.getX();
		final double y = pnt.getY();
		double dx = x - xc;
		double dy = y - yc;
		double d = Math.sqrt(dx*dx + dy*dy);
		if (d < rad) {
			double a = Math.atan2(dy,dx) + angle * (rad-d) / rad;
			double x1 = xc + d * Math.cos(a);
			double y1 = yc + d * Math.sin(a);
			return PntDouble.from(x1, y1);
		}
		return PntDouble.from(x, y);
	}
}




