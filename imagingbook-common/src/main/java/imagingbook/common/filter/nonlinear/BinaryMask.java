package imagingbook.common.filter.nonlinear;

/**
 * This class defines a binary mask, as used in filters, for example.
 * 
 * @author WB
 * @version 2022/09/10
 */
public class BinaryMask {
	
	private final int centerX;			// mask center position (x)
	private final int centerY;			// mask center position (y)
	private final int count;			// number of nonzero mask elements
	private final byte[][] mask;		// mask[x][y]  specifies the support region
	
	public BinaryMask(byte[][] mask, int centerX, int centerY) {
		this.mask = mask;
		this.centerX = centerX;
		this.centerY = centerY;
		this.count = countElements(mask);
	}
	
	public BinaryMask(byte[][] mask) {
		this(mask, mask.length/2, mask[0].length/2);
	}

	private int countElements(byte[][] maskArray) {
		int cnt = 0;
		for (int i = 0; i < maskArray.length; i++) {
			for (int j = 0; j < maskArray[i].length; j++) {
				if (maskArray[i][j] != 0)
					cnt = cnt + 1;
			}
		}
		return cnt;
	}
	
	/**
	 * Returns the x-coordinate of the mask's reference point.
	 * @return
	 */
	public int getCenterX() {
		return centerX;
	}
	
	/**
	 * Returns the y-coordinate of the mask's reference point.
	 * @return
	 */
	public int getCenterY() {
		return centerY;
	}
	
	/**
	 * Returns the number of inside-mask (1) elements.
	 * @return the number of inside-mask elements
	 */
	public int getElementCount() {
		return count;
	}
	
	/**
	 * Returns a {@code byte[x][y]} with outside mask element values
	 * zero, inside mask elements non-zero.
	 * The first array index corresponds to the horizontal coordinate.
	 * 
	 * @return the mask as a {@code byte[][]}
	 */
	public byte[][] getByteArray() {
		return mask;
	}

}
