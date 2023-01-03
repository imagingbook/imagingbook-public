/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.sift.scalespace;

import ij.process.FloatProcessor;
import imagingbook.common.filter.linear.GaussianFilterSeparable;

/**
 * <p>
 * Represents a single scale level in a generic hierarchical scale space. See Secs. 25.1.4 for more details. Pixel data
 * are represented as one-dimensional {@code float} arrays. This class defines no public constructor.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/20 removed FloatProcessor as superclass
 */
public class ScaleLevel {
	
	private final int width, height;
	private final float[] data;
	
	private double absoluteScale;
	
	// ------------------------------
	
	/**
	 * Constructor (non-public).
	 */
	ScaleLevel(int width, int height, float[] data, double absoluteScale) {
		this.width = width;
		this.height = height;
		this.data = (data != null) ? data : new float[width * height];
		this.absoluteScale = absoluteScale;
	}
	
	/**
	 * Constructor (non-public).
	 */
	ScaleLevel(ScaleLevel level, double absoluteScale) {
		this(level.width, level.height, level.data.clone(), absoluteScale);
	}
	
	// ------------------------------
	
	/**
	 * Returns the width of this scale level.
	 * @return the width of this scale level
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the height of this scale level.
	 * @return the height of this scale level
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns a reference to the internal (one-dimensional) data array of this scale level.
	 *
	 * @return to the internal data array
	 */
	public float[] getData() {
		return this.data;
	}
	
	/**
	 * Returns the absolute scale assigned to this scale level.
	 * 
	 * @return the absolute scale
	 */
	public double getAbsoluteScale() {
		return this.absoluteScale;
	}
	
	// ------------------------------

	/**
	 * Returns a new ImageJ {@link FloatProcessor} with the same size and pixel data as this scale level. Note that the
	 * pixel data are not duplicated but shared, i.e., subsequent modifications to the new {@link FloatProcessor} are
	 * transparent and directly affect the contents of this scale level. Thus the resulting {@link FloatProcessor} only
	 * serves as a wrapper for the data in this scale level.
	 *
	 * @return a new {@link FloatProcessor} instance
	 */
	FloatProcessor toFloatProcessor() {
		return new FloatProcessor(this.width, this.height, this.data);
	}

	/**
	 * Decimates this scale level by factor 2 in both directions and returns a new scale level.
	 *
	 * @return a new, decimated scale level
	 */
	ScaleLevel decimate() {	// returns a 2:1 subsampled copy of this ScaleLevel
		final int w1 = this.getWidth();
		final int h1 = this.getHeight();
		final int w2 = w1 / 2;
		final int h2 = h1 / 2;
		
		// new (decimated) level has the same absolute scale:
		ScaleLevel level2 = new ScaleLevel(w2, h2, null, this.absoluteScale);
		// resample data:
		for (int v2 = 0 ; v2 < h2; v2++) {
			int v1 = 2 * v2;
			for (int u2 = 0 ; u2 < w2; u2++) {
				int u1 = 2 * u2;
				level2.setValue(u2, v2, this.getValue(u1, v1));
			}
		}
		return level2; //new ScaleLevel(w2, h2, pixels2, absoluteScale);
	}
	
	// sometimes we need to set the scale after instantiation:
	void setAbsoluteScale(double sigma) {
		this.absoluteScale = sigma;
	}

	/**
	 * Returns the element value at the specified position of this scale level. An exception is thrown if the position
	 * is outside the scale level's boundaries.
	 *
	 * @param u horizontal position
	 * @param v vertical position
	 * @return the element value
	 */
	public float getValue(int u, int v) {
		return this.data[v * this.width + u];
	}

	/**
	 * Sets the element value at the specified position of this scale level. An exception is thrown if the position is
	 * outside the scale level's boundaries.
	 *
	 * @param u horizontal position
	 * @param v vertical position
	 * @param val the new element value
	 */
	private void setValue(int u, int v, float val) {
		this.data[v * this.width + u] = val;
	}

	/**
	 * Collects and returns the 3x3 neighborhood values at this scale level at center position (u,v). The result is
	 * stored in the supplied 3x3 array.
	 *
	 * @param u horizontal position
	 * @param v vertical position
	 * @param nh the 3x3 array where to insert the neighborhood values
	 */
	void get3x3Neighborhood(final int u, final int v, final float[][] nh) {
		for (int i = 0, x = u - 1; i < 3; i++, x++) {
			for (int j = 0, y = v - 1; j < 3; j++, y++) {
				nh[i][j] = this.getValue(x, y);
			}
		}
	}

	/**
	 * Calculates the gradient at the specified scale level position in polar form. The results (gradient magnitude and
	 * direction) are placed in the supplied 2-element array.
	 *
	 * @param u horizontal position
	 * @param v vertical position
	 * @param grad a 2-element array for gradient magnitude and direction
	 */
	public void getGradientPolar(int u, int v, final double[] grad) {
		final double grad_x = this.getValue(u+1, v) - this.getValue(u-1, v);	// x-component of local gradient
		final double grad_y = this.getValue(u, v+1) - this.getValue(u, v-1);	// y-component of local gradient
		grad[0] = Math.hypot(grad_x, grad_y);						// local gradient magnitude (E)
		grad[1] = Math.atan2(grad_y, grad_x);						// local gradient direction (phi)
	}


	/**
	 * Applies a Gaussian filter to this scale level, which is modified.
	 *
	 * @param sigma the width of the Gaussian
	 */
	void filterGaussian(double sigma) {
		FloatProcessor fp = this.toFloatProcessor();
		new GaussianFilterSeparable(sigma).applyTo(fp);
	}
	
	// ---------------------------------------
	
	@Override
	public String toString() {
		return String.format("%s[w=%d h=%d absScale=%.4f]", getClass().getSimpleName(), width, height, absoluteScale);
	}
}
