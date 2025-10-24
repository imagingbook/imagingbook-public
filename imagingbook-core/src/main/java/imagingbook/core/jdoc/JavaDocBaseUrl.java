/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.core.jdoc;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * This package-level annotation is used to specify a URL for automatic retrieval of JavaDoc information. The the
 * supplied URL-string must include all address parts including the module part and must end with '/'.
 * </p>
 * <p>
 * For example, for class '{@literal Compute_Histogram}' in package '{@literal Ch02_Histograms_Statistics}' and module
 * '{@literal imagingbook_plugins_book}' the associated JavaDoc page is
 * </p>
 * <pre>
 * https://imagingbook.github.io/imagingbook-public/javadoc/imagingbook_plugins_book/Ch02_Histograms_Statistics/Compute_Histogram.html</pre>
 * i.e., in the format
 * <pre>
 *     https://BASEURL/MODULENAME/PACKAGENAME/CLASSNAME.htlm</pre>
 * In this case, the URL-string to be supplied is
 * <pre>
 *     https://BASEURL/MODULENAME/</pre>
 * that is,
 * <pre>
 *     https://imagingbook.github.io/imagingbook-public/javadoc/imagingbook_plugins_book/</pre>
 * <p>
 * The remaining parts (PACKAGENAME, CLASSNAME) are filled in automatically.
 * </p>
 * <p>
 * This information is used by {@link JavaDocHelp#getJavaDocUrl(Class)}. Future version may include annotations at the
 * MODULE and TYPE (class) level. Note that currently ImageJ does not fully support modules and cannot read the names
 * and annotations of external modules.
 * </p>
 *
 * @see JavaDocHelp
 */
@Retention(RUNTIME)
@Target({PACKAGE})
public @interface JavaDocBaseUrl {
    public String value();
}
