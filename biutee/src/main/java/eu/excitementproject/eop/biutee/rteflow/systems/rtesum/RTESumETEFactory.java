package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;

import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_SUM_PREPROCESS_SERIALIZATION_FILE_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumDatasetContents;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumInstance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.RtePairsDataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairInstance;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.ExtendedPairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairDataToExtendedPairDataConverter;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.RTESerializedPairsReader;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.PreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswersFileReader;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.DefaultAnswersFileReader;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 22, 2013
 *
 */
public class RTESumETEFactory
{
	public static RteSumDatasetContents createDataset(ConfigurationParams configurationParams, String serialized_parameterName, String rawData_parameterName, TESystemEnvironment teSystemEnvironment) throws BiuteeException 
	{
		File serFile = configurationParams.getFile(serialized_parameterName);
		logger.info("Reading all topic from serialization file: "+serFile.getPath());
		ObjectInputStream serStream = new ObjectInputStream(new FileInputStream(serFile));
		try
		{
			List<PreprocessedTopicDataSet> topics = (List<PreprocessedTopicDataSet>) serStream.readObject();
			
			//File datasetDir = configurationParams.getDirectory(RTE_SUM_DATASET_DIR_NAME);
			
			File datasetDir = retrieveDatasetDirAndSetFileSystemNames();
			
			File goldStandardFile = new File(datasetDir,fileSystemNames.getGoldStandardFileName());
			if (goldStandardFile.exists()&&goldStandardFile.isFile())
			{
				goldStandardFileName = goldStandardFile.getPath();
				logger.info("Retrieving gold-standard file: "+goldStandardFileName);
				AnswersFileReader gsReader = new DefaultAnswersFileReader();
				gsReader.setXml(goldStandardFileName);
				gsReader.read();
				goldStandardAnswers = gsReader.getAnswers();
			}
			else
			{
				logger.info("No gold-standard exist.");
				goldStandardFileName = null;
				goldStandardAnswers = null;
			}
		}
		finally
		{
			serStream.close();
		}
		
	}
	
	
	

	
	private static final Logger logger = Logger.getLogger(RTESumETEFactory.class);
}
