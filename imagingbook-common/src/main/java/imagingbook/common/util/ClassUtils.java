/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

import imagingbook.core.FileUtils;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 
 * @author WB
 * @version 2022/11/20
 *
 */
public abstract class ClassUtils {
	
	private ClassUtils() {}
	
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

	/**
	 * Returns an array of all constants defined by the specified enum class, sorted by their names.
	 *
	 * @param enumClass the enum class
	 * @return an array of enum items sorted by names
	 * @param <E> generic type
	 */
	public static <E extends Enum<E>> E[] getEnumConstantsSorted(Class<E> enumClass) {
		E[] items = enumClass.getEnumConstants();
		// compare by constant names (case sensitive):
		Comparator<E> cpr = new Comparator<>() {
			public int compare(E e1, E e2) {
				return e1.name().compareTo(e2.name());
			}
		};
		Arrays.sort(items, cpr);
		return items;
	}

	/**
	 * Returns the "natural order" {@link Comparator} instance for the specified {@link Comparable} class. Use
	 * {@link Comparator#reversed()} to obtain the comparator for sorting in reverse order.
	 *
	 * @param clazz a class implementing {@link Comparable}
	 * @param <T> the generic type
	 * @return the associated {@link Comparator} instance
	 */
	public static <T extends Comparable> Comparator<T> getComparator(Class<T> clazz) {
		return Comparator.naturalOrder();
	}
}
