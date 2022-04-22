/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.mappings.linear;

import org.junit.Assert;
import org.junit.Test;

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

}
