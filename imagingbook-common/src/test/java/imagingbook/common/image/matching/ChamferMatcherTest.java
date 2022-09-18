package imagingbook.common.image.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.common.ij.IjUtils;
import imagingbook.testimages.BinaryTestImage;

public class ChamferMatcherTest {
	
	static float TOL = 1e-6f;
	static ByteProcessor I = (ByteProcessor) BinaryTestImage.CatThinning.getImage().getProcessor();
	static int MI = I.getWidth();
	static int NI = I.getHeight();

	@Test
	public void testL1() {
		ChamferMatcher matcher = new ChamferMatcher(I, DistanceNorm.L1);

		int x0 = 23, y0 = 20;	// top/left corner of extraction
		int MR = 14, NR = 16;
		
		ByteProcessor R = IjUtils.crop(I, x0, y0, MR, NR);
		assertNotNull(R instanceof ByteProcessor);
		assertEquals(MR, R.getWidth());
		assertEquals(NR, R.getHeight());
		
		float[][] Q = matcher.getMatch(R);
		assertNotNull(Q);
		assertEquals(MI - MR + 1, Q.length);
		assertEquals(NI - NR + 1, Q[0].length);
		
//		System.out.println((MI - MR + 1));
//		System.out.println((NI - NR + 1));
//		IJ.save(new ImagePlus("result", new FloatProcessor(Q)), "D:/tmp/skeletonQ.tif");
		
		// optimal match must be at extraction point:
		assertEquals(0.0f, Q[x0][y0], TOL);
		
		// check score values at corners of Q:
		assertEquals(210, Q[0][0], TOL);
		assertEquals(437, Q[MI - MR][0], TOL);
		assertEquals(747, Q[0][NI - NR], TOL);
		assertEquals(393, Q[MI - MR][NI - NR], TOL);
	}
	
	@Test
	public void testL2() {
		ChamferMatcher matcher = new ChamferMatcher(I, DistanceNorm.L2);

		int x0 = 23, y0 = 20;	// top/left corner of extraction
		int MR = 14, NR = 16;
		
		ByteProcessor R = IjUtils.crop(I, x0, y0, MR, NR);
		assertNotNull(R instanceof ByteProcessor);
		assertEquals(MR, R.getWidth());
		assertEquals(NR, R.getHeight());
		
		float[][] Q = matcher.getMatch(R);
		assertNotNull(Q);
		assertEquals(MI - MR + 1, Q.length);
		assertEquals(NI - NR + 1, Q[0].length);
		
//		System.out.println((MI - MR + 1));
//		System.out.println((NI - NR + 1));
//		IJ.save(new ImagePlus("result", new FloatProcessor(Q)), "D:/tmp/skeletonQ.tif");
		
		// optimal match must be at extraction point:
		assertEquals(0.0f, Q[x0][y0], TOL);
		
		// check score values at corners of Q:
		assertEquals(175.26706f, Q[0][0], TOL);
		assertEquals(383.32092f, Q[MI - MR][0], TOL);
		assertEquals(664.33527f, Q[0][NI - NR], TOL);
		assertEquals(356.92395f, Q[MI - MR][NI - NR], TOL);
	}

}
