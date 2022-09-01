/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pdf;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class Type1CoreFontTest {

	@Test
	public void test1() {
		for (Type1CoreFont fnt : Type1CoreFont.values()) {
			assertNotNull("could not find URL for resource " + fnt, fnt.getURL());
			assertNotNull("could not create font for resource " + fnt, fnt.getBaseFont());
		}
	}

}
