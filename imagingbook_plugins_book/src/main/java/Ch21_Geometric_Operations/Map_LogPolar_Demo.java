/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch21_Geometric_Operations;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.ImageCanvas;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.Mapping2D;
import imagingbook.common.geometry.mappings.nonlinear.LogPolarMapping2;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.image.ImageMapper;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Path2D;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin demonstrating the use of 2D log-polar mapping. Two mapping types are available (Version1, Version2).
 * See Sec. 21.1.6 of [1] for details and examples. The plugin works interactively. Once started, the current mouse
 * position specifies the transformation's center point. A new image with radius/angle coordinates is opened and
 * continuously updated, until the plugin is terminated. The circular source grid is displayed as a graphic overlay.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/16
 * @see LogPolarMapping2
 */
public class Map_LogPolar_Demo implements PlugInFilter, MouseListener, JavaDocHelp {
	
	private static int P = 60;		// number of radial steps
	private static int Q = 100;		// number of angular steps
	private double rmin, rmax;		// min/max radius (determined from image size)
	
	private static boolean ShowSamplingGrid = true;	
	private static BasicAwtColor OverlayColorChoice = BasicAwtColor.Green;
	private static float OverlayStrokeWidth = 0.2f;
	
	private ImagePlus sourceIm;
	private ImageCanvas sourceCv;
	private ImageProcessor sourceIp;
	private ImageProcessor targetIp;
	private ImagePlus targetIm;	
	private Mapping2D mapping;
	private String title;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Map_LogPolar_Demo() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Flower);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		this.sourceIm = im;
		return DOES_ALL;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		rmax = Math.hypot(ip.getWidth(), ip.getHeight()) / 3;
		rmin = rmax / 75;
		
		if (!runDialog()) {
			return;
		}
		
		this.sourceIp = ip;
		this.targetIp = sourceIp.createProcessor(P, Q);
		this.targetIm = new ImagePlus("Log Polar Image", targetIp);
		this.sourceCv = sourceIm.getWindow().getCanvas();
		this.sourceCv.addMouseListener(this);
		//sourceCv.addMouseMotionListener(this);
		this.sourceIm.setOverlay(null);
		this.title = sourceIm.getTitle();
		this.sourceIm.setTitle(title + " [RUNNING]");
		this.sourceIm.updateAndRepaintWindow();
		IJ.wait(100);
	}
	
	// -----------------------------------------------------------------
	
	private void mapAndUpdate(double xc, double yc) {
		this.mapping = new LogPolarMapping2(xc, yc, P, Q, rmax, rmin).getInverse();
		new ImageMapper(mapping).map(sourceIp, targetIp);		
		targetIm.show();
		targetIm.updateAndDraw();
		if (ShowSamplingGrid) {
			sourceIm.setOverlay(getSupportRegionOverlay(xc, yc));
			sourceIm.updateAndDraw();
		}
	}

	void finish() {
		sourceIm.setTitle(title);
		sourceCv.removeMouseListener(this);
	}
	
	// --------- generate source grid overlay ---------------
	
	private Overlay getSupportRegionOverlay(double xc, double yc) {
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		ColoredStroke stroke = new ColoredStroke(OverlayStrokeWidth, OverlayColorChoice.getColor());
		ola.setStroke(stroke);
		
		for (int i = 0; i < P; i++) {
			ola.addShape(makeCircle(xc, yc, i));
		}	
		for (int j = 0; j < Q; j++) {
			ola.addShape(makeSpoke(xc, yc, j));
		}
		
		return ola.getOverlay();
	}
	
	
	private Shape makeCircle(double xc, double yc, int i) {
		Path2D path = new Path2D.Double();
		Pnt2d start = mapping.applyTo(Pnt2d.from(i, 0));
		path.moveTo(start.getX(), start.getY());
		for (int j = 1; j <= Q; j++) {
			Pnt2d pnt = mapping.applyTo(Pnt2d.from(i, j % Q));
			path.lineTo(pnt.getX(), pnt.getY());
		}
		return path;
	}
	
	private Shape makeSpoke(double xc, double yc, int j) {
		Path2D path = new Path2D.Double();
		Pnt2d outer = mapping.applyTo(Pnt2d.from(P - 1, j));
		Pnt2d inner = mapping.applyTo(Pnt2d.from(0, j));
		path.moveTo(outer.getX(), outer.getY());
		path.lineTo(inner.getX(), inner.getY());
		return path;
	}
	
	// --------- mouse event handling --------------------
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isControlDown()) {
			finish();
		}
		else {
			double xc = sourceCv.offScreenXD(e.getX());
			double yc = sourceCv.offScreenYD(e.getY());
			//IJ.log("Mouse pressed: " + xc +","+yc);
			//IJ.log("Mag = " + canvas.getMagnification());
			mapAndUpdate(xc, yc);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
	// ----------------------------------------------------
	
	boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addNumericField("Radial steps (P)", P, 0);
		gd.addNumericField("Angular steps (Q) ", Q, 0);
		gd.addNumericField("Max. radius (rmax)", rmax, 1);
		gd.addNumericField("Min. radius (rmin)", rmin, 1);
		gd.addCheckbox("Draw sampling grid", ShowSamplingGrid);
		gd.addEnumChoice("Overlay color", OverlayColorChoice);
		
		gd.addMessage("Click left in source image to start,\n" +
					  "click ctrl+left to terminate.");
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		P = (int) gd.getNextNumber();
		Q = (int) gd.getNextNumber();
		rmax = gd.getNextNumber();
		rmin = gd.getNextNumber();
		ShowSamplingGrid = gd.getNextBoolean();
		OverlayColorChoice = gd.getNextEnumChoice(BasicAwtColor.class);
		return true;
	}
}
