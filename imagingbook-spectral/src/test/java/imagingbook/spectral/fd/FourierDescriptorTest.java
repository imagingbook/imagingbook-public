package imagingbook.spectral.fd;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.math.Complex;
import imagingbook.spectral.TestUtils;

public class FourierDescriptorTest {
	
	static double TOL = 1e-12;
	
	static Complex[] Go = {	// DFT coefficients
			new Complex(5.000000000, 6.800000000), 
			new Complex(-1.635404556, -2.625802342), 
			new Complex(-0.215246253, -0.009311758), 
			new Complex(0.109673444, -0.154620264), 
			new Complex(-0.259022635, -2.010265635)};
	
	static Complex[] Ge = {	// DFT coefficients
			new Complex(5.000000000, 6.800000000), 
			new Complex(-1.635404556, -2.625802342), 
			new Complex(-0.215246253, -0.009311758), 
			new Complex(0.111111111, -0.111111111), 	// extra entry to make even length (ignored)
			new Complex(0.109673444, -0.154620264), 
			new Complex(-0.259022635, -2.010265635)};
	
	@Test
	public void testTruncateOddLength() {
		Complex[] G = Go;
		int M = G.length;
		{
			Complex[] Gt = FourierDescriptor.truncate(G);
			assertTrue(Gt.length % 2 == 1);
			TestUtils.assertArrayEquals(new Complex[] {G[0], G[1], G[2], G[M-2], G[M-1]}, Gt, TOL);
		}
		{
			Complex[] G2 = FourierDescriptor.truncate(G, 2);
			assertTrue(G2.length % 2 == 1);
			TestUtils.assertArrayEquals(new Complex[] {G[0], G[1], G[2], G[M-2], G[M-1]}, G2, TOL);
		}
		{
			Complex[] G1 = FourierDescriptor.truncate(G, 1);
			assertTrue(G1.length % 2 == 1);
			TestUtils.assertArrayEquals(new Complex[] {G[0], G[1], G[M-1]}, G1, TOL);
		}
		{
			Complex[] G0 = FourierDescriptor.truncate(G, 0);
			assertTrue(G0.length % 2 == 1);
			TestUtils.assertArrayEquals(new Complex[] {G[0]}, G0, TOL);
		}
	}
	
	@Test
	public void testTruncateEvenLength() {
		Complex[] G = Ge;
		int M = G.length;
		{
			Complex[] Gt = FourierDescriptor.truncate(G);
			assertTrue(Gt.length % 2 == 1);
			TestUtils.assertArrayEquals(new Complex[] {G[0], G[1], G[2], G[M-2], G[M-1]}, Gt, TOL);
		}
		{
			Complex[] G2 = FourierDescriptor.truncate(G, 2);
			assertTrue(G2.length % 2 == 1);
			TestUtils.assertArrayEquals(new Complex[] {G[0], G[1], G[2], G[M-2], G[M-1]}, G2, TOL);
		}
		{
			Complex[] G1 = FourierDescriptor.truncate(G, 1);
			assertTrue(G1.length % 2 == 1);
			TestUtils.assertArrayEquals(new Complex[] {G[0], G[1], G[M-1]}, G1, TOL);
		}
		{
			Complex[] G0 = FourierDescriptor.truncate(G, 0);
			assertTrue(G0.length % 2 == 1);
			TestUtils.assertArrayEquals(new Complex[] {G[0]}, G0, TOL);
		}
	}

	@Test (expected = IllegalArgumentException.class)
	public void testTruncateOddLengthFail() {
		Complex[] G = Go;
		FourierDescriptor.truncate(G, 3);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testTruncateEvenLengthFail() {
		Complex[] G = Ge;
		FourierDescriptor.truncate(G, 3);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testTruncateEvenLengthFailNegative() {
		Complex[] G = Ge;
		FourierDescriptor.truncate(G, -1);
	}

}
