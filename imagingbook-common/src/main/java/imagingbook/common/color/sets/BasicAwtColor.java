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
 * {@link ColorEnumeration} defining a set of selected AWT colors.
 * 
 * @author WB
 *
 */
public enum BasicAwtColor implements ColorEnumeration {
	Black(Color.black),
	Blue(Color.blue),
	Cyan(Color.cyan),
	DarkGray(Color.darkGray),
	Gray(Color.gray),
	Green(Color.green),
	LightGray(Color.lightGray),
	Magenta(Color.magenta),
	Orange(Color.orange),
	Pink(Color.pink),
	Red(Color.red),
	White(Color.white),
	Yellow(Color.yellow);
	
	private final Color color;
	
	BasicAwtColor(int r, int g, int b) {
		this(new Color(r, g, b));
	}
	
	BasicAwtColor(Color col) {
		this.color = col;
	}
	
	@Override
	public Color getColor() {
		return this.color;
	}
	
}
