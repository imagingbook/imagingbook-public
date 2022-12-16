# imagingbook-common

This is the main `imagingbook` library with implementions of image processing algorithms,
associated data structures and utility code. 
The main goal of this library is to provide well-documented and reusable implementations of
common image processing algorithms.

## Main Features and Elements

* Extensive set of image processing methods, including
    * image statistics and histograms,
    * linear and non-linear filters,
    * automatic thresholding,
    * morphological operators for binary images,
    * connected component segmentation and binary region processing,
    * color images and colorimetric color spaces,
    * edge detection (including Canny),
    * corner detection,
    * robust line, circle and ellipse fitting methods,
    * primitive detection by RANSAC and Hough transform,
    * feature detection with SIFT and MSER,
    * geometric image transformations and pixel interpolation,
    * gradient noise generation.
    * 
* Rich collection of mathematical methods for handling vectors and matrices, solving 
 linear and non-linear systems, calculation of eigenvalues etc.
* Utility methods for interfacing with ImageJ (GUI, dialogs, progress monitoring, ROIs etc). 
* Automatic generation of `plugins.config` files for producing plugin JAR files.
  


## Use with Maven

Users interested in applying `imagingbook`
functionality in their own ImageJ plugins only require this package as a Maven dependency
in the project's `pom.xml`file:
```
<dependency>
  <groupId>com.imagingbook</groupId>
  <artifactId>imagingbook-common</artifactId>
  <version>6.0.0</version>
</dependency>
```
Replace `6.0.0` with the most current release version available on
[Maven Central](https://mvnrepository.com/artifact/com.imagingbook/imagingbook-common).

**JavaDoc:** https://imagingbook.github.io/imagingbook-public/javadoc/imagingbook.common/module-summary.html

**Maven Artifact:** https://mvnrepository.com/artifact/com.imagingbook/imagingbook-common

**Repository:** [imagingbook-public](https://github.com/imagingbook/imagingbook-public)

