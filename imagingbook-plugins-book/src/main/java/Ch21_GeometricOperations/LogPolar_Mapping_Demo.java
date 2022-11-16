package Ch21_GeometricOperations;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Path2D;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.ImageCanvas;
import ij.gui.Overlay;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.CssColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.Mapping2D;
import imagingbook.common.geometry.mappings.nonlinear.LogPolarMapping1;
import imagingbook.common.geometry.mappings.nonlinear.LogPolarMapping2;
import imagingbook.common.image.ImageMapper;

/**
 * <p>
 * ImageJ plugin demonstrating the use of 2D log-polar mapping. Two mapping
 * types are available (Version1, Version2). See Sec. 21.1.6 of [1] for details
 * and examples. The plugin works interactively. Once started, the current mouse
 * position specifies the transformation's center point. A new image with
 * radius/angle coordinates is opened and continuously updated, until the plugin
 * is terminated. The circular source grid is displayed as a graphic overlay.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/16
 */
public class LogPolar_Mapping_Demo implements PlugInFilter, MouseListener { // TODO: convert to ShapeOverlayAdapter, revise termination (escape)
	
	private enum MappingType {
		Version1, // simple log-polar mapping with no rmin (maps [0, rmax] -> [0,nr])
		Version2; // improved log-polar mapping with rmin (maps [rmin, rmax] -> [0,nr])
	}
	
	private static boolean ShowSamplingGrid = true;	
	private static MappingType MappingVersion = MappingType.Version1;	
	private static int P = 60;		// number of radial steps
	private static int Q = 100;		// number of angular steps
	
	private static CssColor OverlayColorChoice = CssColor.DodgerBlue; //Color.green;
	private static float OverlayStrokeWidth = 0.1f;
	
	private double rmax, rmin;
	private ImagePlus sourceIm;
	private ImageCanvas sourceCv;
	private ImageProcessor sourceIp;
	private ImageProcessor targetIp;
	private ImagePlus targetIm;	
	private Mapping2D mapping;
	private String sourceTitle;
	private Color OverlayColor;
	
	// -----------------------------------------------------------------

	@Override
	public int setup(String arg, ImagePlus im) {
		this.sourceIm = im;
		return DOES_ALL;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		rmax = ip.getWidth() / 2;
		rmin = rmax / Math.exp(2 * Math.PI * (P - 1) / Q);
		
		if (!getUserInput()) {
			return;
		}
		
		this.OverlayColor = OverlayColorChoice.getColor();
		this.sourceIp = ip;
		this.targetIp = sourceIp.createProcessor(P, Q);
		this.targetIm = new ImagePlus("Log Polar Image", targetIp);
		this.sourceCv = sourceIm.getWindow().getCanvas();
		this.sourceCv.addMouseListener(this);
		//sourceCv.addMouseMotionListener(this);
		this.sourceIm.setOverlay(null);
		this.sourceTitle = sourceIm.getTitle();
		this.sourceIm.setTitle(sourceTitle + " [RUNNING]");
		this.sourceIm.updateAndRepaintWindow();
		IJ.wait(100);
	}
	
	// -----------------------------------------------------------------
	
	private void mapAndUpdate(double xc, double yc) {
		switch(MappingVersion) {
		case Version1:
			mapping = new LogPolarMapping1(xc, yc, P, Q, rmax).getInverse();
			break;
		case Version2:
			mapping = new LogPolarMapping2(xc, yc, P, Q, rmax, rmin).getInverse();
			break;
		}
		new ImageMapper(mapping).map(sourceIp, targetIp);		
		targetIm.show();
		targetIm.updateAndDraw();
		if (ShowSamplingGrid) {
			sourceIm.setOverlay(getSupportRegionOverlay(xc, yc));
			sourceIm.updateAndDraw();
		}
	}

	void finish() {
		sourceIm.setTitle(sourceTitle);
		sourceCv.removeMouseListener(this);
	}
	
	// --------- generate source grid overlay ---------------
	
	private Overlay getSupportRegionOverlay(double xc, double yc) {
		Overlay oly = new Overlay();
		for (int i = 0; i < P; i++) {
			ShapeRoi roi = makeCircle(xc, yc, i);
			roi.setStrokeWidth(OverlayStrokeWidth);
			roi.setStrokeColor(OverlayColor);
			oly.add(roi);
		}		
		for (int j = 0; j < Q; j++) {
			ShapeRoi roi = makeSpoke(xc, yc, j);
			roi.setStrokeWidth(OverlayStrokeWidth);
			roi.setStrokeColor(OverlayColor);
			oly.add(roi);
		}
		return oly;
	}
	
	private ShapeRoi makeCircle(double xc, double yc, int i) {
		Path2D path = new Path2D.Double();
		Pnt2d start = mapping.applyTo(Pnt2d.from(i, 0));
		path.moveTo(start.getX(), start.getY());
		for (int j = 1; j <= Q; j++) {
			Pnt2d pnt = mapping.applyTo(Pnt2d.from(i, j % Q));
			path.lineTo(pnt.getX(), pnt.getY());
		}
		return new ShapeRoi(path);
	}
	
	private ShapeRoi makeSpoke(double xc, double yc, int j) {
		Path2D path = new Path2D.Double();
		Pnt2d outer = mapping.applyTo(Pnt2d.from(P - 1, j));
		Pnt2d inner   = mapping.applyTo(Pnt2d.from(0, j));
		path.moveTo(outer.getX(), outer.getY());
		path.lineTo(inner.getX(), inner.getY());
		return new ShapeRoi(path);
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
	
	boolean getUserInput() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Mapping type", MappingVersion);
		gd.addNumericField("Radial steps (P)", P, 0);
		gd.addNumericField("Angular steps (Q) ", Q, 0);
		gd.addNumericField("Max. radius (rmax)", rmax, 2);
		gd.addNumericField("Min. radius (rmin)", rmin, 2);
		gd.addCheckbox("Draw sampling grid", ShowSamplingGrid);
		gd.addEnumChoice("Overlay color", OverlayColorChoice);
		
		gd.addMessage("Click left in source image to start,\n" +
					  "click ctrl+left to terminate.");
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		MappingVersion = gd.getNextEnumChoice(MappingType.class);
		P = (int) gd.getNextNumber();
		Q = (int) gd.getNextNumber();
		rmax = gd.getNextNumber();
		rmin = gd.getNextNumber();
		ShowSamplingGrid = gd.getNextBoolean();
		OverlayColorChoice = gd.getNextEnumChoice(CssColor.class);
		return true;
	}
}
