/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.sets;

import java.awt.Color;
import java.util.Objects;

/**
 * Interface used for color enum types.
 * @author WB
 *
 */
public interface ColorEnumeration {
	
	/**
	 * Returns the enum item's {@link Color}.
	 * @return the item's AWT {@link Color}
	 */
	public Color getColor();

	/**
	 * Returns an array of colors defined by the specified
	 * {@link ColorEnumeration} enum class.
	 * @param clazz a {@link ColorEnumeration} enum class
	 * @return a {@link Color} array
	 */
	public static Color[] getColors(Class<? extends ColorEnumeration> clazz) {
		if (!clazz.isEnum()) {
			throw new RuntimeException(ColorEnumeration.class.getSimpleName() + 
					" may only be implemented by enum types!");
		}
		ColorEnumeration[] colorItems = clazz.getEnumConstants(); 
		Objects.requireNonNull(colorItems);
		return getColors(colorItems);
	}
	
	/**
	 * Returns an array of colors for the specified 
	 * {@link ColorEnumeration} items.
	 * @param cols a sequence of {@link ColorEnumeration} items
	 * @return a {@link Color} array
	 */
	public static Color[] getColors(ColorEnumeration... cols) {
		Color[] colors = new Color[cols.length];
		for (int i = 0; i < cols.length; i++) {
			colors[i] = cols[i].getColor();
		}
		return colors;
	}
	
//	public static void main(String[] args) {
//		Color[] colors = ColorEnumeration.getColors(BasicAwtColor.class);
//		
//		for (Color c : colors) {
//			System.out.println(c.toString());
//		}
//	}
	
}
