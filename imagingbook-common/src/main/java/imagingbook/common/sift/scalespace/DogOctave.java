/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.sift.scalespace;

/**
 * <p>
 * Represents a single "octave" in a hierarchical DoG scale space. See Secs.
 * 25.1.3 - 25.1.4 of [1] for more details.
 * This class defines no public constructor.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/20
 */
public class DogOctave extends ScaleOctave<ScaleLevel> {

	/**
	 * Creates a new {@link DogOctave} from a {@link GaussianOctave}.
	 * @param Gp octave from a Gaussian scale space
	 */
	DogOctave(GaussianOctave Gp) {
		super(Gp.p, Gp.Q, Gp.width, Gp.height, Gp.botLevelIndex, Gp.topLevelIndex-1, Gp.sigma_0);
		// create DoG octave
		for (int q = botLevelIndex; q <= topLevelIndex; q++) {
			ScaleLevel Dpq = getDifference(Gp.getLevel(q+1), Gp.getLevel(q));
			setLevel(q, Dpq);
		}
	}
	
	private ScaleLevel getDifference(ScaleLevel Ga, ScaleLevel Gb) {
		// Ga: Gaussian at level q+1
		// Gb: Gaussian at level q
		// D <-- Ga - Gb (scale the same as Gb)
		float[] dataA = Ga.getData();
		float[] dataB = Gb.getData();
		float[] dataD = new float[dataA.length];
		for (int i = 0; i < dataA.length; i++) {
			dataD[i] = dataA[i] - dataB[i];
		}
		ScaleLevel D = new ScaleLevel(Ga.getWidth(), Ga.getHeight(), dataD, Gb.getAbsoluteScale());
		return D;
	}
	
}
