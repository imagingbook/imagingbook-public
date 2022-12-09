/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch13_Color_Images;

import java.awt.Color;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import imagingbook.common.color.iterate.RandomHueGenerator;


/**
 * ImageJ plugin, creates a tiled image with random colors obtained by varying hue only.
 *
 * @author WB
 * @see imagingbook.common.color.iterate.RandomHueGenerator
 */
public class Random_Hue_Demo implements PlugIn {
	
	private static int TileSize = 20;
	private static int TilesHor = 48;
	private static int TilesVer = 32;
	
	private double Saturation = 0.9;
	private double Brightness = 0.9;
	
	private static int RandomSeed = 0;
	private static String title = Random_Hue_Demo.class.getSimpleName();
	
	@Override
	public void run(String arg) {
		
		if (!runDialog()) {
			return;
		}
		
		int width = TileSize * TilesHor;
		int height = TileSize * TilesVer;
		
		ColorProcessor cp = new ColorProcessor(width, height);
		RandomHueGenerator rhg = new RandomHueGenerator(RandomSeed);
		rhg.setSaturation(Saturation);
		rhg.setBrightness(Brightness);
		
		for (int i = 0; i < TilesHor; i++) {
			for (int j = 0; j < TilesVer; j++) {
				Color c = rhg.next();
				cp.setColor(c);
				cp.fillRect(i * TileSize, j * TileSize, TileSize, TileSize);
			}
		}
		
		new ImagePlus(title, cp).show();
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addMessage("Creates a tiled color image with random hues.");
		gd.addNumericField("Tile size", TileSize, 0);
		gd.addNumericField("Tiles horizontal", TilesHor, 0);
		gd.addNumericField("Tiles vertical", TilesVer, 0);
		gd.addNumericField("Saturation 0..1 (S)", Saturation, 2);
		gd.addNumericField("Brightness 0..1 (V)", Brightness, 2);
		gd.addNumericField("Random seed (0=none)", RandomSeed, 0);
		gd.addStringField("Image title", title, 12);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		TileSize = (int) gd.getNextNumber();
		TilesHor = (int) gd.getNextNumber();
		TilesVer = (int) gd.getNextNumber();
		Saturation = gd.getNextNumber();
		Brightness = gd.getNextNumber();
		RandomSeed = (int) gd.getNextNumber();
		title = gd.getNextString();
		return true;
	}
}
