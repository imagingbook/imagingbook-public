/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.regions;

import java.util.List;


/**
 * This interface defines the functionality of region segmenters
 * that perform contour extraction.
 * 
 * @version 2020/04/01
 * 
 */
public interface ContourTracer {
	
	/**
	 * Retrieves all inner contours of the associated region labeling.
	 * @return the list of inner contours.
	 */
	public List<Contour.Inner> getInnerContours();
	
	/**
	 * Retrieves all inner contours of the associated region labeling.
	 * @param sort set true to sort contours by (descending) length.
	 * @return the list of inner contours.
	 */
	public List<Contour.Inner> getInnerContours(boolean sort);
	
	/**
	 * Retrieves all outer contours of the associated region labeling.
	 * @return the list of outer contours.
	 */
	public List<Contour.Outer> getOuterContours();
	
	/**
	 * Retrieves all outer contours of the associated region labeling.
	 * @param sort set true to sort contours by (descending) length.
	 * @return the list of outer contours.
	 */
	public List<Contour.Outer> getOuterContours(boolean sort);
	
}
