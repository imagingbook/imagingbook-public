/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.core.resource;

import ij.IJ;
import ij.ImagePlus;

/**
 * <p>
 * Interface to be implemented by named image resources.
 * This indicates (for testing) that the associated resource can be opened as
 * an image (by ImageJ).
 * Extends interface {@link NamedResource} by adding method {@link #getImage()},
 * which returns an {@link ImagePlus} instance.
 * For example, given a named resource {@code MyImages.image1},
 * this can be used simply in the form
 * </p>
 * <pre>
 * ImagePlus im = MyImages.image1.getImage();
 * im.show();
 * </pre>
 * 
 * @author WB
 */
public interface ImageResource extends NamedResource {	// TODO: do away with explicit file names, use autoName() only?
	
	public default ImagePlus getImage() {
		return IJ.openImage(getURL().toString());
	}
	
	/**
	 * Derives and returns a filename for this resource item
	 * to be used in parameterless enum constructors.
	 * In this case the file name is identical to the name
	 * of the enum constant and no separate file name needs to be
	 * specified.
	 * The last underscore (_) character in the name, if any is contained,
	 * is replaced by a '.' character such that the remaining characters
	 * are interpreted as a file extension. 
	 * If no underscore is found, "png" is taken as the default file extension. 
	 * For example,
	 * <pre>
	 * "foo_tif"      --&gt; "foo.tif"
	 * "The_File_jpg" --&gt; "The_File.fpg"
	 * "foo"          --&gt; "foo.png"
	 * 
	 * @return the derived image filename
	 */
	public default String autoName() {
		String itemname = this.toString();
		int k = itemname.lastIndexOf('_');
		if (k >= 0) {
			String filename = itemname.substring(0, k);
			String extension  = itemname.substring(k + 1);
			return filename + "." + extension;
		}
		else {
			return itemname + ".png";
		}
	}

}
