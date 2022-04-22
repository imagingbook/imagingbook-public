/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.mser.visualize;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

/**
 * TODO: This should go to imagingbook.pub.geometry
 * TODO: Why not use awt.Line2D ? 
 * Answer: because this version can also use int endpoints and it is immutable!
 * @author WB
 *
 */
public class LineSegment2d {
	
	private final Pnt2d p1, p2;
	
	public LineSegment2d(Pnt2d p1, Pnt2d p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public LineSegment2d(int x1, int y1, int x2, int y2) {
		this.p1 = PntInt.from(x1, y1);
		this.p2 = PntInt.from(x2, y2);
	}
	
	public LineSegment2d(double x1, double y1, double x2, double y2) {
		this.p1 = PntDouble.from(x1, y1);
		this.p2 = PntDouble.from(x2, y2);
	}
	
	public Pnt2d getP1() {
		return p1;
	}
	
	public Pnt2d getP2() {
		return p2;
	}
	

}
