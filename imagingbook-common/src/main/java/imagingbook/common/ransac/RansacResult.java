/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.ransac;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Primitive2d;

/**
 * <p>
 * Represents a single detection result returned by an implementation of {@link RansacDetector}. Implements the
 * {@link Comparable} interface for sorting by detection score. See Sec. 12.1 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @param <T> generic primitive type
 * @author WB
 * @version 2022/11/19
 */
public class RansacResult<T extends Primitive2d> implements Comparable<RansacResult<T>> {
	
	private final T primitiveInit;
	private final T primitiveFinal;
	private final double score;
	private final Pnt2d[] draw;
	private final Pnt2d[] inliers;
	
	// full constructor (initially inliers = null)
	public RansacResult(Pnt2d[] draw, T primitiveInit, T primitiveFinal, double score, Pnt2d[] inliers) {
		this.primitiveInit = primitiveInit;
		this.primitiveFinal = primitiveFinal;
		this.score = score;
		this.draw = draw;
		this.inliers = inliers;
	}

	/**
	 * Returns the initial primitive (e.g., a circle) obtained from the minimum number of randomly drawn points.
	 *
	 * @return the initial primitive
	 */
	public T getPrimitiveInit() {
		return primitiveInit;
	}
	
	/**
	 * Returns the final primitive obtained after fitting numerically to the associated inlier points.
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
	
	@Override
	public int compareTo(RansacResult<T> other) {
		return Double.compare(other.score, this.score);
	}

}
