package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.F1ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumInstance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumProof;
import eu.excitementproject.eop.biutee.rteflow.systems.EndToEndTrainer;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
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
		
		try
		{
			if (args.length<1)throw new TeEngineMlException("No arguments. Enter configuration file name as argument.");
			
			String configurationFileName = args[0];
			new LogInitializer(configurationFileName).init();
			logger = Logger.getLogger(RTESumETETrainer.class);
			
			ExperimentManager.getInstance().start();
			ExperimentManager.getInstance().setConfigurationFile(configurationFileName);

			logger.info(RTESumETETrainer.class.getSimpleName());

			
			RTESumETETrainer trainer = new RTESumETETrainer(configurationFileName, ConfigurationParametersNames.RTE_SUM_TRAIN_AND_TEST_MODULE_NAME);
			trainer.init();
			try
			{
				trainer.train();
			}
			finally
			{
				trainer.cleanUp();
			}
			
			
			boolean experimentManagedSucceeded = ExperimentManager.getInstance().save();
			logger.info("ExperimentManager save "+(experimentManagedSucceeded?"succeeded":"failed")+".");
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
			if (logger!=null)
			{
				ExceptionUtil.logException(e, logger);
			}
		}
		

	}


	public RTESumETETrainer(String configurationFileName, String configurationModuleName)
	{
		super(configurationFileName, configurationModuleName);
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
