/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util.random;

/**
 * A convenience random generator for angular values.
 * 
 * @author WB
 */
public class RandomAngle extends java.util.Random {
	private static final long serialVersionUID = 1L;
	
	public RandomAngle() {
		super();
	}
	
	public RandomAngle(long seed) {
		super(seed);
	}

	/**
	 * Returns a random {@code double} value in the range [-&pi;,+&pi;].
	 *
	 * @return a random angle
	 */
	public double nextAngle() {
		return (2 * this.nextDouble() - 1) * Math.PI;
	}

}
