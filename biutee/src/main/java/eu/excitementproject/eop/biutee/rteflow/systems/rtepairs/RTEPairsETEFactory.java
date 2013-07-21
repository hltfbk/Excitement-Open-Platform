package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
import eu.excitementproject.eop.common.utilities.Utils;
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
	public static Dataset<THPairInstance> createDataset(ConfigurationParams configurationParams, String parameterName, TESystemEnvironment teSystemEnvironment) throws BiuteeException 
	{
		try
		{
			File serializedDataFile = configurationParams.getFile(parameterName);
			logger.info("Loading dataset from serialization file :"+serializedDataFile.getPath());
			RTESerializedPairsReader pairsReader = new RTESerializedPairsReader(
					serializedDataFile.getPath()
					);
			pairsReader.read();
			List<PairData> pairs = pairsReader.getPairsData();
			logger.info("Loading dataset from serialization file - done.");
			logger.info("Converting pairs into extended pairs. Annotation take place here. This might take some time...");
			List<ExtendedPairData> extendedPairs = new ArrayList<>(pairs.size());
			int pairCounter=0;
			int pairs_size = pairs.size();
			for (PairData pair : pairs)
			{
				++pairCounter;
				
				// log
				Integer idInt = pair.getPair().getId();
				String id = "unknown id";
				if (idInt!=null) id = String.valueOf(idInt.intValue());
				logger.info("Converting a pair ("+pairCounter+" out of "+pairs_size+"), id = "+id+".");
					
				// convert
				PairDataToExtendedPairDataConverter converter = new PairDataToExtendedPairDataConverter(pair,teSystemEnvironment);
				converter.convert();
				if (logger.isDebugEnabled())
				{
					logger.debug("Converting a pair - done. Memory in use = "+Utils.stringMemoryUsedInMB());
				}
				extendedPairs.add(converter.getExtendedPairData());

			}
			logger.info("All pairs have been converted.");
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
	
	private static final Logger logger = Logger.getLogger(RTEPairsETEFactory.class);
}
