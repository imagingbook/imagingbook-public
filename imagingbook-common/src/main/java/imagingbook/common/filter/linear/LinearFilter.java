/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.filter.linear;

import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.generic.GenericFilterScalar;
import imagingbook.common.image.PixelPack.PixelSlice;

/**
 * This class represents a 2D linear filter specified by an
 * arbitrary 2D convolution kernel.
 * It is based on {@link GenericFilter} and {@link GenericFilterScalar},
 * which take care of all data copying and filter mechanics.
 * Since it is a "scalar" filter, pixel values are treated as scalars.
 * If the processed image has more than one component 
 * (e.g., a RGB color image), this filter is automatically 
 * and independently applied to all (scalar-valued) components.
 * To apply to an image, use the {@link #applyTo(ij.process.ImageProcessor)}
 * method, for example.
 */
public class LinearFilter extends GenericFilterScalar {

	private final float[][] H;			// the kernel matrix, note H[y][x]!
	private final int xc, yc;			// 'hot spot' coordinates
	
	/**
	 * Constructor, only the 2D filter kernel needs to be specified.
	 * 
	 * @param kernel the 2D filter kernel
	 */
	public LinearFilter(Kernel2D kernel) {
		this.H = kernel.getH();
		this.xc = kernel.getXc();
		this.yc = kernel.getYc();
	}

	@Override
	protected float doPixel(PixelSlice plane, int u, int v) {
		double sum = 0;
		for (int j = 0; j < H.length; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < H[j].length; i++) {
				int ui = u + i - xc;
				sum = sum + plane.getVal(ui, vj) * H[j][i];
			}
		}
 		return (float)sum;
//		return convolve(source, H, u, v, xc, yc);
	}
	
//	public static float convolve(PixelSlice plane, float[][] H, int u, int v, int xc, int yc) {
//		double sum = 0;
//		for (int j = 0; j < H.length; j++) {
//			int vj = v + j - yc;
//			for (int i = 0; i < H[j].length; i++) {
//				int ui = u + i - xc;
//				sum = sum + plane.getVal(ui, vj) * H[j][i];
//			}
//		}
// 		return (float)sum;
//	}

}
