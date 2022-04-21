package imagingbook.common.geometry.line;

import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;

/**
 * This class represents a line in slope-intercept form: y = k x + d.
 * Instances are immutable. Not all possible lines in the 2D plane
 * can be represented.
 * TODO: add implementation of Curve2d
 */
public class SlopeInterceptLine {
	
	private final double k, d;

	public SlopeInterceptLine(double k, double d) {
		this.k = k;
		this.d = d;
	}
	
	public double getK() {
		return k;
	}

	public double getD() {
		return d;
	}
	
	public double[] getParameters() {
		return new double[] {k, d};
	}
	
	public static SlopeInterceptLine from(AlgebraicLine al) {
		double[] p = al.getParameters(); 	// =(A,B,C)
		if (Arithmetic.isZero(p[1])) { 		// B == 0?
			throw new IllegalArgumentException("cannot convert vertical line (B=0)");
		}
		return new SlopeInterceptLine(-p[0]/p[1], -p[2]/p[1]);	// =(-A/B, -C/B)
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <k=%.3f, d=%.3f>",
				this.getClass().getSimpleName(), k, d);
	}
	
	// --------------------------------------------------
	
	public static void main (String[] args) {
		Pnt2d p1 = Pnt2d.from(1, 2);
		Pnt2d p2 = Pnt2d.from(4, 3);
		AlgebraicLine al1 = AlgebraicLine.from(p1, p2);
		System.out.println("al1 = " + al1);
		SlopeInterceptLine sl = SlopeInterceptLine.from(al1);
		System.out.println("sl = " + sl);
		AlgebraicLine al2 = AlgebraicLine.from(sl);
		System.out.println("al2 = " + al2);
		System.out.println("al1 = al2 ? " + al1.equals(al2));
	}
	/*
	al1 = AlgebraicLine <a=-0.316, b=0.949, c=-1.581>
	sl = SlopeInterceptLine <k=0.333, d=1.667>
	al2 = AlgebraicLine <a=0.316, b=-0.949, c=1.581>
	al1 = al2 ? true
	*/
}
