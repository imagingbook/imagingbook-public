/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.quantize;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ColorProcessor;
import imagingbook.common.color.quantize.KMeansClusteringQuantizer.InitialClusterMethod;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.ColorTestImage;
import imagingbook.testutils.NumericTestUtils;

public class KMeansClusteringQuantizerTest {
	
	static float TOL = 1e-3f;
	
	static double[][] clown5 = {
			{192.7347, 120.0224, 57.3743}, 
			{145.6186, 69.6457, 24.7129}, 
			{85.6695, 37.3994, 16.2613}, 
			{23.9025, 7.6050, 4.5106}, 
			{227.1758, 192.0444, 156.4250}};
	
	static double[][] clown16 = {
			{161.2824, 83.3667, 21.7515}, 
			{90.9890, 53.2915, 28.6264}, 
			{119.5952, 79.9889, 53.0179}, 
			{68.3246, 27.9968, 11.6236}, 
			{222.1353, 183.6911, 147.2996}, 
			{133.5807, 62.3472, 13.4170}, 
			{105.3292, 32.2921, 8.2006}, 
			{158.3865, 26.2708, 13.9454}, 
			{215.9886, 129.9721, 30.6293}, 
			{152.2511, 112.7894, 83.8808}, 
			{241.6070, 216.9807, 182.9514}, 
			{190.6969, 105.7322, 21.4632}, 
			{204.5204, 148.6921, 109.6800}, 
			{15.1279, 4.8880, 3.5583}, 
			{195.0388, 115.6499, 69.2909}, 
			{40.2265, 12.4539, 6.1504}};	
	
	static double[][] twoColors2 = {
			{70.0000, 255.0000, 0.0000}, 
			{255.0000, 0.0000, 0.0000}};
	
	static double[][] twoColors1 = {
			{108.2146, 202.3258, 0.0000}};
	
	static double[][] singleColor1 = {
			{70.0000, 255.0000, 0.0000}};
	
	static double[][] randomColors5 = {
			{72.2629, 130.8099, 48.9067}, 
			{73.9616, 211.7706, 116.0130}, 
			{203.8525, 51.5315, 54.6483}, 
			{52.8931, 219.5422, 33.5436}, 
			{155.5064, 207.3146, 46.2246}};
	
	static double[][] randomColors256 = {
			{5.3750, 204.2500, 21.3750}, 
			{161.3374, 211.1446, 49.4940}, 
			{239.0708, 84.3894, 58.4779}, 
			{152.8544, 238.6602, 11.7767}, 
			{151.3297, 243.8352, 50.1099}, 
			{192.5904, 190.4337, 20.1325}, 
			{185.7326, 235.8721, 14.5000}, 
			{197.9610, 226.5455, 57.2078}, 
			{239.5929, 66.4425, 14.9912}, 
			{230.6667, 238.1282, 73.2051}, 
			{231.4167, 219.2500, 25.5500}, 
			{232.7538, 157.1846, 76.1846}, 
			{235.7048, 23.9819, 59.8253}, 
			{170.9506, 18.7531, 28.0988}, 
			{113.0164, 67.0328, 24.6230}, 
			{66.1538, 33.5385, 65.5385}, 
			{18.1481, 127.4444, 35.6296}, 
			{22.3077, 175.7692, 172.5385}, 
			{98.3636, 132.7273, 203.1818}, 
			{10.6471, 145.8235, 80.3529}, 
			{5.5333, 154.6667, 10.2667}, 
			{80.1034, 175.8276, 114.8448}, 
			{29.0435, 165.0000, 78.7826}, 
			{2.8000, 178.4000, 60.0000}, 
			{56.8378, 183.4865, 75.6487}, 
			{9.4286, 180.4286, 42.4286}, 
			{21.4615, 198.9231, 85.3077}, 
			{44.2647, 197.6176, 45.3824}, 
			{39.2414, 187.0000, 114.1034}, 
			{2.0000, 205.7500, 61.7500}, 
			{2.0000, 215.5000, 70.5000}, 
			{34.5556, 198.9444, 146.6667}, 
			{2.0000, 207.0000, 10.7500}, 
			{37.0345, 210.9310, 95.5172}, 
			{16.1111, 213.7778, 30.0000}, 
			{4.0000, 228.8333, 14.5000}, 
			{52.1389, 233.0278, 64.1111}, 
			{5.8000, 238.0000, 37.0000}, 
			{6.8571, 232.7143, 49.8571}, 
			{0.5000, 239.0000, 9.5000}, 
			{34.0741, 243.4444, 132.1852}, 
			{6.8333, 242.0000, 2.5000}, 
			{8.5000, 249.1250, 75.0000}, 
			{6.3333, 251.5000, 62.6667}, 
			{2.0000, 249.0000, 52.6667}, 
			{4.5000, 250.1667, 30.1667}, 
			{239.3379, 20.9862, 19.4069}, 
			{203.1364, 15.3273, 18.1000}, 
			{44.3846, 52.6923, 19.3077}, 
			{37.4074, 103.9630, 15.1111}, 
			{57.3824, 134.4118, 98.9412}, 
			{5.1667, 158.5000, 41.6667}, 
			{7.0000, 161.5000, 65.5000}, 
			{39.7500, 135.4500, 150.1000}, 
			{11.1875, 182.1875, 22.9375}, 
			{50.6667, 169.1333, 40.5667}, 
			{28.1176, 173.0588, 21.0000}, 
			{10.4375, 179.1250, 80.2500}, 
			{156.6786, 228.4643, 201.0357}, 
			{28.2000, 180.1200, 92.6800}, 
			{1.0000, 194.2500, 59.0000}, 
			{31.7500, 187.4167, 72.7917}, 
			{9.9000, 196.8000, 73.1000}, 
			{22.2105, 196.2632, 46.1053}, 
			{23.4545, 203.0000, 14.9091}, 
			{67.7000, 196.5000, 154.6667}, 
			{31.1667, 223.5556, 3.1667}, 
			{8.3000, 210.8000, 42.1000}, 
			{11.0000, 203.1111, 102.7778}, 
			{9.3333, 215.6667, 13.3333}, 
			{5.6000, 225.6000, 27.9000}, 
			{5.0000, 229.7500, 40.2500}, 
			{26.6842, 230.8947, 88.4211}, 
			{15.6471, 238.8824, 8.2941}, 
			{40.0625, 239.8125, 182.3125}, 
			{11.5833, 244.4167, 89.5833}, 
			{15.5263, 233.2105, 157.0000}, 
			{12.7500, 250.0000, 9.7500}, 
			{2.5000, 254.5000, 2.5000}, 
			{3.2500, 252.7500, 11.2500}, 
			{208.2613, 49.7928, 29.4775}, 
			{74.8214, 78.8214, 78.9643}, 
			{27.8519, 141.6296, 56.7778}, 
			{12.3333, 153.6667, 105.0000}, 
			{43.9583, 146.6667, 75.7917}, 
			{17.1111, 169.0556, 131.1111}, 
			{49.7600, 167.9600, 101.0800}, 
			{11.9000, 155.7000, 54.7000}, 
			{26.7308, 148.7308, 7.8077}, 
			{3.0000, 167.2000, 28.8000}, 
			{20.6250, 188.1250, 4.2500}, 
			{115.8478, 179.9348, 120.5000}, 
			{59.0833, 213.7500, 108.1944}, 
			{4.6000, 201.2000, 42.2000}, 
			{31.0435, 217.5217, 57.2609}, 
			{6.5000, 220.1667, 38.6667}, 
			{31.2174, 232.7826, 64.8261}, 
			{10.6667, 222.6000, 4.4667}, 
			{20.2381, 210.2857, 123.0952}, 
			{16.2143, 244.2857, 55.8571}, 
			{11.0000, 233.6471, 67.3529}, 
			{6.2500, 236.2500, 21.2500}, 
			{14.7143, 251.5714, 24.5714}, 
			{29.6957, 247.6087, 51.8261}, 
			{31.8286, 245.6286, 104.7429}, 
			{58.9167, 213.1667, 208.4167}, 
			{226.5726, 22.3419, 103.9744}, 
			{183.8286, 16.5048, 71.5524}, 
			{172.1279, 57.1744, 16.8605}, 
			{20.4444, 108.4444, 73.0000}, 
			{69.1053, 117.6579, 33.7895}, 
			{18.0952, 124.4762, 113.0000}, 
			{8.3846, 113.9231, 11.5385}, 
			{47.6154, 118.5385, 56.0769}, 
			{49.4186, 135.1628, 13.3721}, 
			{9.7273, 171.4545, 10.6364}, 
			{30.2121, 173.3333, 50.5455}, 
			{108.8276, 196.8965, 170.2759}, 
			{6.5000, 187.5000, 62.2500}, 
			{7.0000, 195.4286, 12.8571}, 
			{25.6667, 199.6667, 31.7407}, 
			{20.2308, 207.6923, 2.8462}, 
			{6.7500, 199.2500, 3.3750}, 
			{9.5000, 225.2500, 78.8750}, 
			{26.4643, 200.5000, 58.5714}, 
			{5.2000, 217.6000, 21.8000}, 
			{26.5882, 213.8235, 40.1176}, 
			{14.6429, 218.4286, 94.6429}, 
			{213.3600, 210.2000, 137.6400}, 
			{28.9130, 230.4348, 9.9130}, 
			{14.5385, 226.7692, 45.5385}, 
			{69.7368, 240.8947, 141.5789}, 
			{69.8481, 242.7722, 53.8228}, 
			{62.7959, 242.1225, 108.8980}, 
			{35.9286, 246.0357, 80.4643}, 
			{2.6667, 249.0000, 4.6667}, 
			{13.0769, 250.3846, 41.7692}, 
			{162.4595, 35.1351, 153.2703}, 
			{126.2429, 21.7714, 20.4143}, 
			{23.0526, 83.6316, 53.3684}, 
			{229.9302, 134.9070, 23.8488}, 
			{72.3667, 93.1000, 13.6667}, 
			{92.2653, 126.9796, 11.7959}, 
			{46.7143, 159.3428, 14.2857}, 
			{182.8209, 182.0448, 89.2239}, 
			{11.0667, 196.2667, 32.6667}, 
			{29.2174, 187.7391, 18.2609}, 
			{59.4762, 204.6190, 32.8095}, 
			{194.7619, 129.6190, 164.5238}, 
			{39.3514, 202.9460, 11.6216}, 
			{48.8571, 184.9714, 24.2286}, 
			{72.9107, 203.6250, 63.6607}, 
			{12.3500, 220.7000, 57.7000}, 
			{35.0690, 221.8965, 28.2069}, 
			{48.2727, 233.2500, 9.6591}, 
			{35.5714, 247.5714, 17.2286}, 
			{70.5714, 225.4107, 79.0893}, 
			{7.7059, 238.2353, 115.5882}, 
			{31.1500, 249.0000, 31.9500}, 
			{4.2000, 241.4000, 44.6000}, 
			{94.9559, 221.8529, 64.8088}, 
			{66.5690, 244.6035, 10.0000}, 
			{63.3636, 245.5000, 83.8636}, 
			{46.7143, 224.0286, 85.4571}, 
			{47.1316, 248.1579, 57.8421}, 
			{101.3585, 242.1509, 113.6415}, 
			{12.3333, 243.0000, 17.8333}, 
			{42.8571, 239.2041, 36.9184}, 
			{21.5333, 239.8000, 28.2667}, 
			{25.9000, 249.3000, 4.6500}, 
			{5.5714, 250.0000, 17.8571}, 
			{92.1803, 244.7869, 82.5410}, 
			{60.4342, 245.9868, 28.5263}, 
			{44.2500, 247.1667, 5.8750}, 
			{138.3696, 19.7391, 99.2826}, 
			{232.5417, 39.8125, 159.5625}, 
			{80.5641, 119.3846, 67.4359}, 
			{220.6212, 92.1667, 121.4848}, 
			{131.5385, 120.9808, 22.1154}, 
			{72.1429, 149.6905, 68.8333}, 
			{123.8837, 99.0233, 112.3488}, 
			{45.4643, 149.1786, 44.6786}, 
			{106.7708, 154.2083, 71.7292}, 
			{17.1667, 160.6667, 33.4167}, 
			{150.2439, 156.7805, 134.8537}, 
			{130.7917, 168.9167, 86.4167}, 
			{68.4167, 159.3056, 10.9722}, 
			{67.6389, 149.0000, 40.1667}, 
			{89.9167, 156.7708, 19.8542}, 
			{95.2338, 194.2467, 30.6753}, 
			{84.0909, 171.5000, 80.0455}, 
			{68.9375, 176.3542, 27.8958}, 
			{75.4143, 201.1286, 14.2714}, 
			{12.8000, 209.4000, 75.8000}, 
			{20.6667, 217.0000, 14.0667}, 
			{47.5769, 207.3462, 65.9615}, 
			{49.5833, 215.2500, 10.3750}, 
			{100.1094, 193.9219, 64.3750}, 
			{158.1111, 209.1944, 132.0000}, 
			{21.0625, 231.6875, 18.1250}, 
			{18.0769, 229.1538, 33.4615}, 
			{85.8293, 218.3415, 129.4390}, 
			{27.5882, 232.1765, 44.4706}, 
			{45.2083, 217.5833, 124.5000}, 
			{132.3044, 232.0978, 30.3043}, 
			{92.6508, 245.7937, 53.6984}, 
			{116.4149, 238.4362, 59.2660}, 
			{91.5682, 243.7273, 10.2273}, 
			{84.1266, 243.0380, 31.0380}, 
			{109.5647, 244.2353, 33.9647}, 
			{112.7000, 239.7333, 148.7667}, 
			{118.0577, 100.4808, 55.2692}, 
			{206.2967, 90.0879, 20.1429}, 
			{102.3158, 123.4211, 97.5789}, 
			{32.4706, 80.4118, 128.5882}, 
			{187.8438, 164.1094, 50.0625}, 
			{102.6719, 138.1250, 44.6406}, 
			{135.1867, 173.0267, 53.0800}, 
			{84.2308, 131.2308, 145.2692}, 
			{173.4127, 133.4286, 16.3651}, 
			{150.7000, 169.8286, 20.9571}, 
			{121.4462, 152.7385, 18.6462}, 
			{66.4333, 176.3667, 55.1667}, 
			{38.4643, 179.2500, 6.1429}, 
			{110.7885, 208.8654, 42.3654}, 
			{119.4474, 212.2895, 118.5789}, 
			{59.7895, 186.6053, 8.2632}, 
			{102.7160, 206.4691, 8.6173}, 
			{51.6829, 224.4146, 46.3659}, 
			{129.1609, 211.0690, 10.7701}, 
			{68.6610, 225.6610, 22.0169}, 
			{130.4805, 210.1688, 58.7922}, 
			{97.7808, 221.6712, 27.3288}, 
			{178.2973, 243.0540, 120.1351}, 
			{76.0149, 221.9104, 5.3433}, 
			{77.6250, 222.3472, 45.5833}, 
			{94.2687, 220.4179, 94.6418}, 
			{52.4146, 222.8049, 27.5610}, 
			{135.7174, 242.3913, 113.2174}, 
			{125.5000, 238.2625, 83.7375}, 
			{119.0316, 241.5684, 7.9158}, 
			{165.9194, 236.6290, 83.4677}, 
			{199.3762, 61.0198, 79.7129}, 
			{191.2500, 111.6500, 77.5833}, 
			{150.5968, 54.9677, 71.6452}, 
			{165.8689, 95.9344, 35.6393}, 
			{150.4848, 142.7273, 63.6364}, 
			{96.3492, 172.3810, 45.6667}, 
			{131.6711, 196.3421, 30.7895}, 
			{85.2143, 180.8810, 10.3333}, 
			{115.2692, 180.0897, 12.7051}, 
			{160.5467, 204.2133, 12.9867}, 
			{72.8627, 194.2941, 43.9804}, 
			{67.0000, 197.8667, 93.5778}, 
			{113.0667, 203.0833, 82.2000}, 
			{145.7164, 203.6716, 92.4179}};
	
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
		KMeansClusteringQuantizer.RandomSeed = 17;	// not needed?
		ColorProcessor cp = (ColorProcessor) ir.getImagePlus().getProcessor();
		ColorQuantizer quantizer = new KMeansClusteringQuantizer((int[])cp.getPixels(), K, InitialClusterMethod.Random, 500);
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
