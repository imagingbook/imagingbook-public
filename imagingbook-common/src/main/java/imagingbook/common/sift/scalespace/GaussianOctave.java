/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.sift.scalespace;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * <p>
 * Represents a single "octave" in a hierarchical Gaussian scale space. See Secs. 25.1.2 and 25.1.4 of [1] for more
 * details. This class defines no public constructor.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/20
 */
public class GaussianOctave extends ScaleOctave<ScaleLevel> {
	
	/**
	 * Constructor (non-public).
	 * @param p the index of the new octave
	 * @param Q the number of scale levels inside the new octave
	 * @param Gbot the bottom scale elevel to start with
	 * @param botIndex the bottom scale level index
	 * @param topIndex the top scale level index
	 * @param sigma_0 the scale at scale level with index 0
	 */
	GaussianOctave(int p, int Q, ScaleLevel Gbot, int botIndex, int topIndex, double sigma_0) {
		// initialize generic octave structures (no scale levels yet):
		super(p, Q, Gbot.getWidth(), Gbot.getHeight(), botIndex, topIndex, sigma_0);
		
		// assign the bottom octave level:
		double sigmaA_bot = getAbsoluteScale(p, botIndex);
		this.setLevel(botIndex, Gbot);
		Gbot.setAbsoluteScale(sigmaA_bot);
		
		// create octave levels q = botIndex + 1,...,topIndex
		for (int q = botIndex + 1; q <= topIndex; q++) {
			double sigmaA = getAbsoluteScale(p, q);	// absolute scale of level q
			// relative scale from bottom level (q = -1):
			double sigmaR = sigma_0 * sqrt(pow(2, 2.0 * q/Q) - pow(2, -2.0/Q)); 
			
			// duplicate the botton scale level with the new absolute scale:
			ScaleLevel G_pq = new ScaleLevel(Gbot, sigmaA);
			// filter the new scale level (destructively):
			G_pq.filterGaussian(sigmaR);
			// insert the new scale level into this scale octave
			this.setLevel(q, G_pq);
		}
	}
	
}
