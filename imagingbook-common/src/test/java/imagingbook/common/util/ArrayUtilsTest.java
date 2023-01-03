/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class ArrayUtilsTest {

	@Test
	public void testCountNonNullElements() {
		String[] items1 = {"A", "B", null, "C"};
		assertEquals(3, ArrayUtils.countNonNullElements(items1));
	}

	@Test
	public void testArrayIterator1() {
		String[] items1 = {"A", "B", null, "C"};
		
		Iterator<String> iter = ArrayUtils.getIterator(items1);
		
		List<String> list = new ArrayList<>();
		while (iter.hasNext()) {
			String s = iter.next();
			list.add(s);
		}
		
		String[] items2 = list.toArray(new String[0]);
		assertArrayEquals(items1, items2);
	}
	
	@Test
	public void testArrayIterator2() {
		Integer[] items1 = {9, 4, 5, 6 -3, 19};
		
		Iterator<Integer> iter = ArrayUtils.getIterator(items1);
		
		List<Integer> list = new ArrayList<>();
		while (iter.hasNext()) {
			int s = iter.next();
			list.add(s);
		}
		
		Integer[] items2 = list.toArray(new Integer[0]);
		assertArrayEquals(items1, items2);
	}

}
