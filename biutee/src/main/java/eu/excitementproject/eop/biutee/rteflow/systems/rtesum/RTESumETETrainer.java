package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;

import java.io.IOException;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.F1ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumInstance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumProof;
import eu.excitementproject.eop.biutee.rteflow.systems.EndToEndTrainer;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemMain;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 22, 2013
 *
 */
public class RTESumETETrainer extends EndToEndTrainer<RteSumInstance, RteSumProof>
{
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		new SystemMain()
		{
			@Override
			protected void run(String[] args) throws BiuteeException
			{
				logger = Logger.getLogger(RTESumETETrainer.class);
				RTESumETETrainer trainer = new RTESumETETrainer(configurationFileName, ConfigurationParametersNames.RTE_SUM_TRAIN_AND_TEST_MODULE_NAME);
				try
				{
					trainer.init();
					try
					{
						trainer.train();
					}
					finally
					{
						trainer.cleanUp();
					}
				} catch (TeEngineMlException
						| PluginAdministrationException
						| ConfigurationException | LemmatizerException
						| IOException e)
				{
					throw new BiuteeException("Failed to run",e);
				}
			}
		}.main(RTESumETETrainer.class, args);
	}


	public RTESumETETrainer(String configurationFileName, String configurationModuleName)
	{
		super(configurationFileName, configurationModuleName);
		if (null==logger){logger = Logger.getLogger(RTESumETETrainer.class);}
	}


	@Override
	protected Dataset<RteSumInstance> createDataset() throws BiuteeException
	{
		return RTESumETEFactory.createDataset(configurationParams, ConfigurationParametersNames.RTE_SERIALIZED_DATASET_FOR_TRAINING, ConfigurationParametersNames.RTESUM_DATASET_FOR_TRAINING, teSystemEnvironment);
	}


	@Override
	protected ClassifierGenerator createClassifierGenerator() throws BiuteeException
	{
		return new F1ClassifierGenerator(teSystemEnvironment.getFeatureVectorStructureOrganizer());
	}


	@Override
	protected Prover<RteSumInstance, RteSumProof> createProver() throws BiuteeException
	{
		return RTESumETEFactory.createProver(teSystemEnvironment, lemmatizerProvider);
	}


	@Override
	protected ResultsFactory<RteSumInstance, RteSumProof> createResultsFactory() throws BiuteeException
	{
		return RTESumETEFactory.createResultsFactory();
	}


	@Override
	protected int retrieveNumberOfThreads() throws BiuteeException
	{
		return RTESumETEFactory.retrieveNumberOfThreads(configurationParams);
	}
	
	private static Logger logger = null;
}
