package imagingbook.spectral.fd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.Complex;
import imagingbook.spectral.TestUtils;

public class FourierDescriptorUniformTest {

	static double TOL = 1e-6;
	
	@Test
	public void test1() {	
		Pnt2d[] V = PntUtils.makeDoublePoints(3,2, 5,4, 7,10, 6,11, 4,7);
		
		Complex[] c1 = {	// DFT coefficients
				new Complex(5.000000000, 6.800000000), 
				new Complex(-1.635404556, -2.625802342), 
				new Complex(-0.215246253, -0.009311758), 
				new Complex(0.109673444, -0.154620264), 
				new Complex(-0.259022635, -2.010265635)};
		
		FourierDescriptor fd1 = FourierDescriptorUniform.from(V); 
//		System.out.println(Arrays.toString(fd1.getCoefficients()));
		assertEquals(5, fd1.size());
		TestUtils.assertArrayEquals(c1, fd1.getCoefficients(), TOL);
		
		FourierDescriptor fd2 = FourierDescriptorUniform.from(V, 2); 
//		System.out.println(Arrays.toString(fd2.getCoefficients()));
		assertEquals(5, fd2.size());
		Complex[] c2 = c1;
		TestUtils.assertArrayEquals(c2, fd2.getCoefficients(), TOL);
		
		FourierDescriptor fd3 = FourierDescriptorUniform.from(V, 1); 
//		System.out.println(Arrays.toString(fd3.getCoefficients()));
		assertEquals(3, fd3.size());
		Complex[] c3 = {c1[0], c1[1], c1[4]};
		TestUtils.assertArrayEquals(c3, fd3.getCoefficients(), TOL);
	}
	
}
