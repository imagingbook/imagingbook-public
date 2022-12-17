/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch16_Color_Edges;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.iterate.RandomHueGenerator;
import imagingbook.common.edges.CannyEdgeDetector;
import imagingbook.common.edges.CannyEdgeDetector.Parameters;
import imagingbook.common.edges.EdgeTrace;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.ij.DialogUtils;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * This ImageJ plugin implements the Canny edge detector for all types of images. See Sec. 16.3 of [1] for additional
 * details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/12
 * @see CannyEdgeDetector
 */
public class Color_Edges_Canny implements PlugInFilter {

	private static Parameters params = new Parameters();
	private static boolean ShowEdgeMagnitude = false;
	private static boolean ShowEdgeOrientation = false;
	private static boolean ShowBinaryEdgeMap = true;
	private static boolean ShowColoredEdgeTraces = true;
	private static boolean ListEdgeTraces = false;
	
	private ImagePlus im = null;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Color_Edges_Canny() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Balloons600);
		}
	}

	@Override
	public int setup(String arg0, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}
		
		CannyEdgeDetector detector = new CannyEdgeDetector(ip, params);
		
		if (ShowEdgeMagnitude) {
			ImageProcessor eMag = detector.getEdgeMagnitude();
			(new ImagePlus("Canny Edge Magnitude sigma=" + params.gSigma, eMag)).show();
		}
		
		if (ShowEdgeOrientation) {
			ImageProcessor eOrt = detector.getEdgeOrientation();
			(new ImagePlus("Canny Edge Orientation sigma=" + params.gSigma, eOrt)).show();
		}
		
		if (ShowBinaryEdgeMap) {
			ImageProcessor eBin = detector.getEdgeBinary();
			(new ImagePlus("Canny Binary Edges sigma=" + params.gSigma, eBin)).show();
		}

		if (ShowColoredEdgeTraces) {
			List<EdgeTrace> edgeTraces = detector.getEdgeTraces();
			ColorProcessor traceIp = new ColorProcessor(ip.getWidth(), ip.getHeight());
			RandomHueGenerator rcg = new RandomHueGenerator();
			for (EdgeTrace trace : edgeTraces) {
				traceIp.setColor(rcg.next());
				for (Pnt2d p : trace) {
					traceIp.drawPixel(p.getXint(), p.getYint());
				}
			}
			new ImagePlus("Canny Edges Traces sigma=" + params.gSigma, traceIp).show();
		}
		
		if(ListEdgeTraces) {
			List<EdgeTrace> edgeTraces = detector.getEdgeTraces();
			IJ.log("number of edge traces: " + edgeTraces.size());
			int i = 0;
			for (EdgeTrace et : edgeTraces) {
				IJ.log(i + ". " + et.toString());
				i++;
			}
		}
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());

		addToDialog(params, gd);
		gd.addMessage("Plugin parameters:");
		gd.addCheckbox("Show edge magnitude", ShowEdgeMagnitude);
		gd.addCheckbox("Show edge orientation", ShowEdgeOrientation);
		gd.addCheckbox("Show binary edge map", ShowBinaryEdgeMap);
		gd.addCheckbox("Show colored edge traces", ShowColoredEdgeTraces);
		gd.addCheckbox("List edge traces", ListEdgeTraces);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}	

		getFromDialog(params, gd);
		if (params.gSigma < 0.5f) params.gSigma = 0.5f;
		if (params.gSigma > 20) params.gSigma = 20;
		ShowEdgeMagnitude = gd.getNextBoolean();
		ShowEdgeOrientation = gd.getNextBoolean();
		ShowBinaryEdgeMap = gd.getNextBoolean();
		ShowColoredEdgeTraces = gd.getNextBoolean();
		ListEdgeTraces = gd.getNextBoolean();
		return true;
	}
	
}
