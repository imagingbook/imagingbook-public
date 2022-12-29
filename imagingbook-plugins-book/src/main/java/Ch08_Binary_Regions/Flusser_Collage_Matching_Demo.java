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
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.BinaryRegionSegmentation;
import imagingbook.common.regions.Contour;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.core.resource.ImageResource;
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
	private static boolean USE_CONTOURS_ONLY = false;

	private static int REFERENCE_BOUNDARY_Y = 130;	// everything above is a reference shape
	private static double MAX_MOMENT_DISTANCE = 10.001;
	private static int FONT_SIZE = 20;

	private ImagePlus im;


	@Override
	public void run(String s) {
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

		// collect regions and split into reference/other shapes
		List<BinaryRegion> referenceShapes = new ArrayList<BinaryRegion>();
		List<BinaryRegion> otherShapes = new ArrayList<BinaryRegion>();

		for (BinaryRegion r : regions) {
			if (r.getSize() > MIN_REGION_SIZE) {
				Pnt2d ctr = r.getCenter();
				if (ctr.getY() < REFERENCE_BOUNDARY_Y)
					referenceShapes.add(r);
				else
					otherShapes.add(r);
			}
		}

		// sort reference shapes by center x-coordinates:
		referenceShapes.sort(Comparator.comparingDouble(r -> r.getCenter().getX()));
		// sort other shapes by center x-coordinates:
		otherShapes.sort(Comparator.comparingDouble(r -> r.getCenter().getY()));

		IJ.log("found reference shapes: " + referenceShapes.size());
		IJ.log("found other shapes: " + otherShapes.size());

		// for (BinaryRegion r : referenceShapes) {
		// 	IJ.log(r.toString());
		// }

		// analyze and color the reference shapes
		Color[] refColors = new Color[referenceShapes.size()];
		double[][] refMoments = new double[referenceShapes.size()][];

		ColorProcessor cp = ip.convertToColorProcessor();
		cp.setFont(new Font("Sans", Font.PLAIN, FONT_SIZE));
		RandomHueGenerator rcg = new RandomHueGenerator(17);

		PrintPrecision.set(6);
		IJ.log("Reference shapes:");
		int i = 0;
		for (BinaryRegion r : referenceShapes) {
			double[] moments = null;
			if (USE_CONTOURS_ONLY) {
				Contour c = r.getOuterContour();
				moments = new FlusserMoments(c).getInvariantMoments();
				// TODO: is calculation of scale normalization correct for contours?
			}
			else {
				moments = new FlusserMoments(r).getInvariantMoments();
			}
			String rName = "R" + i;
			IJ.log(rName + ": " + Matrix.toString(moments));

			refMoments[i] = moments;
			refColors[i] = rcg.next();
			paintRegion(r, cp, refColors[i]);
			markRegion(r, cp, rName);
			i++;
		}

		new ImagePlus("Colored Regions", cp).show();
	}

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


	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		// gd.addEnumChoice("Data set to use", DATA_SET);
		gd.addNumericField("Minimum region size (pixels)", MIN_REGION_SIZE);
		gd.addCheckbox("Calculate moments from contours", USE_CONTOURS_ONLY);
		// gd.addCheckbox("List long-encoded covariance matrix", LIST_LONG_ENCODED_MATRIX);
		// gd.addCheckbox("Log output", BE_VERBOSE);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}

		// DATA_SET = gd.getNextEnumChoice(Flusser_Moments_Get_Covariance.DataSet.class);
		MIN_REGION_SIZE = (int) gd.getNextNumber();
		USE_CONTOURS_ONLY = gd.getNextBoolean();
		// LIST_LONG_ENCODED_MATRIX = gd.getNextBoolean();
		// BE_VERBOSE = gd.getNextBoolean();
		return true;
	}

}

