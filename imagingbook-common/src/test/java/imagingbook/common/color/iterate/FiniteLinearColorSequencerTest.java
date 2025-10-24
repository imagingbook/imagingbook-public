/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.iterate;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import org.junit.Test;

import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.color.sets.ColorEnumeration;

public class FiniteLinearColorSequencerTest {

	@Test
	public void test1() {
		Color[] colors = {Color.blue, Color.green, Color.red, Color.gray, Color.black};
		ColorSequencer iter = new FiniteLinearColorSequencer(colors);
		for (int i = 0; i < 100; i++) {
			Color c = iter.next();
			assertEquals(colors[i % colors.length], c);
		}
	}
	
	@Test	// check if reset() works properly
	public void test2() {
		Color[] colors = ColorEnumeration.getColors(BasicAwtColor.class);
		for (int offset = -20; offset < 20; offset++) {
			FiniteLinearColorSequencer iter = new FiniteLinearColorSequencer(BasicAwtColor.class);
			iter.reset(offset);
			for (int i = 0; i < 10; i++) {
				Color c = iter.next();
				assertEquals(colors[Math.floorMod(i + offset, colors.length)], c);
			}
		}
	}

}
