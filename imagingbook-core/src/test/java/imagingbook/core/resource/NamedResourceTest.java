package imagingbook.core.resource;

import static org.junit.Assert.*;

import org.junit.Test;

public class NamedResourceTest {
	
	private enum DummyNamedResource implements NamedResource {
		a,
		A_png,
		foo_tif,
		The_File_jpg,
		_Some____File_txt,
		;
	}

	@Test
	public void testAutoName() {
		assertEquals("a.png", DummyNamedResource.a.getFileName());
		assertEquals("A.png", DummyNamedResource.A_png.getFileName());
		assertEquals("foo.tif", DummyNamedResource.foo_tif.getFileName());
		assertEquals("The_File.jpg", DummyNamedResource.The_File_jpg.getFileName());
		assertEquals("_Some____File.txt", DummyNamedResource._Some____File_txt.getFileName());	
	}

}
