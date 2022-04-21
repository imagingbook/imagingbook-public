/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.threshold.adaptive;

import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import imagingbook.common.threshold.BackgroundMode;
import imagingbook.common.util.ParameterBundle;

/**
 * This implementation of Bernsen's thresholder uses a circular support region,
 * implemented with ImageJ's built-in rank-filter methods.
 * 
 * @author WB
 * @version 2022/04/02
 */
public class BernsenThresholder extends AdaptiveThresholder {
	
	public static class Parameters implements ParameterBundle {
		@DialogLabel("Radius")
		public int radius = 15;
		
		@DialogLabel("c_min")
		public int cmin = 15;
		
		@DialogLabel("Background mode")
		public BackgroundMode bgMode = BackgroundMode.DARK;
	}
	
	private final Parameters params;
	
	public BernsenThresholder() {
		this.params = new Parameters();
	}
	
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
