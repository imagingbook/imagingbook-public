This is a local ImageJ installation set up for testing the
plugins contained in this subproject. Note that the other
plugin collections hold only copies of the plugins contained here
and no other collection has a dedicated ImageJ setup.

* The default output folder is set to ImageJ/plugins/ (by <outputDirectory>
  in the Maven pom.xml),  which makes Eclipse set the default output folder
  accordingly.
  
* The imagingbook-common library is imported as a Maven dependency and
  copied to Image/jars. Thus a rebuild of imagingbook-common and restart
  of ImageJ are required if the library is updated.
  Or simply run 'mvn install' on imagingbook-public.
  
* All other required jar files are copied to ImageJ/jars/ by Maven.
  