package imagingbook.common.ij;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

import ij.IJ;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;

/**
 * Defines a subclass of ImageJ's {@link Overlay} class
 * that accepts elements of a specific type {@link T}.
 * This (abstract) class cannot be instantiated directly but
 * is supposed to be used as the superclass for specific
 * overlay types, e.g., for displaying corners, SIFT markers etc.
 * In the simplest case, the concrete subclass just needs to implement the method 
 * {@link #makeRoi(Object)}, which is only concerned with geometry.
 * Stroke width and color are handled uniformly by this class ({@link CustomOverlay}).
 * The coordinates of added {@link ShapeRoi} instances are automatically 
 * shifted by a <strong>half-pixel distance</strong> (0.5, 0.5) 
 * to display integer points at the associated pixel centers.
 * <br>
 * Note: ImageJ draws all ROI types except {@link ShapeRoi} with a half-pixel offset.
 * This class redefines the methods {@link Overlay#add(Roi)} 
 * to perform the half-pixel shift.
 * <br>
 * TODO: Merge this with {@link ShapeOverlayAdapter} and {@link ColoredStroke} (which is simpler).
 * <br>
 * Note: This is experimental code - do NOT rely on it!
 * 
 * @author WB
 * @version 2020/10/04
 *
 * @param <T> type of elements allowed to be added to this overlay
 */
@Deprecated // replaced by imagingbook.lib.ij.overlay.ShapeOverlayAdapter
public abstract class CustomOverlay<T> extends Overlay {
	
	public static Color DefaultStrokeColor = Color.gray;
	public static double DefaultStrokeWidth = 0.25;
	
	private Color strokeColor = DefaultStrokeColor;
	private double strokeWidth = DefaultStrokeWidth;
	
	protected CustomOverlay() {
	}
	
	protected CustomOverlay(Color strokeColor, double strokeWidth) {
		this.strokeColor = strokeColor;
		this.strokeWidth = strokeWidth;
	}
	
	// -----------------------------------------------------------
	
	/**
	 * This method, which must be implemented by any subclass
	 * of {@link CustomOverlay},
	 * defines how an instance of type {@link T} is 
	 * converted to an {@link Roi} object.
	 * Called by {@link #addItem(Object)}.
	 * 
	 * @param item an instance of type {@link T} to be added to the overlay
	 * @return the resulting {@link Roi} object
	 */
	public abstract Roi makeRoi(T item);
	
	// Experimental convenience method to specify color/width individually:
	public Roi makeRoi(T item, Color strokeColor, double strokeWidth) {
		Roi roi = makeRoi(item);
		roi.setStrokeColor(strokeColor);
		roi.setStrokeWidth(strokeWidth);
		return roi;
	}
	
	
	// -----------------------------------------------------------
	
	@Override  // deactivated method in super class:
	public void setStrokeColor(Color strokeColor) {
		IJ.log(CustomOverlay.class.getSimpleName() + ": setStrokeColor() has no effect, use strokeColor()!");
	}
	
	@Override  // deactivated method in super class:
	public void setStrokeWidth(Double strokeWidth) {
		IJ.log(CustomOverlay.class.getSimpleName() + ": setStrokeWidth() has no effect, use strokeWidth()!");
	}
	
	// -----------------------------------------------------------
	
	/**
	 * Sets the stroke color for all items subsequently added to this overlay.
	 * The stroke color can be changed any time between insertions.
	 * Uncommonly named (like a directive) to avoid confusion with 
	 * the existing {@link Overlay#setStrokeColor(Color)} method,
	 * which modifies all elements currently contained in the overlay.
	 * @param strokeColor the new stroke color to be used
	 */
	public void strokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}
	
	/**
	 * Returns the overlay's current stroke color.
	 * Useful for modifying the value temporarily and then reinstating the
	 * previous value.
	 * @return as described
	 */
	public Color getStrokeColor() {
		return this.strokeColor;
	}
	

	
	/**
	 * Sets the stroke width for all items subsequently added to this overlay.
	 * The stroke width can be changed any time between insertions.
	 * Uncommonly named (like a directive) to avoid confusion with 
	 * the existing {@link Overlay#setStrokeWidth(Double)} method,
	 * which modifies all elements currently contained in the overlay.
	 * @param strokeWidth the new stroke width to be used
	 */
	public void strokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	
	/**
	 * Returns the overlay's current stroke width.
	 * Useful for modifying the value temporarily and then reinstating the
	 * previous value.
	 * @return as described
	 */
	public double getStrokeWidth() {
		return this.strokeWidth;
	}
	
	// -----------------------------------------------------------
	
	/**
	 * Adds an ImageJ {@link Roi} instance to this overlay.
	 * A half-pixel shift is applied if the passed ROI is of type {@link ShapeRoi}.
	 * Should be used instead of {@link Overlay#add(Roi)}.
	 * 
	 * @param roi the ROI to be added 
	 */
	public void addRoi(Roi roi) {
		this.addRoi(roi, false);
	}
	
	
	/**
	 * Adds an ImageJ {@link Roi} instance to this overlay.
	 * Optionally, a half-pixel shift is applied if the passed ROI 
	 * is not drawn with a half-pixel shift by ImageJ.
	 * Does not apply the overlay's current stroke width and color settings.
	 * This method should be used instead of {@link Overlay#add(Roi)}.
	 * 
	 * @param roi the ROI to be added 
	 * @param shiftHalfPixel if set true a half-pixel shift is added to each coordinate
	 */
	public void addRoi(Roi roi, boolean shiftHalfPixel) {
		//IJ.log(roi.getClass().getSimpleName() + ": " + drawsWithHalfPixelShift(roi));
		// roi.setDrawOffset(true); // does nothing
		// add half-pixel shift only to ShapeRoi instances (other ROI types are displayed with offset)
		if (shiftHalfPixel) { // && !drawsWithHalfPixelShift(roi)) { // && roi instanceof ShapeRoi) {
			Rectangle2D r = roi.getFloatBounds();
			roi.setLocation(r.getX() + 0.5, r.getY() + 0.5);
		}
		super.add(roi);
    }
	
//	/**
//	 * Checks if ImageJ automatically draws the particular roi instance with a half-pixel
//	 * shift or not.
//	 * Note that this is handled inconsistently in ImageJ and probably a bug -
//	 * all roi classes should have the same behavior.
//	 * Known ROI-types that do NOT half-pixel shift are
//	 * {@link ShapeRoi} and certain kinds of 
//	 * {@link Line} (namely {@link Roi.POLYGON}, {@link Roi.FREELINE}, {@link Roi.ANGLE}).
//	 *
//	 * @param roi
//	 * @return
//	 */
//	public static boolean drawsWithHalfPixelShift(Roi roi) {
//		return true;
//		return
//			roi instanceof Line ||
//			roi instanceof Arrow ||
//			roi instanceof PointRoi ||
//			roi instanceof PolygonRoi && ((PolygonRoi)roi).getType() == Roi.POLYLINE ||
//			roi instanceof PolygonRoi && ((PolygonRoi)roi).getType() == Roi.FREELINE 
//			;
//	}
	
	// -----------------------------------------------------------
	
	/**
	 * Adds a single item of type {@link T} to this overlay.
	 * Applies the overlay's current stroke width and color settings.
	 * @param item an instance of type {@link T}
	 */
	public void addItem(T item) {
		Roi roi = makeRoi(item);
		roi.setStrokeWidth(this.strokeWidth);
		roi.setStrokeColor(this.strokeColor);
		this.addRoi(roi);
	}
	
	/**
	 * Convenience method.
	 * Adds all items of type {@link T} in the supplied list to this overlay,
	 * using the same graphics (color, stroke width) settings.
	 * @param items a list of instances of type {@link T}
	 */
	public void addItems(List<T> items) {
		for(T item : items) {
			this.addItem(item);
		}
	}
	
	// -----------------------------------------------------------
	
	public void addToOverlay(Overlay otherOverlay) {
		Roi[] rois = this.toArray();
		for (Roi r : rois) {
			otherOverlay.add(r);
		}
	}

}
