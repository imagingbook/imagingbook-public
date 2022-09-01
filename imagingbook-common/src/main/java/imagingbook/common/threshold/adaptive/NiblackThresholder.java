/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.threshold.adaptive;

import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.linear.GaussianFilterSeparable;
import imagingbook.common.util.ParameterBundle;

/**
 * <p>
 * This is an implementation of the adaptive thresholder proposed by Niblack in [1].
 * See Sec. 9.2.2 of [2] for a detailed description.
 * It comes in three different version, depending on the type of local support region:
 * </p>
 * <ul>
 * <li>{@link Box}: uses a rectangular (box-shaped) support region;</li> 
 * <li>{@link Disk}: uses a circular (disk-shaped) support region (see [2], Alg. 9.8);</li> 
 * <li>{@link Gauss}: uses a 2D isotropic Gaussian support region (see [2], Alg. 9.9 and Prog. 9.2).</li> 
 * </ul>
 * Note that {@link NiblackThresholder} itself is abstract and thus cannot be instantiated.
 * <p>
 * [1] W. Niblack. “An Introduction to Digital Image Processing”. Prentice-Hall (1986).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/08/01
 */
public abstract class NiblackThresholder implements AdaptiveThresholder {
	
	public enum RegionType { Box, Disk, Gauss }
	
	/**
	 * Parameters for class {@link NiblackThresholder}.
	 */
	public static class Parameters implements ParameterBundle {
		
		@DialogLabel("Radius")
		public int radius = 15;
		
		@DialogLabel("kappa")
		public double kappa = 0.30;
		
		@DialogLabel("d_min")
		public int dMin = 5;
		
		@DialogLabel("Background mode")
		public BackgroundMode bgMode = BackgroundMode.DARK;
	}
	
	private final Parameters params;
	FloatProcessor Imean;
	FloatProcessor Isigma;

	private NiblackThresholder() {
		this(new Parameters());
	}

	private NiblackThresholder(Parameters params) {
		super();
		this.params = params;
	}
	
	// method to be implemented by real sub-classes:
	abstract void makeMeanAndVariance(ByteProcessor I, int radius);
	
	@Override
	public ByteProcessor getThreshold(ByteProcessor I) {
		final int M = I.getWidth();
		final int N = I.getHeight();
		makeMeanAndVariance(I, params.radius);
		ByteProcessor Q = new ByteProcessor(M, N);
		final double kappa = params.kappa;
		final int dMin = params.dMin;
		final boolean darkBg = (params.bgMode == BackgroundMode.DARK);
		
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				double sigma = Isigma.getf(u, v);
				double mu = Imean.getf(u, v);
				double diff = kappa * sigma + dMin;
				int q = (int) Math.rint((darkBg) ? mu + diff : mu - diff);
				if (q < 0)	 q = 0;
				if (q > 255) q = 255;
				Q.set(u, v, q);
			}
		}
		return Q;
	}
	
	// -----------------------------------------------------------------------
	
	/**
	 * Static convenience method for creating a {@link NiblackThresholder} with
	 * a specific support region type.
	 * Note that {@link NiblackThresholder} itself is abstract and cannot be instantiated
	 * (see concrete sub-types {@link Box}, {@link Disk}, {@link Gauss}).
	 * 
	 * @param regType support region type
	 * @param params other parameters
	 * @return an instance of {@link NiblackThresholder}
	 */
	public static NiblackThresholder create(RegionType regType, Parameters params) {
		switch (regType) {
		case Box : 		return new NiblackThresholder.Box(params);
		case Disk : 	return new NiblackThresholder.Disk(params);
		case Gauss : return new NiblackThresholder.Gauss(params);
		default : 		return null;
		}
	}
	
	// -----------------------------------------------------------------------
	
	/**
	 * Implementation of Niblack's adaptive thresholder using a
	 * rectangular (box-shaped) support region 
	 * (concrete implementation of abstract class {@link NiblackThresholder}).
	 */
	public static class Box extends NiblackThresholder {

		/**
		 * Constructor using default parameters.
		 */
		public Box() {
			super();
		}
		
		/**
		 * Constructor with specific parameters.
		 * @param params parameters
		 */
		public Box(Parameters params) {
			super(params);
		}

		@Override
		void makeMeanAndVariance(ByteProcessor I, int radius) {
			int M = I.getWidth();
			int N = I.getHeight();
			Imean =  new FloatProcessor(M, N);
			Isigma =  new FloatProcessor(M, N);
			final int n = (radius + 1 + radius) * (radius + 1 + radius);

			for (int v = 0; v < N; v++) {
				for (int u = 0; u < M; u++) {
					long A = 0;	// sum of image values in support region
					long B = 0;	// sum of squared image values in support region
					for (int j = -radius; j <= radius; j++) {
						for (int i = -radius; i <= radius; i++) {
							int p = getPaddedPixel(I, u + i, v + j); // this is slow!
							A = A + p;
							B = B + p * p;
						}
					}
					Imean.setf(u, v, (float) A / n);
					Isigma.setf(u, v, (float) Math.sqrt((B - (double) (A * A) / n) / n));
				}
			}
		}
		
		// TODO: change to use an ImageAccessor!
		private int getPaddedPixel(ByteProcessor bp, int u, int v) {
			final int w = bp.getWidth();
			final int h = bp.getHeight();
			if (u < 0)
				u = 0;
			else if (u >= w)
				u = w - 1;
			if (v < 0)
				v = 0;
			else if (v >= h)
				v = h - 1;
			return bp.get(u, v);
		}
		
	}
	
	// -----------------------------------------------------------------------
	
	/**
	 * Implementation of Niblack's adaptive thresholder using a
	 * circular (disk-shaped) support region 
	 * (concrete implementation of abstract class {@link NiblackThresholder}).
	 */
	public static class Disk extends NiblackThresholder {
		
		/**
		 * Constructor using default parameters.
		 */
		public Disk() {
			super();
		}
		
		/**
		 * Constructor with specific parameters.
		 * @param params parameters
		 */
		public Disk(Parameters params) {
			super(params);
		}

		@Override
		void makeMeanAndVariance(ByteProcessor I, int radius) {
			FloatProcessor mean = (FloatProcessor) I.convertToFloat();
			FloatProcessor var =  (FloatProcessor) mean.duplicate();
			
			RankFilters rf = new RankFilters();
			rf.rank(mean, radius, RankFilters.MEAN);
			Imean = mean;
			
			rf.rank(var, radius, RankFilters.VARIANCE);
			var.sqrt();
			Isigma = var;
		}
	}
	
	// -----------------------------------------------------------------------
	
	/**
	 * Implementation of Niblack's adaptive thresholder using a
	 * 2D Gaussian support region 
	 * (concrete implementation of abstract class {@link NiblackThresholder}).
	 */
	public static class Gauss extends NiblackThresholder {
		
		/**
		 * Constructor using default parameters.
		 */
		public Gauss() {
			super();
		}
		
		/**
		 * Constructor with specific parameters.
		 * @param params parameters
		 */
		public Gauss(Parameters params) {
			super(params);
		}
		
		@Override
		void makeMeanAndVariance(ByteProcessor I, int r) {
			// //uses ImageJ's GaussianBlur
			// local variance over square of size (size + 1 + size)^2
			int M = I.getWidth();
			int N = I.getHeight();
			
			Imean = new FloatProcessor(M,N);
			Isigma = new FloatProcessor(M,N);

			FloatProcessor A = I.convertToFloatProcessor();
			FloatProcessor B = I.convertToFloatProcessor();
			B.sqr();
			
			double sigma = r * 0.6;	// sigma of Gaussian filter should be approx. 0.6 of the disk's radius
			
//			GaussianBlur gb = new GaussianBlur();
//			gb.blurFloat(A, sigma, sigma, 0.002);
//			gb.blurFloat(B, sigma, sigma, 0.002);
			
			GenericFilter gaussian = new GaussianFilterSeparable(sigma);
			gaussian.applyTo(A);
			gaussian.applyTo(B);

			for (int v = 0; v < N; v++) {
				for (int u = 0; u < M; u++) {
					float a = A.getf(u, v);
					float b = B.getf(u, v);
					float sigmaG = (float) Math.sqrt(b - a * a);
					Imean.setf(u, v, a);
					Isigma.setf(u, v, sigmaG);
				}
			}
		}
	}
	
}
