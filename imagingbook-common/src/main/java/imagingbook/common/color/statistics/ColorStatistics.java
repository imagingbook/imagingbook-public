/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.statistics;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorStatistics {
	
	//determine how many different colors are contained in the 24 bit full-color image cp
	public static int countColors (ColorProcessor cp) { 
		// duplicate pixel array and sort
		int[] pixels = (int[]) cp.getPixelsCopy();
		Arrays.sort(pixels);  
		
		int n = 1;	// image contains at least one color
		for (int i = 0; i < pixels.length-1; i++) {
			if (pixels[i] != pixels[i+1])
				n = n + 1;
		}
		return n;
	}
	
	
	private static int countColors2 (ColorProcessor cp) { 
		System.out.println("\ncountColors2:");
		// duplicate pixel array and sort
		int[] pixels = (int[]) cp.getPixelsCopy();
		System.out.println("npixels = " + pixels.length);
		
		Arrays.sort(pixels);  
		int P = pixels.length;
		List<int[]> C = new ArrayList<>();				// holds pairs colorindex/count
		
		int n = 1;	// image contains at least one color
		for (int i = 0; i <= P - 2; i++) {
			if (pixels[i] != pixels[i+1]) {
				C.add(new int[] {pixels[i], n});
				n = 1;
			}
			else {		// pixels[i] == pixels[i+1]
				n = n + 1;	// keep counting same color
			}
		}
		C.add(new int[] {pixels[P - 1], n});
		
		int nc = C.size();
		int ntotal = 0;
		for (int[] tup : C) {
			ntotal = ntotal + tup[1];		// add pixel counts for all colors
		}
		System.out.println("ntotal = " + ntotal);
		
		return nc;
	}
	
	private static int countColors3 (ColorProcessor cp) { 
		System.out.println("\ncountColors3:");
		// duplicate pixel array and sort
		int[] pixels = (int[]) cp.getPixelsCopy();
		System.out.println("npixels = " + pixels.length);
		
		Arrays.sort(pixels);  
		int P = pixels.length;
		List<int[]> C = new ArrayList<>();				// holds pairs colorindex/count
		
		int n = 1;	// image contains at least one color
		for (int i = 0; i < P; i++) {
			if (i < P - 1 && pixels[i] == pixels[i+1]) {
				n = n + 1;	// keep counting same color
			}
			else {	// last pixel of color run
				C.add(new int[] {pixels[i], n});
				n = 1;
			}
		}
		
		int nc = C.size();
		int ntotal = 0;
		for (int[] tup : C) {
			ntotal = ntotal + tup[1];		// add pixel counts for all colors
		}
		System.out.println("ntotal = " + ntotal);

		
		return nc;
	}
	
	//computes the combined color histogram for color components (c1, c2)
	// c1, c2:  R = 0, G = 1, B = 2
	static int[][] get2dHistogram (ColorProcessor cp, int c1, int c2) {
		int[] RGB = new int[3];
		int[][] H = new int[256][256];	// histogram array H[i1][i2] 

		for (int v = 0; v < cp.getHeight(); v++) {
			for (int u = 0; u < cp.getWidth(); u++) {
				cp.getPixel(u, v, RGB); 
				int i1 = RGB[c1];	
				int i2 = RGB[c2];	
				// increment corresponding histogram cell
				H[i1][i2]++;
			}
		}	
		return H;
	}
	
	// ----------------------------------------------------------------------
	
	public static void main(String[] args) {
		String path = "D:/svn-book/Book/img/ch-color-images/alps-01s.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/desaturation-hsv/balls.jpg";
//		String path = "D:/svn-book/Book/img/ch-color-images/single-color.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/two-colors.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/random-colors.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/ramp-fire.png";
		ImagePlus im = IJ.openImage(path);
		if (im == null) {
			System.out.println("could not open: " + path);
			return;
		}
		
		ImageProcessor ip = im.getProcessor();
		ColorProcessor cp = ip.convertToColorProcessor();
		
		int nc1 = countColors(cp);
		System.out.println("nc1 = " + nc1);
		
		int nc2 = countColors2(cp);
		System.out.println("nc2 = " + nc2);
		
		int nc3 = countColors3(cp);
		System.out.println("nc3 = " + nc3);
		
	}
	

}
