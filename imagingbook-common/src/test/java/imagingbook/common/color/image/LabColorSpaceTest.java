/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.image;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.colorspace.LabColorSpace;

public class LabColorSpaceTest {

	@Test
	public void testRgbLab() {
		Random rg = new Random(17);
		LabColorSpace cs = LabColorSpace.getInstance();
		for (int i = 0; i < 1000; i++) {
		   	int sr = rg.nextInt(256);
	    	int sg = rg.nextInt(256);
	    	int sb = rg.nextInt(256);   	
	    	float[] RGB1 = {sr/255f, sg/255f, sb/255f};
	    	float[] LAB = cs.fromRGB(new float[] {sr/255f, sg/255f, sb/255f});
	    	float[] RGB2 = cs.toRGB(LAB);
	    	assertArrayEquals(RGB1, RGB2, 0.0001f);
		}
	}

}
