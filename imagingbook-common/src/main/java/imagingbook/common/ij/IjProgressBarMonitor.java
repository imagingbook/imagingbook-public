/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.ij;

import ij.IJ;
import imagingbook.common.util.progress.ProgressMonitor;
import imagingbook.common.util.progress.ProgressReporter;

/**
 * Implementation of {@link ProgressMonitor} which periodically polls the completion state of the associated target task
 * ({@link ProgressReporter}) and sends this information to ImageJ's progress bar (if ImageJ is running).
 *
 * @author WB
 * @version 2022/09/07
 * @see ProgressReporter
 * @see ProgressMonitor
 */
public class IjProgressBarMonitor extends ProgressMonitor {
	
	/**
	 * Constructor.
	 * @param target the target task ({@link ProgressReporter}) to be monitored 
	 */
	public IjProgressBarMonitor(ProgressReporter target) {
		super(target);
	}

	@Override
	public void handleProgress(double progress, long nanoTime) {
		if (IJ.getInstance() != null) {
			IJ.showProgress(progress);
		}
	}
	
	@Override
	public void close() {
		super.close();
		IJ.showProgress(1);
	}

}
