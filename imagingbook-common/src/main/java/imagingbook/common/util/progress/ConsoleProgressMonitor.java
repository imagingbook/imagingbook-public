package imagingbook.common.util.progress;

/**
 * A simple progress monitor that only prints the progress of the monitored target task
 * to the console.
 * See {@link ProgressMonitorExample} for a usage example.
 * 
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
