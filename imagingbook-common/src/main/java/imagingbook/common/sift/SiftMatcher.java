/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.sift;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import imagingbook.common.math.VectorNorm;
import imagingbook.common.math.VectorNorm.NormType;

/**
 * <p>
 * Instances of this class perform matching between SIFT features. See Secs.
 * 25.5 of [1] for more details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/20 removed inner Parameters class, changed constructor
 */
public class SiftMatcher {
	
	public static final NormType DefaultNormType = NormType.L2;
	public static final double DefaultRMax = 0.8;
	
	private final VectorNorm norm;
	private final double rMax;

	/**
	 * Constructor using specific parameters.
	 * 
	 * @param normType the distance norm for comparing feature vectors
	 * @param rMax the max. distance ratio between best and second-best match
	 */
	public SiftMatcher(NormType normType, double rMax) {
		this.norm = normType.getInstance();
		this.rMax = rMax;
	}
	
	/**
	 * Constructor using default parameters.
	 * @see #DefaultNormType
	 * @see #DefaultRMax
	 */
	public SiftMatcher() {
		this(DefaultNormType, DefaultRMax);
	}
	
	/**
	 * Finds matches between two sets of SIFT descriptors. For each descriptor in
	 * the first set, the best and second-best fits are searched for in the second
	 * set. A valid match instance is created if the distance to the best-matching
	 * descriptor is significantly smaller than the distance to the second-best
	 * matching descriptor. The resulting list of matches is sorted by increasing
	 * match distance.
	 * 
	 * @param setA the first set of SIFT descriptors
	 * @param setB the second set of SIFT descriptors
	 * @return a (possibly empty) list of {@link SiftMatch} instances
	 */
	public List<SiftMatch> match(Collection<SiftDescriptor> setA, Collection<SiftDescriptor> setB) {
		List<SiftMatch> matches = new ArrayList<SiftMatch>(setA.size());
				
		for (SiftDescriptor si : setA) {
			SiftDescriptor s1 = null;				// best-matching feature
			double d1 = Double.POSITIVE_INFINITY;	// best match distance
			double d2 = Double.POSITIVE_INFINITY;	// second-best match distance
			
			for (SiftDescriptor sj : setB) {
				double d = si.getDistance(sj, norm); // dist(si, sj);
				if (d < d1) {	// new best match
					d2 = d1;	// demote current best match to second-best (keep distance only)
					s1 = sj;	// new best matching feature
					d1 = d;		// new best match distance
				}
				else // not a new absolute min., but possible second-best
					if (d < d2) { // new second-best distance
						d2 = d;
					}
			}
			if (Double.isFinite(d2) && d2 > 0.001 && d1/d2 < this.rMax) {
				SiftMatch m = new SiftMatch(si, s1, d1);
				matches.add(m);
			}
		}

		Collections.sort(matches);  // sort matches by ascending descriptor distance
		return matches;
	}

}
