/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.quantize;


import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.statistics.ColorHistogram;

/**
 * This is an implementation of Heckbert's median-cut color quantization algorithm 
 * (Heckbert P., "Color Image Quantization for Frame Buffer Display", ACM Transactions
 * on Computer Graphics (SIGGRAPH), pp. 297-307, 1982).
 * Unlike in the original algorithm, no initial uniform (scalar) quantization is used to
 * for reducing the number of image colors. Instead, all colors contained in the original
 * image are considered in the quantization process. After the set of representative
 * colors has been found, each image color is mapped to the closest representative
 * in RGB color space using the Euclidean distance.
 * The quantization process has two steps: first a ColorQuantizer object is created from
 * a given image using one of the constructor methods provided. Then this ColorQuantizer
 * can be used to quantize the original image or any other image using the same set of 
 * representative colors (color table).
 * 
 * @author WB
 * @version 2017/01/03
 * 
 * TODO: needs revision!
 */
public class MedianCutQuantizer implements ColorQuantizer {
	
	private final ColorNode[] allColors;	// a vector of color nodes (used by inner classes)
	private final Set<ColorBox> quantColors;
//	private final ColorNode root;		TODO: organize color boxes in a tree
	
	// -------------------------------------------------------------------------------
	
	public MedianCutQuantizer(ColorProcessor ip, int K) {
		this((int[]) ip.getPixels(), K);
	}
		 
	public MedianCutQuantizer(int[] pixels, int K) {
		allColors = getAllColors(pixels);
		if (allColors.length <= K) {		// not enough colors, nothing to quantize
			quantColors = null;
			return;
		}
		else {
			quantColors = findReferenceColors(K);
		}
	}
	
	// -------------------------------------------------------------------------------
	
//	void listColors(ColorNode[] colors) {
//		for (ColorNode nd : colors) {
//			IJ.log(nd.toString());
//		}
//	}
	
	private ColorNode[] getAllColors(int[] pixels) {
		ColorHistogram colorHist = new ColorHistogram(pixels);
		final int nc = colorHist.getNumberOfColors();
		ColorNode[] colors = new ColorNode[nc];
		for (int i = 0; i < nc; i++) {
			int rgb = colorHist.getColor(i);
			int cnt = colorHist.getCount(i);
			colors[i] = new ColorNode(rgb, cnt);
		}
		return colors;
	}

	private Set<ColorBox> findReferenceColors(int K) {
		final int n = allColors.length;

		ColorBox cb0 = new ColorBox(0, n - 1, 0);
		AbstractSet<ColorBox> B = new HashSet<ColorBox>();
		B.add(cb0);
		
		int k = 1;						// number of quantized color (color boxes)
		boolean done = false;
		
		while (k < K && !done) {
			ColorBox cb = findBoxToSplit(B);
			if (cb != null) {
				ColorBox[] boxes = cb.splitBox();
				B.remove(cb);
				B.add(boxes[0]);
				B.add(boxes[1]);
				k = k + 1;
			} else {
				done = true;
			}
		}
		return B;

	}
	
	private float[][] makeColorMap(Set<ColorBox> quantColors) {
		int n = quantColors.size();
		float[][] map = new float[n][];
		int i = 0;
		for (ColorBox cb : quantColors) {
			map[i] = cb.getAvgColor();
			i++;
		}
		return map;
	}
	
	private ColorBox findBoxToSplit(Iterable<ColorBox> colorBoxes) {
		// from the set of splitable color boxes
		// select the one with the minimum level
		ColorBox boxToSplit = null;
		int minLevel = Integer.MAX_VALUE;
		for (ColorBox cb : colorBoxes) {
			if (cb.colorCount() >= 2) {	// box can be split
				if (cb.level < minLevel) {
					boxToSplit = cb;
					minLevel = cb.level;
				}
			}
		}
		return boxToSplit;
	}
	
//	private ColorNode[] getAvgColors(Iterable<ColorBox> colorBoxes) {
//		//int n = colorBoxes.size();
////		ColorNode[] avgColors = new ColorNode[colorBoxes.size()];
//		List<ColorNode> avgColors = new ArrayList<>();
//		int i = 0;
//		for (ColorBox box : colorBoxes) {
//			//avgColors[i] = box.getAvgColor();
//			avgColors.add(box.getAvgColor());
//			i = i + 1;
//		}
////		return avgColors;
//		return avgColors.toArray(new ColorNode[0]);
//	}
	
	
	// ------- methods required by abstract super class -----------------------
	
	@Override
	public float[][] getColorMap() {
		return makeColorMap(quantColors);
	}

	// -------------- class ColorNode -------------------------------------------

	private class ColorNode {
		private final int red, grn, blu;
		private final int cnt;
		
		ColorNode (int rgb, int cnt) {
			//this.rgb = (rgb & 0xFFFFFF);
			int[] c = RgbUtils.intToRgb(rgb);
			this.red = c[0];
			this.grn = c[1];
			this.blu = c[2];
			this.cnt = cnt;
		}
		
		@SuppressWarnings("unused")
		ColorNode (int red, int grn, int blu, int cnt) {
			//this.rgb = Rgb.rgbToInt(red, grn, blu);
			this.red = red;
			this.grn = grn;
			this.blu = blu;
			this.cnt = cnt;
		}
		
		public String toString() {
			String s = ColorNode.class.getSimpleName();
			s = s + " red=" + red + " green=" + grn + " blue=" + blu + " count=" + cnt;
			return s;
		}
	}
	
	// -------------- non-static inner class ColorBox -----------------------------------

	/**
	 * Represents a 'color box' holding a set of colors (of type {@link ColorNode}),
	 * which is implemented as a contiguous range of elements in array
	 * {@link MedianCutQuantizer#allColors}. Instances of {@link ColorBox} reference
	 * this array directly (this is why this class is non-static).
	 */
	private class ColorBox { 
		final int lower; 		// lower index into 'imageColors'
		final int upper; 		// upper index into 'imageColors'
		final int level; 		// split level of this color box
		
		int count = 0; 	// number of pixels represented by this color box
		float rmin, rmax;	// range of contained colors in red dimension
		float gmin, gmax;	// range of contained colors in green dimension
		float bmin, bmax;	// range of contained colors in blue dimension
		
		ColorBox(int lower, int upper, int level) {
			this.lower = lower;
			this.upper = upper;
			this.level = level;
			this.trim();
		}
		
		/**
		 * Returns the number of different colors within this color box.
		 * @return the number of different colors
		 */
		int colorCount() {
			return upper - lower;
		}
		
		/**
		 * Recalculates the boundaries and population of this color box.
		 */
		private void trim() {
			int n = 0;	
			rmin = gmin = bmin = MAX_RGB;
			rmax = gmax = bmax = 0;
			for (int i = lower; i <= upper; i++) {
				ColorNode color = allColors[i];
				n = n + color.cnt;
				rmax = Math.max(color.red, rmax);
				rmin = Math.min(color.red, rmin);
				gmax = Math.max(color.grn, gmax);
				gmin = Math.min(color.grn, gmin);
				bmax = Math.max(color.blu, bmax);
				bmin = Math.min(color.blu, bmin);
			}
			count = n;
		}
		
		/**
		 * Splits this color box at the median point along its 
		 * longest color dimension. Modifies the original color
		 * box and creates a new one, which is returned.
		 * @return A new box.
		 */
		ColorBox[] splitBox() {	
			if (this.colorCount() < 2)	// this box cannot be split
				return null;
			else {
				int m = this.level;
				// find longest dimension of this box:
				ColorDimension d = this.getMaxBoxDimension();
				// find median along dimension d
				int med = this.findMedian(d);
				// now split this box at the median return the resulting new box
				ColorBox b1 = new ColorBox(lower, med, m + 1);
				ColorBox b2 = new ColorBox(med + 1, upper, m + 1);
				return new ColorBox[] {b1, b2};
			}
		}
		
		/**
		 * Finds the longest dimension of this color box (RED, GREEN, or BLUE)
		 * @return The color dimension of the longest box side.
		 */
		ColorDimension getMaxBoxDimension() {
			final float rLength = rmax - rmin;
			final float gLength = gmax - gmin;
			final float bLength = bmax - bmin;
			if (bLength >= rLength && bLength >= gLength)
				return ColorDimension.BLU;
			else if (gLength >= rLength && gLength >= bLength)
				return ColorDimension.GRN;
			else 
				return ColorDimension.RED;
		}
				
		/**
		 * Finds the position of the median of this color box in RGB space along
		 * the red, green or blue dimension, respectively.
		 * @param dim Color dimension.
		 * @return The median value.
		 */
		int findMedian(ColorDimension dim) {
			// sort color in this box along dimension dim:
			Arrays.sort(allColors, lower, upper + 1, dim.comparator);
			// find the median point:
			int half = count / 2;
			int k, pixCnt;
			for (k = lower, pixCnt = 0; k < upper; k++) {
				pixCnt = pixCnt + allColors[k].cnt;
				if (pixCnt >= half)
					break;
			}			
			return k;		// k = index of median
		}
		
		/**
		 * Calculates the (linear) average of the colors contained in this color box
		 * and returns the result as a {@link ColorNode} instance.
		 * The components are not rounded to integers (this is why they are of type float
		 * in the first place).
		 * 
		 * @return the average color in this box
		 */
		float[] getAvgColor() {
			double rSum = 0;
			double gSum = 0;
			double bSum = 0;
			int n = 0;
			for (int i = lower; i <= upper; i++) {
				ColorNode cn = allColors[i];
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
			String s = this.getClass().getSimpleName();
			s = s + " lower=" + lower + " upper=" + upper;
			s = s + " count=" + count + " level=" + level;
			s = s + " rmin=" + rmin + " rmax=" + rmax;
			s = s + " gmin=" + gmin + " gmax=" + gmax;
			s = s + " bmin=" + bmin + " bmax=" + bmax;
			s = s + " bmin=" + bmin + " bmax=" + bmax;
			return s;
		}
	}
		
	/**
	 * The main purpose of this inner enumeration class is to associate the
	 * color dimensions RED, GRN, BLU with the corresponding comparators
	 * (for sorting along selected color dimensions).
	 */
	private enum ColorDimension {
		RED (new Comparator<ColorNode>() {
			@Override
			public int compare(ColorNode colA, ColorNode colB) {
				return Float.compare(colA.red, colB.red);
			}}), 
		GRN (new Comparator<ColorNode>() {
			@Override
			public int compare(ColorNode colA, ColorNode colB) {
				return Float.compare(colA.grn, colB.grn);
			}}), 
		BLU (new Comparator<ColorNode>() {
			@Override
			public int compare(ColorNode colA, ColorNode colB) {
				return Float.compare(colA.blu, colB.blu);
			}});

		final Comparator<ColorNode> comparator;

		// constructor:
		ColorDimension(Comparator<ColorNode> cmp) {
			this.comparator = cmp;
		}
	}
	
	
	// ----------------------------------------------------------------------
	
	public static void main(String[] args) {
//		String path = "D:/svn-book/Book/img/ch-color-images/alps-01s.png";
		String path = "D:/svn-book/Book/img/ch-color-images/desaturation-hsv/balls.jpg";
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
		
		MedianCutQuantizer quantizer = new MedianCutQuantizer(cp, K);
		quantizer.listColorMap();
		
		System.out.println("quantizing image");
		ByteProcessor qi = quantizer.quantize(cp);
		System.out.println("showing image");
		(new ImagePlus("quantizez", qi)).show();
		
	}
	
} 

/*
image = D:/svn-book/Book/img/ch-color-images/random-colors.png
K = 16
i=  0: r= 74 g=116 b= 22
i=  1: r= 24 g=226 b= 22
i=  2: r=207 g= 31 b= 22
i=  3: r=104 g=226 b= 22
i=  4: r= 70 g=147 b= 63
i=  5: r= 45 g=226 b= 61
i=  6: r=207 g= 31 b= 89
i=  7: r=134 g=226 b= 62
i=  8: r= 68 g=178 b= 23
i=  9: r= 65 g=227 b= 21
i= 10: r=189 g=127 b= 23
i= 11: r=163 g=225 b= 21
i= 12: r= 68 g=148 b=114
i= 13: r= 46 g=226 b=114
i= 14: r=189 g=133 b= 87
i= 15: r=133 g=227 b=114

image = D:/svn-book/Book/img/ch-color-images/desaturation-hsv/balls.jpg
K = 16
i=  0: r= 69 g= 42 b= 44
i=  1: r= 69 g= 65 b= 94
i=  2: r=220 g= 87 b= 14
i=  3: r=171 g= 73 b=102
i=  4: r= 37 g=108 b= 38
i=  5: r= 44 g=119 b=162
i=  6: r=226 g=180 b=  3
i=  7: r=191 g=133 b=139
i=  8: r=153 g= 32 b= 45
i=  9: r= 61 g= 84 b=131
i= 10: r=226 g=134 b=  7
i= 11: r=237 g= 84 b=130
i= 12: r=145 g= 98 b= 24
i= 13: r= 37 g=148 b=202
i= 14: r=244 g=209 b=  1
i= 15: r=209 g=203 b=209

*/

