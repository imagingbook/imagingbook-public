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
public interface ImageResource extends NamedResource {
	
	public default ImagePlus getImage() {
		return IJ.openImage(getURL().toString());
	}
	


}
