/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.statistics;

import java.util.Arrays;

/**
 * This class calculates a color histogram of a set of colors (i.e., a color
 * image). Only the unique colors are accounted for. Colors are supplied as
 * ARGB-encoded integers (A = alpha values being ignored). Colors are internally
 * sorted by their frequency (in descending order).
 * Used mainly for color quantization.
 * 
 * @author WB
 * @version 2022/11/05
 */
public class ColorHistogram {
	
	private final ColorBin[] colorBins;
	
	/**
	 * Creates a color histogram instance from the supplied sequence
	 * of color pixel values (assumed to be ARGB-encoded integers).
	 * The original pixel values are not modified.
	 * 
	 * @param pixelsOrig
	 */
	public ColorHistogram(int[] pixelsOrig) {
		this(pixelsOrig, false);
	}
	
	/**
	 * Creates a color histogram instance from the supplied sequence
	 * of color pixel values (assumed to be ARGB-encoded integers).
	 * The original pixel values are not modified.
	 * 
	 * @param pixelsOrig original pixel values (not modified)
	 * @param sortByFrequency set true to sort the final colors by descending frequency
	 */
	public ColorHistogram(int[] pixelsOrig, boolean sortByFrequency) {
		int[] pixels = new int[pixelsOrig.length];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0xFFFFFF & pixelsOrig[i];	// remove alpha components
		}
		
		Arrays.sort(pixels);	// this is why we need a copy of the input array
		
		// count unique colors:
		int k = -1; // current color index
		int curColor = -1;
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] != curColor) {
				k++;
				curColor = pixels[i];
			}
		}
		int nUnique = k + 1;	// number of unique colors
		
		colorBins = new ColorBin[nUnique];
		
		// tabulate and find frequency of unique colors:
		k = -1;	// current color index
		curColor = -1;
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] != curColor) {	// found a new color
				k++;
				curColor = pixels[i];
				colorBins[k] = new ColorBin(curColor);
			}
			else {							// still with the previous color
				colorBins[k].add(1);
			}
		}
		
		if (sortByFrequency)
			Arrays.sort(colorBins);	// sort unique colors by descending frequency
	}
	
	/**
	 * Returns the number of unique colors.
	 * @return The number of unique colors.
	 */
	public int getNumberOfColors() {
		return colorBins.length;
	}
	
	/**
	 * Returns an array with all (distinct) colors of this histogram as sRGB
	 * int-encoded values. Values are in no particular order but in the same order
	 * as the array returned by {@link #getFrequencies()}.
	 * 
	 * @return an array of all distinct colors
	 */
	public int[] getColors() {
		int[] colors = new int[colorBins.length];
		for (int i = 0; i < colorBins.length; i++) {
			colors[i] = colorBins[i].rgb;
		}
		return colors;
	}
	
	/**
	 * Returns an array with the frequencies of all (distinct) colors of this
	 * histogram. Values are in no particular order but in the same order as the
	 * array returned by {@link #getColors()}.
	 * 
	 * @return an array of all distinct color frequencies
	 */
	public int[] getFrequencies() {
		int[] frequencies = new int[colorBins.length];
		for (int i = 0; i < colorBins.length; i++) {
			frequencies[i] = colorBins[i].count;
		}
		return frequencies;
	}
	
	/**
	 * Returns the unique color with the given index.
	 * Colors are sorted by (decreasing) frequency.
	 * 
	 * @param index The color index.
	 * @return	The color, encoded as an ARGB integer (A is zero).
	 */
	public int getColor(int index) {
		return colorBins[index].rgb;
	}
	
	/**
	 * Returns the frequency of the unique color with the given index.
	 * Colors are sorted by (decreasing) frequency.
	 * @param index The color index.
	 * @return	The frequency of the color.
	 */
	public int getFrequency(int index) {
		return colorBins[index].count;
	}
	
	/**
	 * Lists the unique colors to System.out (intended for
	 * debugging only).
	 */
	public void listUniqueColors() {
		for (ColorBin cn : colorBins) {
			System.out.println(cn.toString());
		}
	}
	
	// --------------------------------------------------------------------------------
	
	/**
	 * Represents a set of pixels with the same color.
	 */
	private static class ColorBin implements Comparable<ColorBin> {
		private final int rgb;	// the pixel color (aRGB-encoded int)
		private int count;		// the size of the set

		private ColorBin(int rgb) {
			this.rgb = rgb;
			this.count = 1;
		}

		/**
		 * Add n pixels to this bin.
		 * @param n the number of pixels to add
		 */
		private void add(int n) {
			count = count + n;	
		}
		
		@Override
		public String toString() {
			return String.format(ColorBin.class.getSimpleName() + " rgb=%d count=%d", rgb, count);
		}

		@Override
		public int compareTo(ColorBin other) {	// to sort by count (high counts first)
			return Integer.compare(other.count, this.count);
		}

	}

}
