/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.image.access;

import ij.process.ImageProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.interpolation.PixelInterpolator.InterpolationMethod;

/** 
 * Accessor for vector-valued images with arbitrary depth 
 * (number of components).
 *
 */
public abstract class VectorAccessor extends ImageAccessor {
	
	final int depth;
	final ScalarAccessor[] componentAccessors;

	VectorAccessor(ImageProcessor ip, int depth, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		super(ip, obs, ipm);
		this.depth = depth;
		this.componentAccessors = new ScalarAccessor[this.depth];
		for (int k = 0; k < depth; k++) {
			componentAccessors[k] = makeComponentAccessor(k);
		}
	}
	
	/**
	 * To be implemented by all real sublasses of {@link ScalarAccessor}, 
	 * who know how to create an accessor object to their k-th component.
	 * See {@link RgbAccessor#makeComponentAccessor(int)} for an example.
	 * 
	 * @param k the component index
	 * @return the image accessor for the specified component
	 */
	abstract ScalarAccessor makeComponentAccessor(int k);
	
	@Override
	public int getDepth() {
		return this.depth;
	}
	
	@Override
	public ScalarAccessor getComponentAccessor(int k) {
		checkComponentIndex(k);
		return componentAccessors[k];
	}
	
	@Override
	public float getVal(int u, int v, int k) {
		checkComponentIndex(k);
		return componentAccessors[k].getVal(u, v);
	}
	
	@Override
	public float getVal(double x, double y, int k) {
		checkComponentIndex(k);
		return componentAccessors[k].getVal(x, y);
	}
	
	@Override
	public void setVal(int u, int v, int k, float val) {
		checkComponentIndex(k);
		componentAccessors[k].setVal(u, v, val);
	}
	
	// ---------------------------------------------------------------------
	
//	@Override
//	public void setDefaultValue(float val) {
//		for (int k = 0; k < depth; k++) {
//			componentAccessors[k].setDefaultValue(val);
//		}
//	}
	
//	@Override
//	public void setDefaultValue(float[] vals) {
//		if (vals.length != depth) {
//			throw new IllegalArgumentException("default values must be of length " + depth);
//		}
//		for (int k = 0; k < depth; k++) {
//			componentAccessors[k].setDefaultValue(vals[k]);
//		}
//	}
	
	@Override
	void checkComponentIndex(int k) {
		if (k < 0 || k >= depth) {
			throw new IllegalArgumentException("invalid component index " + k);
		}
	}

}
