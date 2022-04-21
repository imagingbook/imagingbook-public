/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.filters;

import java.util.Arrays;

import ij.process.ImageProcessor;
import imagingbook.common.filter.GenericFilter;
import imagingbook.common.filter.GenericFilterScalar;
import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.common.image.data.PixelPack.PixelSlice;

/**
 * Ordinary (scalar) median filter for color images implemented
 * by extending the {@link GenericFilter} class.
 * Color images are filtered individually in all channels.
 * 
 * @author W. Burger
 * @version 2020/12/31
 */
public class ScalarMedianFilter extends GenericFilterScalar {
	
	final Parameters params;
	final CircularMask mask;
	final int maskCount;
	final int xc, yc;
	final int[][] maskArray;
	final float[] supportRegion;
	final int medianIndex;
	
	
	public ScalarMedianFilter(ImageProcessor ip) {
		this(new Parameters());
	}
	
	public ScalarMedianFilter(Parameters params) {
		this.params = params;
		this.mask = new CircularMask(params.radius);
		this.maskCount = mask.getCount();
		this.xc = this.yc = mask.getCenter();
		this.maskArray = mask.getMask();
		this.supportRegion = new float[maskCount];
		this.medianIndex = maskCount/2;
	}

	public static class Parameters {
		/** Filter radius */
		public double radius = 3.0;
		/** Out-of-bounds strategy */
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NearestBorder;
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	protected float doPixel(PixelSlice plane, int u, int v) {
		int k = 0;
		for (int i = 0; i < maskArray.length; i++) {
			int ui = u + i - xc;
			for (int j = 0; j < maskArray[0].length; j++) {
				if (maskArray[i][j] > 0) {
					int vj = v + j - yc;
					supportRegion[k] = plane.getVal(ui, vj);
					k = k + 1;
				}
			}
		}
		Arrays.sort(supportRegion);
		return supportRegion[medianIndex];
	}
}
