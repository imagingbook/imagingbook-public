/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.core.resource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;


/**
 * <p>
 * This class defines static methods for accessing resources. What makes things
 * somewhat complex is the requirement that we want to retrieve resources
 * located in the file system or contained inside a JAR file.
 * </p>
 * <p>
 * Here is a typical URI for a JAR-embedded file:
 * {@code "jar:file:/C:/PROJEC~2/parent/IM1D84~1/ImageJ/jars/jarWithResources.jar!/jarWithResouces/resources/clown.jpg"}
 * </p>
 *
 * @author WB
 * @version 2022/11/01
 */
public class ResourceUtils {
	
	/**
	 * Determines if the specified class was loaded from
	 * a JAR file or a .class file in the file system.
	 * 
	 * @param clazz the class
	 * @return true if contained in a JAR file, false otherwise
	 */
	public static boolean isInsideJar(Class<?> clazz) {
		URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
		String path = url.getPath();
		File file = new File(path);
		return file.isFile();
	}
	
	/**
	 * Finds the URI for a resource relative to a specified class.
	 * The resource may be located in the file system or
	 * inside a JAR file.
	 * 
	 * @param clazz the anchor class
	 * @param relPath the resource path relative to the anchor class
	 * @return the URI or {@code null} if the resource was not found
	 */
	public static URI getResourceUri(Class<?> clazz, String  relPath) {
		URI uri = null;
		if (isInsideJar(clazz)) {
			String classPath = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
			//String packagePath = clazz.getPackage().getName().replace('.', File.separatorChar);
			String packagePath = clazz.getPackage().getName().replace('.', '/');
			String compPath = "jar:file:" + classPath + "!/" + packagePath + "/" + relPath;
			try {
				uri = new URI(compPath);
			} catch (URISyntaxException e) {
				// throw new RuntimeException("getResourceURI: " + e.toString());
			}	
		}
		else {	// regular file path
			try {
				uri = clazz.getResource(relPath).toURI();
			} catch (Exception e) {
				//do nothing, just return null - was: throw new RuntimeException("getResourceURI: " + e.toString());
			}
		}
		return uri;
	}
	
	/**
	 * <p>
	 * Finds the path to a resource relative to the location of some class.
	 * Example: Assume class C was loaded from file {@code someLocation/C.class}
	 * and there is a subfolder {@code someLocation/resources/} that contains 
	 * an image file {@code lenna.jpg}. Then the complete path to this image
	 * is obtained by 
	 * </p>
	 * <pre>
	 * Path path = getResourcePath(C.class, "resources/lenna.jpg");
	 * </pre>
	 * 
	 * @param clazz anchor class 
	 * @param relPath the path of the resource to be found (relative to the location of the anchor class)
	 * @return the path to the specified resource
	 * @version 2016/06/03: modified to return proper path to resource inside a JAR file.
	 */
	public static Path getResourcePath(Class<?> clazz, String relPath) {
		URI uri = getResourceUri(clazz, relPath);
		if (uri != null) {
			return uriToPath(uri);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Converts an {@link URI} to a {@link Path} for locations that are either in
	 * the file system or inside a JAR file.
	 * 
	 * @param uri the specified location
	 * @return the associated path
	 */
	public static Path uriToPath(URI uri) {
		Path path = null;
		String scheme = uri.getScheme();
		switch (scheme) {
		case "jar":	{	// resource inside JAR file
			FileSystem fs = null;
			try { // check if this FileSystem already exists 
				fs = FileSystems.getFileSystem(uri);
			} catch (FileSystemNotFoundException e) {
				// that's OK to happen, the file system is not created automatically
			}
			
			if (fs == null) {	// must not create the file system twice
				try {
					fs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
				} catch (IOException e) {
					throw new RuntimeException("uriToPath: " + e.toString());
				}
			}
			
			String ssp = uri.getSchemeSpecificPart();
			int startIdx = ssp.lastIndexOf('!');
			String inJarPath = ssp.substring(startIdx + 1);  // in-Jar path (after the last '!')
			path = fs.getPath(inJarPath);
			break;
		}
		case "file": {	// resource in ordinary file system
			path = Paths.get(uri);
			break;
		}
		default:
			throw new IllegalArgumentException("Cannot handle this URI type: " + scheme);
		}
		return path;
	}
	
	public static Path[] getResourcePaths(URI uri) {
		return getResourcePaths(uriToPath(uri));
	}
	
	/**
	 * Method to obtain the paths to all files in a directory specified by a {@link Path}
	 * (non-recursively). This should work in an ordinary file system as well as a
	 * (possibly nested) JAR file.
	 * 
	 * @param path {@link Path} to a directory (may be contained in a JAR file)
	 * @return a possibly empty sequence of paths
	 */
	public static Path[] getResourcePaths(Path path) {
		// with help from http://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file, #10
		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("path is not a directory: " + path.toString());
		}
		
		List<Path> pathList = new ArrayList<Path>();
		Stream<Path> walk = null;
		try {
			walk = Files.walk(path, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Iterator<Path> it = walk.iterator(); it.hasNext();){
			Path p = it.next();
			if (Files.isRegularFile(p) && Files.isReadable(p)) {
				pathList.add(p);
			}
		}
		walk.close();
		return pathList.toArray(new Path[0]);
	}
	
	/**
	 * Use this method to obtain the paths to all files in a directory located
	 * relative to the specified class (non-recursively). This should work in an
	 * ordinary file system as well as a (possibly nested) JAR file.
	 * 
	 * @param clazz class whose source location defines the root
	 * @param relPath path relative to the root
	 * @return a possibly empty array of paths
	 */
	public static Path[] getResourcePaths(Class<?> clazz, String relPath) {
		return getResourcePaths(getResourceUri(clazz, relPath));
	}
	
	/**
	 * Use this method to obtain the names of all files in a directory located
	 * relative to the specified class (non-recursively). This should work in an
	 * ordinary file system as well as a (possibly nested) JAR file.
	 * 
	 * @param clazz  class whose source location specifies the root
	 * @param relDir directory relative to the root
	 * @return a possibly empty array of file names
	 */
	public static String[] getResourceFileNames(Class<?> clazz, String relDir) {
		Path[] paths = ResourceUtils.getResourcePaths(clazz, relDir);
		List<String> names = new ArrayList<>();
		for (Path p : paths) {
			names.add(p.getFileName().toString());
		}
		return names.toArray(new String[0]);
	}

}
