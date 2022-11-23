/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.sift.scalespace;

import static imagingbook.common.math.Arithmetic.sqr;

import ij.process.FloatProcessor;

/**
 * <p>
 * Represents a hierarchical Gaussian scale space. See Secs. 25.1.2 and 25.1.4
 * of [1] for more details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/20
 */
public class GaussianScaleSpace extends HierarchicalScaleSpace<GaussianOctave> {

	/**
	 * Constructor, builds a {@link GaussianScaleSpace} from a {@link FloatProcessor}.
	 * 
	 * @param fp a {@link FloatProcessor}
	 * @param P the number of scale space octaves
	 * @param Q the number of scale steps (levels) per octave
	 * @param sigma_s the assumed sampling scale (typ. 0.5)
	 * @param sigma_0 the base scale of level 0 
	 * @param botLevel the index of the bottom level in each octave
	 * @param topLevel the index of the to level in each octave
	 */
	public GaussianScaleSpace(FloatProcessor fp, int P, int Q, double sigma_s, double sigma_0, int botLevel, int topLevel) {
		super(P, Q, sigma_s, sigma_0, botLevel, topLevel);	
		build(fp);
	}
	
	// -------------------------------------------------------------
	
	private final void build(FloatProcessor fp) {
		double scaleA = getAbsoluteScale(0, botLevel) ;			// absolute scale of level(0,-1) = bottom
		double scaleR = Math.sqrt(sqr(scaleA) - sqr(sigma_s));	// relative scale from sampling scale
		
		float[] data = ((float[])fp.getPixels()).clone();
		ScaleLevel Ginit = new ScaleLevel(fp.getWidth(), fp.getHeight(), data, scaleR);
		Ginit.filterGaussian(scaleR);
		
		// create the bottom octave
		setOctave(0, new GaussianOctave(0, Q, Ginit, botLevel, topLevel, sigma_0));
		// build the remaining Q-1 octaves:
		for (int p = 1; p < P; p++) {
			// get the top level of the previous octave and decimate it:
			ScaleLevel Gbase = getOctave(p-1).getLevel(Q-1).decimate();
			setOctave(p, new GaussianOctave(p, Q, Gbase, botLevel, topLevel, sigma_0));
		}
	}
	
}
