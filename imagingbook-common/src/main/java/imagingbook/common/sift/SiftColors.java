/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.sift;

import imagingbook.common.color.sets.ColorEnumeration;

import java.awt.Color;

import static imagingbook.common.color.sets.ColorEnumeration.getColors;

/**
 * Color definitions used in various SIFT demo plugins.
 * 
 * @author WB
 * @version 2022/11/19
 */
public enum SiftColors implements ColorEnumeration {
	Red(240, 0, 0),
	Green(0, 185, 15),
	Blue(0, 60, 255),
	Magenta(255, 0, 200),
	Yellow(255, 200, 0),
	;
	
	public static final Color[] ScaleLevelColors = 
			getColors(SiftColors.class);
	
	private final Color color;
	
	SiftColors(int r, int g, int b) {
		this.color = new Color(r, g, b);
	}
	
	@Override
	public Color getColor() {
		return this.color;
	}
	
}
