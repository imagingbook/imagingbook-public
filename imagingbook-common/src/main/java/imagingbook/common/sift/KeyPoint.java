/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.sift;

import java.util.Locale;

public class KeyPoint implements Cloneable, Comparable<KeyPoint> {
	public final int p;	// octave index
	public final int q;	// level index
	
	public final int u;	// lattice x-position 
	public final int v;	// lattice y-position 
	public float x;		// interpolated lattice x-position 
	public float y;		// interpolated lattice y-position 
	
	public float x_real;	// real x-position (in image coordinates)		
	public float y_real;	// real y-position (in image coordinates)		
	public float scale;		// absolute scale
	
	public final float magnitude;	// magnitude of DoG response
	
	protected float[] orientation_histogram;	// for debugging only
	protected double orientation;				// dominant orientation (for debugging only)
	
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
//		if (this.magnitude > other.magnitude) return -1;
//		if (this.magnitude < other.magnitude) return 1;
//		else return 0;
	}
	
	

}
