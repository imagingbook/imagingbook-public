/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.iterate;

import java.awt.Color;
import java.util.Iterator;

/**
 * Common interface for color sequencers.
 * They are supposed to iterate infinitely, thus 
 * {@link #hasNext()} returns {@code true} by default.
 * 
 * @author WB
 *
 */
public interface ColorSequencer extends Iterator<Color> {
	
	@Override
	public default boolean hasNext() {
		return true;
	}

}

