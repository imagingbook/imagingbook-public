/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import imagingbook.core.FileUtils;

/**
 * <p>
 * The {@code main()} method of this class creates the {@code plugins.config} file 
 * for a given plugins project, which is to be included in the associated JAR file.
 * The execution is to be triggered during the Maven build or manually by
 * </p>
 * <pre>
 * mvn exec:java -Dexec.mainClass="imagingbook.pluginutils.PluginsConfigBuilder"</pre>
 * <p>
 * (at the root of a plugins project).
 * The format of the entries in {@code plugins.config} have the following structure:
 * </p>
 * <pre>
 * menu-level, "plugin-name", package.classname</pre>
 * for example:
 * <pre>
 * Plugins&gt;Binary&gt;Regions, "Convex Hull Demo", Binary_Regions.Convex_Hull_Demo</pre>
 * <p>Note that, technically, menu paths may be more than 2 levels deep, but this 
 * does not seem useful.
 * </p>
 * <p>
 * Plugin classes (implementing {@link PlugIn} or {@link PlugInFilter}) may be annotated
 * with {@link IjPluginPath} and {@link IjPluginName} to specify where in ImageJ's menu tree
 * and by which name the plugin should be installed.
 * This information is stored in the {@code plugins.config} file at the root of the associated project,
 * which is automatically added to the project's output JAR file during the Maven build.
 * Example:
 * </p>
 * <pre>
 * // file MySuperPlugin.java
 * import ij.plugin.filter.PlugInFilter;
 * import imagingbook.pluginutils.annotations.IjPluginName;
 * import imagingbook.pluginutils.annotations.IjPluginPath;
 * ...
 * {@literal @}IjPluginPath("Plugins&gt;Mine")
 * {@literal @}IjPluginName("Super Plugin")
 * public class MySuperPlugin implements PlugInFilter {
 * 	// plugin code ...
 * }</pre>
 * <p>
 * In this case, plugin {@code MySuperPlugin} should be installed in ImageJ's menu tree as
 * <pre> Plugins &gt; Mine &gt; Super Plugin</pre>
 * <p>
 * By default (i.e., if no annotations are present), plugins in the default package are installed at the
 * top-level of 'Plugins' whereas plugins inside a named package are  installed 
 * in 'Plugins&gt;<em>package-name</em>' (see below).
 * A {@link IjPluginPath} annotation may also be attached to a whole package
 * in the associated {@code package-info.java} file. The following
 * example specifies {@code Plugins>Binary Regions} as the default menu path
 * for all plugins in package {@code Binary_Regions}:
 * </p>
 * <pre>
 * // file Binary_Regions/package-info.java
 * {@literal @}IjPluginPath("Plugins&gt;Binary Regions")
 * package Binary_Regions;
 * import imagingbook.pluginutils.annotations.IjPluginPath;</pre>
 * <p>
 * Individual plugins may override the menu path specified for the containing package, as summarized below:
 * <p>
 * <strong>Plugin <em>path</em> priority rules summary:</strong>
 * </p>
 * <ol>
 * <li> Value of a {@code @IjPluginPath} annotation at class level (always overrules if exists).</li>
 * <li> Value of a {@code @IjPluginPath} annotation at package level (if exists).</li>
 * <li> {@link #DefaultMenuPath} + {@literal ">"} + <em>package-name</em> if the plugin is inside a named package.</li>
 * <li> {@link #DefaultMenuPath} if the plugin is in the (unnamed) default package.</li>
 * </ol>
 * <p>
 * <strong>Plugin <em>entry</em> priority rules summary:</strong>
 * </p>
 * <ol>
 * <li> Value of {@link IjPluginName} annotation at class level (if exists).</li>
 * <li> Simple name of the plugin class.</li>
 * </ol>
 * <p>
 * Note that, in general, ImageJ uses the information in file {@code plugins.config} 
 * only for plugins loaded from a JAR file!
 * </p>
 * 
 * @see IjPluginPath
 * @see IjPluginName
 * 
 * @author WB
 */
public class PluginsConfigBuilder {
	
	protected static String DefaultMenuPath = "Plugins";	// can be overridden by package or class annotation @IjPluginPath
//	static String DefaultEntryPrefix = "B&B ";
			
	protected static String ConfigFileName = "plugins.config";
	protected static String INFO = "[INFO] ";
	
	protected static boolean VERBOSE = true;
	protected static boolean ReplaceUndescoresInClassNames = true;
	protected static boolean ReplaceUndescoresInPackageNames = true;
	
	private final String artifactId;
	private final String rootPath;
	
	/**
	 * Constructor (private), only called from the main() method.
	 * @param rootName the project's root (output) directory
	 * @param artifactId the project's Maven artifact id 
	 */
	private PluginsConfigBuilder(String rootName, String artifactId) {
		this.artifactId = artifactId;		
		File rootDir = (rootName != null) ?
				new File(rootName) :
				new File(PluginsConfigBuilder.class.getClassLoader().getResource("").getFile());				
		this.rootPath = rootDir.getAbsolutePath();
	}
	
	private List<Class<?>> collectPluginClasses() {
		int n = rootPath.length();
		File rootFile = new File(rootPath);	
		if (!rootFile.exists()) {	// this happens when executed in a project with POM-packaging
			return null;
		}
		
		Path start = rootFile.toPath();
		List<Class<?>> pluginClasses = new ArrayList<>();
		
		try (final Stream<Path> allPaths = Files.walk(start)) {
			allPaths.filter(Files::isRegularFile).forEach(path -> {
				String pathName = path.toString();
				if (FileUtils.getFileExtension(pathName).equals("class")) {
					String className = FileUtils.stripFileExtension(pathName);
					// remove non-class part of filename:
					className = className.substring(n + 1);
					if (className.indexOf('-') < 0) {		// ignore 'package-info' and 'module-info'
						// convert to qualified class name:
						className = className.replace(File.separatorChar, '.');
						// find the associated class object (this should never fail):
						Class<?> clazz = null;
						try {
							clazz = Class.forName(className);
						} catch (final ClassNotFoundException e) {
							throw new RuntimeException(e.getMessage());
						}
	
						if (clazz != null && isIjPlugin(clazz)) {
							pluginClasses.add(clazz);
						}
					}
				}
			});
		} catch (IOException e) {
			//throw new RuntimeException(e.getMessage());
			System.out.println("SOMETHING BAD HAPPENED: " + e.getMessage());
		}
		return pluginClasses;
	}
	
	/**
	 * Writes plugin configuration entries to the specified stream.
	 * 
	 * @param pluginClasses a list of plugin classes
	 * @param strm the output stream (e.g., System.out)
	 */
	private void writeEntriesToStream(List<Class<?>> pluginClasses, PrintStream strm) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		strm.println("# plugins.config file for " + artifactId + " (automatically generated)");
		strm.println("# number of plugins: " + pluginClasses.size());
		strm.println("# date: " + LocalDateTime.now().format(formatter));
		
		for (Class<?> clazz : pluginClasses) {
			// configure menu path:
			Package pkg = clazz.getPackage();
			String menuPath = DefaultMenuPath;
			if (pkg != null) {
				// see if 'package-info.java' contains specifies a menu path for this package
				// TODO: warn if package nesting is deeper than 1
				IjPluginPath packageMenuPathAnn = pkg.getDeclaredAnnotation(IjPluginPath.class);
				String pkgName = (ReplaceUndescoresInPackageNames) ? 
						pkg.getName().replace('_', ' ') : pkg.getName();
				menuPath = (packageMenuPathAnn != null) ? 
						packageMenuPathAnn.value() : DefaultMenuPath + ">" + pkgName;
			}
			// see if clazz specifies a menu path for this package (overrules package specification)
			IjPluginPath classMenuPathAnn = clazz.getDeclaredAnnotation(IjPluginPath.class);
			if (classMenuPathAnn != null) {
				menuPath = classMenuPathAnn.value(); 	
			}
			
			// configure menu entry:
			IjPluginName classMenuEntryAnn = clazz.getDeclaredAnnotation(IjPluginName.class);
			String className = (ReplaceUndescoresInClassNames) ?
					clazz.getSimpleName().replace('_', ' ') : clazz.getSimpleName();
			String menuEntry = (classMenuEntryAnn != null) ? classMenuEntryAnn.value() : className;
			// build line for entry in config file:
			String configLine = String.format("%s, \"%s\", %s", menuPath, menuEntry, clazz.getCanonicalName());
			if (VERBOSE)
				System.out.println(INFO + "*** " + configLine);
			
//			strm.format("%s, \"%s\", %s\n", menuPath, menuEntry, clazz.getCanonicalName());
			strm.println(configLine);
		}
	}
	
	private String buildfile() {
		List<Class<?>> pluginClasses = collectPluginClasses();
		if  (pluginClasses == null) {
			System.out.println(INFO + "WARNING: no target directory (POM project?)");
			return null;
		}
		if  (pluginClasses.isEmpty()) {
			System.out.println(INFO + "WARNING: no plugin classes found!");
			return null;
		}
		System.out.println(INFO + "Number of plugins: " + pluginClasses.size());
//		writeEntriesToStream(pluginClasses, System.out);
		
		File configFile = new File(rootPath + "/" + ConfigFileName);
//		System.out.println("configPath = " + configFile.getAbsolutePath());
		
		try (PrintStream ps = new PrintStream(configFile)) {
			writeEntriesToStream(pluginClasses, ps);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		return configFile.getAbsolutePath();
	}
	
	/**
	 * Returns true if the specified {@link Class} object is a sub-type of
	 * {@link PlugIn} or {@link PlugInFilter}.
	 * 
	 * @param clazz a {@link Class} object
	 * @return true if a plugin type
	 */
	private boolean isIjPlugin(Class<?> clazz) {
		return PlugIn.class.isAssignableFrom(clazz) || PlugInFilter.class.isAssignableFrom(clazz);
	}
	
	// ----------------------------------------------------------------------------------------------
	
	/**
	 * <p>
	 * Method to be called from the command line. Builds the {@code plugins.config} file
	 * from the {@code .class} files found in the specified build directory and
	 * stores the file in the same directory.
	 * Takes two (optional) arguments:
	 * </p>
	 * <ol> 
	 * <li>The project's build (output) directory (where .class files reside).</li>
	 * <li> The project's Maven artifact id.</li>
	 * </ol>
	 * If no build directory is specified, the current directory is used.
	 * @param args {@code args[0]}: project build (output) directory, {@code args[1]}: project artefact id
	 */
	public static void main(String[] args) {
		String rootName   = (args.length > 0) ? args[0] : null;
		String artifactId = (args.length > 1) ? args[1] : null;
		System.out.println(INFO);
		System.out.println(INFO + "--- Building plugins.config file for " + artifactId + " ---");
		for (String arg : args) {
			System.out.println(INFO + "    arg = |" + arg + "|");
		}
		// future use to specify general plugins path from POM:
		// using property <pluginPrefix>"Plugins&gt;B&amp;B "</pluginPrefix>
		
		PluginsConfigBuilder builder = new PluginsConfigBuilder(rootName, artifactId);
		String configPath = builder.buildfile();
		
		if (configPath != null) {
			System.out.println(INFO + "Config file written: " + configPath);
		}
		else {
			System.out.println(INFO + "No config file written");
		}
	}

}
