/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.threshold.adaptive;

import static imagingbook.common.threshold.Thresholder.BackgroundMode;

import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.util.ParameterBundle;


/**
 * <p>
 * This is an implementation of the adaptive thresholder proposed by Bernsen in
 * [1]. It uses a circular support region implemented with ImageJ's built-in
 * rank-filter methods. See Sec. 9.2.1 of [2] for a detailed description.
 * </p>
 * <p>
 * [1] J. Bernsen. Dynamic thresholding of grey-level images. In "Proceedings of
 * the International Conference on Pattern Recognition (ICPR)", pp. 1251–1255,
 * Paris (October 1986). IEEE Computer Society. <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
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
		public int cmin = 15;
		
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
		this.params = new Parameters();
	}
	
	/**
	 * Constructor using specified parameters.
	 * @param params an instance of {@link Parameters}
	 */
	public BernsenThresholder(Parameters params) {
		this.params = params;
	}

	@Override
	public ByteProcessor getThreshold(ByteProcessor I) {
		final int M = I.getWidth();
		final int N = I.getHeight();
		ByteProcessor Imin = (ByteProcessor) I.duplicate();
		ByteProcessor Imax = (ByteProcessor) I.duplicate();

		RankFilters rf = new RankFilters();
		rf.rank(Imin, params.radius, RankFilters.MIN);
		rf.rank(Imax, params.radius, RankFilters.MAX);

		int q = (params.bgMode == BackgroundMode.DARK) ? 256 : 0;
		ByteProcessor Q = new ByteProcessor(M, N);

		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				int gMin = Imin.get(u, v);
				int gMax = Imax.get(u, v);
				int c = gMax - gMin;
				if (c >= params.cmin)
					Q.set(u, v, (gMin + gMax) / 2);
				else
					Q.set(u, v, q);
			}
		}
		return Q;
	}
	
}
