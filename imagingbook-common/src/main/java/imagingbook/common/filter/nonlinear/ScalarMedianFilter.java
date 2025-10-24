/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.filter.nonlinear;

import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.generic.GenericFilterScalar;
import imagingbook.common.filter.mask.CircularMask;
import imagingbook.common.image.PixelPack.PixelSlice;
import imagingbook.common.util.ParameterBundle;

import java.util.Arrays;

/**
 * <p>
 * Ordinary (scalar) median filter for color images implemented by extending the {@link GenericFilter} class. Color
 * images are filtered individually in all channels. See Sec. 15.2.1 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2020/12/31
 */
public class ScalarMedianFilter extends GenericFilterScalar {
	
	public static class Parameters implements ParameterBundle<ScalarMedianFilter> {
		/** Filter radius */
		public double radius = 3.0;
	}
	
	private final CircularMask mask;
	private final int maskCount;
	private final int xc, yc;
	private final byte[][] maskArray;
	private final float[] supportRegion;
	private final int medianIndex;
	
	public ScalarMedianFilter() {
		this(new Parameters());
	}
	
	public ScalarMedianFilter(Parameters params) {
		this.mask = new CircularMask(params.radius);
		this.maskCount = mask.getElementCount();
		this.xc = mask.getCenterX();
		this.yc = mask.getCenterY();
		this.maskArray = mask.getByteArray();
		this.supportRegion = new float[maskCount];
		this.medianIndex = maskCount/2;
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	protected float doPixel(PixelSlice plane, int u, int v) {
		int k = 0;
		for (int i = 0; i < maskArray.length; i++) {
			int ui = u + i - xc;
			for (int j = 0; j < maskArray[0].length; j++) {
				if (maskArray[i][j] != 0) {
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
