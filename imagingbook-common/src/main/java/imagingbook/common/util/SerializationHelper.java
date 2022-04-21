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
 * Helper class providing simple static methods for writing/reading
 * serialized data to/from files. It is recommended to serialize
 * only data structures composed of standard Java types.
 * Otherwise, if self-defined classes are reloaded, classes of 
 * previously serialized objects may not match any more, causing
 * a ClassNotFoundException to be thrown.
 * 
 * @author WB
 * @version 2018/04/03
 */
public class SerializationHelper {
	
	// This class is not supposed to be instantiated.
	private SerializationHelper() {
	}
	
	/**
	 * Writes a serialized representation of an arbitrary Java object to 
	 * a file. Make sure the serialized object is composed of standard Java types 
	 * only to avoid class loader problems.
	 * @param obj The object to be serialized.
	 * @param fileName The file to write to.
	 * @return The full path of the written file.
	 * @deprecated
	 */
	public static String writeObject(Object obj, String fileName) {
		File file = new File(fileName);
		return writeObject(obj, file);
	}
	
	/**
	 * Writes a serialized representation of an arbitrary Java object to 
	 * a file.
	 * @param obj The object to be serialized.
	 * @param file The file to write to.
	 * @return The full path of the written file.
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
	 * Reads an object (of known type) from a serialization file.
	 * The return value must be cast to the appropriate type, which
	 * must be known.
	 * @param fileName The file containing serialized data.
	 * @return The object reconstructed from the file representation or null if unsuccessful.
	 * @deprecated
	 */
	public static Object readObject(String fileName) {
		File file = new File(fileName);
		return readObject(file);
	}
	

	/**
	 * Reads an object (of known type) from a serialization file.
	 * The return value must be cast to the appropriate type, which
	 * must be known.
	 * @param file The file to read.
	 * @return The object reconstructed from the file representation or null if unsuccessful.
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
