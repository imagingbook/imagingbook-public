package imagingbook.common.geometry.circle;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

import java.util.Locale;

/**
 * Represents an algebraic circle of the form
 * A * (x^2 + y^2) + B * x + C * y + D = 0.
 * TODO: add implementation of Curve2d
 * 
 * @author WB
 *
 */
public class AlgebraicCircle  implements Circle {

	private final double A, B, C, D;
	
	public AlgebraicCircle(double A, double B, double C, double D) {
		if (isZero(A) || sqr(B) + sqr(C) - 4 * A * D < 0) {
			throw new IllegalArgumentException("illegal circle parameters");
		}
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
	}
	
	public AlgebraicCircle(double[] p) {
		this(p[0], p[1], p[2], p[3]);
	}
	
	public static AlgebraicCircle from(GeometricCircle gc) {
		double A = 1 / (2 * gc.getR());
		double B = -2 * A * gc.getXc();
		double C = -2 * A * gc.getYc();
		double D = (sqr(B) + sqr(C) - 1) / (4 * A);
		return new AlgebraicCircle(A, B, C, D);
	}
	
	@Override
	public double[] getParameters() {
		return new double[] {A, B, C, D};
	}
	
	public AlgebraicCircle normalize() {
		double s = sqrt(sqr(B) + sqr(C) - 4 * A * D);
		System.out.println("s = " + s);
		return new AlgebraicCircle(s*A, s*B, s*C, s*D);
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s [A=%f, B=%f, C=%f, D=%f]", 
				AlgebraicCircle.class.getSimpleName(), A, B, C, D);
	}
	
	// ------------------------------------------------------------------
	
	public static void main(String[] args) {
		
		GeometricCircle gc1 = new GeometricCircle(200, -300, 777);
		System.out.println(gc1.toString());
		
		AlgebraicCircle ac1 = AlgebraicCircle.from(gc1);
		System.out.println(ac1.toString());
		
		GeometricCircle gc2 = GeometricCircle.from(ac1);
		System.out.println(gc2.toString());
		
		AlgebraicCircle ac2 = ac1.normalize();
		System.out.println(ac2.toString());
		
		GeometricCircle gc3 = GeometricCircle.from(ac2);
		System.out.println(gc3.toString());
		
	}
	
	
}
