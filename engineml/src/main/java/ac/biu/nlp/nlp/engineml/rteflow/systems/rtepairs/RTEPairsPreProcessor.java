package ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.PREPROCESS_DO_NER;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.PREPROCESS_DO_TEXT_NORMALIZATION;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.RTE_PAIRS_PREPROCESS_ANNOTATED;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.RTE_PAIRS_PREPROCESS_DATASET_FILE_NAME;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.RTE_PAIRS_PREPROCESS_MODULE_NAME;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.RTE_PAIRS_PREPROCESS_SERIALIZATION_FILE_NAME;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.rteflow.preprocess.Instruments;
import ac.biu.nlp.nlp.engineml.rteflow.preprocess.InstrumentsFactory;
import ac.biu.nlp.nlp.engineml.utilities.LogInitializer;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.ParserRunException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.DefaultRTEMainReader;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainReader;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainReaderException;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.lap.biu.en.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.SentenceSplitterException;

/**
 * 
 * Reads the data set file of "RTE pairs" which is the main task of RTE 1-5, makes preprocessing
 * and writes the data to a serialization file.
 * The preprocessing includes:
 * <UL>
 * <LI>Text normalization</LI>
 * <LI>Sentence splitting</LI>
 * <LI>parsing</LI>
 * <LI>co reference resolution</LI>
 * <LI>Named entity recognition</LI>
 * </UL>
 * <P>
 * The serialization file is built as follows:
 * pair, true, pair, true, pair, true ... pair false
 * <BR>
 * "pair" is {@link PairData}. true/false are native booleans.
 * To read the file, read each time one object and one boolean. Continue reading until the boolean
 * is false.
 * 
 * @see RTESerializedPairsReader
 * 
 * @author Asher Stern
 * @since Feb 3, 2011
 *
 */
public class RTEPairsPreProcessor
{

	/**
	 * 
	 * @param args one argument - a configuration file name.
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length<1) throw new Exception("no arguments.");
			String configurationFileName = args[0];
			new LogInitializer(configurationFileName).init();
			
			logger.info("RTEPairsPreProcessor");
			
			RTEPairsPreProcessor application = new RTEPairsPreProcessor(configurationFileName);
			application.preprocess();
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
			ExceptionUtil.logException(e, logger);
		}

	}
	
	public RTEPairsPreProcessor(String configurationFileName)
	{
		this.configurationFileName = configurationFileName;
	}
	
	public void preprocess() throws ConfigurationFileDuplicateKeyException, ConfigurationException, RTEMainReaderException, TeEngineMlException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, FileNotFoundException, IOException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException
	{
		logger.info("Starting RTEPairsPreprocessor.");
		logger.info("Reading configuration file.");
		readConfigurationFile();
		logger.info("Reading dataset.");
		readDatasetFile();
		logger.info("pre-processing dataset.");
		makePreprocessing();
		logger.info("writing objects to serialization file.");
		writeToSerializationFile();
		logger.info("pre-process done.");
	}
	
	private void readConfigurationFile() throws ConfigurationFileDuplicateKeyException, ConfigurationException, NumberFormatException, TeEngineMlException, ParserRunException, NamedEntityRecognizerException, TextPreprocessorException
	{
		configurationFile = new ConfigurationFile(this.configurationFileName);
		configurationFile.setExpandingEnvironmentVariables(true);
		ConfigurationParams params = configurationFile.getModuleConfiguration(RTE_PAIRS_PREPROCESS_MODULE_NAME);
		datasetFile = params.get(RTE_PAIRS_PREPROCESS_DATASET_FILE_NAME);
		
		instruments = new InstrumentsFactory().getDefaultInstruments(params);
		
		annotated = params.getBoolean(RTE_PAIRS_PREPROCESS_ANNOTATED);
		preprocessedPairsSerFileName = params.get(RTE_PAIRS_PREPROCESS_SERIALIZATION_FILE_NAME);
		
		if (params.containsKey(PREPROCESS_DO_NER))
			doNer = params.getBoolean(PREPROCESS_DO_NER);
		else
			doNer = true;
	
		if (params.containsKey(PREPROCESS_DO_TEXT_NORMALIZATION))
			doTextNormalization = params.getBoolean(PREPROCESS_DO_TEXT_NORMALIZATION);
		else 
			doTextNormalization = true;
	}
	
	private void readDatasetFile() throws RTEMainReaderException
	{
		RTEMainReader reader = new DefaultRTEMainReader();
		reader.setXmlFile(new File(datasetFile));
		if (annotated)
		{
			reader.setHasClassification();
		}
		reader.read();
		Map<Integer,TextHypothesisPair> pairsMap = reader.getMapIdToPair();
		pairs = new ArrayList<TextHypothesisPair>(pairsMap.size());
		Set<Integer> pairsIds = pairsMap.keySet();
		Integer[] pairsIdsArray = Utils.collectionToArray(pairsIds, new Integer[0]);
		Arrays.sort(pairsIdsArray);
		for (Integer pairId : pairsIdsArray)
		{
			pairs.add(pairsMap.get(pairId));
		}
	}
	
	private void makePreprocessing() throws TeEngineMlException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException
	{
		PairsPreProcessor preProcessor = null;
		preProcessor = new PairsPreProcessor(pairs, instruments);
		
		preProcessor.setProcessingNamedEntities(doNer);
		preProcessor.setMakingTextNormalization(doTextNormalization);
		
		preProcessor.process();
		pairsData = preProcessor.getPairsData();
	}
	
	/**
	 * Write the file in the format expected by {@link RTESerializedPairsReader#read()}
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void writeToSerializationFile() throws FileNotFoundException, IOException
	{
		logger.info("Writing to serialization file...");
		File serFile = new File(preprocessedPairsSerFileName);
		ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(serFile));
		try
		{
			boolean firstIteration = true;
			for (PairData pair : pairsData)
			{
				logger.info("Writing pair: "+pair.getPair().getId());
				if (firstIteration)
				{
					firstIteration = false;
				}
				else
				{
					outputStream.writeBoolean(true);
					outputStream.reset();
				}
				
				outputStream.writeObject(pair);
			}
			outputStream.writeBoolean(false);
			
			logger.info("Writing to serialization file done.");
		}
		finally
		{
			if (outputStream!=null)
				outputStream.close();
		}
		
	}
	
	
	private String configurationFileName;
	private ConfigurationFile configurationFile;
	private Instruments<Info,BasicNode> instruments;
	private String datasetFile;
	private boolean annotated;
	private String preprocessedPairsSerFileName;
	private boolean doNer = true;
	private boolean doTextNormalization = true;
	
	private List<TextHypothesisPair> pairs = null;
	private List<PairData> pairsData;

	private static Logger logger = Logger.getLogger(RTEPairsPreProcessor.class);
}
