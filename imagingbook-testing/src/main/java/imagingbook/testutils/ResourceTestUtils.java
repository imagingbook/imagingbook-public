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
	 * Scans all resources defined by the specified {@link NamedResource} class and
	 * validates that they exist.
	 * 
	 * @param <E> generic type
	 * @param enumClass enum class implementing {@link NamedResource}
	 * @return the number of validated resources
	 */
	public static <E extends Enum<E>> int testNamedResource(Class<E> enumClass) {
		assertTrue("enum must implement " + NamedResource.class, 
				NamedResource.class.isAssignableFrom(enumClass));
		int n = 0;
		for (E item : enumClass.getEnumConstants()) {
			NamedResource res = (NamedResource) item;
			assertNotNull(res.getRelativePath());
			assertNotNull(new File(res.getRelativePath()));
			assertNotNull(res.getURL());
			assertNotNull("could not find resource " + item.toString(), res.getURL());
			n++;
		}
		return n;
	}
	
	/**
	 * Scans all resources defined by some {@link ImageResource} class, validates that they can be opened
	 * as images.
	 * TODO: currently we cannot validate the existence of the resource in a case-sensitive manner,
	 * but this is IMPORTANT when loading resources from a JAR file later.
	 */
	/**
	 * Scans all resources defined by some {@link ImageResource} class and
	 * validates that they can be opened as images.
	 * @param <E> generic type
	 * @param enumClass enumClass enum class implementing {@link ImageResource}
	 * @return the number of validated image resources
	 */
	public static <E extends Enum<E>> int testImageResource(Class<E> enumClass) {
		assertTrue("enum must implement " + ImageResource.class, 
				ImageResource.class.isAssignableFrom(enumClass));
		int n = 0;
		for (E item : enumClass.getEnumConstants()) {
//			System.out.println("testing " + item);
			ImageResource res = (ImageResource) item;
			assertNotNull(res.getRelativePath());
			
			File file = new File(res.getRelativePath());
//			assertNotNull(file);
//			System.out.println("file can read = " + file.canRead());
			
//			System.out.println("URL = " + res.getURL());
//			System.out.println("expected name " + res.getFileName());
//			System.out.println("   is in Jar: " + res.isInsideJar());
		
			assertNotNull("could not find resource " + file.getAbsolutePath(), res.getURL());	
			assertNotNull("could not open image for resource " + res,  res.getImage());
			n++;
		}
		return n;
	}

}
