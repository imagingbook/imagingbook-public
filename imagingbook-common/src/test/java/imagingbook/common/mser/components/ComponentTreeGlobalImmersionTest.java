package imagingbook.common.mser.components;

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
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.common.mser.components.ComponentTree.Method;
import imagingbook.core.resource.ImageResource;

public class ComponentTreeGlobalImmersionTest {
	
	/**
	 * Runs validation on component trees from different images.
	 */
	@Test
	public void test1() {	
		run1(Blob1);
		run1(Blob2);
		run1(Blob3);
		run1(BlobLevelTest);
		run1(BlobLevelTestNoise);
		run1(BlobOriented);
		run1(BlobsInWhite);
		run1(BoatsTiny);
		run1(BoatsTinyB);
		run1(BoatsTinyBW);
		run1(BoatsTinyW);
		run1(BoatsTinyW2);
		run1(AllBlack);
		run1(AllWhite);
	}
	
	private void run1(ImageResource res) {
		ByteProcessor ip = (ByteProcessor) res.getImage().getProcessor();
		ComponentTree<?> ct = ComponentTree.from(ip, Method.GlobalImmersion); //new ComponentTreeClassic<>(ip);
		Assert.assertTrue("component tree validation failed: " + res, ct.validate());
	}
	
	// ----------------------------------------------------------

	/**
	 * Checks the expected number of components and leaves on different
	 * images under rotation and reflection.
	 */
	@Test
	public void test2() {
		run2(Blob1, 6, 1);
		run2(Blob2, 11, 2);
		run2(Blob3, 32, 8);
		run2(BlobLevelTest, 3, 2);
		run2(BlobLevelTestNoise, 341, 159);
		run2(BlobOriented, 10, 1);
		run2(BlobsInWhite, 4, 3);
		run2(BoatsTiny, 312, 70);
		run2(BoatsTinyB, 309, 69);
		run2(BoatsTinyBW, 3, 2);
		run2(BoatsTinyW, 313, 70);
		run2(BoatsTinyW2, 312, 69);
		run2(AllBlack, 1, 1);
		run2(AllWhite, 1, 1);
	}
	
	private void run2(ImageResource res, int noComponents, int noLeaves) {
		ByteProcessor ip = (ByteProcessor) res.getImage().getProcessor();
		
		for (int i = 0; i < 2; i++) {	// 2 rotations
			check2(ip, noComponents, noLeaves);
			ip.flipHorizontal();
			check2(ip, noComponents, noLeaves);
			ip.flipVertical();
			check2(ip, noComponents, noLeaves);
			ip.flipHorizontal();
			check2(ip, noComponents, noLeaves);
			
			ip = (ByteProcessor) ip.rotateLeft();
		}
	}

	
	private void check2(ByteProcessor ip, int noCompomenents, int noLeaves) {
		ComponentTree<?> ct = ComponentTree.from(ip, Method.GlobalImmersion); //new ComponentTreeClassic<>(ip);
		
		Component<?> root = ct.getRoot();
		assertNotNull(root);
		int imgSize = ip.getWidth() * ip.getHeight();
		assertEquals("checking size of root component", root.getSize(), imgSize);
		
		Collection<? extends Component<?>> components = ct.getComponents();
		assertEquals("checking expected total number of components", noCompomenents, components.size());
		
		Collection<? extends Component<?>> leaves = ct.getLeaves();
		assertEquals("checking expected number of leaf components", noLeaves, leaves.size());
	}
}