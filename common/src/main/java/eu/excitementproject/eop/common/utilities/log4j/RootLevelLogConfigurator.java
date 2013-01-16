package eu.excitementproject.eop.common.utilities.log4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


/**
 * 
 * @author Asher Stern
 * 
 * @deprecated Please do not use this class. Use the usual way to initialize
 * log4j, as described in log4j official web site. The engineml project
 * is a good example for initializing and using log4j properly.
 *
 */
@Deprecated
public class RootLevelLogConfigurator
{
	public RootLevelLogConfigurator(String rootName, String actualRootLastName,List<Appender> appenders)
	{
		super();
		this.rootName = rootName;
		this.actualLoggerFullName = rootName+"."+actualRootLastName;
		this.appenders = appenders;
		
	}
	
	public RootLevelLogConfigurator(String rootName, String actualRootLastName,Appender appender)
	{
		this(rootName,actualRootLastName,listOfOneItem(appender));
	}
	
	public void config()
	{
		BasicConfigurator.configure();
		Logger rootLogger = Logger.getLogger(rootName);
		rootLogger.setAdditivity(false);
		rootLogger.removeAllAppenders();
		for (Appender appender : appenders)
		{
			rootLogger.addAppender(appender);
		}
	}
	
	public Logger getActualRootLogger()
	{
		return Logger.getLogger(actualLoggerFullName);
	}
	
	public String getActualRootLoggerName()
	{
		return actualLoggerFullName;
	}
	
	private static <E> List<E> listOfOneItem(E item)
	{
		List<E> ret = new ArrayList<E>(1);
		ret.add(item);
		return ret;
	}
	
	
	private String rootName;
	
	private List<Appender> appenders;
	private String actualLoggerFullName;
}
