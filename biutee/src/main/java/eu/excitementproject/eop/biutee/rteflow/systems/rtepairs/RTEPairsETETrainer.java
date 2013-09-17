package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;


import java.io.IOException;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.AccuracyClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairInstance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairProof;
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
 * @since Jul 15, 2013
 *
 */
public class RTEPairsETETrainer extends EndToEndTrainer<THPairInstance,THPairProof>
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
				logger = Logger.getLogger(RTEPairsETETrainer.class);
				RTEPairsETETrainer trainer = new RTEPairsETETrainer(configurationFileName, ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
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
		}.main(RTEPairsETETrainer.class, args);
	}

	public RTEPairsETETrainer(String configurationFileName, String configurationModuleName)
	{
		super(configurationFileName, configurationModuleName);
		if (null==logger){logger = Logger.getLogger(RTEPairsETETrainer.class);}
	}
	

	
	@Override
	protected Dataset<THPairInstance> createDataset() throws BiuteeException 
	{
		return RTEPairsETEFactory.createDataset(configurationParams, ConfigurationParametersNames.RTE_SERIALIZED_DATASET_FOR_TRAINING ,teSystemEnvironment);
	}

	@Override
	protected int retrieveNumberOfThreads() throws BiuteeException
	{
		return RTEPairsETEFactory.retrieveNumberOfThreads(configurationParams);
	}
	
	@Override
	protected ClassifierGenerator createClassifierGenerator() throws BiuteeException
	{
		return new AccuracyClassifierGenerator(teSystemEnvironment.getFeatureVectorStructureOrganizer());
	}
	
	@Override
	protected Prover<THPairInstance,THPairProof> createProver() throws BiuteeException
	{
		return RTEPairsETEFactory.createProver(teSystemEnvironment,lemmatizerProvider);
	}
	
	@Override
	protected ResultsFactory<THPairInstance,THPairProof> createResultsFactory() throws BiuteeException
	{
		return RTEPairsETEFactory.createResultsFactory();
	}

	private static Logger logger = null;
}
