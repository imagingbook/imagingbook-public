/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.image;

import static imagingbook.common.color.colorspace.StandardIlluminant.D50;
import static imagingbook.common.color.colorspace.StandardIlluminant.D65;
import static org.junit.Assert.assertArrayEquals;

import java.awt.color.ColorSpace;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.colorspace.ChromaticAdaptation;
import imagingbook.common.color.colorspace.XYZscalingAdaptation;
import imagingbook.common.color.colorspace.sRgb65ColorSpace;

public class XYZscalingAdaptationTest {

	@Test
	public void test() {
		ChromaticAdaptation adapt65_50 = new XYZscalingAdaptation(D65, D50);	// adapts from D65 -> D50
		ChromaticAdaptation adapt50_65 = new XYZscalingAdaptation(D50, D65);	// adapts from D50 -> D65
		
		ColorSpace cs = new sRgb65ColorSpace();
		Random rg = new Random(17);
		float[] rgb = new float[3];
		
		for (int i = 0; i < 1000; i++) {
			rgb[0] = rg.nextFloat();
			rgb[1] = rg.nextFloat();
			rgb[2] = rg.nextFloat();
			
			float[] XYZ65a = cs.toCIEXYZ(rgb);
			float[] XYZ50 =  adapt65_50.applyTo(XYZ65a);
			float[] XYZ65b = adapt50_65.applyTo(XYZ50);
			
			assertArrayEquals(XYZ65a, XYZ65b, 0.00001f);
		}
	}

}
