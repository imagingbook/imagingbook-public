package Ch21_GeometricOperations;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.image.access.ImageAccessor;

/**
 * Projects a triangular grid onto the image which is mouse-editable, i.e., grid
 * points can be selected and dragged. The grid is drawn as an overlay.
 * In each repaint, each pixel of the image is re-calculated by 
 * (a) finding the containing (distorted) triangle of the pixel,
 * (b) calculating the affine mapping w.r.t. the original (undistorted) triangle,
 * (c) inverse mapping the pixel coordinate to the original (source) image and
 * (d) retrieving the interpolated pixel value from the original image.
 * 
 * TODO: Lots! The remapping process is currently brute force and can be improved
 * in various ways to make it much faster:
 * + all triangles and affine transformations should be pre-calculated.
 * + no need to repaint the image for every mouse drag, only when mouse is released (done)
 * (but redraw the overlay all the time).
 * + only modified patches (triangles) need to be recalculated(transformations) and re-rendered
 * + do we need to recreate the overlay every time or could it be locally modified?
 * + allow other types of triangulations (e.g., see https://weltbild.scene7.com/asset/vgwjo/vgw/brettspiele-der-welt-254350853.jpg)
 * + make the outer points movable too
 * + enlarge canvas, add margins
 * + implement the grid as a (flexible) separate class,
 * + use {@link ImageAccessor} for interpolation.
 * 
 * @author WB
 * @version 2021/10/05
 *
 */
public class Mesh_Warp_Interactive implements PlugInFilter, MouseListener, MouseMotionListener, KeyListener {

	private static final int R = 10;	// number of grid rows
	private static final int C = 10;	// number of grid columns
	private static final String PropertyID = Mesh_Warp_Interactive.class.getName();
	
	private static Color StrokeColor = Color.blue;
	private static float StrokeWidth = 0.2f;
	private static boolean ShowTriangles = true;

	private ImagePlus im;
	private ImageWindow win;
	private ImageCanvas canvas;
	private String title;
	
	// event handling
	static String EditString = " (editing)";
	private KeyListener[] windowKeyListeners = null;
	private MouseListener[] canvasMouseListeners = null;
	private KeyListener[] canvasKeyListeners = null;
	private MouseMotionListener[] canvasMouseMotionListeners = null;
	

	private double[][][] grid;		// grid[row][col][xy]
	private double[][][] gridOrig;
	
	private double catchRadius = 4;
	
	private int rSelect = -1;			// the currently selected grid point (row)
	private int cSelect = -1;			// the currently selected grid point (column)
//	private double srOrig = 0;
//	private double scOrig = 0; 		// original X/Y-coordinates when dragging started
	
	private Path2D.Double enclosingPolygon = null;
	private boolean nodeSelected = false;
	
	private ImageProcessor ipOrig = null;
	
//	boolean dragging = false;
	
	String infoText = 
		"How to use:\n" +
		"   Left mouse: select and drag grid points\n" +
		"   Right mouse: reset the grid\n" +
		"   Ctrl+ key: zoom in\n" +
		"   Ctrl- key; zoom out\n" +
		"   Enter or Escape key: finish";

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (im.getProperty(PropertyID) != null) {
			IJ.error("Plugin is already running, finish first!");
			return;
		}
		
		this.win = this.im.getWindow();
		this.canvas = this.win.getCanvas();
		this.title = this.im.getTitle();
		
		IJ.log(infoText);
		
		ipOrig = ip.duplicate();	// keep a copy of the original image
		ipOrig.setInterpolate(true);
		ipOrig.setInterpolationMethod(ImageProcessor.BICUBIC);
		
		String title = im.getTitle();
		im.setProperty(PropertyID, "running");
		im.setTitle(title + EditString);
		installListeners();
		
		reset();
		repaint();
		IJ.wait(200);
	}
	
	// ---------------------------------------------------------------
	
	private void reset() {
		grid = makeGridPoints(im.getWidth(), im.getHeight());
		gridOrig = makeGridPoints(im.getWidth(), im.getHeight());
		rSelect = -1;
		cSelect = -1;
		nodeSelected = false;
		enclosingPolygon = null;
	}
	
	private void finish() {
		//IJ.log("finished!");
		removeListeners();
		ipOrig = null;
		//im.setOverlay(null);
		im.setTitle(title);
		nodeSelected = false;
		enclosingPolygon = null;
		repaint();
		im.setProperty(PropertyID, null);
	}
	
	// ---------------------------------------------------------------
	
	private void repaint() {
		//IJ.log("**** repaint ****");
		//remapImage(im.getProcessor());
		Overlay oly = makeGridOverlay(grid);
		oly.translate(0.5, 0.5);
		im.setOverlay(oly);
	}
	
	private double[][][] makeGridPoints(int w, int h) {
		double[][][] pnts = new double[R][C][2];
		// insert equally spaced points over image width/height
		for (int r = 0; r < R; r++) {
			double y = (double) r * (h - 1) / (R - 1);
			for (int c = 0; c < C; c++) {
				double x = (double) c * (w - 1) / (C - 1);
				pnts[r][c][0] = x;
				pnts[r][c][1] = y;
			}
		}
		return pnts;
	}
	
	private Overlay makeGridOverlay(double[][][] pnts) {
		Overlay oly = new Overlay();
		
		final float one = 1.0f;
		final float two = 2.0f;
		GeneralPath gridPath = new GeneralPath();
		// draw vertices
		for (int r = 0; r < pnts.length; r++) {
			for (int c = 0; c < pnts[r].length; c++) {
				float x = (float) pnts[r][c][0];
				float y = (float) pnts[r][c][1];
				gridPath.moveTo(x - two, y - one);
				gridPath.lineTo(x - two, y + one);
				gridPath.moveTo(x + two, y - one);
				gridPath.lineTo(x + two, y + one);
				gridPath.moveTo(x - one, y - two);
				gridPath.lineTo(x + one, y - two);
				gridPath.moveTo(x - one, y + two);
				gridPath.lineTo(x + one, y + two);
			}
		}
		// draw horizontal grid lines
		for (int r = 0; r < pnts.length; r++) {
			gridPath.moveTo((float) pnts[r][0][0], (float) pnts[r][0][1]);
			for (int c = 1; c < pnts[r].length; c++) {
				gridPath.lineTo((float) pnts[r][c][0], (float) pnts[r][c][1]);
			}
		}		
		// draw vertical grid lines
		for (int r = 0; r < pnts[0].length; r++) {
			gridPath.moveTo((float) pnts[0][r][0], (float) pnts[0][r][1]);
			for (int c = 1; c < pnts.length; c++) {
				gridPath.lineTo((float) pnts[c][r][0], (float) pnts[c][r][1]);
			}
		}
		// draw diagonal grid lines
		if (ShowTriangles) {
			for (int r = 0; r < pnts.length - 1; r++) {
				for (int c = 0; c < pnts[0].length - 1; c++) {
					gridPath.moveTo((float) pnts[r][c][0], (float) pnts[r][c][1]);
					gridPath.lineTo((float) pnts[r + 1][c + 1][0], (float) pnts[r + 1][c + 1][1]);
				}
			}
		}
		
		Roi gridRoi = new ShapeRoi(gridPath);
		gridRoi.setStrokeColor(StrokeColor);
		gridRoi.setStrokeWidth(StrokeWidth);
		oly.add(gridRoi);
		
		// mark the selected grid point
		if (nodeSelected) {
			double w = 2;
			double xs = grid[rSelect][cSelect][0];	// gridpoints[sY][sX].getX();
			double ys = grid[rSelect][cSelect][1];	// gridpoints[sY][sX].getY();
			OvalRoi oval = new OvalRoi(xs - w, ys - w, 2 * w, 2 * w);
			oval.setFillColor(Color.green);
			oly.add(oval);
		}
		
		// mark the enclosing polygon (if exists)
		if (nodeSelected && enclosingPolygon != null) {
			Roi polyRoi = new ShapeRoi(enclosingPolygon);
			polyRoi.setStrokeColor(StrokeColor);
			polyRoi.setStrokeWidth(1.0);
			oly.add(polyRoi);
		}
		
		return oly;
	}
	
	// move the currently selected grid point to new position
	private void moveSelectedGridPoint(double newX, double newY) {
		//if (selected) {	// only move if a grid point is currently selected
		if (rSelect >= 0 && rSelect < R && cSelect >= 0 && cSelect < C) {
			grid[rSelect][cSelect][0] = newX;
			grid[rSelect][cSelect][1] = newY;
		}
	}
	
	boolean selectGridPoint(int clickX, int clickY) {
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				double dist = dist(grid[r][c], clickX, clickY); // gridpoints[v][h].distance(clickX, clickY);
				if (dist <= catchRadius) {
					rSelect = r;
					cSelect = c;
//					scOrig = grid[v][h][0];	// keep original coordinates for limiting motion (test only)
//					srOrig = grid[v][h][1];
					
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns a closed ploygon for the grid point (v, h)
	 * @param r selected center (row) coordinate
	 * @param c selected center (column) coordinate
	 * @return
	 */
	private Path2D.Double getEnclosingPolygon(int r, int c) {
		//IJ.log("makeEnclosingPolygon " + r + " / " + c);
		if (0 < r && r < R-1 && 0 < c && c < C-1) {	// currently only inner points have polygons
			Path2D.Double path = new Path2D.Double();
			path.moveTo(grid[r][c+1][0], grid[r][c+1][1]);  	// 0
//			path.lineTo(gridpoints[v-1][h+1][0], gridpoints[v-1][h+1][1]);  // 1
			path.lineTo(grid[r-1][c][0], grid[r-1][c][1]);  	// 2
			path.lineTo(grid[r-1][c-1][0], grid[r-1][c-1][1]);  // 3
			path.lineTo(grid[r][c-1][0], grid[r][c-1][1]);  	// 4
//			path.lineTo(gridpoints[v+1][h-1][0], gridpoints[v+1][h-1][1]);  // 5
			path.lineTo(grid[r+1][c][0], grid[r+1][c][1]);  	// 6
			path.lineTo(grid[r+1][c+1][0], grid[r+1][c+1][1]);  // 7
			path.closePath();
			return path;
		}
		else {
			return null;
		}
	}
	
	double dist(double x1, double y1, double x2, double y2) {
		final double dx = x1 - x2;
		final double dy = y1 - y2;
		return Math.hypot(dx, dy);
	}
	
	double dist(double[] pnt, double cX, double cY) {
		return dist(pnt[0], pnt[1], cX, cY);
	}
	
	// -----------------------------------------------------------------------------
	
	/**
	 * Returns an array of grid row/column positions (r1, c1, r2, c2, r3, c3) or null 
	 * @param x
	 * @param y
	 * @return
	 */
	int[] findEnclosingTriangle(double x, double y) {
		//IJ.log(String.format("findEnclosingTriangle for (%.2f,%.2f)", x, y));
		for (int r = 0; r < R-1; r++) {
			for (int c = 0; c < C-1; c++) {
				double[] p0 = grid[r][c];
				double[] p1 = grid[r+1][c];
				double[] p2 = grid[r+1][c+1];
				double[] p3 = grid[r][c+1];
//				IJ.log("  p0 = " + Arrays.toString(p0));
//				IJ.log("  p1 = " + Arrays.toString(p1));
//				IJ.log("  p2 = " + Arrays.toString(p2));
//				IJ.log("  p3 = " + Arrays.toString(p3));
				// test lower triangle A
				if (makeTriangle(p0, p1, p2).contains(x, y)) {
					return new int[] { r, c, r+1, c, r+1, c+1 };
				}
				// test upper triangle B
				if (makeTriangle(p0, p2, p3).contains(x, y)) {
					return new int[] { r, c, r+1, c+1, r, c+1 };
				}
			}
		}	
		return null;
	}
	
//	Point2D[] toPointArray(double[]... coords) {
//		Point2D[] pnts = new Point2D[coords.length];
//		for (int i = 0; i < coords.length; i++) {
//			pnts[i] = new Point2D.Double(coords[i][0], coords[i][1]);
//		}
//		return pnts;
//	}
	
	private Path2D.Double makeTriangle(double[] pa, double[] pb, double[] pc) {
		Path2D.Double path = new Path2D.Double();
		path.moveTo(pa[0], pa[1]);
		path.lineTo(pb[0], pb[1]);
		path.lineTo(pc[0], pc[1]);
		path.closePath();
		return path;
	}
	
	
	// TODO: perform selectively only for modified triangles!
	// iterate over triangles!
	private void remapImage(ImageProcessor ip) {
		int width = im.getWidth();
		int height = im.getHeight();
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				if (enclosingPolygon == null || enclosingPolygon.contains(u, v)) {
				// remap pixel (u,v)
					int[] tri = findEnclosingTriangle(u, v);
					if (tri != null) {
						int r1 = tri[0], c1 = tri[1];
						int r2 = tri[2], c2 = tri[3];
						int r3 = tri[4], c3 = tri[5];
						Pnt2d A1 = getGridPointPos(grid, r1, c1);
						Pnt2d A2 = getGridPointPos(grid, r2, c2);
						Pnt2d A3 = getGridPointPos(grid, r3, c3);
						Pnt2d B1 = getGridPointPos(gridOrig, r1, c1);
						Pnt2d B2 = getGridPointPos(gridOrig, r2, c2);
						Pnt2d B3 = getGridPointPos(gridOrig, r3, c3);
						Pnt2d[] A = {A1, A2, A3};
						Pnt2d[] B = {B1, B2, B3};
						AffineMapping2D am = AffineMapping2D.fromPoints(A, B);
						//IJ.log("    affine mapping = " + am.toString());
						Pnt2d sourceXY = am.applyTo(PntInt.from(u, v));
						//IJ.log(String.format("remapImage(): (%d,%d) -> (%.2f,%.2f)", u, v, sourceXY[0], sourceXY[1]));
						int val = ipOrig.getPixelInterpolated(sourceXY.getX(), sourceXY.getY());
						ip.putPixel(u, v, val);
					}
				}
			}
		}
		
	}
	
	Pnt2d getGridPointPos(double[][][] grid, int r, int c) {
		return PntDouble.from(grid[r][c][0], grid[r][c][1]);
	}
	

	// ------------------------------------------------------------
	// EVENT HANDLING below
	// ----------------------------------------------------------------
	
	private void installListeners() {	
		windowKeyListeners = removeKeyListeners(win);
		canvasKeyListeners = removeKeyListeners(canvas);
		canvasMouseListeners = removeMouseListeners(canvas);
		canvasMouseMotionListeners = removeMouseMotionListeners(canvas);
			
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		
		canvas.requestFocus();	// important, otherwise key events have no effect!!
	}
	
	private void removeListeners() {
		//IJ.log("Revert Listeners:");
		if (win != null) {
			// remove this plugin's listener(s)
			removeKeyListeners(win);
			// install original listener(s)
			addKeyListeners(win, windowKeyListeners);
		}

		if (canvas != null) {
			// remove this plugin's listener(s)
			removeKeyListeners(canvas);
			removeMouseListeners(canvas);
			removeMouseMotionListeners(canvas);
			// install original listener(s)
			addKeyListeners(canvas, canvasKeyListeners);
			addMouseListeners(canvas, canvasMouseListeners);
			addMouseMotionListeners(canvas, canvasMouseMotionListeners);
		}
	}
	
	// ----------------------------------------------------------------

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int offscreenX = canvas.offScreenX(x);
		int offscreenY = canvas.offScreenY(y);
		//IJ.log("Mouse pressed: " + offscreenX + "," + offscreenY);
		if ((e.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0) {	// if ((e.getModifiers() & Event.META_MASK) != 0)
			//IJ.log("RIGHT Mouse pressed");
			reset();
			repaint();
		}
		else {
			nodeSelected = selectGridPoint(offscreenX, offscreenY);
			if (nodeSelected) {
				enclosingPolygon = getEnclosingPolygon(rSelect, cSelect);
			}
			else if (nodeSelected) {
				rSelect = cSelect = -1;
			}
			repaint();
		}
		e.consume();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//IJ.log("mouseReleased: ");
		remapImage(im.getProcessor());
		repaint();
		if (e.getClickCount() == 2 && !e.isConsumed()) {
		     e.consume();
		     //IJ.log("DOUBLE CLICK!!");
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int offscreenX = canvas.offScreenX(e.getX());
		int offscreenY = canvas.offScreenY(e.getY());
		//IJ.log("Mouse dragged to: " + offscreenX + "," + offscreenY);
		if (nodeSelected) {
//			if (dist(offscreenX, offscreenY, sXorig, sYorig) < 10) {
			if (enclosingPolygon != null && enclosingPolygon.contains(offscreenX, offscreenY)) {
			//if (enclosingPolygon != null) {
				moveSelectedGridPoint(offscreenX, offscreenY);
				repaint();
			}
			else {
				//IJ.log("dragging limited");
			}
		}
		else {
			//IJ.log("dragging but no node selected");
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {	}
	@Override
	public void mouseClicked(MouseEvent e) { }
	@Override
	public void mouseEntered(MouseEvent e) { }
	@Override
	public void mouseMoved(MouseEvent e) { }
	
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		String keyText = KeyEvent.getKeyText(keyCode);
		IJ.log(String.format("keyPressed: keyCode=%d (%s)", keyCode , keyText));
		
		if(keyCode == KeyEvent.VK_ESCAPE) {
			IJ.log("*** ESCAPE key pressed!");
			finish();
		}
		else if(keyCode == KeyEvent.VK_ENTER) {
			IJ.log("*** ENTER key pressed!");
			finish();
		}
		else if(keyCode == KeyEvent.VK_PLUS && e.isControlDown()) {
			IJ.log("CTLR-plus pressed!");
			canvas.zoomIn(im.getWidth()/2, im.getHeight()/2);
		}
		else if(keyCode == KeyEvent.VK_MINUS && e.isControlDown()) {
			IJ.log("CTLR-minus pressed!");
			canvas.zoomOut(im.getWidth()/2, im.getHeight()/2);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//IJ.log("*** SOME key released!");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//IJ.log("*** SOME key typed!");	
	}
	
	// ------------------------------------------------------
	
//	private void listKeyListeners(Component comp) {
//		Class<?> ijcls = IJ.getInstance().getClass();
//		KeyListener[] listeners = comp.getKeyListeners();
//		IJ.log("Key listeners in " + comp.getClass().getName());
//		for (KeyListener kl : listeners) {
//			IJ.log("  key listener " + kl.getClass().getName() + " " + ijcls.isInstance(kl) + " " + (kl == IJ.getInstance()));
//		}
//	}
	
	private KeyListener[] removeKeyListeners(Component comp) {
		KeyListener[] listeners = comp.getKeyListeners();
		for (KeyListener kl : listeners) {
			//IJ.log("  removing key listener: " + kl.getClass().getName() + " from " + comp.getClass().getName());
			comp.removeKeyListener(kl);
		}
		return listeners;
	}
	
	private MouseListener[] removeMouseListeners(Component comp) {
		MouseListener[] listeners = comp.getMouseListeners();
		for (MouseListener ml : listeners) {
			//IJ.log("  removing mouse listener " + ml + " from " + comp);
			comp.removeMouseListener(ml);
		}
		return listeners;
	}
	
	private MouseMotionListener[] removeMouseMotionListeners(Component comp) {
		MouseMotionListener[] listeners = comp.getMouseMotionListeners();
		for (MouseMotionListener ml : listeners) {
			//IJ.log("  removing mouse listener " + ml + " from " + comp);
			comp.removeMouseMotionListener(ml);
		}
		return listeners;
	}
	
	private void addKeyListeners(Component comp, KeyListener[] listeners) {
		if (comp == null || listeners == null) return;
		for (KeyListener kl : listeners) {
			//IJ.log("  adding key listener: " + kl.getClass().getName() + " to " + comp.getClass().getName());
			comp.addKeyListener(kl);
		}
	}
	
	private void addMouseListeners(Component comp, MouseListener[] listeners) {
		if (comp == null || listeners == null) return;
		for (MouseListener ml : listeners) {
			comp.addMouseListener(ml);
		}
	}
	
	private void addMouseMotionListeners(Component comp, MouseMotionListener[] listeners) {
		if (comp == null || listeners == null) return;
		for (MouseMotionListener ml : listeners) {
			comp.addMouseMotionListener(ml);
		}
	}

}
