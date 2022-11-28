package Ch21_GeometricOperations;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
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
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

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
 * <li>apply target-to-source mapping of pixel coordinates to the original (source) image
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
 * Note that this is a simplistic implementation which leaves much room for improvements
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
public class Mesh_Warp_Interactive implements PlugInFilter {

	private static ImageResource SampleImage = GeneralSampleImage.WartburgSmall_jpg;
	private static final String PropertyKey = Mesh_Warp_Interactive.class.getName();
	private static final String EditString = " (editing)";
	
	private static int Rows = 10;	// number of grid rows
	private static int Cols = 10;	// number of grid columns
	
	private static BasicAwtColor StrokeColorChoice = BasicAwtColor.Blue;
	private static BasicAwtColor HighlightColorChoice = BasicAwtColor.Green;
	
	private static double StrokeWidth = 0.25;
	private static double CatchRadius = 3.0;
	private static boolean ShowTriangles = true;
	private static boolean HighlightSelection = true;
	private static boolean RemoveOverlayWhenDone = true;
	
	// event handling variables:
	private KeyListener[] windowKeyListeners = null;
	private MouseListener[] canvasMouseListeners = null;
	private KeyListener[] canvasKeyListeners = null;
	private MouseMotionListener[] canvasMouseMotionListeners = null;
	
	// ---- data structures representing the grid and mesh --------------
	
	private Pnt2d[][] gridOrig;				// grid points positions
	private Pnt2d[][] gridWarped;			// gridWarped[row][col][x/y]
	private PntInt nodeSelected = null;		// the selected grid node (x = row, y = column), inner node only!
	
	// triangles:
	private Triangle[][][] trianglesOrig;
	private Triangle[][][] trianglesWarped;		// trianglesWarped[row][col][0/1]
	private TriangleGroup trianglesSelected = null;

	// ------------------------------------------------------------------
	
	private ImageWindow win;
	private ImageCanvas canvas;
	private ImagePlus im;
	private ImageProcessor ipOrig = null;
	private String title;	
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Mesh_Warp_Interactive() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(SampleImage);
		}
	}

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
		redraw();
		IJ.wait(100);
	}
	
	// ---------------------------------------------------------------
	
	private void reset() {
		initGridAndTriangles(im.getWidth(), im.getHeight());
		nodeSelected = null;
		trianglesSelected = null;
	}
	
	private void finish() {
		revertListeners();
		im.setTitle(title);
		nodeSelected = null;
		trianglesSelected = null;
		if (RemoveOverlayWhenDone) {
			im.setOverlay(null);
		}
		ipOrig = null;
		im.setProperty(PropertyKey, null);
		im.updateAndDraw();
	}
	
	// ---------------------------------------------------------------
	
	private void redraw() {
		im.setOverlay(makeGridOverlay(gridWarped));
		im.updateAndDraw();
	}
	
	private void initGridAndTriangles(int w, int h) {
		this.gridOrig = new Pnt2d[Rows][Cols];
		this.gridWarped = new Pnt2d[Rows][Cols];
		
		// insert equally spaced points over image width/height
		for (int r = 0; r < Rows; r++) {
			double y = (double) r * (h - 1) / (Rows - 1);
			for (int c = 0; c < Cols; c++) {
				double x = (double) c * (w - 1) / (Cols - 1);
				gridOrig[r][c] = gridWarped[r][c] = Pnt2d.from(x, y);
			}
		}
		
		trianglesOrig = new Triangle[Rows - 1][Cols - 1][2];
		trianglesWarped = new Triangle[Rows - 1][Cols - 1][2];
		updateTrianglesAll(trianglesOrig, gridOrig);
		updateTrianglesAll(trianglesWarped, gridWarped);
	}
	
	private Overlay makeGridOverlay(Pnt2d[][] pnts) {
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		ColoredStroke pathstroke = new ColoredStroke(StrokeWidth, StrokeColorChoice.getColor());
		ColoredStroke polystroke = new ColoredStroke(StrokeWidth, HighlightColorChoice.getColor());
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
		if (nodeSelected != null && HighlightSelection) {	
			Pnt2d ps = gridWarped[nodeSelected.x][nodeSelected.y];
			double xs = ps.getX();
			double ys = ps.getY();
			Ellipse2D.Double circle = new Ellipse2D.Double(xs - rad, ys - rad, 2 * rad, 2 * rad);
			ola.addShape(circle, highlightstroke);
		}
		
		// mark the enclosing polygon (if exists)
		if (trianglesSelected != null && HighlightSelection) {
			for (Triangle t : trianglesSelected.trgls) {
				ola.addShape(t, polystroke);
			}
		}
		
		return ola.getOverlay();
	}
	
	// move the currently selected grid point to new position
	private void moveSelectedGridPoint(int xNew, int yNew) {
		if (nodeSelected != null) {	//  && (ns.x >= 0) && (ns.x < Rows) && (ns.y >= 0) && (ns.y < Cols)
			gridWarped[nodeSelected.x][nodeSelected.y] = Pnt2d.from(xNew, yNew);
		}
	}
	
	private PntInt findGridPoint(PntInt xyClick) {
		// only inner grid points may be selected, not the ones at the border!
		for (int r = 1; r < gridWarped.length - 1; r++) {
			for (int c = 1; c < gridWarped[r].length - 1; c++) {
				double dist = xyClick.distance(gridWarped[r][c]);
				if (dist <= CatchRadius) {
					return PntInt.from(r, c);
				}
			}
		}
		return null;
	}
	
	private void updateGridSelection(PntInt xy) {
		nodeSelected = findGridPoint(xy);
		trianglesSelected = (nodeSelected == null) ? null : new TriangleGroup(nodeSelected);
	}

	// -----------------------------------------------------------------------------
	
	private void remapImage() {
		updateTrianglesAll(trianglesWarped, gridWarped);
		updateTrianglesSelected();
		updateAffineMappings();
		warpImage();
	}
	
	private void warpImage() {
		ImageProcessor ip = im.getProcessor();	
		int width = ip.getWidth();
		int height = ip.getHeight();
		
		// iterate over all pixels:
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				Triangle tWarp = null;
				
				// if there is a selection we only remap pixels inside
				if (trianglesSelected != null) {
					tWarp = trianglesSelected.findTriangle(u, v); 		// search only over enclosing polygon triangles
					if (tWarp == null)
						continue;	
				}
				
				// otherwise (if no selection), we remap all pixels
				else {
					tWarp = findEnclosingGridTriangle(u, v);				
				}

				// if enclosing triangle was found, remap pixel (u,v)
				if (tWarp != null) {							// containing triangle found
					AffineMapping2D am = tWarp.getMapping();
					Pnt2d xy = am.applyTo(PntInt.from(u, v));	// source image position
					int val = ipOrig.getPixelInterpolated(xy.getX(), xy.getY());
					ip.set(u, v, val);
				}
			}
		}
	}
	
	private Triangle findEnclosingGridTriangle(double x, double y) {
		for (int r = 0; r < Rows - 1; r++) {
			for (int c = 0; c < Cols - 1; c++) {
				for (int i = 0; i < 2; i++) {
					Triangle t = trianglesWarped[r][c][i];
					if (t.contains(x, y)) {
						return t;
					}
				}
			}
		}
		return null;  // no enclosing triangle found
	}
	
	private void updateTrianglesAll(Triangle[][][] theTriangles, Pnt2d[][] theGrid) {
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
	
	private void updateTrianglesSelected() {
		if (trianglesSelected != null) {
			trianglesSelected.updateTriangles();
		}
	}
	
	private void updateAffineMappings() {
		for (int r = 0; r < Rows - 1; r++) {
			for (int c = 0; c < Cols - 1; c++) {
				for (int i = 0; i < 2; i++) {
					Triangle tOrig = trianglesOrig[r][c][i];
					Triangle tWarp = trianglesWarped[r][c][i];
					AffineMapping2D am = getAffineMapping(tWarp, tOrig);
					tWarp.setMapping(am);
				}
			}
		}
	}
	
	private AffineMapping2D getAffineMapping(Triangle tP, Triangle tQ) {
		Pnt2d[] P = {tP.pa, tP.pb, tP.pc};
		Pnt2d[] Q = {tQ.pa, tQ.pb, tQ.pc};
		return AffineMapping2D.fromPoints(P, Q);
	}

	// ------------------------------------------------------------
	// EVENT HANDLING:
	// ----------------------------------------------------------------
	
	// anonymous sub-class of MouseAdapter
	private final MouseAdapter MA = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			try {		// right mouse button -> reset
				if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0) {
					reset();
				}
				else {	// otherwise update the grid
					updateGridSelection(
							PntInt.from(canvas.offScreenX(e.getX()), canvas.offScreenY(e.getY())));
				}
				redraw();	
				e.consume();
			} catch (Exception ex) {
				IJ.handleException(ex);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			try {
				remapImage();
				redraw();
				if (e.getClickCount() == 2 && !e.isConsumed()) {
					e.consume();
				}
			} catch (Exception ex) {
				IJ.handleException(ex);
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			try {
				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());
				if ((nodeSelected != null) && (trianglesSelected != null) && (trianglesSelected.contains(x, y))) {
					moveSelectedGridPoint(x, y);
					redraw();
				}
				} catch (Exception ex) {
					IJ.handleException(ex);
			}
		}
		
	};
	
	// anonymous sub-class of KeyAdapter
	private final KeyAdapter KA = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			// escape -> finish
			if(keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_ENTER) {
				finish();
			}
			// ctrl/+ zoom in
			else if(keyCode == KeyEvent.VK_PLUS && e.isControlDown()) {
				canvas.zoomIn(im.getWidth()/2, im.getHeight()/2);
			}
			// ctrl/- zoom out
			else if(keyCode == KeyEvent.VK_MINUS && e.isControlDown()) {
				canvas.zoomOut(im.getWidth()/2, im.getHeight()/2);
			}
		}
	};
	
	private void setupListeners() {
		// remove current listeners and keep for later re-install
		windowKeyListeners = removeKeyListeners(win);
		canvasKeyListeners = removeKeyListeners(canvas);
		canvasMouseListeners = removeMouseListeners(canvas);
		canvasMouseMotionListeners = removeMouseMotionListeners(canvas);
		
		canvas.addKeyListener(KA);
		canvas.addMouseListener(MA);
		canvas.addMouseMotionListener(MA);

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
	
	// ----------------
	
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
	
	/**
	 * Represents a 2D triangle, used for point inclusion testing and
	 * affine mapping.
	 * @author WB
	 */
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
			// make triangle path:
			this.moveTo(pa.getX(), pa.getY());
			this.lineTo(pb.getX(), pb.getY());
			this.lineTo(pc.getX(), pc.getY());
			this.closePath();
		}
		
//		@Override
//		public String toString() {
//			return (String.format("Triangle[row=%d col=%d id=%d", this.row, this.col, this.id));
//		}
	}
	
	/**
	 * This is the group of triangles associated with a particular grid point.
	 */
	class TriangleGroup {
		final int r, c;						// the grid point
		private final Triangle[] trgls;
		
		TriangleGroup(PntInt rc) {
			this.r = rc.x;
			this.c = rc.y;
			this.trgls = new Triangle[6];
			updateTriangles();
		}

		private void updateTriangles() {
			trgls[0] = trianglesWarped[r-1][c-1][0];
			trgls[1] = trianglesWarped[r-1][c-1][1];		
			trgls[2] = trianglesWarped[r-1][c][0];
			trgls[3] = trianglesWarped[r][c-1][1];			
			trgls[4] = trianglesWarped[r][c][0];
			trgls[5] = trianglesWarped[r][c][1];
		}

		private Triangle findTriangle(double x, double y) {
			for (Triangle t : this.trgls) {
				if (t.contains(x, y)) {
					return t;
				}
			}
			return null;
		}	

		private boolean contains(double x, double y) {
			return (findTriangle(x, y) != null);
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
		gd.addCheckbox("Highlight selection", HighlightSelection);
		gd.addCheckbox("Remove overlay when done", RemoveOverlayWhenDone);
		
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
		HighlightSelection = gd.getNextBoolean();
		RemoveOverlayWhenDone = gd.getNextBoolean();
		
		return true;
	}
}
