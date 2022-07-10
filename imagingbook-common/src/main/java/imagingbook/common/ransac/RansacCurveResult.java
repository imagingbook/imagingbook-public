/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.ransac;

import imagingbook.common.geometry.basic.Curve2d;
import imagingbook.common.geometry.basic.Pnt2d;


/**
 * Represents a single detection result returned by an implementation of 
 * {@link RansacCurveDetector}.
 * 
 * @author WB
 *
 * @param <T> generic primitive type
 */
public class RansacCurveResult<T extends Curve2d> {
	
	private final T primitiveInit;
	private final T primitiveFinal;
	private final double score;
	private final Pnt2d[] draw;
	private final Pnt2d[] inliers;
	
	// full constructor (initially inliers = null)
	public RansacCurveResult(Pnt2d[] draw, T primitiveInit, T primitiveFinal, double score, Pnt2d[] inliers) {
		this.primitiveInit = primitiveInit;
		this.primitiveFinal = primitiveFinal;
		this.score = score;
		this.draw = draw;
		this.inliers = inliers;
	}
	
	/**
	 * Returns the initial primitive (e.g., a circle) obtained from the randomly 
	 * drawn points.
	 * @return the initial primitive
	 */
	public T getPrimitiveInit() {
		return primitiveInit;
	}
	
	/**
	 * Returns the final primitive obtained by fitting to all inlier points.
	 * @return the final primitive
	 */
	public T getPrimitiveFinal() {
		return primitiveFinal;
	}
	
	/**
	 * Returns the score (number of inliers) associated with this result.
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Returns the randomly drawn points that lead to this result.
	 * @return array of points
	 */
	public Pnt2d[] getDraw() {
		return draw;
	}
	
	/**
	 * Returns the set of inliers (points) associated with this result.
	 * @return array of points
	 */
	public Pnt2d[] getInliers() {
		return inliers;
	}
	
	// ---------------------------------------------------------------------------
	

	
	
	public int compareTo(RansacCurveResult<T> other) {
		return Double.compare(other.score, this.score);
	}

}
