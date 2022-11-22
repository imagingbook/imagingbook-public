package imagingbook.common.sift.scalespace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.sampleimages.GeneralSampleImage;

public class GaussianScaleSpaceTest {
	
	static double sigma_0 = 1.6;	// initial scale level
	static double sigma_s = 0.5;	// original image (sampling) scale
	
	static int Q = 3;	// scale steps per octave
	static int P = 4;	// number of octaves
	static int botLevel = 0;	// index q of bottom level in each octave
	static int topLevel = Q;	// index q of top level in each octave
	

	@Test
	public void testScaleSpaceParameters() {
//		FloatProcessor fp = makeSymmetricBoxImage(50, 40);
		ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImage().getProcessor();
		FloatProcessor fp = ip.convertToFloatProcessor();
		
		GaussianScaleSpace gss =
				new GaussianScaleSpace(fp, sigma_s, sigma_0, P, Q, botLevel, topLevel);
		
		assertEquals(P, gss.getP());
		assertEquals(Q, gss.getQ());
		assertEquals(sigma_0, gss.getOctave(0).getAbsoluteScale(0), 1e-6);
		
		for (int p = 0; p < 1; p++) {
			ScaleOctave oct = gss.getOctave(p);
			assertNotNull(oct);
			assertEquals(p, oct.getOctaveIndex());
			
			// check all levels in octave:
			assertEquals(botLevel, oct.getBottomLevelIndex());
			assertEquals(topLevel, oct.getTopLevelIndex());
			
			
			for (int q = botLevel; q <= topLevel; q++) {
				ScaleLevel lvl = oct.getLevel(q);
				assertNotNull(lvl);
			}
		}	
	}
	
	
//	@Test
//	public void testSymmetryOfTestImage() {
//		FloatProcessor fp = makeSymmetricBoxImage(50, 40);
//		double[] chk1 = checkSymmetry(fp, 1e-6f);
//		assertNull(makeChkString(0, 0, chk1), chk1);
//	}

	
//	@Test
//	public void testScaleSpaceSymmetry() {
//		FloatProcessor fp = makeSymmetricBoxImage(512, 256);
//		double[] chk1 = checkSymmetry(fp, 1e-6f);
//		assertNull(makeChkString(0, 0, chk1), chk1);
//		
//		GaussianScaleSpace gss =
//				new GaussianScaleSpace(fp, sigma_s, sigma_0, P, Q, botLevel, topLevel);
//		
//		for (int p = 0; p < 2; p++) {
//			ScaleOctave oct = gss.getOctave(p);
//			for (int q = 0; q < Q; q++) {
//				ScaleLevel lvl = oct.getLevel(q);
//				double[] chk = checkSymmetry(lvl.toFloatProcessor(), 1e-3f);
//				assertNull(makeChkString(p, q, chk), chk);
//			}
//		}
//	}
	
//	// chk = [u, v, u' v', val, val']
//	private String makeChkString(int p, int q, double[] chk) {
//		if (chk == null) return "";
//		return String.format("p=%d q=%d (%d,%d)=%.6f (%d,%d)=%.6f",
//				p, q, (int)chk[0], (int)chk[1], chk[4], (int)chk[2], (int)chk[3], chk[5]);
//	}
	
	// ----------------------------------------------------------
	
	@SuppressWarnings("unused")
	private FloatProcessor makeSymmetricBoxImage(int w2, int h2) {
		// total size is (2 * w2 + 1) x (2 * h2 + 1)
		int width =  2 * w2; // + 1;
		int height = 2 * h2; // + 1;
		int wb = w2 / 2;	// box width
		int hb = h2 / 2;	// box height
		ByteProcessor bp = new ByteProcessor(width, height);
		bp.setValue(255);
		bp.fill();
		for (int u = w2 - wb; u < w2 + wb; u++) {
			for (int v = h2 - hb; v < h2 + hb; v++) {
				bp.putPixel(u, v, 100);
			}
		}	
		return bp.convertToFloatProcessor();
	}
	
	// returns [u, v, u' v', val, val']
	@SuppressWarnings("unused")
	private double[] checkSymmetry(FloatProcessor fp, float tol) {
		int w = fp.getWidth();
		int h = fp.getHeight();
		// check horizontal symmetry:
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w/2; u++) {
				int uu = w - u - 1;
				float p1 = fp.getf(u, v);
				float p2 = fp.getf(uu, v);
				if (Math.abs(p1 - p2) > tol) {
					return new double[] {u, v, uu, v, p1, p2};
				}			
//				assertEquals(p1, p2, tol);
			}
		}
		// check vertical symmetry:
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h/2; v++) {
				int vv = h - v - 1;
				float p1 = fp.getf(u, v);
				float p2 = fp.getf(u, vv);
				if (Math.abs(p1 - p2) > tol) {
					return new double[] {u, v, u, vv, p1, p2};
				}
//				assertEquals(p1, p2, tol);
			}
		}
		return null;
	}

}
