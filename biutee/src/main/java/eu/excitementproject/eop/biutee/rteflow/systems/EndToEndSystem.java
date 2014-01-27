package eu.excitementproject.eop.biutee.rteflow.systems;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.Provider;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 16, 2013
 *
 */
public abstract class EndToEndSystem extends SystemInitialization
{
	public EndToEndSystem(String configurationFileName, String configurationModuleName)
	{
		super(configurationFileName, configurationModuleName);
	}
	
	protected void init() throws ConfigurationFileDuplicateKeyException, ConfigurationException, MalformedURLException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		super.init();
		try
		{
			numberOfThreads = retrieveNumberOfThreads();
			if (numberOfThreads<1) throw new BiuteeException("Illegal number of threads. Must be one or more. Given number of threads is: "+numberOfThreads);
			logger.info("Using "+numberOfThreads+" threads.");
			logger.info("Creating scripts.");
			scripts = createScripts(numberOfThreads);
			if (scripts.size()<1) throw new BiuteeException("Empty list of scripts has been created.");
			completeInitializationWithScript(scripts.get(0));
		}
		catch (BiuteeException e)
		{
			throw new TeEngineMlException("Error in initialization. See nested exception.",e);
		}
	}
	
	protected void cleanUp()
	{
		super.cleanUp();
		if (scripts!=null)
		{
			for (OperationsScript<Info, BasicNode> script : scripts)
			{
				if (script!=null)
				{
					script.cleanUp();
				}
			}
		}
	}
	
	protected abstract int retrieveNumberOfThreads() throws BiuteeException;
	
	protected List<OperationsScript<Info, BasicNode>> createScripts(int numberOfthreads) throws BiuteeException
	{
		try
		{
			ScriptsCreator scriptsCreator = new ScriptsCreator(configurationFile,teSystemEnvironment.getPluginRegistry(),teSystemEnvironment,numberOfthreads);
			scriptsCreator.create();
			return scriptsCreator.getScripts();
		}
		catch (InterruptedException | ExecutionException e)
		{
			throw new BiuteeException("Failed to create scripts.",e);
		}
	}
	
	
	

	/**
	 * This class is used for the member field {@link EndToEndTrainer#lemmatizerProvider}.
	 *
	 */
	public class LemmatizerProvider implements Provider<Lemmatizer>
	{
		@Override
		public Lemmatizer get() throws BiuteeException
		{
			try
			{
				return getLemmatizer();
			}
			catch (MalformedURLException | LemmatizerException e)
			{
				throw new BiuteeException("Failed to get the lemmatizer.",e);
			}
		}
	}

	protected LemmatizerProvider lemmatizerProvider = new LemmatizerProvider();
	protected int numberOfThreads;
	protected List<OperationsScript<Info, BasicNode>> scripts;
	
	private static final Logger logger = Logger.getLogger(EndToEndSystem.class);
}
