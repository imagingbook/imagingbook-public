/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.line;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.line.AlgebraicLine;

public interface LineFit {
	
	public abstract int getSize();
	
	public abstract double[] getLineParameters();

	public default AlgebraicLine getLine() {
		double[] p = this.getLineParameters();
		if (p == null) {
			return null;
		}
		else {
			return new AlgebraicLine(p);
		}
	}
	
//	public abstract Pnt2d[] getPoints();
	
	public default double getOrthogonalError(Pnt2d[] points) {
//		final Pnt2d[] points = getPoints();
		AlgebraicLine line = getLine();
		return line.getSquareError(points);
	}
}
