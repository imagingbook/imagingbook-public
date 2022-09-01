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

/**
 * Used for color enum types. This means that every enum class (item) implements this interface.
 * @author WB
 *
 */
public interface ColorEnumeration {
	
	public Color getColor();
	
	public default int getRGB() {
		return getColor().getRGB();
	}

	public static Color[] getColors(Class<? extends ColorEnumeration> clazz) {
		ColorEnumeration[] cols = clazz.getEnumConstants(); 
		if (cols == null) {
			throw new RuntimeException(ColorEnumeration.class.getSimpleName() + " may only be implemented by enum types!");
		}
//		Color[] colors = new Color[cols.length];
//		for (int i = 0; i < cols.length; i++) {
//			colors[i] = cols[i].getColor();
//		}
//		return colors;
		return getColors(cols);
	}
	
	public static Color[] getColors(ColorEnumeration... cols) {
		Color[] colors = new Color[cols.length];
		for (int i = 0; i < cols.length; i++) {
			colors[i] = cols[i].getColor();
		}
		return colors;
	}
	
	
	public static void main(String[] args) {
		
		Color[] colors = ColorEnumeration.getColors(BasicAwtColor.class);
		for (Color c : colors) {
			System.out.println(c.toString());
		}
	}
	
}
