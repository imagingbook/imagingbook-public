/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.edgepreservingfilters;

import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.common.util.ParameterBundle;

public interface KuwaharaF {

	public static class Parameters implements ParameterBundle {
		
		/** Radius of the filter (should be even) */
		@DialogLabel("Radius of the filter (>1)")
		public int radius = 2;
		
		/** Threshold on sigma to avoid banding in flat regions */
		@DialogLabel("Variance threshold 0,..,10")
		public double tsigma = 5.0;
		
		@DialogLabel("Out-of-bounds strategy")
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NearestBorder;
	}

}
