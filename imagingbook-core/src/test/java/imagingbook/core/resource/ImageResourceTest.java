package imagingbook.core.resource;

import static org.junit.Assert.*;

import org.junit.Test;

public class ImageResourceTest {
	
	private enum DummyImageResource implements ImageResource {
		a,
		A_png,
		foo_tif,
		The_File_jpg,
		_Some____File_txt,
		a_File_With_Explicit_Path("./the/explicit/filename.txt"),
		;

		private final String filename;
		
		DummyImageResource() {
			this.filename = autoName();
		}
		
		DummyImageResource(String filename) {
			this.filename = filename;
		}
		
		@Override
		public String getFileName() {
			return filename;
		}	
	}

	@Test
	public void testAutoName() {
		assertEquals("a.png", DummyImageResource.a.getFileName());
		assertEquals("A.png", DummyImageResource.A_png.getFileName());
		assertEquals("foo.tif", DummyImageResource.foo_tif.getFileName());
		assertEquals("The_File.jpg", DummyImageResource.The_File_jpg.getFileName());
		assertEquals("_Some____File.txt", DummyImageResource._Some____File_txt.getFileName());	
	}
	
	@Test
	public void testExplicitName() {
		assertEquals("./the/explicit/filename.txt", DummyImageResource.a_File_With_Explicit_Path.getFileName());
	}

}
