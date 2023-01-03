/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.filter.nonlinear;

import imagingbook.common.filter.generic.GenericFilterVector;
import imagingbook.common.filter.mask.CircularMask;
import imagingbook.common.image.PixelPack;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.VectorNorm;

import java.util.Arrays;

/**
 * <p>
 * Sharpening vector median filter for color images implemented by extending the {@link GenericFilterVector} class. See
 * Sec. 15.2.3 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/10
 */
public class VectorMedianFilterSharpen extends GenericFilterVector {
	
	public static class Parameters extends VectorMedianFilter.Parameters {
		/** Sharpening factor (0 = none, 1 = max.) */
		public double sharpen = 0.5;
		/** Threshold for replacing the current center pixel */
		public double threshold = 0.0;	
	}
	
	private final Parameters params;
	private final CircularMask mask;
	private final int maskCount;
	private final byte[][] maskArray;
	private final int xc, yc;
	private final float[][] supportRegion;		// supportRegion[i][c] with index i, color component c
	private final VectorNorm vNorm;
	private final int a;						// a = 2,...,n
	private final float[] vals = new float[3];	// check, adapt to depth
	
	public VectorMedianFilterSharpen() {	
		this(new Parameters());
	}
	
	public VectorMedianFilterSharpen(Parameters params) {
		this.params = params;
		this.mask = new CircularMask(params.radius);
		this.maskCount = mask.getElementCount();
		this.maskArray = mask.getByteArray();
		this.xc = mask.getCenterX();
		this.yc = mask.getCenterY();
		this.supportRegion = new float[maskCount][3];
		this.a = (int) Math.round(maskCount - params.sharpen * (maskCount - 2));
		this.vNorm = params.distanceNorm.getInstance();
	}
	
	@Override
	protected float[] doPixel(PixelPack pack, int u, int v) {
		float[] pCtr = pack.getPix(u, v);
		getSupportRegion(pack, u, v);
		double dCtr = trimmedAggregateDistance(pCtr, a); 
		double dMin = Double.MAX_VALUE;
		int jMin = -1;
		for (int j = 0; j < supportRegion.length; j++) {
			//float[] p = supportRegion[j];
			double d = trimmedAggregateDistance(supportRegion[j], a);
			if (d < dMin) {
				jMin = j;
				dMin = d;
			}
		}
		float[] pmin = supportRegion[jMin];
		// modify this pixel only if the min aggregate distance of some
		// other pixel in the filter region is smaller
		// than the aggregate distance of the original center pixel:
		float[] pF = new float[3];			// the returned color tupel
		if (dCtr - dMin > params.threshold * a) {	// modify this pixel
			Matrix.copyD(pmin, pF);
		}
		else {	// keep the original pixel value
			Matrix.copyD(pCtr, pF);
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
					Matrix.copyD(vals, supportRegion[k]);
					k = k + 1;
				}
			}
		}
	}
	
	private double trimmedAggregateDistance(float[] p, int a) {
		if (a <= 1) {
			return 0;	// aggregate distance of rank 1 is always zero
		}
		final int N = supportRegion.length;
		final double[] R = new double[N];
		for (int i = 0; i < N; i++) {
			R[i] = vNorm.distance(p, supportRegion[i]);
		}
		if (a < N) {
			Arrays.sort(R);	// only sort if the rank is less than N
		}
		double d = 0;
		for (int i = 1; i < a; i++) {
			d = d + R[i];
		}
		return d;
	}

}
