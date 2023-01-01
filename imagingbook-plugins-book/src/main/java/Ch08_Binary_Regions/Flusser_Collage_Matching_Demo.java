package Ch08_Binary_Regions;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.iterate.RandomHueGenerator;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.moments.FlusserMoments;
import imagingbook.common.math.MahalanobisDistance;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.BinaryRegionSegmentation;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.common.util.tuples.Tuple2;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.kimia.Kimia1070;
import imagingbook.sampleimages.kimia.KimiaCollage;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Assumes a special binary input image (collage of shapes). The top row are the reference shapes. They are assigned
 * different colors. In the second step, all other shapes are compared to the reference shapes and colored with the
 * color of the most similar shape.
 *
 * Taken from 'Flusser_Moments_Collage_Euclidean'
 *
 * @author WB
 * @version 2018/03/07
 */
public class Flusser_Collage_Matching_Demo implements PlugIn {

	private static final ImageResource ir = KimiaCollage.ShapeCollage1;

	private static int MIN_REGION_SIZE = 100;
	private static boolean USE_MAHALANOBIS_DISTANCE = false;
	private static int REFERENCE_BOUNDARY_Y = 130;	// everything above this position is a reference shape
	private static double MAX_MOMENT_DISTANCE = 0.5;
	private static int FONT_SIZE = 20;
	private static Color UNMATCHED_COLOR = Color.gray;

	private static double[][] cov = Matrix.fromLongBits(Kimia1070.covarianceRegionBits);
	private static MahalanobisDistance md = new MahalanobisDistance(cov);
	private static double[][] U = md.getWhiteningTransformation();

	private ImagePlus im;


	@Override
	public void run(String str) {
		im = ir.getImagePlus();
		im.show();

		if (!runDialog()) {
			return;
		}

		// segment the image into binary regions
		ByteProcessor ip = (ByteProcessor) im.getProcessor();
		BinaryRegionSegmentation segmenter = new RegionContourSegmentation((ByteProcessor) ip);
		List<BinaryRegion> regions = segmenter.getRegions(true);
		if (regions.isEmpty()) {
			IJ.log("No regions found!");
			return;
		}

		// collect all regions and split into reference/other shapes
		List<BinaryRegion> refShapes = new ArrayList<BinaryRegion>();
		List<BinaryRegion> othShapes = new ArrayList<BinaryRegion>();
		for (BinaryRegion r : regions) {
			if (r.getSize() > MIN_REGION_SIZE) {
				Pnt2d ctr = r.getCenter();
				if (ctr.getY() < REFERENCE_BOUNDARY_Y)
					refShapes.add(r);
				else
					othShapes.add(r);
			}
		}

		// sort reference shapes by center x-coordinates:
		refShapes.sort(Comparator.comparingDouble(r -> r.getCenter().getX()));
		// sort other shapes by center x-coordinates:
		othShapes.sort(Comparator.comparingDouble(r -> r.getCenter().getY()));

		IJ.log("found reference shapes: " + refShapes.size());
		IJ.log("found other shapes: " + othShapes.size());

		// for (BinaryRegion r : referenceShapes) {
		// 	IJ.log(r.toString());
		// }

		PrintPrecision.set(9);
		IJ.log("cov = \n" + Matrix.toString(cov));
		IJ.log("U = \n" + Matrix.toString(U));

		ColorProcessor cp = ip.convertToColorProcessor();
		cp.setFont(new Font("Sans", Font.PLAIN, FONT_SIZE));
		RandomHueGenerator rcg = new RandomHueGenerator(17);

		PrintPrecision.set(6);

		// analyze and color the reference shapes
		IJ.log("\nProcessing reference shapes:");
		Color[] refColors = new Color[refShapes.size()];
		double[][] refMoments = new double[refShapes.size()][];
		int i = 0;
		for (BinaryRegion r : refShapes) {
			refMoments[i] = new FlusserMoments(r).getInvariantMoments();
			String rName = "R" + i;
			IJ.log(rName + ": " + Matrix.toString(refMoments[i]));
			refColors[i] = rcg.next();
			paintRegion(r, cp, refColors[i]);
			markRegion(r, cp, rName);
			i++;
		}

		// process other regions
		IJ.log("\nProcessing other shapes:");
		double[][] othMoments = new double[othShapes.size()][];
		int j = 0;
		for (BinaryRegion s : othShapes) {
			othMoments[j] = new FlusserMoments(s).getInvariantMoments();
			String rName = "S"+j;
			IJ.log(rName);
			IJ.log("   original: " + Matrix.toString(othMoments[j]));
			IJ.log("   whitened: " + Matrix.toString(Matrix.multiply(U, othMoments[j])));
			Tuple2<Integer, Double> match = (USE_MAHALANOBIS_DISTANCE) ?
					findBestMatchMahalanobis(othMoments[j], refMoments) :
					findBestMatchEuclidean(othMoments[j], refMoments);
			int k = match.get0();
			double dist = match.get1();
			IJ.log(String.format("  %s: dmin = %.9f (R%d)", rName, dist, k));
			if (dist <= MAX_MOMENT_DISTANCE) {
				paintRegion(s, cp, refColors[k]);
			}
			else {
				paintRegion(s, cp, UNMATCHED_COLOR);
			}
			markRegion(s, cp, rName);
			j++;
		}

		new ImagePlus("Colored Regions", cp).show();
	}

	/**
	 * Finds the reference moment vector closest to the given sample moment vector and returns the associated index (k)
	 * and distance (d).
	 * @param moments a sample moment vector
	 * @param refMoments an array of reference moment vectors
	 * @return a pair (k, d) consisting of reference index k and min. vector distance d
	 */
	private Tuple2<Integer, Double> findBestMatchEuclidean(double[] moments, double[][] refMoments) {
		int iMin = -1;
		double dMin = Double.POSITIVE_INFINITY;
		for (int i = 0; i < refMoments.length; i++) {
			double d = Matrix.distL2(moments, refMoments[i]);	// Euclidean distance
			if (d < dMin) {
				dMin = d;
				iMin = i;
			}
		}
		return new Tuple2<>(iMin, dMin);
	}

	private Tuple2<Integer, Double> findBestMatchMahalanobis(double[] moments, double[][] refMoments) {
		int iMin = -1;
		double dMin = Double.POSITIVE_INFINITY;
		for (int i = 0; i < refMoments.length; i++) {
			double d = md.distance(moments, refMoments[i]);	// Euclidean distance
			if (d < dMin) {
				dMin = d;
				iMin = i;
			}
		}
		return new Tuple2<>(iMin, dMin);
	}

	// -----------------------------------------------------------------------

	void markRegion(BinaryRegion r, ImageProcessor ip, String s) {
		ip.setColor(Color.white);
		ip.drawString(s, (int) Math.round(r.getCenter().getX()), (int) Math.round(r.getCenter().getY()));
	}

	void paintRegion(BinaryRegion r, ImageProcessor ip, Color col) {
		ip.setColor(col);
		for (Pnt2d p : r) {
			ip.drawDot(p.getXint(), p.getYint());
		}
	}

	// ----------------------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		// gd.addEnumChoice("Data set to use", DATA_SET);
		gd.addNumericField("Minimum region size", MIN_REGION_SIZE, 0);
		gd.addNumericField("Uax. moment vector distance", MAX_MOMENT_DISTANCE, 3);
		gd.addCheckbox("Use Mahalanobis distance", USE_MAHALANOBIS_DISTANCE);
		// gd.addCheckbox("List long-encoded covariance matrix", LIST_LONG_ENCODED_MATRIX);
		// gd.addCheckbox("Log output", BE_VERBOSE);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}

		// DATA_SET = gd.getNextEnumChoice(Flusser_Moments_Get_Covariance.DataSet.class);
		MIN_REGION_SIZE = (int) gd.getNextNumber();
		MAX_MOMENT_DISTANCE = gd.getNextNumber();
		USE_MAHALANOBIS_DISTANCE = gd.getNextBoolean();
		// LIST_LONG_ENCODED_MATRIX = gd.getNextBoolean();
		// BE_VERBOSE = gd.getNextBoolean();
		return true;
	}

}

