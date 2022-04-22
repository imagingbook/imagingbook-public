/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.util.progress.ij;

import ij.IJ;
import imagingbook.common.util.progress.ProgressMonitor;
import imagingbook.common.util.progress.ProgressReporter;

/**
 * An instance of this class monitors the progress of another object (task)
 * by querying its status periodically.
 * TODO: Make the associated action more generic (currently progress information is 
 * used to update ImageJ's peogress bar.
 * @author WB
 *
 */
public class ProgressBarMonitor extends ProgressMonitor {
	
	private final boolean ijRunning;
	private boolean ijUpdateProgressBar = true;
	
	public ProgressBarMonitor(ProgressReporter target) {
		super(target);
		this.ijRunning = (IJ.getInstance() != null);
	}

	@Override
	public void handleProgress(double progress, long nanoTime) {
		if (ijRunning && ijUpdateProgressBar) {
			IJ.showProgress(progress);
		}
		
	}

}
