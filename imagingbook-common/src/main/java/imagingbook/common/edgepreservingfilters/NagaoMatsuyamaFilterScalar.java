/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.edgepreservingfilters;

import imagingbook.common.filter.GenericFilterScalar;
import imagingbook.common.image.data.PixelPack.PixelSlice;

/**
 * Scalar version.
 * This class implements a 5x5 Nagao-Matsuyama filter, as described in
 * NagaoMatsuyama (1979).
 * 
 * @author W. Burger
 * @version 2021/01/02
 */

public class NagaoMatsuyamaFilterScalar extends GenericFilterScalar implements NagaoMatsuyamaF {

	private final float varThreshold;
	private float minVariance;
	private float minMean;
	
	public NagaoMatsuyamaFilterScalar() {
		this(new Parameters());
	}
	
	public NagaoMatsuyamaFilterScalar(Parameters params) {
		this.varThreshold = (float) params.varThreshold;
	}
	
	// ------------------------------------------------------

	@Override
	protected float doPixel(PixelSlice plane, int u, int v) {
		minVariance = Float.POSITIVE_INFINITY;
		evalSubregion(plane, Constants.R0, u, v);
		minVariance = minVariance - varThreshold;
		for (int[][] Rk : Constants.SubRegions) {
			evalSubregion(plane, Rk, u, v);
		}
 		return minMean;
 	}
	
	void evalSubregion(PixelSlice source, int[][] R, int u, int v) {
		float sum1 = 0; 
		float sum2 = 0;
		int n = 0;
		for (int[] p : R) {
			float a = source.getVal(u+p[0], v+p[1]);
			sum1 = sum1 + a;
			sum2 = sum2 + a * a;
			n = n + 1;
		}
		float nr = n;
		float var = (sum2 - sum1 * sum1 / nr) / nr;	// = sigma^2
		if (var < minVariance) {
			minVariance = var;
			minMean = sum1 / nr; // mean of subregion with min. variance so far
		}
	}
	
}
