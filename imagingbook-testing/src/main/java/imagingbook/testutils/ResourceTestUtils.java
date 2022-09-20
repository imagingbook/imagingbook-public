/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.testutils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import imagingbook.core.resource.ImageResource;
import imagingbook.core.resource.NamedResource;

/**
 * Static methods for testing implementations of {@link NamedResource} and {@link ImageResource}.
 * This class is duplicated in various projects since it is only available during the
 * test phase and thus not contained in any dependency JAR. 
 * 
 * @author WB
 */
public abstract class ResourceTestUtils {
	
	/**
	 * Scans all resources defined by some {@link NamedResource} class, validates that they exist.
	 */
	public static <E extends Enum<E>> void testNamedResource(Class<E> enumClass) {
		assertTrue("enum must implement NamedResource", NamedResource.class.isAssignableFrom(enumClass));
		if (NamedResource.class.isAssignableFrom(enumClass)) {
			for (E item : enumClass.getEnumConstants()) {
				NamedResource res = (NamedResource) item;
				assertNotNull(res.getRelativePath());
				assertNotNull(new File(res.getRelativePath()));
				assertNotNull(res.getURL());
				assertNotNull("could not find resource " + item.toString(), res.getURL());
			}
				
		}
	}
	
	/**
	 * Scans all resources defined by some {@link ImageResource} class, validates that they can be opened
	 * as images.
	 */
	public static <E extends Enum<E>> void testImageResource(Class<E> enumClass) {
		assertTrue("enum must implement ImageResource", ImageResource.class.isAssignableFrom(enumClass));
		if (ImageResource.class.isAssignableFrom(enumClass)) {
			for (E item : enumClass.getEnumConstants()) {
				ImageResource res = (ImageResource) item;
				assertNotNull(res.getRelativePath());
				File file = new File(res.getRelativePath());
				assertNotNull(file);
				assertNotNull(res.getURL());
				assertNotNull("could not find file " + file.getAbsolutePath(), res.getURL());
				assertNotNull("could not open image for resource " + res,  res.getImage());
			}
		}
	}

}
