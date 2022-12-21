/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.color.quantize;


import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.statistics.ColorHistogram;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.PriorityQueue;

/**
 * <p>
 * This is an implementation of Heckbert's median-cut color quantization algorithm [1]. Unlike in the original
 * algorithm, no initial uniform (scalar) quantization is used to for reducing the number of image colors. Instead, all
 * colors contained in the original image are considered in the quantization process. After the set of representative
 * colors has been found, each image color is mapped to the closest representative in RGB color space using the
 * Euclidean distance. See Sec. 13.4.2 (Algs. 13.1-3) of [2] for details.
 * </p>
 * <p>
 * The quantization process has two steps: first a {@link ColorQuantizer} instance is created from a given image using
 * one of the constructor methods provided. This quantizer can then be used to quantize the original image or any other
 * image using the same set of representative colors (color table).
 * </p>
 * <p>
 * [1] Heckbert P., "Color Image Quantization for Frame Buffer Display", ACM Transactions on Computer Graphics
 * (SIGGRAPH), pp. 297-307 (1982). <br> [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/10/05 revised to use PriorityQueue, made deterministic
 */
public class MedianCutQuantizer implements ColorQuantizer {
	
	/** The array of original (distinct) colors. */
	private final ColorBin[] origColors;
	/** The final color map (array of representative colors). */
	private final float[][] colorMap;
	
	// -------------------------------------------------------------------------------

	/**
	 * Constructor, creates a new {@link MedianCutQuantizer} with up to K colors, but never more than the number of
	 * colors found in the supplied pixel data.
	 *
	 * @param pixels an image as a aRGB-encoded int array
	 * @param K the desired number of colors (1 or more)
	 */
	public MedianCutQuantizer(int[] pixels, int K) {
		if (K < 1) 
			throw new IllegalArgumentException("K must be at least 1");
		this.origColors = getDistinctColors(pixels);
		this.colorMap = (origColors.length > K) ?
				makeColorMap(findReferenceColors(K)) :	// more than K colors
				makeColorMap(origColors);				// exactly K or fewer colors
	}
	
	// -------------------------------------------------------------------------------
	
	@Override
	public float[][] getColorMap() {
		return this.colorMap;
	}
	
	// -------------------------------------------------------------------------------

	/**
	 * Collects and returns the distinct colors found in the specified pixel array as an array of {@link ColorBin}
	 * instances.
	 *
	 * @param pixels an array of aRGB-encoded color values
	 * @return an array of distinct colors
	 */
	private ColorBin[] getDistinctColors(int[] pixels) {
		ColorHistogram ch = new ColorHistogram(pixels, true);
		final int nc = ch.getNumberOfColors();
		ColorBin[] colors = new ColorBin[nc];
		for (int i = 0; i < nc; i++) {
			int rgb = ch.getColor(i);
			int cnt = ch.getFrequency(i);
			colors[i] = new ColorBin(cnt, rgb);
		}
		return colors;
	}

	/**
	 * Performs the actual quantization and returns the calculated reference colors as an array of K
	 * {@link ColorBox} instances. Color boxes are maintained in a sorted list (of type {@link PriorityQueue}) B whose
	 * sorting order is determined by the {@link ColorBox#compareTo(ColorBox)}. Boxes are iteratively split until the
	 * specified number of quantized colors is reached. In each step, the first color box (head element) of the sorted
	 * list is removed and two new boxes (the result of splitting) are added. Thus each step increases the size of list
	 * B by 1.
	 *
	 * @return an array of reference colors
	 */
	private ColorBox[] findReferenceColors(int K) {
		final int n = origColors.length;
		ColorBox cb0 = new ColorBox(0, n - 1, 0);
		AbstractQueue<ColorBox> B = new PriorityQueue<>();	// the (ordered) set of quantized colors
		
		B.add(cb0);	
		int k = 1;						// number of quantized color (color boxes)
		boolean done = false;
		
		while (k < K && !done) {		// B.size() < K
			ColorBox cb = B.remove(); 	// take the first box from the queue // findBoxToSplit(B);
			ColorBox[] boxes = cb.splitBox();
			if (boxes != null) {		// this should never happen
				B.add(boxes[0]);
				B.add(boxes[1]);
				k = k + 1;
			}
			else {
				done = true;
			}
		}
		// B.size() == K
		return B.toArray(new ColorBox[0]);	// sort?
	}
	
	// --------------------------------------------------------
	
	@SuppressWarnings("unused")
	private void listColorBoxes(AbstractQueue<ColorBox> boxes) {
		if (boxes.isEmpty()) {
			System.out.println("boxes empty");
			return;
		}
		System.out.println("*** boxes " + boxes.size());
		ColorBox[] all = boxes.toArray(new ColorBox[0]);
		Arrays.sort(all);
		for (int i = 0; i < all.length; i++) {
			System.out.println("    " + i + ": " + all[i].toString());
		}
	}
	
	// --------------------------------------------------------
	
	private float[][] makeColorMap(ColorBin[] colors) {
		int n = colors.length;
		float[][] map = new float[n][];
		int i = 0;
		for (ColorBin cn : colors) {
			map[i] = new float[] { cn.red, cn.grn, cn.blu };
			i++;
		}
		return map;
	}
	
	private float[][] makeColorMap(ColorBox[] quantColors) {
		int n = quantColors.length;
		float[][] map = new float[n][];
		int i = 0;
		for (ColorBox cb : quantColors) {
			map[i] = cb.getAvgColor();
			i++;
		}
		return map;
	}

	// -------------- class ColorBin -------------------------------------------

	private class ColorBin {
		private final int red, grn, blu;
		private final int cnt;
		
		private ColorBin(int cnt, int rgb) {
			this(cnt, RgbUtils.intToRgb(rgb));
		}
		
		private ColorBin (int cnt, int[] rgb) {
			this.cnt = cnt;
			this.red = rgb[0];
			this.grn = rgb[1];
			this.blu = rgb[2];
		}
		
		@Override
		public String toString() {
			String s = ColorBin.class.getSimpleName();
			s = s + " red=" + red + " green=" + grn + " blue=" + blu + " count=" + cnt;
			return s;
		}
	}
	
	// -------------- non-static inner class ColorBox -----------------------------------

	/**
	 * Represents a 'color box' holding a set of colors (of type {@link ColorBin}), which is implemented as a contiguous
	 * range of elements in array {@link MedianCutQuantizer#origColors}. Instances of {@link ColorBox} reference this
	 * array directly (this is why this class is non-static). Instances of this class are immutable.
	 */
	private class ColorBox implements Comparable<ColorBox> { 
		final int loIdx; 		// lower index into 'imageColors'
		final int hiIdx; 		// upper index into 'imageColors'
		final int level; 		// split level of this color box
		
		final int count; 				// number of pixels represented by this color box
		final float rmin, rmax;			// range of contained colors in red dimension
		final float gmin, gmax;			// range of contained colors in green dimension
		final float bmin, bmax;			// range of contained colors in blue dimension
		final float maxSpan;			// largest side length of this color box 
		final ColorDimension maxDim;	// color dimension of largest length
		
		ColorBox(int loIdx, int hiIdx, int level) {
			this.loIdx = loIdx;
			this.hiIdx = hiIdx;
			this.level = level;
			
			// trim this color box:
			int n = 0;	
			float rMin = MAX_RGB, gMin = MAX_RGB, bMin = MAX_RGB;
			float rMax = 0, gMax = 0, bMax = 0;		
			for (int i = loIdx; i <= hiIdx; i++) {
				ColorBin cn = origColors[i];
				n = n + cn.cnt;
				rMax = Math.max(cn.red, rMax);
				rMin = Math.min(cn.red, rMin);
				gMax = Math.max(cn.grn, gMax);
				gMin = Math.min(cn.grn, gMin);
				bMax = Math.max(cn.blu, bMax);
				bMin = Math.min(cn.blu, bMin);
			}
			this.rmax = rMax; this.rmin = rMin;
			this.gmax = gMax; this.gmin = gMin;
			this.bmax = bMax; this.bmin = bMin;
			this.count = n;
			
			// find the largest dimension of this color box
			float rd = rmax - rmin;
			float gd = gmax - gmin;
			float bd = bmax - bmin;
			float maxS = rd;
			ColorDimension maxD = ColorDimension.RED;
			if (bd >= rd && bd >= gd) {
				maxS = bd;
				maxD = ColorDimension.BLU;
			}
			else if (gd >= rd && gd >= bd) {
				maxS = gd;
				maxD = ColorDimension.GRN;
			}
	
			this.maxSpan = maxS;
			this.maxDim = maxD;
		}
		
		/**
		 * Returns the number of different colors within this color box.
		 * 
		 * @return the number of different colors
		 */
		int colorCount() {
			return hiIdx - loIdx;
		}

		/**
		 * Splits this color box at the median point along its longest color dimension. Modifies the original color box
		 * and creates a new one, which is returned.
		 *
		 * @return A new color box.
		 */
		ColorBox[] splitBox() {	
			if (this.colorCount() < 2)	// this box cannot be split
				throw new RuntimeException("cannot split " + this.toString());
			
			int m = this.level;
			// find longest dimension of this box:
			ColorDimension dim = this.maxDim; // this.getMaxBoxDimension();
			// find median along dimension d
			int medIdx = this.findMedian(dim);
			// now split this box at the median return the resulting new boxes
			ColorBox cb1 = new ColorBox(loIdx, medIdx, m + 1);
			ColorBox cb2 = new ColorBox(medIdx + 1, hiIdx, m + 1);
			return new ColorBox[] {cb1, cb2};
		}

		/**
		 * Finds the position of the median of this color box in RGB space along the red, green or blue dimension,
		 * respectively.
		 *
		 * @param dim the color dimension
		 * @return the median value
		 */
		int findMedian(ColorDimension dim) {
			// sort color in this box along dimension dim:
			Arrays.sort(origColors, loIdx, hiIdx + 1, dim.comparator);
			// find the median index:
			int half = count / 2;
			int k, pixCnt;
			for (k = loIdx, pixCnt = 0; k < hiIdx; k++) {
				pixCnt = pixCnt + origColors[k].cnt;
				if (pixCnt >= half)
					break;
			}			
			return k;		// k = index of median
		}

		/**
		 * Calculates the (linear) average of the colors contained in this color box and returns the result as a
		 * {@code float} vector. This is the 3D centroid (in RGB color space) of the pixel colors represented by this
		 * box.
		 *
		 * @return the average color for this box
		 */
		private float[] getAvgColor() {
			double rSum = 0;
			double gSum = 0;
			double bSum = 0;
			int n = 0;				// pixel count for this color box
			for (int i = loIdx; i <= hiIdx; i++) {
				ColorBin cn = origColors[i];
				int cnt = cn.cnt;
				rSum = rSum + cnt * cn.red;
				gSum = gSum + cnt * cn.grn;
				bSum = bSum + cnt * cn.blu;
				n = n + cnt;
			}
			float avgRed = (float) (rSum / n);
			float avgGrn = (float) (gSum / n);
			float avgBlu = (float) (bSum / n);
			return new float[] {avgRed, avgGrn, avgBlu};
		}

		@Override
		public String toString() {
			return String.format(Locale.US, 
					"%s[lev=%1d lo=%5d hi=%5d cnt=%5d R={%.1f, %.1f} G={%.1f, %.1f} B={%.1f, %.1f} maxD=%s maxS=%.1f]", 
					getClass().getSimpleName(), level, loIdx, hiIdx, count, 
					rmin, rmax, gmin, gmax, bmin, bmax, maxDim.toString(), maxSpan);
		}

		/**
		 * This method is required to satisfy the {@link Comparator} interface. It determines how boxes are selected for
		 * splitting. The specific strategy is to prefer color boxes at low levels. Boxes at the same level are selected
		 * based on their maximum color span, i.e., their maximum side length in color space. Finally, the number of
		 * pixels decides.
		 */
		@Override
		public int compareTo(ColorBox other) {
			int v = Integer.compare(this.level, other.level);	// first compare by level (lower levels first)
			if (v != 0) return v;
			v = Float.compare(other.maxSpan, this.maxSpan);		// then compare by color span (larger spans first)
			if (v != 0) return v;
			v = Integer.compare(other.count, this.count);		// then compare by number of pixels (more pixels first)
			return v;
		}
	}

	/**
	 * Associates the color dimensions RED, GRN, BLU with the corresponding comparators (for sorting along selected
	 * color dimensions).
	 */
	private enum ColorDimension {
		RED (new Comparator<ColorBin>() {
			@Override
			public int compare(ColorBin colA, ColorBin colB) {
				return Float.compare(colA.red, colB.red);
			}}), 
		GRN (new Comparator<ColorBin>() {
			@Override
			public int compare(ColorBin colA, ColorBin colB) {
				return Float.compare(colA.grn, colB.grn);
			}}), 
		BLU (new Comparator<ColorBin>() {
			@Override
			public int compare(ColorBin colA, ColorBin colB) {
				return Float.compare(colA.blu, colB.blu);
			}});

		private final Comparator<ColorBin> comparator;

		// constructor:
		private ColorDimension(Comparator<ColorBin> cmp) {
			this.comparator = cmp;
		}
	}
	
}

