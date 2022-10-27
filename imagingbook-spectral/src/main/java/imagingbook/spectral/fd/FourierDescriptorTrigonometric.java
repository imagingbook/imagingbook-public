/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.spectral.fd;

import static imagingbook.common.math.Arithmetic.mod;
import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;
import imagingbook.common.math.Complex;


/**
 * Defines static methods to create Fourier descriptors
 * directly from 2D polygons without re-sampling or interpolation.
 * 
 * @author WB
 * @version 2022/10/24
 */
public abstract class FourierDescriptorTrigonometric {
	
	private FourierDescriptorTrigonometric() {}
	
	/**
	 * Creates a {@link FourierDescriptor} directly from
	 * the vertices of a closed polygon (without interpolation).
	 * For a given number (mp) of Fourier coefficient pairs,
	 * the resulting number of Fourier coefficients is M = 2 * mp + 1.
	 * 
	 * @param V sequence of 2D points representing a closed polygon
	 * @param mp the number of Fourier coefficient pairs (arbitrary)
	 * @return a new {@link FourierDescriptorTrigonometric} instance
	 */
	public static FourierDescriptor from(Pnt2d[] V, int mp) {
		Complex[] G = makeDftSpectrumTrigonometric(Utils.toComplexArray(V), mp);
		return new FourierDescriptor(G);
	}

	// ---------------------------------------------------------------------------
	
	private static Complex[] makeDftSpectrumTrigonometric(Complex[] g, int mp) {
		final int N = g.length;				// number of polygon vertices
		final int M = 2 * mp + 1;			// number of Fourier coefficients (always odd)
		final double[] dx = new double[N];		// dx[k] is the delta-x for polygon segment <k,k+1>
		final double[] dy = new double[N];		// dy[k] is the delta-y for polygon segment <k,k+1>
		final double[] lambda = new double[N];	// lambda[k] is the length of the polygon segment <k,k+1>
		final double[] L  = new double[N + 1]; 	// T[k] is the cumulated path length at polygon vertex k in [0,K]
        
        Complex[] G = new Complex[M];
        
        L[0] = 0;
        for (int i = 0; i < N; i++) {	// compute Dx, Dy, Dt and t tables
            dx[i] = g[(i + 1) % N].re - g[i].re;
            dy[i] = g[(i + 1) % N].im - g[i].im;
            lambda[i] = sqrt(sqr(dx[i]) + sqr(dy[i]));	// TODO: use hypot()
            if (Arithmetic.isZero(lambda[i])) {
        		throw new Error("zero-length polygon segment!");
        	}
            L[i+1] = L[i] + lambda[i];
        }
        
        double Ln = L[N];	// Ln is the closed polygon length
               
        // calculate DFT coefficient G[0]:
        double x0 = g[0].re; // V[0].getX();
        double y0 = g[0].im; // V[0].getY();
        double a0 = 0;
        double c0 = 0;
        for (int i = 0; i < N; i++) {	// for each polygon vertex
        	double s = (sqr(L[i+1]) - sqr(L[i])) / (2 * lambda[i]) - L[i];
        	double xi = g[i].re; // V[i].getX();
        	double yi = g[i].im; // V[i].getY();
        	a0 = a0 + s * dx[i] + (xi - x0) * lambda[i];
        	c0 = c0 + s * dy[i] + (yi - y0) * lambda[i];
        }
        //G[0] = new Complex(x0 + a0/Ln, y0 + c0/Ln);
        setCoefficient(G, 0, new Complex(x0 + a0/Ln, y0 + c0/Ln));
        
        // calculate remaining FD pairs G[-m], G[+m] for m = 1,...,Mp
        for (int m = 1; m <= mp; m++) {	// for each FD pair
        	double w = 2 * PI * m / Ln;
        	double a = 0, c = 0;
        	double b = 0, d = 0;
            for (int i = 0; i < N; i++) {	//	for each polygon vertex
            	double w0 = w * L[i];				
            	double w1 = w * L[(i + 1) % N];		
                double dCos = cos(w1) - cos(w0);
                a = a + dCos * (dx[i] / lambda[i]);
                c = c + dCos * (dy[i] / lambda[i]);
                double dSin = sin(w1) - sin(w0);
                b = b + dSin * (dx[i] / lambda[i]);
                d = d + dSin * (dy[i] / lambda[i]);
            }
            double s = Ln / sqr(2 * PI * m);
            setCoefficient(G, +m, new Complex(s * (a + d), s * (c - b)));
            setCoefficient(G, -m, new Complex(s * (a - d), s * (b + c)));
        }
        
        return G;
	}
	
	private static void setCoefficient(Complex[] C, int m, Complex z) {
		C[mod(m, C.length)] = z;
	}
//	private static void setCoefficient(Complex[] G, int m, Complex z) {
//		int mm = Arithmetic.mod(m, G.length);
//		G[mm] = new Complex(z);
//	}

}
