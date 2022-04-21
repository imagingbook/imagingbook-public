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
import ij.process.FloatProcessor;
import imagingbook.common.filter.GenericFilter;
import imagingbook.common.filter.linear.GaussianFilterSeparable;
import imagingbook.common.threshold.BackgroundMode;
import imagingbook.common.util.ParameterBundle;

/**
 * This version of Niblack's thresholder uses a circular support region, implemented 
 * with IJ's rank-filter methods.
 * 
 * @author WB
 * @version 2022/04/02
 */
public abstract class NiblackThresholder extends AdaptiveThresholder {
	
	public enum RegionType { Box, Disk, Gaussian }
	
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
	protected FloatProcessor Imean;
	protected FloatProcessor Isigma;

	private NiblackThresholder () {
		super();
		this.params = new Parameters();
	}

	private NiblackThresholder (Parameters params) {
		super();
		this.params = params;
	}
	
	// method to be implemented by real sub-classes:
	protected abstract void makeMeanAndVariance(ByteProcessor I, int radius);
	
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
	
	public static NiblackThresholder create(RegionType regType, Parameters params) {
		switch (regType) {
		case Box : 		return new NiblackThresholder.Box(params);
		case Disk : 	return new NiblackThresholder.Disk(params);
		case Gaussian : return new NiblackThresholder.Gauss(params);
		default : 		return null;
		}
	}
	
	// -----------------------------------------------------------------------
	
	public static class Box extends NiblackThresholder {

		public Box() {
			super();
		}
		
		public Box(Parameters params) {
			super(params);
		}

		@Override
		protected void makeMeanAndVariance(ByteProcessor I, int radius) {
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
		
	}
	
	// -----------------------------------------------------------------------
	
	public static class Disk extends NiblackThresholder {
		
		public Disk() {
			super();
		}
		
		public Disk(Parameters params) {
			super(params);
		}

		@Override
		protected void makeMeanAndVariance(ByteProcessor I, int radius) {
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
	
	public static class Gauss extends NiblackThresholder {
		
		public Gauss() {
			super();
		}
		
		public Gauss(Parameters params) {
			super(params);
		}
		
		@Override
		protected void makeMeanAndVariance(ByteProcessor I, int r) {
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
