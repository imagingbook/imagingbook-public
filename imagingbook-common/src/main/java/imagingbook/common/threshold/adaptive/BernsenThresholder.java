/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.threshold.adaptive;

import ij.IJ;
import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.util.ParameterBundle;


/**
 * <p>
 * This is an implementation of the adaptive thresholder proposed by Bernsen in [1]. It uses a circular support region
 * implemented with ImageJ's built-in rank-filter methods. See Sec. 9.2.1 of [2] for a detailed description.
 * </p>
 * <p>
 * [1] J. Bernsen. Dynamic thresholding of grey-level images. In "Proceedings of the International Conference on Pattern
 * Recognition (ICPR)", pp. 1251–1255, Paris (October 1986). IEEE Computer Society. <br> [2] W. Burger, M.J. Burge,
 * <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/04/02
 */
public class BernsenThresholder implements AdaptiveThresholder {
	
	/**
	 * Parameters for class {@link BernsenThresholder}.
	 */
	public static class Parameters implements ParameterBundle<BernsenThresholder> {
		/** Radius of circular support region */ 
		@DialogLabel("Radius")
		public int radius = 15;
		/** Minimum contrast */ 
		@DialogLabel("Min. contrast")
		public double cmin = 15;
		/** Background type (see {@link BackgroundMode}) */
		@DialogLabel("Background mode")
		public BackgroundMode bgMode = BackgroundMode.DARK;
	}
	
	// --------------------------------------------
	
	private final Parameters params;
	
	/**
	 * Constructor using default parameters.
	 */
	public BernsenThresholder() {
		this(new Parameters());
	}
	
	/**
	 * Constructor using specified parameters.
	 * @param params an instance of {@link Parameters}
	 */
	public BernsenThresholder(Parameters params) {
		this.params = params;
	}

	@Override
	public FloatProcessor getThreshold(ByteProcessor I) {
		final int W = I.getWidth();
		final int H = I.getHeight();
		ByteProcessor Imin = (ByteProcessor) I.duplicate();
		ByteProcessor Imax = (ByteProcessor) I.duplicate();

		RankFilters rf = new RankFilters();
		rf.rank(Imin, params.radius, RankFilters.MIN);	// minimum filter
		rf.rank(Imax, params.radius, RankFilters.MAX);	// maximum filter

		// new ImagePlus("Imin", Imin).show();
		// new ImagePlus("Imax", Imax).show();

		// ByteProcessor Contrast = (ByteProcessor) Imax.duplicate();
		// for (int v = 0; v < H; v++) {
		// 	for (int u = 0; u < W; u++) {
		// 		Contrast.set(u, v, Contrast.get(u, v) - Imin.get(u, v));
		// 	}
		// }
		// new ImagePlus("Contrast", Contrast).show();

		// threshold surface
		FloatProcessor Q = new FloatProcessor(W, H);
		// default threshold for regions with insufficient contrast
		float qq = (params.bgMode == BackgroundMode.DARK) ? 256 : -1;


		for (int v = 0; v < H; v++) {
			for (int u = 0; u < W; u++) {
				int gMin = Imin.get(u, v);
				int gMax = Imax.get(u, v);
				int c = gMax - gMin;			// local contrast
				if (c >= params.cmin)
					Q.setf(u, v, 0.5f * (gMin + gMax));
				else
					Q.setf(u, v, qq);
			}
		}
		return Q;
	}

}
