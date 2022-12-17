/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.core.resource;

import java.io.InputStream;
import java.net.URL;

/**
 * <p>
 * This interface is supposed to be implemented by some enum class that specifies the root of a resource tree. The
 * following example defines such an enum type that refers to three images. The path relative to the location of the
 * enum class itself must be specified and images must be stored there. The enum type must define an appropriate
 * constructor and implement method {@link #getFileName()}:
 * </p>
 * <pre>
 * public enum MyImages implements NamedResource {
 * 	image1("image1.tif"),
 * 	image2("image2.png"),
 * 	image3("jpegs/image3.jpg");
 * 	// field to associate the resource's file name
 * 	private final String filename;
 * 	&#64;Override
 * 	public String getFileName() {
 * 		return filename;
 *    }
 * 	// constructor
 * 	MyImages(String filename) {
 * 		this.filename = filename;
 *    }
 * }</pre>
 * <p>
 * By default, resource files are assumed to be placed in a folder with the same name as the resource class (i.e.,
 * "MyImages") relative to the location of the class. Resources may then be simply referenced by their enum-name, for
 * example,
 * </p>
 * <pre>
 * URL url = MyImages.image1.getURL();
 * ImagePlus im = IJ.openImage(url.toString());
 * </pre>
 * <p>
 * The benefit of using a named resource (over specifying resources by path strings) is that the resource is guaranteed
 * to exist at compile time (this is assured by validating the existence of all named resources in the test phase) and
 * can be retrieved even if located inside a JAR file. Also, everything is defined in a single place and renaming a
 * resource is easy.
 * </p>
 *
 * @author WB
 * @version 2022/08/23
 * @see ImageResource
 */
public interface NamedResource {

	/**
	 * Specifies the name of the resource directory relative to some resource class. For example, given some resource
	 * class in
	 * <pre>src/main/java/.../foo/ZeClass.java</pre>
	 * the associated resource files are assumed to be in directory
	 * <pre>src/main/resources/.../foo/ZeClass-data/</pre>
	 */
	public static final String RelativeDirectorySuffix = "-data";

	/**
	 * Returns the resource directory relative to the implementing class. The default implementations assumes that this
	 * is a single-level directory with exactly the same name as the implementing class, extended by
	 * {@link #RelativeDirectorySuffix} ("-data"). Implementations may override this definition to return some other
	 * relative directory. Note that the directory should not have a legal Java package name to avoid confusion in the
	 * build process (e.g., by javadoc).
	 *
	 * @return the relative resource directory for the associated resource
	 */
	public default String getRelativeDirectory() {
		// return getClass().getSimpleName() + RelativeDirectorySuffix;
		return getRelativeDirectory(this.getClass());
	}

	public static String getRelativeDirectory(Class<? extends NamedResource> clazz) {
		return clazz.getSimpleName() + RelativeDirectorySuffix;
	}

	/**
	 * Returns the path to the associated resource relative to the location of the implementing class. This method is
	 * not supposed to be overridden.
	 *
	 * @return the relative path to the associated resource
	 */
	public default String getRelativePath() {
		return getRelativeDirectory() + "/" + getFileName();
	}

	/**
	 * Returns the file name for the associated resource (to be implemented by concrete classes).
	 *
	 * @return the name of the resource file
	 */
	public String getFileName();

	/**
	 * Returns the URL to the associated resource. This method is not supposed to be overridden. Throws an exception if
	 * the requested resource does not exist.
	 *
	 * @return the URL to the associated resource
	 */
	public default URL getURL() {
		Class<?> clazz = this.getClass();
		String relPath = this.getRelativePath();
		URL url = clazz.getResource(relPath);
		if (url == null) {
			throw new RuntimeException("could not find resource " +
					clazz.getResource("") + relPath);
		}
		return url;
	}
	
	/**
	 * Returns true if the associated resource (class) was loaded from a JAR file.
	 * 
	 * @return true if inside a JAR file
	 */
	public default boolean isInsideJar() {
		return ResourceUtils.isInsideJar(getClass());
//		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
//		String path = url.getPath();
//		File file = new File(path);
//		return file.isFile();
	}

	/**
	 * Returns an {@link InputStream} for reading from this resource. See also
	 * {@link Class#getResourceAsStream(String)}. This method is not supposed to be overridden.
	 *
	 * @return an {@link InputStream} for the associated resource
	 */
	public default InputStream getStream() {
		return getClass().getResourceAsStream(getRelativePath());
	}

	/**
	 * Returns the names of the actual files contained in the associated resource directory of the specified class,
	 * which must implement the {@link NamedResource} interface. This can be used to check if a given named resource has
	 * a matching file in a case-sensitive way.
	 *
	 * @param clazz the resource class
	 * @return an array of strings
	 */
	public static String[] getResourceFileNames(Class<? extends NamedResource> clazz) {
		// return ResourceUtils.getResourceFileNames(clazz, clazz.getSimpleName() + RelativeDirectorySuffix);
		return ResourceUtils.getResourceFileNames(clazz, getRelativeDirectory(clazz));
	}

}
