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

public class SiftMatchTest {

	@Test
	public void testCompareTo() {

		SiftDescriptor sd1 = new SiftDescriptor(1.0, 2.0, 10.0, 3, 3.3, 0.0, null);
		SiftDescriptor sd2 = new SiftDescriptor(1.0, 2.0, 10.0, 3, 5.5, 0.0, null);
		
		double dist1 = 222.2, dist2 = 333.3;
		SiftMatch match1 = new SiftMatch(sd1, sd2, dist1);
		SiftMatch match2 = new SiftMatch(sd2, sd1, dist2);
		assertEquals(-1, match1.compareTo(match2));
		assertEquals( 1, match2.compareTo(match1));
		assertEquals( 0, match1.compareTo(match1));
	}
	
}
