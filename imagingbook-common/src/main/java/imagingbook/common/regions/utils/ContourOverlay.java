/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.regions.utils;

import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import imagingbook.common.regions.Contour;
import imagingbook.common.regions.ContourTracer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.List;

/**
 * A special overlay for showing binary region contours.
 * TODO: convert to CustomOverlay
 * 
 * @version 2020/04/01
 */
public class ContourOverlay extends Overlay {

	static float defaultStrokeWidth = 1.0f; //0.2f;
	static Color defaultOuterColor = Color.red;
	static Color defaultInnerColor = Color.green;
	
//	static int capsstyle = BasicStroke.CAP_ROUND;
//	static int joinstyle = BasicStroke.JOIN_ROUND;
//	static float[] outerDashing = {12, 4}; 
//	static float[] innerDashing = {12, 4}; 
//	static boolean DRAW_CONTOURS = true;
	
	public ContourOverlay(ContourTracer tracer) {
		this(tracer, defaultStrokeWidth, defaultOuterColor, defaultInnerColor);
	}
	
	public ContourOverlay(ContourTracer tracer, 
			double strokeWidth, Color outerColor, Color innerColor) {
		
		List<? extends Contour> outerContours = tracer.getOuterContours();
		List<? extends Contour> innerContours = tracer.getInnerContours();
		addContours(outerContours, outerColor, strokeWidth);
		addContours(innerContours, innerColor, strokeWidth);
	}
	
	public void addContours(List<? extends Contour> contours, Color color, double strokeWidth) {
		BasicStroke stroke = new BasicStroke((float)strokeWidth);
		for (Contour c : contours) {
			Shape s = c.getPolygonPath();
			Roi roi = new ShapeRoi(s);
			roi.setStrokeColor(color);
			roi.setStroke(stroke);
//			roi.setStrokeWidth(strokeWidth);
			add(roi);
		}
	}

}



