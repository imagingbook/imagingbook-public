package imagingbook.common.color.colorspace;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GammaMappingFunctionTest {

	static double TOLD = 1e-15;
	static float TOLF = 1e-6f;

	@Test
	public void testSRGB() {
		doCheckDouble(GammaMappingFunction.sRGB);
		doCheckFloat(GammaMappingFunction.sRGB);
	}
	
	@Test
	public void testITU709() {
		doCheckDouble(GammaMappingFunction.ITU709);
		doCheckFloat(GammaMappingFunction.ITU709);
	}
	
	@Test
	public void testBookExample() {
		doCheckDouble(new GammaMappingFunction(0.5, 0.2));
		doCheckFloat(new GammaMappingFunction(0.5, 0.2));
	}
	
	// ----------------------------
	
	private void doCheckDouble(GammaMappingFunction mf) {	
		for (int i = 0; i <= 1000; i++) {
			double lc1 = (double) i / 1000;
			double nlc = mf.applyFwd(lc1);
			double lc2 = mf.applyInv(nlc);
			assertEquals(lc1, lc2, TOLD);
		}
		assertEquals(0.0, mf.applyFwd(0.0), TOLD);
		assertEquals(1.0, mf.applyFwd(1.0), TOLD);
		assertEquals(0.0, mf.applyInv(0.0), TOLD);
		assertEquals(1.0, mf.applyInv(1.0), TOLD);
	}
	
	private void doCheckFloat(GammaMappingFunction mf) {	
		for (int i = 0; i <= 1000; i++) {
			float lc1 = (float) i / 1000;
			float nlc = mf.applyFwd(lc1);
			float lc2 = mf.applyInv(nlc);
			assertEquals(lc1, lc2, TOLF);
		}
		assertEquals(0.0f, mf.applyFwd(0.0f), TOLF);
		assertEquals(1.0f, mf.applyFwd(1.0f), TOLF);
		assertEquals(0.0f, mf.applyInv(0.0f), TOLF);
		assertEquals(1.0f, mf.applyInv(1.0f), TOLF);
	}

}
