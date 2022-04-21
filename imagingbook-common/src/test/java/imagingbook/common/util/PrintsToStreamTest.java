package imagingbook.common.util;



import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

public class PrintsToStreamTest {

	@Test
	public void test() {
		String expected = "name=instance of Foo x=33 y=19";
		Assert.assertEquals(expected, new Foo().printToString());
		Assert.assertEquals(expected, new Foo().toString());
	}
	
	static class Foo implements PrintsToStream {
		String name = "instance of " + this.getClass().getSimpleName();
		int x = 33;
		int y = 19;

		@Override
		public void printToStream(PrintStream strm) {
			strm.format("name=%s x=%d y=%d", name, x, y);
		}
		
		public String toString() {
			return this.printToString();
		}
	}

}
