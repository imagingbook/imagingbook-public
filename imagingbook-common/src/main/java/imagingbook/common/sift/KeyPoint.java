/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package imagingbook.common.sift;

import java.util.Locale;

/**
 * <p>
 * Represents a SIFT key point in hierarchical scale space. See Sec. 25.3 of [1]
 * for more details. This class is non-public, instances are immutable.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/20
 */
public class KeyPoint implements Cloneable, Comparable<KeyPoint> {
	
	/** octave index */
	public final int p;
	/** level index */
	public final int q;
	
	/** lattice x-position */
	public final int u;
	
	/** lattice y-position */
	public final int v;
	
	/** interpolated lattice x-position */
	public final float x;
	
	/** interpolated lattice y-position */
	public final float y;
	
	/** real x-position (in image coordinates) */
	public final float x_real;
	
	/** real y-position (in image coordinates) */	
	public final float y_real;
	
	/** absolute scale */
	public final float scale;
	
	/** magnitude of DoG response */
	public final float magnitude;
	
	// --------------------------------------------
	
	/** for debugging only */
	float[] orientation_histogram;
	/** dominant orientation (for debugging only) */
	double orientation;
	
	// --------------------------------------------
	
	/** Constructor (non-public). */
	KeyPoint(int p, int q, int u, int v, float x, float y, float x_real, float y_real, float scale, float magnitude) {
		this.p = p;
		this.q = q;
		this.u = u;
		this.v = v;
		this.x = x;
		this.y = y;
		this.x_real = x_real;
		this.y_real = y_real;
		this.scale = scale;
		this.magnitude = magnitude;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s[p=%d, q=%d, u=%d, v=%d, scale=%.2f, mag=%.2f]", 
				getClass().getSimpleName(), p, q, u, v, scale, magnitude);
	}
	
	@Override
	public KeyPoint clone() {
		try {
			return (KeyPoint) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override //used for sorting keypoints by decreasing gradient magnitude
	public int compareTo(KeyPoint other) {
		return Float.compare(other.magnitude, this.magnitude);
	}

}
