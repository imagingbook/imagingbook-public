/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.cie;

import static java.lang.Double.isFinite;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StandardIlluminantTest {

	@Test
	public void test1() {
		for (StandardIlluminant ill : StandardIlluminant.values()) {
			double[] XYZ = ill.getXYZ();
//			System.out.println(ill.toString() + ": " + Arrays.toString(XYZ));
			assertTrue("XYZ component < 0 in " + ill.toString(), XYZ[0] > 0 && XYZ[1] > 0 && XYZ[2] > 0);
			assertTrue("XYZ component infinite or NaN in " + ill.toString(), isFinite(XYZ[0]) && isFinite(XYZ[1]) && isFinite(XYZ[2]));
		}
	}
	
	@Test
	public void test2() {
		for (StandardIlluminant ill : StandardIlluminant.values()) {
			double[] xy = CieUtils.XYZToxy(ill.getXYZ());
//			System.out.println(ill.toString() + ": " + Arrays.toString(xy));
			assertTrue("xy component < 0 in " + ill.toString(), xy[0] > 0 && xy[1] > 0);
			assertTrue("xy component > 1 in " + ill.toString(), xy[0] <= 1 && xy[1] <= 1);
			assertTrue("xy component infinite or NaN in " + ill.toString(), isFinite(xy[0]) && isFinite(xy[1]));
		}
	}

}
