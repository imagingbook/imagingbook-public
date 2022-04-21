/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.interpolation;
import imagingbook.common.image.access.ScalarAccessor;


public class NearestNeighborInterpolator implements PixelInterpolator {
	
	public NearestNeighborInterpolator() {
	}
	
	@Override
	public float getInterpolatedValue(ScalarAccessor ia, double x, double y) {
		final int u = (int) Math.rint(x);
		final int v = (int) Math.rint(y);
		return ia.getVal(u, v);
	}
	
}
