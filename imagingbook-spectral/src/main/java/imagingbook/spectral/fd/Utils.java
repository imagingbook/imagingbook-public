package imagingbook.spectral.fd;

import java.awt.geom.Path2D;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Complex;

public abstract class Utils {

//	static double crossProduct(Complex c1, Complex c2) {
//		return c1.re * c2.im - c1.im * c2.re;
//	}

	public static Complex[] toComplexArray(Pnt2d[] points) {
		int N = points.length;
		Complex[] samples = new Complex[N];
		for (int i = 0; i < N; i++) {
			samples[i] = new Complex(points[i].getX(), points[i].getY());
		}
		return samples;
	}
	
	public static Path2D toPath(Complex[] C) {
		Path2D path = new Path2D.Float();
		path.moveTo(C[0].re, C[0].im);
		for (int i = 1; i < C.length; i++) {
			path.lineTo(C[i].re, C[i].im);
		}
		path.closePath();
		return path;
	}
	
}
