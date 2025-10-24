/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.testutils;

import imagingbook.core.resource.ImageResource;
import imagingbook.core.resource.NamedResource;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
	 * @param clazz enum class implementing {@link NamedResource}
	 * @return the number of validated resources
	 */
	public static int testNamedResource(Class<? extends NamedResource> clazz) {
		assertTrue("NamedResource class must be an enum type", clazz.isEnum());
		assertTrue("class must implement " + NamedResource.class, 
				NamedResource.class.isAssignableFrom(clazz));
		
		String[] names = NamedResource.getNamedResourceFileNames(clazz);
		HashSet<String> hs = new HashSet<>(Arrays.asList(names));
		
		int n = 0;
		for (NamedResource res : clazz.getEnumConstants()) {
			assertTrue(hs.contains(res.getFileName()));	// check if resource file exists (case sensitive!)
			assertNotNull(res.getRelativePath());
			assertNotNull(new File(res.getRelativePath()));
			assertNotNull(res.getURL());
			assertNotNull("could not find resource " + res.toString(), res.getURL());
			n++;
		}
		return n;
	}
	
	/**
	 * Scans all resources defined by some {@link ImageResource} class and
	 * validates that they can be opened as images.
	 * 
	 * @param clazz enum class implementing {@link ImageResource}
	 * @return the number of validated image resources
	 */
	public static int testImageResource(Class<? extends ImageResource> clazz) {
		assertTrue("ImageResource class must be an enum type", clazz.isEnum());
		assertTrue("class must implement " + ImageResource.class, 
				ImageResource.class.isAssignableFrom(clazz));
		
		String[] names = ImageResource.getResourceFileNames(clazz);
		HashSet<String> hs = new HashSet<>(Arrays.asList(names));
		
		int n = 0;
		for (ImageResource res : clazz.getEnumConstants()) {

			String expectedFileName = res.getFileName();
			assertTrue("image file not found for resource " + res.toString() + ", expected: " + expectedFileName,
					hs.contains(res.getFileName()));	// check if resource file exists (case sensitive!)
			
			assertNotNull(res.getRelativePath());
			
			File file = new File(res.getRelativePath());
//			assertNotNull(file);
//			System.out.println("file can read = " + file.canRead());
			
//			System.out.println("URL = " + res.getURL());
//			System.out.println("expected name " + res.getFileName());
//			System.out.println("   is in Jar: " + res.isInsideJar());
		
			assertNotNull("could not find resource " + file.getAbsolutePath(), res.getURL());	
			assertNotNull("could not open image for resource " + res,  res.getImagePlus());
			n++;
		}
		return n;
	}

}
