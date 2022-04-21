/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package ColorImages;

import java.awt.image.IndexColorModel;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.text.TextWindow;

public class Show_Lookup_Table implements  PlugInFilter {

	@Override
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G + DOES_8C;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		ImageProcessor ip2 = ip.createProcessor(256, 256); //ip.convertToByteProcessor();
		int k = 0;
		for (int row = 0; row < 16; row++) {
			int v = row * 16;
			for (int col = 0; col < 16; col++) {
				int u = col * 16;
				// fill the patch with value k
				for (int j = 0; j < 16; j++) {
					for (int i = 0; i < 16; i++) {
						ip2.set(u + i, v + j, k);
					}
				}
				k = k + 1;
			}
			
		}
		new ImagePlus("ColorTable", ip2).show();
		
		listLut(ip);
		
	}
	
	/**
	 * @author Wayne Rasband
	 * @param ip The image.
	 */
	void listLut(ImageProcessor ip) {
		IndexColorModel icm = (IndexColorModel) ip.getColorModel();
		int size = icm.getMapSize();
		byte[] r = new byte[size];
		byte[] g = new byte[size];
		byte[] b = new byte[size];
		icm.getReds(r);
		icm.getGreens(g);
		icm.getBlues(b);
		StringBuffer sb = new StringBuffer();
		String headings = "Index\tRed\tGreen\tBlue";
		for (int i = 0; i < size; i++)
			sb.append(i + "\t" + (r[i] & 255) + "\t" + (g[i] & 255) + "\t" + (b[i] & 255) + "\n");
		new TextWindow("LUT", headings, sb.toString(), 250, 400);
	}

}
