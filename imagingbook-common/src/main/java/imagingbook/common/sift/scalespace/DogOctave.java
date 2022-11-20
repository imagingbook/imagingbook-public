/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.sift.scalespace;

/**
 * <p>
 * Represents a single "octave" in a hierarchical DoG scale space. See Secs.
 * 25.1.3 - 25.1.4 of [1] for more details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/20
 */
public class DogOctave extends ScaleOctave {

	// TODO: check correctness of bottom and top levels!!
	DogOctave(ScaleOctave Gp) {
		super(Gp.p, Gp.Q, Gp.width, Gp.height, Gp.botLevelIndex, Gp.topLevelIndex-1);
		// create DoG octave
		for (int q = botLevelIndex; q <= topLevelIndex; q++) {
			ScaleLevel Dpq = differenceOfGaussians(Gp.getLevel(q+1), Gp.getLevel(q));
			this.setLevel(q, Dpq);
		}
	}
	
	public ScaleLevel differenceOfGaussians(ScaleLevel A, ScaleLevel B) {
		// A: Gaussian at level q+1
		// B: Gaussian at level q
		// C <-- A - B (scale the same as B)
		ScaleLevel C = B.duplicate();
		float[] dataA = A.getData();
		float[] dataB = B.getData();
		float[] dataC = C.getData();
		for (int i = 0; i < dataA.length; i++) {
			dataC[i] = dataA[i] - dataB[i];
		}
		C.setAbsoluteScale(B.getAbsoluteScale());
		return C;
	}
	
}
