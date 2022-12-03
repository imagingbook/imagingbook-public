/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.geometry.fitting.line;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.line.AlgebraicLine;

/**
 * Interface to be implemented by all 2D line fits.
 * 
 * @author WB
 * @version 2022/09/22
 */
public interface LineFit {
	
	/**
	 * Returns the size of the point set used for calculating this line fit.
	 * @return the number of sample points used for fitting
	 */
	public abstract int getSize();
	
	/**
	 * Returns the parameters [A, B, C] for the {@link AlgebraicLine}
	 * associated with this line fit. To be implemented by concrete
	 * classes. {@code null} is returned if no fit was found.
	 * 
	 * @return algebraic line parameters [A, B, C]
	 * @see AlgebraicLine
	 */
	public abstract double[] getLineParameters();

	/**
	 * Returns the {@link AlgebraicLine} associated with this line fit.
	 * {@code null} is returned if no fit was found.
	 * 
	 * @return an {@link AlgebraicLine} instance
	 */
	public default AlgebraicLine getLine() {
		double[] p = this.getLineParameters();
		if (p == null) {
			return null;
		}
		else {
			return new AlgebraicLine(p);
		}
	}
	
	/**
	 * Calculates and returns the sum of the squared orthogonal distances
	 * of the specified points for this line fit.
	 * 
	 * @param points an array of 2D points
	 * @return the squared orthogonal error
	 */
	public default double getSquaredOrthogonalError(Pnt2d[] points) {
		AlgebraicLine line = getLine();
		return line.getSquareError(points);
	}
}
