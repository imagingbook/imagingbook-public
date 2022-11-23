package imagingbook.common.mser;

import ij.gui.GenericDialog;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.DialogUtils.DialogHide;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.mser.components.ComponentTree.Method;
import imagingbook.common.util.ParameterBundle;

/**
 * Parameters for MSER detection. Instances of this class may be placed inside a
 * {@link GenericDialog} using
 * {@link DialogUtils#addToDialog(ParameterBundle, ij.gui.GenericDialog)}.
 */
public class MserParameters implements ParameterBundle<MserDetector> {
	//{@link DialogUtils#addToDialog(ParameterBundle<?>, GenericDialog)}
	@DialogLabel("Component tree method")
	public Method method = Method.LinearTime;
	
	@DialogLabel("Delta")
	public int delta = 5;							// = \Delta
	
	@DialogLabel("Min component size (pixels)")
	public int minAbsComponentArea = 3;
	
	@DialogLabel("Min rel. component size")
	public double minRelCompSize = 0.0001;		// = \alpha_{\min}
	
	@DialogLabel("Max rel. component size")
	public double maxRelCompSize = 0.25;		// = \alpha_{\max}
	
	@DialogLabel("Max component size variation")
	public double maxSizeVariation = 0.25;
	
	@DialogLabel("Min component diversity")
	public double minDiversity = 0.50;
	
	@DialogLabel("Constrain ellipse size")
	public boolean constrainEllipseSize = true;
	
	@DialogLabel("Min region compactness")
	public double minCompactness = 0.2;
	
	@DialogLabel("Validate component tree")@DialogHide
	public boolean validateComponentTree = false;
}