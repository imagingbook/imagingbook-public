package imagingbook.common.color.quantize;

import org.junit.Test;

import ij.process.ColorProcessor;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.ColorTestImage;
import imagingbook.testutils.NumericTestUtils;

public class MedianCutQuantizerTest {
	
	static float TOL = 1e-3f;
	
	static ImageResource clown = ColorTestImage.Clown;
	
	static ImageResource twoColors = ColorTestImage.TwoColors;
	
	static double[][] clown5 = {
			{112.0956, 32.3692, 12.2368}, 
			{192.6338, 145.3809, 109.3478}, 
			{18.7955, 5.9237, 3.9769}, 
			{57.8603, 23.2002, 10.6199}, 
			{154.8085, 83.6543, 21.1042}};
	
	static double[][] clown16 = {
			{14.4110, 4.4241, 3.7959}, 
			{202.9240, 117.8785, 24.8337}, 
			{211.9555, 162.9507, 122.1393}, 
			{25.8749, 6.1988, 1.8082}, 
			{142.0128, 70.4512, 16.1758}, 
			{60.6632, 29.6637, 17.1812}, 
			{87.2993, 34.8597, 12.4226}, 
			{235.7550, 208.7890, 175.1670}, 
			{55.0617, 16.7464, 4.0685}, 
			{8.8047, 4.0231, 3.0644}, 
			{104.4277, 58.8488, 25.8088}, 
			{192.4769, 117.9470, 74.3852}, 
			{169.8695, 87.4385, 17.5987}, 
			{136.8920, 29.8787, 12.0510}, 
			{130.3528, 91.8480, 65.7117}, 
			{27.1316, 9.1349, 7.3839}};	
	
	@Test
	public void test1() {
		int K = 5;
		runTest(clown, K, Matrix.toFloat(clown5));
//		System.out.println("-----------------------------");
//		runTest(clown, K, Matrix.toFloat(clown5));
	}
	
	@Test
	public void test2() {
		runTest(clown, 16, Matrix.toFloat(clown16));
	}
	
	
	private void runTest(ImageResource ir, int K, float[][] colormap) {
		ColorProcessor cp = (ColorProcessor) ir.getImage().getProcessor();
		MedianCutQuantizer quantizer = new MedianCutQuantizer(cp, K);
		float[][] cm = quantizer.getColorMap();
		PrintPrecision.set(4);
		System.out.println(Matrix.toString(cm));
		System.out.println();
		NumericTestUtils.assertArrayEquals(colormap, cm, TOL);
	}

}
