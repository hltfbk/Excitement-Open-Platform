package ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.classifiers.Classifier;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierUtils;
import ac.biu.nlp.nlp.engineml.classifiers.LabeledSample;
import ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier;
import ac.biu.nlp.nlp.engineml.datastructures.TablePrinter;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.operations.specifications.Specification;
import ac.biu.nlp.nlp.engineml.plugin.PluginAdministrationException;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TextTreesProcessor;
import ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames;
import ac.biu.nlp.nlp.engineml.rteflow.systems.RTESystemsUtils;
import ac.biu.nlp.nlp.engineml.rteflow.systems.SystemInitialization;
import ac.biu.nlp.nlp.engineml.script.OperationsScript;
import ac.biu.nlp.nlp.engineml.script.ScriptException;
import ac.biu.nlp.nlp.engineml.script.ScriptFactory;
import ac.biu.nlp.nlp.engineml.utilities.LogInitializer;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.common.datastructures.MapsBasedTable;
import eu.excitementproject.eop.common.datastructures.Table;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;

/**
 * An entry point for "system" flow for RTE-pairs test phase.
 * @deprecated Use {@link RTEPairsMultiThreadTester}.
 *
 *
 * @see RTEPairsMultiThreadTester
 * 
 * 
 * @see TextTreesProcessor
 * 
 * @author Asher Stern
 * @since Feb 17, 2011
 *
 */
@Deprecated
public class RTEPairsSingleThreadTester extends SystemInitialization
{
	public static void main(String[] args)
	{
		try
		{
			if (args.length<1)
				throw new TeEngineMlException("Need first argument as configuration file name.");
			
			String configurationFileName = args[0];
			new LogInitializer(configurationFileName).init();
			
			logger.info("RTEPairsSingleThreadTester");
			
			RTEPairsSingleThreadTester tester = new RTEPairsSingleThreadTester(configurationFileName);
			tester.runTest();
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
			ExceptionUtil.logException(e, logger);
		}
	}
	
	public RTEPairsSingleThreadTester(String configurationFileName) throws ConfigurationFileDuplicateKeyException, ConfigurationException
	{
		super(configurationFileName,ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
	}
	
	public void runTest() throws ConfigurationFileDuplicateKeyException, ConfigurationException, LemmatizerException, TeEngineMlException, FileNotFoundException, IOException, ClassNotFoundException, AnnotatorException, PluginAdministrationException
	{
		init();
		try
		{
			test();
		}
		finally
		{
			cleanUp();
		}
	}
	
	private void test() throws TeEngineMlException, FileNotFoundException, ConfigurationException, IOException, ClassNotFoundException, AnnotatorException, PluginAdministrationException
	{
		logger.info("Starting the test.");
		pairsResults = new LinkedHashMap<ExtendedPairData, PairResult>();
		try
		{
			Table<String, Long> timeTable = new MapsBasedTable<String, Long>();
			Table<String, Double> costTable = new MapsBasedTable<String, Double>();
			Table<String, Long> expandedTable = new MapsBasedTable<String, Long>();
			Table<String, Long> generatedeTable = new MapsBasedTable<String, Long>();
//
//			Set<Integer> badPairs = new HashSet<Integer>();
////			badPairs.add(86);
////			badPairs.add(236);
//			badPairs.add(346);
////			badPairs.add(462);
//			badPairs.add(548);
////			badPairs.add(549);
//			badPairs.add(555);

			
			for (ExtendedPairData pairData : pairsData)
			{
				logger.info("Working on pair: "+pairData.getPair().getId());
				
//				PairProcessor pairProcessor =
//					new PairProcessor(pairData, classifierForSearch, lemmatizer, script, unigramProbabilityEstimation, ruleBasesToRetrieveMultiWords);

//				if (badPairs.contains(pairData.getPair().getId()))
//				{
//					logger.info("skipping pair "+pairData.getPair().getId());
//					continue;
//				}
				
//				
				PairProcessorForStatistics pairProcessor =
					new PairProcessorForStatistics(pairData, classifierForSearch, lemmatizer, script, teSystemEnvironment,costTable,timeTable,expandedTable,generatedeTable);
				
				pairProcessor.process();
				
				pairsResults.put(pairData, new PairResult(pairProcessor.getBestTree(), pairProcessor.getBestTreeSentence(), pairProcessor.getBestTreeHistory()));
			}
			
			logger.info("All pairs done.");
			
			logger.info("Writing statistics");
			
			printTable("cost_statistics.csv",costTable);
			printTable("time_statistics.csv",timeTable);
			printTable("expanded_statistics.csv",expandedTable);
			printTable("generated_statistics.csv",generatedeTable);
			
			logger.info("Writing results to log");
			printPairsResults();
		}
		catch(RuleBaseException e)
		{
			throw new TeEngineMlException("Processing pair failure. See nested exception",e);
		}
		catch (OperationException e)
		{
			throw new TeEngineMlException("Processing pair failure. See nested exception",e);
		}
		catch (ClassifierException e)
		{
			throw new TeEngineMlException("Processing pair failure. See nested exception",e);
		}
		catch (ScriptException e)
		{
			throw new TeEngineMlException("Processing pair failure. See nested exception",e);
		}

	}
	
	private <K,V> void printTable(String filename, Table<K, V> table) throws FileNotFoundException
	{
		PrintWriter writer = new PrintWriter(new File(filename));
		try
		{
			TablePrinter<K,V> costPrinter = new TablePrinter<K, V>(writer, table);
			costPrinter.print();
		}
		finally
		{
			writer.close();
		}

	}
	
	private void printPairsResults() throws ClassifierException, FileNotFoundException, ConfigurationException, IOException, ClassNotFoundException, TeEngineMlException, PluginAdministrationException
	{
		int truetrue = 0;
		int truefalse = 0;
		int falsetrue = 0;
		int falsefalse = 0;
		Vector<LabeledSample> listLabeledSamples = new Vector<LabeledSample>(this.pairsResults.keySet().size());
		for (ExtendedPairData pairData : this.pairsResults.keySet())
		{
			PairResult pairResult = this.pairsResults.get(pairData);
			boolean realClassification = pairData.getPair().getBooleanClassificationType();
			double classifierClassificationValue = classifierForPredictions.classify(pairResult.getBestTree().getFeatureVector());
			boolean classifierClassification = ClassifierUtils.classifierResultToBoolean(classifierClassificationValue);
			if ( (true==realClassification) && (true==classifierClassification) )truetrue++;
			if ( (true==realClassification) && (false==classifierClassification) )truefalse++;
			if ( (false==realClassification) && (true==classifierClassification) )falsetrue++;
			if ( (false==realClassification) && (false==classifierClassification) )falsefalse++;
			
			logger.info(pairData.getPair().getId()+": real="+realClassification+", classification="+classifierClassification+" ("+String.format("%-2.5f",classifierClassificationValue)+")");
			LabeledSample labeledSample = new LabeledSample(pairResult.getBestTree().getFeatureVector(), realClassification);
			listLabeledSamples.add(labeledSample);
			logger.info(ClassifierUtils.printLabeledSample(labeledSample));
			logger.info("Sentence: "+pairResult.getBestTreeSentence());
			logger.info("Hypothesis: "+pairData.getPair().getHypothesis());
			logger.info("History:");
			for (Specification spec : pairResult.getBestTreeHistory().getSpecifications())
			{
				logger.info(spec.toString());
			}
			logger.info(StringUtil.generateStringOfCharacter('-', 100));
		}
		

		logger.info(StringUtil.generateStringOfCharacter('-', 100));
		Vector<LabeledSample> normalizedSamples = new Vector<LabeledSample>(listLabeledSamples.size());
		for (LabeledSample sample : listLabeledSamples)
		{
			normalizedSamples.add(classifierForPredictions.getNormalizedSample(sample));
		}
		logger.info("Printing normalized samples as SVM LIGHT format:");
		ClassifierUtils.printSamplesAsSvmLightInput(normalizedSamples, logger);

		
		
		logger.info("Avarages:");
		ClassifierUtils.printAvarages(listLabeledSamples);
		logger.info(StringUtil.generateStringOfCharacter('-', 100));
		logger.info("real=true, classifier=true: "+truetrue);
		logger.info("real=true, classifier=false: "+truefalse);
		logger.info("real=false, classifier=true: "+falsetrue);
		logger.info("real=false, classifier=false: "+falsefalse);
		
		double accuracy =  ((double)(truetrue+falsefalse))/( (double)(truetrue+truefalse+falsetrue+falsefalse) );
		logger.info("Accuracy = "+String.format("%-4.4f", accuracy));
		
		RTESystemsUtils.printAccuraciesOfClassifiers(configurationFile, listLabeledSamples, teSystemEnvironment.getFeatureVectorStructureOrganizer());
	}
	
	@Override
	protected void init() throws ConfigurationFileDuplicateKeyException, ConfigurationException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		super.init();
		try
		{
			this.script = new ScriptFactory(this.configurationFile,teSystemEnvironment.getPluginRegistry()).getDefaultScript();
			logger.info("Operation Script class = "+script.getClass().getName());
			logger.info("Initializing Operations Script...");
			script.init();
			scriptInitialized = true;
			logger.info("Operation Script initialized.");
			
			completeInitializationWithScript(script);

			logger.info("Creating classifiers.");
//			File serializedSamples = rteTestParams.getFile(ConfigurationParametersNames.RTE_PAIRS_TEST_SERIALIZED_SAMPLES_NAME);
//			this.classifier = buildClassifier(serializedSamples);
			this.classifierForSearch = RTESystemsUtils.searchClassifierForTest(configurationParams, teSystemEnvironment.getFeatureVectorStructureOrganizer());
			logger.info("ClassifierForSearch:");
			logger.info(this.classifierForSearch.descriptionOfTraining());
			
			this.classifierForPredictions = RTESystemsUtils.newClassiferForPredictionsForTest(configurationParams, teSystemEnvironment.getFeatureVectorStructureOrganizer());
			logger.info("classifierForPredictions:\n"+this.classifierForPredictions.descriptionOfTraining());
			
			/// Read the file in the same format saved by {@link RTEPairsPreProcessor#writeToSerializationFile}
			logger.info("Reading serialization pairs.");
			File serializedPairs = configurationParams.getFile(ConfigurationParametersNames.RTE_PAIRS_PREPROCESS_SERIALIZATION_FILE_NAME);
			RTESerializedPairsReader reader = new RTESerializedPairsReader(serializedPairs.getPath());
			reader.read();
			List<PairData> originalPairsData = reader.getPairsData();
			
			logger.info("Converting pairs to ExtendedPairData objects.");
			pairsData = new ArrayList<ExtendedPairData>(originalPairsData.size());
			for (PairData originalPairData : originalPairsData)
			{
				PairDataToExtendedPairDataConverter converter = new PairDataToExtendedPairDataConverter(originalPairData,this.teSystemEnvironment);
				converter.convert();
				pairsData.add(converter.getExtendedPairData());
			}
		}
		catch (ConfigurationException e)
		{
			throw new TeEngineMlException("configuration file problem.",e);
		}
		catch (MalformedURLException e)
		{
			throw new TeEngineMlException("Initialization problem. See nested exception.",e);
		}
		catch (FileNotFoundException e)
		{
			throw new TeEngineMlException("Initialization problem. See nested exception.",e);
		}
		catch (IOException e)
		{
			throw new TeEngineMlException("Initialization problem. See nested exception.",e);
		}
		catch (ClassNotFoundException e)
		{
			throw new TeEngineMlException("Initialization problem. See nested exception.",e);
		}
		catch (OperationException e)
		{
			throw new TeEngineMlException("Initialization problem. See nested exception.",e);
		}
		catch (ClassifierException e)
		{
			throw new TeEngineMlException("Classifier initialization failed.",e);
		}
		catch (TreeCoreferenceInformationException e)
		{
			throw new TeEngineMlException("Initialization problem. See nested exception.",e);
		}
		catch (AnnotatorException e)
		{
			throw new TeEngineMlException("Initialization problem. See nested exception.",e);
		}
	}
	
	protected void cleanUp()
	{
		super.cleanUp();
		if(scriptInitialized)if(script!=null)script.cleanUp();
	}
	
	
	
	
//	private Classifier buildClassifier(File serializedSamples) throws ClassifierException, IOException, ClassNotFoundException
//	{
//		Classifier ret = new ClassifierFactory().getDefaultClassifier();
//		ObjectInputStream serStream = new ObjectInputStream(new FileInputStream(serializedSamples));
//		try
//		{
//			@SuppressWarnings("unchecked")
//			Vector<LabeledSample> samples = (Vector<LabeledSample>) serStream.readObject();
//			ret.train(samples);
//			ret.setFeaturesNames( ClassifierUtils.extendFeatureNames(Feature.toMapOfNames(), script.getRuleBasesNames()));
//			
//			return ret;
//		}
//		finally
//		{
//			if (serStream!=null)
//				serStream.close();
//		}
//	}
	
	
	
	

	private ArrayList<ExtendedPairData> pairsData;
	private LinearClassifier classifierForSearch;
	private Classifier classifierForPredictions;
	private OperationsScript<Info, BasicNode> script;
	private boolean scriptInitialized = false;
	
	private LinkedHashMap<ExtendedPairData, PairResult> pairsResults;
	
	private static Logger logger = Logger.getLogger(RTEPairsSingleThreadTester.class);
}
