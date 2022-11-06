/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.quantize;

import static imagingbook.common.math.Arithmetic.sqr;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.statistics.ColorHistogram;

/**
 * This class implements color quantization using k-means clustering of image
 * pixels in RGB color space. It provides two modes for selecting initial color
 * clusters: (a) random sampling of the input colors, (b) using the K most
 * frequent colors.
 * During clustering all input pixels are used, i.e., no stochastic sub-sampling
 * is applied (which could make the process a lot more efficient).
 * 
 * @author WB
 * @version 2022/11/06
 */
public class KMeansClusteringQuantizer implements ColorQuantizer {
	
	/**
	 * Seed for random number generation (set to a nonzero value to
	 * obtain repeatable results for debugging and testing).
	 */
	public static long RandomSeed = 0;	
	private final Random random = (RandomSeed == 0) ? new Random() : new Random(RandomSeed);
	
	public static int DefaultIterations = 500;
	
	private final ColorCluster[] clusters;
	private final double totalError;
	private final float[][] colormap;
	
	
	/** Method for choosing initial color clusters. */
	public enum InitialClusterMethod {
		/** Use K different random colors to initialize clusters. */
		Random,
		/** Use the K most frequent image colors to initialize clusters. */
		MostFrequent
	};
	
	// --------------------------------------------------------------
	
	/**
	 * Constructor, creates a new {@link KMeansClusteringQuantizer} with up to K colors,
	 * using default parameters.
	 * 
	 * @param pixels an image as a aRGB-encoded int array
	 * @param K  the desired number of colors (1 or more)
	 */
	public KMeansClusteringQuantizer(int[] pixels, int K) {
		this(pixels, K, InitialClusterMethod.Random, DefaultIterations);
	}

	/**
	 * Constructor, creates a new {@link KMeansClusteringQuantizer} with up to K colors,
	 * but never more than the number of colors found in the supplied pixel data.
	 * 
	 * @param pixels an image as a aRGB-encoded int array
	 * @param K  the desired number of colors (1 or more)
	 * @param initMethod the method to initialize color clusters ({@link InitialClusterMethod})
	 * @param maxIterations the maximum number of clustering iterations 
	 */
	public KMeansClusteringQuantizer(int[] pixels, int K, InitialClusterMethod initMethod, int maxIterations) {
		clusters = initClusters(pixels, K, initMethod);
		totalError = doCluster(pixels, maxIterations);
		colormap = makeColorMap();
	}
	
	// --------------------------------------------------------------

	private ColorCluster[] initClusters(int[] pixels, int K, InitialClusterMethod method) {
		int[] samples = null;
		switch (method) {
		case Random:
			samples = getRandomColors(pixels, K);
		case MostFrequent:
			samples = getMostFrequentColors(pixels, K);
		}
		int k = Math.min(samples.length, K);
		ColorCluster[] clstrs = new ColorCluster[k];	// create an array of k clusters
		for (int i = 0; i < k; i++) {
			clstrs[i] = new ColorCluster(samples[i]);	// initialize cluster center
		}
		return clstrs; 
	}

	/**
	 * Returns (maximally) k colors randomly selected from the given pixel data.
	 */
	private int[] getRandomColors(int[] pixels, int k) {
		ColorHistogram colorHist = new ColorHistogram(pixels);
		int[] colors = colorHist.getColors();
		if (colors.length <= k) {
			return colors;
		}
		else {
			shuffle(colors, this.random);	// randomly permute colors
			return Arrays.copyOf(colors, k);
		}
	}
	
	/**
	 * Perform random permutation on the specified array.
	 * https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
	 */
	private void shuffle(int[] arr, Random random) {
		for (int i = arr.length - 1; i > 0; i--) {
			int idx = random.nextInt(i + 1);
			int tmp = arr[idx];
			arr[idx] = arr[i];
			arr[i] = tmp;
		}
	}
	
	/**
	 * Returns the (maximally) k most frequent color values in the given pixel data.
	 * If fewer than k colors are available, these are returned, i.e., the resulting
	 * array may have less than k elements.
	 */
	private int[] getMostFrequentColors(int[] pixels, int k) {
		ColorHistogram colorHist = new ColorHistogram(pixels, true);	// sorts color bins by frequency
		int[] colors = colorHist.getColors();
		if (colors.length <= k) {
			return colors;
		}
		else {
			return Arrays.copyOf(colors, k);
		}
	}
	
	private double doCluster(int[] pixels, int maxIterations) {
		int changed = Integer.MAX_VALUE;
		double distSum = Double.POSITIVE_INFINITY;
		int j = 0;
		while (changed > 0 && j < maxIterations) {
			distSum = assignSamples(pixels);
			changed = updateClusters();
			j++;
		}
		return distSum;
	}

	
	private double assignSamples(int[] pixels) {
		double distSum = 0;
		for (int p : pixels) {
			double dist = addToClosestCluster(p);
			distSum = distSum + dist;
		}
		return distSum;
	}
	
	private int updateClusters() {
		int changed = 0;
		for (ColorCluster c : clusters) {
			changed = changed + c.update();
		}
		return changed;
	}
	
	private double addToClosestCluster(int p) {
		double minDist = Double.POSITIVE_INFINITY;
		ColorCluster closest = null;
		for (ColorCluster c : clusters) {
			double d = c.getSquaredDistance(p);
			if (d < minDist) {
				minDist = d;
				closest = c;
			}
		}
		closest.addPixel(p);
		return minDist;
	}

	private float[][] makeColorMap() {
		List<float[]> colList = new LinkedList<>();
		for (ColorCluster c : clusters) {
			if (!c.isEmpty()) {
				colList.add(c.getCenterColor());
			}
		}		
		return colList.toArray(new float[0][]);
	}
	
	// ------- methods required by abstract super class -----------------------
	
	@Override
	public float[][] getColorMap() {
		return colormap;
	}
	
	
	// ------------------------------------------------------------------------
	/**
	 * Lists the color clusters to System.out (for debugging only).
	 */
	public void listClusters() {
		for (ColorCluster c : clusters) {
			System.out.println(c.toString());
		}
	}
	
	/**
	 * Returns the final clustering error, calculated as the sum of
	 * the squared distances of the color samples to the associated cluster
	 * centers. This calculation is performed during the final iteration.
	 * 
	 * @return the final clustering error
	 */
	public double getTotalError() {
		return totalError;
	}

	// ------------------------------------------------------------------------
	
	/**
	 * This inner class represents a color cluster.	*/
	private static class ColorCluster {
		private int sR, sG, sB;				// RGB sums of contained pixels
		private int pcount;					// pixel counter, used during pixel assignment
		private int population = 0;			// number of pixels contained in this cluster
		private double cR, cG, cB;			// center of this cluster

		private ColorCluster(int p) {
			int[] rgb = RgbUtils.intToRgb(p);
			cR = rgb[0];
			cG = rgb[1];
			cB = rgb[2];
			reset();
		}

		private float[] getCenterColor() {
			return new float[] {(float)cR, (float)cG, (float)cB};
		}

		private boolean isEmpty() {
			return (population == 0);
		}

		private void reset() {	// reset sums, used at the start of the pixel assignment.
			sR = 0;
			sG = 0;
			sB = 0;
			pcount = 0;
		}
		
		private void addPixel(int p) {
			int[] rgb = RgbUtils.intToRgb(p);
			sR += rgb[0];
			sG += rgb[1];
			sB += rgb[2];
			pcount = pcount + 1;
		}
		
		/**
		 * This method is invoked after all samples have been assigned to clusters. It
		 * updates the cluster's center and returns by how much its population changed from
		 * the previous clustering (absolute count).
		 * @return the change in cluster population from the previous clustering
		 */
		private int update() {
			if (pcount > 0) {
				double scale = 1.0 / pcount;
				cR = sR * scale;
				cG = sG * scale;
				cB = sB * scale;
			}
			int changeCount = Math.abs(pcount - population);	// change in cluster population
			population = pcount;
			reset();
			return changeCount;	
		}
		
		/**
		 * Calculates and returns the squared Euclidean distance between the color p
		 * and this cluster's center in RGB space.
		 * @param p Color sample
		 * @return squared distance to the cluster center
		 */
		private double getSquaredDistance(int p) {
			int[] rgb = RgbUtils.intToRgb(p);
			return sqr(rgb[0] - cR) + sqr(rgb[1] - cG) + sqr(rgb[2] - cB);
		}
		
		@Override
		public String toString() {
			return String.format(Locale.US, this.getClass().getSimpleName() +
					": ctr=(%.1f,%.1f,%.1f), pop=%d", cR, cG, cB, population);
		}
	}
	
	// ----------------------------------------------------------------------
	
	public static void main(String[] args) {
		String path = "D:/svn-book/Book/img/ch-color-images/alps-01s.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/desaturation-hsv/balls.jpg";
//		String path = "C:/_SVN/svn-book/Book/img/ch-color-images/desaturation-hsv/balls.jpg";
		
//		String path = "D:/svn-book/Book/img/ch-color-images/single-color.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/two-colors.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/random-colors.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/ramp-fire.png";
		
		int K = 16; 
		System.out.println("image = " + path);
		System.out.println("K = " + K);

		ImagePlus im = IJ.openImage(path);
		if (im == null) {
			System.out.println("could not open: " + path);
			return;
		}
		
		ImageProcessor ip = im.getProcessor();
		ColorProcessor cp = ip.convertToColorProcessor();
		
		ColorQuantizer quantizer = new KMeansClusteringQuantizer((int[])cp.getPixels(), K, InitialClusterMethod.Random, 500);
		quantizer.listColorMap();
		
		System.out.println("quantizing image");
		ByteProcessor qi = quantizer.quantize(cp);
		System.out.println("showing image");
		(new ImagePlus("quantizez", qi)).show();
		
	}
	
} 

