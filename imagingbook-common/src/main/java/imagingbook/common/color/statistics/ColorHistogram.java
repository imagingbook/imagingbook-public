/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.statistics;

import java.util.Arrays;

/**
 * This class calculates a color histogram of a set of colors (i.e., a color image).
 * Only the unique colors are accounted for. Colors are supplied as ARGB-encoded
 * integers (A = alpha values being ignored).
 * Colors are internally sorted by their frequency (in descending order).
 *  
 * @author WB
 * @version 2017/01/04
 */
public class ColorHistogram {
	
	private final ColorNode[] colornodes;
	
	public ColorHistogram(int[] pixelsOrig) {
		this(pixelsOrig, false);
	}
	
	/**
	 * Creates a color histogram instance from the supplied sequence
	 * of color pixel values (assumed to be ARGB-encoded integers).
	 * The original pixel values are not modified.
	 * 
	 * @param pixelsOrig Original pixel values (not modified).
	 * @param sortByFrequency Pass true to sort the final colors by descending frequency.
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
		
		colornodes = new ColorNode[nUnique];
		
		// tabulate and find frequency of unique colors:
		k = -1;	// current color index
		curColor = -1;
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] != curColor) {	// found a new color
				k++;
				curColor = pixels[i];
				colornodes[k] = new ColorNode(curColor);
			}
			else {							// still with the previous color
				colornodes[k].add(1);
			}
		}
		
		if (sortByFrequency)
			Arrays.sort(colornodes);	// sort unique colors by descending frequency
	}
	
	/**
	 * Returns the number of unique colors.
	 * @return The number of unique colors.
	 */
	public int getNumberOfColors() {
		return colornodes.length;
	}
	
	/**
	 * Returns the unique color with the given index.
	 * Colors are sorted by (decreasing) frequency.
	 * @param index The color index.
	 * @return	The color, encoded as an ARGB integer (A is zero).
	 */
	public int getColor(int index) {
		return colornodes[index].rgb;
	}
	
	/**
	 * Returns the frequency of the unique color with the given index.
	 * Colors are sorted by (decreasing) frequency.
	 * @param index The color index.
	 * @return	The frequency of the color.
	 */
	public int getCount(int index) {
		return colornodes[index].count;
	}
	
	/**
	 * Lists the unique colors to System.out (intended for
	 * debugging only).
	 */
	public void listUniqueColors() {
		for (ColorNode cn : colornodes) {
			System.out.println(cn.toString());
		}
	}
	
	// --------------------------------------------------------------------------------
	
	private class ColorNode implements Comparable<ColorNode> {
		private final int rgb;
		private int count;

		ColorNode(int rgb) {
			this.rgb = rgb;
			this.count = 1;
		}

		void add(int n) {
			count = count + n;	
		}
		
		public String toString() {
			return String.format(ColorNode.class.getSimpleName() + " rgb=%d count=%d", rgb, count);
		}

		@Override
		public int compareTo(ColorNode c2) {	// to sort by count (high counts first)
			if (this.count > c2.count) return -1;
			if (this.count < c2.count) return 1;
			else return 0;
		}
	}
	
}
