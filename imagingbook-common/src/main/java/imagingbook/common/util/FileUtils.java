/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.jar.Manifest;

import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;


/**
 * This class defines various static methods for managing
 * file-based resources and JAR manifest files.
 * 
 * @author W. Burger
 */
public abstract class FileUtils {

	/**
	 * Removes the extension part of a pathname.
	 * Examples:<br>
	 * "foo.txt" &rarr; "foo",
	 * "foo" &rarr; "foo",
	 * "foo." &rarr; "foo.",
	 * ".txt" &rarr; ".txt".
	 * @param name the pathname
	 * @return the pathname without the extension (if valid)
	 */
	public static String stripFileExtension(String name) {
		int dotInd = name.lastIndexOf('.');
		// if dot is in the first position,
		// we are dealing with a hidden file rather than an DefaultFileExtension
		return (dotInd > 0) ? name.substring(0, dotInd) : name;
	}
	
	/**
	 * Extracts the extension part of a pathname as a string.
	 * Examples:<br>
	 * "foo.txt" &rarr; "txt",
	 * "foo" &rarr; "",
	 * "foo." &rarr; "",
	 * ".txt" &rarr; "".
	 * @param name the pathname
	 * @return the extension or an empty string
	 */
	public static String getFileExtension(String name) {
		int dotInd = name.lastIndexOf('.');
		return (dotInd > 0) ? name.substring(dotInd + 1) : "";
	}
	
	// ----  resources-related stuff ----------------------------------
	
	/**
	 * Find the path from which a given class was loaded.
	 * @param clazz a class.
	 * @return the path of the .class file for the given class or null (e.g.
	 * if the class is anonymous).
	 */
	public static String getClassPath(Class<?> clazz) {
		String path = null;
		try {
			URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
			if (uri != null && !uri.getPath().isEmpty()) {
				path = new File(uri).getCanonicalPath();
			}
		} catch (URISyntaxException | IOException e) { }
		return path;
	}
	
//	public static String getClassPath(Class<?> clazz) {
//		return clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
//		//return clazz.getProtectionDomain().getCodeSource().getLocation().toString();
//	}
	
	// ----------------------------------------------------------------

	/**
	 * Lists (to System.out) the paths where classes are loaded from.
	 */
	public static void printClassPath() {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL[] urls = ((URLClassLoader) cl).getURLs();
		for (URL url : urls) {
			System.out.println(url.getPath());
		}
	}
	
	// ----------------------------------------------------------------
	
	/**
	 * Checks 'by name' if a particular class exists.
	 * 
	 * @param classname fully qualified name of the class, e.g. {@literal imagingbook.lib.util.FileUtils}
	 * @return {@code true} if the class was found, {@code false} otherwise
	 */
	public static boolean checkClass(String classname) {
		// String logStr = "  checking class " + classname + " ... ";
		try {
			if (Class.forName(classname) != null) {
				// IJ.log(logStr + "OK");
				return true;
			}
		} catch (ClassNotFoundException e) { }
		// IJ.log(logStr + "ERROR");
		return false;
	}
	
	
	// ----------------------------------------------------------------
	
	// from https://bukkit.org/threads/extracting-file-from-jar.16962/
	/**
	 * Reads all data from the given input stream and copies them
	 * to to a file.
	 * @param in the input stream
	 * @param file the output file
	 * @throws IOException if anything goes wrong
	 */
	public static void copyToFile(InputStream in, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = in.read(buf)) != -1) {
				out.write(buf, 0, i);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
	
	
	
	// ----------------------------------------------------------------
	
	/**
	 * Finds the manifest (from META-INF/MANIFEST.MF) of the JAR file
	 * from which {@literal clazz} was loaded.
	 * 
	 * See: http://stackoverflow.com/a/1273432
	 * @param clazz A class in the JAR file of interest.
	 * @return A {@link Manifest} object or {@literal null} if {@literal clazz}
	 * was not loaded from a JAR file.
	 */
	public static Manifest getJarManifest(Class<?> clazz) {
		String className = clazz.getSimpleName() + ".class";		
		String classPath = clazz.getResource(className).toString();
		//IJ.log("classPath = " + classPath);
		if (!classPath.startsWith("jar")) { // Class not from JAR
		  return null;
		}
		String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
		Manifest manifest = null;
		try {
			manifest = new Manifest(new URL(manifestPath).openStream());
		} catch (IOException ignore) { }
		return manifest;
	}
	
	// ----------------------------------------------------------------
	
	
	/**
	 * Opens a dialog for the user to select a single folder (no files).
	 * Contained files and sub-folders are shown.
	 * Uses native (system) look-and-feel; original look-and-feel is restored.
	 * 
	 * @param startDirectory the directory to start from (pass {@code ""} or {@code "."} for the current directory)
	 * @param dialogTitle the string shown in the title bar of the dialog window
	 * @return a {@link File} object representing the selected directory or {@code null} if the dialog was canceled 
	 */
	public static File selectFolder(String startDirectory, String dialogTitle) {
		File startDir = new File(startDirectory);
		if (!startDir.exists()) {
			startDir = new File(".");
		}
		
		JFileChooser chooser = null;
		LookAndFeel laf = UIManager.getLookAndFeel();	// get current look-and-feel

		try { 
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // use native look and feel
			chooser = new JFileChooser(startDir) {
				private static final long serialVersionUID = 1L;
				public void approveSelection() {
					if (getSelectedFile().isFile()) {
						return;
					} else {
						super.approveSelection();
					}
				} 
			};
			UIManager.setLookAndFeel(laf);	// reset look-and-feel
		} 
		catch (Exception e) { }
		
		if (chooser == null)
			return null;
		
		chooser.setDialogTitle(dialogTitle);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setAcceptAllFileFilterUsed(true);
		chooser.setMultiSelectionEnabled(false);
		chooser.setSelectedFile(new File("."));	// see https://stackoverflow.com/a/48316113

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selected = chooser.getSelectedFile();
			if (selected == null)
				throw new RuntimeException("selected directory is null");
			return selected;
		}
		else {	// dialog was canceled
			return null;
		}
	}
	
	// -----------------------------------------------------------------
	
	private static final String DirectoryKey = "imagingbook#dir";
	private static String DefaultUserDirectory = System.getProperty("user.dir");
	
	/**
	 * Sets a system property to memorize the current directory.
	 * Use {@link #getCurrentDirectory()} to retrieve this value.
	 * If the specified pathname is not a directory (i.e., a plain file), 
	 * its parent directory is used.
	 * 
	 * @param pathname a directory of file path
	 */
	@Deprecated  // use setCurrentDirectory(Class, String) instead
	public static void setCurrentDirectory(String pathname) {
		File file = new File(pathname);
		String dir = (file.isDirectory()) ? file.toString() : file.getParent();
		System.setProperty(DirectoryKey, dir);
	}
	
	/**
	 * Associates a current directory with the specified class by
	 * setting a system property to make this persistent through class reloads.
	 * Use {@link #getCurrentDirectory(Class)} to retrieve this value.
	 * If the specified pathname is not a directory (i.e., a plain file), 
	 * its parent directory is used.
	 * 
	 * @param clazz the class to associate the directory with
	 * @param pathname a directory of file path
	 */
	public static void setCurrentDirectory(Class<?> clazz, String pathname) {
		File file = new File(pathname);
		String dir = (file.isDirectory()) ? file.toString() : file.getParent();
		System.setProperty(makeDirectoryKey(clazz), dir);
	}
	
	public static void setCurrentDirectory(Class<?> clazz, Path path) {
		File file = path.toFile();
		String dir = (file.isDirectory()) ? file.toString() : file.getParent();
		System.setProperty(makeDirectoryKey(clazz), dir);
	}
	
	/**
	 * Returns the current directory path. 
	 * If the directory was set with {@link #setCurrentDirectory(String)} before
	 * this path is returned,
	 * otherwise the value for the "user.dir" system property.
	 * 
	 * @return a string with the current directory path
	 */
	@Deprecated  // use getCurrentDirectory(Class) instead
	public static String getCurrentDirectory() {
		String dir = System.getProperty(DirectoryKey);
		return (dir != null) ? dir : DefaultUserDirectory;
	}
	
	
	/**
	 * Returns the current directory associated with the specified class. 
	 * (usually of type {@code PlugIn} or {@code PlugInFilter}).
	 * If the directory was set with {@link #setCurrentDirectory(Class,String)} before
	 * this path is returned, {@code null} otherwise.
	 * 
	 * @param clazz the class to associate the directory with
	 * @return a string with the current directory path or null if not registered
	 */
	public static String getCurrentDirectory(Class<?> clazz) {
		return System.getProperty(makeDirectoryKey(clazz));
	}
	
	private static String makeDirectoryKey(Class<?> clazz) {
		return DirectoryKey + "#" + clazz.getCanonicalName();
	}
	
	// -----------------------------------------------------------------
			
	public static void main(String[] args) {
//		String fileName = ".txt";
//		System.out.println("name = " + fileName);
//		System.out.println("stripped = " + stripFileExtension(fileName));
//		System.out.println("ext = " + getFileExtension(fileName));
//		System.out.println(getClassPath(FileUtils.class));
		
		System.out.println("dir = " + getCurrentDirectory());
		setCurrentDirectory("C:/tmp");
		System.out.println("dir = " + getCurrentDirectory());
		setCurrentDirectory("D:/tmp");
		System.out.println("dir = " + getCurrentDirectory());
	}
	

}
