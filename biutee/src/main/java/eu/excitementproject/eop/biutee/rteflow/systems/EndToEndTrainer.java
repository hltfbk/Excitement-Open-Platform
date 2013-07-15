package eu.excitementproject.eop.biutee.rteflow.systems;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierTrainer;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Instance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Proof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Trainer;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.DefaultClassifierTrainer;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.Provider;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 * @param <I>
 * @param <P>
 */
public abstract class EndToEndTrainer<I extends Instance, P extends Proof> extends SystemInitialization
{
	public EndToEndTrainer(String configurationFileName, String configurationModuleName)
	{
		super(configurationFileName, configurationModuleName);
	}
	
	public void train() throws BiuteeException
	{
		logger.info("Start training.");
		logger.info("Loading dataset.");
		Dataset<I> dataset = createDataset();
		int numberOfthreads = retrieveNumberOfThreads();
		logger.info("Using "+numberOfthreads+" threads.");
		logger.info("Creating scripts.");
		List<OperationsScript<Info, BasicNode>> scripts = createScripts(numberOfthreads);
		try
		{
			logger.info("Creating trainer.");
			Trainer<I,P> trainer = new Trainer<I,P>(
					dataset,scripts,
					createClassifierGenerator(),createProver(),
					numberOfthreads,createResultsFactory(),
					createClassifierTrainer(),
					teSystemEnvironment.getFeatureVectorStructureOrganizer()
					);
			logger.info("Training.");
			trainer.train();
			logger.info("Training done.");
		}
		finally
		{
			for (OperationsScript<Info, BasicNode> script : scripts)
			{
				script.cleanUp();
			}
		}
	}
	
	

	protected abstract Dataset<I> createDataset() throws BiuteeException;
	protected abstract int retrieveNumberOfThreads() throws BiuteeException;
	protected abstract ClassifierGenerator createClassifierGenerator() throws BiuteeException;
	protected abstract Prover<I, P> createProver() throws BiuteeException;
	protected abstract ResultsFactory<I, P> createResultsFactory() throws BiuteeException;

	
	protected ClassifierTrainer createClassifierTrainer() throws BiuteeException
	{
		return new DefaultClassifierTrainer(teSystemEnvironment.getFeatureVectorStructureOrganizer());
	}

	protected List<OperationsScript<Info, BasicNode>> createScripts(int numberOfthreads) throws BiuteeException
	{
		try
		{
			ScriptsCreator scriptsCreator = new ScriptsCreator(configurationFile,teSystemEnvironment.getPluginRegistry(),numberOfthreads);
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
	protected class LemmatizerProvider implements Provider<Lemmatizer>
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
	
	private static final Logger logger = Logger.getLogger(EndToEndTrainer.class);
}
