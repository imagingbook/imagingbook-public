package imagingbook.common.random;

import static imagingbook.common.ransac.RandomDraw.hasDuplicates;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.ransac.RandomDraw;

public class RandomDrawTest {

	@Test
	public void test1() {
		Integer[] numbers = { null, 1, 2, null, 3, 4, 5, 6, 7, null, null, null, 8, 9, 10 , null};
		int K = 2;
		int N = 1000000;
		
		Random rg = new Random(17);
		RandomDraw<Integer> rd = new RandomDraw<>(rg);
		
		for (int i = 0; i < N; i++) {
			Integer[] draw = rd.drawFrom(numbers, K);
			assertEquals(K, draw.length);
			assertFalse("duplicates found in " + Arrays.toString(draw), hasDuplicates(draw));
		}
	}
	
	@Test
	public void test2() {
		int K = 5;
		String[] numbers = { null, "a", "b", null, "c", "d", "e", "f", "g", null, "0", null, "h", "i", "j", null};
		int N = 1000000;
		
		RandomDraw<String> rd = new RandomDraw<>();
		
		for (int i = 0; i < N; i++) {
			String[] draw = rd.drawFrom(numbers, K);
			assertEquals(K, draw.length);
			assertFalse("duplicates found in " + Arrays.toString(draw), RandomDraw.hasDuplicates(draw));
		}
	}
	
	@Test
	public void test3() {
		int K = 3;
		String[] numbers = { "a", "b", "c"};
		int N = 1000000;
		
		Random rg = new Random(17);
		RandomDraw<String> rd = new RandomDraw<>(rg);
		
		for (int i = 0; i < N; i++) {
			String[] draw = rd.drawFrom(numbers, K);
			assertEquals(K, draw.length);
			assertFalse("duplicates found in " + Arrays.toString(draw), RandomDraw.hasDuplicates(draw));
		}
	}

}
