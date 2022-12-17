/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.core.plugin;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
//import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to specify the menu path (tree branch) of the associated plugin class
 * or an entire plugin package. Takes a {@link String} argument e.g.,
 * <pre>
 * {@literal @}IjMenuPath("Plugins&gt;My Stuff")
 * {@literal @}IjMenuEntry("My Plugin")
 * public class My_Plugin implements PlugInFilter {
 *   ...
 * }</pre>
 * May also be used on the {@code package} declaration 
 * in file {@code package-info.java} to set the default menu
 * path for all plugins directly contained in that package (but not nested), e.g.,
 * <pre>
 * {@literal @}IjMenuPath("Plugins&gt;Binary Regions")
 * package Binary_Regions;</pre>
 * Annotations for individual plugin classes override the package-level setting.
 */
@Retention(RUNTIME)
@Target({ TYPE, PACKAGE })
public @interface IjPluginPath {
	public String value();
}
