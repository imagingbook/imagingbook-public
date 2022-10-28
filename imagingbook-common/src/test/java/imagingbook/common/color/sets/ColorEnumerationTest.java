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
		
		assertEquals(colors[0], BasicAwtColor.Blue.getColor());
		assertEquals(colors[1], CssColor.Aqua.getColor());
		assertEquals(colors[2], RalColor.RAL1012.getColor());
	}
	
	@Test
	public void test3() {
		ColorEnumeration ce1 = BasicAwtColor.Blue;
		Color col = ce1.getColor();
		assertNotNull(col);
		ColorEnumeration ce2 = ColorEnumeration.findColor(col, BasicAwtColor.class);
		assertNotNull(ce2);
		assertEquals(ce1, ce2);
	}

}
