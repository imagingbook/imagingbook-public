/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.corners;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class CornerTest {

	@Test
	public void testSorting() {
		Corner c1 = new Corner(1,0,1);
		Corner c2 = new Corner(2,0,2);
		Corner c3 = new Corner(3,0,3);
		Corner c4 = new Corner(4,0,2);
		
		Corner[] corners = {c1, c2, c3, c4};
//		System.out.println("corners orig =   " + Arrays.toString(corners));
		
		Arrays.sort(corners);
//		System.out.println("corners sorted = " + Arrays.toString(corners));
		
		for (int i = 1; i < corners.length; i++) {
			assertTrue(corners[i-1].getQ() >= corners[i].getQ());
		}
	}

}
