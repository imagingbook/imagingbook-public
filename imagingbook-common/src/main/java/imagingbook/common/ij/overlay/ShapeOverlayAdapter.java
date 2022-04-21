package imagingbook.common.ij.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import ij.gui.Overlay;
import ij.gui.ShapeRoi;
import ij.gui.TextRoi;

/**
 * <p>
 * This is an adapter for ImageJ's {@link Overlay} class to ease the insertion of
 * AWT {@link Shape} elements.
 * Shapes are only geometric descriptions but have no stroke width, color
 * etc. attached. 
 * These are determined by {@link ShapeOverlayAdapter}'s <strong>current state</strong>
 * when insertion is done with {@link #addShape(Shape)} (i.e., without explicit
 * stroke information).
 * Alternatively, the shape's stroke and color may be explicitly specified by calling 
 * {@link #addShape(Shape, ColoredStroke)}.
 * When a shape is inserted into the associated overlay it is converted to an ImageJ
 * {@link ShapeRoi} instance whose stroke/fill properties are set. 
 * </p>
 * <p>
 * Similarly, text elements can be added using {@link #addText(double, double, String)}
 * or {@link #addText(double, double, String, Font, Color)}.
 * </p>
 * <p>
 * By default, shapes added to {@link ShapeOverlayAdapter} are automatically translated
 * by 0.5 units in x/y direction, such that integer coordinates are shifted to pixel
 * centers. This can be deactivated by using constructor 
 * {@link #ShapeOverlayAdapter(Overlay, boolean)}.
 * </p>
 * <p>
 * <strong>Usage example 1 (adding shapes using stroke/color state):</strong>
 * </p>
 * <pre>
 * ImagePlus im = ...
 * Overlay oly = new Overlay();
 * ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
 * 
 * ola.setStroke(new ColoredStroke(0.25, Color.blue));
 * ola.addShape(new Line2D.Double(x1, y1, x2, y2));
 * ...			// add more shapes with same stroke
 * ola.setStroke(new ColoredStroke(0.4, Color.pink));
 * ola.addShape(new Line2D.Double(x3, y3, x4, y4));
 * ...			// add more shapes with same stroke
 * im.setOverlay(oly);
 * im.show();</pre>
 * 
 * <p>
 * <strong>Usage example 2 (adding shapes with element-wise stroke/color specification):</strong>
 * </p>
 * <pre>
 * ImagePlus im = ...
 * Overlay oly = new Overlay();
 * ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
 * ColoredStroke mystroke = new ColoredStroke(0.25, Color.blue);
 * ola.addShape(new Line2D.Double(x1, y1, x2, y2), mystroke);
 * ...				// add more shapes
 * im.setOverlay(oly);
 * im.show();</pre>
 * 
 * <p>
 * <strong>Usage example 3 (adding text):</strong>
 * </p>
 * <pre>
 * ImagePlus im = ...
 * Overlay oly = new Overlay();
 * ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
 * ola.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
 * ola.setTextColor(Color.green);
 * ola.addText(x, y, "text using current font/color state");
 * 
 * ola.addText(x, y, "text with explicit font & color", new Font(Font.SERIF, Font.PLAIN, 10), Color.black);
 * ...
 * im.setOverlay(oly);
 * im.show();</pre>
 * 
 * 
 * @author WB
 * @version 2022/03/30
 * 
 * @see ColoredStroke
 * @see Shape
 *
 */
public class ShapeOverlayAdapter {
	
	public static Color DefaultTextColor = Color.black;
	public static float DefaultStrokeWidth = 0.5f;
	public static Font DefaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
	
	private final AffineTransform PixelOffsetTransform; // AWT transformation!
	
	private final Overlay overlay;
	private ColoredStroke stroke;
	private Color textColor = DefaultTextColor;
	private Font font = DefaultFont;
	
	private boolean pixelOffset = true;
	
	// ----------------------------------------------------------
	
	public ShapeOverlayAdapter(Overlay oly, boolean halfPixelOffset) {
		this.overlay = oly;
		this.stroke = makeDefaultStroke();
		this.PixelOffsetTransform = (halfPixelOffset) ?
				new AffineTransform(1, 0, 0, 1, 0.5, 0.5) :
				new AffineTransform();
	}
	
	public ShapeOverlayAdapter(Overlay oly) {
		this(oly, true);
	}
	
	public ShapeOverlayAdapter() {
		this(new Overlay());
	}
	
	// ----------------------------------------------------------
	
	public ColoredStroke getStroke() {
		return stroke;
	}
	
	public void setStroke(ColoredStroke stroke) {
		this.stroke = stroke;
	}
	
	public void setTextColor(Color color) {
		this.textColor = color;
	}
	
	public void setFont(Font font) {
		this.font = font;
	}
	
	public void setPixelOffset(boolean pixelOffset) {
		this.pixelOffset = pixelOffset;
	}
	
	public Overlay getOverlay() {
		return this.overlay;
	}
	
	// ----------------------------------------------------------
	
	protected ShapeRoi shapeToRoi(Shape s, ColoredStroke stroke) {
		s = (pixelOffset) ? PixelOffsetTransform.createTransformedShape(s) : s;
		ShapeRoi roi = new ShapeRoi(s);
		BasicStroke bs = stroke.getBasicStroke();
		roi.setStrokeWidth(bs.getLineWidth());
		roi.setStrokeColor(stroke.getStrokeColor());
		roi.setFillColor(stroke.getFillColor());
		roi.setStroke(bs);
		return roi;
	}
	
	public void addShape(Shape s) {
		overlay.add(shapeToRoi(s, this.stroke));
	}
	
	public void addShapes(Shape[] shapes) {
		for (Shape s : shapes) {
			overlay.add(shapeToRoi(s, this.stroke));
		}
	}
	
	public void addShape(Shape s, ColoredStroke stroke) {
		overlay.add(shapeToRoi(s, stroke));
	}
	
	public void addShapes(Shape[] shapes, ColoredStroke stroke) {
		for (Shape s : shapes) {
			overlay.add(shapeToRoi(s, stroke));
		}
	}
	
	// TODO: check font/color settings
	public void addText(double x, double y, String text) {
		TextRoi troi = new TextRoi(x, y, text, this.font);
		troi.setStrokeColor(this.textColor);
		overlay.add(troi);
	}
	
	public void addText(double x, double y, String text, Font font, Color textColor) {
		TextRoi troi = new TextRoi(x, y, text, font);
		troi.setStrokeColor(textColor);
		overlay.add(troi);
	}

	// ----------------------------------------------------------
	
	private static ColoredStroke makeDefaultStroke() {
		ColoredStroke stroke = new ColoredStroke();
		stroke.setLineWidth(DefaultStrokeWidth);
		stroke.setStrokeColor(DefaultTextColor);
		return stroke;
	}

}
