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
