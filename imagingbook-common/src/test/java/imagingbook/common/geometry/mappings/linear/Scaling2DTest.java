/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.mappings.linear;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;

public class Scaling2DTest {

	@Test
	public void test1() {
		double sx = 3.14;
		double sy = -2.97;
		Scaling2D S = new Scaling2D(sx, sy);
		Scaling2D Si1 = new Scaling2D(1/sx, 1/sy);
		Scaling2D Si2 = S.getInverse();
		
//		System.out.println("S  = \n" + S.toString());
//		System.out.println("Si1 = \n" + Si1.toString());
//		System.out.println("Si2 = \n" + Si2.toString());
		
		int n = 15;
		Random rg = new Random(17);
		Pnt2d[] P = new Pnt2d[n];
		Pnt2d[] Q = new Pnt2d[n];
		
		for (int k = 0; k < 100; k++) {	// 100 tries
			for (int i = 0; i < n; i++) {
				P[i] = Pnt2d.from(100 * rg.nextDouble(), 100 * rg.nextDouble());
				Q[i] = S.applyTo(P[i]);
			}
			
			for (int i = 0; i < P.length; i++) {
				Assert.assertEquals(P[i], Si1.applyTo(Q[i]));
				Assert.assertEquals(P[i], Si2.applyTo(Q[i]));
			}
		}
	}

}
