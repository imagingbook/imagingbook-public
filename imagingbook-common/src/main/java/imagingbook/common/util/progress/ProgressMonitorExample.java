/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.util.progress;

/**
 * A simple example for how to use {@link ProgressReporter} and
 * {@link ProgressMonitor}.
 *
 */
public class ProgressMonitorExample {
	
	public static void main(String[] args) {
		SlowProcess process = new SlowProcess();
		try (ProgressMonitor monitor = new MyProcessMonitor(process)) {	// uses autoStart
			monitor.setWaitTime(200); 	// monitor progress every 200 ms
			process.run();				// the task to be monitored
		}
		System.out.println("done.");

	}
	
	// --------------------------------------------------------------
	
	/**
	 * The task to be monitored (slow process).
	 */
	static class SlowProcess implements ProgressReporter {
		
		int iter = 0;
		int iterMax = 100;
		
		@Override
		public double getProgress() {
			return (double) iter / iterMax;
		}
		
		protected void run() {
			for (iter = 0; iter < iterMax; iter++) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
	}

	// --------------------------------------------------------------
	
	/**
	 * A simple progress monitor that only prints query results
	 * to the console.
	 */
	static class MyProcessMonitor extends ProgressMonitor {
		
		public MyProcessMonitor(ProgressReporter target) {
			super(target);
		}

		@Override
		public void handleProgress(double progress, long elapsedNanoTime) {
			System.out.format("progress = %.3f elapsed = %.2fs\n", progress, elapsedNanoTime / 1E9D);
		}
	}


}
