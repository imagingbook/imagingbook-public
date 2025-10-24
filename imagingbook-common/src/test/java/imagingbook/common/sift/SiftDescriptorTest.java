/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.sift;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SiftDescriptorTest {

	@Test
	public void testCompareTo() {
		// SiftDescriptor(double x, double y, double scale, int scaleLevel, double magnitude, double orientation, int[] features)
		double mag1 = 3.3333, mag2 = 5.5555;
		SiftDescriptor sd1 = new SiftDescriptor(1.0, 2.0, 10.0, 3, mag1, 0.0, null);
		SiftDescriptor sd2 = new SiftDescriptor(1.0, 2.0, 10.0, 3, mag2, 0.0, null);
		assertEquals( 1, sd1.compareTo(sd2));
		assertEquals(-1, sd2.compareTo(sd1));
		assertEquals( 0, sd1.compareTo(sd1));
	}


}
