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


public class FourierDescriptorTrigonometricTest {

	static double TOL = 1e-6;
	
	@Test
	public void testConstructors() {	
		Pnt2d[] V = PntUtils.makeDoublePoints(3,2, 5,4, 7,10, 6,11, 4,7);
		
		Complex[] c1 = {	// DFT coefficients
				new Complex(4.899143645, 6.495130576), 
				new Complex(-0.932154980, -2.315184973), 
				new Complex(0.001385310, 0.033226034), 
				new Complex(-0.030610108, -0.033017669), 
				new Complex(-0.470565980, -1.415652554)};
		
		FourierDescriptor fd1 = FourierDescriptorTrigonometric.from(V, 2);
//		System.out.println(Arrays.toString(fd1.getCoefficients()));
		assertEquals(5, fd1.size());
		TestUtils.assertArrayEquals(c1, fd1.getCoefficients(), TOL);
		
		FourierDescriptor fd2 = FourierDescriptorTrigonometric.from(V, 2);
//		System.out.println(Arrays.toString(fd2.getCoefficients()));
		assertEquals(5, fd2.size());
		Complex[] c2 = c1;
		TestUtils.assertArrayEquals(c2, fd2.getCoefficients(), TOL);
		
		FourierDescriptor fd3 = FourierDescriptorTrigonometric.from(V, 1); 
//		System.out.println(Arrays.toString(fd3.getCoefficients()));
		assertEquals(3, fd3.size());
		Complex[] c3 = {c1[0], c1[1], c1[4]};
		TestUtils.assertArrayEquals(c3, fd3.getCoefficients(), TOL);
	}
	
	@Test	// check scale invariance of FDs of scaled point sets
	public void testScaleInvariance() {
		int mp = 2;
		Pnt2d[] V = PntUtils.makeDoublePoints(3,2, 5,4, 7,10, 6,11, 4,7);
		
		FourierDescriptor fd = FourierDescriptorTrigonometric.from(V, mp);
		FourierDescriptor fdi = fd.makeScaleInvariant();	// reference FD
		
		Random rg = new Random(17);
		for (int i = 0; i < 100; i++) {
			double scale = 0.1 + 30 * rg.nextDouble();
			AffineMapping2D map = new Scaling2D(scale);
			FourierDescriptor fd2 = FourierDescriptorTrigonometric.from(map.applyTo(V), mp);
			FourierDescriptor fd2i = fd2.makeScaleInvariant();
			assertEquals(0.0, fdi.distanceComplex(fd2i), 1e-6);	// fdi == fd2i ?
		}
	}
	
	@Test	// check rotation invariance of FDs of rotated point sets
	public void testRotationInvariance() {
		int mp = 2;
		Pnt2d[] V = PntUtils.makeDoublePoints(3,2, 5,4, 7,10, 6,11, 4,7);
		
		FourierDescriptor fd = FourierDescriptorTrigonometric.from(V, mp);
		FourierDescriptor fdi = fd.makeRotationInvariant();	// reference FD
		
		RandomAngle rg = new RandomAngle(17);
		for (int i = 0; i < 100; i++) {
			double phi = rg.nextAngle();	
			AffineMapping2D map = new Rotation2D(phi);
			FourierDescriptor fd2 = FourierDescriptorTrigonometric.from(map.applyTo(V), mp);
			FourierDescriptor fd2i = fd2.makeRotationInvariant();
			assertEquals(0.0, fdi.distanceComplex(fd2i), 1e-6);	// fdi == fd2i ?
		}		
	}
}
