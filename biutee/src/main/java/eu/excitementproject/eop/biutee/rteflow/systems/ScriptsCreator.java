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
			PluginRegistry pluginRegistry, int numberOfScripts)
	{
		super();
		this.configurationFile = configurationFile;
		this.pluginRegistry = pluginRegistry;
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
		List<Future<Boolean>> futures = executor.invokeAll(callables);
		ExecutionException exception = null;
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
		
		scripts = new ArrayList<>(numberOfScripts);
		while (!queue.isEmpty())
		{
			OperationsScript<Info,BasicNode> script = queue.take();
			scripts.add(script);
		}
		
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
		public Boolean call() throws OperationException, InterruptedException
		{
			OperationsScript<Info,BasicNode> script = new ScriptFactory(configurationFile, pluginRegistry).getDefaultScript();
			script.init();
			queue.put(script);
			return true;
		}
		
	}

	// input
	private final ConfigurationFile configurationFile;
	private final PluginRegistry pluginRegistry;
	private final int numberOfScripts;

	// internals
	private ArrayBlockingQueue<OperationsScript<Info,BasicNode>> queue;
	
	// output
	private List<OperationsScript<Info,BasicNode>> scripts;
	
	// logger
	private static final Logger logger = Logger.getLogger(ScriptsCreator.class);
}
