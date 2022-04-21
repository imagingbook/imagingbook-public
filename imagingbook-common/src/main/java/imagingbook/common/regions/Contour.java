/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.regions;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.geometry.basic.Pnt2d;


/**
 * This class represents a closed contour as a sequence of
 * pixel coordinates. It implements the {@link Comparable}
 * interface for sorting contours by length.
 * It supports iteration over the points along the contour, 
 * e.g., by
 * <pre>
 * Contour C = ...;
 * for (Point p : C) {
 *    // process p ...
 * }
 * </pre>
 * 
 * @version 2020/12/21
 */
public class Contour implements Comparable<Contour>, Iterable<Pnt2d> {
	
	static private int INITIAL_SIZE = 50;
	
	private final int label;
	private final List<Pnt2d> points;
	
	/**
	 * Creates a new (empty) contour with the given region label.
	 * @param label the region label for this contour.
	 */
	public Contour (int label) {
		this.label = label;
		points = new ArrayList<Pnt2d>(INITIAL_SIZE);
	}
	
	public void addPoint (Pnt2d p) {
		points.add(p);
	}
	
	//--------------------- retrieve contour points -------
	
	/**
	 * Get the list of contour points.
	 * @return a reference to the internal list of contour points.
	 */
	public List<Pnt2d> getPointList() {
		return points;
	}
	
	/**
	 * Get the contour points as an array.
	 * @return a new array of contour points.
	 */
	public Pnt2d[] getPointArray() {
		return points.toArray(new Pnt2d[0]);
	}
		
	//--------------------- contour statistics ------------
	
	/**
	 * Get the length of the contour.
	 * @return the number of points on the contour.
	 */
	public int getLength() {
		return points.size();
	}
	
	/**
	 * Get the region label associated with this contour.
	 * @return the region label of the contour.
	 */
	public int getLabel() {
		return label;
	}
	
	//--------------------- debug methods ------------------
	
	@Override
	public String toString(){
		return
			"Contour " + label + ": " + this.getLength() + " points";
	}
	
	/**
	 * Get the polygon for this contour (for subsequent drawing).
	 * @return the polygon.
	 */
	public Path2D getPolygonPath() {
		return getPolygonPath(0.5, 0.5);	// offset by 0.5 to pass through pixel centers
	}
	
	/**
	 * Get the polygon for this contour (for subsequent drawing).
	 * An offset can be specified for shifting the contour positions
	 * at pixel centers (set to 0.5/0.5).
	 * 
	 * @param xOffset the horizontal offset.
	 * @param yOffset the vertical offset.
	 * @return a polygon.
	 */
	public Path2D getPolygonPath(double xOffset, double yOffset) {
		Path2D path = new Path2D.Float();
		Pnt2d[] pnts = this.getPointArray();
		if (pnts.length > 1){
			path.moveTo(pnts[0].getX() + xOffset, pnts[0].getY() + yOffset);
			for (int i = 1; i < pnts.length; i++) {
				path.lineTo(pnts[i].getX() + xOffset,  pnts[i].getY() + yOffset);
			}
			path.closePath();
		}
		else {	// mark single pixel region "X"
			double x = pnts[0].getX();
			double y = pnts[0].getY();
			path.moveTo(x + xOffset - 0.5, y + yOffset - 0.5);
			path.lineTo(x + xOffset + 0.5, y + yOffset + 0.5);
			path.moveTo(x + xOffset - 0.5, y + yOffset + 0.5);
			path.lineTo(x + xOffset + 0.5, y + yOffset - 0.5);
		}
		return path;
	}
	
	/**
	 * Returns the number of successive duplicates in this contour.
	 * The result should be zero.
	 * @return as described.
	 */
	public int countDuplicatePoints() {
		Pnt2d[] pnts = this.getPointArray();
		if (pnts.length <= 1) {
			return 0;
		}
		int cnt = 0;
		for (int i = 0; i < pnts.length; i++) {
			int j = (i + 1) % pnts.length;
			if (pnts[i].getX() == pnts[j].getX() && pnts[i].getY() == pnts[j].getY()) {
				cnt++;
			}
		}
		return cnt;
	}
	
	/**
	 * Checks if this contour is closed w.r.t. the specified
	 * {@link NeighborhoodType2D}, i.e., if the last and the first
	 * contour point are "connected".
	 * 
	 * @param nht the (@link NeighborhoodType}.
	 * @return true if the contour is closed.
	 */
	public boolean isClosed(NeighborhoodType2D nht) {
		Pnt2d[] pnts = this.getPointArray();
		if (pnts.length < 2) 
			return true;
		Pnt2d p1 = pnts[pnts.length - 1];
		Pnt2d p2 = pnts[0];
		double d2 = p1.distanceSq(p2);	// N4: max 1, N8: max 2
		//System.out.println(nht + " dist=" + d2);
		
		if (nht == NeighborhoodType2D.N4 && d2 <= 1)
			return true;
		if (nht == NeighborhoodType2D.N8 && d2 <= 2)
			return true;
		return false;
	}

		
	// Compare method for sorting contours by length (longer contours at front)
	@Override
	public int compareTo(Contour other) {
		//return other.points.size() - this.points.size();
		return Integer.compare(other.points.size(), this.points.size());
	}

	@Override
	public Iterator<Pnt2d> iterator() {
		return points.iterator();
	}
	
	// -----------------------------------------------------------------------------------
	
	public static class Outer extends Contour {
		public Outer(int label) {
			super(label);
		}
		
		@Override
		public String toString(){
			return
				"Contour.Outer " + this.getLabel() + ": " + this.getLength() + " points";
		}
	}
	
	public static class Inner extends Contour {
		public Inner(int label) {
			super(label);
		}
		
		@Override
		public String toString(){
			return
				"Contour.Inner " + this.getLabel() + ": " + this.getLength() + " points";
		}
	}
	

}
