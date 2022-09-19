/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.mappings.linear;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.math.Matrix;
import imagingbook.common.testutils.NumericTestUtils;

public class Rotation2DTest {

	@Test
	public void test1() {	// test inverse of rotation
		Rotation2D R = new Rotation2D(0.5);
		double[][] A = R.getTransformationMatrix();		

		Rotation2D Ri = R.getInverse();
		double[][] Ai = Ri.getTransformationMatrix();
		
		double[][] AAi = Matrix.multiply(A, Ai);
		
		// A * Ai = I
		NumericTestUtils.assertArrayEquals(Matrix.idMatrix(3), AAi); 	
	}
	
	@Test
	public void test2() {	// test forward/backward rotation by same angle
		Random rg = new Random(17);
		for (int i = 0; i < 1000; i++) {
			double angle = 2000 * rg.nextDouble() - 1000; 	// degrees
			Rotation2D R1 = new Rotation2D(Math.toRadians( angle));
			Rotation2D R2 = new Rotation2D(Math.toRadians(-angle));
	
			Rotation2D R12 = R1.concat(R2);
			double[][] A12 = R12.getTransformationMatrix();
			NumericTestUtils.assertArrayEquals(Matrix.idMatrix(3), A12);
		}
	}
	
	@Test
	public void test3() {	// test two-step rotation and concatentation
		double angle1 = 25; 	// degrees
		double angle2 = 87; 	// degrees
		Rotation2D R1 = new Rotation2D(Math.toRadians(angle1));
		Rotation2D R2 = new Rotation2D(Math.toRadians(angle2));
		Rotation2D R3 = new Rotation2D(Math.toRadians(angle1 + angle2));
		
		Rotation2D R12 = R1.concat(R2);
		
		double[][] A3 = R3.getTransformationMatrix();		
		double[][] A12 = R12.getTransformationMatrix();
		NumericTestUtils.assertArrayEquals(A3, A12);
	}
	
}
