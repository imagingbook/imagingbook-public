package Ch21_GeometricOperations;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;

/**
 * <p>
 * ImageJ plugin, performs piecewise affine transformation by triangulation of
 * the input image, as described in Sec. 21.1.8 (see Fig. 21.13) of [1].
 * </p>
 * <p>
 * The plugin projects a triangular grid onto the image which is mouse-editable,
 * i.e., grid points can be selected and dragged. Note that the outer grid
 * points (along the image border) cannot be moved. The grid is drawn as a
 * graphic overlay. In each repaint iteration, pixel values are re-calculated by
 * </p>
 * <ol>
 * <li>finding the containing (distorted) triangle of the pixel,</li>
 * <li>calculating the affine mapping w.r.t. the original (undistorted)
 * triangle,</li>
 * <li>inverse mapping the pixel coordinate to the original (source) image
 * and</li>
 * <li>retrieving the interpolated pixel values from the original image.</li>
 * </ol>
 * <p>
 * The following mouse and keypoint events are observed:
 * </p>
 * <ul>
 * <li>left mouse botton: select and drag grid points,</li>
 * <li>right mouse botton: reset the grid,</li>
 * <li>ctrl/+ key: zoom in,</li>
 * <li>ctrl/- key; zoom out,</li>
 * <li>enter or escape key: finish editing.</li>
 * </ul>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/25
 */
public class Mesh_Warp_Interactive implements PlugInFilter, MouseListener, MouseMotionListener, KeyListener {
	
	/*
	Ideas for improvement: The remapping process is currently brute force and can be improved
	in various ways to make it much faster:
	+ all triangles and affine transformations should be pre-calculated.
	+ allow other types of triangulations (e.g., 
		see https://weltbild.scene7.com/asset/vgwjo/vgw/brettspiele-der-welt-254350853.jpg);
	+ make the outer points movable too
	+ enlarge canvas, add margins;
	+ implement the grid as a (flexible) separate class;
	+ use {@link ImageAccessor} for interpolation.
	*/

	private static final String PropertyKey = Mesh_Warp_Interactive.class.getName();
	private static final String EditString = " (editing)";
	
	private static int Rows = 10;	// number of grid rows
	private static int Cols = 10;	// number of grid columns
	private static BasicAwtColor StrokeColorChoice = BasicAwtColor.Blue;
	private static BasicAwtColor HighlightColorChoice = BasicAwtColor.Green;
	private static double StrokeWidth = 0.2;
	private static double CatchRadius = 3.0;
	private static boolean ShowTriangles = true;
	
	
	private ImagePlus im;
	private ImageWindow win;
	private ImageCanvas canvas;
	private String title;
	
	// event handling variables:
	private KeyListener[] windowKeyListeners = null;
	private MouseListener[] canvasMouseListeners = null;
	private KeyListener[] canvasKeyListeners = null;
	private MouseMotionListener[] canvasMouseMotionListeners = null;
	
	// grid points positions
	private double[][][] grid;				// grid[row][col][x/y]
	private double[][][] gridOrig;
	
	private Triangle[][][] triangles;		// triangles[row][col][A/B]
	private Triangle[][][] trianglesOrig;
	
	private int rSelect = -1;			// the currently selected grid point (row)
	private int cSelect = -1;			// the currently selected grid point (column)
	
	private Path2D.Double enclosingPolygon = null;
	private boolean nodeSelected = false;
	private ImageProcessor ipOrig = null;

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (im.getProperty(PropertyKey) != null) {
			IJ.error("Plugin is already running, finish first!");
			return;
		}
		
		if (!runDialog()) {
			return;
		}
		
		this.win = this.im.getWindow();
		this.canvas = this.win.getCanvas();
		this.title = this.im.getTitle();
		
		ipOrig = ip.duplicate();	// keep a copy of the original image
		ipOrig.setInterpolate(true);
		ipOrig.setInterpolationMethod(ImageProcessor.BICUBIC);
		
		String title = im.getTitle();
		im.setProperty(PropertyKey, "running");
		im.setTitle(title + EditString);
		setupListeners();
		
		reset();
		repaint();
		IJ.wait(200);
	}
	
	// ---------------------------------------------------------------
	
	private void reset() {
		initGridAndTriangles(im.getWidth(), im.getHeight());
		rSelect = -1;
		cSelect = -1;
		nodeSelected = false;
		enclosingPolygon = null;
	}
	
	private void finish() {
		revertListeners();
		ipOrig = null;
		im.setTitle(title);
		nodeSelected = false;
		enclosingPolygon = null;
		repaint();
		im.setProperty(PropertyKey, null);
	}
	
	// ---------------------------------------------------------------
	
	private void repaint() {
		Overlay oly = makeGridOverlay(grid);
		im.setOverlay(oly);
		im.updateAndDraw();
	}
	
	private void initGridAndTriangles(int w, int h) {
		gridOrig = new double[Rows][Cols][2];
		grid = new double[Rows][Cols][2];
		
		// insert equally spaced points over image width/height
		for (int r = 0; r < Rows; r++) {
			double y = (double) r * (h - 1) / (Rows - 1);
			for (int c = 0; c < Cols; c++) {
				double x = (double) c * (w - 1) / (Cols - 1);
				gridOrig[r][c][0] = grid[r][c][0] = x;
				gridOrig[r][c][1] = grid[r][c][1] = y;
			}
		}
		
		trianglesOrig = new Triangle[Rows - 1][Cols - 1][2];
		updateTriangles(trianglesOrig, gridOrig);
		
		triangles = new Triangle[Rows - 1][Cols - 1][2];
		updateTriangles(triangles, grid);
	}
	
	private Overlay makeGridOverlay(double[][][] pnts) {
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		ColoredStroke pathstroke = new ColoredStroke(StrokeWidth, StrokeColorChoice.getColor());
		ColoredStroke polystroke = new ColoredStroke(3 * StrokeWidth, StrokeColorChoice.getColor());
		ColoredStroke highlightstroke = 
				new ColoredStroke(StrokeWidth, HighlightColorChoice.getColor(), HighlightColorChoice.getColor());
		
		// draw the complete grid
		Path2D.Double gridPath = new Path2D.Double();
		
		// draw horizontal grid lines
		for (int r = 0; r < pnts.length; r++) {
			gridPath.moveTo(pnts[r][0][0], pnts[r][0][1]);
			for (int c = 1; c < pnts[r].length; c++) {
				gridPath.lineTo(pnts[r][c][0], pnts[r][c][1]);
			}
		}		
		// draw vertical grid lines
		for (int r = 0; r < pnts[0].length; r++) {
			gridPath.moveTo(pnts[0][r][0], pnts[0][r][1]);
			for (int c = 1; c < pnts.length; c++) {
				gridPath.lineTo(pnts[c][r][0], pnts[c][r][1]);
			}
		}
		// draw diagonal grid lines
		if (ShowTriangles) {
			for (int r = 0; r < pnts.length - 1; r++) {
				for (int c = 0; c < pnts[0].length - 1; c++) {
					gridPath.moveTo(pnts[r][c][0], pnts[r][c][1]);
					gridPath.lineTo(pnts[r + 1][c + 1][0], pnts[r + 1][c + 1][1]);
				}
			}
		}	
		ola.addShape(gridPath, pathstroke);
		
		// draw the vertices
		double rad = CatchRadius;
		for (int r = 0; r < pnts.length; r++) {
			for (int c = 0; c < pnts[r].length; c++) {
				double x = pnts[r][c][0];
				double y = pnts[r][c][1];
				Ellipse2D.Double circle = new Ellipse2D.Double(x - rad, y - rad, 2 * rad, 2 * rad);
				ola.addShape(circle, polystroke);
			}
				}
		
		// mark the selected grid point
		if (nodeSelected) {
			double xs = grid[rSelect][cSelect][0];	// gridpoints[sY][sX].getX();
			double ys = grid[rSelect][cSelect][1];	// gridpoints[sY][sX].getY();
			Ellipse2D.Double circle = new Ellipse2D.Double(xs - rad, ys - rad, 2 * rad, 2 * rad);
			ola.addShape(circle, highlightstroke);
		}
		
		// mark the enclosing polygon (if exists)
		if (nodeSelected && enclosingPolygon != null) {
			ola.addShape(enclosingPolygon, polystroke);
		}
		
		return ola.getOverlay();
	}
	
	// move the currently selected grid point to new position
	private void moveSelectedGridPoint(double newX, double newY) {
		//if (selected) {	// only move if a grid point is currently selected
		if (rSelect >= 0 && rSelect < Rows && cSelect >= 0 && cSelect < Cols) {
			grid[rSelect][cSelect][0] = newX;
			grid[rSelect][cSelect][1] = newY;
		}
	}
	
	private boolean selectGridPoint(int clickX, int clickY) {
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				double dist = dist(grid[r][c], clickX, clickY);
				if (dist <= CatchRadius) {
					rSelect = r;
					cSelect = c;
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns a closed polygon for the grid point (v, h)
	 * @param r selected center (row) coordinate
	 * @param c selected center (column) coordinate
	 * @return
	 */
	private Path2D.Double getEnclosingPolygon(int r, int c) {
		if (0 < r && r < Rows-1 && 0 < c && c < Cols-1) {	// currently only inner points have polygons
			Path2D.Double path = new Path2D.Double();
			path.moveTo(grid[r][c+1][0], grid[r][c+1][1]);  	// 0
			path.lineTo(grid[r-1][c][0], grid[r-1][c][1]);  	// 2
			path.lineTo(grid[r-1][c-1][0], grid[r-1][c-1][1]);  // 3
			path.lineTo(grid[r][c-1][0], grid[r][c-1][1]);  	// 4
			path.lineTo(grid[r+1][c][0], grid[r+1][c][1]);  	// 6
			path.lineTo(grid[r+1][c+1][0], grid[r+1][c+1][1]);  // 7
			path.closePath();
			return path;
		}
		else {
			return null;
		}
	}
	
	private double dist(double x1, double y1, double x2, double y2) {
		final double dx = x1 - x2;
		final double dy = y1 - y2;
		return Math.hypot(dx, dy);
	}
	
	private double dist(double[] pnt, double cX, double cY) {
		return dist(pnt[0], pnt[1], cX, cY);
	}
	
	// -----------------------------------------------------------------------------
	
	private Triangle findEnclosingTriangle(double x, double y) {
		for (int r = 0; r < Rows - 1; r++) {
			for (int c = 0; c < Cols - 1; c++) {
				for (int i = 0; i < 2; i++) {
					Triangle t = triangles[r][c][i];
					if (t.contains(x, y)) {
						return t;
					}
				}
			}
		}
		return null;	// no enclosing triangle found
	}
	
//	/**
//	 * Returns an array of grid row/column positions (r1, c1, r2, c2, r3, c3) or null 
//	 */
//	private int[] findEnclosingTriangle(double x, double y) {
//		for (int r = 0; r < Rows-1; r++) {
//			for (int c = 0; c < Cols-1; c++) {
//				int r0 = r, 	c0 = c;
//				int r1 = r+1, 	c1 = c;
//				int r2 = r+1, 	c2 = c+1;
//				int r3 = r, 	c3 = c+1;
//				
//				double[] p0 = grid[r0][c0];
//				double[] p1 = grid[r1][c1];
//				double[] p2 = grid[r2][c2];
//				double[] p3 = grid[r3][c3];
//				
//				// check lower triangle A:
//				Triangle tA = new Triangle(p0, p1, p2);
//				if (tA.contains(x, y)) {
//					return new int[] { r0, c0, r1, c1, r2, c2 };
//				}
//				// check upper triangle B:
//				Triangle tB = new Triangle(p0, p2, p3);
//				if (tB.contains(x, y)) {
//					return new int[] { r0, c0, r2, c2, r3, c3 };
//				}
//			}
//		}	
//		return null;	// no enclosing triangle found
//	}
	
//	Point2D[] toPointArray(double[]... coords) {
//		Point2D[] pnts = new Point2D[coords.length];
//		for (int i = 0; i < coords.length; i++) {
//			pnts[i] = new Point2D.Double(coords[i][0], coords[i][1]);
//		}
//		return pnts;
//	}
	
//	private Path2D.Double makeTriangle(double[] pa, double[] pb, double[] pc) {
//		Path2D.Double path = new Path2D.Double();
//		path.moveTo(pa[0], pa[1]);
//		path.lineTo(pb[0], pb[1]);
//		path.lineTo(pc[0], pc[1]);
//		path.closePath();
//		return path;
//	}
	
	
	// iterate over triangles!
//	private void remapImage(ImageProcessor ip) {
//		updateTriangles(triangles, grid);
//		int width = im.getWidth();
//		int height = im.getHeight();
//		// iterate over all pixels:
//		for (int u = 0; u < width; u++) {
//			for (int v = 0; v < height; v++) {
//				if (enclosingPolygon == null || enclosingPolygon.contains(u, v)) {
//					// remap pixel (u,v)
//					int[] tri = findEnclosingTriangle(u, v);
//					if (tri != null) {
//						int r0 = tri[0], c0 = tri[1];
//						int r1 = tri[2], c1 = tri[3];
//						int r2 = tri[4], c2 = tri[5];
//						Pnt2d p0 = getGridPointPos(grid, r0, c0);
//						Pnt2d p1 = getGridPointPos(grid, r1, c1);
//						Pnt2d p2 = getGridPointPos(grid, r2, c2);
//						Pnt2d q0 = getGridPointPos(gridOrig, r0, c0);
//						Pnt2d q1 = getGridPointPos(gridOrig, r1, c1);
//						Pnt2d q2 = getGridPointPos(gridOrig, r2, c2);
//						Pnt2d[] P = {p0, p1, p2};
//						Pnt2d[] Q = {q0, q1, q2};
//						// find affine transformation from original triangle:
//						AffineMapping2D am = AffineMapping2D.fromPoints(P, Q);
//						Pnt2d xy = am.applyTo(PntInt.from(u, v));	// source image position
//						int val = ipOrig.getPixelInterpolated(xy.getX(), xy.getY());
//						ip.set(u, v, val);
//					}
//				}
//			}
//		}
//	}
	
	private void remapImage(ImageProcessor ip) {
		updateTriangles(triangles, grid);
		int width = im.getWidth();
		int height = im.getHeight();
		// iterate over all pixels:
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				if (enclosingPolygon == null || enclosingPolygon.contains(u, v)) {
					// find the containing triangle in 'triangles' (if any)
					Triangle tri = findEnclosingTriangle(u, v);
					if (tri != null) {
						// where to get the associated original triangle from?
						int r0 = tri[0], c0 = tri[1];
						int r1 = tri[2], c1 = tri[3];
						int r2 = tri[4], c2 = tri[5];
						Pnt2d p0 = getGridPointPos(grid, r0, c0);
						Pnt2d p1 = getGridPointPos(grid, r1, c1);
						Pnt2d p2 = getGridPointPos(grid, r2, c2);
						Pnt2d q0 = getGridPointPos(gridOrig, r0, c0);
						Pnt2d q1 = getGridPointPos(gridOrig, r1, c1);
						Pnt2d q2 = getGridPointPos(gridOrig, r2, c2);
						Pnt2d[] P = {p0, p1, p2};
						Pnt2d[] Q = {q0, q1, q2};
						// find affine transformation from original triangle:
						AffineMapping2D am = AffineMapping2D.fromPoints(P, Q);
						Pnt2d xy = am.applyTo(PntInt.from(u, v));	// source image position
						int val = ipOrig.getPixelInterpolated(xy.getX(), xy.getY());
						ip.set(u, v, val);
					}
				}
			}
		}
	}
	
	private void updateTriangles(Triangle[][][] theTriangles, double[][][] theGrid) {
		for (int r = 0; r < Rows - 1; r++) {
			for (int c = 0; c < Cols - 1; c++) {
				int r0 = r, 	c0 = c;
				int r1 = r+1, 	c1 = c;
				int r2 = r+1, 	c2 = c+1;
				int r3 = r, 	c3 = c+1;
				
				double[] p0 = theGrid[r0][c0];
				double[] p1 = theGrid[r1][c1];
				double[] p2 = theGrid[r2][c2];
				double[] p3 = theGrid[r3][c3];
				
				theTriangles[r][c][0] = new Triangle(p0, p1, p2);	// triangle A
				theTriangles[r][c][1] = new Triangle(p0, p2, p3);	// triangle B
			}
		}
	}
	
	private Pnt2d getGridPointPos(double[][][] grid, int r, int c) {
		return PntDouble.from(grid[r][c][0], grid[r][c][1]);
	}
	

	// ------------------------------------------------------------
	// EVENT HANDLING:
	// ----------------------------------------------------------------
	
	private void setupListeners() {
		// remove current listeners and keep for later re-install
		windowKeyListeners = removeKeyListeners(win);
		canvasKeyListeners = removeKeyListeners(canvas);
		canvasMouseListeners = removeMouseListeners(canvas);
		canvasMouseMotionListeners = removeMouseMotionListeners(canvas);
			
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
//		win.addWindowListener(this);
		
		win.addWindowListener(new WindowAdapter() {  
            @Override
			public void windowClosing(WindowEvent e) {  
                finish();
            }  
        });
		
		canvas.requestFocus();	// important, otherwise key events have no effect!!
	}
	
	private void revertListeners() {
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
		if(keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_ENTER) {
			finish();
		}
		else if(keyCode == KeyEvent.VK_PLUS && e.isControlDown()) {
			canvas.zoomIn(im.getWidth()/2, im.getHeight()/2);
		}
		else if(keyCode == KeyEvent.VK_MINUS && e.isControlDown()) {
			canvas.zoomOut(im.getWidth()/2, im.getHeight()/2);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) { }

	@Override
	public void keyTyped(KeyEvent e) { }
	
	// ------------------------------------------------------
	
	private KeyListener[] removeKeyListeners(Component comp) {
		KeyListener[] listeners = comp.getKeyListeners();
		for (KeyListener kl : comp.getKeyListeners()) {
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
	
	@SuppressWarnings("serial")
	class Triangle extends Path2D.Double {
		
		final Pnt2d pa, pb, pc;
		
		Triangle(double[] pa, double[] pb, double[] pc) {
			this.pa = Pnt2d.from(pa);
			this.pb = Pnt2d.from(pb);
			this.pc = Pnt2d.from(pc);
			this.moveTo(pa[0], pa[1]);
			this.lineTo(pb[0], pb[1]);
			this.lineTo(pc[0], pc[1]);
			this.closePath();
		}
		
		Triangle(Pnt2d pa, Pnt2d pb, Pnt2d pc) {
			this.pa = pa;
			this.pb = pb;
			this.pc = pc;
			this.moveTo(pa.getX(), pa.getY());
			this.lineTo(pb.getX(), pb.getY());
			this.lineTo(pc.getX(), pc.getY());
			this.closePath();
		}
	}
	
	// -------------------------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		
		gd.addMessage(DialogUtils.makeLineSeparatedString(
				"How to use:",
				"      left mouse: select and drag grid points",
				"      right mouse: reset the grid",
				"      ctrl/+ key: zoom in\n",
				"      ctrl/- key: zoom out\n",
				"      enter or escape key: finish editing"));
		
		gd.addNumericField("Number of grid rows", Rows, 0);
		gd.addNumericField("Number of grid columns", Cols, 0);
		gd.addEnumChoice("Grid stroke color", StrokeColorChoice);
		gd.addEnumChoice("Point highlight color", HighlightColorChoice);
		gd.addNumericField("Grid stroke width", StrokeWidth, 1);
		gd.addNumericField("Catch radius", CatchRadius, 1);
		gd.addCheckbox("Show grid triangles", ShowTriangles);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		Rows = (int) gd.getNextNumber();
		Cols = (int) gd.getNextNumber();
		StrokeColorChoice = gd.getNextEnumChoice(BasicAwtColor.class);
		HighlightColorChoice = gd.getNextEnumChoice(BasicAwtColor.class);
		StrokeWidth = gd.getNextNumber();
		CatchRadius = gd.getNextNumber();
		ShowTriangles = gd.getNextBoolean();
		
		return true;
	}
}
