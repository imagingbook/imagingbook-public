/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.mser;

import static imagingbook.common.color.sets.ColorEnumeration.getColors;

import java.awt.Color;

import imagingbook.common.color.sets.ColorEnumeration;

/**
 * Color definitions used in MSER demo plugins.
 * 
 * @author WB
 * @version 2022/11/19
 */
public enum MserColors implements ColorEnumeration {
	Red(240, 0, 0),
	Green(0, 185, 15),
	Blue(0, 60, 255),
	Magenta(255, 0, 200),
	Brown(160, 82, 45),
	Yellow(255, 200, 0),
	Orange(255, 182, 86),
	Cyan(0, 198, 255),
	;

	public static final Color[] LevelColors = 
			getColors(Red, Green, Blue, Magenta);

	
	private final Color color;
	
	MserColors(int r, int g, int b) {
		this.color = new Color(r, g, b);
	}
	
	@Override
	public Color getColor() {
		return this.color;
	}

}
