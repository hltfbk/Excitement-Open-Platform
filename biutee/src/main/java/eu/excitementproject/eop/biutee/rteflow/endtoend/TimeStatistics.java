package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.io.Serializable;

import eu.excitementproject.eop.transformations.utilities.TimeElapsedTracker;


/**
 * 
 * @author Asher Stern
 * @since Dec 23, 2013
 *
 */
public class TimeStatistics implements Serializable
{
	private static final long serialVersionUID = 2588315787317423702L;
	
	public static TimeStatistics fromTimeElapsedTracker(TimeElapsedTracker tracker)
	{
		long cputime = tracker.getCpuTimeElapsed();
		long worldtime = tracker.getWorldClockElapsed();
		return new TimeStatistics(cputime,worldtime);
	}
	
	public TimeStatistics(long cpuTimeNanoSeconds, long worldClockTimeMilliSeconds)
	{
		super();
		this.cpuTimeNanoSeconds = cpuTimeNanoSeconds;
		this.worldClockTimeMilliSeconds = worldClockTimeMilliSeconds;
	}
	
	
	
	public long getCpuTimeNanoSeconds()
	{
		return cpuTimeNanoSeconds;
	}
	public long getWorldClockTimeMilliSeconds()
	{
		return worldClockTimeMilliSeconds;
	}
	
	



	@Override
	public String toString()
	{
		return "Cpu Time (ns) = "
				+ String.format("%,d", getCpuTimeNanoSeconds()) 
				+ ", World Clock Time (ms) = "
				+ String.format("%,d",getWorldClockTimeMilliSeconds()) ;
	}





	private final long cpuTimeNanoSeconds;
	private final long worldClockTimeMilliSeconds;
}
