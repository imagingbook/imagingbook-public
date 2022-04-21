package imagingbook.common.util.progress;

final class ProgressMonitorExample implements ProgressReporter {
	
	int iter = 0;
	int iterMax = 100;
	
	@Override
	public double getProgress() {
		return (double) iter / iterMax;
	}
	
	/**
	 * The task (slow process) to be monitored.
	 */
	protected void run() {
		for (iter = 0; iter < iterMax; iter++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}
	
	public static void main(String[] args) {
		ProgressMonitorExample reporter = new ProgressMonitorExample();
		try (ProgressMonitor monitor = new MyProcessMonitor(reporter)) {	// uses autoStart
			reporter.run();	// the task to be monitored
		}
		System.out.println("done.");

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
