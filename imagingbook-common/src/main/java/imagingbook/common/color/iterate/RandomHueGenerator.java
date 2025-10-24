/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.color.iterate;

import java.awt.Color;
import java.util.Random;

/**
 * Instances of this class create a sequence of random colors which only differ in hue, with saturation and value fixed.
 *
 * @author WB
 * @version 2022/11/06
 */
public class RandomHueGenerator implements ColorSequencer {
//	TODO: Improve randomness (currently not too good, much red ...), modify saturation?.
	private float h = 0.0f;		// hue (changing)
	private float s = 0.9f;		// saturation (fixed)
	private float b = 0.9f;		// brightness (fixed)
	private final Random rnd; 

	/**
	 * Constructor (no random seed).
	 */
	public RandomHueGenerator() {
		this(0);
	}

	/**
	 * Constructor with specific random seed (for repeatability).
	 *
	 * @param seed random seed
	 */
	public RandomHueGenerator(long seed) {
		this.rnd = (seed == 0) ? new Random() : new Random(seed);
	}

	/**
	 * Set the 'saturation' component of subsequent colors.
	 * @param s new saturation value
	 */
	public void setSaturation(double s) {
		if (s < 0) s = 0;
		if (s > 1) s = 1;
		this.s = (float) s;
	}

	/**
	 * Set the 'brightness' component of subsequent colors.
	 * @param b new saturation value
	 */
	public void setBrightness(double b) {
		if (b < 0) b = 0;
		if (b > 1) b = 1;
		this.b = (float) b;
	}
	
	/**
	 * Returns the next random color.
	 */
	@Override
	public Color next() {
		//h = (h + 0.8f + 0.3f * (float)rnd.nextGaussian()) % 1.0f;
		//h = (float) (h + rnd.nextGaussian()) % 1.0f;
		h = (h + 0.1f + 0.8f * rnd.nextFloat()) % 1.0f;
		return Color.getHSBColor(h, s, b); 
	}

}
