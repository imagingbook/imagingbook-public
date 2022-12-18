/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch21_Geometric_Operations;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import imagingbook.common.image.ImageGraphics;

import java.awt.Font;
import java.awt.Graphics2D;

/**
 * <p>
 * This ImageJ plugin draws a test grid in a new image. It uses anti-aliased drawing operations provided by
 * imagingbook's {@link ImageGraphics} class. Used in [1] for testing geometric transformations (see Fig. 21.6) and
 * interpolation methods (see Fig. 22.25).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/28
 * @see ImageGraphics
 */
public class Draw_Test_Grid implements PlugIn {
	
	private static int W = 400;
	private static int H = 400;
	private static int xStep = 20;
	private static int yStep = 20;
	private static int xStart = 100;
	private static int yStart = 100;
	private static int xN = 10;
	private static int yN = 10;
	
	private static int foreground = 0;
	private static int background = 255;
	
    @Override
	public void run(String arg) {
    	ByteProcessor ip = new ByteProcessor(W, H);
    	ip.setValue(background);
    	ip.fill();
    	
    	try (ImageGraphics ig = new ImageGraphics(ip)) {
			ig.setColor(foreground);
			ig.setLineWidth(1.0);
			
			int y = yStart;
	    	int x1 = xStart;
	    	int x2 = xStart + xN * xStep;
			for (int j = 0; j <= yN; j++) {
				ig.drawLine(x1, y, x2, y);
				y = y + yStep;
			}
			
			int x = xStart;
			int y1 = yStart;
			int y2 = yStart + yN * yStep;
			for (int i = 0; i <= xN; i++) {
				ig.drawLine(x, y1, x, y2);
				x = x + xStep;
			}
			
			ig.drawLine(0, 0, W - 1, H - 1);
			ig.drawOval(xStart, yStart, W/2, H/2);
			
			Graphics2D g = ig.getGraphics2D();
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
			float xLeft = 8;
			float yTop  = 19;
			float xRight = W - 17;
			float yBot  = H - 10;
			g.drawString("1", xLeft, yTop);
			g.drawString("2", xRight, yTop);
			g.drawString("3", xRight, yBot);
			g.drawString("4", xLeft, yBot);
    	}
    	
        new ImagePlus("Grid",ip).show();
    }
}
