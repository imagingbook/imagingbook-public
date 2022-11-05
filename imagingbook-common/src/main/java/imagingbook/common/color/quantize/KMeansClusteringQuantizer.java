/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.quantize;

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
 * This class implements color quantization using k-means clustering
 * of image pixels in RGB color space. Two modes of selecting
 * the colors for the initial clusters are provided:
 * (a) random sampling of the input colors,
 * (b) using the most frequent colors.
 * 
 * @author WB
 * @version 2017/01/04
 */
public class KMeansClusteringQuantizer implements ColorQuantizer {
	
	private final Parameters params;
	private final float[][] colormap;
	private final ColorCluster[] clusters;
	private final double totalError;
	
	public enum SamplingMethod {
		Random, 
		Most_Frequent
	};
	
	public static class Parameters {
		/** Maximum number of quantized colors. */
		public int maxColors = 16;
		/** Maximum number of clustering iterations */
		public int maxIterations = 500;
		/** The method used for selecting the initial color samples. */
		public SamplingMethod samplMethod = SamplingMethod.Random;
		
		void check() {
			if (maxColors < 2 || maxColors > 256 || maxIterations < 1) {
				throw new IllegalArgumentException();
			}
		}
	}
	
	// --------------------------------------------------------------
	
	public KMeansClusteringQuantizer(ColorProcessor cp, Parameters params) {
		this((int[]) cp.getPixels(), params);
	}

	/**
	 * Creates a new quantizer instance from the supplied sequence
	 * of color values (assumed to be ARGB-encoded integers).
	 * 
	 * @param pixels Sequence of input color values.
	 * @param params Parameter object.
	 */
	public KMeansClusteringQuantizer(int[] pixels, Parameters params) {
		params.check();
		this.params = params;
		clusters = makeClusters(pixels);
		totalError = cluster(pixels);
		colormap = makeColorMap();
	}
	
	public KMeansClusteringQuantizer(int[] pixels) {
		this(pixels, new Parameters());
	}
	
	// --------------------------------------------------------------

	private ColorCluster[] makeClusters(int[] pixels) {
		int Kmax = Math.min(pixels.length, params.maxColors);
		int[] samples = getColorSamples(pixels, Kmax);
		int k = Math.min(samples.length, Kmax);
		ColorCluster[] cls = new ColorCluster[k];	// create an array of K clusters
		for (int i = 0; i < k; i++) {
			cls[i] = new ColorCluster(samples[i]); // initialize cluster center
		}
		return cls; 
	}
	
	/**
	 * We randomly pick k distinct colors from the original image
	 * pixels.
	 * @param pixels
	 * @param k
	 * @return
	 */
	private int[] getColorSamples(int[] pixels, int k) {
		switch (params.samplMethod) {
		case Random:
			return getRandomColors(pixels, k);
		case Most_Frequent:
			return getMostFrequentColors(pixels, k);
		default:
			return null;
		}
	}

	/**
	 * Returns (maximally) k colors randomly selected from the given pixel data.
	 * 
	 * @param pixels
	 * @param k
	 * @return
	 */
	private int[] getRandomColors(int[] pixels, int k) {
		ColorHistogram colorHist = new ColorHistogram(pixels);
		int[] colors = colorHist.getColors();
		if (colors.length <= k) {
			return colors;
		}
		else {
			shuffle(colors);	// randomly permute colors
			return Arrays.copyOf(colors, k);
		}
	}
	
	// see https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
	private static void shuffle(int[] array) {
		shuffle(array, 0);
	}
	
	private static void shuffle(int[] array, long seed) {
		int index, temp;
		Random random = (seed == 0) ? new Random() : new Random(seed);
		for (int i = array.length - 1; i > 0; i--) {
			index = random.nextInt(i + 1);
			temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
	}
	
	/**
	 * Returns the (maximally) k most frequent color values in the given pixel data.
	 * If fewer than k colors are available, these are returned, i.e., the resulting
	 * array may have less than k elements.
	 * 
	 * @param pixels the pixel data (aRGB-encoded int values)
	 * @param k the required number of most frequent colors
	 * @return an array of the most frequent color values (aRGB-encoded int values)
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
	
	private double cluster(int[] pixels) {
		int changed = Integer.MAX_VALUE;
		double distSum = Double.POSITIVE_INFINITY;
		int j = 0;
		while (changed > 0 && j < params.maxIterations) {
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
			changed = changed + c.upDate();
		}
		return changed;
	}
	
	private double addToClosestCluster(int p) {
		double minDist = Double.POSITIVE_INFINITY;
		ColorCluster closest = null;
		for (ColorCluster c : clusters) {
			double d = c.getDistance(p);
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
	 * Returns the total error of this clustering, calculated as the sum of
	 * the squared distances of the color samples to the associated cluster
	 * center. This calculation is performed during the final iteration.
	 * 
	 * @return The sum of the squared distances between samples and cluster centers.
	 */
	public double getTotalError() {
		return totalError;
	}

	// ------------------------------------------------------------------------
	
	private class ColorCluster {
		private int sR, sG, sB;				// RGB sums of contained pixels
		private int pCounter;				// pixel counter, used during pixel assignment
		private int population = 0;			// number of contained pixels
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

		private void reset() {	// Used at the start of the pixel assignment.
			sR = 0;
			sG = 0;
			sB = 0;
			pCounter = 0;
		}
		
		private void addPixel(int p) {
			int[] rgb = RgbUtils.intToRgb(p);
			sR += rgb[0];
			sG += rgb[1];
			sB += rgb[2];
			pCounter = pCounter + 1;
		}
		
		/**
		 * This method is invoked after all samples have been assigned.
		 * It updates the cluster's center and returns true if its
		 * population changed from the previous clustering.
		 * 
		 * @return true if the population of this cluster has changed.
		 */
		private int upDate() {
			if (pCounter > 0) {
				double scale = 1.0 / pCounter;
				cR = sR * scale;
				cG = sG * scale;
				cB = sB * scale;
			}
			int changed = Math.abs(pCounter - population);	// change in cluster population
			population = pCounter;
			reset();
			return changed;	
		}
		
		/**
		 * Calculates and returns the squared Euclidean distance between the color p
		 * and this cluster's center in RGB space.
		 * @param p Color sample
		 * @return Squared distance to the cluster center
		 */
		private double getDistance(int p) {
			int[] rgb = RgbUtils.intToRgb(p);
			final double dR = rgb[0] - cR;
			final double dG = rgb[1] - cG;
			final double dB = rgb[2] - cB;
			return dR * dR + dG * dG + dB * dB;
		}
		
		@Override
		public String toString() {
			return String.format(Locale.US, ColorCluster.class.getSimpleName() +
					": center=(%.1f,%.1f,%.1f), population=%d", cR, cG, cB, population);
		}
	}
	
	// ----------------------------------------------------------------------
	
	public static void main(String[] args) {
//		String path = "D:/svn-book/Book/img/ch-color-images/alps-01s.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/desaturation-hsv/balls.jpg";
		String path = "C:/_SVN/svn-book/Book/img/ch-color-images/desaturation-hsv/balls.jpg";
		
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
		
		Parameters params = new Parameters();
		params.maxColors = K;
		ColorQuantizer quantizer = new KMeansClusteringQuantizer(cp, params);
		quantizer.listColorMap();
		
		System.out.println("quantizing image");
		ByteProcessor qi = quantizer.quantize(cp);
		System.out.println("showing image");
		(new ImagePlus("quantizez", qi)).show();
		
	}
	
} 

