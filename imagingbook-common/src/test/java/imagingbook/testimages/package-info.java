/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

/**
 * <p>
 * This package contains test and sample images to be used with the {@literal imagingbook}
 * software suite.
 * Sample images are packed into a JAR file which is supposed to be imported as a Maven artifact.
 * Images are defined as "named resources" and verified (by unit testing) to exist in the
 * source tree.
 * </p>
 * <p>
 * This code is part of the {@literal imagingbook}
 * software suite supporting the Digital Image Processing books by Wilhelm Burger and Mark J Burge 
 * (see <a href="https://www.imagingbook.com">https://www.imagingbook.com</a>).
 * </p>
 * <p>
 * Usage example:
 * </p>
 * <pre>
 * import ij.process.ImageProcessor;
 * import imagingbook.core.resource.ImageResource;
 * import imagingbook.sampleimages.GeneralTestImage;
 * 
 * ImageResource ir = GeneralTestImage.Clown;
 * ImageProcessor ip = ir.getImage().getProcessor();
 * // process ip ...
 * </pre>
 * 
 * @see NamedResource
 * @see ImageResource
 */
package imagingbook.testimages;

import imagingbook.core.resource.ImageResource;
import imagingbook.core.resource.NamedResource;

