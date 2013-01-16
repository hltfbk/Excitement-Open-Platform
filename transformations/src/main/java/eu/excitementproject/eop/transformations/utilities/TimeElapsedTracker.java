package eu.excitementproject.eop.transformations.utilities;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Date;

/**
 * Measures times: CPU time and World-clock time.
 * Used to measure the run-time of a piece of code.
 * 
 * @author Asher Stern
 * @since Jul 31, 2011
 *
 */
public class TimeElapsedTracker
{
	public void start()
	{
		if (started)
		{
			malformed=true;
		}
		worldClockStart = new Date().getTime();
		if (threadMXBean.isCurrentThreadCpuTimeSupported())
		{
			cpuTimeStart = threadMXBean.getCurrentThreadCpuTime();
		}
		
		started=true;
		ended=false;
	}
	
	public void end()
	{
		if (started)
		{
			if (threadMXBean.isCurrentThreadCpuTimeSupported())
			{
				cpuTimeElapsed = threadMXBean.getCurrentThreadCpuTime()-cpuTimeStart;
			}
			worldClockElapsed = new Date().getTime()-worldClockStart;
			ended=true;
			started=false;
			
			averageCPUTimeMS = averageCPUTimeMS*count+((double)(cpuTimeElapsed/1000000));
			averageCPUTimeMS /= (count+1);
			averageWorldClockMS = averageWorldClockMS*count+((double)worldClockElapsed);
			averageWorldClockMS /= (count+1);
			
			accumulateCpuTimeNano+=cpuTimeElapsed;
			accumulateWorldClockMS+=worldClockElapsed;
			
			count++;
		}
		else
		{
			malformed=true;
			started=false;
			ended=false;
		}
	}
	
	/**
	 * Returns CPU time elapsed from "start" to "end" in nanoseconds
	 * @return
	 */
	public long getCpuTimeElapsed()
	{
		return cpuTimeElapsed;
	}

	/**
	 * Returns World clock time elapsed from "start" to "end" in milliseconds
	 * @return
	 */
	public long getWorldClockElapsed()
	{
		return worldClockElapsed;
	}
	
	
	
	public double getAverageWorldClockMS()
	{
		return averageWorldClockMS;
	}

	public double getAverageCPUTimeMS()
	{
		return averageCPUTimeMS;
	}
	
	

	public boolean isMalformed()
	{
		return malformed;
	}

	@Override
	public String toString()
	{
		if (malformed)
		{
			return "malformed";
		}
		else if (!ended)
		{
			return "Not computed";
		}
		else
		{
			StringBuffer sb = new StringBuffer();
			sb.append("CPU time: ");
			sb.append(String.format("%,d",cpuTimeElapsed/1000000));
			sb.append(" MS. World clock time: ");
			sb.append(String.format("%,d MS.",worldClockElapsed));
			return sb.toString();
		}
	}
	
	public String getAverages()
	{
		if (malformed)
		{
			return "malformed";
		}
		else if (!ended)
		{
			return "Not computed";
		}
		else
		{
			return String.format("Average CPU time: %,.0f MS. Average world clock time: %,.0f MS. Count = %,d", averageCPUTimeMS, averageWorldClockMS, count);
		}
		
	}
	
	public String getAccumulated()
	{
		if (malformed)
		{
			return "malformed";
		}
		else if (!ended)
		{
			return "Not computed";
		}
		else
		{
			return String.format("Accumulated CPU time: %,d MS. Accumulated world clock time: %,d MS. Count = %,d", accumulateCpuTimeNano/1000000, accumulateWorldClockMS, count);
		}
		
	}








	private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	private long cpuTimeStart = 0;
	private long worldClockStart = 0;
	
	private long cpuTimeElapsed = 0;
	private long worldClockElapsed = 0;
	
	private boolean started = false;
	private boolean ended = false;
	
	private boolean malformed = false;
	
	private long count=0;
	private double averageWorldClockMS=0;
	private double averageCPUTimeMS=0;
	
	private long accumulateCpuTimeNano = 0;
	private long accumulateWorldClockMS = 0;
}
