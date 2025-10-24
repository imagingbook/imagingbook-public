/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.sampleimages;

import imagingbook.core.resource.ImageResource;
import imagingbook.core.resource.ResourceUtils;
import imagingbook.testutils.ResourceTestUtils;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GeneralSampleImageTest {

    @Test   // checks if number of enum constants is the same as the number of resources in file system
    public void testImageCount() {
        int n = GeneralSampleImage.values().length;
        //System.out.println("n = " + n);
        String[] names = ImageResource.getResourceFileNames(GeneralSampleImage.class);
        assertEquals(n, names.length);
    }

    @Test
    public void testGeneralSampleImageResourcesA() {
        GeneralSampleImage[] items = GeneralSampleImage.values();
        for (GeneralSampleImage res : items) {
            String fileName = res.getFileName();
            assertNotNull(fileName);

            String relPath = res.getRelativePath();
            assertNotNull(relPath);

            File file = new File(relPath);
            assertNotNull(file);

            URL url = res.getURL();
            assertNotNull("Could not locate resource " + file.getAbsolutePath(), url);

            // try to instantiate image from resource:
            assertNotNull("Could not open image from resource " + res,  res.getImagePlus());
        }
    }

	
	@Test   // does basically the same as above using a dedicated method 'testImageResource()'
	public void testGeneralSampleImageResourcesB() {
		int n = ResourceTestUtils.testImageResource(GeneralSampleImage.class);
        assertEquals(n, GeneralSampleImage.values().length);
	}

}
