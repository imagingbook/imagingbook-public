package imagingbook.common.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.sampleimages.GeneralSampleImage;

public class ZipCompressorTest {

	@Test	// random byte[]
	public void test1() {
		ZipCompressor compr = new ZipCompressor();
		
		byte[] input = makeRandomByteArray(100000);
		System.out.println("input length = " + input.length);
		
		byte[] compressed = compr.compressByteArray(input);
		System.out.println("compressed length = " + compressed.length);
		
		byte[] decompressed = compr.decompressByteArray(compressed);
		System.out.println("decompressed length = " + decompressed.length);
		
		assertEquals(input.length, decompressed.length);
		assertArrayEquals(input, decompressed);
	}
	
	@Test	// byte[] data from ByteProcessor pixels
	public void test2() {
		ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImage().getProcessor();
		assertTrue(ip instanceof ByteProcessor);
		
		ZipCompressor compr = new ZipCompressor();
		
		byte[] input = (byte[]) ip.getPixels();
		System.out.println("input length = " + input.length);
		
		byte[] compressed = compr.compressByteArray(input);
		System.out.println("compressed length = " + compressed.length);
		
		byte[] decompressed = compr.decompressByteArray(compressed);
		System.out.println("decompressed length = " + decompressed.length);
		
		assertEquals(input.length, decompressed.length);
		assertArrayEquals(input, decompressed);
	}
	
//	@Test	// byte[] by reading bytes from PNG file
//	public void test3() throws URISyntaxException, IOException {
//		ZipCompressor compr = new ZipCompressor();
//		
////		InputStream is = GeneralTestImage.MonasterySmall.getStream();
//		URL url = GeneralTestImage.Boats.getURL();
//		Path path = Paths.get(url.toURI());
////		File file = path.toFile();
//		
//		byte[] input = Files.readAllBytes(path);
//		System.out.println("input length = " + input.length);
//		
//		byte[] compressed = compr.compressByteArray(input);
//		System.out.println("compressed length = " + compressed.length);
//		
//		byte[] decompressed = compr.decompressByteArray(compressed);
//		System.out.println("decompressed length = " + decompressed.length);
//		
//		assertEquals(input.length, decompressed.length);
//		assertArrayEquals(input, decompressed);
//	}
		
	// --------------------------------------------
	
	private byte[] makeRandomByteArray(int length) {
		byte[] ba = new byte[length];
		Random rg = new Random(17);
		rg.nextBytes(ba);		
		return ba;
	}

}
