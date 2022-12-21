/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.filter.edgepreserving;

import imagingbook.common.filter.generic.GenericFilterScalar;
import imagingbook.common.image.PixelPack.PixelSlice;

/**
 * <p>
 * Scalar version of a Kuwahara-type filter, similar to the filter described in [1]. It structures the filter region
 * into five overlapping, square subregions (including a center region) of size (r+1) x (r+1). See Sec. 17.1 of [2] for
 * additional details.
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
public class KuwaharaFilterScalar extends GenericFilterScalar implements KuwaharaF {
	
	private final int n;			// fixed subregion size 
	private final int dm;			// = d-
	private final int dp;			// = d+
	private final float tsigma;

	// constructor using default settings
	public KuwaharaFilterScalar() {
		this(new Parameters());
	}
	
	public KuwaharaFilterScalar(Parameters params) {
		int r = params.radius;
		this.n = (r + 1) * (r + 1);		// size of complete filter
		this.dm = (r / 2) - r;			// d- = top/left center coordinate
		this.dp = this.dm + r;			// d+ = bottom/right center coordinate
		this.tsigma = (float)params.tsigma;
	}
	
	// ------------------------------------------------------

	private float Smin;		// min. variance
	private float Amin;			
	
	@Override
	protected float doPixel(PixelSlice plane, int u, int v) {
		Smin = Float.MAX_VALUE;
		evalSubregionGray(plane, u, v);		// a centered subregion (not in original Kuwahara)
		Smin = Smin - tsigma * n;				// tS * n because we use variance scaled by n
		evalSubregionGray(plane, u + dm, v + dm);
		evalSubregionGray(plane, u + dm, v + dp);
		evalSubregionGray(plane, u + dp, v + dm);
		evalSubregionGray(plane, u + dp, v + dp);
		
 		return Amin;
 	} 
	
	// sets the member variables Smin, Amin
	private void evalSubregionGray(PixelSlice source, int u, int v) {
		float S1 = 0; 
		float S2 = 0;
		for (int j = dm; j <= dp; j++) {
			for (int i = dm; i <= dp; i++) {
				float a = source.getVal(u+i, v+j);
				S1 = S1 + a;
				S2 = S2 + a * a;
			}
		}
//		double s = (sum2 - sum1*sum1/n)/n;	// actual sigma^2
		float s = S2 - S1 * S1 / n;			// s = n * sigma^2
		if (s < Smin) {
			Smin = s;
			Amin = S1 / n; // mean
		}
	}
}
