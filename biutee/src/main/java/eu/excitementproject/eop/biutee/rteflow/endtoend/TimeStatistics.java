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
		return fromTimeElapsedTracker(tracker,null,null);
	}

	public static TimeStatistics fromTimeElapsedTracker(TimeElapsedTracker tracker, Long numberOfExpandedElements, Long numberOfGeneratedElements)
	{
		long cputime = tracker.getCpuTimeElapsed();
		long worldtime = tracker.getWorldClockElapsed();
		return new TimeStatistics(cputime,worldtime,numberOfExpandedElements,numberOfGeneratedElements);
	}

	
	public TimeStatistics(long cpuTimeNanoSeconds, long worldClockTimeMilliSeconds,
			Long numberOfExpandedElements,Long numberOfGeneratedElements)
	{
		super();
		this.cpuTimeNanoSeconds = cpuTimeNanoSeconds;
		this.worldClockTimeMilliSeconds = worldClockTimeMilliSeconds;
		this.numberOfExpandedElements = numberOfExpandedElements;
		this.numberOfGeneratedElements = numberOfGeneratedElements;
	}
	
	
	
	public long getCpuTimeNanoSeconds()
	{
		return cpuTimeNanoSeconds;
	}
	public long getWorldClockTimeMilliSeconds()
	{
		return worldClockTimeMilliSeconds;
	}
	public Long getNumberOfExpandedElements()
	{
		return numberOfExpandedElements;
	}
	public Long getNumberOfGeneratedElements()
	{
		return numberOfGeneratedElements;
	}
	

	@Override
	public String toString()
	{
		String ret = "Cpu Time (ns) = "
				+ String.format("%,d", getCpuTimeNanoSeconds()) 
				+ ", World Clock Time (ms) = "
				+ String.format("%,d",getWorldClockTimeMilliSeconds());
		
		if ( (numberOfExpandedElements!=null) && (numberOfGeneratedElements!=null) )
		{
			ret = ret
					+ ", Number of expanded elements = "
					+ String.format("%,d",getNumberOfExpandedElements())
					+ ", Number of generated elements = "
					+ String.format("%,d",getNumberOfGeneratedElements());
		}
		return ret;
	}





	private final long cpuTimeNanoSeconds;
	private final long worldClockTimeMilliSeconds;
	private final Long numberOfExpandedElements;
	private final Long numberOfGeneratedElements;
}
