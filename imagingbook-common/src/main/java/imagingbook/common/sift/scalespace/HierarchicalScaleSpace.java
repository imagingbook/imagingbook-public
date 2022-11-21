/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.sift.scalespace;

import java.io.PrintStream;

import imagingbook.common.util.PrintsToStream;


/**
 * <p>
 * This abstract class defines a generic hierarchical scale space, consisting of
 * multiple "octaves". See Sec. 25.1.4. of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/20
 * @see ScaleLevel
 * @see ScaleOctave
 * @see GaussianScaleSpace
 * @see DogScaleSpace
 */
public abstract class HierarchicalScaleSpace implements PrintsToStream {
	
	protected final int P;						// number of octaves
	protected final int Q; 						// number of levels per octave
	protected final double sigma_s;				// absolute scale of original image
	protected final double sigma_0;				// absolute base scale of first octave (level 0,0)
	protected final int botLevel, topLevel; 	// bottom and top level index in each octave
	protected final ScaleOctave[] octaves;		
	
	HierarchicalScaleSpace(int P, int Q, double sigma_s, double sigma_0, int botLevel, int topLevel) {
		this.Q = Q;
		this.P = P;
		this.sigma_s = sigma_s;
		this.sigma_0 = sigma_0;
		this.botLevel = botLevel;
		this.topLevel = topLevel;
		octaves = new ScaleOctave[P];	
	}
	
	/**
	 * Returns the number of octaves in this scale space.
	 * 
	 * @return the number of octaves
	 */
	public int getP() {
		return P;
	}
	
	/**
	 * Returns the number of scale levels in each octave of this scale space.
	 * 
	 * @return the number of scale levels
	 */
	public int getQ() {
		return Q;
	}
	
	public double getSigma_s() {
		return sigma_s;
	}
	
	public double getSigma_0() {
		return sigma_0;
	}
	
	public int getBottomLevelIndex() {
		return this.botLevel;
	}
	
	public int getTopLevelIndex() {
		return this.topLevel;
	}
	
	/**
	 * Returns a reference to the p-th octave in this scale space.
	 * Valid octave indexes are p = 0,..,P-1 (see {@link #getP()}).
	 * 
	 * @param p
	 * @return
	 * @see #getP()
	 */
	public ScaleOctave getOctave(int p) {
		return octaves[p];
	}
	
	/**
	 * Returns the q-th scale space level of octave p in this scale space.
	 * Valid octave indexes are p = 0,..,P-1 (see {@link #getP()}).
	 * 
	 * @param p
	 * @param q
	 * @return
	 */
	public ScaleLevel getScaleLevel(int p, int q) {
		return getOctave(p).getLevel(q);
	}
	
//	public float getValue(int p, int q, int u, int v) {
//		ScaleLevel level = getLevel(p,q);
//		return level.getf(u, v);
//	}
	
	public int getScaleIndex(int p, int q) {
		int m = Q * p + q; 
		return m;
	}
	
	public float getScaleIndexFloat(float p, float q) {
		float m = Q * p + q; 
		return m;
	}
	
	public double getAbsoluteScale(int p, float q) {
		double m = Q * p + q;
		return sigma_0 * Math.pow(2, m/Q);
	}
	
	public double getRelativeScale(double scaleA, double scaleB) {	// scaleA <= scaleB
		if (scaleA > scaleB) {
			throw new IllegalArgumentException("getRelativeScale(): scaleA > scaleB");
		}
		return Math.sqrt(scaleB*scaleB - scaleA*scaleA);
	}
	
	/**
	 * Calculates and returns the real (unscaled) x-position for a
	 * local coordinate at the specified octave.
	 * 
	 * @param p the octave index
	 * @param xp the (scale-level) local coordinate
	 * @return the original x-position
	 */
	public double getRealX(int p, double xp) {
		return Math.pow(2, p) * xp;	// TODO: optimize (precalculate Math.pow(p, 2))
	}
	
	/**
	 * Calculates and returns the real (unscaled) y-position for a
	 * local coordinate at the specified octave.
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
		for (ScaleOctave oct : octaves) {
			oct.printToStream(strm);
			strm.println();
		}
	}
	
	// ----------------------------------------------------------------
	
	public void show() {
		show("");
	}
	
	public void show(String title) {
		if (!title.isEmpty()) {
			title = title + ": ";
		}
		for (int p = 0; p < P; p++) {
			octaves[p].showAsStack(title + "Octave p=" + p);
		}
	}
	
}
