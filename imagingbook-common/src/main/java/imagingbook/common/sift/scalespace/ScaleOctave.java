/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.sift.scalespace;

import ij.ImageStack;
import ij.process.FloatProcessor;
import imagingbook.common.util.LinearContainer;
import imagingbook.common.util.PrintsToStream;

import java.io.PrintStream;
import java.util.Locale;

/**
 * <p>
 * Represents a single "octave", which is a stack of scale "levels", in a generic hierarchical scale space. See Sec.
 * 25.1.4. of [1] for details. Basically this is an array with flexible bottom and top index.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @param <LevelT> the scale level type
 * @author WB
 * @version 2022/11/20
 */
public abstract class ScaleOctave<LevelT extends ScaleLevel> implements PrintsToStream {
	
	final int Q; 			// number of levels per doubling scale factor
	final int p;			// octave index
	final int width, height;
	final int botLevelIndex, topLevelIndex;
	final LinearContainer<LevelT> levels;
	final double sigma_0;
	
	ScaleOctave(int p, int Q, int width, int height, int botLevelIndex, int topLevelIndex, double sigma_0) {
		this.p = p;
		this.Q = Q;
		this.width = width;
		this.height = height;
		if (botLevelIndex > topLevelIndex) 
			throw new IllegalArgumentException("ScaleOctave (constructor): botLevelIndex > topLevelIndex");
		this.botLevelIndex = botLevelIndex;
		this.topLevelIndex = topLevelIndex;
		this.sigma_0 = sigma_0;
		levels = new LinearContainer<>(botLevelIndex, topLevelIndex);
	}
	
	/**
	 * Returns the index (p) of this scale space octave.
	 * @return the octave index
	 */
	public int getOctaveIndex() {
		return p;
	}
	
	/**
	 * Returns the image width of this scale space octave.
	 * @return the image width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the image height of this scale space octave.
	 * @return the image height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns a reference to the scale level of this octave with the specified index.
	 *
	 * @param q the level index
	 * @return a reference to scale level q
	 */
	public LevelT getLevel(int q) {	// TODO: honor bottom level, check q
		return levels.getElement(q);
	}
	
	// for internal use only
	void setLevel(int q, LevelT level) {	// TODO: check q
		levels.setElement(q, level);
	}

	/**
	 * Returns true iff q is outside the level range and position (u,v) is inside the level's bounds.
	 *
	 * @param q the level index
	 * @param u horizontal coordinate
	 * @param v vertical coordinate
	 * @return true iff inside this level
	 */
	public boolean isInside(int q, int u, int v) {
		return (botLevelIndex < q && q < topLevelIndex &&
				0 < u && u < width-1 && 
				0 < v && v < height-1);
	}
	
	/**
	 * Returns the absolute scale (&sigma;) associated with level q.
	 * @param q the level index
	 * @return the absolute scale of the level
	 */
	public double getAbsoluteScale(int q) {
		return getLevel(q).getAbsoluteScale();
	}

	/**
	 * Returns the bottom level index for this scale space octave (e.g., this is -1 for the Gaussian scale space used in
	 * SIFT).
	 *
	 * @return the bottom level index
	 */
	int getBottomLevelIndex() {
		return botLevelIndex;
	}

	/**
	 * Returns the top level index for this scale space octave (e.g., this is Q+1 for the Gaussian scale space used in
	 * SIFT).
	 *
	 * @return the top level index
	 */
	int getTopLevelIndex() {
		return topLevelIndex;
	}

	/**
	 * Returns the absolute scale (&sigma;) for octave index p and level index q;
	 *
	 * @param p the octave index
	 * @param q the level index
	 * @return the absolute scale
	 */
	public double getAbsoluteScale(int p, int q) {
		double m =  Q * p + q; 
		double sigma = sigma_0 * Math.pow(2, m/Q);
		return sigma;
	}

	/**
	 * Collects and returns the 3x3x3 neighborhood values from this octave at scale level q and center position (u,v).
	 * The result is stored in the supplied 3x3x3 array nh[s][x][y].
	 *
	 * @param q the level index
	 * @param u the horizontal coordinate
	 * @param v the vertical coordinate
	 * @param nh the 3x3x3 neighborhood array to hold the result
	 */
	public void getNeighborhood(int q, int u, int v, final float[][][] nh) {
		// nh[s][x][y]
		for (int s = 0, li = q - 1; s < 3; s++, li++) {
			getLevel(li).get3x3Neighborhood(u, v, nh[s]);
		}
	}
	
	// ---------------------------------------------------------

	/**
	 * Returns an ImageJ {@link ImageStack} for this octave which can be displayed. Frame labels are automatically set.
	 *
	 * @return an {@link ImageStack} for this octave
	 */
	public ImageStack getImageStack() {
		ImageStack stk = new ImageStack(width, height);
		for (int q = botLevelIndex; q <= topLevelIndex; q++) {
			ScaleLevel level = getLevel(q);
			if (level != null) {
				double scale = level.getAbsoluteScale();
				String title = String.format(Locale.US, "q=%d, \u03C3=%.4f", q, scale);
				FloatProcessor fp = level.toFloatProcessor();
				fp.resetMinAndMax();
				stk.addSlice(title, fp);
			}
		}
		return stk;
	}
	
	@Override
	public void printToStream(PrintStream strm) {
		strm.println("  Scale Octave p=" + p);
		for (int q = botLevelIndex; q <= topLevelIndex; q++) {
			ScaleLevel level = getLevel(q);
			if (level != null) {
				double scale = level.getAbsoluteScale();
				strm.format(Locale.US, "   level (p=%d, q=%d, scale=%.4f)\n", p, q, scale);
			}
		}
	}
	
}
