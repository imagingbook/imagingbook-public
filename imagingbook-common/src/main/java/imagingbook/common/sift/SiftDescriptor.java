/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.sift;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.VectorNorm;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Locale;

/**
 * <p>
 * This class defines a SIFT descriptor. See Sec. 25.3 of [1] for more details. Instances are immutable.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/20
 */
public class SiftDescriptor implements Pnt2d, Comparable<SiftDescriptor> {
	
	private final double x;	// image position
	private final double y;
	private final int scaleLevel;
	private final double scale;
	private final double magnitude;
	private final double orientation;
	private final int[] features;
	
	// --------------------------------------------------------------------
	
	// Constructor (non-public)
	SiftDescriptor(double x, double y, double scale, int scaleLevel, double magnitude, double orientation, int[] features) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.scaleLevel = scaleLevel;
		this.magnitude = magnitude;
		this.orientation = orientation;
		this.features = features;
	}
	
	// --------------------------------------------------------------------
	
	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	/**
	 * Returns this descriptor's absolute scale (&sigma;).
	 * 
	 * @return the absolute scale
	 */
	public double getScale() {
		return scale;
	}
	
	/**
	 * Returns this descriptor's scale level (scale space octave index p).
	 * 
	 * @return the scale level
	 */
	public int getScaleLevel() {
		return scaleLevel;
	}
	
	/**
	 * Returns this descriptor's gradient response magnitude (score).
	 * 
	 * @return the gradient response magnitude
	 */
	public double getMagnitude() {
		return magnitude;
	}

	/**
	 * Returns this descriptor's orientation angle (in radians).
	 * 
	 * @return the orientation angle
	 */
	public double getOrientation() {
		return orientation;
	}
	
	/**
	 * Returns this descriptor's feature vector (array of integers).
	 * 
	 * @return the feature vector
	 */
	public int[] getFeatures() {
		return features;
	}
	
	// -----------------------------
	
	private static final VectorNorm DefaultNorm = VectorNorm.L2.getInstance();

	/**
	 * Calculates and returns the distance between this descriptor's feature vector and another descriptor's feature
	 * vector using the Euclidean distance ({@link imagingbook.common.math.VectorNorm.L2}).
	 *
	 * @param other another {@link SiftDescriptor}
	 * @return the distance
	 */
	public double getDistance(SiftDescriptor other) {
		return DefaultNorm.distance(this.features, other.features);
	}

	/**
	 * Calculates and returns the distance between this descriptor's feature vector and another descriptor's feature
	 * vector using the specified {@link VectorNorm}.
	 *
	 * @param other another {@link SiftDescriptor}
	 * @param norm a {@link VectorNorm} instance
	 * @return the distance
	 */
	public double getDistance(SiftDescriptor other, VectorNorm norm) {
		return norm.distance(this.features, other.features);
	}
	
	// -----------------------------

	@Override	// returns an oriented M-shaped polygon centered at (x,y)
	public Shape getShape(double featureScale) {
		double d = featureScale * this.getScale();
		// create M-shaped path around origin oriented along x-axis:
		Path2D poly = new Path2D.Double();
		poly.moveTo(0, 0);
		poly.lineTo(d, d);
		poly.lineTo(-d, d);
		poly.lineTo(-d, -d);
		poly.lineTo(d, -d);
		poly.closePath();
		// translate and rotate shape:
		AffineTransform atf = new AffineTransform();
		atf.translate(this.getX(), this.getY());
		atf.rotate(this.getOrientation());
		return atf.createTransformedShape(poly);
	}
	
	// -----------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, 
				"%s[x=%.1f y=%.1f scale=%.2f mag=%.4f angle=%.2f]", 
				getClass().getSimpleName(), x, y, scale, magnitude, orientation);
	}

	
	@Override // for sorting SIFT descriptors by descreasing magnitude
	public int compareTo(SiftDescriptor other) {
		return Double.compare(other.magnitude, this.magnitude);
	}

}
