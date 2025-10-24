/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GridIndexer2DTest {
	
	private static int W = 300;
	private static int H = 200;

	@Test
	public void testDefaultValueIndexer1() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.DefaultValue;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);

		assertEquals(0, pi.getIndex(0, 0));
		assertEquals(W-1, pi.getIndex(W-1, 0));
		assertEquals(W, pi.getIndex(0, 1));
		assertEquals(W*H-1, pi.getIndex(W-1, H-1));

		assertEquals(15070, pi.getIndex(70, 50));
		assertEquals(-1, pi.getIndex(W + 10, 50));
		assertEquals(-1, pi.getIndex(-4, 50));
		assertEquals(-1, pi.getIndex(70, H));
		assertEquals(-1, pi.getIndex(70, -1000));
	}

	@Test
	public void testDefaultValueIndexer2() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.DefaultValue;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		Random rg = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int u = (int) ((rg.nextDouble() - 0.5) * 10 * W);
			int v = (int) ((rg.nextDouble() - 0.5) * 10 * H);
			int k = pi.getIndex(u, v);
			if (u < 0 || u >= W || v < 0 || v >= H)
				assertEquals(-1, k);
			else
				assertTrue(0 <= k && k < W*H);
		}
	}

	// -------------------------------------------------------------------
		
	@Test
	public void testNearestBorderIndexer1() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.NearestBorder;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);

		assertEquals(0, pi.getIndex(0, 0));
		assertEquals(W-1, pi.getIndex(W-1, 0));
		assertEquals(W, pi.getIndex(0, 1));
		assertEquals(W*H-1, pi.getIndex(W-1, H-1));

		assertEquals(15070, pi.getIndex(70, 50));
		assertEquals(15299, pi.getIndex(W + 10, 50));
		assertEquals(15000, pi.getIndex(-4, 50));
		assertEquals(59770, pi.getIndex(70, H));
		assertEquals(70, pi.getIndex(70, -1000));
	}

	@Test
	public void testNearestBorderIndexer2() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.NearestBorder;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		Random rg = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int u = (int) ((rg.nextDouble() - 0.5) * 10 * W);
			int v = (int) ((rg.nextDouble() - 0.5) * 10 * H);
			int k = pi.getIndex(u, v);
			assertTrue(0 <= k && k < W*H);
			if (u < 0) {
				assertEquals(pi.getIndex(0, v), k);
			}
			if (v < 0) {
				assertEquals(pi.getIndex(u, 0), k);
			}
			if (u >= W) {
				assertEquals(pi.getIndex(W-1, v), k);
			}
			if (v > H) {
				assertEquals(pi.getIndex(u, H-1), k);
			}
		}
	}

	// -------------------------------------------------------------------

	@Test
	public void testMirrorImageIndexer1() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.MirrorImage;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);

		assertEquals(0, pi.getIndex(0, 0));
		assertEquals(W-1, pi.getIndex(W-1, 0));
		assertEquals(W, pi.getIndex(0, 1));
		assertEquals(W*H-1, pi.getIndex(W-1, H-1));

		assertEquals(15070, pi.getIndex(70, 50));
		assertEquals(15289, pi.getIndex(W + 10, 50));
		assertEquals(15003, pi.getIndex(-4, 50));
		assertEquals(59770, pi.getIndex(70, H));
		assertEquals(59770, pi.getIndex(70, -1000));
	}

	@Test
	public void testMirrorImageIndexer2() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.MirrorImage;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);

		assertEquals(pi.getIndex(0, 0), pi.getIndex(-1, 0));
		assertEquals(pi.getIndex(W-1, 0), pi.getIndex(-W, 0));
		assertEquals(pi.getIndex(2*W-1, 0), pi.getIndex(-2*W, 0));
		assertEquals(pi.getIndex(55-1, 0), pi.getIndex(-55, 0));

		assertEquals(pi.getIndex(0, 0), pi.getIndex(0, -1));
		assertEquals(pi.getIndex(0, H-1), pi.getIndex(0, -H));
		assertEquals(pi.getIndex(0, 2*H-1), pi.getIndex(0, -2*H));
		assertEquals(pi.getIndex(0, 55-1), pi.getIndex(0, -55));
	}

	@Test
	public void testMirrorImageIndexer3() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.MirrorImage;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		Random rg = new Random(17);

		for (int i = 0; i < 10000; i++) {
			int u = (int) ((rg.nextDouble() - 0.5) * 10 * W);
			int v = (int) ((rg.nextDouble() - 0.5) * 10 * H);
			int k = pi.getIndex(-u, -v);
			assertEquals(pi.getIndex(u-1, v-1), k);
			assertTrue(0 <= k && k < W*H);
		}
	}

	// ------------------------------------------------------------
	@Test
	public void testPeriodicImageIndexer1() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.PeriodicImage;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);

		assertEquals(0, pi.getIndex(0, 0));
		assertEquals(W-1, pi.getIndex(W-1, 0));
		assertEquals(W, pi.getIndex(0, 1));
		assertEquals(W*H-1, pi.getIndex(W-1, H-1));

		assertEquals(15070, pi.getIndex(70, 50));
		assertEquals(15010, pi.getIndex(W + 10, 50));
		assertEquals(15296, pi.getIndex(-4, 50));
		assertEquals(70, pi.getIndex(70, H));
		assertEquals(70, pi.getIndex(70, -1000));
	}

	@Test
	public void testPeriodicImageIndexer2() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.PeriodicImage;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);

		assertEquals(pi.getIndex(0, 0), pi.getIndex(W, 0));
		assertEquals(pi.getIndex(0, 0), pi.getIndex(-W, 0));
		assertEquals(pi.getIndex(0, 0), pi.getIndex(2*W, 0));
		assertEquals(pi.getIndex(W+55, 0), pi.getIndex(10*W+55, 0));

		assertEquals(pi.getIndex(0, 0), pi.getIndex(0, H));
		assertEquals(pi.getIndex(0, 0), pi.getIndex(0, -H));
		assertEquals(pi.getIndex(0, 0), pi.getIndex(0, 2*H));
		assertEquals(pi.getIndex(0, H+55), pi.getIndex(0, 10*W+55));
	}

	@Test
	public void testPeriodicImageIndexer3() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.PeriodicImage;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		Random rg = new Random(17);

		for (int i = 0; i < 10000; i++) {
			int u = (int) ((rg.nextDouble() - 0.5) * 10 * W);
			int v = (int) ((rg.nextDouble() - 0.5) * 10 * H);
			int uu = Math.floorMod(u, W);
			int vv = Math.floorMod(v, H);
			int k = pi.getIndex(u, v);
			assertEquals(pi.getIndex(uu, vv), k);
			assertTrue(0 <= k && k < W*H);
		}
	}
	
	// ------------------------------------------------------------
	
	@Test
	public void testExceptionIndexer1() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.ThrowException;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);

		assertEquals(0, pi.getIndex(0, 0));
		assertEquals(W-1, pi.getIndex(W-1, 0));
		assertEquals(W, pi.getIndex(0, 1));
		assertEquals(W*H-1, pi.getIndex(W-1, H-1));

		assertEquals(15070, pi.getIndex(70, 50));
	}
	
	@Test (expected = GridIndexer2D.OutOfImageException.class)
	public void testExceptionIndexer2() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.ThrowException;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		pi.getIndex(W + 10, 50);
	}
	
	@Test (expected = GridIndexer2D.OutOfImageException.class)
	public void testException3() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.ThrowException;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		pi.getIndex(-4, 50);
	}
	
	@Test (expected = GridIndexer2D.OutOfImageException.class)
	public void testException4() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.ThrowException;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		pi.getIndex(70, H);
	}
	
	@Test (expected = GridIndexer2D.OutOfImageException.class)
	public void testException5() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.ThrowException;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		pi.getIndex(70, -1000);
	}

}
