/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.mser.visualize;

import java.awt.Color;

public class Colors {
	
	public static Color Red = new Color(240,0,0);
	public static Color Green = new Color(0,185,15);
	public static Color Blue = new Color(0,60,255);
	public static Color Magenta = new Color(255,0,200);
	public static Color Brown = new Color(0xA0, 0x52, 0x2D);
	public static Color Yellow = new Color(255,200,0);
	public static Color Orange= new Color(0xffb656);
	public static Color Cyan= new Color(0x00c6FF);		//11efff

	public static Color[] LevelColors = {Red, Green, Blue, Magenta}; // same as SIFT
	
	// https://learnui.design/tools/data-color-picker.html#palette
	// makePalette(0x2a31fb, 0xcb00d6, 0xff00a7, 0xff0078, 0xff5b4d, 0xff9c27, 0xffd019, 0xfffd49);
	public static Color[] makePalette(Integer... cols) {
		Color[] colors = new Color[cols.length];
		for (int i = 0; i < cols.length; i++) {
			colors[i] = new Color(cols[i]);
		}
		return colors;
	}
	
	

}
