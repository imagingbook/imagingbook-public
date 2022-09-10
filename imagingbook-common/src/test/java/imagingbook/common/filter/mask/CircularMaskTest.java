package imagingbook.common.filter.mask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ij.process.ByteProcessor;

public class CircularMaskTest {

	@Test
	public void test1() {
		BinaryMask mask = new CircularMask(4.5);
		
		assertEquals(11, mask.getWidth());
		assertEquals(11, mask.getHeight());
		
		assertEquals(5, mask.getCenterX());
		assertEquals(5, mask.getCenterY());
		
		assertEquals(69, mask.getElementCount());

		byte[][] B = mask.getByteArray();
		assertNotNull(B);
		
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
