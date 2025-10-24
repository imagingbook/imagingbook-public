/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.sets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;

import org.junit.Test;

public class ColorEnumerationTest {

	@Test
	public void test1() {
		Color[] colors = ColorEnumeration.getColors(BasicAwtColor.class);
		assertEquals(colors.length, BasicAwtColor.values().length);
	}
	
	@Test
	public void test2() {
		Color[] colors = ColorEnumeration.getColors(BasicAwtColor.Blue, CssColor.Aqua, RalColor.RAL1012);
		assertEquals(3, colors.length);
		
		assertEquals(colors[0], BasicAwtColor.Blue.getColor());
		assertEquals(colors[1], CssColor.Aqua.getColor());
		assertEquals(colors[2], RalColor.RAL1012.getColor());
	}
	
	@Test
	public void test3() {
		ColorEnumeration ce1 = BasicAwtColor.Blue;
		Color col = ce1.getColor();
		assertNotNull(col);
		ColorEnumeration ce2 = ColorEnumeration.findColor(col, BasicAwtColor.class);
		assertNotNull(ce2);
		assertEquals(ce1, ce2);
	}

}
