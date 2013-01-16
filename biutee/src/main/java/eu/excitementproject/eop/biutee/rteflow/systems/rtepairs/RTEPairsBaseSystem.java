package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.macro.GlobalPairInformation;
import eu.excitementproject.eop.biutee.rteflow.macro.InitialFeatureVectorUtility;
import eu.excitementproject.eop.biutee.rteflow.macro.InitializationTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.biutee.rteflow.systems.RTESystemsUtils;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemInitialization;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.transformations.datastructures.DsUtils;
import eu.excitementproject.eop.transformations.utilities.DatasetParameterValueParser;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * A base class for {@link RTEPairsMultiThreadTrainer} and {@link RTEPairsMultiThreadTester},
 * as well as some other classes.
 * 
 * @author Asher Stern
 * @since 29 July - 2012
 *
 */
public class RTEPairsBaseSystem extends SystemInitialization
{
	public RTEPairsBaseSystem(String configurationFileName, String configurationModuleName)
	{
		super(configurationFileName, configurationModuleName);
	}
	
	
	/**
	 * 
	 * Just some explanations about what's going on:
	 * The configuration file contains a parameter with the file name of the dataset. This parameter may contain also
	 * several files, not only a single file. When it contains several files, it should also contain a "name" of dataset for each file.
	 * This way, the dataset file of RTE1 should be named with a name for RTE1, while dataset file of RTE2 should be named with
	 * a name for RTE2, and so forth. These names are used as features. So, one feature in the feature vector is the name of the
	 * dataset from which the given T-H pair came from.
	 * <BR>In test, the dataset names should exist in the parameter, even if no dataset file corresponds to them, in order
	 * to preserve consistency of features between train and test.
	 * <P>
	 * Example:<BR>
	 * In training, the parameter value might look like "RTE1=rte1-dev.xml;RTE2=rte2-dev.xml"<BR>
	 * In test, it might look like "RTE1;RTE2=rte2-test.xml"<BR>
	 * <P>
	 * 
	 * Now, the flow is as follows: Here the system reads this parameter, and gets the dataset names. Here the system
	 * sets the value of "dataset name" for each T-H pair. In addition, it updates the {@link FeatureVectorStructureOrganizer} to
	 * hold features for each dataset name.
	 * Next, in {@link PairProcessor}, the dataset name which is part of the {@link ExtendedPairData} which is given
	 * to the {@link PairProcessor} in the constructor - this dataset name is given as part of {@link GlobalPairInformation}.
	 * Next, this {@link GlobalPairInformation} is forward to the {@link TextTreesProcessor} - using
	 * {@link InitializationTextTreesProcessor#setGlobalPairInformation(GlobalPairInformation)}.
	 * Next, This {@link GlobalPairInformation} is forwarded to {@link InitialFeatureVectorUtility}, using the method
	 * {@link InitialFeatureVectorUtility#setGlobalPairInformation(GlobalPairInformation)}.
	 * Next, in {@link InitialFeatureVectorUtility}, the appropriate feature index for the dataset name is located, in
	 * {@link FeatureVectorStructureOrganizer}. Recall that <B>here</B>, in this method, it was set!
	 * Then, this feature is assigned value of 1.0.
	 * 
	 * @return
	 * @throws ConfigurationException
	 * @throws TeEngineMlException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected List<PairData> readDatasetAndUpdateFeatureVectorStructure() throws ConfigurationException, TeEngineMlException, FileNotFoundException, IOException, ClassNotFoundException
	{
		DatasetParameterValueParser parameterParser = constructDatasetParameterParser();
		updateFeatureVectorStructure(parameterParser);
		return readData(parameterParser);
	}
	
	protected DatasetParameterValueParser constructDatasetParameterParser() throws ConfigurationException, TeEngineMlException
	{
		String parameterValue = configurationParams.get(ConfigurationParametersNames.RTE_PAIRS_PREPROCESS_SERIALIZATION_FILE_NAME);
		
		DatasetParameterValueParser parameterParser = new DatasetParameterValueParser(parameterValue);
		parameterParser.parse();
		
		return parameterParser;
	}
	
	/**
	 * This method is described in the JavaDoc of {@link #readDatasetAndUpdateFeatureVectorStructure()}
	 * @see #readDatasetAndUpdateFeatureVectorStructure()
	 * @param parameterParser
	 * @throws TeEngineMlException
	 */
	protected void updateFeatureVectorStructure(DatasetParameterValueParser parameterParser) throws TeEngineMlException
	{
		if (parameterParser.getSingleFileNameWithNoDatasetName()!=null)
		{
			// do nothing
			logger.info("No dataset name was specified. All pairs in train and test are treated as coming from the same dataset.");
		}
		else
		{
			LinkedHashSet<String> datasetNames = parameterParser.getDatasetNames();
			teSystemEnvironment.getFeatureVectorStructureOrganizer().setDynamicGlobalFeatureNames(datasetNames);
			logger.info("The following dataset names were specified: "+DsUtils.collectionToString(datasetNames));
		}
		
	}

	/**
	 * This method is described in the JavaDoc of {@link #readDatasetAndUpdateFeatureVectorStructure()}
	 * @see #readDatasetAndUpdateFeatureVectorStructure()
	 * 
	 * @param parameterParser
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws TeEngineMlException
	 */
	protected List<PairData> readData(DatasetParameterValueParser parameterParser) throws FileNotFoundException, IOException, ClassNotFoundException, TeEngineMlException
	{
		List<PairData> listOfAllPairs = null;
		
		if (parameterParser.getSingleFileNameWithNoDatasetName()!=null)
		{
			listOfAllPairs = RTESystemsUtils.readSingleFilePairsData(parameterParser.getSingleFileNameWithNoDatasetName());
		}
		else
		{
			listOfAllPairs = new ArrayList<PairData>();
			ValueSetMap<String, String> mapDatasetNameToFileName = parameterParser.getMapDatasetNameToFileName();
			// sanity
			if (! parameterParser.getDatasetNames().equals(mapDatasetNameToFileName.keySet().getMutableSetCopy())) throw new TeEngineMlException("BUG: Something was wrong with the dataset parameter: parameterParser.getDatasetNames() =["+ DsUtils.collectionToString(parameterParser.getDatasetNames())+"] while parameterParser.getMapDatasetNameToFileName().keySet() = ["+ DsUtils.collectionToString(mapDatasetNameToFileName.keySet().getMutableCollectionCopy())+"]");
			for (String datasetName : parameterParser.getDatasetNames())
			{
				for (String fileName : mapDatasetNameToFileName.get(datasetName))
				{
					List<PairData> pairs = RTESystemsUtils.readSingleFilePairsData(fileName);
					List<PairData> pairsWithDatasetName = new ArrayList<PairData>(pairs.size());
					for (PairData pair : pairs)
					{
						pairsWithDatasetName.add(
								new PairData(pair.getPair(), pair.getTextTrees(), pair.getHypothesisTree(), pair.getMapTreesToSentences(), pair.getCoreferenceInformation(), datasetName));
					}
					listOfAllPairs.addAll(pairsWithDatasetName);
				} // end for each file name
			}// end for each dataset name
		}

		return listOfAllPairs;
	}
	
	
	protected void checkCorrectnessOfDatasetName(boolean specificed) throws TeEngineMlException
	{
		boolean empty = isEmpty(teSystemEnvironment.getFeatureVectorStructureOrganizer().getDynamicGlobalFeatures());
		if ( (specificed) && (empty) )
		{
			throw new TeEngineMlException("A dataset name was specified, but in the initialization (configuration file) no dataset names were specified.");
		}
		if ( (!specificed) && (!empty) )
		{
			throw new TeEngineMlException("Since dataset names were specified during initialization (based on the configuration file parameter), you should specify a dataset name for each pair.");
		}
		
	}

	private boolean isEmpty(ImmutableMap<?,?> immutableMap)
	{
		boolean ret = true;
		if  (immutableMap!=null)
		{
			if (immutableMap.keySet().size()>0)
			{
				ret = false;
			}
		}
		return ret;
	}
	
	private static final Logger logger = Logger.getLogger(RTEPairsBaseSystem.class);
}
