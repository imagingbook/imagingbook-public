/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.core.jdoc;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.MODULE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This module or package-level annotation is used to specify a base URL for automatic retrieval of JavaDoc
 * information. The URL is given WITH the module's name, e.g., for module {@code imagingbook_plugins_book} this is<br>
 * {@literal https://imagingbook.github.io/imagingbook-public/javadoc/imagingbook_plugins_book}<br>
 * This information is used by {@link JavaDocUtils#getJavaDocUrl(Class)}.
 *
 * @see JavaDocUtils#JAVADOC_BASE_URL
 */
@Retention(RUNTIME)
@Target({MODULE, PACKAGE})
public @interface JavaDocBaseUrl {
    public String value();
}
