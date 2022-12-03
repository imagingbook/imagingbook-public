/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.geometry.basic;

/**
 * Interface specifying a 2D curve capable of calculating the minimum
 * distance to a given point.
 *  
 * @author WB
 *
 */
public interface Primitive2d {
	
	/**
	 * Returns the minimum absolute distance from the specified point to this curve.
	 * 
	 * @param p a 2D point
	 * @return the minimum distance (always positive)
	 */
	public double getDistance(Pnt2d p);
	
}
