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
 *
 */
public interface ImageResource extends NamedResource {
	
	public default ImagePlus getImage() {
		return IJ.openImage(getURL().toString());
	}
	
	/**
	 * Returns a default image ".png" filename for this resource,
	 * to be used in parameterless enum constructors.
	 * In this case the file name is identical to the name
	 * of the enum constant and no separate file name needs to be
	 * specified.
	 * 
	 * @return a default image filename ({@code enumconstantname + ".png"})
	 */
	public default String autoName() {
		String itemname = this.toString();
		int k = itemname.lastIndexOf('_');
		if (k >= 0) {
//			System.out.println(itemname + ": found _ at " + k);
			String filename = itemname.substring(0, k);
			String extension  = itemname.substring(k + 1);
//			System.out.println(filename + "." + extension);
			return filename + "." + extension;
		}
		else {
			return itemname + ".png";
		}
	}
	
//	public default String autoName() {
//		return this.toString() + ".png";
//	}

}
