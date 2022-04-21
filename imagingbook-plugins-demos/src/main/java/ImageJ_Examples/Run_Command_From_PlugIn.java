/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Examples;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 * This plugin demonstrates how to run another ImageJ "command" (plugin)
 * from our own PlugIn using the IJ.run() method.
 * 
 * @author WB
 */
public class Run_Command_From_PlugIn implements PlugIn {
	
    public void run(String args) {
    	ImagePlus im = IJ.getImage();
    	IJ.run(im, "Invert", "");	// run the "Invert" command on im
    	// IJ.run("Invert");		// alternatively, run "Invert" on the current image
    }
}
