/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.image.matching;
import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

/**
 * <p>
 * Instances of this class perform "chamfer" matching on binary images.
 * The "search" image I (to be searched for matches of the "reference" image R) is
 * initially associated with the {@link ChamferMatcher}.
 * The assumption is, that the search image I is fixed and the {@link ChamferMatcher}
 * tries to match multiple reference images R.
 * All images are considered binary, with non-zero values taken as foreground
 * pixels.
 * See Sec. 23.2.3 (Alg. 23.3) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2021/11/26
 * @version 2022/09/16 revised
 */
public class ChamferMatcher {
	
	private final int MI, NI;		// dimensions of the search image
	private final float[][] D;		// distance transform of I
	private int MR, NR;				// dimensions of the reference image (temp.)
	
	/**
	 * Constructor using the default distance norm (L2).
	 * @param I the "search" image (to be searched for matches of the "reference" image)
	 */
	public ChamferMatcher(ByteProcessor I) {
		this(I, DistanceNorm.L2);
	}
	
	/**
	 * Constructor using the specified distance norm.
	 * @param I the reference image (to be matched to)
	 * @param norm the distance norm
	 */
	public ChamferMatcher(ByteProcessor I, DistanceNorm norm) {
		this.MI = I.getWidth();
		this.NI = I.getHeight();
		this.D = (new DistanceTransform(I, norm)).getDistanceMap();
	}
	
	/**
	 * Matches the specified reference image R to the (fixed) search image I.
	 * @param R some binary reference image
	 * @return a 2D array Q[r][s] of match scores
	 */
	public float[][] getMatch(ByteProcessor R) {
		this.MR = R.getWidth();
		this.NR = R.getHeight();
//		final int[][] Ra = R.getIntArray();
		float[][] Q = new float[MI - MR + 1][NI - NR + 1];
		for (int r = 0; r <= MI - MR; r++) {
			for (int s = 0; s <= NI - NR; s++) {
				Q[r][s] = getMatchScore(R, r, s);
			}	
		}	
		return Q;
	}
	
	private float getMatchScore(ByteProcessor R, int r, int s) {
		float q = 0.0f;
		for (int i = 0; i < MR; i++) {
			for (int j = 0; j < NR; j++) {
				if (R.get(i, j) != 0) {	// foreground pixel in reference image
					q = q + D[r + i][s + j];
				}
			}
		}
		return q;
	}  	

	@SuppressWarnings("unused")
	private float getMatchScore(int[][] R, int r, int s) {
		float q = 0.0f;
		for (int i = 0; i < R.length; i++) {
			for (int j = 0; j < R[i].length; j++) {
				if (R[i][j] > 0) {	// foreground pixel in reference image
					q = q + D[r + i][s + j];
				}
			}
		}
		return q;
	}  	
	
	/**
	 * Matches the specified point set to the (fixed) search image I.
	 * The points represent the foreground pixels of a virtual reference image (R)
	 * with the specified width and height.
	 * 
	 * @param points a set of foreground points
	 * @param width the width of the virtual reference image
	 * @param height the height of the virtual reference image
	 * @return a 2D array Q[r][s] of match scores of size width x height
	 */
	public float[][] getMatch(PntInt[] points, int width, int height) {
		float[][] Q = new float[width][height];
		for (int r = 0; r <= width; r++) {
			for (int s = 0; s <= height; s++) {
				float q = getMatchValue(points, r, s);
				Q[r][s] = q;
			}	
		}	
		return Q;
	}
	
	private float getMatchValue(PntInt[] points, int r, int s) {
		float q = 0.0f;
		for (PntInt p : points) {
			final int u = r + p.x;
			final int v = s + p.y;
			if (0 <= u && u < MI && 0 <= v && v < NI) {
				q = q + D[u][v];
			}
		}
		return q;
	}

}
