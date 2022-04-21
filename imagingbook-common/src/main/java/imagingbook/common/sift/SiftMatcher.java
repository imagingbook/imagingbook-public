/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.sift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import imagingbook.common.math.VectorNorm;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.common.util.ParameterBundle;

public class SiftMatcher {
	
	public static class Parameters implements ParameterBundle {
		/** Specify type of distance norm */
		public NormType norm = NormType.L2;
		/** Max. ratio between best and second-best match */
		public double rMax = 0.8;
		/** Set true to sort matches */
		public boolean sort = true;
	}
	
	private final Parameters params;
	private final SiftDescriptor[] fA;
	private final VectorNorm am;

	// constructor - using default parameters
	public SiftMatcher(List<SiftDescriptor> sfA) {
		this(sfA, new Parameters());
	}
	
	// constructor - using specific parameters
	public SiftMatcher(List<SiftDescriptor> sfA, Parameters params) {
		this.fA = sfA.toArray(new SiftDescriptor[0]);
		this.params = params;
		am = params.norm.create();
	}
	
	public List<SiftMatch> matchDescriptors(List<SiftDescriptor> sfB) {
		SiftDescriptor[] fB = sfB.toArray(new SiftDescriptor[0]);
		List<SiftMatch> matches = new ArrayList<SiftMatch>(fA.length);
				
		for (int i = 0; i < fA.length; i++) {
			SiftDescriptor si = fA[i];
			int i0 = -1;
//			int i1 = -1;
			double d0 = Double.MAX_VALUE;
			double d1 = Double.MAX_VALUE;
			
			for (int j = 0; j < fB.length; j++) {
				double d = dist(si, fB[j]);
				if (d < d0) {	// new absolute minimum distance
//					i1 = i0;	// old best becomes second-best
					d1 = d0;
					i0 = j;
					d0 = d;
				}
				else // not a new absolute min., but possible second-best
					if (d < d1) { // new second-best
//						i1 = j;
						d1 = d;
					}
			}
//			if (i1 >= 0 && d1 > 0.001 && d0/d1 < params.rho_max) 
			if (Double.isFinite(d1) && d1 > 0.001 && d0/d1 < params.rMax) {
				SiftDescriptor s0 = fB[i0];
				SiftMatch m = new SiftMatch(si, s0, d0);
				matches.add(m);
			}
		}
		if (params.sort) {
			Collections.sort(matches);  // sort matches to ascending distance d1
		}
		
		return matches;
	}
	
	double dist(SiftDescriptor d1, SiftDescriptor d2) {
		//final ArrayMatcher matcher = params.norm.matcher;
		return am.distance(d1.getFeatures(), d2.getFeatures());
	}

}
