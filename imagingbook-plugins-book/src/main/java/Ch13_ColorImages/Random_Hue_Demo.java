/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch13_ColorImages;

import java.awt.Color;
import java.util.Random;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import imagingbook.common.color.iterate.RandomHueGenerator;

/**
 * ImageJ plugin, creates a random color image using {@link RandomHueGenerator}.
 * 
 * @author WB
 *
 */
public class Random_Hue_Demo implements PlugIn {
	
	static int TileSize = 20;
	static int TilesHor = 48;
	static int TilesVer = 32;
	Random rnd = new Random();
	
	@Override
	public void run(String arg) {
		int width = TileSize * TilesHor;
		int height = TileSize * TilesVer;
		
		ColorProcessor cp = new ColorProcessor(width, height);
		RandomHueGenerator rhg = new RandomHueGenerator();
		
		for (int i = 0; i < TilesHor; i++) {
			for (int j = 0; j < TilesVer; j++) {
				Color c = rhg.next();
				cp.setColor(c);
				cp.fillRect(i * TileSize, j * TileSize, TileSize, TileSize);
			}
		}
		
		new ImagePlus(getClass().getSimpleName(), cp).show();
	}
}
