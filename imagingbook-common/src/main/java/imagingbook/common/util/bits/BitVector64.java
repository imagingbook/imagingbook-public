package imagingbook.common.util.bits;

/**
 * This class implements {@link BitVector} with internal 64-bit 
 * {@code long} data.
 * @author WB
 */
public class BitVector64 implements BitVector {
	
	private static final int WL = 64;
	
	private final long[] data;
	private final int length;
	
	public BitVector64(int length) {
		this.length = length;
		int n = (length % WL == 0) ? length / WL : length / WL + 1;	// word count
		this.data = new long[n];
	}
	
	public BitVector64(byte[] bytes) {
		this(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] != 0) {
				this.set(i);
			}
		}
	}
	
	public BitVector64(boolean[] bools) {
		this(bools.length);
		for (int i = 0; i < bools.length; i++) {
			this.set(i, bools[i]);
		}
	}
	
	// ---------------------------------------------------------------------

	@Override
	public int getLength() {
		return this.length;
	}
	
	@Override
	public boolean get(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final long mask = 1L << (i % WL);
		return (data[i / WL] & mask) != 0L;
	}
	
	@Override
	public void set(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		final long mask = 1L << (i % WL);
		data[j] =  data[j] | mask;
	}
	
	@Override
	public void unset(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		long mask = 1L << (i % WL);
		data[j] =  data[j] & ~mask;
	}
	
	@Override
	public void setAll() {
		for (int j = 0; j < data.length; j++) {
			data[j] = ~0L;
		}
	}

	@Override
	public void unsetAll() {
		for (int j = 0; j < data.length; j++) {
			data[j] = 0L;
		}
	}
		
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(BitVector64.class.getSimpleName() + "[");
		for (int i = 0; i < length; i++) {
			buf.append(this.get(i) ? "1" : "0");
		}
		buf.append("]");
		return buf.toString();
	}
	
//	public String toHexString() {
//		StringBuilder buf = new StringBuilder();
//		buf.append(BitVector64.class.getSimpleName() + "[");
//		for (int i = 0; i < data.length; i++) {
//			buf.append(Long.toHexString(data[i]));
//		}
//		buf.append("]");
//		return buf.toString();
//	}
	
	//----------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		int K = 99;
		BitVector64 b = new BitVector64(K);
		
		System.out.println(b.toString());
		b.setAll();
		System.out.println(b.toString());
		b.unsetAll();
		System.out.println(b.toString());
		
//		System.out.println(b.toHexString());
		
		System.out.println();
		
		for (int i = 0; i < b.length; i++) {
			b.set(i);
			System.out.println(b.toString());
			b.unset(i);
		}
	}

}
