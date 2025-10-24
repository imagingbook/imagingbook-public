/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.sift.scalespace;

import ij.ImagePlus;
import ij.ImageStack;
import imagingbook.common.util.LinearContainer;
import imagingbook.common.util.PrintsToStream;

import java.io.PrintStream;

/**
 * <p>
 * This abstract class defines a generic hierarchical scale space, consisting of multiple "octaves". See Sec. 25.1.4. of
 * [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @param <OctaveT> the octave type
 * @author WB
 * @version 2022/11/20
 * @see ScaleLevel
 * @see ScaleOctave
 * @see GaussianScaleSpace
 * @see DogScaleSpace
 */
public abstract class HierarchicalScaleSpace<OctaveT extends ScaleOctave<?>> implements PrintsToStream {
	
	final int P;								// number of octaves
	final int Q; 								// number of levels per octave
	final double sigma_s;						// absolute scale of original image
	final double sigma_0;						// absolute base scale of first octave (level 0,0)
	final int botLevel, topLevel; 				// bottom and top level index in each octave
	final LinearContainer<OctaveT> octaves;		// array of scale space octaves 0,...,P-1
	
	/**
	 * Constructor (non-public).
	 * 
	 * @param P the number of scale space octaves
	 * @param Q the number of scale steps (levels) per octave
	 * @param sigma_s the assumed sampling scale (typ. 0.5)
	 * @param sigma_0 the base scale of level 0 
	 * @param botLevel the index of the bottom level in each octave
	 * @param topLevel the index of the to level in each octave
	 */
	HierarchicalScaleSpace(int P, int Q, double sigma_s, double sigma_0, int botLevel, int topLevel) {
		this.Q = Q;
		this.P = P;
		this.sigma_s = sigma_s;
		this.sigma_0 = sigma_0;
		this.botLevel = botLevel;
		this.topLevel = topLevel;
		this.octaves = new LinearContainer<>(0, P-1);
	}
	
	/**
	 * Returns the number of octaves in this scale space.
	 * @return the number of octaves
	 */
	public int getP() {
		return P;
	}
	
	/**
	 * Returns the number of scale levels in each octave of this scale space.
	 * @return the number of scale levels
	 */
	public int getQ() {
		return Q;
	}
	
	/**
	 * Returns the assumed sampling scale.
	 * @return the assumed sampling scale
	 */
	public double getSigma_s() {
		return sigma_s;
	}
	
	/**
	 * Returns the base scale assigned to level 0 of octave 0.
	 * @return the base scale
	 */
	public double getSigma_0() {
		return sigma_0;
	}

	/**
	 * Returns the bottom level index in each scale space octave (e.g., this is -1 for the Gaussian scale space used in
	 * SIFT).
	 *
	 * @return the bottom level index
	 */
	public int getBottomLevelIndex() {
		return this.botLevel;
	}

	/**
	 * Returns the top level index in each scale space octave (e.g., this is Q+1 for the Gaussian scale space used in
	 * SIFT).
	 *
	 * @return the top level index
	 */
	public int getTopLevelIndex() {
		return this.topLevel;
	}

	/**
	 * Returns a reference to the p-th octave in this scale space. Valid octave indexes are p = 0,..,P-1 (see
	 * {@link #getP()}).
	 *
	 * @param p the octave index
	 * @return the associated {@link ScaleOctave} instance
	 * @see #getP()
	 */
	public OctaveT getOctave(int p) {
		//return octaves[p];
		return octaves.getElement(p);
	}
	
	// used internally only
	void setOctave(int p, OctaveT oct) {
//		octaves[p] = oct;
		octaves.setElement(p, oct);
	}

	/**
	 * Returns the q-th scale space level of octave p in this scale space. Valid octave indexes are p = 0,..,P-1 (see
	 * {@link #getP()}).
	 *
	 * @param p the octave index
	 * @param q the (within-octave) level index
	 * @return the associated {@link ScaleLevel} instance
	 */
	public ScaleLevel getScaleLevel(int p, int q) {
		return getOctave(p).getLevel(q);
	}

	/**
	 * Returns the absolute scale (&sigma;) at scale level p, q.
	 *
	 * @param p the octave index
	 * @param q the (within-octave) level index
	 * @return the absolute level scale
	 */
	public double getAbsoluteScale(int p, float q) {
		double m = Q * p + q;
		return sigma_0 * Math.pow(2, m/Q);
	}

	/**
	 * Calculates and returns the real (unscaled) x-position for a local coordinate at the specified octave.
	 *
	 * @param p the octave index
	 * @param xp the (scale-level) local coordinate
	 * @return the original x-position
	 */
	public double getRealX(int p, double xp) {
		return Math.pow(2, p) * xp;	// TODO: optimize (precalculate Math.pow(p, 2))
	}

	/**
	 * Calculates and returns the real (unscaled) y-position for a local coordinate at the specified octave.
	 *
	 * @param p the octave index
	 * @param yp the scale-level) local coordinate
	 * @return the original y-position
	 */
	public double getRealY(int p, double yp) {
		return Math.pow(2, p) * yp;
	}
	
	// ----------------------------------------------------------------
	
	@Override
	public void printToStream(PrintStream strm) {
		strm.println("Hierarchical Scale Space (" + this.getClass().getSimpleName() + ")");
		for (ScaleOctave<?> oct : octaves) {
			oct.printToStream(strm);
			strm.println();
		}
	}
	
	// ----------------------------------------------------------------

	/**
	 * Returns the contents of this scale space as an array of ImageJ ({@link ImagePlus}) images, one for each octave.
	 * Each image contains a stack of frames, one for each scale level.
	 *
	 * @param title a string used to compose the title of the images
	 * @return an array of {@link ImagePlus} instances.
	 */
	public ImagePlus[] getImages(String title) {
		ImagePlus[] images = new ImagePlus[P];
		for (int p = 0; p < P; p++) {
//			ImageStack stk = octaves[p].getImageStack();
			ImageStack stk = octaves.getElement(p).getImageStack();
			images[p] = new ImagePlus(title + " Octave p=" + p, stk);
		}
		return images;
	}
	
}
