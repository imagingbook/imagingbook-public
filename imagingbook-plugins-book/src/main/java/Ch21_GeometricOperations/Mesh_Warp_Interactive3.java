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
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;

/**
 * <p>
 * Different grid raster.
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
 * Note that this implementation is ad hoc and leaves much room for improvements
 * and increased efficiency.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/25
 */
public class Mesh_Warp_Interactive3 implements PlugInFilter, MouseListener, MouseMotionListener, KeyListener {

	private static final String PropertyKey = Mesh_Warp_Interactive3.class.getName();
	private static final String EditString = " (editing)";
	
	private static int Rows = 11;	// number of grid rows (must be odd)
	private static int Cols = 11;	// number of grid columns (must be odd)
	private static BasicAwtColor StrokeColorChoice = BasicAwtColor.Blue;
	private static BasicAwtColor HighlightColorChoice = BasicAwtColor.Green;
	private static double StrokeWidth = 0.2;
	private static double CatchRadius = 3.0;
	private static boolean ShowTriangles = true;
	
	// event handling variables:
	private KeyListener[] windowKeyListeners = null;
	private MouseListener[] canvasMouseListeners = null;
	private KeyListener[] canvasKeyListeners = null;
	private MouseMotionListener[] canvasMouseMotionListeners = null;
	
	// data structures representing the grid and mesh
	// grid points positions:
	private Pnt2d[][] gridOrig;
	private Pnt2d[][] gridWarp;				// gridWarp[row][col][x/y]
	// triangles:
	private Triangle[][][] trianglesOrig;
	private Triangle[][][] trianglesWarp;		// trianglesWarp[row][col][0/1]
	
	private int rSelect = -1;			// the currently selected grid point (row)
	private int cSelect = -1;			// the currently selected grid point (column)
	private EnclosingPoly enclosingPolygon = null;
	private boolean nodeSelected = false;
	
	
	private ImageWindow win;
	private ImageCanvas canvas;
	private ImagePlus im;
	private ImageProcessor ipOrig = null;
	private String title;

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
		im.setTitle(title);
		nodeSelected = false;
		enclosingPolygon = null;
		ipOrig = null;
		im.setProperty(PropertyKey, null);
		repaint();
	}
	
	// ---------------------------------------------------------------
	
	private void repaint() {
		Overlay oly = makeGridOverlay(gridWarp);
		im.setOverlay(oly);
		im.updateAndDraw();
	}
	
	private void initGridAndTriangles(int w, int h) {
		gridOrig = new Pnt2d[Rows][Cols];	// initialized with null
		gridWarp = new Pnt2d[Rows][Cols];
		
		// initialize grid:
		for (int r = 0; r < Rows; r++) {
			double y = (double) r * (h - 1) / (Rows - 1);
			for (int c = 0; c < Cols; c++) {
				double x = (double) c * (w - 1) / (Cols - 1);
				gridOrig[r][c] = gridWarp[r][c] = Pnt2d.from(x, y);
			}
		}
	
		trianglesOrig = new Triangle[Rows - 1][Cols - 1][2];
		trianglesWarp = new Triangle[Rows - 1][Cols - 1][2];
		updateTriangles(trianglesOrig, gridOrig);
		updateTriangles(trianglesWarp, gridWarp);
	}
	
	private Overlay makeGridOverlay(Pnt2d[][] pnts) {
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		ColoredStroke pathstroke = new ColoredStroke(StrokeWidth, StrokeColorChoice.getColor());
		ColoredStroke polystroke = new ColoredStroke(3 * StrokeWidth, StrokeColorChoice.getColor());
		ColoredStroke highlightstroke = 
				new ColoredStroke(StrokeWidth, HighlightColorChoice.getColor(), HighlightColorChoice.getColor());
		
		// draw the complete grid
		Path2D.Double gridPath = new Path2D.Double();
		
		// draw horizontal grid lines
		for (int r = 0; r < pnts.length; r++) {
			gridPath.moveTo(pnts[r][0].getX(), pnts[r][0].getY());
			for (int c = 1; c < pnts[r].length; c++) {
				gridPath.lineTo(pnts[r][c].getX(), pnts[r][c].getY());
			}
		}		
		// draw vertical grid lines
		for (int r = 0; r < pnts[0].length; r++) {
			gridPath.moveTo(pnts[0][r].getX(), pnts[0][r].getY());
			for (int c = 1; c < pnts.length; c++) {
				gridPath.lineTo(pnts[c][r].getX(), pnts[c][r].getY());
			}
		}
		// draw diagonal grid lines
		if (ShowTriangles) {
			for (int r = 0; r < pnts.length - 1; r++) {
				for (int c = 0; c < pnts[0].length - 1; c++) {
					gridPath.moveTo(pnts[r][c].getX(), pnts[r][c].getY());
					gridPath.lineTo(pnts[r + 1][c + 1].getX(), pnts[r + 1][c + 1].getY());
				}
			}
		}	
		ola.addShape(gridPath, pathstroke);
		
		// draw the vertices
		double rad = CatchRadius;
		for (int r = 0; r < pnts.length; r++) {
			for (int c = 0; c < pnts[r].length; c++) {
				double x = pnts[r][c].getX();
				double y = pnts[r][c].getY();
				Ellipse2D.Double circle = new Ellipse2D.Double(x - rad, y - rad, 2 * rad, 2 * rad);
				ola.addShape(circle, polystroke);
			}
				}
		
		// mark the selected grid point
		if (nodeSelected) {
			Pnt2d ps = gridWarp[rSelect][cSelect];
			double xs = ps.getX();
			double ys = ps.getY();
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
	private void moveSelectedGridPoint(int newX, int newY) {
		if (nodeSelected && (rSelect >= 0) && (rSelect < Rows) && (cSelect >= 0) && (cSelect < Cols)) {
			gridWarp[rSelect][cSelect] = Pnt2d.from(newX, newY);
		}
	}
	
	private boolean selectGridPoint(int clickX, int clickY) {
		for (int r = 0; r < gridWarp.length; r++) {
			for (int c = 0; c < gridWarp[r].length; c++) {
				double dx = gridWarp[r][c].getX() - clickX;
				double dy = gridWarp[r][c].getY() - clickY;
				double dist = Math.hypot(dx, dy);
				if (dist <= CatchRadius) {
					rSelect = r;
					cSelect = c;
					return true;
				}
			}
		}
		rSelect = -1;
		cSelect = -1;
		return false;
	}
	
	// -----------------------------------------------------------------------------
	
	private Triangle findEnclosingTriangle(double x, double y) {
		for (int r = 0; r < Rows - 1; r++) {
			for (int c = 0; c < Cols - 1; c++) {
				for (int i = 0; i < 2; i++) {
					Triangle t = trianglesWarp[r][c][i];
					if (t.contains(x, y)) {
						return t;
					}
				}
			}
		}
		return null;	// no enclosing triangle found
	}
	
	private void remapImage(ImageProcessor ip) {
		updateTriangles(trianglesWarp, gridWarp);
		updateMappings();
		int width = im.getWidth();
		int height = im.getHeight();
		// iterate over all pixels:
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				if (enclosingPolygon == null || enclosingPolygon.contains(u, v)) {
					// find the containing triangle in 'triangles' (if any)
					Triangle tWarp = findEnclosingTriangle(u, v);
					if (tWarp != null) {
						AffineMapping2D am = tWarp.getMapping();
						Pnt2d xy = am.applyTo(PntInt.from(u, v));	// source image position
						int val = ipOrig.getPixelInterpolated(xy.getX(), xy.getY());
						ip.set(u, v, val);
					}
				}
			}
		}
	}
	
	private AffineMapping2D getAffineMapping(Triangle tP, Triangle tQ) {
		Pnt2d[] P = {tP.pa, tP.pb, tP.pc};
		Pnt2d[] Q = {tQ.pa, tQ.pb, tQ.pc};
		return AffineMapping2D.fromPoints(P, Q);
	}
		
	private void updateTriangles(Triangle[][][] theTriangles, Pnt2d[][] theGrid) {
		for (int r = 0; r < Rows - 1; r++) {
			for (int c = 0; c < Cols - 1; c++) {
				Pnt2d p0 = theGrid[r][c];
				Pnt2d p1 = theGrid[r+1][c];
				Pnt2d p2 = theGrid[r+1][c+1];
				Pnt2d p3 = theGrid[r][c+1];
				theTriangles[r][c][0] = new Triangle(p0, p1, p2, r, c, 0);	// triangle A
				theTriangles[r][c][1] = new Triangle(p0, p2, p3, r, c, 1);	// triangle B
			}
		}
	}
	
	private void updateMappings() {
		for (int r = 0; r < Rows - 1; r++) {
			for (int c = 0; c < Cols - 1; c++) {
				for (int i = 0; i < 2; i++) {
					Triangle tOrig = trianglesOrig[r][c][i];
					Triangle tWarp = trianglesWarp[r][c][i];
					AffineMapping2D am = getAffineMapping(tWarp, tOrig);
					tWarp.setMapping(am);
				}
			}
		}
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

		win.addWindowListener(new WindowAdapter() {  
            @Override
			public void windowClosing(WindowEvent e) {  
                finish();
            }  
        });
		
		canvas.requestFocus();	// important, otherwise key events have no effect!!
	}
	
	private void revertListeners() {
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
		if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0) {
			reset();
		}
		else {
			int x = canvas.offScreenX(e.getX());
			int y = canvas.offScreenY(e.getY());
			nodeSelected = selectGridPoint(x, y);
			if (nodeSelected) {
				if ((0 < rSelect) && (rSelect < Rows-1) && (0 < cSelect) && (cSelect < Cols-1)) {
					enclosingPolygon = new EnclosingPoly(rSelect, cSelect);
				}
				else {
					enclosingPolygon = null;
				}
			}
		}
		repaint();
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
		int x = canvas.offScreenX(e.getX());
		int y = canvas.offScreenY(e.getY());
		if (nodeSelected && (enclosingPolygon != null) && (enclosingPolygon.contains(x, y))) {
			moveSelectedGridPoint(x, y);
			repaint();
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
			comp.removeKeyListener(kl);
		}
		return listeners;
	}
	
	private MouseListener[] removeMouseListeners(Component comp) {
		MouseListener[] listeners = comp.getMouseListeners();
		for (MouseListener ml : listeners) {
			comp.removeMouseListener(ml);
		}
		return listeners;
	}
	
	private MouseMotionListener[] removeMouseMotionListeners(Component comp) {
		MouseMotionListener[] listeners = comp.getMouseMotionListeners();
		for (MouseMotionListener ml : listeners) {
			comp.removeMouseMotionListener(ml);
		}
		return listeners;
	}
	
	private void addKeyListeners(Component comp, KeyListener[] listeners) {
		if (comp == null || listeners == null) return;
		for (KeyListener kl : listeners) {
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
	
	// ------------------------------------------------------------------
	
	@SuppressWarnings("serial")
	class Triangle extends Path2D.Double {
		
		final int row, col, id;
		final Pnt2d pa, pb, pc;
		
		private AffineMapping2D mapping = null;
		
		AffineMapping2D getMapping() {
			return this.mapping;
		}
		
		void setMapping(AffineMapping2D mapping) {
			this.mapping = mapping;
		}
		
		Triangle(Pnt2d pa, Pnt2d pb, Pnt2d pc, int row, int col, int id) {
			this.row = row;
			this.col = col;
			this.id = id;	// = 0,1
			this.pa = pa;
			this.pb = pb;
			this.pc = pc;
			this.moveTo(pa.getX(), pa.getY());
			this.lineTo(pb.getX(), pb.getY());
			this.lineTo(pc.getX(), pc.getY());
			this.closePath();
		}
	}
	
	@SuppressWarnings("serial")
	class EnclosingPoly extends Path2D.Double {
		
		EnclosingPoly(int r, int c) {
			this.moveTo(gridWarp[r][c+1].getX(), 	gridWarp[r][c+1].getY());  	// 0
			this.lineTo(gridWarp[r-1][c].getX(), 	gridWarp[r-1][c].getY());  	// 2
			this.lineTo(gridWarp[r-1][c-1].getX(), 	gridWarp[r-1][c-1].getY());  // 3
			this.lineTo(gridWarp[r][c-1].getX(), 	gridWarp[r][c-1].getY());  	// 4
			this.lineTo(gridWarp[r+1][c].getX(), 	gridWarp[r+1][c].getY());  	// 6
			this.lineTo(gridWarp[r+1][c+1].getX(), 	gridWarp[r+1][c+1].getY());  // 7
			this.closePath();
		}
	}
	
	// -------------------------------------------------------------------
	
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
