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
import java.util.Locale;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import imagingbook.common.util.LinearContainer;
import imagingbook.common.util.PrintsToStream;

/**
 * <p>
 * Represents a single "octave", which is a stack of scale "levels", in a
 * generic hierarchical scale space. See Sec. 25.1.4. of [1] for details.
 * Basically this is an array with flexible bottom and top index.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/20
 */
public abstract class ScaleOctave implements PrintsToStream {
	
	protected double sigma_0 = 1.6;	// TODO: make field final
	protected final int Q; 			// number of levels per doubling scale factor
	protected final int p;			// octave index
	protected final int width, height;
	protected final int botLevelIndex, topLevelIndex;	
	protected final LinearContainer<ScaleLevel> levels;
	
	ScaleOctave(int p, int Q, int width, int height, int botLevelIndex, int topLevelIndex) {
		this.p = p;
		this.Q = Q;
		this.width = width;
		this.height = height;
		if (botLevelIndex > topLevelIndex) 
			throw new IllegalArgumentException("ScaleOctave (constructor): botLevelIndex > topLevelIndex");
		this.botLevelIndex = botLevelIndex;
		this.topLevelIndex = topLevelIndex;
		levels = new LinearContainer<ScaleLevel>(botLevelIndex, topLevelIndex);
	}
	
	/* Create a scale octave from a given bottom level level_b
	 */
	ScaleOctave(int p, int Q, ScaleLevel level_b, int botIndex, int topIndex) {
		this(p, Q, level_b.getWidth(), level_b.getHeight(), botIndex, topIndex);
		this.setLevel(botIndex, level_b);
	}
	
	public int getOctaveIndex() {
		return p;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public ScaleLevel getLevel(int q) {	// TODO: honor bottom level, check q
		return levels.getElement(q);
	}
	
	void setLevel(int q, ScaleLevel level) {	// TODO: check q
		levels.setElement(q, level);
	}
	
	public boolean isInside(int q, int u, int v) {
		return (botLevelIndex < q && q < topLevelIndex &&
				0 < u && u < width-1 && 
				0 < v && v < height-1);
	}
	
	public double getAbsoluteScale(int q) {
		return getLevel(q).getAbsoluteScale();
	}
	
	int getBottomLevelIndex() {
		return botLevelIndex;
	}
	
	int getTopLevelIndex() {
		return topLevelIndex;
	}
	
	public int getScaleIndex(int p, int q) {
		int m = Q * p + q; 
		return m;
	}
	
	public double getAbsoluteScale(int p, int q) {
		double m = getScaleIndex(p, q);
		double sigma = sigma_0 * Math.pow(2, m/Q);
		return sigma;
	}
	
//	public double getRelativeScale(double scaleA, double scaleB) {	// scaleA <= scaleB
//		return Math.sqrt(scaleB*scaleB - scaleA*scaleA);
//	}
//	
//	public int getOctaveIndex() {
//		return p;
//	}
	
	/*
	 * Collects and returns the 3x3x3 neighborhood values from this octave 
	 * at scale level q and center position u,v. The result is stored
	 * in the given 3x3x3 array nh[s][x][y], to which a reference is returned.
	 */
	public void getNeighborhood(int q, int u, int v, final float[][][] nh) {
		// nh[s][x][y]
		for (int s = 0, li = q - 1; s < 3; s++, li++) {
			getLevel(li).get3x3Neighborhood(u, v, nh[s]);
		}
	}
	
	// ---------------------------------------------------------
	
	// for debugging only:
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
	
	public void show(String name, int p) {
		for (int q = botLevelIndex; q <= topLevelIndex; q++) {
			ScaleLevel level = getLevel(q);
			if (level != null) {
				double scale = level.getAbsoluteScale();
				String title = String.format(Locale.US, "%s (p=%d, q=%d, \u03C3=%.4f)", name, p, q, scale);
				FloatProcessor fp = level.toFloatProcessor();
				fp.resetMinAndMax();
				new ImagePlus(title, fp).show();
			}
		}
	}
	
	public void showAsStack(String name) {
		ImageStack stk = new ImageStack(width, height);
		for (int q = botLevelIndex; q <= topLevelIndex; q++) {
			ScaleLevel level = getLevel(q);
			if (level != null) {
				double scale = level.getAbsoluteScale();
				String title = String.format(Locale.US, "q=%d, \u03C3=%.4f", q, scale);
				stk.addSlice(title, level);
			}
		}
		(new ImagePlus(name,stk)).show();
	}
	
	public void showAsStack(String name, int p) {
		ImageStack stk = new ImageStack(width, height);
		for (int q = botLevelIndex; q <= topLevelIndex; q++) {
			ScaleLevel level = getLevel(q);
			if (level != null) {
				double scale = level.getAbsoluteScale();
				String title = String.format(Locale.US, "p=%d, q=%d, \u03C3=%.4f", p, q, scale);
				stk.addSlice(title, level);
			}
		}
		(new ImagePlus(name,stk)).show();
	}
	
}
