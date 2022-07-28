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
