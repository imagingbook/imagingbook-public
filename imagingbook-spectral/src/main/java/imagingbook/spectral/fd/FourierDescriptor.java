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

import java.awt.geom.Path2D;
import java.util.Locale;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import imagingbook.common.math.Complex;

/**
 * <p>
 * This class represents elliptic Fourier descriptors. See
 * Ch.26 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction Using Java</em>, 2nd ed, Springer (2016).
 * </p>
 * 
 * @author WB
 * @version 2022/10/24
 * 
 * @see FourierDescriptorUniform
 * @see FourierDescriptorTrigonometric
 */
public class FourierDescriptor {

	public static final int MinReconstructionSamples = 50;

	private final int mp;		// number of Fourier pairs
	private final Complex[] G;	// complex-valued DFT spectrum (of length 2 * mp + 1)
	private final double scale;	// scale factor to apply for reconstructing original (sample) size
	
	/**
	 * Constructor using the default scale (1).
	 * @param G a complex-valued DFT spectrum
	 */
	public FourierDescriptor(Complex[] G) {
		this(G, 1.0);
	}
	
	/**
	 * Constructor using a specific scale.
	 * @param G a complex-valued DFT spectrum
	 * @param scale the reconstruction scale
	 */
	public FourierDescriptor(Complex[] G, double scale) {
		this.mp = (G.length - 1) / 2;
		this.G = truncate(G, this.mp);	// always make G odd-sized
		this.scale = scale;
	}
	
	/**
	 * Constructor for cloning Fourier descriptors.
	 * @param fd an existing instance
	 */
	public FourierDescriptor(FourierDescriptor fd) {
		this(fd.G.clone(), fd.scale);
	}

	// ----------------------------------------------------------------

	/**
	 * <p>
	 * Truncate the given DFT spectrum to the specified number of coefficient pairs.
	 * Truncation removes the highest-frequency coefficients. The resulting spectrum
	 * is always odd-sized. If the number of coefficient pairs is zero, only
	 * coefficient 0 remains, i.e., the new spectrum has length 1. An exception is
	 * thrown if the original spectrum has fewer coefficient pairs than needed.
	 * For example, for an even-sized spectrum with 10 coefficients G[m] = a,b,...,j,
	 * </p>
	 * <pre>
	 * m    = 0 1 2 3 4 5 6 7 8 9
	 * G[m] = a b c d e f g h i j </pre>
	 * <p>
	 * the truncated spectrum for mp = 3 is
	 * </p>
	 * <pre>
	 * m'    = 0 1 2 3 4 5 6
	 * G[m'] = a b c d h i j </pre>
	 * <p>
	 * I.e., the highest frequency coefficients (e, f, g) of the original spectrum are 
	 * removed.
	 * 
	 * @param G  the original DFT spectrum (with length greater than 2 * mp + 1)
	 * @param mp the number of remaining coefficient pairs.
	 * @return the truncated spectrum (always odd-sized)
	 */
	public static Complex[] truncate(Complex[] G, int mp) {
		if (mp < 0) {
			throw new IllegalArgumentException("number of coefficient pairs must be >= 0 but is " + mp);
		}
		int M = G.length;
		int Mnew = 2 * mp + 1;
		if (Mnew > M) {
			throw new IllegalArgumentException("spectrum has fewer coefficient pairs than needed");
		}
		Complex[] Gnew = new Complex[Mnew];
		// fill the new spectrum:
		for (int m = 0; m < Mnew; m++) {
			if (m <= Mnew / 2)
				Gnew[m] = G[m];
			else
				Gnew[m] = G[M - Mnew + m];
		}
		return Gnew;
	}
	
	/**
	 * Truncates the given DFT spectrum to the maximum number of coefficient pairs.
	 * The resulting spectrum is always odd-sized. If the original spectrum is
	 * odd-sized, the same spectrum is return, otherwise the single
	 * highest-frequency coefficient is removed.
	 * 
	 * @param G the original DFT spectrum
	 * @return the truncated spectrum (always odd-sized)
	 * @see #truncate(Complex[], int)
	 */
	public static Complex[] truncate(Complex[] G) {
		return truncate(G, (G.length - 1) / 2);
	}

	// ----------------------------------------------------------------

	/**
	 * Returns the size (length) of this Fourier descriptor's array of
	 * DFT coefficients G. The resulting value is always odd.
	 * @return the size of the DFT coefficient array (M)
	 */
	public int size() {
		return G.length;
	}
	
	/**
	 * Returns the scale of this Fourier descriptor, that is, the
	 * factor required to scale it to its original (sample) size.
	 * 
	 * @return the scale factor
	 */
	public double getScale() {
		return scale;
	}
	
	/**
	 * Returns the number of Fourier coefficient pairs for this descriptor.
	 * The result is (M-1)/2, M being the size of the DFT coefficient array G.
	 * @return the number of Fourier coefficient pairs
	 */
	public int getMp() {
		return this.mp;
	}
	
	/**
	 * Returns the array G = {G[0], ..., G[M-1]} of complex-valued DFT coefficients. 
	 * @return the array of DFT coefficients
	 */
	public Complex[] getCoefficients() {
		return G;
	}
	
	/**
	 * Returns the complex-valued DFT coefficient with the specified
	 * frequency index m. The returned coefficient is G[m mod G.length].
	 * Unique coefficients are returned for m = 0, ..., M-1,
	 * where M is the size of the DFT coefficient array, or
	 * m = -mp, ..., +mp where mp is the number of coefficient pairs.
	 * 
	 * @param m frequency index (positive or negative)
	 * @return the associated DFT coefficient
	 */
	public Complex getCoefficient(int m) {
		return new Complex(G[getCoefficientIndex(m)]);
	}

	/**
	 * Returns the coefficient array index for the specified frequency index,
	 * which may be positive or negative.
	 * 
	 * @param m frequency index (positive or negative)
	 * @return the coefficient array index
	 */
	public int getCoefficientIndex(int m) {
		return mod(m, G.length);
	}

	// ----------------------------------------------------------------
	// Invariance-related methods
	// ----------------------------------------------------------------
	
	/**
	 * Makes this Fourier descriptor invariant to scale, start-point and
	 * rotation. The descriptors center position (coefficient 0) is preserved.
	 * Returns multiple candidate descriptors, since start-point invariance
	 * is not unique. The original (this) descriptor is not modified.
	 * 
	 * @return an array of modified Fourier descriptors
	 */
	public FourierDescriptor[] makeInvariant() {
		// Step 1: make scale invariant
		FourierDescriptor fdS = this.makeScaleInvariant();

		// Step 2: make start-point invariant (not unique, produces 2 versions)
		FourierDescriptor[] fdAB = fdS.makeStartPointInvariant();

		// Step 3: make all versions rotation invariant
		for (int i = 0; i < fdAB.length; i++) {
			fdAB[i] = fdAB[i].makeRotationInvariant();
		}

		return fdAB;
	}
	
	// ----------------------------------------------------------------
	
	/**
	 * Returns a new scale invariant Fourier descriptor by normalizing the L2 norm
	 * of the sub-vector {G[-mp], ..., G[-1], G[1], ..., G[mp]}. Coefficient G_0
	 * remains unmodified. The original Fourier descriptor is not modified.
	 * 
	 * @return a new scale-normalized Fourier descriptor
	 */
	public FourierDescriptor makeScaleInvariant() {
		double sum = 0;
		for (int i = 1; i < G.length; i++) {
			sum = sum + G[i].abs2();
		}
		double scale = Math.sqrt(sum);	// = L2 norm
		double s = 1 / scale;
		
		Complex[] G2 = new Complex[G.length];
		G2[0] = G[0];
		for (int i = 1; i < G2.length; i++) {
			G2[i] = G[i].multiply(s);
		}
		return new FourierDescriptor(G2, scale);
	}

	/**
	 * Returns a pair of start-point normalized Fourier descriptors.
	 * The original Fourier descriptor is not modified.
	 * 
	 * @return a pair of start-point normalized Fourier descriptors
	 */
	public FourierDescriptor[] makeStartPointInvariant() {
		double phi = getStartPointPhase();

		Complex[] Ga = shiftStartPointPhase(phi);
		Complex[] Gb = shiftStartPointPhase(phi + Math.PI);
		
		return new FourierDescriptor[] {
				new FourierDescriptor(Ga, this.scale), 
				new FourierDescriptor(Gb, this.scale)};
	}
	
	private Complex[] shiftStartPointPhase(double phi) {
		Complex[] Gnew = G.clone();
		for (int m = -mp; m <= mp; m++) {
			if (m != 0) {
				int k = getCoefficientIndex(m);
				Gnew[k] = Gnew[k].rotate(m * phi);
			}
		}
		return Gnew;
	}
	
	/**
	 * Calculates the 'canonical' start point. This version uses 
	 * (a) a coarse search for a global maximum of fp() and subsequently 
	 * (b) a numerical optimization using Brent's method
	 * (implemented with Apache Commons Math).
	 * 
	 * @param mp number of Fourier coefficient pairs
	 * @return the "canonical" start point phase
	 */
	private double getStartPointPhase() {
		
		// the 1-dimensional target function to be optimized:
		UnivariateFunction fp = new UnivariateFunction() {	// function to be maximized
			/** 
			 * The value returned is the sum of the cross products of the FD pairs,
			 * with all coefficients rotated to the given start point phase phi.
			 * TODO: This could be made a lot more efficient!
			 */
			@Override
			public double value(double phi) {
				double sum = 0;
				for (int m = 1; m <= mp; m++) {
					Complex Gm = getCoefficient(-m).rotate(-m * phi);
					Complex Gp = getCoefficient( m).rotate( m * phi);
					sum = sum + Gp.re * Gm.im - Gp.im * Gm.re;	// "cross product" Gp x Gm
				}
				return sum;
			}
		};
		
		// search for the global maximum in coarse steps
		double cmax = Double.NEGATIVE_INFINITY;
		int kmax = -1;
		int K = 25;	// number of steps over 180 degrees
		for (int k = 0; k < K; k++) {
			final double phi = Math.PI * k / K; 	// phase to evaluate
			final double c = fp.value(phi);
			if (c > cmax) {
				cmax = c;
				kmax = k;
			}
		}
		// optimize using previous and next point as the bracket.
		double minPhi = Math.PI * (kmax - 1) / K;
		double maxPhi = Math.PI * (kmax + 1) / K;	

		UnivariateOptimizer optimizer = new BrentOptimizer(1E-4, 1E-6);
		int maxIter = 20;
		UnivariatePointValuePair result = optimizer.optimize(
				new MaxEval(maxIter),
				new UnivariateObjectiveFunction(fp),
				GoalType.MAXIMIZE,
				new SearchInterval(minPhi, maxPhi)
				);
		double phi0 = result.getPoint();
		return phi0;	// the canonical start point phase
	}
	
	/**
	 * Returns a new rotation invariant Fourier descriptor by
	 * applying complex rotation to all coefficients (except coefficient 0).
	 * The original Fourier descriptor is not modified.
	 * 
	 * @return a new rotation-normalized Fourier descriptor
	 */
	public FourierDescriptor makeRotationInvariant() {
		Complex z = new Complex(0,0);
		for (int m = 1; m <= mp; m++) {
			final double w = 1.0 / m;	// weighting factor emphasizing low-frequency components
			z = z.add(getCoefficient(-m).multiply(w));
			z = z.add(getCoefficient(+m).multiply(w));
		}
		double beta = z.arg();
		Complex[] G2 = G.clone();
		for (int i = 1; i < G2.length; i++) {
			G2[i] = G[i].rotate(-beta);
		}
		
		return new FourierDescriptor(G2, this.scale);
	}

	// -----------------------------------------------------------------

	/**
	 * Returns a new translation invariant Fourier descriptor by
	 * setting coefficient 0 to zero.
	 * The original Fourier descriptor is not modified.
	 * This method is not used for shape invariance calculation,
	 * since position is not shape-relevant.
	 * 
	 * @return a new translation-normalized Fourier descriptor
	 */
	public FourierDescriptor makeTranslationInvariant() {
		Complex[] G2 = G.clone();
		G2[0] = Complex.ZERO;
		return new FourierDescriptor(G2, this.scale);
	}
	
	// ------------------------------------------------------------------
	// Reconstruction-related methods:
	// ------------------------------------------------------------------

	/**
	 * Calculates and returns a reconstruction of the associated
	 * 2D shape with N sample points using the full DFT spectrum.
	 * 
	 * @param N number of samples
	 * @return reconstructed shape points
	 */
	public Complex[] getReconstruction(int N) {
		return getReconstruction(N, mp);
//		Complex[] S = new Complex[N];
//		for (int i = 0; i < N; i++) {
//			double t = (double) i / N;
//			S[i] = getReconstructionPoint(t);
//		}
//		return S;
	}

	/**
	 * Calculate a reconstruction from the partial DFT spectrum with N sample
	 * points and using Mp coefficient pairs.
	 * 
	 * @param N number of samples
	 * @param p number of coefficient pairs
	 * @return reconstructed shape points
	 */
	public Complex[] getReconstruction(int N, int p) {
		p = Math.min(p, this.mp);
		Complex[] S = new Complex[N];
		for (int i = 0; i < N; i++) {
			double t = (double) i / N;
			S[i] = getReconstructionPoint(t, p);
		}
		return S;
	}

	/**
	 * Reconstructs a single spatial point from the complete FD
	 * at the fractional path position t in [0,1].
	 * 
	 * @param t path position
	 * @return single contour point
	 */
	public Complex getReconstructionPoint(double t) {
		return getReconstructionPoint(t, mp);
	}

	public Complex getReconstructionPoint(double t, int p) {
		p = Math.min(p, mp);
		double x = G[0].re;
		double y = G[0].im;
		for (int m = -p; m <= +p; m++) {
			if (m != 0) {
				Complex Gm = getCoefficient(m);
				double A = scale * Gm.re;
				double B = scale * Gm.im;
				double phi = 2 * Math.PI * m * t;
				double sinPhi = Math.sin(phi);
				double cosPhi = Math.cos(phi);
				x = x + A * cosPhi - B * sinPhi;
				y = y + A * sinPhi + B * cosPhi;
			}
		}
		return new Complex(x, y);
	}

//	/**
//	 * Reconstructs a single spatial point from this FD using
//	 * coefficients [mm,...,mp] = [m-,...,m+] at the fractional path position t in [0,1].
//	 * 
//	 * @param t path position
//	 * @param mmin most negative frequency index
//	 * @param mmax most positive frequency index
//	 * @return single contour point
//	 */
//	private Complex getReconstructionPoint(double t, int mmin, int mmax) {
//		double x = G[0].re;
//		double y = G[0].im;
//		for (int m = mmin; m <= mmax; m++) {
//			if (m != 0) {
//				Complex Gm = getCoefficient(m);
//				double A = scale * Gm.re;
//				double B = scale * Gm.im;
//				double phi = 2 * Math.PI * m * t;
//				double sinPhi = Math.sin(phi);
//				double cosPhi = Math.cos(phi);
//				x = x + A * cosPhi - B * sinPhi;
//				y = y + A * sinPhi + B * cosPhi;
//			}
//		}
//		return new Complex(x, y);
//	}

	// -----------------------------------------------------------------------
	// Ellipse path-related methods:
	// -----------------------------------------------------------------------

	// TODO: Use actual ellipse shape, check offsets!
	public Path2D makeEllipse(Complex G1, Complex G2, int m, double xOffset, double yOffset) {
		Path2D path = new Path2D.Float();
		int recPoints = Math.max(MinReconstructionSamples, G.length * 3);
		for (int i = 0; i < recPoints; i++) {
			double t = (double) i / recPoints;
			Complex p1 = this.getEllipsePoint(G1, G2, m, t);
			double xt = p1.re;
			double yt = p1.im;
			if (i == 0) {
				path.moveTo(xt + xOffset, yt + yOffset);
			}
			else {
				path.lineTo(xt + xOffset, yt + yOffset);
			}
		}
		path.closePath();
		return path;
	}

	/**
	 * Get the reconstructed point for two DFT coefficients G1, G2 at a given
	 * position t.
	 * @param G1 first coefficient
	 * @param G2 second coefficient
	 * @param m frequency number
	 * @param t contour position
	 * @return reconstructed point
	 */
	public Complex getEllipsePoint(Complex G1, Complex G2, int m, double t) {
		Complex p1 = getReconstructionPoint(G1, -m, t);
		Complex p2 = getReconstructionPoint(G2, m, t);
		return p1.add(p2);
	}

	/**
	 * Returns the spatial point reconstructed from a single
	 * DFT coefficient 'Gm' with frequency 'm' at 
	 * position 't' in [0,1].
	 * 
	 * @param Gm single DFT coefficient
	 * @param m frequency index
	 * @param t contour position
	 * @return reconstructed point
	 */
	private Complex getReconstructionPoint(Complex Gm, int m, double t) {
		double wm = 2 * Math.PI * m;
		double Am = Gm.re;
		double Bm = Gm.im;
		double cost = Math.cos(wm * t);
		double sint = Math.sin(wm * t);
		double xt = Am * cost - Bm * sint;
		double yt = Bm * cost + Am * sint;
		return new Complex(xt, yt);
	}

	/**
	 * Reconstructs the shape using all FD pairs.
	 * 
	 * @return reconstructed shape
	 */
	public Path2D makeFourierPairsReconstruction() {
		return  makeFourierPairsReconstruction(mp);
	}

	/**
	 * Reconstructs the shape obtained from FD-pairs 0,...,Mp as a polygon (path).
	 * 
	 * @param pairs number of Fourier coefficient pairs
	 * @return reconstructed shape
	 */
	public Path2D makeFourierPairsReconstruction(int pairs) {
		pairs = Math.min(pairs, mp);
		int recPoints = Math.max(MinReconstructionSamples, G.length * 3);
		Path2D path = new Path2D.Float();
		for (int i = 0; i < recPoints; i++) {
			double t = (double) i / recPoints;
			Complex pt = new Complex(getCoefficient(0));	// assumes that coefficient 0 is never scaled
			// calculate a particular reconstruction point 
			for (int m = 1; m <= pairs; m++) {
				Complex ep = getEllipsePoint(getCoefficient(-m), getCoefficient(m), m, t);
				pt = pt.add(ep.multiply(scale));
			}
			double xt = pt.re; 
			double yt = pt.im; 
			if (i == 0) {
				path.moveTo(xt, yt);
			}
			else {
				path.lineTo(xt, yt);
			}
		}
		path.closePath();
		return path;
	}


	
	// -----------------------------------------------------------------
	// -----------------------------------------------------------------

//	/**
//	 * For testing: apply shape rotation to this FourierDescriptor (phi in radians)
//	 * 
//	 * @param phi rotation angle
//	 */
//	public void rotate(double phi) {
//		rotate(G, phi);
//	}

	/**
	 * For testing: apply shape rotation to this FourierDescriptor (phi in radians)
	 * 
	 * @param shape complex point
	 * @param phi angle
	 */
	public static Complex[] rotateShape(Complex[] shapeOrig, double phi) {
		Complex[] shape = shapeOrig.clone();
		Complex rot = new Complex(phi);
		for (int m = 0; m < shape.length; m++) {
			shape[m] = shape[m].multiply(rot);
		}
		return shape;
	}
	
//	public static Complex[] scaleShape(Complex[] shapeOrig, double scale) {
//		Complex[] shape = shapeOrig.clone();
//		for (int m = 1; m < shape.length; m++) {
//			shape[m] = shape[m].multiply(rot);
//		}
//		return shape;
//	}

//	/**
//	 * Apply a particular start-point phase shift
//	 * 
//	 * @param phi start point phase
//	 * @param mp most positive/negative frequency index
//	 */
//	private void shiftStartPointPhase(double phi) {
//		for (int m = -mp; m <= mp; m++) {
//			if (m != 0) {
//				setCoefficient(m, getCoefficient(m).rotate(m * phi));
//			}
//		}
//	}
	


	
	public double distanceComplex(FourierDescriptor fd2) {
		return distanceComplex(fd2, mp);
	}

	public double distanceComplex(FourierDescriptor fd2, int p) {
		FourierDescriptor fd1 = this;
		p = Math.min(p, mp);
		double sum = 0;
		for (int m = -p; m <= p; m++) {
			if (m != 0) {
				Complex G1m = fd1.getCoefficient(m);
				Complex G2m = fd2.getCoefficient(m);
				sum = sum + sqr(G1m.re - G2m.re) + sqr(G1m.im - G2m.im);
			}
		}
		return Math.sqrt(sum);
	}

	public double distanceMagnitude(FourierDescriptor fd2) {
		return distanceMagnitude(fd2, mp);
	}

	public double distanceMagnitude(FourierDescriptor fd2, int pairs) {
		FourierDescriptor fd1 = this;
		pairs = Math.min(pairs, mp);
		double sum = 0;
		for (int m = -pairs; m <= pairs; m++) {
			if (m != 0) {
				double mag1 = fd1.getCoefficient(m).abs();
				double mag2 = fd2.getCoefficient(m).abs();
				double dmag = mag2 - mag1;
				sum = sum + (dmag * dmag);
			}
		}
		return Math.sqrt(sum);
	}
	
	// ---------------------------------------------------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s: mp=%d scale=%.3f", this.getClass().getSimpleName(), mp, scale);
	}

}
