/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.mappings;

import imagingbook.common.geometry.basic.Pnt2d;

public interface Mapping2D extends Cloneable {
	
	/**
	 * Applies this mapping to a single 2D point.
	 * 
	 * @param pnt the original point
	 * @return the transformed point
	 */
	Pnt2d applyTo (Pnt2d pnt);
	
	default Pnt2d[] applyTo(Pnt2d[] pnts) {
		Pnt2d[] outPnts = new Pnt2d[pnts.length];
		for (int i = 0; i < pnts.length; i++) {
			outPnts[i] = applyTo(pnts[i]);
		}
		return outPnts;
	}
	
	/**
	 * The inverse of this mapping is calculated (if possible)
	 * and returned. Implementing classes are supposed to
	 * override this default method.
	 * 
	 * @return the inverse mapping
	 */
	default Mapping2D getInverse() {
		throw new UnsupportedOperationException("Cannot invert mapping " + this.toString());
	}

	/**
	 * Returns the Jacobian matrix for this mapping, evaluated at
	 * the given 2D point.
	 * This method is only implemented for selected mappings.
	 * 
	 * @param pnt the 2D position to calculate the Jacobian for
	 * @return the Jacobian matrix
	 */
	default double[][] getJacobian(Pnt2d pnt) {
		throw new UnsupportedOperationException("No Jacobian available for this mapping");
	}
}
