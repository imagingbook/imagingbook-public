package imagingbook.common.color.quantize;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ColorProcessor;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.ColorTestImage;
import imagingbook.testutils.NumericTestUtils;

public class MedianCutQuantizerTest {
	
	static float TOL = 1e-3f;
	
	static double[][] clown5 = {
			{154.8085, 83.6543, 21.1042}, 
			{84.9780, 27.7847, 11.4284}, 
			{18.7955, 5.9237, 3.9769}, 
			{161.4187, 104.8991, 70.0490}, 
			{223.8567, 185.8727, 148.6565}};
	
	static double[][] clown16 = {
			{211.9555, 162.9507, 122.1393}, 
			{130.3528, 91.8480, 65.7117}, 
			{235.7550, 208.7890, 175.1670}, 
			{192.4769, 117.9470, 74.3852}, 
			{136.8920, 29.8787, 12.0510}, 
			{104.4277, 58.8488, 25.8088}, 
			{202.9240, 117.8785, 24.8337}, 
			{27.1316, 9.1349, 7.3839}, 
			{142.0128, 70.4512, 16.1758}, 
			{87.2993, 34.8597, 12.4226}, 
			{169.8695, 87.4385, 17.5987}, 
			{60.6632, 29.6637, 17.1812}, 
			{55.0617, 16.7464, 4.0685}, 
			{25.8749, 6.1988, 1.8082}, 
			{8.8047, 4.0231, 3.0644}, 
			{14.4110, 4.4241, 3.7959}};	
	
	static double[][] twoColors2 = {
			{70.0000, 255.0000, 0.0000}, 
			{255.0000, 0.0000, 0.0000}};
	
	static double[][] twoColors1 = {
			{108.2146, 202.3258, 0.0000}};
	
	static double[][] singleColor1 = {
			{70.0000, 255.0000, 0.0000}};
	
	static double[][] randomColors5 = {
			{69.8032, 147.0743, 55.4368}, 
			{133.5277, 226.0861, 54.7705}, 
			{197.9305, 80.4093, 55.0683}, 
			{44.1269, 226.5416, 21.5424}, 
			{45.4006, 226.1801, 87.4693}};
	
	static double[][] randomColors256 = {
			{76.2051, 84.0769, 153.1538}, 
			{71.1842, 89.3947, 116.7632}, 
			{94.2308, 143.6667, 141.2051}, 
			{110.1026, 88.9487, 92.5128}, 
			{165.0513, 226.7692, 182.8718}, 
			{155.2821, 174.2308, 132.9487}, 
			{168.3333, 211.0513, 31.2821}, 
			{222.2051, 89.2308, 134.2564}, 
			{35.5897, 179.5128, 156.6410}, 
			{104.8462, 223.0000, 168.2564}, 
			{207.4615, 170.3590, 88.4872}, 
			{119.8974, 107.2821, 26.6667}, 
			{229.4359, 25.3333, 115.0256}, 
			{10.7692, 210.2308, 69.2051}, 
			{74.6667, 210.4872, 68.6923}, 
			{51.5128, 100.4359, 95.7436}, 
			{161.8462, 223.0513, 137.1795}, 
			{97.8974, 180.7179, 148.1795}, 
			{219.6923, 169.6923, 59.5897}, 
			{220.0513, 172.2308, 19.1282}, 
			{45.0513, 95.1026, 23.2051}, 
			{149.2895, 46.8684, 19.9474}, 
			{67.7692, 228.7949, 138.2308}, 
			{246.5128, 46.1282, 62.5897}, 
			{174.1538, 49.1282, 21.7179}, 
			{114.0513, 186.7692, 33.4615}, 
			{26.5385, 145.1026, 33.5385}, 
			{15.7632, 209.1579, 54.0789}, 
			{136.0769, 211.0513, 70.1026}, 
			{140.1282, 240.6667, 52.8462}, 
			{38.3077, 209.2564, 69.6154}, 
			{111.8421, 57.6842, 61.6579}, 
			{219.1795, 146.2564, 137.5897}, 
			{236.8974, 34.4103, 153.6667}, 
			{42.9474, 73.2105, 63.7632}, 
			{44.2308, 226.4615, 156.2051}, 
			{187.9487, 229.8718, 117.9744}, 
			{156.1316, 34.4474, 90.2632}, 
			{229.7436, 226.0769, 58.4615}, 
			{220.0513, 138.5897, 24.3590}, 
			{223.0000, 210.6154, 22.6410}, 
			{23.3077, 242.6923, 114.5897}, 
			{194.1538, 31.2564, 94.3077}, 
			{24.9737, 211.3684, 115.7368}, 
			{246.6410, 107.4103, 20.4103}, 
			{221.1842, 76.5526, 22.9737}, 
			{167.0263, 13.5000, 63.8421}, 
			{224.2821, 45.6154, 64.5897}, 
			{244.6923, 48.3846, 9.2308}, 
			{156.3421, 80.6579, 23.7368}, 
			{188.8718, 237.7436, 20.4359}, 
			{111.8974, 169.7179, 33.7179}, 
			{112.6410, 186.9487, 10.7692}, 
			{55.9211, 211.5263, 53.2895}, 
			{199.5897, 47.2051, 33.1538}, 
			{166.6410, 240.3333, 27.9231}, 
			{94.9231, 242.7179, 70.9231}, 
			{90.9231, 220.5641, 63.7692}, 
			{111.1026, 218.8974, 64.2564}, 
			{141.1842, 232.4737, 9.8947}, 
			{138.7895, 210.2368, 52.3158}, 
			{139.7436, 217.3077, 10.3846}, 
			{37.2564, 242.0769, 68.3590}, 
			{27.6410, 145.1538, 129.4872}, 
			{26.3947, 146.0789, 92.1316}, 
			{43.3421, 180.8947, 116.2632}, 
			{47.0769, 225.4359, 194.9231}, 
			{159.8421, 115.2368, 134.4737}, 
			{176.0526, 33.9474, 114.6842}, 
			{136.9737, 230.5526, 118.1842}, 
			{102.1795, 229.7949, 133.5128}, 
			{102.2821, 177.6154, 115.4103}, 
			{52.2105, 51.2895, 22.5789}, 
			{161.8947, 86.3684, 62.2105}, 
			{216.7179, 132.1795, 59.0769}, 
			{197.1538, 225.6667, 62.4359}, 
			{234.1538, 13.1026, 92.9487}, 
			{164.0513, 46.2821, 64.3846}, 
			{65.6923, 115.6667, 22.9231}, 
			{53.5385, 142.4615, 63.7949}, 
			{107.2821, 63.5897, 23.2564}, 
			{190.3684, 205.6053, 20.7895}, 
			{62.0000, 211.8718, 116.8205}, 
			{102.8718, 241.0000, 114.5641}, 
			{191.9231, 103.8205, 23.3846}, 
			{158.6154, 110.6923, 22.0769}, 
			{146.8684, 12.2895, 22.6842}, 
			{94.8718, 111.8974, 21.6667}, 
			{219.8718, 105.0513, 25.3077}, 
			{35.6154, 211.5128, 91.5128}, 
			{149.1842, 147.2895, 13.1842}, 
			{24.5000, 146.2895, 7.3421}, 
			{14.1842, 208.2895, 92.2632}, 
			{242.5641, 77.4359, 23.4872}, 
			{168.2105, 209.2105, 8.8421}, 
			{223.0000, 46.3333, 33.1282}, 
			{244.9487, 43.5897, 29.4359}, 
			{221.1053, 47.9211, 9.6316}, 
			{55.4359, 211.1538, 70.1282}, 
			{29.2564, 178.4359, 33.7692}, 
			{43.9487, 176.5128, 61.8205}, 
			{72.5641, 209.0513, 91.8718}, 
			{18.0526, 166.3947, 64.1579}, 
			{245.6154, 14.7436, 62.3590}, 
			{81.5128, 185.2564, 35.8718}, 
			{197.2051, 15.3590, 33.8462}, 
			{81.2632, 168.8684, 11.4211}, 
			{35.8421, 210.5000, 53.9474}, 
			{136.9231, 240.5641, 68.7692}, 
			{74.1282, 213.4872, 52.1795}, 
			{55.6667, 168.1538, 34.0256}, 
			{33.7436, 200.4615, 33.1795}, 
			{13.2105, 241.0263, 53.3947}, 
			{13.3421, 202.1316, 32.8684}, 
			{73.7436, 239.5641, 69.6154}, 
			{13.7692, 240.6410, 70.0256}, 
			{138.6842, 201.2368, 9.2368}, 
			{136.5000, 201.7895, 33.6579}, 
			{141.5641, 219.2308, 32.9231}, 
			{114.4872, 241.0256, 69.9487}, 
			{131.8205, 238.9231, 33.9231}, 
			{140.7436, 246.4872, 10.6410}, 
			{35.2308, 247.7692, 32.1538}, 
			{34.8718, 219.7179, 8.9487}, 
			{80.8718, 185.7692, 12.7179}, 
			{11.7436, 248.3846, 8.0256}, 
			{93.3846, 219.5641, 33.8462}, 
			{87.4737, 147.1316, 89.2632}, 
			{102.5789, 210.6316, 113.4211}, 
			{113.1842, 201.8158, 62.0000}, 
			{140.4359, 236.7179, 93.0513}, 
			{61.8974, 177.6667, 65.3077}, 
			{146.0769, 241.2051, 34.2564}, 
			{180.6667, 32.7436, 157.0256}, 
			{165.4474, 108.5263, 90.2895}, 
			{228.6923, 102.0513, 90.6410}, 
			{45.7436, 116.8462, 61.1795}, 
			{154.0256, 170.8718, 90.0256}, 
			{75.7179, 239.8205, 91.3846}, 
			{32.3590, 178.8718, 10.4872}, 
			{221.2051, 241.3590, 25.0000}, 
			{10.4615, 220.0000, 34.6923}, 
			{182.0513, 180.7949, 23.3846}, 
			{154.4872, 137.9487, 61.7436}, 
			{174.4872, 15.8974, 24.5385}, 
			{202.1538, 231.7179, 92.0513}, 
			{162.2308, 227.6410, 91.2051}, 
			{23.0000, 118.2632, 21.3421}, 
			{169.8974, 172.2051, 62.8718}, 
			{56.0000, 249.0513, 31.2308}, 
			{29.6842, 228.6316, 135.7105}, 
			{16.8718, 184.5897, 63.4103}, 
			{116.5000, 25.6316, 17.1316}, 
			{77.1795, 176.9487, 94.8205}, 
			{146.8205, 179.1026, 35.6923}, 
			{102.7949, 114.9744, 61.6923}, 
			{197.2368, 12.8158, 63.8421}, 
			{246.3590, 13.4103, 9.1538}, 
			{181.1053, 145.0789, 21.1842}, 
			{47.0513, 180.0769, 91.8718}, 
			{19.7368, 144.4474, 60.0000}, 
			{209.0263, 89.2632, 61.6842}, 
			{140.1316, 207.6053, 94.3947}, 
			{228.7179, 47.4359, 92.6667}, 
			{169.7692, 240.8718, 58.8974}, 
			{114.8718, 143.7692, 34.7692}, 
			{19.4474, 175.3421, 90.8684}, 
			{65.5897, 242.6923, 116.2051}, 
			{168.7368, 210.8684, 62.3158}, 
			{199.4615, 53.0513, 62.6154}, 
			{83.1282, 141.3846, 64.8462}, 
			{241.1026, 85.3077, 62.0513}, 
			{195.3947, 74.3421, 21.7632}, 
			{140.6842, 173.3421, 60.2368}, 
			{56.7179, 222.2564, 9.4615}, 
			{115.7692, 184.5897, 61.2051}, 
			{74.6923, 218.7179, 8.2564}, 
			{114.7949, 144.9487, 11.2308}, 
			{37.4872, 242.3333, 92.3846}, 
			{144.8462, 180.4872, 12.0769}, 
			{54.4737, 144.6316, 10.7368}, 
			{14.6579, 240.5000, 89.9474}, 
			{116.0769, 176.9744, 91.0513}, 
			{148.8974, 143.8205, 35.9487}, 
			{82.8158, 142.9737, 10.5526}, 
			{94.0000, 202.7632, 64.4737}, 
			{244.6410, 13.6410, 34.9231}, 
			{196.7179, 47.8974, 8.7949}, 
			{195.8421, 12.7895, 10.6316}, 
			{167.0513, 240.4103, 6.6923}, 
			{56.5128, 146.6154, 36.5641}, 
			{221.4737, 15.5789, 12.6316}, 
			{86.4359, 144.3846, 34.2821}, 
			{224.7368, 15.6053, 33.8684}, 
			{74.7949, 246.6923, 33.0000}, 
			{222.1842, 14.5526, 62.3684}, 
			{113.5641, 210.6923, 88.7179}, 
			{93.2105, 216.0789, 89.6579}, 
			{94.6579, 240.9737, 91.0789}, 
			{12.1053, 175.4474, 11.8158}, 
			{8.5789, 178.0789, 34.0000}, 
			{109.2564, 143.9744, 59.8974}, 
			{54.1842, 210.5263, 92.4474}, 
			{116.6154, 167.5897, 63.5128}, 
			{84.8947, 166.8947, 64.5526}, 
			{56.8974, 242.7949, 91.0769}, 
			{83.2821, 184.2564, 62.7692}, 
			{74.0256, 242.4359, 52.0256}, 
			{83.9744, 169.7436, 35.7949}, 
			{113.5641, 219.0000, 9.8718}, 
			{37.5128, 241.0513, 51.9231}, 
			{53.9211, 168.8684, 11.0263}, 
			{113.6842, 170.5000, 10.9737}, 
			{52.3846, 221.9744, 31.0769}, 
			{91.9474, 201.3684, 33.7105}, 
			{54.5128, 185.0513, 34.3333}, 
			{54.3421, 203.9474, 33.2895}, 
			{55.6316, 205.6579, 10.3947}, 
			{73.4359, 220.4615, 30.7179}, 
			{55.9487, 240.1282, 66.7949}, 
			{113.9744, 202.3077, 32.5641}, 
			{32.0256, 219.4359, 33.5897}, 
			{72.6667, 203.0000, 33.4872}, 
			{115.1026, 219.0513, 33.4359}, 
			{74.5789, 200.8947, 9.8158}, 
			{93.3846, 249.1282, 34.9744}, 
			{93.2308, 218.3590, 10.6154}, 
			{36.0000, 203.2632, 9.7368}, 
			{111.6410, 247.8974, 33.6410}, 
			{94.3684, 200.8947, 10.2368}, 
			{114.8158, 202.1316, 10.7632}, 
			{116.2821, 249.0513, 9.4615}, 
			{57.0263, 241.0000, 52.3421}, 
			{117.7692, 244.1282, 91.2308}, 
			{13.2564, 220.8462, 9.7179}, 
			{11.8684, 203.6316, 9.0789}, 
			{73.0000, 247.5897, 11.4615}, 
			{34.6410, 248.9231, 10.5641}, 
			{113.2564, 240.8718, 55.0769}, 
			{13.1795, 248.5641, 32.4615}, 
			{93.6316, 241.7368, 55.2105}, 
			{53.5897, 185.9744, 12.5385}, 
			{93.2564, 249.6667, 11.3077}, 
			{111.4359, 235.1282, 36.7949}, 
			{72.8205, 234.1282, 36.0000}, 
			{36.4103, 234.2821, 34.4103}, 
			{74.1026, 233.2051, 9.6667}, 
			{34.7105, 234.0263, 9.8947}, 
			{116.8421, 235.0789, 9.6579}, 
			{14.2632, 234.8421, 11.2105}, 
			{13.1316, 233.7105, 34.8684}, 
			{94.6053, 237.5789, 9.7895}, 
			{90.2368, 236.2895, 32.9211}, 
			{57.0263, 235.9211, 32.8684}, 
			{53.3158, 235.5263, 11.0526}, 
			{55.9487, 247.7949, 11.3590}};
	
	// ----------------------------------------------
	
	@Test
	public void test1() {
		runTest(ColorTestImage.Clown, 5, 5, Matrix.toFloat(clown5));
		runTest(ColorTestImage.Clown, 16, 16, Matrix.toFloat(clown16));
	}
	
	@Test
	public void test2() {
		runTest(ColorTestImage.RandomColors, 5, 5, Matrix.toFloat(randomColors5));
		runTest(ColorTestImage.RandomColors, 256, 256, Matrix.toFloat(randomColors256));
	}

	@Test
	public void test3() {
		runTest(ColorTestImage.TwoColors, 16, 2, Matrix.toFloat(twoColors2));
		runTest(ColorTestImage.TwoColors, 2, 2, Matrix.toFloat(twoColors2));
		runTest(ColorTestImage.TwoColors, 1, 1, Matrix.toFloat(twoColors1));
	}
	
	@Test
	public void test4() {
		runTest(ColorTestImage.SingleColor, 16, 1, Matrix.toFloat(singleColor1));
		runTest(ColorTestImage.SingleColor, 1, 1, Matrix.toFloat(singleColor1));
	}
	
	// --------------------------------
	
	static boolean SHOWCOLORMAP = false;
	
	private void runTest(ImageResource ir, int K, int Kact, float[][] colormap) {
		ColorProcessor cp = (ColorProcessor) ir.getImagePlus().getProcessor();
		ColorQuantizer quantizer = new MedianCutQuantizer((int[])cp.getPixels(), K);
		assertEquals(Kact, quantizer.getColorCount());
		float[][] cm = quantizer.getColorMap();
		
		if (SHOWCOLORMAP) {
			PrintPrecision.set(4);
			System.out.println(Matrix.toString(cm));
			System.out.println();
		}
		NumericTestUtils.assert2dArrayEquals(colormap, cm, TOL);
	}

}
