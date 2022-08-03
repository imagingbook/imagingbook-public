/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.sift;

import java.util.Locale;

/**
 * Represents a SIFT key point in hierarchical scale space.
 * 
 * @author WB
 *
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
	public float x;
	/** interpolated lattice y-position */
	public float y;
	
	/** real x-position (in image coordinates) */
	public float x_real;
	/** real y-position (in image coordinates) */	
	public float y_real;
	/** absolute scale */
	public float scale;
	
	/** magnitude of DoG response */
	public final float magnitude;
	
	/** for debugging only */
	protected float[] orientation_histogram;
	/** dominant orientation (for debugging only) */
	protected double orientation;
	
	protected KeyPoint(int p, int q, int u, int v, float x, float y, float x_real, float y_real, float scale, float magnitude) {
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
		return String.format(Locale.US, "p=%d, q=%d, u=%d, v=%d, scale=%.2f, mag=%.2f", p, q, u, v, scale, magnitude);
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

	@Override
	public int compareTo(KeyPoint other) {
		//used for sorting keypoints by magnitude
		return Float.compare(other.magnitude, this.magnitude);
	}
	
	

}
