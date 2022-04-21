package imagingbook.common.ransac;

import static imagingbook.common.math.Arithmetic.sqr;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.fitting.line.LineFit;
import imagingbook.common.geometry.fitting.line.OrthogonalLineFitEigen;
import imagingbook.common.geometry.line.AlgebraicLine;

/**
 * RANSAC detector for straight lines.
 * 
 * @author WB
 * 
 * @see AlgebraicLine
 * @see GenericRansacDetector
 *
 */
public class RansacDetectorLine extends GenericRansacDetector<AlgebraicLine>{
	
	private final Parameters params;
	
	public static class Parameters extends RansacParameters {
		@DialogLabel("Min. distance between sample points")
		public int minPairDistance;
		
		// default parameters can be set here
		public Parameters() {
			this.maxIterations = 1000;
			this.distanceThreshold = 2.0;
			this.minSupportCount = 100;
			this.minPairDistance = 25;
		}	
	}
	
	// constructors ------------------------------------
	
	public RansacDetectorLine(Parameters params) {
		super(2, params);
		this.params = params;
	}
	
	public RansacDetectorLine() {
		this(new Parameters());
	}
	
	// ----------------------------------------------------------------
	
	@Override // override default method to check for min pair distance
	protected Pnt2d[] drawRandomPoints(Pnt2d[] points) {
		final int MaxTries = 20;
		int i = 0;
		Pnt2d[] draw = super.drawRandomPoints(points);
		while (draw[0].distanceSq(draw[1]) < sqr(params.minPairDistance) && i < MaxTries) {
			draw = super.drawRandomPoints(points);
			i++;
		}
		return (i < MaxTries) ? draw : null;
	}

	@Override
	protected AlgebraicLine fitInitial(Pnt2d[] points) {
		return AlgebraicLine.from(points[0], points[1]);
	}
	
	@Override
	protected AlgebraicLine fitFinal(Pnt2d[] inliers) {
		LineFit fit = new OrthogonalLineFitEigen(inliers);
		return fit.getLine();
	}

}
