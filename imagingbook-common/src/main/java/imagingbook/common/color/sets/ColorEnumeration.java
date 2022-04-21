package imagingbook.common.color.sets;

import java.awt.Color;

/**
 * Used for color enum types. This means that every enum class (item) implements this interface.
 * @author WB
 *
 */
public interface ColorEnumeration {
	
	public Color getColor();
	
	public default int getRGB() {
		return getColor().getRGB();
	}

	public static Color[] getColors(Class<? extends ColorEnumeration> clazz) {
		ColorEnumeration[] cols = clazz.getEnumConstants(); 
		if (cols == null) {
			throw new RuntimeException(ColorEnumeration.class.getSimpleName() + " may only be implemented by enum types!");
		}
//		Color[] colors = new Color[cols.length];
//		for (int i = 0; i < cols.length; i++) {
//			colors[i] = cols[i].getColor();
//		}
//		return colors;
		return getColors(cols);
	}
	
	public static Color[] getColors(ColorEnumeration... cols) {
		Color[] colors = new Color[cols.length];
		for (int i = 0; i < cols.length; i++) {
			colors[i] = cols[i].getColor();
		}
		return colors;
	}
	
	
	public static void main(String[] args) {
		
		Color[] colors = ColorEnumeration.getColors(BasicAwtColor.class);
		for (Color c : colors) {
			System.out.println(c.toString());
		}
	}
	
}
