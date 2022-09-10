package imagingbook.common.filter.mask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ij.process.ByteProcessor;

public class BinaryMaskTest {

	@Test
	public void test1() {
		
		// note: this array is transposed (first coordinate is x)!
		byte[][] B = {
				{ 0, 1, 0, 0, 1 },
				{ 1, 1, 0, 1, 0 },
				{ 1, 0, 1, 1, 1 }};
		
		BinaryMask mask = new BinaryMask(B);
		
		assertEquals(3, mask.getWidth());
		assertEquals(5, mask.getHeight());
		
		assertEquals(1, mask.getCenterX());
		assertEquals(2, mask.getCenterY());
		
		assertEquals(9, mask.getElementCount());
		
		assertNotNull(mask.getByteArray());
		
		ByteProcessor bp = mask.getByteProcessor();
		assertNotNull(bp);
		assertEquals(mask.getWidth(), bp.getWidth());
		assertEquals(mask.getHeight(), bp.getHeight());
		
		for (int u = 0; u < mask.getWidth(); u++) {
			for (int v = 0; v < mask.getHeight(); v++) {
				assertEquals(255 * B[u][v], bp.get(u, v));
			}
		}
		
	}

}
