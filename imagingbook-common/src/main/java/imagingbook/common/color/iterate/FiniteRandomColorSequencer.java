package imagingbook.common.color.iterate;

import java.awt.Color;
import java.util.Random;

import imagingbook.common.color.sets.ColorEnumeration;

public class FiniteRandomColorSequencer extends FiniteLinearColorSequencer {
	
	private final Random rg = new Random();

	public FiniteRandomColorSequencer(Color... colors) {
		super(colors);
	}
	
	public FiniteRandomColorSequencer(Class<? extends ColorEnumeration> clazz) {
		super(clazz);
	}
	
	/**
	 * Sets the seed of the internal {@link Random} instance, which
	 * affects successive calls to {@link #nextRandom()} only.
	 * 
	 * @param seed the new seed value
	 * @see Random#setSeed(long)
	 */
	public void setRandomSeed(long seed) {
		rg.setSeed(seed);
	}
	
	@Override
	public Color next() {
		int step = rg.nextInt(Math.max(1, colorArray.length - 1)); // works for a single color too
		next = (next + 1 + step) % colorArray.length;
		return colorArray[next];
	}
	
	
}
