package eu.excitementproject.eop.common.utilities.log4j;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class Demo
{

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args)
	{
		RootLevelLogConfigurator configurator = new RootLevelLogConfigurator("a", "b", new ConsoleAppender(new VerySimpleLayout()));
		configurator.config();
		Logger l = configurator.getActualRootLogger();
		l.setLevel(Level.INFO);
		l.info("hi");
		IntervalTracker it = new IntervalTracker("demo", Level.ERROR);
		try{Thread.sleep(1000);}catch(Exception e){e.printStackTrace();}
		it.log(l);
		it.suspend();
		try{Thread.sleep(1000);}catch(Exception e){e.printStackTrace();}
		it.log(l);
		it.start();
		try{Thread.sleep(1000);}catch(Exception e){e.printStackTrace();}
		it.log(l);
		
		
		
	}

}
