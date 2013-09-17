package eu.excitementproject.eop.biutee.rteflow.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.PluginRegistry;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.transformations.operations.OperationException;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 */
public class ScriptsCreator
{
	public ScriptsCreator(ConfigurationFile configurationFile,
			PluginRegistry pluginRegistry, TESystemEnvironment teSystemEnvironment, 
			int numberOfScripts)
	{
		super();
		this.configurationFile = configurationFile;
		this.pluginRegistry = pluginRegistry;
		this.teSystemEnvironment = teSystemEnvironment;
		this.numberOfScripts = numberOfScripts;
	}



	public void create() throws InterruptedException, ExecutionException
	{
		List<ScriptCreatorCallable> callables = new ArrayList<>(numberOfScripts);
		for (int index=0;index<numberOfScripts;++index)
		{
			callables.add(new ScriptCreatorCallable());
		}
		queue = new ArrayBlockingQueue<OperationsScript<Info,BasicNode>>(numberOfScripts);
		ExecutorService executor = Executors.newFixedThreadPool(numberOfScripts);
		ExecutionException exception = null;
		logger.debug("Calling executor.invokeAll to construct all sciprts in parallel.");
		List<Future<Boolean>> futures = executor.invokeAll(callables);
		try
		{
			for (Future<Boolean> future : futures)
			{
				try
				{
					future.get();
				}
				catch (ExecutionException e)
				{
					if (null==exception)
					{
						exception=e;
					}
				}
			}
		}
		finally
		{
			executor.shutdown();
		}
		logger.debug("All scripts have been constructed and initialized.");
		
		scripts = new ArrayList<>(numberOfScripts);
		while (!queue.isEmpty())
		{
			OperationsScript<Info,BasicNode> script = queue.take();
			scripts.add(script);
		}
		logger.debug("List of scripts has been created.");
		
		if (exception != null)
		{
			logger.error("Creation and initialization of scripts failed. Now cleaning up all available scripts. Afterwards, the exception will be thrown.",exception);
			for (OperationsScript<Info,BasicNode> script : scripts)
			{
				try{script.cleanUp();}
				catch(Exception ex)
				{
					//do nothing with the exception, because the ExecutionException will be thrown.
					logger.error("Clean up of script also failed.",ex);
				}
			}
			logger.error("Now throwing the exception of the script initialization...");
			throw exception;
		}
	}
	
	
	
	public List<OperationsScript<Info, BasicNode>> getScripts()
	{
		return scripts;
	}



	private class ScriptCreatorCallable implements Callable<Boolean>
	{
		@Override
		public Boolean call() throws OperationException, InterruptedException, GapException
		{
			try
			{
				logger.debug("Constructing a script...");
				OperationsScript<Info,BasicNode> script = new ScriptFactory(configurationFile, pluginRegistry,teSystemEnvironment).getDefaultScript();
				script.init();
				logger.info("a script has been constructed.");
				queue.put(script);
				logger.debug("Script has been put in script quque.");
				return true;
			}
			catch(Throwable t)
			{
				logger.error("Script construction failed.",t);
				throw t; // This works on JDK 1.7 and above.
			}
		}
	}

	// input
	private final ConfigurationFile configurationFile;
	private final PluginRegistry pluginRegistry;
	private final TESystemEnvironment teSystemEnvironment;
	private final int numberOfScripts;

	// internals
	private ArrayBlockingQueue<OperationsScript<Info,BasicNode>> queue;
	
	// output
	private List<OperationsScript<Info,BasicNode>> scripts;
	
	// logger
	private static final Logger logger = Logger.getLogger(ScriptsCreator.class);
}
