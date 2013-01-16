package eu.excitementproject.eop.transformations.utilities;

/**
 * 
 * Used to acknowledge threads that they should abort their current work.
 * When a task is divided into several threads, and is done in a multi-thread manner,
 * if one of the threads fails to accomplish its work, than it means that the task will
 * not be completed, which means that there is no need that the other threads will continue
 * their work.
 * <P>
 * Instead of using {@link Thread#destroy()} or {@link Thread#stop()}, which
 * are deprecated, a simple concept of {@link StopFlag} is introduced here.
 * All the threads share the same instance of {@link StopFlag}, and constantly check
 * its status by calling the method {@link #isStop()}. If that method returns <tt>true</tt>,
 * it means that there was some failure in one of the threads, and the other threads will
 * end their work safely.
 * <BR>
 * A thread that fails should call {@link #stop()} in order to acknowledge the other threads
 * that there was a failure and they should abort their work.
 * 
 * @author Asher Stern
 * @since Jun 6, 2011
 *
 */
public class StopFlag
{
	@SuppressWarnings("serial")
	public static class StopException extends Exception{}
	
	/**
	 * A thread should call this if it has any failure and cannot accomplish its task.
	 */
	public synchronized void stop()
	{
		stop=true;
	}
	
	/**
	 * Indicates whether any thread had a failure.
	 * 
	 * @return true if any thread had a failure (and called {@link #stop()} method).
	 */
	public boolean isStop()
	{
		return this.stop;
	}
	
	private boolean stop=false;
}
