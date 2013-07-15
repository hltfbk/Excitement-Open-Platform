package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.AccuracyClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.RtePairsDataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.RtePairsProver;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.RtePairsResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairInstance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairProof;
import eu.excitementproject.eop.biutee.rteflow.systems.EndToEndTrainer;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
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
		try
		{
			if (args.length<1)throw new TeEngineMlException("No arguments. Enter configuration file name as argument.");
			
			String configurationFileName = args[0];
			new LogInitializer(configurationFileName).init();
			
			ExperimentManager.getInstance().start();
			ExperimentManager.getInstance().setConfigurationFile(configurationFileName);

			logger.info("RTEPairsETETrainer");

			
			RTEPairsETETrainer trainer = new RTEPairsETETrainer(configurationFileName, ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
			try
			{
				trainer.init();
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
			ExceptionUtil.logException(e, logger);
		}
	}

	public RTEPairsETETrainer(String configurationFileName, String configurationModuleName)
	{
		super(configurationFileName, configurationModuleName);
	}
	

	
	@Override
	protected Dataset<THPairInstance> createDataset() throws BiuteeException 
	{
		try
		{
			RTESerializedPairsReader pairsReader = new RTESerializedPairsReader(
					configurationParams.getFile(ConfigurationParametersNames.RTE_PAIRS_PREPROCESS_SERIALIZATION_FILE_NAME).getPath()
					);
			pairsReader.read();
			List<PairData> pairs = pairsReader.getPairsData();
			List<ExtendedPairData> extendedPairs = new ArrayList<>(pairs.size());
			for (PairData pair : pairs)
			{
				PairDataToExtendedPairDataConverter converter = new PairDataToExtendedPairDataConverter(pair,teSystemEnvironment);
				converter.convert();
				extendedPairs.add(converter.getExtendedPairData());
			}
			RtePairsDataset dataset = new RtePairsDataset(extendedPairs);
			return dataset;
		} catch (ConfigurationException | ClassNotFoundException | IOException | TeEngineMlException | AnnotatorException | TreeCoreferenceInformationException e)
		{
			throw new BiuteeException("Failed to create dataset.",e);
		}
	}

	@Override
	protected int retrieveNumberOfThreads() throws BiuteeException
	{
		try
		{
			return configurationParams.getInt(ConfigurationParametersNames.RTE_ENGINE_NUMBER_OF_THREADS_PARAMETER_NAME);
		}
		catch (ConfigurationException e)
		{
			throw new BiuteeException("Failed to read number of threads.",e);
		}
	}
	
	@Override
	protected ClassifierGenerator createClassifierGenerator() throws BiuteeException
	{
		return new AccuracyClassifierGenerator(teSystemEnvironment.getFeatureVectorStructureOrganizer());
	}
	
	@Override
	protected Prover<THPairInstance,THPairProof> createProver() throws BiuteeException
	{
		return new RtePairsProver(teSystemEnvironment,lemmatizerProvider);
	}
	
	
	protected ResultsFactory<THPairInstance,THPairProof> createResultsFactory() throws BiuteeException
	{
		return new RtePairsResultsFactory();
	}


	private static final Logger logger = Logger.getLogger(RTEPairsETETrainer.class);
}
