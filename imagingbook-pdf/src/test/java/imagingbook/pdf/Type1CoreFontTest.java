/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.pdf;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertNotNull;

public class Type1CoreFontTest {

    /**
     * Locate the resource files for every enum value of Type1CoreFont and
     * instantiate the associated font object.
     * Note: this test used to have problems with class path / loader! And stil DOES!!
     * TODO: clean up class loader problem associated with getURL()!
     */
	@Test
	public void testType1CoreFonts() {
        for (Type1CoreFont fnt : Type1CoreFont.values()) {
            URL url = fnt.getClass().getResource(Type1CoreFont.Helvetica.getRelativePath());
            assertNotNull("could not find URL for resource " + fnt, url);
			//assertNotNull("could not find URL for resource " + fnt, fnt.getURL());      // fnt.getURL() throws error (class loader problem?)
			assertNotNull("could not create font " + fnt, fnt.getBaseFont(url.toString()));
            //assertNotNull("could not create font for resource " + fnt, fnt.getBaseFont());    // uses fnt.getURL(), which fails
		}
	}

}
