/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.morphology;

import ij.process.Blitter;
import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;

/**
 * <p>
 * Implements a binary morphological dilation operation. See Sec. 7.2.7 of [1]
 * for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/09/18
 */
public class BinaryOutline implements BinaryMorphologyOperator {
	
	private static final byte[][] H4 = { 
			{ 0, 1, 0 }, 
			{ 1, 1, 1 }, 
			{ 0, 1, 0 }};
	
	private static final byte[][] H8 = { 
			{ 1, 1, 1 }, 
			{ 1, 1, 1 }, 
			{ 1, 1, 1 }};
	
	private final NeighborhoodType2D nh;
	
	/**
	 * Constructor, creates a {@link BinaryOutline} operator using
	 * a 4-neighborhood by default ({@link NeighborhoodType2D#N4}).
	 */
	public BinaryOutline() {
		this(NeighborhoodType2D.N4);
	}
	
	/**
	 * Constructor, creates a {@link BinaryOutline} operator using
	 * a the specified neighborhood type ({@link NeighborhoodType2D}).
	 * 
	 * @param nh neighborhood type ({@link NeighborhoodType2D})
	 */
	public BinaryOutline(NeighborhoodType2D nh) {
		this.nh = nh;
	}
	
	@Override
	public void applyTo(ByteProcessor ip) {
		ByteProcessor fg = (ByteProcessor) ip.duplicate();
		byte[][] H = (nh == NeighborhoodType2D.N4) ? H4 : H8;
		new BinaryErosion(H).applyTo(fg);	//erode(fg, H);
		ip.copyBits(fg, 0, 0, Blitter.DIFFERENCE);
	}

}
