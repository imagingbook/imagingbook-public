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
