package Ch11_CirclesAndEllipses;

import java.awt.Color;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic.FitType;
import imagingbook.common.geometry.fitting.circle.utils.CircleSampler;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;


/**
 * ImageJ plugin, randomly samples an ideal circle and performs
 * circle fitting on the (noisy) sample points.
 * TODO: needs revision!
 * 
 * @author WB
 *
 */
public class Circle_Fit_Algebraic implements PlugIn {

	public static int W = 400;
	public static int H = 400;
	
	public static int N = 100;
	public static double XC = 200;
	public static double YC = 190;
	public static double R = 150;
	public static double Angle0 = 0;
	public static double Angle1 = 45; // was Math.PI/4;
	public static double SigmaNoise = 5.0; //2.0;
	
	public static final double StrokeWidth = 1.0;
	public static double DashLength = 6;
	public static double NoDash = 0;
	public static double CenterMarkRadius = 3;
	
	public static final Color PointColor = Color.blue;
	public static double PointRadius = 2;
	
	public static final Color RealCurveColor = 		new Color(0,176,80);
	public static final Color AlgebraicFitColor = 	new Color(255,0,0);
	public static final Color GeometricFitColor = 	new Color(0,112,192);
	public static final Color IntermediateFitColor = new Color(150,150,150);
	public static final Color StartColor = 			new Color(255,192,0);
	
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
		String title = this.getClass().getSimpleName() + "-" + algType;
		
		GeometricCircle realCircle = new GeometricCircle(XC, YC, R);
		IJ.log(" real: " + realCircle.toString());
		
		Pnt2d[] points = new CircleSampler(realCircle).getPoints(N, Angle0, Angle1, SigmaNoise);
		
		// ------------------------------------------------------------------------
		CircleFitAlgebraic fitter = CircleFitAlgebraic.getFit(algType, points);
		// ------------------------------------------------------------------------
		
		GeometricCircle fitCircle = fitter.getGeometricCircle();
		if (fitCircle == null) {
			IJ.log("Algebraic fit: no result!");
			return;
		}
		
//		IJ.log("  fit: " + fitCircle);
//		IJ.log(String.format(Locale.US, "  error real = %.3f", realCircle.getMeanSquareError(points)));
//		IJ.log(String.format(Locale.US, "  error fit  = %.3f", fitCircle.getMeanSquareError(points)));

		ImagePlus im = NewImage.createByteImage(title, W, H, 1, NewImage.FILL_WHITE);
		
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		
		// ----- draw sample points ----------------------------------
		if (DrawPoints) {
			ColoredStroke pointStroke = new ColoredStroke(StrokeWidth, PointColor, 0);
			pointStroke.setFillColor(PointColor);
			for (Pnt2d p : points) {
				//ola.addShape(makePointShape(p, PointRadius), pointStroke);
				ola.addShape(p.getShape(PointRadius), pointStroke);
			}
		}
		// ----- draw real circle ----------------------------------
		if (DrawRealCircle) {
			ColoredStroke outerStroke = new ColoredStroke(StrokeWidth, RealCurveColor, 0);
			ola.addShapes(realCircle.getShapes(), outerStroke);
		}
		// ----- draw fitted circle ----------------------------------
		if (DrawFitCircle) {	
			ColoredStroke outerStroke = new ColoredStroke(StrokeWidth, AlgebraicFitColor, DashLength);
			ola.addShapes(fitCircle.getShapes(), outerStroke);		
		}
		
		im.setOverlay(ola.getOverlay());
		im.show();
	}
	
	// ------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		
		gd.addEnumChoice("fit method", algType);
		gd.addCheckbox("DrawPoints", DrawPoints);
		gd.addCheckbox("DrawRealCircle", DrawRealCircle);
		gd.addCheckbox("DrawFitCircle", DrawFitCircle);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		algType = gd.getNextEnumChoice(CircleFitAlgebraic.FitType.class);
		DrawPoints = gd.getNextBoolean();
		DrawRealCircle = gd.getNextBoolean();
		DrawFitCircle = gd.getNextBoolean();

		return true;
	}

}
