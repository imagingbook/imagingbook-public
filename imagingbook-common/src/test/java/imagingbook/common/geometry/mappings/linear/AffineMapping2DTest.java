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

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Matrix;

public class AffineMapping2DTest {
	
	static double[][] a = 
		{{-2, 4, -3}, 
		{ 3, 7,  2}, 
		{ 0, 0,  1}};
	
	static double[][] b = 
		{{0, 0, 0}, 
		{0, 0, 0}, 
		{0.5, 0, 1}};

	@Test
	public void testIsAffine() {
		LinearMapping2D A = new LinearMapping2D(a);
		Assert.assertTrue(AffineMapping2D.isAffine(A));
		
		LinearMapping2D Ai = new LinearMapping2D(Matrix.inverse(a));
		Assert.assertTrue(AffineMapping2D.isAffine(Ai));
		
		LinearMapping2D AAi = A.concat(Ai);
		Assert.assertTrue(AffineMapping2D.isAffine(AAi));
		
		LinearMapping2D B = new LinearMapping2D(b);
		Assert.assertFalse(AffineMapping2D.isAffine(B));
	}

	@Test
	public void testfromPoints1() {	// exact mapping from 3 point pairs
		Pnt2d[] P = {
			Pnt2d.from(3, 2),
			Pnt2d.from(1, 7),
			Pnt2d.from(5, 9)
			};
		Pnt2d[] Q = {
			Pnt2d.from(7, 3),
			Pnt2d.from(8, 6),
			Pnt2d.from(11, 7)
			};
		
		// check A(P_i) = Q_i
		AffineMapping2D A = AffineMapping2D.fromPoints(P, Q);
		for (int i = 0; i < P.length; i++) {
			Assert.assertEquals(Q[i], A.applyTo(P[i]));
		}
		
		// check Ai(Q_i) = P_i
		AffineMapping2D Ai = A.getInverse();
		for (int i = 0; i < P.length; i++) {
			Assert.assertEquals(P[i], Ai.applyTo(Q[i]));
		}	
	}
	
	@Test
	public void testfromPoints2() {	// check exact but overdetermined (min. least-squares) fits
		Pnt2d[] P0 = {
			Pnt2d.from(3, 2),
			Pnt2d.from(1, 7),
			Pnt2d.from(5, 9)
			};
		Pnt2d[] Q0 = {
			Pnt2d.from(7, 3),
			Pnt2d.from(8, 6),
			Pnt2d.from(11, 7)
			};
		
		AffineMapping2D A0 = AffineMapping2D.fromPoints(P0, Q0);
		
		// create random point sets P, Q
		int n = 15;
		Random rg = new Random(17);
		Pnt2d[] P = new Pnt2d[n];
		Pnt2d[] Q = new Pnt2d[n];
		
		for (int k = 0; k < 100; k++) {	// 100 tries
			for (int i = 0; i < n; i++) {
				P[i] = Pnt2d.from(100 * rg.nextDouble(), 100 * rg.nextDouble());
				Q[i] = A0.applyTo(P[i]);
			}
			
			AffineMapping2D A = AffineMapping2D.fromPoints(P, Q);
//			System.out.println("A = " + A.toString());
			for (int i = 0; i < P.length; i++) {
				Assert.assertEquals(Q[i], A.applyTo(P[i]));
			}
		}
	}
}
