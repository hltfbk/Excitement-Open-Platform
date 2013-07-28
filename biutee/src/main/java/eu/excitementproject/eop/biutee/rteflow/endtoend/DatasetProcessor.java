package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.utilities.StopFlag;

/**
 * Given a {@link Dataset} of T-H pairs, finds a proof for each pair.
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 * @param <I>
 * @param <P>
 */
public class DatasetProcessor<I extends Instance, P extends Proof>
{
	/////////////// PUBLIC ///////////////
	
	public DatasetProcessor(Dataset<I> dataset,
			List<OperationsScript<Info, BasicNode>> scripts,
			LinearClassifier classifierForSearch, Prover<I, P> prover,
			int numberOfThreads)
	{
		super();
		this.dataset = dataset;
		this.scripts = scripts;
		this.classifierForSearch = classifierForSearch;
		this.prover = prover;
		this.numberOfThreads = numberOfThreads;
	}
	
	
	public void process() throws BiuteeException
	{
		initInternals();
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		List<ProverCallable> callables = new ArrayList<>(mapInstances.keySet().size());
		for (Integer id : mapInstances.keySet())
		{
			callables.add(new ProverCallable(id));
		}
		try
		{
			List<Future<P>> futures = executor.invokeAll(callables);
			ExecutionException exception = null;
			for (Future<P> future : futures)
			{
				try{future.get();}catch (ExecutionException e){if (null==exception)exception=e;}
			}
			if (exception != null)
			{
				throw new BiuteeException("An error occurred when processing an instance. See nested exceptions.",exception);
			}
		}
		catch (InterruptedException e)
		{
			throw new BiuteeException("Failed to execute threads. See nested exception.",e);
		}
		finally
		{
			executor.shutdown();
		}
		
		proofs = new ArrayList<>(mapInstances.keySet().size());
		for (Integer id : mapInstances.keySet())
		{
			I instance = mapInstances.get(id);
			P proof = mapProofs.get(id);
			if (null==proof) throw new BiuteeException("Bug. No proof has been generated from the following instance:\n"+instance.toString());
			proofs.add(new InstanceAndProof<I, P>(instance, proof));
		}
	}
	
	
	public List<InstanceAndProof<I, P>> getProofs() throws BiuteeException
	{
		if (null==proofs) throw new BiuteeException("Proofs have not been generated.");
		return proofs;
	}

	
	/////////////// PRIVATE ///////////////

	
	private void initInternals() throws BiuteeException
	{
		if (numberOfThreads!=scripts.size()) throw new BiuteeException("Error. The given number of threads differs from the number of given \""+OperationsScript.class.getSimpleName()+"\"s.");
		try
		{
			mapInstances = collectionToMapWithId(dataset.getListOfInstances());
			scriptQueue = new ArrayBlockingQueue<>(numberOfThreads);
			for (OperationsScript<Info, BasicNode> script : scripts)
			{
				scriptQueue.put(script);
			}
			mapProofs = new LinkedHashMap<>();
		}
		catch (InterruptedException e)
		{
			throw new BiuteeException("There was a problem in the initialization of script queue. See nested exception.",e);
		}
	}

	private static <O> Map<Integer, O> collectionToMapWithId(Collection<O> objects)
	{
		Map<Integer, O> map = new LinkedHashMap<>();
		int id = 1;
		for (O object : objects)
		{
			map.put(id, object);
			++id;
		}
		return map;
	}
	
	
	private class ProverCallable implements Callable<P>
	{
		public ProverCallable(int instanceId)
		{
			super();
			this.instanceId = instanceId;
		}
		@Override
		public P call() throws BiuteeException
		{
			P proof = null;
			if (!stopFlag.isStop())
			{
				boolean failed=true;
				try
				{
					proof = runProver();
					synchronized(mapProofs)
					{
						mapProofs.put(instanceId, proof);
					}
					failed=false;
				}
				finally
				{
					if (failed)
					{
						stopFlag.stop();
					}
				}
			}
			else
			{
				proof = null;
			}
			return proof;
		}
		
		private P runProver() throws BiuteeException
		{
			OperationsScript<Info, BasicNode> script = null;
			try
			{
				P proof = null;
				script = scriptQueue.take();
				try
				{
					proof = prover.prove(mapInstances.get(instanceId), script, classifierForSearch);
				}
				finally
				{
					scriptQueue.put(script);
				}
				return proof;
			}
			catch (InterruptedException e)
			{
				throw new BiuteeException("There was a problem with script queue. See nested exception.",e);
			}
		}
		private final int instanceId;
	}

	// input
	private final Dataset<I> dataset;
	private final List<OperationsScript<Info, BasicNode>> scripts; // already initialized
	private final LinearClassifier classifierForSearch;
	private final Prover<I, P> prover;
	private final int numberOfThreads;
	
	
	
	// internals
	private Map<Integer, I> mapInstances;
	private BlockingQueue<OperationsScript<Info, BasicNode>> scriptQueue;
	private Map<Integer, P> mapProofs;
	private StopFlag stopFlag = new StopFlag();
	
	// output
	private List<InstanceAndProof<I, P>> proofs = null;
	

	// logger
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DatasetProcessor.class);
}
