package imagingbook.common.mser;

import static imagingbook.DATA.MserTestImage.AllBlack;
import static imagingbook.DATA.MserTestImage.AllWhite;
import static imagingbook.DATA.MserTestImage.Blob1;
import static imagingbook.DATA.MserTestImage.Blob2;
import static imagingbook.DATA.MserTestImage.Blob3;
import static imagingbook.DATA.MserTestImage.BlobLevelTest;
import static imagingbook.DATA.MserTestImage.BlobLevelTestNoise;
import static imagingbook.DATA.MserTestImage.BlobOriented;
import static imagingbook.DATA.MserTestImage.BlobsInWhite;
import static imagingbook.DATA.MserTestImage.BoatsTiny;
import static imagingbook.DATA.MserTestImage.BoatsTinyB;
import static imagingbook.DATA.MserTestImage.BoatsTinyBW;
import static imagingbook.DATA.MserTestImage.BoatsTinyW;
import static imagingbook.DATA.MserTestImage.BoatsTinyW2;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.common.mser.MserDetector.Parameters;
import imagingbook.common.mser.components.Component;
import imagingbook.common.mser.components.ComponentTree.Method;
import imagingbook.core.resource.ImageResource;

public class MserDetectorTest {

	private Parameters params = null;
	MserDetector detector = null;

	/**
	 * Runs validation on component trees from different images.
	 */
	@Test
	public void test1() {
		params = new Parameters();	// MSER default parameters

		params.method = Method.LinearTime;
		params.delta = 5;
		params.minAbsComponentArea = 	3;
		params.minRelCompSize = 	0.0001;
		params.maxRelCompSize = 	0.25;
		params.maxSizeVariation = 		0.25;
		params.minDiversity = 			0.50;
		params.constrainEllipseSize = 	true;
		params.minCompactness = 		0.2;		
		params.validateComponentTree =	false;

		detector = new MserDetector(params);
		
		runMser(Blob1, 3);
		runMser(Blob2, 6);
		runMser(Blob3, 9);
		runMser(BlobLevelTest, 2);
		runMser(BlobLevelTestNoise, 2);
		runMser(BlobOriented, 3);
		runMser(BlobsInWhite, 3);
		runMser(BoatsTiny, 21);
		runMser(BoatsTinyB, 22);
		runMser(BoatsTinyBW, 1);
		runMser(BoatsTinyW, 21);
		runMser(BoatsTinyW2, 22);
		runMser(AllBlack, 0);
		runMser(AllWhite, 0);
	}

	private void runMser(ImageResource res, int mserExpected) {
//		System.out.println("running " + res);
		ByteProcessor ip = (ByteProcessor) res.getImage().getProcessor();
		List<Component<MserData>> msers = detector.applyTo((ByteProcessor) ip);
//		System.out.println(msers.size());
		assertEquals("detected MSER components (" + res + ")", mserExpected, msers.size());
	}

}
