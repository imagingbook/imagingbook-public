/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.core.resource;

import ij.IJ;
import ij.ImagePlus;

import java.awt.Image;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * <p>
 * This interface is supposed to be implemented by {@code enum} classes to provide a
 * repository for images that is simple and safe to use.
 * Since this repository can be tested at compile-time for completeness and readability,
 * dependent code can be certain that all listed resources are available at runtime.
 * </p>
 * <p>
 * Methods are provided to directly open the associated resources
 * as images (by ImageJ).
 * This interface extends i{@link NamedResource} by adding method {@link #getImagePlus()},
 * which returns an {@link ImagePlus} instance.
 * By default, image files are assumed to reside in a directory at the same
 * package level as the associated enum-class in a special subdirectory named
 * {@code <EnumClassName>-data}.
 * For example, in a standard Maven setup this means:
 * </p>
 * <pre>
 * main/java/com/foo/MyImageResource.java          = 'ImageResource' enum class
 * main/resources/com/foo/MyImageResource-data/image1.png    = image files
 * main/resources/com/foo/MyImageResource-data/image2.tif
 * main/resources/com/foo/MyImageResource-data/...</pre>
 * <p>
 * For example, given a named resource {@code MyImageResource.image1}, 
 * this can be used simply in the form
 * </p>
 * <pre>
 * ImagePlus im = MyImageResource.image1.getImage();
 * im.show();</pre>
 * <p>
 * By default, resource file names are derived automatically from the enum item's name (by method
 * {@link #getAutoName()}). File extensions can be encoded in the enum-item's name
 * (such as {@code bar_tif} for some image file {@code bar.tif}).
 * If no extension is encoded, "png" is assumed as the default extension.
 * This behavior can be changed by overriding method {@link #getDefaultExtension()}.
 * If some different file-related behavior is needed, override method {@link #getFileName()}.
 * </p>
 * <p>
 * See also {@code imagingbook.testutils.ResourceTestUtils#testImageResource(Class)} for testing.
 * </p>
 * @author WB
 */
public interface ImageResource extends NamedResource {

	/**
	 * Opens end returns a {@link ImagePlus} instance for this {@link ImageResource}.
	 * @return a {@link ImagePlus} instance
	 */
	public default ImagePlus getImagePlus() {
		// TODO: opening GIF image stacks does not work, returns only a single image
		return IJ.openImage(getURL().toString());
	}
	
	@Override
	public default String getFileName() {
		return this.getAutoName();
	}
	
	/**
	 * The set of image file extensions supported in {@link #getAutoName()}.
	 */
	static final HashSet<String> ValidImageFileExtensions =
			new HashSet<>(Arrays.asList("png", "tif", "tiff", "jpg", "jpeg", "gif"));

	/**
	 * <p>
	 * Derives and returns a filename for this resource item to be used in parameterless enum constructors. By default
	 * the file name is identical to the name of the enum constant supplemented with a ".png" extension. No separate
	 * file name needs to be supplied. A different file extension may be specified by having the enum name end with an
	 * underscore followed by a valid image file extension, that is, "png", "tif", "tiff", "jpg", "jpeg" or "gif". In
	 * this case, the last underscore of the enum name is replaced by a '.' character to form the file name. (Note that
	 * '.' is no legal character in a Java identifier, thus cannot be used for the enum name directly.) If the last
	 * underscore in an item's name is not followed by a valid extension, the default case is assumed ("png").
	 * This default-extension can be changed by overriding method {@link #getDefaultExtension()}.
	 * </p>
	 * <p>
	 * Examples:
	 * </p>
	 * <pre>
	 * enum MyImageResource implements ImageResource {
	 * 	a,                  // file "a.png"
	 * 	A_png,              // file "A.png"
	 * 	foo_tif,            // file "foo.tif"
	 * 	foo_tiff,           // file "foo.tiff"
	 * 	The_File_jpg,       // file "The_File.jpg"
	 * 	The_File_jpeg,      // file "The_File.jpeg"
	 * 	_Some____File_bla;  // file "_Some____File_bla.png"
	 * }</pre>
	 * TODO: support "-" in file names
	 * @return the image filename derived from the enum item's name
	 */
	public default String getAutoName() {
		String itemname = this.toString();
		int k = itemname.lastIndexOf('_');
		if (k >= 0) {
			String filename = itemname.substring(0, k);
			String extension  = itemname.substring(k + 1);
			// found an encoded file extension:
			if (ValidImageFileExtensions.contains(extension)) {
				return filename + "." + extension;
			}
		}
		// no encoded file extension, use default extension
		return itemname + "." + getDefaultExtension();
	}

	/**
	 * Implementing classes should override this method if extensions are not encoded in enum-names,
	 * all images for this resource have the same file extension that is not "png".
	 * @return the default file extension, e.g. "jpg" (no dot!)
	 */
	public default String getDefaultExtension() {
		return "png";
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
		// return ResourceUtils.getResourceFileNames(clazz, clazz.getSimpleName() + RelativeDirectorySuffix);
		return ResourceUtils.getResourceFileNames(clazz, NamedResource.getRelativeDirectory(clazz));
	}

	// ---------------- thumbnail handling --------------------------------------

	/**
	 * The default thumbnail size (maximum width or height).
	 */
	static final int DefaultThumbSize = 128;
	static final HashMap<String, ImagePlus> ThumbnailMap = new HashMap<>();

	/**
	 * Returns a thumbnail for this {@link ImageResource} with the default icon size.
	 * All thumbnails are cached, i.e., are only
	 * created once when first requested for a particular size.
	 *
	 * @return an {@link ImagePlus} instance containing the icon image
	 * @see #DefaultThumbSize
	 * @see #getThumbnail(int)
	 */
	public default ImagePlus getThumbnail() {
		return getThumbnail(DefaultThumbSize);
	}

	/**
	 * Returns a thumbnail for this {@link ImageResource} with the specified size.
	 * All thumbnails are cached, i.e., are only
	 * created once when first requested for a particular size.
	 * @param size the size of the thumbnail (largest dimension)
	 * @return an {@link ImagePlus} instance containing the icon image
	 * @see #getThumbnail()
	 */
	public default ImagePlus getThumbnail(int size) {
		String key =  this.toString() + size + this.getClass().getCanonicalName();
		ImagePlus hashedImp = ThumbnailMap.get(key);
		if (hashedImp != null) {
			return hashedImp;
		}
		else {
			Image im = this.getImagePlus().getImage();	// an AWT image!
			int w = im.getWidth(null);
			int h = im.getHeight(null);
			int hints = Image.SCALE_SMOOTH;
			Image imScaled = (w >= h) ?
					im.getScaledInstance(size, -1, hints) :
					im.getScaledInstance(-1, size, hints);
			ImagePlus thumb = new ImagePlus(null, imScaled);
			ThumbnailMap.put(key, thumb);
			return thumb;
		}
	}

}
