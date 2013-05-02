package eu.excitementproject.eop.biutee.utilities.legacy;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.BIU.utils.logging.EL;

import eu.excitementproject.eop.biutee.utilities.BiuteeLog4jConfigurator;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * The <code>ExperimentLogger</code> is a class, in Utils project, in package
 * org.BIU.utils.logging.
 * 
 * That class uses log4j in an unusual way, which is not the way used by this system.
 * However, a lot of legacy code depends on it.
 * 
 *  This class, ExperimentLoggerNeutralizer, makes all of the uses of the
 *  <code>ExperimentLogger</code> to behave as a regular Logger of log4j.
 *  
 * @author Asher Stern
 * @since Feb 16, 2011
 *
 */
public class ExperimentLoggerNeutralizer
{
	public static final String LOG4J_FILE_NAME = "log4j.properties";
	
	
	/**
	 * Use this method if the log4j loggers are initialized by another mechanism
	 * 
	 * @throws TeEngineMlException
	 */
	public void neutralize() throws TeEngineMlException
	{
		//throw new RuntimeException("Disabled temporarily during migration");
		EL.initByPass();
//		ac.biu.nlp.inference.lexical.logging.EL.initByPass();
	}
	
	@Deprecated
	public void neutralizeOld() throws TeEngineMlException
	{
		createFileIfNotExist(BiuteeLog4jConfigurator.DEFAULT_LOG4J_PROPERTIES_CONTENTS,LOG4J_FILE_NAME);
		try
		{
			throw new RuntimeException("Disabled temporarily during migration");
//			EL.init(null);
		}
		catch (Exception e)
		{
			throw new TeEngineMlException("Could not initialize EL.",e);
		}
		
	}
	
	
	
	
	@Deprecated
	protected void createFileIfNotExist(String fileContents, String fileName) throws TeEngineMlException
	{
		File file = new File(fileName);
		boolean toCreate = true;
		if (file.exists())if(file.isFile())toCreate=false;
		if (toCreate)
		{
			try
			{
				PrintWriter printWriter = new PrintWriter(file);
				try
				{
					printWriter.println(fileContents);
				}
				finally
				{
					if (printWriter!=null)
						printWriter.close();
				}
			}
			catch (IOException e)
			{
				throw new TeEngineMlException("Could not create file: "+fileName+". See nested exception",e);
			}
		}
		
	}

}
