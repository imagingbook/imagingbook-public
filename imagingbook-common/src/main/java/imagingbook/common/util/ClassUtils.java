package imagingbook.common.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class ClassUtils {
	
	/**
	 * Lists all accessible public fields of the given object and 
	 * returns the result as a string.
	 * @param obj a (non-null) object 
	 * @return a string listing the names and values of the object's fields
	 */
	public static String listFields(Object obj) {
		Class<?> clz = obj.getClass();
		StringBuilder buf = new StringBuilder(clz.getName() + ":\n");
		Field[] fields = clz.getFields();
		for (Field f : fields) {
			String name = f.getName();
			String value = null;
			try {
				value = f.get(obj).toString();
			} catch (IllegalArgumentException | IllegalAccessException e1) {			}
			buf.append(name + ": " + value + "\n");
		}
		return buf.toString();
	}
	
	
	
	/**
	 * Collects and returns the list of classes defined in the specified package.
	 * The {@link Package} may be obtained from an existing {@link Class} object ({@code clazz}) by
	 * {@code clazz.getPackage()}.
	 * 
	 * @param pkg a {@link Package} instance, e.g., {@code Package.getPackage("imagingbook.lib.ij.overlay")}
	 * @return a list of classes contained in the package
	 */
	public static List<Class<?>> getClassesInPackage(Package pkg) {
		return getClassesInPackage(pkg.getName());
	}

	/**
	 * Collects and returns the list of classes defined in the specified package.
	 * Adapted from https://stackoverflow.com/a/58773271 
	 * 
	 * @param pkgName the full package name, e.g., "imagingbook.lib.ij.overlay"
	 * @return a list of classes contained in the package
	 */
	public static List<Class<?>> getClassesInPackage(final String pkgName) {
		System.out.println("pkgName = " + pkgName);
		final String pkgPath = pkgName.replace('.', '/');
		System.out.println("pkgPath = " + pkgPath);
		
		URL pkgURL = ClassLoader.getSystemClassLoader().getResource(pkgPath);
		System.out.println("URL = " + pkgURL);
		
		URI pkgURI = null;
		try {
			pkgURI = Objects.requireNonNull(pkgURL.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage());
		}
		System.out.println("URI = " + pkgURI);
	
		Path root = null;
		if (pkgURI.toString().startsWith("jar:")) {
			root = FileSystems.getFileSystem(pkgURI).getPath(pkgPath);
			if (root == null) {
				try {
					root = FileSystems.newFileSystem(pkgURI, Collections.emptyMap()).getPath(pkgPath);
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage());
				}
			}
		} else {
			root = Paths.get(pkgURI);
		}
	
		final ArrayList<Class<?>> allClasses = new ArrayList<Class<?>>();
		try (final Stream<Path> allPaths = Files.walk(root)) {
			allPaths.filter(Files::isRegularFile).forEach(path -> {
				String pathName = path.toString();
				//System.out.println("pathName = " + pathName);
				if (FileUtils.getFileExtension(pathName).equals("class")) {
					String className = FileUtils.stripFileExtension(pathName);
					System.out.println("className = " + className);
					className = className.replace(File.separatorChar, '.');
					className = className.substring(className.indexOf(pkgName));
					try {           
						allClasses.add(Class.forName(className));
					} catch (final ClassNotFoundException | StringIndexOutOfBoundsException ignored) {
					}
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return allClasses;
	}

}
