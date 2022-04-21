package imagingbook.common.ij;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.testutils.ImageTests;

public class IjUtilsTest {

	private static int W = 97;
	private static int H = 51;
	
	@Test
	public void testByteArrayReadWrite() {
		ByteProcessor bp1 = makeRandomByteProcessor(W, H);
		byte[][] A = IjUtils.toByteArray(bp1);
		
		ByteProcessor bp2 = IjUtils.toByteProcessor(A);
		assertTrue(ImageTests.match(bp1, bp2));
	}
	
	@Test
	public void testIntArrayReadWrite() {
		ByteProcessor bp1 = makeRandomByteProcessor(W, H);
		int[][] A = bp1.getIntArray();
		
		ByteProcessor bp2 = IjUtils.toByteProcessor(A);
		assertTrue(ImageTests.match(bp1, bp2));
	}
	
	@Test
	public void testFloatArrayReadWrite() {
		FloatProcessor fp1 = makeRandomFloatProcessor(W, H);
		float[][] A = IjUtils.toFloatArray(fp1);
		
		FloatProcessor fp2 = IjUtils.toFloatProcessor(A);
		assertTrue(ImageTests.match(fp1, fp2, 1e-6));
	}
	
	// ----------------------------------------------------------------
		
	private ByteProcessor makeRandomByteProcessor(int w, int h) {
		Random rg = new Random(17);
		ByteProcessor bp = new ByteProcessor(w, h);
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				bp.set(u, v, rg.nextInt(256));
			}
		}
		return bp;
	}
	
	private FloatProcessor makeRandomFloatProcessor(int w, int h) {
		Random rg = new Random(17);
		FloatProcessor fp = new FloatProcessor(w, h);
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				fp.setf(u, v, rg.nextFloat());
			}
		}
		return fp;
	}

}
