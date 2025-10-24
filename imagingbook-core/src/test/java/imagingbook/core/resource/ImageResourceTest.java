/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.core.resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImageResourceTest {
	
	private enum DummyNamedResource implements ImageResource {
		a,
		A_png,
		foo_tif,
		foo_tiff,
		The_File_jpg,
		The_File_jpeg,
		_Some____File_bla,
		;
	}

	@Test
	public void testAutoName() {
		assertEquals("a.png", DummyNamedResource.a.getFileName());
		assertEquals("A.png", DummyNamedResource.A_png.getFileName());
		assertEquals("foo.tif", DummyNamedResource.foo_tif.getFileName());
		assertEquals("foo.tiff", DummyNamedResource.foo_tiff.getFileName());
		assertEquals("The_File.jpg", DummyNamedResource.The_File_jpg.getFileName());
		assertEquals("The_File.jpeg", DummyNamedResource.The_File_jpeg.getFileName());
		assertEquals("_Some____File_bla.png", DummyNamedResource._Some____File_bla.getFileName());
	}
	
	// public static void main(String[] args) {
	// 	for (ImageResource item : DummyNamedResource.values()) {
	// 		System.out.println(item + " --> " + item.getFileName());
	// 	}
	// }

}
