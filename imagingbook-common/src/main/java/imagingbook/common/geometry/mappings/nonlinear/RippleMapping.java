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

/**
 * A non-linear mapping that produces a ripple effect.
 * The transformation is implicitly inverted, i.e., maps target to source image
 * coordinates.
 * Note: This class has been deprecated and substituted by an ImageJ plugin.
 * 
 * @author WB
 *
 */
@Deprecated
public class RippleMapping implements Mapping2D {
	final double xWavel; // = 20;
	final double yWavel ; // = 100;
	final double xAmpl ; // = 0;
	final double yAmpl ; // = 10;
   
	public RippleMapping (double xWavel, double xAmpl, double yWavel, double yAmpl) {
		this.xWavel = xWavel;
		this.yWavel = yWavel;
		this.xAmpl = xAmpl;
		this.yAmpl = yAmpl;
	}

	@Override
	public Pnt2d applyTo(Pnt2d pnt) {
		final double x = pnt.getX();
		final double y = pnt.getY();
		double xx = x + xAmpl * Math.sin(y / xWavel);
		double yy = y + yAmpl * Math.sin(x / yWavel);
		return PntDouble.from(xx, yy);
	}
}




