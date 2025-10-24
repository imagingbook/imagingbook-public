/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LinearContainerTest {

	@Test
	public void test1() {
		int n = 10;
		LinearContainer<Integer> lc = new LinearContainer<>(n);
		for (int i = 0; i < n; i++) {
			assertNull(lc.getElement(i));
		}
		
		for (Integer x : lc) {
			assertNull(x);
		}
	}
	
	@Test
	public void test2() {
		int bot = -13;
		int top = 27;
		LinearContainer<Integer> lc = new LinearContainer<>(bot, top);
		for (int i = bot; i <= top; i++) {
			lc.setElement(i, i);
		}
		
		for (int i = bot; i <= top; i++) {
			assertEquals(i, (long)lc.getElement(i));
		}
	}
	
	@Test
	public void test3() {
		int bot = -376200;
		int top = bot;
		LinearContainer<Integer> lc = new LinearContainer<>(bot, top);
		for (int i = bot; i <= top; i++) {
			lc.setElement(i, i);
		}
		
		for (int i = bot; i <= top; i++) {
			assertEquals(i, (long)lc.getElement(i));
		}
	}

}
