package imagingbook.common.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Modern-style directory traversal. Requires Java 1.7 or higher!
 * 
 * @author WB
 * @version 2016/04/05
 *
 */
public class DirectoryWalker {
	
	private final List<String> fileList;
	private final String[] extensions;
	
	/**
	 * Constructor.
	 * @param extensions a sequence of file extensions like ".jpg", ".gif", ".tif" etc.
	 * Note that extensions are case sensitive, i.e., multiple extensions must be 
	 * supplied if upper/lower case extensions should be considered.
	 * Supply {@code null} to accept *any* file extension.
	 */
	public DirectoryWalker(String... extensions) {
		this.fileList = new ArrayList<String>();
		this.extensions = extensions;
	}
	
	/**
	 * Use this method to recursively collect all files with the initially specified
	 * extensions, starting from the given directory. 
	 * TODO: clean up exception handling.
	 * 
	 * @param startDir The start directory.
	 * @return A list of file names.
	 */
	public Collection<String> collectFiles(String startDir) {
//		DirectoryWalker walker = new DirectoryWalker(extensions);
		try {
			this.traverse(Paths.get(startDir));
		} catch (IOException e) { }
		return this.fileList;
	}
	
	/**
	 * Traverses the directory tree and collects all matching file names.
	 * TODO: clean up exception handling.
	 * 
	 * @param startDir start directory
	 * @throws IOException
	 */
	private void traverse(Path startDir) throws IOException {
		Files.walkFileTree(startDir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String pathName = file.toString();
				if (hasMatchingExtension(pathName)) {
					fileList.add(pathName);
					// System.out.println("added file " + file.toString());
				}
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
				if (e == null) {
					//System.out.println("visiting " + dir.toString());
					return FileVisitResult.CONTINUE;
				} else {
					// directory iteration failed
					throw e;
				}
			}
		});
	}
	
	/**
	 * Checks if the pathName has any of the specified extensions.
	 * This is case sensitive!
	 * 
	 * @param pathName
	 * @return true if the path name matches one of the specified extensions
	 */
	private boolean hasMatchingExtension(String pathName) {
		if (extensions == null || extensions.length == 0)
			return true;
		boolean result = false;
		for (String s : extensions) {
			if (pathName.endsWith(s)) {
				result = true;
				break;
			}
		}
		return result;
	}

}
