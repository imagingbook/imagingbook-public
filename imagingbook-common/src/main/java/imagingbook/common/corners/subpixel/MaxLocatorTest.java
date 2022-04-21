package imagingbook.common.corners.subpixel;

import imagingbook.common.corners.subpixel.SubpixelMaxInterpolator.Method;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

class MaxLocatorTest {
	
	public static void main(String[] args) {
		
		PrintPrecision.set(8);
//		float[] samples = {16,9,7,11,8,15,14,12,10}; // = s_0,...,s_8, result xyz = {-0.37500000, 0.05555556, 16.57638931}
		float[] samples = {40229.785156f, 33941.535156f, 25963.150391f, 39558.175781f, 39078.843750f, 33857.863281f, 39861.664063f, 38746.250000f, 33652.839844f};

		for (Method m : SubpixelMaxInterpolator.Method.values()) {
			SubpixelMaxInterpolator interp = SubpixelMaxInterpolator.getInstance(m); 
		
			if (interp != null) {
				System.out.println("interpolator = " + interp.getClass().getName());
				
				float[] xyz = interp.getMax(samples);
				if (xyz != null) {
					System.out.println("xyz = " + Matrix.toString(xyz));
				}
				else {
					System.out.println("*** Max could not be located! ***");
				}			
				System.out.println();
			}
		}
	}

}

//Results for float[] samples = {16,9,7,11,8,15,14,12,10};
//
//interpolator = imagingbook.pub.corners.subpixel.SubpixelMaxInterpolator$QuadraticTaylor
//xyz = {-0.38320211, 0.08748906, 16.59667587}
//
//interpolator = imagingbook.pub.corners.subpixel.SubpixelMaxInterpolator$QuadraticLeastSquares
//xyz = {-0.41613588, 0.32979476, 15.65628719}
//
//interpolator = imagingbook.pub.corners.subpixel.SubpixelMaxInterpolator$Quartic
//xyz = {-0.40573445, 0.11285823, 16.62036324}

//----------------------------------------------------------------------------
//
// Results for float[] samples = {40229.785156f, 33941.535156f, 25963.150391f, 39558.175781f, 39078.843750f, 33857.863281f, 39861.664063f, 38746.250000f, 33652.839844f};
//
//interpolator = imagingbook.pub.corners.subpixel.SubpixelMaxInterpolator$QuadraticTaylor
//xyz = {-0.02513363, -0.20850648, 40271.58203125}
//
//interpolator = imagingbook.pub.corners.subpixel.SubpixelMaxInterpolator$QuadraticLeastSquares
//*** Max could not be located! ***
//
//interpolator = imagingbook.pub.corners.subpixel.SubpixelMaxInterpolator$Quartic
//*** Max could not be located! ***