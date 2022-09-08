/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.edge;

import java.util.Iterator;
import java.util.List;

import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.util.ArrayIterator;

/**
 * Represents a chain of connected edge points (integer pixel coordinates).
 * As is, this could have also be implemented as a generic {@link List} but
 * additional properties may be added in a future version.
 *
 * @author WB
 * @version 2022/09/07 made immutable, add() method removed
 */
public class EdgeTrace implements Iterable<PntInt> {
	
	private final PntInt[] edgePoints;
	
	/**
	 * Constructor, creates a {@link EdgeTrace}
	 * from the specified points.
	 * 
	 * @param points an array of {@link PntInt} elements
	 */
	public EdgeTrace(PntInt[] points) {
		if (points.length == 0) {
			throw new IllegalArgumentException("point array must not be empty");
		}
		this.edgePoints = points;
	}
	
	/**
	 * Constructor, creates a {@link EdgeTrace}
	 * from the specified points.
	 * 
	 * @param points a list of {@link PntInt} elements
	 */
	public EdgeTrace(List<PntInt> points) {
		this(points.toArray(new PntInt[0]));
	}
	
	/**
	 * Returns the size of (number of points in) this edge trace.
	 * @return the size
	 */
	public int size() {
		return edgePoints.length;
	}
	
	/**
	 * Returns an array of points contained in this 
	 * edge trace.
	 * 
	 * @return an array of edge points
	 */
	public PntInt[] getPoints() {
		return edgePoints.clone();
	}

	@Override
	public Iterator<PntInt> iterator() {
		return new ArrayIterator<>(edgePoints);
	}
	
	@Override
	public String toString() {
		return String.format("%s: start=%s length=%d", 
				this.getClass().getSimpleName(), edgePoints[0].toString(), size());
	}

}
