package imagingbook.common.util.bits;

/**
 * This class implements {@link BitVector} with internal 32-bit {@code int} data.
 * @author WB
 */
public class BitVector32 implements BitVector {
	
	private static final int WL = 32;
	
	private final int[] data;
	private final int length;
	
	public BitVector32(int length) {
		this.length = length;
		int n = (length % WL == 0) ? length / WL : length / WL + 1;	// word count
		this.data = new int[n];
	}
	
	public BitVector32(byte[] bytes) {
		this(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] != 0) {
				this.set(i);
			}
		}
	}
	
	public BitVector32(boolean[] bools) {
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
		final int mask = 1 << (i % WL);
		return (data[i / WL] & mask) != 0;
	}
	
	@Override
	public void set(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		final int mask = 1 << (i % WL);
		data[j] =  data[j] | mask;
	}
	
	@Override
	public void unset(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		final int mask = 0x01 << (i % WL);
		data[j] =  data[j] & ~mask;
	}
	
	@Override
	public void setAll() {
		for (int j = 0; j < data.length; j++) {
			data[j] = ~0;
		}
	}

	@Override
	public void unsetAll() {
		for (int j = 0; j < data.length; j++) {
			data[j] = 0;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(BitVector32.class.getSimpleName() + "[");
		for (int i = 0; i < length; i++) {
			buf.append(this.get(i) ? "1" : "0");
		}
		buf.append("]");
		return buf.toString();
	}
	
	//----------------------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		int K = 99;
//		BitVector32 b = new BitVector32(K);
//		
//		System.out.println(b.toString());
//		b.setAll();
//		System.out.println(b.toString());
//		b.unsetAll();
//		System.out.println(b.toString());
//		System.out.println();
//		
//		for (int i = 0; i < b.length; i++) {
//			b.set(i);
//			System.out.println(b.toString());
//			b.unset(i);
//		}
//	}

}
