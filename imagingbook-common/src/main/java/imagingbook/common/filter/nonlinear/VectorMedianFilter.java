/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.filter.nonlinear;

import imagingbook.common.filter.generic.GenericFilterVector;
import imagingbook.common.filter.mask.CircularMask;
import imagingbook.common.image.PixelPack;
import imagingbook.common.math.VectorNorm;
import imagingbook.common.math.VectorNorm.NormType;

/**
 * Basic vector median filter for color images implemented
 * by extending the {@link GenericFilterVector} class.
 * 
 * @author WB
 * @version 2020/12/31
 * @version 2022/09/10 removed debugging code
 */
public class VectorMedianFilter extends GenericFilterVector {
	
	public static class Parameters extends ScalarMedianFilter.Parameters {
		/** Distance norm to use */
		public NormType distanceNorm = NormType.L1;	
	}
	
	private final CircularMask mask;
	private final int maskCount;
	private final byte[][] maskArray;
	private final int xc, yc;
	private final float[][] supportRegion;		// supportRegion[i][c] with index i, color component c
	private final VectorNorm vNorm;
	private final float[] vals = new float[3];	// check, adapt to depth
	
	//-------------------------------------------------------------------------------------
	
	public VectorMedianFilter() {	
		this(new Parameters());
	}
	
	public VectorMedianFilter(Parameters params) {
		this.mask = new CircularMask(params.radius);
		this.maskCount = mask.getElementCount();
		this.maskArray = mask.getByteArray();
		this.xc = mask.getCenterX();
		this.yc = mask.getCenterY();
		this.supportRegion = new float[maskCount][3];
		this.vNorm = params.distanceNorm.getInstance();
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	protected float[] doPixel(PixelPack pack, int u, int v) {
		final float[] pCtr = pack.getPix(u, v);
		getSupportRegion(pack, u, v);
		double dCtr = aggregateDistance(pCtr); 
		double dMin = Double.MAX_VALUE;
		int jMin = -1;
		for (int j = 0; j < supportRegion.length; j++) {
			float[] p = supportRegion[j];
			double d = aggregateDistance(p);
			if (d < dMin) {
				jMin = j;
				dMin = d;
			}
		}
		float[] pmin = supportRegion[jMin];
		// modify this pixel only if the min aggregate distance of some
		// other pixel in the filter region is smaller
		// than the aggregate distance of the original center pixel:
		final float[] pF = new float[3];	// the returned color tupel
		if (dMin < dCtr) {	// modify this pixel
			copyPixel(pmin, pF);
		}
		else {	// keep the original pixel value
			copyPixel(pCtr, pF);
		}
		return pF;
	}
	
	private void getSupportRegion(PixelPack src, int u, int v) {
		// fill 'supportRegion' for current mask position
		int k = 0;
		for (int i = 0; i < maskArray.length; i++) {
			int ui = u + i - xc;
			for (int j = 0; j < maskArray[0].length; j++) {
				if (maskArray[i][j] != 0) {
					int vj = v + j - yc;
					src.getPix(ui, vj, vals);
					copyPixel(vals, supportRegion[k]);
					k = k + 1;
				}
			}
		}
	}
	
	// find the color with the smallest summed distance to all others.
	// brute force and thus slow
	private double aggregateDistance(float[] p) {
		double d = 0;
		for (int i = 0; i < supportRegion.length; i++) {
			d = d + vNorm.distance(p, supportRegion[i]);
		}
		return d;
	}
}
