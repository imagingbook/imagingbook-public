package imagingbook.core.resource;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * <p>
 * This interface is supposed to be implemented by some enum class that
 * specifies the root of a resource tree. The following example defines
 * such an enum type that refers to three images. The path relative 
 * to the location of the enum class itself must be specified
 * and images must be stored there.
 * The enum type must define an appropriate constructor and implement
 * method {@link #getRelativePath()}:
 * </p>

 * <pre>
 * public enum MyImages implements NamedResource {
 * 	image1("image1.tif"),
 *	image2("image2.png"),
 * 	image3("jpegs/image3.jpg");
 * 
 * 	// field to associate relative resource path
 * 	private final String relPath;
 * 
 * 	&#64;Override
 * 	public String getRelativePath() {
 * 		return relPath;
 * 	}
 * 
 * 	// constructor
 * 	MyImages(String relPath) {
 * 		this.relPath = relPath;
 * 	}
 * }</pre>
 * <p>
 * Resources are then only referenced by their enum-name, for example,
 * </p>
 * <pre>
 * URI uri = MyImages.image1.getURI();
 * ImagePlus im = IJ.openImage(uri.toString());
 * </pre>
 * <p>
 * The benefit of using a named resource (over specifying resources by path strings) 
 * is that the resource is guaranteed to exist at compile time (this is assured
 * by validating the existence of all named resources in the test phase) and can be retrieved
 * even if located inside a JAR file.
 *
 * </p>
 * @author WB
 * @version 2022/04/12
 */
public interface NamedResource {
	
	public String getRelativePath();
	
	public default URL getURL() {
		Class<?> clazz = this.getClass();
		return clazz.getResource(this.getRelativePath());
	}
	
//	@Deprecated
//	public default URI getURI() {
//		try {
//			return getURL().toURI();
//		} catch (URISyntaxException e) {
//			return null;
//		}
//	}
	
//	/**
//	 * Returns the {@link Path} to the associated resource object.
//	 * @return the resource's {@link Path} 
//	 */
//	@Deprecated
//	public default Path getPath() {
//		//return toPath(this.getURI());
//		return Paths.get(getURI());
//	}
	
//	public default URI getURI() {
//		Class<?> clazz = this.getClass();
//		String resourceName = getRelativePath();
//		Objects.requireNonNull(resourceName);
//		URI uri = null;
//		if (this.isInsideJar()) {	// (classInsideJar(clazz))
//			String classPath = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
//			//String packagePath = clazz.getPackage().getName().replace('.', File.separatorChar);
//			String packagePath = clazz.getPackage().getName().replace('.', '/');
//			String compPath = "jar:file:" + classPath + "!/" + packagePath + "/" + resourceName;
//			try {
//				uri = new URI(compPath);
//			} catch (URISyntaxException e) {
//				throw new RuntimeException(e.toString());
//			}	
//		}
//		else {	// regular file path
//			try {
//				URL url = clazz.getResource(resourceName);
//				if (url != null) {
//					uri = url.toURI();
//				}
//			} catch (URISyntaxException e) {
//				//do nothing, just return null
//			}
//		}
//		return uri;
//	}
	
//	/**
//	 * Converts an {@link URI} to a {@link Path} for objects that are either
//	 * in the file system or inside a JAR file.
//	 * 
//	 * @param uri the specified {@link URI}
//	 * @return the associated {@link Path}
//	 */
//	public static Path toPath(URI uri) {
//		Path path = null;
//		String scheme = uri.getScheme();
//		switch (scheme) {
//		case "jar":	{	// resource inside JAR file
//			FileSystem fs = null;
//			try { // check if this FileSystem already exists 
//				fs = FileSystems.getFileSystem(uri);
//			} catch (FileSystemNotFoundException e) {
//				// that's OK to happen, the file system is not created automatically
//			}
//			
//			if (fs == null) {	// must not create the file system twice
//				try {
//					Map<String, Object> map = Collections.emptyMap();
//					fs = FileSystems.newFileSystem(uri, map);	// FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
//				} catch (IOException e) {
//					throw new RuntimeException(e.toString());
//				}
//			}
//			
//			String ssp = uri.getSchemeSpecificPart();
//			int startIdx = ssp.lastIndexOf('!');
//			String inJarPath = ssp.substring(startIdx + 1);  // in-Jar path (after the last '!')
//			path = fs.getPath(inJarPath);
//			break;
//		}
//		case "file": {	// resource in ordinary file system
//			path = Paths.get(uri);
//			break;
//		}
//		default:
//			throw new IllegalArgumentException("Cannot handle this URI type: " + scheme);
//		}
//		return path;
//	}
	
	public default boolean isInsideJar() {
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		String path = url.getPath();
		File file = new File(path);
		return file.isFile();
	}

//	static boolean classInsideJar(Class<?> clazz) {
//	URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
//	String path = url.getPath();
//	File file = new File(path);
//	return file.isFile();
//}
	
	/**
	 * Returns an {@link InputStream} for reading from this resource.
	 * See also {@link Class#getResourceAsStream(String)}.
	 * @return an {@link InputStream} for this resource
	 */
	public default InputStream getStream() {
		return getClass().getResourceAsStream(getRelativePath());
	}

}
