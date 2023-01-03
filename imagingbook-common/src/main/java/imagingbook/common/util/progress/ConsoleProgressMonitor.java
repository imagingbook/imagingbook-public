/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util.progress;

/**
 * A simple progress monitor that only prints the progress of the monitored target task to the console. See
 * {@link ProgressMonitorExample} for a usage example.
 *
 * @see ProgressMonitor
 * @see ProgressReporter
 * @see ProgressMonitorExample
 */
public class ConsoleProgressMonitor extends ProgressMonitor {
	
	/**
	 * Constructor.
	 * @param target the task ({@link ProgressReporter}) to be monitored
	 */
	public ConsoleProgressMonitor(ProgressReporter target) {
		super(target);
	}

	@Override
	public void handleProgress(double progress, long elapsedNanoTime) {
		System.out.format("progress = %.3f elapsed time = %.2fs\n", progress, elapsedNanoTime / 1.0e9);
	}
	
	@Override
	public void close() {
		super.close();
		System.out.println("done.");
	}

}
