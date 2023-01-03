package Ch08_Binary_Regions;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.moments.FlusserMoments;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.math.Statistics;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.BinaryRegionSegmentation;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.kimia.Kimia1070;
import imagingbook.sampleimages.kimia.Kimia216;
import imagingbook.sampleimages.kimia.Kimia99;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This ImageJ plugin calculates the covariance matrix for the 11-element Flusser moment vectors over a collection of
 * binary images. The plugin assumes binary images (with 0 background and non-zero foreground) and calculates the 11
 * scale and rotation invariant Flusser moments (sec. G.3 of [1] for details) for the largest contained region contained
 * in each image of the data set . The resulting covariance matrix is output in {@code float} and bit-encoded
 * {@code long} format to avoid precision loss. The latter can be converted to {@code double} using
 * {@link Matrix#fromLongBits(long[][])}. The covariance matrix is used as a parameter to the Mahalanobis distance for
 * matching moment vectors (see {@link Flusser_Moments_Matching_Demo}).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/28
 * @see Flusser_Moments_Matching_Demo
 */
public class Flusser_Moments_Covariance_Matrix implements PlugIn {

	private enum DataSet {
		Kimia99, Kimia216, Kimia1070
	}

	private static DataSet DATA_SET = DataSet.Kimia99;
	private static int MIN_REGION_SIZE = 50;
	private static boolean LIST_LONG_ENCODED_MATRIX = false;

	public void run(String arg0) {
		if (!runDialog())
			return;

		Class<? extends ImageResource> clazz = null;
		switch(DATA_SET) {
			case Kimia99:  		clazz = Kimia99.class; break;
			case Kimia216:  	clazz = Kimia216.class; break;
			case Kimia1070:  	clazz = Kimia1070.class; break;
		}

		ImageResource[] irs = clazz.getEnumConstants();
		IJ.log("Processing " + clazz.getSimpleName() + " dataset (" + irs.length + " images)");

		// moment vectors
		List<double[]> samples = new ArrayList<double[]>();

		for (int i = 0; i < irs.length; i++) {
			ImageResource ir = irs[i];
			ImagePlus img = ir.getImagePlus();
			if (img == null) {
				IJ.log("ERROR: could not open " + ir);
				continue;
			}
			ImageProcessor ip = img.getProcessor();	// do some processing
			if (!(ip instanceof ByteProcessor)) {
				IJ.log("ERROR: Wrong image type: " + ir);
				continue;
			}

			BinaryRegionSegmentation segmenter = new RegionContourSegmentation((ByteProcessor) ip);
			List<BinaryRegion> regions = segmenter.getRegions(true);
			if (!regions.isEmpty()) {
				BinaryRegion r = regions.get(0);
				if (r.getSize() >= MIN_REGION_SIZE) {
					double[] moments = new FlusserMoments(r).getInvariantMoments();
					if (!isFinite(moments)) {
						IJ.log("ERROR: non-finite moment vector for " + ir + ": " + Matrix.toString(moments));
					}
					else {
						samples.add(moments);
					}
				}
			}
		}

		double[][] samplesA = samples.toArray(new double[0][]);
		double[][] cov = Statistics.covarianceMatrix(samplesA);

		PrintPrecision.set(9);
		IJ.log("covariance matrix (double):\n" + Matrix.toString(cov));

		if ( LIST_LONG_ENCODED_MATRIX) {
			IJ.log("covariance matrix (long):\n" + Matrix.toString(Matrix.toLongBits(cov)));
		}
	}

	private boolean isFinite(double[] moments) {
		for (int i = 0; i < moments.length; i++) {
			if (Double.isFinite(moments[i])) {
				return true;
			}
		}
		return false;
	}

	// ------------------------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Data set to use", DATA_SET);
		gd.addNumericField("Minimum region size (pixels)", MIN_REGION_SIZE);
		gd.addCheckbox("List long-encoded covariance matrix", LIST_LONG_ENCODED_MATRIX);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}

		DATA_SET = gd.getNextEnumChoice(DataSet.class);
		MIN_REGION_SIZE = (int) gd.getNextNumber();
		LIST_LONG_ENCODED_MATRIX = gd.getNextBoolean();
		return true;
	}

}

