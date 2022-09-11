/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.corners;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class SubpixelMaxInterpolatorTest {
	
	private static final float TOL = 1E-6f;
	private static final int DefaultMaxIterations = 20;	// iteration limit
	private static final double DefaulMaxDelta = 1e-6;	// smallest x/y move to continue search 
	private static final double DefaultMaxRad = 1.0;	// x/y search boundary (-xyLimit, +xyLimit)
	
	private static final float[] samples1 = {16,9,7,11,8,15,14,12,10}; // = s_0,...,s_8
	private static final float[] samples2 = {2,1,1,1,1,1,1,1,1};
	private static final float[] samples3 = {1,1,1,1,1,1,1,1,1};

	// ------------------------------------------------------------------------
	
	@Test
	public void testQuadraticTaylor1() {
		SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.QuadraticTaylor();
		float[] expected = {-0.38320211f, 0.08748906f, 16.59667587f};
		assertArrayEquals(expected, interp.getMax(samples1), TOL);
	}
	
	@Test
	public void testQuadraticLeastSquares1() {
		SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.QuadraticLeastSquares();
		float[] expected = {-0.41613588f, 0.32979476f, 15.65628719f};
		assertArrayEquals(expected, interp.getMax(samples1), TOL);
	}
	
	@Test
	public void testQuartic1() {
		SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.Quartic(DefaultMaxIterations, DefaulMaxDelta, DefaultMaxRad);
		float[] expected = {-0.40573445f, 0.11285823f, 16.62036324f};
		assertArrayEquals(expected, interp.getMax(samples1), TOL);
	}
	
	// ------------------------------------------------------------------------
	
	@Test
	public void testQuadraticTaylor2() {
		SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.QuadraticTaylor();
		float[] expected = {0.00000000f, 0.00000000f, 2.00000000f};
		assertArrayEquals(expected, interp.getMax(samples2), TOL);
	}
	
	@Test
	public void testQuadraticLeastSquares2() {
		SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.QuadraticLeastSquares();
		float[] expected = {0.00000000f, 0.00000000f, 1.55555558f};
		assertArrayEquals(expected, interp.getMax(samples2), TOL);
	}
	
	@Test
	public void testQuartic2() {
		SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.Quartic(DefaultMaxIterations, DefaulMaxDelta, DefaultMaxRad);
		float[] expected = {0.00000000f, 0.00000000f, 2.00000000f};
		assertArrayEquals(expected, interp.getMax(samples2), TOL);
	}
	
	// ------------------------------------------------------------------------
	
		@Test
		public void testQuadraticTaylor3() {
			SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.QuadraticTaylor();
			assertNull(interp.getMax(samples3));
		}
		
		@Test
		public void testQuadraticLeastSquares3() {
			SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.QuadraticLeastSquares();
			assertNull(interp.getMax(samples3));
		}
		
		@Test
		public void testQuartic3() {
			SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.Quartic(DefaultMaxIterations, DefaulMaxDelta, DefaultMaxRad);
			assertNull(interp.getMax(samples3));
		}
}
