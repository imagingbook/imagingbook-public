/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.sift.scalespace;

public class GaussianOctave extends ScaleOctave {
	
	GaussianOctave(int p, int Q, ScaleLevel Gbot, int botIndex, int topIndex, double sigma_0) {
		super(p, Q, Gbot, botIndex, topIndex);	// initialize the bottom level (botIndex) of this octave with Gbot
		this.sigma_0 = sigma_0;					// reference scale at level 0 of this octave
		double sigmaA_bot = getAbsoluteScale(p, botIndex);
		Gbot.setAbsoluteScale(sigmaA_bot);
		
		// create octave levels q = botIndex + 1,...,topIndex
		for (int q = botIndex + 1; q <= topIndex; q++) {
			double sigmaA_q = getAbsoluteScale(p, q);	// absolute scale of level q
//			double sigmaR_q = Math.sqrt(sigmaA_q * sigmaA_q - sigmaA_bot * sigmaA_bot) / Math.pow(2, p);  // relative scale from bottom level (-1)
			double sigmaR_q = sigma_0 * Math.sqrt(Math.pow(2, 2.0 * q / Q) - Math.pow(2, -2.0 / Q)); // relative scale from bottom level (-1)
			ScaleLevel G_pq = Gbot.duplicate();
			G_pq.filterGaussian(sigmaR_q);
			G_pq.setAbsoluteScale(sigmaA_q);
			this.setLevel(q, G_pq);
		}
	}
	
}
