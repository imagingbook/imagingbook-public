package imagingbook.spectral.fd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.Complex;
import imagingbook.spectral.TestUtils;

public class FourierDescriptorFromPolygonTest {

	static double TOL = 1e-6;
	
	@Test
	public void test1() {	
		Pnt2d[] V = PntUtils.makeDoublePoints(3,2, 5,4, 7,10, 6,11, 4,7);
		
		Complex[] c1 = {	// DFT coefficients
				new Complex(4.899143645, 6.495130576), 
				new Complex(-0.932154980, -2.315184973), 
				new Complex(0.001385310, 0.033226034), 
				new Complex(-0.030610108, -0.033017669), 
				new Complex(-0.470565980, -1.415652554)};
		
		FourierDescriptor fd1 = new FourierDescriptorFromPolygon(V, 2);
//		System.out.println(Arrays.toString(fd1.getCoefficients()));
		assertEquals(5, fd1.size());
		TestUtils.assertArrayEquals(c1, fd1.getCoefficients(), TOL);
		
		FourierDescriptor fd2 = new FourierDescriptorFromPolygon(V, 2);
//		System.out.println(Arrays.toString(fd2.getCoefficients()));
		assertEquals(5, fd2.size());
		Complex[] c2 = c1;
		TestUtils.assertArrayEquals(c2, fd2.getCoefficients(), TOL);
		
		FourierDescriptor fd3 = new FourierDescriptorFromPolygon(V, 1); 
//		System.out.println(Arrays.toString(fd3.getCoefficients()));
		assertEquals(3, fd3.size());
		Complex[] c3 = {c1[0], c1[1], c1[4]};
		TestUtils.assertArrayEquals(c3, fd3.getCoefficients(), TOL);
	}
}
