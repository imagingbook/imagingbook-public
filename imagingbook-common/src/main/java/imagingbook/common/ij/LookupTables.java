/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.ij;

import ij.IJ;
import ij.process.ImageProcessor;
import ij.process.LUT;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;


/**
 * This class holds utility methods for lookup-tables and implementations
 * of various standard ImageJ lookup tables (originally defined in class
 * {@link ij.plugin.LutLoader}.
 * 
 * @author WB
 * @version 2016/12/19
 */
public abstract class LookupTables {
	
	/**
	 * Create a new lookup-table from three RGB arrays of length 256.
	 * @param r Red component values.
	 * @param g Green component values.
	 * @param b Blue component values.
	 * @return A new instance of type {@link ij.process.LUT}.
	 */
	public static LUT create(byte[] r, byte[] g, byte[] b) {
		if (r.length != 256 || g.length != 256 || b.length != 256) {
			throw new IllegalArgumentException("Component arrays must be of length 256");
		}
		return new LUT(r, g, b);
	}


	/**
	 * ImageJ's 'fire' LUT, as originally defined in class {@link ij.plugin.LutLoader}.
	 * @return A new instance of type {@link ij.process.LUT}.
	 */
	public static LUT fire() {
		int[] r = { 0, 0, 1, 25, 49, 73, 98, 122, 146, 162, 173, 184, 195, 207, 217, 229, 240, 252,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 };
		int[] g = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 35, 57, 79, 101, 117, 133, 147, 161,
				175, 190, 205, 219, 234, 248, 255, 255, 255, 255 };
		int[] b = { 0, 61, 96, 130, 165, 192, 220, 227, 210, 181, 151, 122, 93, 64, 35, 5, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 35, 98, 160, 223, 255 };

		return create(interpolateTo256(r), interpolateTo256(g), interpolateTo256(b));
	}


	/**
	 * ImageJ's 'grays' LUT, as originally defined in class {@link ij.plugin.LutLoader}.
	 * @return A new instance of type {@link ij.process.LUT}.
	 */
	public static LUT grays() {
		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];
		for (int i = 0; i < r.length; i++) {
			r[i] = (byte) i;
			g[i] = (byte) i;
			b[i] = (byte) i;
		}
		return create(r, g, b);
	}

	/**
	 * ImageJ's 'ice' LUT, as originally defined in class {@link ij.plugin.LutLoader}.
	 * @return A new instance of type {@link ij.process.LUT}.
	 */
	public static LUT ice() {
		int[] r = { 0, 0, 0, 0, 0, 0, 19, 29, 50, 48, 79, 112, 134, 158, 186, 201, 217, 229, 242, 250, 250, 250, 250,
				251, 250, 250, 250, 250, 251, 251, 243, 230 };
		int[] g = { 156, 165, 176, 184, 190, 196, 193, 184, 171, 162, 146, 125, 107, 93, 81, 87, 92, 97, 95, 93, 93, 90,
				85, 69, 64, 54, 47, 35, 19, 0, 4, 0 };
		int[] b = { 140, 147, 158, 166, 170, 176, 209, 220, 234, 225, 236, 246, 250, 251, 250, 250, 245, 230, 230, 222,
				202, 180, 163, 142, 123, 114, 106, 94, 84, 64, 26, 27 };

		return new LUT(interpolateTo256(r), interpolateTo256(g), interpolateTo256(b));
	}

	/**
	 * ImageJ's 'spectrum' LUT, as originally defined in class {@link ij.plugin.LutLoader}.
	 * @return A new instance of type {@link ij.process.LUT}.
	 */
	public static LUT spectrum() {
		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];
		for (int i = 0; i < r.length; i++) {
			Color c = Color.getHSBColor(i/255f, 1.0f, 1.0f);
			r[i] = (byte) c.getRed();
			g[i] = (byte) c.getGreen();
			b[i] = (byte) c.getBlue();
		}
		return new LUT(r, g, b);
	}

	/**
	 * ImageJ's 'rgb332' LUT, as originally defined in class {@link ij.plugin.LutLoader}.
	 * @return A new instance of type {@link ij.process.LUT}.
	 */
	public static LUT rgb332() {
		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];
		for (int i = 0; i < r.length; i++) {
			r[i] = (byte) (i & 0xE0);
			g[i] = (byte) ((i << 3) & 0xE0);
			b[i] = (byte) ((i << 6) & 0xC0);
		}
		return new LUT(r, g, b);
	}
	
	/**
	 * ImageJ's 'redgreen' LUT, as originally defined in class {@link ij.plugin.LutLoader}.
	 * @return A new instance of type {@link ij.process.LUT}.
	 */
	public static LUT redgreen() {
		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];
		for (int i = 0; i < 128; i++) {
			r[i] = (byte) (i * 2);
			g[i] = (byte) 0;
			b[i] = (byte) 0;
		}
		for (int i = 128; i < 256; i++) {
			r[i] = (byte) 0;
			g[i] = (byte) (i * 2);
			b[i] = (byte) 0;
		}

		return new LUT(r, g, b);
	}

	// --------------------------------------------------------------

	private static byte[] interpolateTo256(int[] samples) {
		final int nColors = 256;
		int nSamples = samples.length;
		byte[] component = new byte[nColors];
		double scale = (double) nSamples / nColors;
		for (int i = 0; i < component.length; i++) {
			int i1 = (int) Math.floor(i * scale);
			int i2 = i1 + 1;
			if (i2 >= nSamples)
				i2 = nSamples - 1;
			double frac = i * scale - i1;	// frac is in [0,1]
			int val = (int) Math.round((1.0 - frac) * samples[i1] + frac * samples[i2]);
			if (val < 0)
				val = 0;
			else if (val > 255)
				val = 255;
			component[i] = (byte) val;
			//component[i] = (byte) ((1.0 - frac) * (samples[i1] & 0xFF) + frac * (samples[i2] & 0xFF));
		}
		return component;
	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * Lists the contents of the lookup-table currently associated
	 * with the specified image.
	 * 
	 * @param ip The image.
	 */
	public static void listCurrentLut(ImageProcessor ip) {
		ColorModel cm = ip.getCurrentColorModel();
		IndexColorModel icm = (IndexColorModel) cm;
		int mapSize = icm.getMapSize();
		byte[] reds = new byte[mapSize];
		byte[] grns = new byte[mapSize];
		byte[] blus = new byte[mapSize];
		icm.getReds(reds);
		icm.getGreens(grns);
		icm.getBlues(blus);
		for (int i = 0; i < mapSize; i++) {
			IJ.log(String.format("%3d: %3d %3d %3d", i, reds[i] & 0xFF, grns[i] & 0xFF, blus[i] & 0xFF));
		}
	}
	
	
	/**
	 * Modifies the lookup table to display a bright image with gray values
	 * in the range minGray ... 255. Does nothing if ip is of type
	 * ColorProcessor.
	 * 
	 * @param ip The target image.
	 * @param minGray Minimum gray value.
	 */
	public static void brightLut(ImageProcessor ip, int minGray) {
		if (minGray < 0 || minGray >= 255)
			return;
		ColorModel cm = ip.getColorModel();
		if (!(cm instanceof IndexColorModel))
			return;
		IndexColorModel icm = (IndexColorModel) cm;
		int mapSize = icm.getMapSize();
		byte[] reds = new byte[mapSize];
		byte[] grns = new byte[mapSize];
		byte[] blus = new byte[mapSize];
		float scale = (255 - minGray) / 255f;
		for (int i = 0; i < mapSize; i++) {
			byte g = (byte) (Math.round(minGray + scale * i) & 0xFF);
			reds[i] = g;
			grns[i] = g;
			blus[i] = g;
		}
		ip.setColorModel(new IndexColorModel(8, mapSize, reds, grns, blus));
	}

}
