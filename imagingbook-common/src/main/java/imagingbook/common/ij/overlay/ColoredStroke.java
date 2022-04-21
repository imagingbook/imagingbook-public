package imagingbook.common.ij.overlay;

import java.awt.BasicStroke;
import java.awt.Color;

import imagingbook.common.math.Matrix;

/**
 * A mirror class of {@link BasicStroke} which adds stroke and fill colors.
 * Instances of this class are cloneable and mutable,
 * setters for all fields are provided, i.e., strokes can be easily customized.
 * Use {@link #getBasicStroke()} to convert to an AWT {@link BasicStroke} instance.
 * 
 * @author WB
 * @version 2021/10/26
 */
public class ColoredStroke implements Cloneable {
	
	public static final Color DefaultStrokeColor = Color.black;
	public static final Color DefaultFillColor = null;
	
	private float lineWidth;
	private int endCap;
	private int lineJoin; 
	private float miterLimit;
	private float[] dashArray;
	private float dashPhase;
	private Color strokeColor;
	private Color fillColor;
	
	public ColoredStroke() {
		this(new BasicStroke());
	}
	
	public ColoredStroke(BasicStroke bs) {
		this(
			bs.getLineWidth(),
			bs.getEndCap(),
			bs.getLineJoin(),
			bs.getMiterLimit(),
			bs.getDashArray(),
			bs.getDashPhase(),
			DefaultStrokeColor,
			DefaultFillColor);
	}
	
	public ColoredStroke(double lineWidth, int endCap, int lineJoin, 
			double miterLimit, float[] dashArray, double dashPhase,
			Color strokeColor, Color fillColor) {
		this.lineWidth = (float) lineWidth;
		this.endCap = endCap;
		this.lineJoin =  lineJoin;
		this.miterLimit = (float) miterLimit;
		this.dashArray = dashArray;
		this.dashPhase = (float) dashPhase;
		this.strokeColor = strokeColor;
		this.fillColor = fillColor;
	}
	
	// convenience constructors 
	
	public ColoredStroke(double lineWidth, Color strokeColor, double dashLength) {
		this();
		this.setLineWidth(lineWidth);
		this.setStrokeColor(strokeColor);
		if (dashLength > 0) {
			this.setDash(dashLength);
		}
	}
	
	public ColoredStroke(double lineWidth, Color strokeColor) {
		this();
		this.setLineWidth(lineWidth);
		this.setStrokeColor(strokeColor);
	}
	
	@Override
	public ColoredStroke clone() {
		return new ColoredStroke(lineWidth, endCap, lineJoin, 
			miterLimit, dashArray, dashPhase, strokeColor, fillColor);
	}

	// -------------------------------------------------------
	
	public void setLineWidth(double lineWidth) {
		this.lineWidth = (float) lineWidth;
	}

	public void setEndCap(int endCap) {
		this.endCap = endCap;
	}

	public void setLineJoin(int lineJoin) {
		this.lineJoin = lineJoin;
	}

	public void setMiterLimit(double miterLimit) {
		this.miterLimit = (float) miterLimit;
	}

	public void setDashArray(float[] dashArray) {
		this.dashArray = dashArray;
	}

	public void setDashPhase(double dashPhase) {
		this.dashPhase = (float) dashPhase;
	}

	public void setStrokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}
	
	// -------------------------------------------------------------
	
	/**
	 * Convenience method to set (unset) the dash pattern
	 * Usage examples:
	 * <pre> 
	 * setDash(6);      // = setDashArray(new float[] {6})
	 * setDash(6, 4);   // = setDashArray(new float[] {6, 4})
	 * setDash();       // = setDashArray(null)
	 * </pre>
	 * @see BasicStroke
	 * @see BasicStroke#getDashArray()
	 * @param dashes a (possibly empty) sequence of dash lengths
	 */
	public void setDash(double... dashes) {
		if (dashes == null || dashes.length == 0) {
			this.dashArray = null;
		}
		else {
			this.dashArray = Matrix.toFloat(dashes);
		}
	}
	
	// -------------------------------------------------------------
	
	public Color getStrokeColor() {
		return this.strokeColor;
	}
	
	public Color getFillColor() {
		return this.fillColor;
	}

	// -------------------------------------------------------------
	
	/**
	 * Returns a AWT {@link BasicStroke} instance for the current
	 * state of this stroke (with no color information).
	 * @return a AWT {@link BasicStroke}
	 */
	public BasicStroke getBasicStroke() {
		return new BasicStroke(lineWidth, endCap, lineJoin, 
			miterLimit, dashArray, dashPhase);
	}

}
