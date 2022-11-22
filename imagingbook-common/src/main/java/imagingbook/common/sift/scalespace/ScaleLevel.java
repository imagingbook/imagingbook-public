/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.sift.scalespace;

import ij.process.FloatProcessor;
import imagingbook.common.filter.linear.GaussianFilterSeparable;
import imagingbook.common.math.Matrix;

/**
 * <p>
 * Represents a single scale level in a generic hierarchical scale space. See
 * Secs. 25.1.4 for more details.
 * Pixel data are represented as one-dimensional {@code float} arrays.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/20 removed FloatProcessor as superclass
 */
public class ScaleLevel {
	
	private final int width, height;
	private final float[] data;
	
	private double absoluteScale;		// TODO: should be final
	
	// ------------------------------
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public float[] getData() {
		return this.data;
	}
	
	public double getAbsoluteScale() {
		return this.absoluteScale;
	}
	
	// ------------------------------
	
	ScaleLevel(int width, int height, float[] data, double absoluteScale) {
		this.width = width;
		this.height = height;
		this.data = (data != null) ? data : new float[width * height];
		this.absoluteScale = absoluteScale;
	}
	
	ScaleLevel(FloatProcessor fp, double absoluteScale) {
		this(fp.getWidth(), fp.getHeight(), getValues((float[])fp.getPixels(), true), absoluteScale);
	}
	
	ScaleLevel(ScaleLevel level) {
		this(level.width, level.height, level.data.clone(), level.absoluteScale);
	}
	
	/**
	 * Returns a copy of the specified pixel data and optionally
	 * normalizes to [0,1].
	 * 
	 * @param data the original pixel data
	 * @param normalize pass true to normalize data to [0,1]
	 * @return a copy of the specified pixel data
	 */
	private static float[] getValues(float[] data, boolean normalize) {
		float[] values = data.clone();
		if (normalize) {
			float minVal = Matrix.min(values);
			float maxVal = Matrix.max(values);
			float offset = -minVal;
			float scale = 1.0f / (maxVal - minVal);
			for (int i = 0; i < values.length; i++) {
				values[i] = (values[i] + offset) * scale; 
			}
		}
		return values;
	}
	
	// ------------------------------

	@Deprecated // does not belong here!
	void filterGaussian(double sigma) {
		FloatProcessor fp = this.toFloatProcessor();
		new GaussianFilterSeparable(sigma).applyTo(fp);	// TODO: validate change!
	}
	
	/**
	 * Returns a copy of this scale level with identical size and duplicated
	 * data.
	 * 
	 * @return a copy of this scale level
	 */
	public ScaleLevel duplicate() {
		return new ScaleLevel(this);
	}
	
	/**
	 * Decimates this scale level by factor 2 in both directions and returns a 
	 * new scale level.
	 *  
	 * @return a new, decimated scale level
	 */
	ScaleLevel decimate() {	// returns a 2:1 subsampled copy of this ScaleLevel
		int w1 = this.getWidth();
		int h1 = this.getHeight();
		int w2 = w1 / 2;
		int h2 = h1 / 2;
		
		ScaleLevel level2 = new ScaleLevel(w2, h2, null, absoluteScale);
		
		for (int v2 = 0 ; v2 < h2; v2++) {
			int v1 = 2 * v2;
			for (int u2 = 0 ; u2 < w2; u2++) {
				int u1 = 2 * u2;
				level2.setValue(u2, v2, this.getValue(u1, v1));
			}
		}
		return level2; //new ScaleLevel(w2, h2, pixels2, absoluteScale);
	}
	
	// TODO: this sould be eliminated
	void setAbsoluteScale(double sigma) {
		this.absoluteScale = sigma;
	}
	

	/**
	 * Returns the element value at the specified position of this scale level.
	 * An exception is thrown if the position is outside the scale level's
	 * boundaries.
	 * 
	 * @param u horizontal position
	 * @param v vertical position
	 * @return the element value
	 */
	public float getValue(int u, int v) {
		return this.data[v * this.width + u];
	}
	
	/**
	 * Sets the element value at the specified position of this scale level.
	 * An exception is thrown if the position is outside the scale level's
	 * boundaries.
	 * 
	 * @param u horizontal position
	 * @param v vertical position
	 * @param val the new element value
	 */
	private void setValue(int u, int v, float val) {
		this.data[v * this.width + u] = val;
	}
	
	/**
	 * Collects and returns the 3x3 neighborhood values at this scale level 
	 * at center position (u,v). The result is stored in the supplied 3x3 array.
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
	 * Calculates the gradient at the specified scale level position in polar form.
	 * The results (gradient magnitude and direction) are placed in the supplied
	 * 2-element array.
	 * 
	 * @param u    horizontal position
	 * @param v    vertical position
	 * @param grad a 2-element array for gradient magnitude and direction
	 */
	public void getGradientPolar(int u, int v, final double[] grad) {
		final double grad_x = this.getValue(u+1, v) - this.getValue(u-1, v);	// x-component of local gradient
		final double grad_y = this.getValue(u, v+1) - this.getValue(u, v-1);	// y-component of local gradient
		grad[0] = Math.hypot(grad_x, grad_y);						// local gradient magnitude (E)
		grad[1] = Math.atan2(grad_y, grad_x);						// local gradient direction (phi)
	}
	
	/**
	 * Returns a new ImageJ {@link FloatProcessor} with the same size and pixel data
	 * as this scale level. Note that the pixel data are not duplicated but shared,
	 * i.e., subsequent modifications to the new {@link FloatProcessor} are
	 * transparent and directly affect the contents of this scale level.
	 * 
	 * @return a new {@link FloatProcessor} instance
	 */
	public FloatProcessor toFloatProcessor() {
		return new FloatProcessor(this.width, this.height, this.data);
	}
}
