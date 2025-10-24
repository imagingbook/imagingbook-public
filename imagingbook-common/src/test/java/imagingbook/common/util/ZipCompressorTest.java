/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
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
	
	@Test
	public void test0() {
		String inputText = "some really foolish text 12345 $%§&";
//		System.out.println("input=\"" + inputText + "\"");
		
		ZipCompressor compr = new ZipCompressor();
		byte[] compressed = compr.compressByteArray(inputText.getBytes());
//		System.out.println("compressed=\"" + new String(compressed) + "\" + length=" + compressed.length);

		byte[] decompressed = compr.decompressByteArray(compressed);
		String outputText = new String(decompressed);
//		System.out.println("decompressed=\"" + outputText + "\"");
		assertEquals(inputText, outputText);
	}

	@Test	// random byte[]
	public void test1() {
		ZipCompressor compr = new ZipCompressor();
		
		byte[] input = makeRandomByteArray(100000);
//		System.out.println("input length = " + input.length);
		
		byte[] compressed = compr.compressByteArray(input);
//		System.out.println("compressed length = " + compressed.length);
		
		byte[] decompressed = compr.decompressByteArray(compressed);
//		System.out.println("decompressed length = " + decompressed.length);
		
		assertEquals(input.length, decompressed.length);
		assertArrayEquals(input, decompressed);
	}
	
	@Test	// byte[] data from ByteProcessor pixels
	public void test2() {
		ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
		assertTrue(ip instanceof ByteProcessor);
		
		ZipCompressor compr = new ZipCompressor();
		
		byte[] input = (byte[]) ip.getPixels();
//		System.out.println("input length = " + input.length);
		
		byte[] compressed = compr.compressByteArray(input);
//		System.out.println("compressed length = " + compressed.length);
		
		byte[] decompressed = compr.decompressByteArray(compressed);
//		System.out.println("decompressed length = " + decompressed.length);
		
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
