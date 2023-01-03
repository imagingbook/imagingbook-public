/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.points;

import static imagingbook.testutils.NumericTestUtils.assert2dArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;

public class ProjectiveFit2dTest {
	

	@Test
	public void testExact() {
		
		Pnt2d[] P = {
				Pnt2d.from(2, 5),
				Pnt2d.from(4, 6),
				Pnt2d.from(7, 9),
				Pnt2d.from(5, 9)};
		
		Pnt2d[] Q = {
				Pnt2d.from(4, 3),
				Pnt2d.from(5, 2),
				Pnt2d.from(9, 3),
				Pnt2d.from(7, 5)};
		
		double[][] Aexp = {
				{-1.230769, 2.076923, -1.769231}, 
				{-2.461538, 2.615385, -3.538462}, 
				{-0.307692, 0.230769, 1.000000}};
		
		double Eexp = 0;
		
		ProjectiveFit2d fit = new ProjectiveFit2d(P, Q);
		
		double[][] A = fit.getTransformationMatrix();
//		PrintPrecision.set(6);
//		System.out.println("A = \n" + Matrix.toString(A));
//		System.out.println("E = \n" + fit.getError());
		
		assert2dArrayEquals(Aexp, A, 1e-6);
		assertEquals(Eexp, fit.getError(), 1e-6);
	}
	
	@Test
	public void testLeastSquares() {
		
		Pnt2d[] P = {
				Pnt2d.from(2, 5),
				Pnt2d.from(4, 6),
				Pnt2d.from(7, 9),
				Pnt2d.from(5, 9),
				Pnt2d.from(9, 3),
				};
		
		Pnt2d[] Q = {
				Pnt2d.from(4, 3),
				Pnt2d.from(5, 2),
				Pnt2d.from(9, 3),
				Pnt2d.from(7, 5),
				Pnt2d.from(8, 4)};
		
		double[][] Aexp = {
				{0.637210, -1.108980, 5.652385}, 
				{0.285236, -0.589606, 3.237697}, 
				{0.053139, -0.153864, 1.000000}};
		
		double Eexp = 19.698318;
		
		ProjectiveFit2d fit = new ProjectiveFit2d(P, Q);
	
		double[][] A = fit.getTransformationMatrix();
//		PrintPrecision.set(6);
//		System.out.println("A = \n" + Matrix.toString(A));
//		System.out.println("E = \n" + fit.getError());
		
		assert2dArrayEquals(Aexp, A, 1e-6);
		assertEquals(Eexp, fit.getError(), 1e-6);
	}

}
