/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fd;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;
import imagingbook.common.math.Complex;

/**
 * <p>
 * Defines static methods to create Fourier descriptors from uniformly sampled point sequences. See Ch. 26 of [1] for
 * additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction Using Java</em>, 2nd ed,
 * Springer (2016).
 * </p>
 *
 * @author WB
 * @version 2022/10/24
 */
public abstract class FourierDescriptorUniform {
	
	private FourierDescriptorUniform() {}

	/**
	 * Creates a new Fourier descriptor from a uniformly sampled polygon V with the maximum number of Fourier
	 * coefficient pairs. The length of the resulting DFT spectrum equals V.length.
	 *
	 * @param V a polygon
	 * @return a new {@link FourierDescriptorUniform} instance
	 */
	public static FourierDescriptor from(Pnt2d[] V) {
		return from(FourierDescriptor.toComplexArray(V));
	}

	/**
	 * Creates a new Fourier descriptor from a uniformly sampled polygon V with Mp coefficient pairs. The length of the
	 * resulting DFT spectrum is 2 * mp + 1, i.e., it must be assured that Mp &lt; (V.length - 1) &divide; 2.
	 *
	 * @param V a polygon
	 * @param mp number of coefficient pairs
	 * @return a new {@link FourierDescriptorUniform} instance
	 */
	public static FourierDescriptor from(Pnt2d[] V, int mp) {
		return from(FourierDescriptor.toComplexArray(V), mp);
	}
	
	
	// -------------------------------------------------------------------

	/**
	 * Creates a new Fourier descriptor from a uniformly sampled polygon V with the maximum number of Fourier
	 * coefficient pairs. The length of the resulting DFT spectrum equals V.length.
	 *
	 * @param V a polygon
	 * @return a new {@link FourierDescriptorUniform} instance
	 */
	public static FourierDescriptor from(Complex[] V) {
		return new FourierDescriptor(DFT(V));
	}

	/**
	 * Creates a new Fourier descriptor from a uniformly sampled polygon V with Mp coefficient pairs. The length of the
	 * resulting DFT spectrum is 2 * mp + 1, i.e., it must be assured that Mp &lt; (V.length - 1) &divide; 2.
	 *
	 * @param V a sequence of uniformly-spaced 2D sample points
	 * @param mp number of coefficient pairs
	 * @return a new {@link FourierDescriptorUniform} instance
	 */
	public static FourierDescriptor from(Complex[] V, int mp) {
		if (mp > (V.length - 1) / 2) {
			throw new IllegalArgumentException("number of Fourier pairs (mp) may not be greater than " 
							+ ((V.length - 1) / 2));
		}
		return new FourierDescriptor(DFT(V, 2 * mp + 1));
	}
	
	// -------------------------------------------------------------------

	/**
	 * DFT with the resulting spectrum (signal, if inverse) of the same length as the input vector g. Not using sin/cos
	 * tables.
	 *
	 * @param g signal vector
	 * @return DFT spectrum
	 */
	private static Complex[] DFT(Complex[] g) {
		int M = g.length;
//		double[] cosTable = makeCosTable(M);	// cosTable[m] == cos(2*pi*m/M)
//		double[] sinTable = makeSinTable(M);	// sinTable[m] == sin(2*pi*m/M)
		Complex[] G = new Complex[M];
		double s = 1.0 / M; //common scale factor (fwd/inverse differ!)
		for (int m = 0; m < M; m++) {
			double Am = 0;
			double Bm = 0;
			for (int k = 0; k < M; k++) {
				double x = g[k].re;
				double y = g[k].im;
				double phi = 2 * Math.PI * m * k / M;
				double cosPhi = Math.cos(phi);
				double sinPhi = Math.sin(phi);
				Am = Am + x * cosPhi + y * sinPhi;
				Bm = Bm - x * sinPhi + y * cosPhi;
			}
			G[m] = new Complex(s * Am, s * Bm);
		}
		return G;
	}


	/**
	 * As {@link #DFT(Complex[])}, with the length of the resulting spectrum (or signal, if inverse) explicitly
	 * specified.
	 *
	 * @param g signal vector
	 * @param MM length of the resulting DFT spectrum
	 * @return DFT spectrum
	 */
	private static Complex[] DFT(Complex[] g, int MM) {
		int M = g.length;
		if (MM > M) {
			throw new IllegalArgumentException("truncated spectrum must be shorter than original MM = " + MM);
		}
//		double[] cosTable = makeCosTable(M);	// cosTable[m] == cos(2*pi*m/M)
//		double[] sinTable = makeSinTable(M);	// sinTable[m] == sin(2*pi*m/M)
		Complex[] G = new Complex[MM];
		double s = 1.0 / M; //common scale factor (fwd/inverse differ!)
		
//		for (int j = Mp/2-Mp+1; j <= Mp/2; j++) {
		for (int j = -MM/2; j <= (MM-1)/2; j++) {
			double Am = 0;
			double Bm = 0;
			for (int k = 0; k < M; k++) {
				double x = g[k].re;
				double y = g[k].im;
				//int mk = (m * k) % M; double phi = 2 * Math.PI * mk / M;
				double phi = 2 * Math.PI * j * k / M;	
				double cosPhi = Math.cos(phi);
				double sinPhi = Math.sin(phi);
				Am = Am + x * cosPhi + y * sinPhi;
				Bm = Bm - x * sinPhi + y * cosPhi;
			}
			G[Arithmetic.mod(j, MM)] = new Complex(s * Am, s * Bm);
		}
		return G;
	}

//	private double[] makeCosTable(int M) {
//		double[] cosTab = new double[M];
//		for (int m = 0; m < M; m++) {
//			cosTab[m] = Math.cos(2 * Math.PI * m / M);
//		}
//		return cosTab;
//	}

//	private double[] makeSinTable(int M) {
//		double[] sinTab = new double[M];
//		for (int m = 0; m < M; m++) {
//			sinTab[m] = Math.sin(2 * Math.PI * m / M);
//		}
//		return sinTab;
//	}
	
}
