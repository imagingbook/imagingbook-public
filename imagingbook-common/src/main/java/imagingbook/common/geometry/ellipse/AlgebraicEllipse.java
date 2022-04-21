package imagingbook.common.geometry.ellipse;

import static imagingbook.common.math.Arithmetic.sqr;
import static imagingbook.common.math.Matrix.multiply;
import static imagingbook.common.math.Matrix.normL2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

public class AlgebraicEllipse implements Ellipse {
	
	/**
	 * Represents an algebraic ellipse of the form
	 * A x^2 + B xy + C y^2 + D x + E y + F = 0
	 * 
	 * @author WB
	 */
	
	public final double A, B, C, D, E, F;
	
	public AlgebraicEllipse(double A, double B, double C, double D, double E, double F) {
		this(new double[] {A, B, C, D, E, F});
	}
	
	public AlgebraicEllipse(double[] p) {
		p = Matrix.normalize(p);
		A = p[0];
		B = p[1];
		C = p[2];
		D = p[3];
		E = p[4];
		F = p[5];
	}
	
	public static AlgebraicEllipse from(double[] p) {
		return (p == null) ? null : new AlgebraicEllipse(p);
	}
	
	@Override
	public double[] getParameters() {
		return new double[] {A, B, C, D, E, F};
	}
	
	public static AlgebraicEllipse from(GeometricEllipse ell) {
		double ra = ell.ra;
		double rb = ell.rb;
		double xc = ell.xc;
		double yc = ell.yc;
		double theta = ell.theta;
		
		double cosT = cos(theta);
		double sinT = sin(theta);
		
		double A = sqr(ra * sinT) + sqr(rb * cosT);
		double B = 2 * (sqr(rb) - sqr(ra)) * sinT * cosT;
		double C = sqr(ra * cosT) + sqr(rb * sinT);
		double D = -2 * A * xc - B * yc;
		double E = -2 * C * yc - B * xc;
		double F = A * sqr(xc) + B * xc * yc + C * sqr(yc) - sqr(ra * rb);
		
		return new AlgebraicEllipse(A, B, C, D, E, F);
	}
	
	public double getAlgebraicDistance(Pnt2d p) {
		double x = p.getX();
		double y = p.getY();

//		double s = 1.0 / Math.sqrt(4*A*C - sqr(B));
//		double s = 1.0 / (4*A*C - sqr(B));
		return A * sqr(x) + B * x * y + C * sqr(y) + D * x + E * y + F;
//		double a = s * A;
//		double b = s * B;
//		double c = s * C;
//		double d = s * D;
//		double e = s * E;
//		double f = s * F;
		
//		System.out.println("norm = " + (4*a*c - sqr(b)));
//		return a * sqr(x) + b * x * y + c * sqr(y) + d * x + e * y + f;
	}
	
	
//	public double getSampsonDistance1(Pnt2d p) {
//		double x = p.getX();
//		double y = p.getY();
//		double da = A * sqr(x) + B * x * y + C * sqr(y) + D * x + E * y + F;	// algebraic distance
//		double s = 2 * sqrt(sqr(A*x + B*y/2 + D) + sqr(B*x/2 + C*y + F));
//		return Math.abs(da) / s;
//	}
	
//	public double getSampsonDistance2(Pnt2d p) {
//		double x = p.getX();
//		double y = p.getY();
//		double da = A*sqr(x) + B*x*y + C*sqr(y) + D*x + E*y + F;	// algebraic distance
//		System.out.println("   da = " + da);
//		double s = 4 * (sqr(A*x + B*y/2 + D) + sqr(B*x/2 + C*y + F));
//		return sqr(da) / s;
//	}
	
	
	public double getSampsonDistance(Pnt2d p) {
		double x = p.getX();
		double y = p.getY();
		double ad = A * sqr(x) + B * x * y + C * sqr(y) + D * x + E * y + F;	// algebraic distance
//		System.out.println("   ad = " + ad);
		
//		RealVector J = Matrix.makeRealVector(2*A*x + B*y + D, B*x + 2*C*y + E);
//		System.out.println("J = " + Matrix.toString(J));
//		double nJ = J.getNorm();
		
		// norm of gradient of da at (x,y)
		double nGrad = Math.hypot(2*A*x + B*y + D, 2*C*y + B*x + E);
//		System.out.println("nJ = " + nJ);
		return ad / nGrad;
	}
	
	public double getGoncharovaDistance(Pnt2d p) {
		double x = p.getX();
		double y = p.getY();
		double G = A * sqr(x) + B * x * y + C * sqr(y) + D * x + E * y + F;	// algebraic distance
		
		double[] gV = {2*A*x + B*y + D, 2*C*y + B*x + E};
		double ngV = normL2(gV);
		double sd = G / ngV;
		
		double[][] H = {{ 2*A, B }, { B, 2*C }};
		double gd = sd * (1 + G * Matrix.dotProduct(multiply(gV, H), gV) / (2 * Math.pow(ngV, 4)));
		
		return gd;
	}
	
	
	// --------------------------------------------------------------------------
	
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s %s", 
				this.getClass().getSimpleName(), 
				Matrix.toString(getParameters()));
	}
	
	// --------------------------------------------------------------------------
	
	public static void main(String[] args) {
		PrintPrecision.set(9);
		GeometricEllipse eg  = new GeometricEllipse(150, 50, 150, 150, 0.5);
		System.out.println("eg = " + eg);
		AlgebraicEllipse ea = AlgebraicEllipse.from(eg);
		System.out.println("ea = " + ea);
		
		Pnt2d p = Pnt2d.from(300, 300);
		System.out.println("p  = " + p);
		Pnt2d pc = eg.getClosestPoint(p);
		System.out.println("pc = " + pc);
		
//		System.out.println("Sampson dist1(p) = " + ea.getSampsonDistance1(p));
//		System.out.println("Sampson dist2(p) = " + ea.getSampsonDistance2(p));
		System.out.println("Sampson distS(p) = " + ea.getSampsonDistance(p));
		System.out.println("Gonch distS(p) = " + ea.getGoncharovaDistance(p));
		
	}

}
