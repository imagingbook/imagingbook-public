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

public class AffineFit2dTest {
	
	@Test
	public void testExact() {
		
		Pnt2d[] P = {
				Pnt2d.from(2, 5),
				Pnt2d.from(4, 6),
				Pnt2d.from(7, 9)};
		
		Pnt2d[] Q = {
				Pnt2d.from(4, 3),
				Pnt2d.from(5, 2),
				Pnt2d.from(9, 3)};
		
		double[][] Aexp = {
				{-0.333333, 1.666667, -3.666667}, 
				{-1.333333, 1.666667, -2.666667}};
		
		double Eexp = 0;
		
		AffineFit2d fit = new AffineFit2d(P, Q);
		
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
				{0.723775, 0.351116, 0.444473}, 
				{0.157220, 0.102847, 1.892793}};
		
		double Eexp = 2.1897661200287466;
		
		AffineFit2d fit = new AffineFit2d(P, Q);
	
		double[][] A = fit.getTransformationMatrix();
//		PrintPrecision.set(6);
//		System.out.println("A = \n" + Matrix.toString(A));
//		System.out.println("E = \n" + fit.getError());
		
		assert2dArrayEquals(Aexp, A, 1e-6);
		assertEquals(Eexp, fit.getError(), 1e-6);
	}

}
