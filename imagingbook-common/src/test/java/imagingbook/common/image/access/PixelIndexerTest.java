package imagingbook.common.image.access;

import org.junit.Assert;
import org.junit.Test;

public class PixelIndexerTest {
	
	static int W = 300;
	static int H = 200;

	@Test
	public void testDefaultValue() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.ZeroValues;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		Assert.assertEquals(15070, pi.getIndex(70, 50));
		Assert.assertEquals(-1, pi.getIndex(W + 10, 50));
		Assert.assertEquals(-1, pi.getIndex(-4, 50));
		Assert.assertEquals(-1, pi.getIndex(70, H));
		Assert.assertEquals(-1, pi.getIndex(70, -1000));
	}
		
	@Test
	public void testNearestBorder() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.NearestBorder;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		Assert.assertEquals(15070, pi.getIndex(70, 50));
		Assert.assertEquals(15299, pi.getIndex(W + 10, 50));
		Assert.assertEquals(15000, pi.getIndex(-4, 50));
		Assert.assertEquals(59770, pi.getIndex(70, H));
		Assert.assertEquals(70, pi.getIndex(70, -1000));
	}

	@Test
	public void testMirrorImage() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.MirrorImage;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		Assert.assertEquals(15070, pi.getIndex(70, 50));
		Assert.assertEquals(15010, pi.getIndex(W + 10, 50));
		Assert.assertEquals(15296, pi.getIndex(-4, 50));
		Assert.assertEquals(70, pi.getIndex(70, H));
		Assert.assertEquals(70, pi.getIndex(70, -1000));
	}
	
	@Test
	public void testException1() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.ThrowException;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		Assert.assertEquals(15070, pi.getIndex(70, 50));
	}
	
	@Test (expected = GridIndexer2D.OutOfImageException.class)
	public void testException2() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.ThrowException;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		pi.getIndex(W + 10, 50);
	}
	
	@Test (expected = GridIndexer2D.OutOfImageException.class)
	public void testException3() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.ThrowException;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		pi.getIndex(-4, 50);
	}
	
	@Test (expected = GridIndexer2D.OutOfImageException.class)
	public void testException4() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.ThrowException;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		pi.getIndex(70, H);
	}
	
	@Test (expected = GridIndexer2D.OutOfImageException.class)
	public void testException5() {
		OutOfBoundsStrategy strategy = OutOfBoundsStrategy.ThrowException;
		GridIndexer2D pi = GridIndexer2D.create(W, H, strategy);
		pi.getIndex(70, -1000);
	}

}
