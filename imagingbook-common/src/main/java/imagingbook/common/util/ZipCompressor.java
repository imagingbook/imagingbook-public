package imagingbook.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * This class provides ZIP-compression/decompression of byte arrays.
 * 
 * @author WB
 * @version 2022/09/06
 */
public class ZipCompressor {

	private final boolean nowrap;	// true -> ZLIB header and checksum fields will not be used
	private final int bufsize;
	
	public ZipCompressor(int bufsize, boolean nowrap) {
		this.bufsize = bufsize;
		this.nowrap = nowrap;
	}

	public ZipCompressor() {
		this(1024, true);
	}
	
	// -----------------------------------------------------------
			
	public byte[] compressByteArray(byte[] inputBytes){
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, nowrap);
		deflater.setInput(inputBytes);
		deflater.finish();

		final byte[] buf = new byte[bufsize];
		byte[] outputBytes = null;

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			while (!deflater.finished()) {
				int size = deflater.deflate(buf);
				bos.write(buf, 0, size);
			}
			outputBytes = bos.toByteArray();
		} catch (IOException e) { }

		return outputBytes;
	}

	public byte[] decompressByteArray(byte[] inputBytes){
		Inflater inflater = new Inflater(nowrap);
		inflater.setInput(inputBytes);

		final byte[] buf = new byte[bufsize];
		byte[] outputBytes = null;

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			while(!inflater.finished()){
				int size = inflater.inflate(buf);
				bos.write(buf, 0, size);
			}
			outputBytes = bos.toByteArray();
		} catch (IOException | DataFormatException e) { } 

		return outputBytes;
	}
	
}
