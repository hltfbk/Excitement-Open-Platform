package eu.excitementproject.eop.common.utilities.log4j;

import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Used to log a time interval. This class is used by <code>org.apache.log4j.Logger</code>.
 * <P>
 * Usage:
 * <OL>
 * <LI>Call the constructor with name and level.</LI>
 * <LI>When you want to log the time passed since construction of the {@link IntervalTracker},
 * till current time, call {@link #log(Logger)} method, with the logger you want to write
 * that log into.</LI>
 * </OL>
 * @author Asher Stern
 *
 */
public class IntervalTracker
{
	public static final long MILLI_SECONDS_IN_SECOND = 1000;
	
	
	/**
	 * Constructs and starts the {@link IntervalTracker}, with name and level.
	 * The log messages will be logged only if the given level is greater than
	 * or equal the logger's (given in {@link #log(Logger)} or {@link #log(Logger, Level)})
	 * level.
	 * 
	 * @param name
	 * @param level
	 */
	public IntervalTracker(String name, Level level)
	{
		this(name,level,true);
		
	}
	
	/**
	 * Use this constructor if you don't want to start immediately.
	 * You will have to call {@link #start()} manually.
	 * @param name
	 * @param level
	 * @param startNow
	 */
	public IntervalTracker(String name, Level level, boolean startNow)
	{
		this.name = (name!=null)?name:"(no name)";
		this.level = level!=null?level:Level.DEBUG;
		if (startNow)
			start();
	}
	
	/**
	 * Starts the {@link IntervalTracker}. Call this method if you used
	 * the constructor {@link #IntervalTracker(String, Level, boolean)} with <tt>false</tt>
	 * in the last parameter.
	 */
	public void start()
	{
		startDate = new Date();
		started = true;
	}
	
	/**
	 * If <tt> true </tt> - the log messages will be printed in seconds. if <tt>false</tt> -
	 * the log messages will be printed in milliseconds.
	 * @param logSeconds
	 */
	public void setLogSeconds(boolean logSeconds)
	{
		this.logSeconds = logSeconds;
	}
	
	/**
	 * Writes the appropriate message to the log. The time interval is logger only
	 * if the {@linkplain IntervalTracker}'s level is greater than or equal the logger's level.
	 * 
	 * @param logger a <code>org.apache.log4j.Logger</code> into which the message will
	 * be logged.
	 */
	public void log(Logger logger)
	{
		logger.log(this.level, this);
	}

	/**
	 * Writes the appropriate message to the log.
	 * <BR>
	 * The time interval is logger only if the {@linkplain IntervalTracker}'s level
	 * is greater than or equal the logger's level, but the logging level itself is the
	 * level specified by <code>logLevel</code> parameter.
	 * 
	 * @param logger
	 * @param logLevel
	 */
	public void log(Logger logger, Level logLevel)
	{
		if (this.level.isGreaterOrEqual(logger.getEffectiveLevel()))
		{
			logger.log(logLevel, this);
		}
	}

	
	public String toString()
	{
		return this.name+" interval = "+getIntervalTimeString(logSeconds)+(logSeconds?" seconds.":" ms.");
	}
	
	/**
	 * Returns the Level of the {@link IntervalTracker}.
	 * @return
	 */
	public Level getLevel()
	{
		return this.level;
	}
	
	
	/**
	 * Suspends interval tracking. When you want to continue, call {@link #start()}.
	 */
	public void suspend()
	{
		if (startDate!=null)
		{
			Date currentDate = new Date();
			accumulate += currentDate.getTime()-startDate.getTime();
		}
		startDate = null;
	}
	
	protected long getIntervalTime()
	{
		long ret = accumulate;
		if (startDate!=null)
		{
			Date currentDate = new Date();
			ret += currentDate.getTime()-startDate.getTime();
		}
		return ret;
	}
	
	protected String getIntervalTimeString(boolean inSeconds)
	{
		String ret = null;
		if (!started)
			ret = "warning: not started";
		else
		{
			if (inSeconds)ret = String.valueOf(getIntervalTime()/MILLI_SECONDS_IN_SECOND);
			else ret = String.valueOf(getIntervalTime());
		}
		return ret;
	}
	
	
	
	protected String name;
	protected Level level;
	
	protected Date startDate;
	protected boolean started = false;
	protected boolean logSeconds = true;
	protected long accumulate = 0;
}
