![frankenburg-1918-960x200k.png](docs/img/443892583-frankenburg-1918-960x200k.png)

# 'imagingbook' Main Source Code Repository

This repository contains the **Java source code** accompanying the 
**digital image processing books** by **W. Burger and M. J. Burge**, 
published by Springer. This software is based on **[ImageJ](http://rsbweb.nih.gov/ij/index.html)**.
Please visit the main website **[imagingbook.com](https://imagingbook.com/)** for more information.

**Index terms:** digital image processing, computer algorithms, Java, ImageJ, textbook support.

## Repository Structure

The source code is built as a modular Maven project, which includes the following sub-projects:

* **imagingbook-common**: the common library of image processing algorithms and utility code.
* **imagingbook-plugins-book**: a collection of ImageJ plugins related to individual book chapters.
* **imagingbook-plugins-extras**: plugins related to chapters in previous book editions or other publications.
* **imagingbook-plugins-demos**: various plugins illustrating basic ImageJ concepts.
* **imagingbook-plugins-tools**: plugins providing tools for working with ImageJ.

[**Change Log**](CHANGES.md)

## Documentation (JavaDoc):

* [**imagingbook-common**](https://imagingbook.github.io/imagingbook-public/imagingbook-common/javadoc)
* [**imagingbook-plugins-book**](https://imagingbook.github.io/imagingbook-public/imagingbook-plugins-book/javadoc)
* [**imagingbook-plugins-extras**](https://imagingbook.github.io/imagingbook-public/imagingbook-plugins-extras/javadoc)
* [**imagingbook-plugins-demos**](https://imagingbook.github.io/imagingbook-public/imagingbook-plugins-demos/javadoc)
* [**imagingbook-plugins-tools**](https://imagingbook.github.io/imagingbook-public/imagingbook-plugins-tools/javadoc)

## Usage with Maven
Each of the above items is available as a [Maven artifact on Maven Central](https://mvnrepository.com/artifact/com.imagingbook).
For example, to use the `imagingbook-common` library, simply include the following in your project's `pom.xml`file:

## Related Projects

* **[imagingbook-plugins-all](https://github.com/imagingbook/imagingbook-plugins-all)**: Ready-to-go Maven project setup with imagingbook-library and all plugins.
* **[imagingbook-maven-demo-project](https://github.com/imagingbook/imagingbook-maven-demo-project)**: A minimal Maven setup for ImageJ + imagingbook-library.
* **[imagingbook-calibrate](https://github.com/imagingbook/imagingbook-calibrate)**. Implementation of Zhang's camera calibration scheme.
* **[imagingbook-violajones](https://github.com/imagingbook/imagingbook-violajones)**: Implementation of Viola-Jones face detection.
