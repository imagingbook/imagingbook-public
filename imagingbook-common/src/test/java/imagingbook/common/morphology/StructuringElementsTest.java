package imagingbook.common.morphology;

import static imagingbook.common.math.Arithmetic.sqr;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import imagingbook.common.testutils.NumericTestUtils;


public class StructuringElementsTest {

	@Test
	public void testMakeBoxKernel3x3() {
		byte[][] H = StructuringElements.makeBoxKernel3x3();
		assertEquals(H.length, 3);
		assertEquals(H[0].length, 3);
		assertEquals(9, countElements(H));
	}
	
	@Test
	public void testMakeBoxKernel() {
		for (int k = 0; k < 7; k++) {
			byte[][] H = StructuringElements.makeBoxKernel(k);
			assertEquals(H.length, 2 * k + 1);
			assertEquals(H[0].length, 2 * k + 1);
			assertEquals(sqr(2 * k + 1), countElements(H));
		}
	}
	
	@Test
	public void testMakeDiskKernel1() {
		for (int k = 0; k < 7; k++) {
			byte[][] H = StructuringElements.makeDiskKernel(k);
			assertEquals(H.length, 2 * k + 1);
			assertEquals(H[0].length, 2 * k + 1);
		}
	}
	
	@Test
	public void testMakeDiskKernel2() {
		byte[][] H;
		
		H = StructuringElements.makeDiskKernel(0);
		assertEquals(1, countElements(H));
		
		H = StructuringElements.makeDiskKernel(1);
		assertEquals(5, countElements(H));
		
		H = StructuringElements.makeDiskKernel(2);
		assertEquals(13, countElements(H));
		
		H = StructuringElements.makeDiskKernel(3);
		assertEquals(29, countElements(H));
		
		H = StructuringElements.makeDiskKernel(4);
		assertEquals(49, countElements(H));
		
		H = StructuringElements.makeDiskKernel(5);
		assertEquals(81, countElements(H));
		
		H = StructuringElements.makeDiskKernel(5.5);
		assertEquals(97, countElements(H));
	}
	
	@Test
	public void testToByteArray() {
		
		byte[][] Hb = {
				{0, 1, 0, 0, 1 },
				{1, 0, 1, 1, 0 },
				{0, 0, 1, 0, 1 }};
		
		int[][] Hi = {
				{0, 1, 0, 0, 1 },
				{1, 0, 1, 1, 0 },
				{0, 0, 1, 0, 1 }};

		StructuringElements.toByteArray(Hi);
		NumericTestUtils.assertArrayEquals(Hb, StructuringElements.toByteArray(Hi));
	}
	
	@Test
	public void testReflect() {
		byte[][] H = {
				{0, 1, 0, 0, 1, 1 },
				{1, 0, 1, 1, 0, 0 },
				{0, 0, 1, 0, 1, 0 }};
		
		byte[][] Hr = StructuringElements.reflect(H);
		assertEquals(H.length, Hr.length);
		assertEquals(H[0].length, Hr[0].length);
		NumericTestUtils.assertArrayEquals(H, StructuringElements.reflect(Hr));
	}
	
	// ------------------------------------------------------------
	
	private int countElements(byte[][] maskArray) {
		int cnt = 0;
		for (int i = 0; i < maskArray.length; i++) {
			for (int j = 0; j < maskArray[i].length; j++) {
				if (maskArray[i][j] != 0)
					cnt = cnt + 1;
			}
		}
		return cnt;
	}

//	private void print(byte[][] maskArray) {
//		for (int i = 0; i < maskArray.length; i++) {
//			System.out.println(Arrays.toString(maskArray[i]));
//		}
//	}
	

}
