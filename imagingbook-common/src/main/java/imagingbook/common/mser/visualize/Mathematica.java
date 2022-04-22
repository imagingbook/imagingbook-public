/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.mser.visualize;

import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;

public abstract class Mathematica {
	
	public static String toString(LineSegment2d ls) {
		Pnt2d p1 = ls.getP1();
		Pnt2d p2 = ls.getP2();
		return String.format(Locale.US, "Line[{{%.2f,%.2f}, {%.2f,%.2f}}]", p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
	

//	public static void main(String[] args) {
//		LineSegment2d ls = new LineSegment2d(1,2,4,5);
//		System.out.println(Mathematica.toString(ls));
//		
//	}
}
