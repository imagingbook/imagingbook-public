package imagingbook.common.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;

/**
 * <p>This class defines functionality for drawing anti-aliased "pixel"
 * graphics in images of type 
 * {@link ByteProcessor},
 * {@link ShortProcessor} or
 * {@link ColorProcessor}
 * (there is no support for {@link FloatProcessor}).
 * It uses the capabilities of AWT's {@link Graphics2D} class
 * by drawing to a {@link BufferedImage}, which is a copy of
 * the original image. After performing the drawing operations
 * the {@link BufferedImage} is copied back to the original.
 * Thus all operations possible on a {@link Graphics2D} instance
 * are available, including the drawing of {@link Shape}
 * objects with floating-point coordinates, arbitrary 
 * strokes and anti-aliasing, which is not available with
 * ImageJ's built-in graphics operations (for class {@link ImageProcessor}).
 * </p>
 * <p>
 * Since drawing involves copying the image multiple times, graphic operations
 * should be grouped for efficiency reasons.
 * Here is an example for the intended form of use:
 * </p>
 * <pre>
 * ImageProcessor ip = ... ;   // some ByteProcessor, ShortProcessor or ColorProcessor
 * try (ImageGraphics g = new ImageGraphics(ip)) {
 * 	g.setColor(255);
 * 	g.setLineWidth(1.0);
 * 	g.drawLine(40, 100.5, 250, 101.5);
 * 	g.drawOval(230.6, 165.2, 150, 150);
 * 	...
 * }</pre>
 * <p>
 * Note the use of <code>double</code> coordinates throughout.
 * The original image ({@code ip} in the above example) is automatically updated 
 * at the end of the {@code try() ...} clause (by {@link ImageGraphics} implementing the
 * {@link AutoCloseable} interface).
 * The {@link #getGraphics()} method exposes the underlying 
 * {@link Graphics2D} instance of the {@link ImageGraphics} object, which can then be used to
 * perform arbitrary graphic operations.
 * Thus, the above example could <strong>alternatively</strong> be implemented as follows:
 * <pre>
 * ImageProcessor ip = ... ;   // some ByteProcessor, ShortProcessor or ColorProcessor
 * try (ImageGraphics g = new ImageGraphics(ip)) {
 * 	Graphics2D g2 = g.getGraphics();
 * 	g2.setColor(Color.white);
 * 	g2.setStroke(new BasicStroke(1.0f));
 * 	g2.draw(new Line2D.Double(40, 100.5, 250, 101.5));
 * 	g2.draw(new Ellipse2D.Double(230.6, 165.2, 150, 150));
 * 	...
 * }</pre>
 * <p>
 * This class also defines several convenience methods for drawing
 * shapes with floating-point ({@code double}) coordinates, as well as for
 * setting colors and stroke parameters.
 * If intermediate updates are needed (e.g., for animations), the {@code update()} method
 * can be invoked any time.
 * The plugin {@code Geometric_Operations.Draw_Test_Grid} in the
 * <a href="https://github.com/imagingbook/imagingbook-plugins-all/tree/master">
 * imagingbook-plugins-all</a> repository shows a complete example.
 * </p>
 * 
 * TODO: Add text drawing, merge with {@link ColoredStroke}.
 * 
 * @author W. Burger
 * @version 2020-01-07
 * 
 * @see ShapeOverlayAdapter
 */
public class ImageGraphics implements AutoCloseable {
	
	private static BasicStroke DEFAULT_STROKE = new BasicStroke();
	private static Color DEFAULT_COLOR = Color.white;
	private static boolean DEFAULT_ANTIALIASING = true;
	
	private ImageProcessor ip;
	private BufferedImage bi;
	private final Graphics2D g;
	
	private BasicStroke stroke = DEFAULT_STROKE;
	private Color color = DEFAULT_COLOR;
	
	// -------------------------------------------------------------
	
	/**
	 * Constructor. The supplied image must be of type
	 * {@link ByteProcessor}, {@link ShortProcessor} or 
	 * {@link ColorProcessor}.
	 * An {@link IllegalArgumentException} is thrown for images
	 * of type {@link FloatProcessor}.
	 * 
	 * @param ip image to draw on
	 */
	public ImageGraphics(ImageProcessor ip) {
		this(ip, null, null);
	}
	
	/**
	 * Constructor. The supplied image must be of type
	 * {@link ByteProcessor}, {@link ShortProcessor} or 
	 * {@link ColorProcessor}.
	 * An {@link IllegalArgumentException} is thrown for images
	 * of type {@link FloatProcessor}.
	 * 
	 * @param ip image to draw on
	 * @param color the initial drawing color
	 * @param stroke the initial stroke
	 */
	public ImageGraphics(ImageProcessor ip, Color color, BasicStroke stroke) {
		this.ip = ip;
		this.bi = toBufferedImage(ip);
		
		if (color != null) this.color = color;
		if (stroke != null) this.stroke = stroke;
		
		this.g = (Graphics2D) bi.getGraphics();
		this.g.setColor(color);
		this.g.setColor(this.color);
		this.g.setStroke(this.stroke);
		this.setAntialiasing(DEFAULT_ANTIALIASING);
	}
	
	public void setAntialiasing(boolean on) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, on ? 
				RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, on ?
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
	
	/**
	 * Returns the underlying {@link Graphics2D} object,
	 * which can be used to perform arbitrary graphics operations.
	 * @return the {@link Graphics2D} object
	 */
	public Graphics2D getGraphics() {
		return this.g;
	}
	
	/**
	 * Forces the image to be updated by copying the (modified)
	 * {@link BufferedImage} back to the input image.
	 * There is usually no need to call this (expensive) method explicitly.
	 * It is called automatically and only once at end of the 
	 * {@code try() ...} clause, as described in the {@link ImageGraphics}
	 * class documentation above.
	 */
	public void update() {
		copyImageToProcessor(bi, ip);
	}

	@Override
	public void close() {
		update();
		ip = null;
		bi = null;
	}
	
	// -----------------------------------------------------------
	
	// Needed, since IJ's conversion methods are not named consistently.
	private BufferedImage toBufferedImage(ImageProcessor ip) {
		if (ip instanceof ByteProcessor) {
			return ((ByteProcessor) ip).getBufferedImage(); 
		}
		else if (ip instanceof ShortProcessor) {
			return ((ShortProcessor) ip).get16BitBufferedImage(); 
		}
		else if (ip instanceof ColorProcessor) {
			return ((ColorProcessor) ip).getBufferedImage();
		}
		else {
			throw new IllegalArgumentException("Cannot create BufferedImage from " +
					ip.getClass().getName());
		}
	}
	
	/**
	 * Copies the contents of the {@link BufferedImage} to the specified 
	 * {@link ImageProcessor}. The size and type of the BufferedImage
	 * is assumed to match the ImageProcessor.
	 * 
	 * @param bi the local (intermediate) {@link BufferedImage} instance
	 * @param ip the original {@link ImageProcessor}
	 */
	private void copyImageToProcessor(BufferedImage bi, ImageProcessor ip) {
		ImageProcessor ip2 = null;
		if (ip instanceof ByteProcessor) {
			ip2 = new ByteProcessor(bi); 
		}
		else if (ip instanceof ShortProcessor) {
			ip2 = new ShortProcessor(bi); 
		}
		else if (ip instanceof ColorProcessor) {
			ip2 = new ColorProcessor(bi); 
		}
		else {
			throw new IllegalArgumentException("Cannot create BufferedImage from " +
					ip.getClass().getName());
		}
		ip.copyBits(ip2, 0, 0, Blitter.COPY);
	}
	
	// -----------------------------------------------------------
	//  Convenience methods for drawing selected shapes with double coordinates
	// -----------------------------------------------------------
	
	/**
	 * Convenience method. Draws a straight line segment specified with
	 * {@code double} coordinate values.
	 * @param x1 x-coordinate of start point
	 * @param y1 y-coordinate of start point
	 * @param x2 x-coordinate of end point
	 * @param y2 y-coordinate of end point
	 * @see Line2D
	 */
	public void drawLine(double x1, double y1, double x2, double y2) {
		g.draw(new Line2D.Double(x1, y1, x2, y2));
	}
	
	/**
	 * Convenience method. Draws an ellipse specified with
	 * {@code double} coordinate values.
	 * @param x x-coordinate of the upper-left corner of the framing rectangle
	 * @param y y-coordinate of the upper-left corner of the framing rectangle
	 * @param w width
	 * @param h height
	 * @see Ellipse2D
	 */
	public void drawOval(double x, double y, double w, double h) {
		g.draw(new Ellipse2D.Double(x, y, w, h));
	}
	
	/**
	 * Convenience method. Draws a rectangle specified with
	 * {@code double} coordinate values.
	 * @param x x-coordinate of the upper-left corner
	 * @param y y-coordinate of the upper-left corner
	 * @param w width
	 * @param h height
	 * @see Rectangle2D
	 */
	public void drawRectangle(double x, double y, double w, double h) {
		g.draw(new Rectangle2D.Double(x, y, w, h));
	}
	
	/**
	 * Convenience method. Draws a closed polygon specified by a 
	 * sequence of {@link Point2D} objects (with arbitrary coordinate values).
	 * Note that the the polygon is automatically closed, i.e.,
	 * N+1 segments are drawn if the number of given points is N.
	 * @param points a sequence of 2D points
	 * @see Path2D
	 */
	public void drawPolygon(Point2D ... points) {
		Path2D.Double p = new Path2D.Double();
		p.moveTo(points[0].getX(), points[0].getY());
		for (int i = 1; i < points.length; i++) {
			p.lineTo(points[i].getX(), points[i].getY());
		}
		p.closePath();
		g.draw(p);
	}
	
	// stroke-related methods -------------------------------------
	
	/**
	 * Sets this graphics context's current color to the specified color. 
	 * All subsequent graphics operations using this graphics context use 
	 * this specified color.
	 * @param color the new rendering color
	 * @see Graphics#setColor
	 */
	public void setColor(Color color) {
		this.color = color;
		g.setColor(color);
	}
	
	/**
	 * Sets this graphics context's current color to the specified
	 * (gray) color, with RGB = (gray, gray, gray).
	 * @param gray the gray value
	 */
	public void setColor(int gray) {
		if (gray < 0) gray = 0;
		if (gray > 255) gray = 255;
		this.setColor(new Color(gray, gray, gray));
	}
	
	/**
	 * Sets the stroke to be used for all subsequent graphics operations.
	 * @param stroke a {@link BasicStroke} instance
	 * @see BasicStroke
	 */
	public void setStroke(BasicStroke stroke) {
		this.stroke = stroke;
		g.setStroke(this.stroke);
	}
	
	/**
	 * Sets the line width of the current stroke.
	 * All other stroke properties remain unchanged.
	 * @param width the line width
	 * @see BasicStroke
	 */
	public void setLineWidth(double width) {
		this.stroke = new BasicStroke((float)width, stroke.getEndCap(), stroke.getLineJoin());
		g.setStroke(this.stroke);
	}
	
	/**
	 * Sets the end cap style of the current stroke to "BUTT".
	 * All other stroke properties remain unchanged.
	 * @see BasicStroke
	 */
	public void setEndCapButt() {
		this.setEndCap(BasicStroke.CAP_BUTT);
	}
	
	/**
	 * Sets the end cap style of the current stroke to "ROUND".
	 * All other stroke properties remain unchanged.
	 * @see BasicStroke
	 */
	public void setEndCapRound() {
		this.setEndCap(BasicStroke.CAP_ROUND);
	}
	
	/**
	 * Sets the end cap style of the current stroke to "SQUARE".
	 * All other stroke properties remain unchanged.
	 * @see BasicStroke
	 */
	public void setEndCapSquare() {
		this.setEndCap(BasicStroke.CAP_SQUARE);
	}
	
	private void setEndCap(int cap) {
		setStroke(new BasicStroke(stroke.getLineWidth(), cap, stroke.getLineJoin()));
	}
	
	// ---------------------
	
	/**
	 * Sets the line segment join style of the current stroke to "BEVEL".
	 * All other stroke properties remain unchanged.
	 * @see BasicStroke
	 */
	public void setLineJoinBevel() {
		setLineJoin(BasicStroke.JOIN_BEVEL);
	}
	
	/**
	 * Sets the line segment join style of the current stroke to "MITER".
	 * All other stroke properties remain unchanged.
	 * @see BasicStroke
	 */
	public void setLineJoinMiter() {
		setLineJoin(BasicStroke.JOIN_MITER);
	}
	
	/**
	 * Sets the line segment join style of the current stroke to "ROUND".
	 * All other stroke properties remain unchanged.
	 * @see BasicStroke
	 */
	public void setLineJoinRound() {
		setLineJoin(BasicStroke.JOIN_ROUND);
	}
	
	private void setLineJoin(int join) {
		setStroke(new BasicStroke(stroke.getLineWidth(), stroke.getEndCap(), join));
	}
}
