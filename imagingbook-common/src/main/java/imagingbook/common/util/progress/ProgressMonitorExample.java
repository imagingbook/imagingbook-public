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
 * A simple example for how to use {@link ProgressReporter} and {@link ProgressMonitor}.
 *
 * @see ProgressReporter
 * @see ProgressMonitor
 */
public abstract class ProgressMonitorExample {
	
	/**
	 * The task to be monitored (some slow process).
	 */
	public static class SlowProcess implements ProgressReporter {		
		int iter = 0;
		int iterMax = 100;
		
		@Override
		public double getProgress() {
			return (double) iter / iterMax;
		}
		
		public void run() {
			for (iter = 0; iter < iterMax; iter++) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
	}

	// --------------------------------------------------------------
	
	/**
	 * Main method for demonstration.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		SlowProcess process = new SlowProcess();
		try (ProgressMonitor monitor = new ConsoleProgressMonitor(process)) {	// uses autoStart
			monitor.setWaitTime(200); 	// check progress every 200 ms
			process.run();				// the task to be monitored
		}

	}
	
}
