package imagingbook.common.color.iterate;

import java.awt.Color;

import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.color.sets.ColorEnumeration;

/**
 * <p>
 * This class defines methods for iterating over an ordered set of AWT colors.
 * The color set can be constructed from individual AWT colors 
 * (see {@link #ColorSequencer(Color...)})
 * or an enum class implementing
 * {@link ColorEnumeration} (see {@link #ColorSequencer(Class)}).
 * </p>
 * <p>
 * Usage examples:
 * </p>
 * <pre>
 * // from individual colors (or array of colors):
 * ColorSequencer iter1 = new FiniteLinearColorSequencer(Color.blue, Color.green, Color.red);
 * for (int i = 0; i < 10; i++) {
 * 	Color c = iter1.next();
 * 	// use color c
 * }
 * 
 * // from enum type:
 * FiniteLinearColorSequencer iter2 = new FiniteLinearColorSequencer(BasicAwtColor.class);
 * iter2.reset(5);
 * for (int i = 0; i < 10; i++) {
 * 	Color c = iter2.nextRandom();
 * 	// use color c
 * }
 * </pre>
 * 
 * @author WB
 * @version 2022/04/06
 * 
 * @see Color
 */
public class FiniteLinearColorSequencer implements ColorSequencer {

	protected final Color[] colorArray;
	protected int next = 0;

	public FiniteLinearColorSequencer(Color... colors) {
		if (colors.length == 0) {
			throw new IllegalArgumentException("color set may not be empty!");
		}
		this.colorArray = colors;
	}

	public FiniteLinearColorSequencer(Class<? extends ColorEnumeration> clazz) {
		this.colorArray = ColorEnumeration.getColors(clazz);
	}
	
	// -------------------------------------------------------

	public int size() {
		return colorArray.length;
	}

	public Color getColor(int idx) {
		return colorArray[idx];
	}

	public Color[] getColors() {
		return colorArray;
	}

	// --- iteration stuff -----------------------------------

	/**
	 * Reset the iterator, such that the next color returned by {@link #next()} has
	 * index 0.
	 */
	public void reset() {
		reset(0);
	}

	/**
	 * Reset the iterator such that the index of the item returned by the following
	 * call to {@link #next()} has the specified start index.
	 * 
	 * @param start the new start index
	 */
	public void reset(int start) {
		this.next = Math.floorMod(start - 1, colorArray.length);
	}

	@Override
	public Color next() {
		next = (next + 1) % colorArray.length;
		return colorArray[next];
	}

	// ------------------------------------------------------

	public static void main(String[] args) {

		ColorSequencer iter1 = new FiniteLinearColorSequencer(Color.blue, Color.green, Color.red);
		for (int i = 0; i < 10; i++) {
			Color c = iter1.next();
			// use color c
			System.out.println(c.toString());
		}
		System.out.println();
		FiniteLinearColorSequencer iter2 = new FiniteLinearColorSequencer(BasicAwtColor.class);
		iter2.reset(5);
		for (int i = 0; i < 10; i++) {
			Color c = iter2.next();
			// use color c
			System.out.println(c.toString());
		}
	}

}
