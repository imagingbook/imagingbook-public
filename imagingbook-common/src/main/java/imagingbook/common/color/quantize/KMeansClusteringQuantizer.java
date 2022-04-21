/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.quantize;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

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
	private final Cluster[] clusters;
	private final double totalError;
	
	public enum SamplingMethod {
		Random, Most_Frequent
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

	private Cluster[] makeClusters(int[] pixels) {
		int Kmax = Math.min(pixels.length, params.maxColors);
		int[] samples = getColorSamples(pixels, Kmax);
		int k = Math.min(samples.length, Kmax);
		Cluster[] cls = new Cluster[k];	// create an array of K clusters
		for (int i = 0; i < k; i++) {
			cls[i] = new Cluster(samples[i]); // initialize cluster center
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

	private int[] getRandomColors(int[] pixels, int k) {
		Random rng = new Random();
		Set<Integer> pixelSet = new LinkedHashSet<Integer>();
		while (pixelSet.size() < k) {
			Integer next = rng.nextInt(pixels.length);
			int p = pixels[next];
			// adding to a set automatically does a containment check
			pixelSet.add(p);
		}
		int[] s = new int[k];
		int i = 0;
		for (Integer p : pixelSet) {
			s[i] = p;
			i++;
		}
		return s;
	}
	
	private int[] getMostFrequentColors(int[] pixels, int k) {
		ColorHistogram colorHist = new ColorHistogram(pixels, true);
		k = Math.min(k, colorHist.getNumberOfColors());
		int[] s = new int[k];
		for (int i = 0; i < k; i++) {
			s[i] = colorHist.getColor(i);
		}
		return s;
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
		for (Cluster c : clusters) {
			changed = changed + c.upDate();
		}
		return changed;
	}
	
	private double addToClosestCluster(int p) {
		double minDist = Double.POSITIVE_INFINITY;
		Cluster closest = null;
		for (Cluster c : clusters) {
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
		for (Cluster c : clusters) {
			if (!c.isEmpty()) {
				colList.add(c.getCenterColor());
			}
		}		
		return colList.toArray(new float[0][]);
	}
	
	/**
	 * Lists the color clusters to System.out (intended for debugging only).
	 */
	public void listClusters() {
		for (Cluster c : clusters) {
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

	// ------- methods required by abstract super class -----------------------
	
	@Override
	public float[][] getColorMap() {
		return colormap;
	}
	
	// ------------------------------------------------------------------------
	
	private class Cluster {
		int sRed, sGrn, sBlu;		// RGB sum of contained pixels
		int pCounter;				// pixel counter, used during pixel assignment
		int population = 0;			// number of contained pixels
		double cRed, cGrn, cBlu;	// center of this cluster

		Cluster(int p) {
			int[] rgb = RgbUtils.intToRgb(p);
			cRed = rgb[0];
			cGrn = rgb[1];
			cBlu = rgb[2];
			reset();
		}

		public float[] getCenterColor() {
			return new float[] {(float)cRed, (float)cGrn, (float)cBlu};
		}

		public boolean isEmpty() {
			return (population == 0);
		}

		void reset() {	// Used at the start of the pixel assignment.
			sRed = 0;
			sGrn = 0;
			sBlu = 0;
			pCounter = 0;
		}
		
		void addPixel(int p) {
			int[] rgb = RgbUtils.intToRgb(p);
			sRed += rgb[0];
			sGrn += rgb[1];
			sBlu += rgb[2];
			pCounter = pCounter + 1;
		}
		
		/**
		 * This method is invoked after all samples have been assigned.
		 * It updates the cluster's center and returns true if its
		 * population changed from the previous clustering.
		 * @return true if the population of this cluster has changed.
		 */
		int upDate() {
			if (pCounter > 0) {
				double scale = 1.0 / pCounter;
				cRed = sRed * scale;
				cGrn = sGrn * scale;
				cBlu = sBlu * scale;
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
		double getDistance(int p) {
			int[] rgb = RgbUtils.intToRgb(p);
			final double dR = rgb[0] - cRed;
			final double dG = rgb[1] - cGrn;
			final double dB = rgb[2] - cBlu;
			return dR * dR + dG * dG + dB * dB;
		}
		
		@Override
		public String toString() {
			return String.format(Locale.US, Cluster.class.getSimpleName() +
					": center=(%.1f,%.1f,%.1f), population=%d", cRed, cGrn, cBlu, population);
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

