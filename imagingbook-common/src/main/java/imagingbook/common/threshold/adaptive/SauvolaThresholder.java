/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.threshold.adaptive;

import ij.ImagePlus;
import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.util.ParameterBundle;

/**
 * <p>
 * This is an implementation of the adaptive thresholder proposed in [1]. See also Sec. 9.2 (Eq. 9.74) of [2].
 * </p>
 * <p>
 * [1] Adaptive thresholder as proposed in J. Sauvola and M. Pietikäinen, "Adaptive document image binarization",
 * Pattern Recognition 33(2), 1135-1143 (2000). <br> [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em> 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/08/01
 */
public class SauvolaThresholder implements AdaptiveThresholder {
	
	/**
	 * Parameters for class {@link SauvolaThresholder}.
	 */
	public static class Parameters implements ParameterBundle<SauvolaThresholder> {
		@DialogUtils.DialogLabel("Radius")
		public int radius = 15;
		@DialogUtils.DialogLabel("kappa")
		public double kappa = 0.5;
		@DialogUtils.DialogLabel("sigma_max")
		public double sigmaMax = 128;
		@DialogUtils.DialogLabel("Background mode")
		public BackgroundMode bgMode = BackgroundMode.DARK;
	}

	private final Parameters params;
	
	public SauvolaThresholder() {
		this(new Parameters());
	}
	
	public SauvolaThresholder(Parameters params) {
		this.params = params;
	}
	
	@Override
	public FloatProcessor getThreshold(ByteProcessor I) {
		FloatProcessor Imean = I.convertToFloatProcessor();
		FloatProcessor Isigma = (FloatProcessor) Imean.duplicate();
		
		RankFilters rf = new RankFilters();
		rf.rank(Imean, params.radius, RankFilters.MEAN);
		// new ImagePlus("Imean", Imean). show();
		
		rf.rank(Isigma, params.radius, RankFilters.VARIANCE);
		Isigma.sqrt();
		// new ImagePlus("Isigma", Isigma). show();
		
		final int W = I.getWidth();
		final int H = I.getHeight();
		final float kappa = (float) params.kappa;
		final float sigmaMax = (float) params.sigmaMax;
		final boolean darkBg = (params.bgMode == BackgroundMode.DARK);

		FloatProcessor DIFF = new FloatProcessor(W, H);
		FloatProcessor Q = new FloatProcessor(W, H);
		for (int v = 0; v < H; v++) {
			for (int u = 0; u < W; u++) {
				final float sigmaR = Isigma.getf(u, v);
				final float meanR = Imean.getf(u, v);
				final float diff = kappa * (sigmaR / sigmaMax - 1);
				DIFF.setf(u, v, diff);
				float q = (darkBg) ? meanR * (1 - diff) : meanR * (1 + diff);
				// if (q < 0) q = 0;		// necessary?
				// if (q > 255) q = 255;
				Q.setf(u, v, q);
			}
		}

		// new ImagePlus("DIFF", DIFF). show();
		return Q;
	}
	
}
