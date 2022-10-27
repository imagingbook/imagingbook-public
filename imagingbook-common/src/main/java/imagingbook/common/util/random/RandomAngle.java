package imagingbook.common.util.random;

/**
 * A convenience random generator for angular values.
 * 
 * @author WB
 */
public class RandomAngle extends java.util.Random {
	private static final long serialVersionUID = 1L;
	
	public RandomAngle() {
		super();
	}
	
	public RandomAngle(long seed) {
		super(seed);
	}
	
	/**
	 * Returns a random {@code double} value in the range
	 * [-&pi;,+&pi;].
	 * 
	 * @return a random angle
	 */
	public double nextAngle() {
		return (2 * this.nextDouble() - 1) * Math.PI;
	}

}
