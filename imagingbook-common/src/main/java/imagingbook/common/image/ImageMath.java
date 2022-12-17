/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image;

import ij.process.Blitter;
import ij.process.ImageProcessor;

/**
 * This class defines static methods for non-destructive, arithmetic operations on ImageJ's 
 * {@link ImageProcessor} objects.
 * Unlike the built-in {@link ImageProcessor} methods, the methods below always
 * return new images and keep the original images unmodified.
 * 
 * @version 2013/08/23: static methods converted to use generics.
 */
public abstract class ImageMath {
	
	private ImageMath() {}
	
	/**
	 * Calculates the absolute pixel values of the specified image
	 * and returns a new image of the same type.
	 *  
	 * @param <T> the type of {@link ImageProcessor}
	 * @param ip the input image
	 * @return the resulting image
	 */
	public static <T extends ImageProcessor> T abs(T ip) {
		@SuppressWarnings("unchecked")
		T ip2 = (T) ip.duplicate();
		ip2.abs();
		return ip2;
	}
	
	/**
	 * Calculates the squared pixel values of the specified image
	 * and returns a new image of the same type.
	 *  
	 * @param <T> the type of {@link ImageProcessor}
	 * @param ip the input image
	 * @return the resulting image
	 */
	public static <T extends ImageProcessor> T sqr(T ip) {
		@SuppressWarnings("unchecked")
		T ip2 = (T) ip.duplicate();
		ip2.sqr();
		return ip2;
	}
	
	/**
	 * Calculates the square root of the pixel values of the specified image
	 * and returns a new image of the same type.
	 *  
	 * @param <T> the type of {@link ImageProcessor}
	 * @param ip the input image
	 * @return the resulting image
	 */
	public static <T extends ImageProcessor> T sqrt(T ip) {
		@SuppressWarnings("unchecked")
		T ip2 = (T) ip.duplicate();
		ip2.sqrt();
		return ip2;
	}
	
	/**
	 * Adds the pixel values of the specified images
	 * and returns a new image of the same type
	 * as the first image.
	 *  
	 * @param <T> the type of {@link ImageProcessor}
	 * @param ip1 first input image
	 * @param ip2 second input image
	 * @return the resulting image
	 */
	public static<T extends ImageProcessor> T add(T ip1, T ip2) {
		if (!ip1.getClass().equals(ip2.getClass())) {
			throw new IllegalArgumentException("input images must be of the same type");
		}
		@SuppressWarnings("unchecked")
		T ip3 = (T) ip1.duplicate();
		ip3.copyBits(ip2, 0, 0, Blitter.ADD);
		return ip3;
	}
	
	/**
	 * Multiplies the pixel values of the specified images
	 * and returns a new image of the same type
	 * as the first image.
	 *  
	 * @param <T> the type of {@link ImageProcessor}
	 * @param ip1 first input image
	 * @param ip2 second input image
	 * @return the resulting image
	 */
	public static<T extends ImageProcessor> T mult(T ip1, T ip2) {
		if (!ip1.getClass().equals(ip2.getClass())) {
			throw new IllegalArgumentException("input images must be of the same type");
		}
		@SuppressWarnings("unchecked")
		T ip3 = (T) ip1.duplicate();
		ip3.copyBits(ip2, 0, 0, Blitter.MULTIPLY);
		return ip3;
	}

}
