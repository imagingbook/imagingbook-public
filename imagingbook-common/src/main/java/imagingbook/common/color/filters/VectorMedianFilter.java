/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.filters;

import java.awt.Color;

import imagingbook.common.filter.GenericFilterVector;
import imagingbook.common.image.data.PixelPack;
import imagingbook.common.math.VectorNorm;
import imagingbook.common.math.VectorNorm.NormType;

/**
 * Basic vector median filter for color images implemented
 * by extending the {@link GenericFilterVector} class.
 * 
 * @author W. Burger
 * @version 2020/12/31
 */
public class VectorMedianFilter extends GenericFilterVector {
	
	public static class Parameters extends ScalarMedianFilter.Parameters {
		/** Distance norm to use */
		public NormType distanceNorm = NormType.L1;
		
		/** For testing only */
		public boolean showMask = false;
		/** For testing only */
		public boolean markModifiedPixels = false;
		/** For testing only */
		public Color modifiedColor = Color.black;
		
	}
	
	private final Parameters params;
	private final CircularMask mask;
	private final int maskCount;
	final int[][] maskArray;
	final int xc, yc;
	private final float[][] supportRegion;		// supportRegion[i][c] with index i, color component c
	private final VectorNorm vNorm;
	private final float[] pTmp = new float[3];	// check, adapt to depth
	private final float[] modColor;
	
	public int modifiedCount = 0; // for debugging only
	
	//-------------------------------------------------------------------------------------
	
	public VectorMedianFilter() {	
		this(new Parameters());
	}
	
	public VectorMedianFilter(Parameters params) {
		this.params = params;
		this.mask = new CircularMask(params.radius);
		this.maskCount = mask.getCount();
		this.maskArray = mask.getMask();
		this.xc = this.yc = mask.getCenter();
		this.supportRegion = new float[maskCount][3];
		this.vNorm = params.distanceNorm.create();
		
		this.modColor = params.modifiedColor.getRGBColorComponents(null);
		if (params.showMask) Utils.showMask(this.mask, "Mask-" + this.getClass().getSimpleName());
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	protected float[] doPixel(PixelPack pack, int u, int v) {
		final float[] pCtr = pack.getVec(u, v);
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
			if (params.markModifiedPixels) {
				copyPixel(modColor, pF);
				modifiedCount++;
			}
			else {
				copyPixel(pmin, pF);
			}
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
				if (maskArray[i][j] > 0) {
					int vj = v + j - yc;
					copyPixel(src.getVec(ui, vj, pTmp), supportRegion[k]);
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
