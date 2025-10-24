/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fd;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.geometry.mappings.linear.Rotation2D;
import imagingbook.common.geometry.mappings.linear.Scaling2D;
import imagingbook.common.math.Complex;
import imagingbook.common.util.random.RandomAngle;

public class FourierDescriptorUniformTest {

	static double TOL = 1e-6;
	
	@Test
	public void testConstructors() {	
		Pnt2d[] V = PntUtils.makeDoublePoints(3,2, 5,4, 7,10, 6,11, 4,7);
		
		Complex[] G = {	// DFT spectrum for V1
				new Complex(5.000000000, 6.800000000), 
				new Complex(-1.635404556, -2.625802342), 
				new Complex(-0.215246253, -0.009311758), 
				new Complex(0.109673444, -0.154620264), 
				new Complex(-0.259022635, -2.010265635)};
		
		FourierDescriptor fd = FourierDescriptorUniform.from(V); 
//		System.out.println(Arrays.toString(fd1.getCoefficients()));
		assertEquals(5, fd.size());
		TestUtils.assertArrayEquals(G, fd.getCoefficients(), TOL);
		
		FourierDescriptor fdB = FourierDescriptorUniform.from(V, 2); 
//		System.out.println(Arrays.toString(fd2.getCoefficients()));
		assertEquals(5, fdB.size());
		Complex[] GB = G;
		TestUtils.assertArrayEquals(GB, fdB.getCoefficients(), TOL);
		
		FourierDescriptor fdC = FourierDescriptorUniform.from(V, 1); 
//		System.out.println(Arrays.toString(fd3.getCoefficients()));
		assertEquals(3, fdC.size());
		Complex[] GC = {G[0], G[1], G[4]};
		TestUtils.assertArrayEquals(GC, fdC.getCoefficients(), TOL);
	}
	
	@Test	// check scale invariance of FDs of scaled point sets
	public void testScaleInvariance() {	
		Pnt2d[] V = PntUtils.makeDoublePoints(3,2, 5,4, 7,10, 6,11, 4,7);
		
		FourierDescriptor fd = FourierDescriptorUniform.from(V);
		FourierDescriptor fdi = fd.makeScaleInvariant();	// reference FD
		
		Random rg = new Random(17);
		for (int i = 0; i < 100; i++) {
			double scale = 0.1 + 30 * rg.nextDouble();
			AffineMapping2D map = new Scaling2D(scale);
			FourierDescriptor fd2 = FourierDescriptorUniform.from(map.applyTo(V));
			FourierDescriptor fd2i = fd2.makeScaleInvariant();
			assertEquals(0.0, fdi.distanceComplex(fd2i), 1e-6);	// fdi == fd2i ?
		}
	}
	
	@Test	// check rotation invariance of FDs of rotated point sets
	public void testRotationInvariance() {	
		Pnt2d[] V = PntUtils.makeDoublePoints(3,2, 5,4, 7,10, 6,11, 4,7);
		
		FourierDescriptor fd = FourierDescriptorUniform.from(V);
		FourierDescriptor fdi = fd.makeRotationInvariant();	// reference FD
		
		RandomAngle rg = new RandomAngle(17);
		for (int i = 0; i < 100; i++) {
			double phi = rg.nextAngle();	
			AffineMapping2D map = new Rotation2D(phi);
			FourierDescriptor fd2 = FourierDescriptorUniform.from(map.applyTo(V));
			FourierDescriptor fd2i = fd2.makeRotationInvariant();
			assertEquals(0.0, fdi.distanceComplex(fd2i), 1e-6);	// fdi == fd2i ?
		}		
	}
	
}
