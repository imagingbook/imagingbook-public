/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.core.resource;

import java.util.Arrays;
import java.util.HashSet;

import ij.IJ;
import ij.ImagePlus;

/**
 * <p>
 * Interface to be implemented by named image resources.
 * This indicates (for testing) that the associated resource can be opened as
 * an image (by ImageJ).
 * Extends interface {@link NamedResource} by adding method {@link #getImage()},
 * which returns an {@link ImagePlus} instance.
 * By default, image files are assumed to reside in a directory at the same 
 * level and with exactly the same name as the defining enum class itself.
 * E.g., in a standard Maven-setup this is:
 * </p>
 * <pre>
 * .../java/    .../bar/MyImageResource.java          = enum class implementing 'ImageResource'
 * .../resource/.../bar/MyImageResource/image1.png    = associated image files
 * .../resource/.../bar/MyImageResource/image2.tif
 * .../resource/.../bar/MyImageResource/...</pre>
 * <p>
 * For example, given a named resource {@code MyImages.image1},
 * this can be used simply in the form
 * </p>
 * <pre>
 * ImagePlus im = MyImages.image1.getImage();
 * im.show();
 * </pre>
 * <p>
 * By default, resource file names are derived automatically from the
 * enum item's name (by method {@link #autoName()}).
 * If some other behavior is needed, method {@link #getFileName()}
 * should be overridden.
 * 
 * @author WB
 */
public interface ImageResource extends NamedResource {
	
	public default ImagePlus getImage() {
		return IJ.openImage(getURL().toString());
	}
	
	@Override
	public default String getFileName() {
		return this.autoName();
	}
	
	
	static final HashSet<String> ValidImageExtensions = 
			new HashSet<>(Arrays.asList("png", "tif", "tiff", "jpg", "jpeg"));
	
	/**
	 * <p>
	 * Derives and returns a filename for this resource item to be used in
	 * parameterless enum constructors. By default the file name is identical to the
	 * name of the enum constant supplemented with a ".png" extension. No separate
	 * file name needs to be supplied. A different file extension may be specified
	 * by having the enum name end with an underscore followed by a valid image file
	 * extension, that is, "png", "tif", "tiff", "jpg", or "jpeg". In this case, the
	 * last underscore of the enum name is replaced by a '.' character to form the
	 * file name. (Note that '.' is no legal character in a Java identifier, thus
	 * cannot be used for the enum name directly.) If the last underscore is not
	 * followed by a valid extension, the default case is assumed ("png").
	 * </p>
	 * <p>
	 * Examples:
	 * </p>
	 * <pre>
	 * enum DummyNamedResource implements ImageResource {
	 *	a,
	 *	A_png,				// A.png
	 *	foo_tif,			// foo.tif
	 *	foo_tiff,			// foo.tiff
	 *	The_File_jpg,		// The_File.jpg
	 *	The_File_jpeg,		// The_File.jpeg
	 *	_Some____File_bla;	// _Some____File_bla.png
	 * }
	 * </pre>
	 * 
	 * @return the derived image filename
	 */
	public default String autoName() {
		String itemname = this.toString();
		int k = itemname.lastIndexOf('_');
		if (k >= 0) {
			String filename = itemname.substring(0, k);
			String extension  = itemname.substring(k + 1);
			if (ValidImageExtensions.contains(extension)) {
				return filename + "." + extension;
			}
		}
		return itemname + ".png";
	}
	



}
