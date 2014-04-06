package eu.excitementproject.eop.biutee.rteflow.systems;

import java.util.Date;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.transformations.utilities.GlobalMessages;

import static eu.excitementproject.eop.biutee.utilities.SuccessFileIndicatorUtility.*;

/**
 * The code that runs in the
 * <code>public static void main(String[] args)</code> methods of BIUTEE
 * entry points.
 * <P>
 * The method {@link #run(String[])} should be implemented according to
 * each entry point.
 * 
 * @author Asher Stern
 * @since Jul 28, 2013
 *
 */
public abstract class SystemMain
{
	public Logger mainCanThrowExceptions(Class<?> cls, String[] args) throws Throwable
	{
		Logger logger = null;
		try
		{
			try
			{
				markWorking();
				if (args.length<1)throw new BiuteeException("No arguments. Enter configuration file name as argument.");

				configurationFileName = args[0];
				new LogInitializer(configurationFileName).init();
				logger = Logger.getLogger(cls);

				ExperimentManager.getInstance().start();
				ExperimentManager.getInstance().setConfigurationFile(configurationFileName);

				logger.info(cls.getSimpleName());
				ExperimentManager.getInstance().addMessage(cls.getSimpleName());

				Date startDate = new Date();
				run(args);
				Date endDate = new Date();
				long elapsedSeconds = (endDate.getTime()-startDate.getTime())/1000;
				logger.info(cls.getSimpleName()+" done. Time elapsed: "+elapsedSeconds/60+" minutes and "+elapsedSeconds%60+" seconds.");
			}
			finally
			{
				GlobalMessages.getInstance().addToLogAndExperimentManager(logger);
			}
			boolean experimentManagedSucceeded = ExperimentManager.getInstance().save();
			logger.info("ExperimentManager save "+(experimentManagedSucceeded?"succeeded":"failed")+".");
			markSuccess();
			return logger;
		}
		catch(Throwable e)
		{
			markFailure();
			throw e;
		}
	}
	
	public void main(Class<?> cls, String[] args) {
		Logger logger = null;
		try {
			logger = mainCanThrowExceptions(cls, args);
		}
		catch (Throwable e) {
			ExceptionUtil.outputException(e, System.out);
			if (logger!=null)
			{
				ExceptionUtil.logException(e, logger);
			}
			
		}
	}

	protected abstract void run(String[] args) throws BiuteeException;
	
	protected String configurationFileName;
}
