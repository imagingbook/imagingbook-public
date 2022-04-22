package imagingbook.core.resource;

import ij.IJ;
import ij.ImagePlus;

/**
 * <p>
 * Named image resource types implement this interface.
 * This indicates (for testing) that the associated resource can be opened as
 * an image (by ImageJ).
 * Extends interface {@link NamedResource} by adding method {@link #getImage()},
 * which returns an {@link ImagePlus} instance.
 * So this can be used, e.g., on the named resource {@code MyImages.image1}, simply in the form
 * </p>
 * <pre>
 * ImagePlus im = MyImages.image1.getImage();
 * im.show();
 * </pre>
 * 
 * @author WB
 *
 */
public interface ImageResource extends NamedResource {
	
	public default ImagePlus getImage() {
		return IJ.openImage(getURL().toString());
	}

}
