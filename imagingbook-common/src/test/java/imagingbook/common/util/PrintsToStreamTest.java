/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
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
		
		@Override
		public String toString() {
			return this.printToString();
		}
	}

}
