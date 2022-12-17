/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch26_MSER;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.GuiTools;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.mser.MserColors;
import imagingbook.common.mser.MserData;
import imagingbook.common.mser.MserDetector;
import imagingbook.common.mser.MserParameters;
import imagingbook.common.mser.components.Component;
import imagingbook.common.mser.components.PixelMap.Pixel;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin which runs MSER detection [1] on the current image and shows the result as a vector overlay in a new
 * image. If the option {@code MarkMserPixels} is selected, the output is a color image with pixels belonging to MSER
 * components marked in different colors. The input image is always converted to grayscale before MSER detection is
 * performed. See Ch. 26 of [2] for details. If no image is currently open, the user is asked to load a predefined
 * sample image.
 * </p>
 * <p>
 * [1] J. Matas, O. Chum, M. Urban, and T. Pajdla. Robust widebaseline stereo from maximally stable extremal regions.
 * Image and Vision Computing 22(10), 761–767 (2004). <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing
 * &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/24
 * @see MserDetector
 */
public class MSER_Detection_Demo implements PlugInFilter {
	
	private static ImageResource SampleImage = GeneralSampleImage.MortarSmall;
	
	private static boolean ShowMserCount = true;
	private static boolean ShowEllipses = true;
	private static boolean MarkMserPixels = false;
	private static boolean ShowColorPalette = false;
	private static boolean ShowMserLabels = false;
	private static boolean ShowElapsedTime = false;
	
	// processing direction
	private static boolean BlackToWhite = true;		// detect on original image (default)
	private static boolean WhiteToBlack = false;	// detect on inverted image
	
	private static boolean UseTwoColorsOnly = false;	// detect on inverted image
	private static Color BlackToWhiteColor = MserColors.Yellow.getColor(); //.Orange;
	private static Color WhiteToBlackColor = MserColors.Cyan.getColor();
	private static int MinDisplayWidth = 300;
	
	private static MserParameters params = new MserParameters();	// MSER parameters
	
	private ImagePlus im = null;
	private Color[] colors = null;
	private double ellipseStrokeWidth = 0;		// set dynamically
	private int labelFontSize = 0;				// set dynamically
	private Font labelFont = null;				// set dynamically	
	private Roi roi = null;
	private ByteProcessor bp = null;
	private ImageProcessor ip2 = null;
	private Overlay oly = null;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public MSER_Detection_Demo() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(SampleImage);
		}
	}
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		this.im = im;
		return DOES_8G + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		roi = im.getRoi();
		ellipseStrokeWidth = Math.max(0.25, ip.getWidth() * 0.1 / 100);
		labelFontSize = Math.max(3, (int) Math.round(ip.getWidth() / 100.0));	
				
		if (!runDialog(params)) {
			return;
		}
		
		bp = ip.convertToByteProcessor();
		ip2 = (MarkMserPixels) ? ip.convertToColorProcessor() : ip.convertToByteProcessor();
		oly = new Overlay();
		String title = im.getShortTitle() + "-MSER";
		
		if (BlackToWhite || WhiteToBlack) title = title + "-";
		if (BlackToWhite)  title = title + "B";
		if (WhiteToBlack)  title = title + "W";
		if (UseTwoColorsOnly) title = title + "-2c";
		
		Color[] palette = MserColors.LevelColors;
		labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, labelFontSize);
	
		List<Component<MserData>> msersB = null;
		List<Component<MserData>> msersW = null;
//		List<Component<MserData>> msersAll = new ArrayList<>();
		
		double elapsedTime = 0;
		
		if (BlackToWhite) {
			MserDetector detector = new MserDetector(bp, params);
			msersB = detector.getMserFeatures();
			if (ShowMserCount) {
				IJ.log("Found MSERs (BlackToWhite): " + msersB.size());
			}
			if (UseTwoColorsOnly) 
				makeColorsBlackToWhite();
			else
				makeColors(palette);
			drawToOverlay(msersB);
			elapsedTime += detector.getElapsedTime();
		}
		
		if (WhiteToBlack) {
			bp.invert();
			MserDetector detector = new MserDetector(bp, params);
			msersW = detector.getMserFeatures();
			if (ShowMserCount) {
				IJ.log("Found MSERs (WhiteToBlack): " + msersW.size());
			}
			if (UseTwoColorsOnly) 
				makeColorsWhiteToBlack();
			else
				makeColors(palette);
			drawToOverlay(msersW);
			elapsedTime += detector.getElapsedTime();
		}
		
		if (ShowMserCount && BlackToWhite && WhiteToBlack) {
			IJ.log("Found MSERs total: " + (msersB.size() + msersW.size()));
		}
		
		if (ShowElapsedTime) {
			IJ.log(String.format("Algorithm %s: time elapsed %.0fms", params.method, elapsedTime));
		}
		
		if (ShowColorPalette) {
			if (UseTwoColorsOnly) 
				showTwoColors();
			else
				showColorPalette(title + "-palette");
		}
		
		// ----------------------------------------------------------------------
		
//		Component.sortBySize(msersAll);	// optional
//		drawToOverlay(msersAll);
		
		ImagePlus cimp = new ImagePlus(title, ip2);
		setMserImageProps(cimp, params);
		cimp.setOverlay(oly);
		cimp.show();
		
		// zoom result image if too small
		if (cimp.getWidth() < MinDisplayWidth) {
			int zoomFac = (int) Math.ceil((double) MinDisplayWidth / cimp.getWidth());
			GuiTools.zoomExact(cimp, zoomFac);
		}	
	}
	 
	private void drawToOverlay(List<Component<MserData>> msers) {
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
		ola.setFont(labelFont);
		
		for (Component<MserData> c : msers) {
			
			// ignore MSERs outside the current ROI (if specified)
			Pnt2d ctr = c.getProperties().getCenter();
			if (roi != null && !roi.containsPoint(ctr.getX(), ctr.getY())) {
				continue;
			}
			
			Color vecCol = getColor(c.getLevel());
			
			// draw contained points
			if (MarkMserPixels) {
				float[] hsb = Color.RGBtoHSB(vecCol.getRed(), vecCol.getGreen(), vecCol.getBlue(), null);
				Color pixCol = Color.getHSBColor(hsb[0], 0.5f, 0.5f);
				ip2.setColor(pixCol);
				for (Pixel pnt : c.getAllPixels()) {
					ip2.drawDot(pnt.x, pnt.y);
				}
			}
			
			if (ShowEllipses) {
				// suggests 0.2% of image width as stroke width (but at least 0.5)			
				GeometricEllipse ellipse = c.getProperties().getEllipse();
				ola.addShape(ellipse.getShape(), new ColoredStroke(ellipseStrokeWidth, vecCol));
				
				if (ShowMserLabels) {
					ola.setTextColor(vecCol);
					ola.addText(ellipse.xc, ellipse.yc, Integer.toString(c.ID));
				}
			}
		}
	}
	
	// --------------------------------------------
	
	private boolean runDialog(MserParameters params) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		addToDialog(params, gd);
		
		gd.addCheckbox("BLACK -> WHITE", BlackToWhite);
		gd.addCheckbox("WHITE -> BLACK", WhiteToBlack);
		gd.addCheckbox("Use 2 colors only", UseTwoColorsOnly);
		
		gd.addMessage("Output parameters:");
		gd.addCheckbox("Show MSER count", ShowMserCount);
		gd.addCheckbox("Show elapsed time", ShowElapsedTime);
		gd.addCheckbox("Show ellipses", ShowEllipses);
		gd.addNumericField("Ellipse stroke width", ellipseStrokeWidth, 2);
		gd.addCheckbox("Show MSER labels", ShowMserLabels);
		gd.addNumericField("Label font size", labelFontSize, 0);
		gd.addCheckbox("Mark MSER pixels", MarkMserPixels);
		gd.addCheckbox("Show color palette", ShowColorPalette);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		getFromDialog(params, gd);
		
		BlackToWhite = gd.getNextBoolean();
		WhiteToBlack = gd.getNextBoolean();
		UseTwoColorsOnly = gd.getNextBoolean();	
		// Output:
		ShowMserCount = gd.getNextBoolean();
		ShowElapsedTime = gd.getNextBoolean();
		ShowEllipses = gd.getNextBoolean();
		ellipseStrokeWidth = gd.getNextNumber();
		ShowMserLabels = gd.getNextBoolean();
		labelFontSize = (int) gd.getNextNumber();
		MarkMserPixels = gd.getNextBoolean();
		ShowColorPalette = gd.getNextBoolean();
				
//		// Debugging:
//		params.validateComponentTree = gd.getNextBoolean();
				
		return true;
	}
	
	// --------------------------------------------
	
	private void makeColors(Color[] palette) {
		final int n = palette.length;
		colors = new Color[256];
		for (int level = 0; level < 256; level++) {
			int k = (int) (n * level / 256.0);		// k in [0,n-1]
			colors[level] = palette[k];
		}
	}
	
	private void makeColorsBlackToWhite() {
		colors = new Color[256];
		for (int level = 0; level < 256; level++) {
			colors[level] = BlackToWhiteColor;
		}
	}
	
	private void makeColorsWhiteToBlack() {
		colors = new Color[256];
		for (int level = 0; level < 256; level++) {
			colors[level] = WhiteToBlackColor;
		}
	}
	
	private Color getColor(int level) {
		// level is in 0,..,255
		if (level < 0) 
			level = 0;
		if (level >= colors.length)
			level = colors.length - 1;
		return colors[level];
	}
	
	private void showColorPalette(String title) {
		int W = 20;
		int H = 256;
		ColorProcessor cp = new ColorProcessor(W, H);
		for (int level = 0; level < H; level++) {
			cp.setColor(getColor(level));
			for (int i = 0; i < W; i++) {
				cp.drawPixel(i, level);
			}
		}
		cp.flipVertical();
		new ImagePlus(title, cp).show();
	}
	
	private void showTwoColors() {
		{
			ColorProcessor cp = new ColorProcessor(20, 64);
			cp.setColor(BlackToWhiteColor);
			cp.fill();
			new ImagePlus("color-BW", cp).show();
		}
		{
			ColorProcessor cp = new ColorProcessor(20, 64);
			cp.setColor(WhiteToBlackColor);
			cp.fill();
			new ImagePlus("color-WB", cp).show();
		}
	}

	
	// --------------------------------------------
	
	private void setMserImageProps(ImagePlus imp, MserParameters params) {
		imp.setProp("MSER-delta", params.delta);
		imp.setProp("MSER-maxVariation", params.maxSizeVariation);
		imp.setProp("MSER-minDiversity", params.minDiversity);
		imp.setProp("MSER-maxRelArea", params.maxRelCompSize);
		imp.setProp("MSER-minRelArea", params.minRelCompSize);
		imp.setProp("MSER-minAbsArea", params.minAbsComponentArea);
		imp.setProp("MSER-method", params.method.toString());	
	}
	
}
