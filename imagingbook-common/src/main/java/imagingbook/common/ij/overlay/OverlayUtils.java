/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.ij.overlay;

import ij.gui.Overlay;
import ij.gui.Roi;

public abstract class OverlayUtils {

	/**
	 * Returns a new overlay produced by joining two existing
	 * overlays, which remain unchanged. All involved ROIs are cloned.
	 * 
	 * @param olyA the first overlay
	 * @param olyB the second overlay
	 * @return the new overlay
	 */
	public static Overlay join(Overlay olyA, Overlay olyB) {
		Overlay oly = olyA.duplicate();
		for (Roi roi : olyB) {
			oly.add((Roi)roi.clone());
		}
		return oly;
	}

}
