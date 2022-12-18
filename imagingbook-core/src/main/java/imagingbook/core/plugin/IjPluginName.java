/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.core.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Annotation to specify the menu entry (name) of the associated plugin class.
 * Takes a {@link String} argument e.g.,
 * <pre>
 *  {@literal @}IjMenuEntry("My Grand Plugin")
 * public class My_Plugin implements PlugIn {
 *   ...
 * }</pre>
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface IjPluginName {
	public String value();
}
