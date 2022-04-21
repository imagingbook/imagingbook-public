package imagingbook.common.util.progress;

import imagingbook.common.filter.GenericFilter;
import imagingbook.common.util.progress.ij.ProgressBarMonitor;

/**
 * This class represents a "progress monitor". This is a simple
 * {@link Thread} that queries the attached target task 
 * (implementing {@link ProgressReporter}) and calls its 
 * {@link #handleProgress(double, long)} method at regular intervals.
 * 
 * {@link ProgressMonitor} implements the {@link AutoCloseable} and thus can
 * (and should) be used in a try-with-resources context, e.g.,
 * <pre>
 * ProgressReporter task = ....; // the task to be monitored
 * try (ProgressMonitor m = new MyProgressMonitor(task)) {
 *     // run task ...
 * }</pre>
 * Otherwise, if not used in a auto-start/close mode, the methods {@link #start()} 
 * and {@link #close()} should be used to "manually" start and terminate
 * monitoring.
 * See {@link GenericFilter} and {@link ProgressBarMonitor} for an example.
 */
public abstract class ProgressMonitor extends Thread implements AutoCloseable {
	
	public static int DefaultWaitTime = 250; // ms
	
	private int waitTime = DefaultWaitTime;
	private final ProgressReporter target;

	private boolean running;
	private long startTime, endTime;
	
	/**
	 * Constructor, starts monitoring immediately.
	 * The target object's {@link ProgressReporter#getProgress()}
	 * method is called it regular intervals.
	 * 
	 * @param target the object (task) to be monitored
	 */
	public ProgressMonitor(ProgressReporter target) {
		this(target, true);
	}
	
	/**
	 * Constructor, optionally starts monitoring immediately or waits
	 * for its {@link #start()} method to be called (which starts the 
	 * associated thread).
	 * The target object's {@link ProgressReporter#getProgress()}
	 * method is called it regular intervals.
	 * 
	 * @param target the object (task) to be monitored
	 * @param autoStart starts immediately if true
	 */
	public ProgressMonitor(ProgressReporter target, boolean autoStart) {
		if (target == null)
			throw new IllegalArgumentException("null target instance!");
		this.target = target;
		if (autoStart) {
			start();
		}
	}
	
	/**
	 * The time interval between progress queries can be set with this method.
	 * See also {@link #DefaultWaitTime}.
	 * 
	 * @param waitTime the time interval (in milliseconds)
	 */
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
	
	/**
	 * Abstract method to be implemented by concrete sub-classes.
	 * It is called by the {@link ProgressMonitor} thread in regular intervals.
	 * Current progress data are passed.
	 * It is up to the implementation what action should be performed,
	 * e.g., display the current progress graphically 
	 * (see e.g. {@link ProgressBarMonitor}).
	 * 
	 * @param progress the current progress value [0,1)
	 * @param elapsedNanoTime the time elapsed since the progress monitor was started (in nanoseconds)
	 */
	public abstract void handleProgress(double progress, long elapsedNanoTime);
	
	// --------------------------------------------------------

	@Override
	public void run() {
		handleProgress(0, 0);
		this.startTime = System.nanoTime();
		running = true;
		do {
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {break;}
			handleProgress(target.getProgress(), System.nanoTime() - startTime);
		} while(running);
		this.endTime = System.nanoTime();
	}
	
	@Override
	public void close() {
		running = false;
        this.interrupt();
	}
	
	// --------------------------------------------------------
	
	/**
	 * Returns the time elapsed since monitoring started (in seconds).
	 * @return the elapsed time in seconds
	 */
	public double getElapsedTime() { // result is in seconds
		if (running) {
			return  (System.nanoTime() - startTime) / 1E9d;
		}
		else {
			return (endTime - startTime) / 1E9d;
		}
	}

}
