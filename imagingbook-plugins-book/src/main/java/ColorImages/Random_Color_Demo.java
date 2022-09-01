/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package ColorImages;

import java.awt.Color;
import java.util.Random;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import imagingbook.common.color.iterate.RandomHueGenerator;

/**
 * ImageJ plugin demonstrating the use of {@link RandomHueGenerator}.
 * 
 * @author WB
 *
 */
public class Random_Color_Demo implements PlugIn {
	
	static int Tile_Size = 20;
	static int Tiles_Hor = 48;
	static int Tiles_Ver = 32;
	Random rnd = new Random();
	
//	private float h = 0.0f;
//	private float s = 0.9f;
//	private float b = 0.9f;


	@Override
	public void run(String arg) {
		int width = Tile_Size * Tiles_Hor;
		int height = Tile_Size * Tiles_Ver;
		ColorProcessor cp = new ColorProcessor(width, height);
		RandomHueGenerator rcg = new RandomHueGenerator();
		for (int i = 0; i < Tiles_Hor; i++) {
			for (int j = 0; j < Tiles_Ver; j++) {
				Color c = rcg.next();
				cp.setColor(c);
				cp.fillRect(i * Tile_Size, j * Tile_Size, Tile_Size, Tile_Size);
			}
		}
		
		(new ImagePlus(getClass().getSimpleName(), cp)).show();
	}
}
