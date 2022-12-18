![frankenburg-1918-960x200k.png](docs/img/443892583-frankenburg-1918-960x200k.png)

# IMAGINGBOOK - Main Source Code Repository

This repository contains the **Java source code** accompanying the 
**Digital Image Processing** textbooks by **W. Burger and M. J. Burge**, 
published by Springer. This software is based on **[ImageJ](http://rsbweb.nih.gov/ij/index.html)**.
Please visit our main website **[imagingbook.com](https://imagingbook.com/)** for more information.

**Index terms:** digital image processing, computer algorithms, Java, ImageJ, textbook support.

## Repository Structure

The source code is built as a modular Maven project, which includes the following modules:

### Library and Data Modules

These "library-only" modules are packaged as `JAR` files and typically imported as Maven
dependencies. They may also be copied manually to ImageJ's `jars/` directory 
(see <a href="#use-without-maven">Use Without Maven</a> below):

* **[imagingbook-core](imagingbook-core/)** <br>
    Minimal infrastructure required for building other `imagingbook` library modules.
    Includes code for basic file handling, managing resources and automatically compiling 
    `plugins.config` files for ImageJ plugin sets.  
* **[imagingbook-common](imagingbook-common/)** <br> 
    This is the main `imagingbook` library with implementions of image processing algorithms,
    associated data structures and utility code. Users interested in applying `imagingbook`
    functionality in their own programs only need to import this module (artefact) as a Maven
    dependency.
* **[imagingbook-spectral](imagingbook-spectral/)** <br>
    Library code related to spectral image processing (Fourier transforms etc.),
    separated from the main `imagingbook` library to minimize third-party dependencies.
* **[imagingbook-pdf](imagingbook-pdf/)**<br>
    PDF-related library code, separated from the main `imagingbook` library to minimize 
    third-party dependencies.
* **[imagingbook-sample-images](imagingbook-sample-images/)** <br>
    Provides a set of (JAR-packaged) sample images used for demos and testing.

### ImageJ Plugin Modules

These packages contain sets of `ImageJ` plugins that make use of the `imagingbook` library.
Each plugin set is packaged as a `JAR` file and may be imported as a Maven dependency
or copied manually to ImageJ's `plugins/` directory:

* **[imagingbook-plugins-book](imagingbook-plugins-book/)** <br>
    A collection of `ImageJ` plugins related to individual book chapters (including 
    materials from previous editions) and tools for working with `ImageJ` (e.g., exact
    zooming and PDF-export).
* **[imagingbook-plugins-demos](imagingbook-plugins-demos/)** <br>
    Plugins demonstrating the use of various technical concepts in `ImageJ` and the
    `imagingbook` library.

<!-- [**Change Log**](CHANGES.md) -->

### API Documentation

* [**imagingbook-public**](https://imagingbook.github.io/imagingbook-public/javadoc)
  (aggregated JavaDoc for latest build)


### Where to Report Problems

This software is mainly intended for educational purposes and comes as is, with no
guarantees whatsoever. API changes that make the code incompatible with previous versions
may happen at any time. Users are encouraged to report any enountered problems here: 

* **https://github.com/imagingbook/imagingbook-public/issues**


## Use With Maven

### Using Libraries Only

Each of the above modules is available as a 
[Maven artifact on Maven Central](https://search.maven.org/search?q=g:com.imagingbook).
For example, to use the `imagingbook-common` library, simply include the following in your
project's `pom.xml`file:
```
<dependency>
  <groupId>com.imagingbook</groupId>
  <artifactId>imagingbook-common</artifactId>
  <version>7.0.0</version>
</dependency>
```
Replace `7.0.0` with the most current release version found on 
[Maven Central](https://mvnrepository.com/artifact/com.imagingbook/imagingbook-common).

### Preconfigured Maven Projects

The following preconfigured projects are available on GitHub for getting started:

* **[imagingbook-plugins-all](https://github.com/imagingbook/imagingbook-plugins-all)** <br> 
    This is a ready-to-go Maven project that includes the `imagingbook` library, all plugin sets listed above, 
    packaged in a complete `ImageJ` runtime setup.
* **[imagingbook-maven-demo-project](https://github.com/imagingbook/imagingbook-maven-demo-project)** <br> This is
    a minimal Maven setup for using the `imagingbook` library with `ImageJ`. It includes some sample Java code
    to get started.

These projects are set up to develop and run `ImageJ` user plugins out of the box. 
They are based on a special Maven profile in 
([**imagingbook-parent-pom**](https://github.com/imagingbook/imagingbook-public/tree/master/imagingbook-parent-pom)),
which takes care of ImageJ's particular directory structure during the Maven build.
To use, clone any of these repositories and import it as a Maven project in your favorite IDE.
If necessary, perform Maven `clean` and `install` to update all dependencies and the runtime setup.
These projects may also be used as a good starting point when working without Maven (see below).


## <a id="WithoutMaven"></a>Use Without Maven

To use the `imagingbook` library in an existing (non-Maven based) `ImageJ` environment you need to manually copy all necessary JAR files
e.g., from [imagingbook-plugins-all](https://github.com/imagingbook/imagingbook-plugins-all/tree/master/ImageJ):

* `ImageJ/jars/*.jar` &rarr; `ImageJ/jars`
* `ImageJ/plugins/*.jar` &rarr;  `ImageJ/plugins`

Then restart `ImageJ`. Libraries and plugins should be loaded automatically. The JAR files in 
[imagingbook-plugins-all](https://github.com/imagingbook/imagingbook-plugins-all/tree/master/ImageJ)
are typically from the most recent stable ("release") build.

## Related Projects

Other projects using the `imagingbook` library include:

* **[imagingbook-calibrate](https://github.com/imagingbook/imagingbook-calibrate)**: Implementation of Zhang's camera calibration scheme.
* **[imagingbook-violajones](https://github.com/imagingbook/imagingbook-violajones)**: Implementation of Viola-Jones face detection.



<!-- [Markdown tests](docs/markdown-test.md) -->
