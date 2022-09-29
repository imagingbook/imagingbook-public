package imagingbook.common.color.sets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;

import org.junit.Test;

public class ColorEnumerationTest {

	@Test
	public void test1() {
		Color[] colors = ColorEnumeration.getColors(BasicAwtColor.class);
		assertEquals(colors.length, BasicAwtColor.values().length);
	}
	
	@Test
	public void test2() {
		Color[] colors = ColorEnumeration.getColors(BasicAwtColor.Blue, CssColor.Aqua, RalColor.RAL1012);
		assertEquals(3, colors.length);
	}
	
	@Test
	public void test3() {
		ColorEnumeration ce = BasicAwtColor.Blue;
		Color col = ce.getColor();
		assertNotNull(col);
		assertEquals(ce, ColorEnumeration.findColor(col, BasicAwtColor.class));
	}

}
