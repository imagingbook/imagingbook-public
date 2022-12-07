/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.core.resource;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import ij.IJ;
import ij.ImagePlus;

/**
 * <p>
 * Interface to be implemented by named image resources. This indicates (for testing) that the associated resource can
 * be opened as an image (by ImageJ). Extends interface {@link NamedResource} by adding method {@link #getImagePlus()},
 * which returns an {@link ImagePlus} instance. By default, image files are assumed to reside in a directory at the same
 * level and with exactly the same name as the defining enum class itself. E.g., in a standard Maven-setup this is:
 * </p>
 * <pre>
 * .../java/    .../bar/MyImageResource.java          = enum class implementing 'ImageResource'
 * .../resource/.../bar/MyImageResource/image1.png    = associated image files
 * .../resource/.../bar/MyImageResource/image2.tif
 * .../resource/.../bar/MyImageResource/...</pre>
 * <p>
 * For example, given a named resource {@code MyImages.image1}, this can be used simply in the form
 * </p>
 * <pre>
 * ImagePlus im = MyImages.image1.getImage();
 * im.show();</pre>
 * <p>
 * By default, resource file names are derived automatically from the enum item's name (by method {@link #autoName()}).
 * If some other behavior is needed, method {@link #getFileName()} should be overridden.
 *
 * @author WB
 */
public interface ImageResource extends NamedResource {

	/**
	 * Opens end returns a {@link ImagePlus} instance for this {@link ImageResource}.
	 *
	 * @return a {@link ImagePlus} instance
	 */
	public default ImagePlus getImagePlus() {
		// TODO: opening GIF image stacks does not work, returns only a single image
		return IJ.openImage(getURL().toString());
	}
	
	@Override
	public default String getFileName() {
		return this.autoName();
	}
	
	/**
	 * The set of image file extensions supported in {@link #autoName()}.
	 */
	static final HashSet<String> ValidImageExtensions = 
			new HashSet<>(Arrays.asList("png", "tif", "tiff", "jpg", "jpeg", "gif"));

	/**
	 * <p>
	 * Derives and returns a filename for this resource item to be used in parameterless enum constructors. By default
	 * the file name is identical to the name of the enum constant supplemented with a ".png" extension. No separate
	 * file name needs to be supplied. A different file extension may be specified by having the enum name end with an
	 * underscore followed by a valid image file extension, that is, "png", "tif", "tiff", "jpg", "jpeg" or "gif". In
	 * this case, the last underscore of the enum name is replaced by a '.' character to form the file name. (Note that
	 * '.' is no legal character in a Java identifier, thus cannot be used for the enum name directly.) If the last
	 * underscore in a item's name is not followed by a valid extension, the default case is assumed ("png").
	 * </p>
	 * <p>
	 * Examples:
	 * </p>
	 * <pre>
	 * enum DummyNamedResource implements ImageResource {
	 * 	a,                  // file "a.png"
	 * 	A_png,              // file "A.png"
	 * 	foo_tif,            // file "foo.tif"
	 * 	foo_tiff,           // file "foo.tiff"
	 * 	The_File_jpg,       // file "The_File.jpg"
	 * 	The_File_jpeg,      // file "The_File.jpeg"
	 * 	_Some____File_bla;  // file "_Some____File_bla.png"
	 * }</pre>
	 *
	 * @return the image filename derived from the enum item's name
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

	/**
	 * Returns the names of the actual files contained in the associated resource directory of the specified class,
	 * which must implement the {@link ImageResource} interface. This can be used to check if a given named resource has
	 * a matching file in a case-sensitive way.
	 *
	 * @param clazz the resource class
	 * @return an array of strings
	 */
	public static String[] getResourceFileNames(Class<? extends ImageResource> clazz) {
		return ResourceUtils.getResourceFileNames(clazz, clazz.getSimpleName());
	}

	// ---------------- icon handling --------------------------------------

	/**
	 * The default icon size (maximum width or height).
	 */
	static final int DefaultIconSize = 128;
	static final HashMap<String, ImagePlus> IconMap = new HashMap<>();

	/**
	 * Returns an icon for this {@link ImageResource} with the default icon size. All icons are cached, i.e., are only
	 * created once when first requested for a particular size.
	 *
	 * @return an {@link ImagePlus} instance containing the icon image
	 * @see #DefaultIconSize
	 * @see #getImageIcon(int)
	 */
	public default ImagePlus getImageIcon() {
		return getImageIcon(DefaultIconSize);
	}

	/**
	 * Returns an icon for this {@link ImageResource} with the specified size. All icons are cached, i.e., are only
	 * created once when first requested for a particular size.
	 *
	 * @param iconSize
	 * @return an {@link ImagePlus} instance containing the icon image
	 * @see #getImageIcon()
	 */
	public default ImagePlus getImageIcon(int iconSize) {
		String key =  this.toString() + iconSize + this.getClass().getCanonicalName();
		ImagePlus hashedImp = IconMap.get(key);
		if (hashedImp != null) {
			return hashedImp;
		}
		else {
			Image im = this.getImagePlus().getImage();	// an AWT image!
			int w = im.getWidth(null);
			int h = im.getHeight(null);
			int hints = Image.SCALE_SMOOTH;
			Image imScaled = (w >= h) ?
					im.getScaledInstance(iconSize, -1, hints) :
					im.getScaledInstance(-1, iconSize, hints);
			ImagePlus icon = new ImagePlus(null, imScaled);
			IconMap.put(key, icon);
			return icon;
		}
	}

}
