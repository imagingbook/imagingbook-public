package imagingbook.common.histogram;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.common.math.Matrix;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralTestImage;

public class HistogramUtilsTest {

	@Test
	public void test1() {
		ImageResource ir = GeneralTestImage.Boats;
		
		ImageProcessor ip = ir.getImage().getProcessor();
		int[] h = ip.getHistogram();
		
		assertEquals("wrong histogram count (all)", ip.getHeight() * ip.getWidth(), HistogramUtils.count(h));
		assertEquals("wrong histogram count (range)", 412708, HistogramUtils.count(h, 10, 200));
		
		assertEquals("wrong histogram max", 7480, HistogramUtils.max(h));
		
		int[] H = HistogramUtils.cumulate(h);
		assertEquals("wrong H[0]", h[0], H[0]);
		assertEquals("wrong H[K-1]", HistogramUtils.count(h), H[H.length - 1]);
		
		double[] pdf = HistogramUtils.pdf(h);
		assertEquals("pdf does not sum to 1", 1.0, Matrix.sum(pdf), 1e-6);
		
		double[] cdf = HistogramUtils.cdf(h);
		assertEquals("cdf[K-1] should be 1", 1.0, cdf[cdf.length - 1], 1e-6);
		
		assertEquals("wrong mean", 120.026837, HistogramUtils.mean(h), 1e-6);
		assertEquals("wrong variance (fast)", 2419.411167, HistogramUtils.variance(h), 1e-6);
		assertEquals("wrong variance (slow)", 2419.411167, HistogramUtils.varianceSlow(h, 0, h.length-1), 1e-6);
		
		assertEquals("wrong median", 138, HistogramUtils.median(h));
	}

}


