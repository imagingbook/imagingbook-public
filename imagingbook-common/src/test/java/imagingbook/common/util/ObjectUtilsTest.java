/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

import org.junit.Test;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.testutils.NumericTestUtils;

public class ObjectUtilsTest {

	@Test
	public void testStringEncoding() {
		PrintPrecision.set(16);
		double[][] M1 =
			{{3.240479, -1.537150, -0.498535},
			 {-0.969256, 1.875992, 0.041556},
			 {0.055648, -0.204043, 1.057311}};
		
		String encoded = ObjectUtils.encodeToString(M1);
		double[][] M2 = (double[][]) ObjectUtils.decodeFromString(encoded);	
		NumericTestUtils.assert2dArrayEquals(M1, M2, 0.0);
		
		double[][] M1minus2 = Matrix.add(M1, Matrix.multiply(-1, M2));
		NumericTestUtils.assert2dArrayEquals(new double[3][3], M1minus2, 0.0);
	}

}
