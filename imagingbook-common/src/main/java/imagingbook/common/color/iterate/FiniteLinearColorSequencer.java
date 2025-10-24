/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.iterate;

import imagingbook.common.color.sets.ColorEnumeration;

import java.awt.Color;

/**
 * <p>
 * This class defines methods for iterating over an ordered set of AWT colors. The color set can be constructed from
 * individual AWT colors (see {@link #FiniteLinearColorSequencer(Color...)}) or an enum class implementing
 * {@link ColorEnumeration} (see {@link #FiniteLinearColorSequencer(Class)}).
 * </p>
 * <p>
 * Usage examples:
 * </p>
 * <pre>
 * // from individual colors (or array of colors):
 * ColorSequencer iter1 = new FiniteLinearColorSequencer(Color.blue, Color.green, Color.red);
 * for (int i = 0; i &lt; 10; i++) {
 * 	Color c = iter1.next();
 * 	// use color c
 * }
 * // from enum type:
 * FiniteLinearColorSequencer iter2 = new FiniteLinearColorSequencer(BasicAwtColor.class);
 * iter2.reset(5);
 * for (int i = 0; i &lt; 10; i++) {
 * 	Color c = iter2.nextRandom();
 * 	// use color c
 * }
 * </pre>
 *
 * @author WB
 * @version 2022/04/06
 * @see Color
 * @see ColorEnumeration
 */
public class FiniteLinearColorSequencer implements ColorSequencer {

	protected final Color[] colorArray;
	protected int next = 0;

	public FiniteLinearColorSequencer(Color... colors) {
		if (colors.length == 0) {
			throw new IllegalArgumentException("color set may not be empty!");
		}
		this.colorArray = colors.clone();
	}

	public FiniteLinearColorSequencer(Class<? extends ColorEnumeration> clazz) {
//		this.colorArray = ColorEnumeration.getColors(clazz);
		this(ColorEnumeration.getColors(clazz));
	}
	
	// -------------------------------------------------------

	public int size() {
		return colorArray.length;
	}

	public Color getColor(int idx) {
		return colorArray[idx];
	}

	public Color[] getColors() {
		return colorArray;
	}

	// --- iteration stuff -----------------------------------

	/**
	 * Reset the iterator, such that the color returned by the following call to {@link #next()} has index 0.
	 */
	public void reset() {
		reset(0);
	}

	/**
	 * Reset the iterator such that the index of the item returned by the following call to {@link #next()} has the
	 * specified start index.
	 *
	 * @param offset the new start index
	 */
	public void reset(int offset) {
		this.next = Math.floorMod(offset, colorArray.length);
	}

	@Override
	public Color next() {
		Color nc = colorArray[next];
		next = (next + 1) % colorArray.length;
		return nc;
	}

}
