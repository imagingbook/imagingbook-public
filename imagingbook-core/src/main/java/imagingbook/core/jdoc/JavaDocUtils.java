/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.core.jdoc;

public abstract class JavaDocUtils {

    public static final String JAVADOC_BASE_URL = "https://imagingbook.github.io/imagingbook-public/javadoc/";

    /**
     * Returns the JavaDoc URL for the specified class. By default the base URL is {@link #JAVADOC_BASE_URL}, which is
     * overridden by the value of a {@link JavaDocBaseUrl} annotation present at the module level (in file
     * {@code module-info.java}). Of course there is no guarantee that the associated JavaDoc exists at the returned
     * address.
     *
     * @param clazz a class reference
     * @return a string with the web URL for the JavaDoc information of the specified class
     */
    public static String getJavaDocUrl(Class<?> clazz) {
        Module module = clazz.getModule();
        Package pkg = clazz.getPackage();

        String baseUrl = null;

        if (module.isAnnotationPresent(JavaDocBaseUrl.class)) {
            baseUrl = module.getAnnotation(JavaDocBaseUrl.class).value();
        }
        else if (pkg != null && pkg.isAnnotationPresent(JavaDocBaseUrl.class)) {
            baseUrl = pkg.getAnnotation(JavaDocBaseUrl.class).value();
        }

        if (baseUrl != null) {
            String classCName = clazz.getCanonicalName();
            return baseUrl + "/" + classCName.replace('.', '/') + ".html";
        }
        else {
            return null;
        }
    }

}
