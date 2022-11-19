/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.mser.visualize;

import java.awt.Color;

import imagingbook.common.color.sets.ColorEnumeration;

/**
 * Color definitions used in MSER demo plugins.
 * @author WB
 * @version 2022/11/19
 *
 */
public enum MserColor implements ColorEnumeration {
	Red(240,0,0),
	Green(0,185,15),
	Blue(0,60,255),
	Magenta(255,0,200),
	Brown(0xA0, 0x52, 0x2D),
	Yellow(255,200,0),
	Orange(0xff, 0xb6, 0x56),
	Cyan(0x00, 0xc6, 0xFF),
	;

	public static final Color[] LevelColors = 
		{Red.getColor(), Green.getColor(), Blue.getColor(), Magenta.getColor()}; // same as SIFT
	
	private final Color color;
	
	MserColor(int r, int g, int b) {
		this.color = new Color(r, g, b);
	}
	
	@Override
	public Color getColor() {
		return this.color;
	}
	
//	// https://learnui.design/tools/data-color-picker.html#palette
//	// makePalette(0x2a31fb, 0xcb00d6, 0xff00a7, 0xff0078, 0xff5b4d, 0xff9c27, 0xffd019, 0xfffd49);
//	public static Color[] makePalette(Integer... cols) {
//		Color[] colors = new Color[cols.length];
//		for (int i = 0; i < cols.length; i++) {
//			colors[i] = new Color(cols[i]);
//		}
//		return colors;
//	}
	

}
