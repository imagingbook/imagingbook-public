package imagingbook.spectral.fd;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;

public class FourierDescriptorUniformTest {

	@Test
	public void test() {
		double[][] points = {{3,2}, {5,4}, {7,10}, {6,11}, {4,7}};
		Pnt2d[] V = PntUtils.fromDoubleArray(points);
		
		FourierDescriptorUniform fd1 = new FourierDescriptorUniform(V); 
		System.out.println(Arrays.toString(fd1.getCoefficients()));
		assertEquals(5, fd1.size());
		
		FourierDescriptorUniform fd2 = new FourierDescriptorUniform(V, 2); 
		System.out.println(Arrays.toString(fd2.getCoefficients()));
		assertEquals(5, fd2.size());
		
		FourierDescriptorUniform fd3 = new FourierDescriptorUniform(V, 1); 
		System.out.println(Arrays.toString(fd3.getCoefficients()));	
		assertEquals(3, fd3.size());
	}

}
