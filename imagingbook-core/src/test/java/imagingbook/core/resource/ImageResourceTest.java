package imagingbook.core.resource;

import static org.junit.Assert.*;

import org.junit.Test;

public class ImageResourceTest {
	
	private enum DummyNamedResource implements ImageResource {
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
	
//	public static void main(String[] args) {
//		for (ImageResource item : DummyNamedResource.values()) {
//			System.out.println(item + " --> " + item.getFileName());
//		}
//	}

}
