package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumInstance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumProof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumProver;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.systems.EndToEndSystem;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;


/**
 * 
 * @author Asher Stern
 * @since Jul 22, 2013
 *
 */
public class RTESumETEFactory
{
	public static Dataset<RteSumInstance> createDataset(ConfigurationParams configurationParams, String serialized_parameterName, String rawData_parameterName, TESystemEnvironment teSystemEnvironment) throws BiuteeException
	{
		RTESumETEDatasetFactory datasetFactory = new RTESumETEDatasetFactory(configurationParams,serialized_parameterName,rawData_parameterName,teSystemEnvironment);
		datasetFactory.createDataset();
		return datasetFactory.getDataset();
	}
	
	public static int retrieveNumberOfThreads(ConfigurationParams configurationParams) throws BiuteeException
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
	
	
	public static Prover<RteSumInstance,RteSumProof> createProver(TESystemEnvironment teSystemEnvironment,EndToEndSystem.LemmatizerProvider lemmatizerProvider) throws BiuteeException
	{
		return new RteSumProver(teSystemEnvironment, lemmatizerProvider);
	}
	
	public static ResultsFactory<RteSumInstance,RteSumProof> createResultsFactory() throws BiuteeException
	{
		return new RteSumResultsFactory();
	}
}
