/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.sift;


import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * SIFT descriptor.
 * Added magnitude field and Comparable interface for easy sorting.
 * 2020/10/04: Added scale level for display reasons.
 * 
 * @author W. Burger
 * @version 2020/10/04
 */
public class SiftDescriptor implements Pnt2d, Comparable<SiftDescriptor> {
	
	private final double x;	// image position
	private final double y;
	private final int scaleLevel;
	private final double scale;
	private final double magnitude;
	private final double orientation;
	private final int[] features;
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getScale() {
		return scale;
	}
	
	public int getScaleLevel() {
		return scaleLevel;
	}
	
	public double getMagnitude() {
		return magnitude;
	}

	public double getOrientation() {
		return orientation;
	}
	public int[] getFeatures() {
		return features;
	}

	public SiftDescriptor(double x, double y, double scale, int scaleLevel, double magnitude, double orientation, int[] features) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.scaleLevel = scaleLevel;
		this.magnitude = magnitude;
		this.orientation = orientation;
		this.features = features;
	}
	
	// -----------------------------
	
	public double getDistanceL1(SiftDescriptor other) {
		int[] f1 = this.features;
		int[] f2 = other.features;
		int sum = 0;
		for (int i=0; i<f1.length; i++) {
			sum = sum + Math.abs(f1[i] - f2[i]);
		}
		return sum;
	}
	
	public double getDistanceL2(SiftDescriptor other) {
		int[] f1 = this.features;
		int[] f2 = other.features;
		int sum = 0;
		for (int i = 0; i < f1.length; i++) {
			int d = f1[i] - f2[i];
			sum = sum + d * d;
		}
		return Math.sqrt(sum);
	}
	
	public double getDistanceLinf(SiftDescriptor other) {
		int[] f1 = this.features;
		int[] f2 = other.features;
		int dmax = 0;
		for (int i = 0; i < f1.length; i++) {
			int d = Math.abs(f1[i] - f2[i]);
			dmax = Math.max(dmax, d);
		}
		return dmax;
	}
	
	// -----------------------------
	
	@Override
	public Shape getShape(double featureScale) {
		final double DisplayAngleOffset = -Math.PI / 2;
		double x = this.getX(); 
		double y = this.getY();
		double scale = featureScale * this.getScale();
		double orient = this.getOrientation() + DisplayAngleOffset;
		double sin = Math.sin(orient);
		double cos = Math.cos(orient);
		Path2D poly = new Path2D.Double();	
		poly.moveTo(x + (sin - cos) * scale, y - (sin + cos) * scale);
		poly.lineTo(x + (sin + cos) * scale, y + (sin - cos) * scale);
		poly.lineTo(x, y);
		poly.lineTo(x - (sin - cos) * scale, y + (sin + cos) * scale);
		poly.lineTo(x - (sin + cos) * scale, y - (sin - cos) * scale);
		poly.closePath();
		return poly;
	}
	
	// -----------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, 
				"x=%.1f y=%.1f s=%.2f mag=%.4f angle=%.2f", 
				x, y, scale, magnitude, orientation);
	}

	//used for sorting SIFT descriptors by magnitude
	@Override
	public int compareTo(SiftDescriptor d2) {
		if (this.magnitude > d2.magnitude) return -1;
		if (this.magnitude < d2.magnitude) return 1;
		else return 0;
	}



}
