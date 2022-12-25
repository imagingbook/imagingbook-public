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
