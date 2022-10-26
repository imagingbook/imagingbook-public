package imagingbook.spectral.fd;

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

}
