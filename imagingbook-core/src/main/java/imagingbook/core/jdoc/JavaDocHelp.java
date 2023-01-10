/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.core.jdoc;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * Classes implementing this interface provide a URL that links to the associated JavaDoc page (returned by method
 * {@link #getJavaDocUrl()}). This is mainly intended as help information in user dialogs of ImageJ plugins.
 */
public interface JavaDocHelp {

    /**
     * Returns a JavaDoc URL for this object's class.
     * @return a JavaDoc URL
     */
    public default String getJavaDocUrl() {
        String url = getJavaDocUrl(this.getClass());
        if (Objects.isNull(url)) {
            throw new RuntimeException("no JavaDoc URL available");
        }
        return url;
    }

    /**
     * Returns the JavaDoc URL for the specified class, which is obtained from the value of {@link JavaDocBaseUrl}
     * annotations present at the PACKAGE level (in file {@code package-info.java}). Note that there is no guarantee
     * that the associated JavaDoc exists at the resulting address (this is not checked by unit tests).
     *
     * @param clazz a class reference
     * @return a string with the web URL for the JavaDoc information of the specified class
     */
    public static String getJavaDocUrl(Class<?> clazz) {
        // Module mod = clazz.getModule();
        Package pkg = clazz.getPackage();
        String baseUrl = null;

        if (pkg != null && pkg.isAnnotationPresent(JavaDocBaseUrl.class)) {
            baseUrl = pkg.getAnnotation(JavaDocBaseUrl.class).value();
            // baseUrl is assumed to end with '/'
            if (!baseUrl.endsWith("/")) {
                throw new RuntimeException("JavaDocBaseUrl value must end with '/': " + baseUrl);
            }
        }

        if (baseUrl == null) {
            return null;
        }
        else {
            String classCName = clazz.getCanonicalName();
            String url = baseUrl + classCName.replace('.', '/') + ".html";
            // valid URL is checked during testing!
            return url;
        }
    }

    /**
     * Checks if the specified string is a valid URL. Does the standard (non-perfect) URL/URI check followed by some
     * additional checks that could indicate likely definition errors.
     *
     * @param url the  string to be checked
     * @return true iff a valid URL
     */
    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            return false;
        }
        // do additional checks on url:
        {   // check for multiple "//"s
            int k1 = url.indexOf("//"); // there must be at least one "//"
            if (k1 < 0)
                return false;
            int k2 = url.indexOf("//", k1 + 2);
            if (k2 > 0)                 // there must not be another "//"
                return false;
            if (url.contains(".."))
                return false;
        }
        return true;
    }

    // public static void main(String[] args) {
    //     String[] urls = {
    //             "https://www.geeksforgeeks.org/",
    //             "https://www.geeksforgeeks..org/foo/3",
    //             "http://www.geeksforgeeks.org//check-if-url-is-valid-or-not-in-java/",
    //             "http//foo",
    //             "imagingbook.com"
    //     };
    //     for (String url : urls) {
    //         boolean passed = isValidURL(url);
    //         System.out.println(passed + " | " + url);
    //     }
    // }

    // true | https://www.geeksforgeeks.org/
    // false | https://www.geeksforgeeks..org/foo/3
    // false | http://www.geeksforgeeks.org//check-if-url-is-valid-or-not-in-java/
    // false | http//foo
    // false | imagingbook.com

}
