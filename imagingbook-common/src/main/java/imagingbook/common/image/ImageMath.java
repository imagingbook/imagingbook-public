/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.image;

import ij.process.Blitter;
import ij.process.ImageProcessor;


/**
 * This class provides some static utility methods for ImageJ's ImageProcessors.
 * Unlike the built-in {@link ImageProcessor} methods, the methods below
 * return new images and keep the original images unmodified.
 * 
 * @version 2013/08/23: static methods converted to use generics.
 */
public abstract class ImageMath {
	
//	public static FloatProcessor sqr(FloatProcessor fp) {
//		fp.sqr();
//		return fp;
//	}
	
	public static <T extends ImageProcessor> T abs(T ip) {
		@SuppressWarnings("unchecked")
		T ip2 = (T) ip.duplicate();
		ip2.abs();
		return ip2;
	}
	
	public static <T extends ImageProcessor> T sqr(T ip) {
		@SuppressWarnings("unchecked")
		T ip2 = (T) ip.duplicate();
		ip2.sqr();
		return ip2;
	}
	
	public static <T extends ImageProcessor> T sqrt(T ip) {
		@SuppressWarnings("unchecked")
		T ip2 = (T) ip.duplicate();
		ip2.sqrt();
		return ip2;
	}
	
	public static<T extends ImageProcessor> T add(T ip1, T ip2) {
		@SuppressWarnings("unchecked")
		T ip3 = (T) ip1.duplicate();
		ip3.copyBits(ip2, 0, 0, Blitter.ADD);
		return ip3;
	}
	
	public static<T extends ImageProcessor> T mult(T ip1, T ip2) {
		@SuppressWarnings("unchecked")
		T ip3 = (T) ip1.duplicate();
		ip3.copyBits(ip2, 0, 0, Blitter.MULTIPLY);
		return ip3;
	}

}
