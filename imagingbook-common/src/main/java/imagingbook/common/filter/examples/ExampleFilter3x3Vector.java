/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.filter.examples;

import imagingbook.common.filter.generic.GenericFilterVector;
import imagingbook.common.image.PixelPack;

public class ExampleFilter3x3Vector extends GenericFilterVector {

	private final static float[][] H = {
			{1, 2, 1},
			{2, 4, 2},
			{1, 2, 1}};
	
	private final static int width = 3;
	private final static int height = 3;
	private final static int xc = 1;
	private final static int yc = 1;
	private final static float s = 16;
	
	@Override
	protected float[] doPixel(PixelPack pack, int u, int v) {
		int depth = pack.getDepth();
		float[] sum = new float[depth];
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				float[] p = pack.getVec(ui, vj);
				for (int k = 0; k < depth; k++) {
					sum[k] = sum[k] + (p[k] * H[i][j]) / s;
				}
			}
		}
		return sum;
	}

}
