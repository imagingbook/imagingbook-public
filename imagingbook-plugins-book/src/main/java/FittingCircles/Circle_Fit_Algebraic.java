package FittingCircles;

import java.util.Locale;

//import Fitting.Display;
import ij.IJ;
import ij.gui.GenericDialog;
import ij.io.LogStream;
import ij.plugin.PlugIn;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic.FitType;
import imagingbook.common.math.PrintPrecision;

public class Circle_Fit_Algebraic implements PlugIn {
	
	static {
		LogStream.redirectSystem();
		PrintPrecision.set(6);
	}
	
	public static int N = 100;
	public static double XC = 200;
	public static double YC = 190;
	public static double R = 150;
	public static double Angle0 = 0;
	public static double Angle1 = 45; // was Math.PI/4;
	public static double SigmaNoise = 5.0; //2.0;
	
	static boolean DrawPoints = true;
	static boolean DrawRealCircle = true;
	static boolean DrawFitCircle = true;
	
	static CircleFitAlgebraic.FitType algType = FitType.Pratt;

	@Override
	public void run(String arg) {
		
		if (!runDialog()) {
			return;
		}
		
		IJ.log("Algebraic fit type = " + algType);		
//		String title = this.getClass().getSimpleName() + "-" + algType;
		
		GeometricCircle realCircle = new GeometricCircle(XC, YC, R);
		IJ.log(" real: " + realCircle.toString());
		
		Pnt2d[] points = new imagingbook.common.geometry.fitting.circle.CircleSampler(realCircle).getPoints(N, Angle0, Angle1, SigmaNoise);
//		Pnt2d[] points = CircleMaker.makeTestCircle(XC, YC, R, N, 0, 2 * Math.PI, 10.0);	// fit-01
//		Pnt2d[] points = CircleMaker.makeTestCircle(XC, YC, R, N, 0, Math.PI/4, 5.0);		// fit-02
//		Pnt2d[] points = CircleMaker.makeTestGander(30);
//		Pnt2d[] points = CircleMaker.make3Points(30);
//		Pnt2d[] points = CircleMaker.collinearPoints1();
		
		// ------------------------------------------------------------------------
		CircleFitAlgebraic fitter = CircleFitAlgebraic.getFit(algType, points);
		// ------------------------------------------------------------------------
		
		GeometricCircle fitCircle = fitter.getGeometricCircle();
		if (fitCircle == null) {
			IJ.log("Algebraic fit: no result!");
			return;
		}
		IJ.log("  fit: " + fitCircle);
		
		IJ.log(String.format(Locale.US, "  error real = %.3f", realCircle.getMeanSquareError(points)));
		IJ.log(String.format(Locale.US, "  error fit  = %.3f", fitCircle.getMeanSquareError(points)));
	}
	
	// ------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		
		gd.addEnumChoice("fit method", algType);
		gd.addCheckbox("DrawPoints", DrawPoints);
		gd.addCheckbox("DrawRealCircle", DrawRealCircle);
		gd.addCheckbox("DrawFitCircle", DrawFitCircle);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		algType = gd.getNextEnumChoice(CircleFitAlgebraic.FitType.class);
		DrawPoints = gd.getNextBoolean();
		DrawRealCircle = gd.getNextBoolean();
		DrawFitCircle = gd.getNextBoolean();

		return true;
	}

}
