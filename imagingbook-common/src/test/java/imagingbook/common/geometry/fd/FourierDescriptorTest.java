package imagingbook.common.geometry.fd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.math.Complex;


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

	// ---------------------------------------------------------------------------------------
	
	@Test
	public void testConstructor1() {
		FourierDescriptor fd = new FourierDescriptor(Go);
		assertEquals(2, fd.getMp());
		assertEquals(Go.length, fd.size());
		assertEquals(1.0, fd.getScale(), 0.0);
	}
	
	@Test
	public void testConstructor2() {
		FourierDescriptor fd = new FourierDescriptor(Go, Math.PI);
		assertEquals(2, fd.getMp());
		assertEquals(Go.length, fd.size());
		assertEquals(Math.PI, fd.getScale(), 0.0);
	}
	
	// ---------------------------------------------------------------------------------------
	
	@Test
	public void testScaleInvariance1() {
		FourierDescriptor fd1 = new FourierDescriptor(Go);
		FourierDescriptor fd2 = fd1.makeScaleInvariant();
		assertEquals(fd1.getMp(), fd2.getMp());
		assertEquals(fd1.size(), fd2.size());
		assertEquals(3.7094473979783538, fd2.getScale(), 1e-6);
		assertTrue(fd1.getCoefficient(0).equals(fd2.getCoefficient(0)));
		
		int mp = fd2.getMp();
		
		// check if all coefficient pairs are properly scaled
		double scale = fd2.getScale();
		for (int m = -mp; m <= +mp; m++) {
			if (m != 0) {
				assertTrue(fd1.getCoefficient(m).equals(fd2.getCoefficient(m).multiply(scale), 1e-6));
			}
		}
		
		// check if norm of coefficient pairs is 1
		double sum = 0;
		for (int m = -mp; m <= +mp; m++) {
			if (m != 0) {
				sum += fd2.getCoefficient(m).abs2();
			}
		}
		assertEquals(1.0, sum, 1e-6);
	}
	
	@Test
	public void testRotationInvariance1() {
		FourierDescriptor fd1 = new FourierDescriptor(Go);
		FourierDescriptor fd2 = fd1.makeRotationInvariant();
		
		assertEquals(fd1.getMp(), fd2.getMp());
		assertEquals(fd1.size(), fd2.size());
		assertEquals(fd1.getScale(), fd2.getScale(), 1e-6);
		
		int mp = fd2.getMp();
		
		// check if the weighted sum of rotated coefficient pairs is real:
		Complex z = new Complex(0,0);
		for (int m = 1; m <= mp; m++) {
			final double w = 1.0 / m;
			z = z.add(fd2.getCoefficient(-m).multiply(w));
			z = z.add(fd2.getCoefficient(+m).multiply(w));
		}
		assertEquals(0.0, z.arg(), 1e-6);	// z must be real
		
		// check if the magnitude of all coefficient pairs is unchanged:
		for (int m = 1; m <= mp; m++) {
			assertEquals(fd1.getCoefficient(-m).abs(), fd2.getCoefficient(-m).abs(), 1e-6);
			assertEquals(fd1.getCoefficient(+m).abs(), fd2.getCoefficient(+m).abs(), 1e-6);
		}
	}
	
//	@Test
//	public void testRotationInvariance2() {	// TODO: this is confused
//		RandomAngle rg = new RandomAngle(17);
//		
//		FourierDescriptor fd1 = new FourierDescriptor(Go);
//		FourierDescriptor fd1r = fd1.makeRotationInvariant();
//		
//		// check if rotated versions of the shape normalize to the same FD
//		for (int k = 0; k < 100; k++) {
//			double phi = rg.nextAngle();
//			FourierDescriptor fd2 = new FourierDescriptor(rotateShape(Go, phi));
//			FourierDescriptor fd2r = fd2.makeRotationInvariant();
//			assertEquals(0.0, fd1r.distanceComplex(fd2r), 1e-6);	// fd1r == fd2r ?
//		}
//	}
	
	@Test
	public void testStartPointInvariance1() {
		
	}
}

// System.out.println(z);
