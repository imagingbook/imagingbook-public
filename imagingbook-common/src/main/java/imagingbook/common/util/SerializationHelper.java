/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Helper class providing static methods for writing/reading
 * serialized data to/from files. It is recommended to serialize
 * only data structures composed of standard Java types.
 * Otherwise, if self-defined classes are reloaded, classes of 
 * previously serialized objects may not match any more, causing
 * a {@link ClassNotFoundException} to be thrown.
 * 
 * @author WB
 * @version 2022/07/28
 */
public abstract class SerializationHelper {
	
	// This class is not supposed to be instantiated.
	private SerializationHelper() {}
	
	/**
	 * Writes a serialized representation of an arbitrary Java object to 
	 * a file.
	 * @param obj the object to be serialized.
	 * @param file the file to write to.
	 * @return the full path of the written file.
	 */
	public static String writeObject(Object obj, File file) {
		String path = file.getAbsolutePath();
		try (FileOutputStream strm = new FileOutputStream(file);
			 OutputStream buffer = new BufferedOutputStream(strm);
			 ObjectOutput output = new ObjectOutputStream(buffer);) 
		{
			output.writeObject(obj);
		} catch (IOException e) {
			System.err.println(e.toString());
			return null;
		}
		return path;
	}

	/**
	 * Reads an object from a serialization file.
	 * The return value must be cast to the appropriate type, which
	 * must be known.
	 * @param file the file to read.
	 * @return the object reconstructed from the file representation or null if unsuccessful.
	 */
	public static Object readObject(File file) {
		Object obj = null;
		try (InputStream strm = new FileInputStream(file);
			 InputStream buffer = new BufferedInputStream(strm);
			 ObjectInput input = new ObjectInputStream(buffer);) 
		{
			obj = input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			System.err.println(e.toString());
			return null;
		}
		return obj;
	}

}
