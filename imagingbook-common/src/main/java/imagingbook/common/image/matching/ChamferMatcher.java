/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.image.matching;
import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Instances of this class perform "chamfer" matching on binary images. The "search" image I (to be searched for matches
 * of the "reference" image R) is linked to this {@link ChamferMatcher} instance. The associated distance transformation
 * of the search image I is pre-calculated during construction. The assumption is, that the search image I is fixed and
 * the {@link ChamferMatcher} tries to match multiple reference images R. All images are considered binary, with
 * non-zero values taken as foreground pixels. See Sec. 23.2.3 (Alg. 23.3) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/14
 */
public class ChamferMatcher {
	
	private final int wI, hI;		// dimensions of the search image
	private final float[][] DI;		// distance transform of I

	/**
	 * Constructor using the default distance norm (L2). The supplied image must be binary with zero background values.
	 * @param I the binary "search" image (to be searched for matches of the "reference" image)
	 */
	public ChamferMatcher(ByteProcessor I) {
		this(I, DistanceTransform.DistanceType.L2);
	}
	
	/**
	 * Constructor using the specified distance norm. The supplied image must be binary with zero background values.
	 * @param I the binary "search" image (to be searched for matches of the "reference" image)
	 * @param norm the distance norm
	 */
	public ChamferMatcher(ByteProcessor I, DistanceTransform.DistanceType norm) {
		this.wI = I.getWidth();
		this.hI = I.getHeight();
		this.DI = (new DistanceTransform(I, norm)).getDistanceMap();
	}
	
	/**
	 * Calculates the match function for specified reference image R to this matcher's search image I (defined
	 * by the constructor). The returned function Q[r][s] is the match score for reference image R positioned
	 * at (r,s) in the search image coordinate frame.
	 *
	 * @param R a binary reference image
	 * @return a 2D array Q[r][s] of match scores
	 */
	public float[][] getMatch(ByteProcessor R) {
		PntInt[] pR = collectForegroundPoints(R);
		return getMatch(pR, R.getWidth(), R.getHeight());
	}

	// Collect all foreground pixel coordinates of R into a point array.
	private PntInt[] collectForegroundPoints(ByteProcessor R) {
		final int w = R.getWidth();
		final int h = R.getHeight();
		List<PntInt> pntList = new ArrayList<>();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (R.get(i, j) != 0) {	// foreground pixel in reference image
					pntList.add(PntInt.from(i, j));
				}
			}
		}
		return pntList.toArray(new PntInt[0]);
	}

	// ------------------------------------------------------------

	/**
	 * Matches the specified point set to the (fixed) search image I. The points represent the foreground pixels of a
	 * virtual reference image (R) with the specified width and height.
	 *
	 * @param pR a set of foreground points representing the reference image R
	 * @param wR the width of the reference image
	 * @param hR the height of the reference image
	 * @return a 2D array Q[r][s] of match scores
	 */
	public float[][] getMatch(PntInt[] pR, int wR, int hR) {
		final float[][] Q = new float[wI - wR + 1][hI - hR + 1];
		for (int r = 0; r < Q.length; r++) {
			for (int s = 0; s < Q[r].length; s++) {
				Q[r][s] = getMatchScore(pR, r, s);
			}	
		}	
		return Q;
	}

	// Calculates the match score for a single position (r,s).
	private float getMatchScore(PntInt[] pR, int r, int s) {
		float q = 0.0f;
		for (PntInt p : pR) {
			final int u = r + p.x;
			final int v = s + p.y;
			if (0 <= u && u < DI.length && 0 <= v && v < DI[u].length) {
				q = q + DI[u][v];
			}
		}
		return q;
	}

}
