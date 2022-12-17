/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.filter.edgepreserving;

import static imagingbook.common.math.Arithmetic.sqr;

import imagingbook.common.filter.generic.GenericFilterVector;
import imagingbook.common.image.PixelPack;
import imagingbook.common.math.Matrix;

/**
 * <p>
 * Vector version (applicable to ColorProcessor only) of a Kuwahara-type filter, similar to the filter described in [1].
 * It structures the filter region into five overlapping, square subregions (including a center region) of size (r+1) x
 * (r+1). See Sec. 17.1 of [2] for additional details.
 * </p>
 * <p>
 * [1] F. Tomita and S. Tsuji. Extraction of multiple regions by smoothing in selected neighborhoods. IEEE Transactions
 * on Systems, Man, and Cybernetics 7, 394–407 (1977).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2021/01/02
 */
public class KuwaharaFilterVector extends GenericFilterVector implements KuwaharaF {
	
	private final int n;			// fixed subregion size 
	private final int dm;			// = d-
	private final int dp;			// = d+
	private final float tsigma;

	// constructor using default settings
	public KuwaharaFilterVector() {
		this(new Parameters());
	}
	
	public KuwaharaFilterVector(Parameters params) {
		int r = params.radius;
		this.n = sqr(r + 1);	// size of complete filter
		this.dm = (r / 2) - r;			// d- = top/left center coordinate
		this.dp = this.dm + r;			// d+ = bottom/right center coordinate
		this.tsigma = (float)params.tsigma;
	}
	
	// ------------------------------------------------------

	private final float[] Amin = new float[3];
	private final float[] rgb = new float[3];
	private float Smin;		// min. variance

	@Override
	protected float[] doPixel(PixelPack pack, int u, int v)  {
		Smin = Float.POSITIVE_INFINITY;
		evalSubregion(pack, u, v);				// centered subregion - different to original Kuwahara!
		Smin = Smin - 3 * tsigma * n;			// tS * n because we use variance scaled by n
		evalSubregion(pack, u + dm, v + dm);
		evalSubregion(pack, u + dm, v + dp);
		evalSubregion(pack, u + dp, v + dm);
		evalSubregion(pack, u + dp, v + dp);
		Matrix.copyD(Amin, rgb);
 		return rgb;
 	} 
	
	private void evalSubregion(PixelPack source, int u, int v) {
		// evaluate the subregion centered at (u,v)
		final float[] S1 = new float[3];	// sum of pixel values
		final float[] S2 = new float[3];	// sum of squared pixel values
		for (int j = dm; j <= dp; j++) {
			for (int i = dm; i <= dp; i++) {		
				float[] cp = source.getPix(u + i, v + j);

				S1[0] = S1[0] + cp[0];
				S1[1] = S1[1] + cp[1];
				S1[2] = S1[2] + cp[2];
				
				S2[0] = S2[0] + sqr(cp[0]);
				S2[1] = S2[1] + sqr(cp[1]);
				S2[2] = S2[2] + sqr(cp[2]);
			}
		}
		// calculate the variance for this subregion:
//		S[0] = S2[0] - S1[0] * S1[0] / n;
//		S[1] = S2[1] - S1[1] * S1[1] / n;
//		S[2] = S2[2] - S1[2] * S1[2] / n;
		// total variance (scaled by n):
//		float Srgb = S[0] + S[1] + S[2];
		float Srgb = (S2[0] - sqr(S1[0]) + S2[1] - sqr(S1[1]) + S2[2] - sqr(S1[2])) / n;
		if (Srgb < Smin) { 
			Smin = Srgb;
			Amin[0] = S1[0] / n;
			Amin[1] = S1[1] / n;
			Amin[2] = S1[2] / n;
		}
	}

}
