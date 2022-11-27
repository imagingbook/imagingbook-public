package Ch21_GeometricOperations;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.linear.LinearMapping2D;
import imagingbook.common.geometry.mappings.linear.ProjectiveMapping2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.interpolation.InterpolationMethod;
import imagingbook.common.math.PrintPrecision;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * Performs a 4-point projective mapping to A4 proportions,
 * using a Mapping object from the imagingbook library.
 * 
 * @author W. Burger
 * @version 2015/12/10
 */
public class Rectify_Quad_Selection implements PlugInFilter {
	
	private static double A4width = 210, A4height = 297;	// A4 paper size in mm
	private static double TargetPixelSize = 0.5;			// pixel size in mm
	
	private static boolean ListTransformationMatrix = true;
	
	private ImagePlus im = null;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Rectify_Quad_Selection() {
		if (IjUtils.noCurrentImage() && DialogUtils.askForSampleImage()) {
			ImagePlus imp = GeneralSampleImage.PostalPackageSmall_jpg.getImage();
			float[] xpts = {22, 330, 981, 756};
			float[] ypts = {347, 71, 207, 591};
			Roi roi = new PolygonRoi(xpts, ypts, Roi.POLYGON);
			imp.setRoi(roi);
			imp.show();
		}
	}
	
	
	
	public int setup(String arg0, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES + ROI_REQUIRED;
	}

	public void run(ImageProcessor source) {
		Roi roi = im.getRoi();
		if (!(roi instanceof PolygonRoi)) {
			IJ.error("Polygon selection required!");
			return;
		}
		
		FloatPolygon poly = roi.getFloatPolygon();
		if (poly.npoints < 4) {
			IJ.error("At least 4 points must be selected!");
			return;
		}
		
		if (!runDialog()) {
			return;
		}
				
		int tWidth = (int) Math.round(A4width / TargetPixelSize);
		int tHeight = (int) Math.round(A4height / TargetPixelSize);
		
		Pnt2d[] imCorners = getPoints(poly);
		Pnt2d[] tarCorners = {
				PntInt.from(0, 0),
				PntInt.from(tWidth, 0),
				PntInt.from(tWidth, tHeight),
				PntInt.from(0, tHeight)};
		
		LinearMapping2D mp = ProjectiveMapping2D.fromPoints(imCorners, tarCorners).getInverse();	// inverse mapping (target to source)

		if (ListTransformationMatrix) {
			PrintPrecision.set(6);
			IJ.log("M = \n" + mp.toString());
		}
	
		ImageProcessor target = source.createProcessor(tWidth, tHeight);
		ImageMapper mapper = new ImageMapper(mp, null, InterpolationMethod.Bilinear);
		mapper.map(source, target);
		(new ImagePlus("target", target)).show();
	}
	
	/**
	 * Utility method for extracting the points of an AWT polygon.
	 * @param poly the original polygon
	 * @return the polygon's vertex points
	 */
	private Pnt2d[] getPoints(FloatPolygon poly) {
		Pnt2d[] X = new Pnt2d[poly.npoints];
		for (int i = 0; i < poly.npoints; i++) {
			X[i] = Pnt2d.from(poly.xpoints[i], poly.ypoints[i]);
		}
		return X;
	}
	
	// --------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		
		gd.addCheckbox("List transformation matrix", ListTransformationMatrix);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
	
		ListTransformationMatrix = gd.getNextBoolean();
		
		return true;
	}
	
}
