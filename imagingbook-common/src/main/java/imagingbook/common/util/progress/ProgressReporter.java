package imagingbook.common.util.progress;

import imagingbook.common.filter.GenericFilter;

/**
 * Monitored objects (tasks) must implement this interface.
 * See {@link GenericFilter} for an example.
 * See also {@link ProgressMonitor}.
 * 
 */
public interface ProgressReporter {
	
	/**
	 * Returns a value in [0,1) indicating to which degree this
	 * task is complete.
	 * @return a value between 0 and 1
	 */
	public double getProgress();

}
