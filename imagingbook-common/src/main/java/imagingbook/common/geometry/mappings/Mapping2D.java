/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.mappings;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * Common interface to be implemented by all (linear and nonlinear) 2D mappings.
 * 
 * @author WB
 *
 */
public interface Mapping2D extends Cloneable {
	
	/**
	 * Applies this mapping to a single 2D point.
	 * 
	 * @param pnt the original point
	 * @return the transformed point
	 */
	public Pnt2d applyTo (Pnt2d pnt);
	
	/**
	 * Applies this mapping to an array of 2D points and
	 * returns a new array of points.
	 * 
	 * @param pnts the original points
	 * @return the transformed points
	 */
	public default Pnt2d[] applyTo(Pnt2d[] pnts) {
		Pnt2d[] outPnts = new Pnt2d[pnts.length];
		for (int i = 0; i < pnts.length; i++) {
			outPnts[i] = applyTo(pnts[i]);
		}
		return outPnts;
	}

}
