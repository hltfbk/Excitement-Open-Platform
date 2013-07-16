package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.RtePairsDataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.RtePairsProver;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.RtePairsResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairInstance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairProof;
import eu.excitementproject.eop.biutee.rteflow.systems.EndToEndSystem;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 16, 2013
 *
 */
public class RTEPairsETEFactory
{
	public static Dataset<THPairInstance> createDataset(ConfigurationParams configurationParams, TESystemEnvironment teSystemEnvironment) throws BiuteeException 
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
		}
		catch (ConfigurationException | ClassNotFoundException | IOException | TeEngineMlException | AnnotatorException | TreeCoreferenceInformationException e)
		{
			throw new BiuteeException("Failed to create dataset.",e);
		}
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

	
	public static Prover<THPairInstance,THPairProof> createProver(TESystemEnvironment teSystemEnvironment,EndToEndSystem.LemmatizerProvider lemmatizerProvider) throws BiuteeException
	{
		return new RtePairsProver(teSystemEnvironment,lemmatizerProvider);
	}
	
	
	public static ResultsFactory<THPairInstance,THPairProof> createResultsFactory() throws BiuteeException
	{
		return new RtePairsResultsFactory();
	}
}
