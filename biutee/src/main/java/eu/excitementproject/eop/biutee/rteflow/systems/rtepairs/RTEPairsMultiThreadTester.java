package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.RTESystemsUtils;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemInitialization;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * TODO - some code duplicates with {@link RTEPairsSingleThreadTester} should be merged.
 * 
 * <P>
 * This class is the entry point of the "system" flow for RTE-pairs data-set test phase.
 * See developer guide about "system" flow, "macro" flow and "micro" flow.
 * <BR>
 * Note that the macro flow starts in {@link TextTreesProcessor}, so if you want
 * to apply BIUTEE to another type of data-set, then you will have to write
 * an alternative flow for the "system" flow (which will inherit from {@link SystemInitialization}),
 * which will call the "macro" flow using the methods of {@link TextTreesProcessor}.
 * See developer guide for more details.
 * 
 * 
 * @see SystemInitialization
 * @see RTEPairsMultiThreadTrainer
 * 
 * @see TextTreesProcessor
 * 
 * 
 * @author Asher Stern
 * @since Feb 22, 2011
 *
 */
public class RTEPairsMultiThreadTester extends RTEPairsBaseSystem
{
	public static void main(String[] args)
	{
		try
		{
			if (args.length<1)
				throw new TeEngineMlException("Need first argument as configuration file name.");
			
			String configurationFileName = args[0];
			new LogInitializer(configurationFileName).init();
			
			ExperimentManager.getInstance().start();
			ExperimentManager.getInstance().setConfigurationFile(configurationFileName);
			logger.info("RTEPairsMultiThreadTester");
			
			RTEPairsMultiThreadTester tester = new RTEPairsMultiThreadTester(configurationFileName);
			Date startDate = new Date();
			tester.runTest();
			Date endDate = new Date();
			long elapsedSeconds = (endDate.getTime()-startDate.getTime())/1000;
			logger.info("RTEPairsMultiThreadTester done. Time elapsed: "+elapsedSeconds/60+" minutes and "+elapsedSeconds%60+" seconds.");
			ExperimentManager.getInstance().save();
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
			ExceptionUtil.logException(e, logger);
		}
	}
	
	public RTEPairsMultiThreadTester(String configurationFileName) throws ConfigurationFileDuplicateKeyException, ConfigurationException
	{
		super(configurationFileName,ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
	}
	
	public void runTest() throws ConfigurationFileDuplicateKeyException, ConfigurationException, LemmatizerException, TeEngineMlException, FileNotFoundException, InterruptedException, IOException, ClassNotFoundException, PluginAdministrationException
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

	
	@Override
	protected void init() throws ConfigurationFileDuplicateKeyException, ConfigurationException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		super.init();
		try
		{
			// Read the T-H pairs (already pre-processed)
			List<PairData> originalPairsData = readDatasetAndUpdateFeatureVectorStructure();
//			File serializedPairs = configurationParams.getFile(ConfigurationParametersNames.RTE_PAIRS_PREPROCESS_SERIALIZATION_FILE_NAME);
//			RTESerializedPairsReader reader = new RTESerializedPairsReader(serializedPairs.getPath());
//			reader.read();
//			List<PairData> originalPairsData = reader.getPairsData();
			pairsData = new ArrayList<ExtendedPairData>(originalPairsData.size());
			logger.info("Converting all pairs to an ExtendedPairData format. (Since annotations take place here, it might take some time)...");
			for (PairData originalPairData : originalPairsData)
			{
				PairDataToExtendedPairDataConverter converter = new PairDataToExtendedPairDataConverter(originalPairData,this.teSystemEnvironment);
				converter.convert();
				pairsData.add(converter.getExtendedPairData());
			}
			logger.info("Converting all pairs to an ExtendedPairData format - Done.");
			
			
			// Create a classifier, based on a serialized vector of LabeledSamples (stored in a ser file), or based on a reasonable guess
			OperationsScript<?, ?> script = new ScriptFactory(configurationFile,teSystemEnvironment.getPluginRegistry(),teSystemEnvironment).getDefaultScript();
			script.init();
			try
			{
				this.ruleBasesNames = script.getRuleBasesNames();
				completeInitializationWithScript(script);
				String ruleBasesNamesString = setToString(this.ruleBasesNames);
				ruleBasesNamesString = "Rule bases: "+ruleBasesNamesString;
				logger.info(ruleBasesNamesString);
				ExperimentManager.getInstance().addMessage(ruleBasesNamesString);
				//File serializedSamples = rteTestParams.getFile(ConfigurationParametersNames.RTE_PAIRS_TEST_SERIALIZED_SAMPLES_NAME);
				//this.classifier = new SynchronizedClassifier(buildClassifier(serializedSamples));
				this.classifierForSearch = RTESystemsUtils.createOrLoadSearchClassifierForTest(configurationParams, teSystemEnvironment.getFeatureVectorStructureOrganizer());
				logger.info("Tester: classifierForSearch:");
				logger.info(this.classifierForSearch.descriptionOfTraining());

				this.classifierForPredictions = RTESystemsUtils.createOrLoadClassiferForPredictionsForTest(configurationParams, teSystemEnvironment.getFeatureVectorStructureOrganizer());
				logger.info("classifierForPredictions:\n"+this.classifierForPredictions.descriptionOfTraining());
			}
			finally
			{
				script.cleanUp();
			}
			

			
			// Read the number of threads.
			numberOfThreads = configurationParams.getInt(ConfigurationParametersNames.RTE_ENGINE_NUMBER_OF_THREADS_PARAMETER_NAME);
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
		catch (ClassifierException e)
		{
			throw new TeEngineMlException("Classifier initialization failed.",e);
		}
		catch (OperationException e)
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
	
	
	

	
	private void test() throws TeEngineMlException, InterruptedException, FileNotFoundException, ConfigurationException, IOException, ClassNotFoundException
	{
		pairsResults = new LinkedHashMap<ExtendedPairData, PairResult>();
		List<ExtendedPairData>[] pairsForThreads = RTESystemsUtils.splitPairsForThreads(pairsData, numberOfThreads);
		OneThreadTester[] threadTesters = new OneThreadTester[numberOfThreads];
		Thread[] threadsArray = new Thread[numberOfThreads];
		for (int threadIndex=0;threadIndex<numberOfThreads;threadIndex++)
		{
			OneThreadTester tester = new OneThreadTester(pairsForThreads[threadIndex]);
			threadTesters[threadIndex] = tester;
			Thread thread = new Thread(tester);
			threadsArray[threadIndex] = thread;
			thread.start();
		}
		
		TeEngineMlException threadException = null;
		for (int threadIndex=0;threadIndex<numberOfThreads;threadIndex++)
		{
			threadsArray[threadIndex].join();
			if (threadException==null)
				threadException = threadTesters[threadIndex].getOneThreadException();
			if (null==threadException)
			{
				pairsResults.putAll(threadTesters[threadIndex].getOneThreadResults());
			}
		}
		if (threadException!=null)
			throw threadException;
		
		try
		{
			String resultsSerFileName = BiuteeConstants.RTE_PAIRS_OUTPUT_RESULTS_FILE_PREFIX+BiuteeConstants.RTE_PAIRS_OUTPUT_RESULTS_FILE_INFIX_TEX+BiuteeConstants.RTE_PAIRS_OUTPUT_RESULTS_FILE_POSTFIX;
			logger.info("Storing results in a serialization file: \""+resultsSerFileName+"\"");
			RTESystemsUtils.saveInSerFile(resultsSerFileName, pairsResults);
			ExperimentManager.getInstance().register(new File(resultsSerFileName));

			
			File xmlResultsFile = new File(BiuteeConstants.RTE_PAIRS_XML_RESULTS_FILE_NAME_PREFIX+BiuteeConstants.RTE_PAIRS_XML_RESULTS_FILE_NAME_POSTFIX);
			logger.info("Printing results in an XML file: "+xmlResultsFile.getPath());
			ResultsToXml resultsToXml = new ResultsToXml(ResultsToXml.convertPairResults(pairsResults, classifierForPredictions),xmlResultsFile);
			resultsToXml.output();
			RTESystemsUtils.logTextFile(xmlResultsFile, logger);
			ExperimentManager.getInstance().register(xmlResultsFile);
			
			printPairsResults();
		}
		catch(ClassifierException e)
		{
			throw new TeEngineMlException("Could not print results. See nested exception",e);
		}


	}
	
	private void printPairsResults() throws ClassifierException, FileNotFoundException, ConfigurationException, IOException, ClassNotFoundException, TeEngineMlException
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
		
		logger.info(StringUtil.generateStringOfCharacter('-', 100));
		if (BiuteeConstants.PRINT_TIME_STATISTICS)
		{
			logger.info("Printing time statistics (CSV format)...");
			StringBuffer sb = new StringBuffer();
			sb.append("\n");
			sb.append("id,cpuTime,worldClockTime,number_of_expanded,number_of_generated,cost\n");
			long sumCpuTime=0;
			long sumWorldClockTime=0;
			long sumExpanded=0;
			long sumGenerated=0;
			double sumCost=0.0;
			int count=0;
			for (ExtendedPairData pair : pairsResults.keySet())
			{
				Integer id = pair.getPair().getId();
				Long cpuTime = pairsResults.get(pair).getCpuTime();
				Long worldClockTime = pairsResults.get(pair).getWorldClockTime();
				Long numberOfExpandedElements = pairsResults.get(pair).getNumberOfExpandedElements();
				Long numberOfGeneratedElements = pairsResults.get(pair).getNumberOfGeneratedElements();
				double cost = -this.classifierForSearch.getProduct(pairsResults.get(pair).getBestTree().getFeatureVector());
				
				sb.append(id).append(",").append(cpuTime).append(",").append(worldClockTime).append(",").append(numberOfExpandedElements).append(",").append(numberOfGeneratedElements).append(",").append(String.format("%-4.4f",cost)).append("\n");
				sumCpuTime+=cpuTime;
				sumWorldClockTime+=worldClockTime;
				sumExpanded+=numberOfExpandedElements;
				sumGenerated+=numberOfGeneratedElements;
				sumCost+=cost;
				++count;
			}
			sb.append("\n");
			sb.append("Sum,").append(sumCpuTime).append(",").append(sumWorldClockTime).append(",").append(sumExpanded).append(",").append(sumGenerated).append(",").append(String.format("%-4.4f",sumCost)).append("\n");
			sb.append("Average,").append(sumCpuTime/count).append(",").append(sumWorldClockTime/count).append(",").append(sumExpanded/count).append(",").append(sumGenerated/count).append(",").append(String.format("%-4.4f",(sumCost/(double)count))).append("\n");
			logger.info(sb.toString());
		}
		
		
		// RTESystemsUtils.printAccuraciesOfClassifiers(configurationFile, listLabeledSamples, ruleBasesNames);
	}
	

	
	public  LinkedHashMap<ExtendedPairData, ResultWithScores<PairResult>> buildMapResultsWithScores() throws ClassifierException, TeEngineMlException
	{
		if (null==this.pairsResults) throw new TeEngineMlException("test() was not called yet.");
		return RTESystemsUtils.buildMapResultsWithScores(this.pairsResults, this.classifierForPredictions, this.classifierForSearch);
	}
	
	
	
	
	
	
	
//	private Classifier buildClassifier(File serializedSamples) throws ClassifierException, OperationException, IOException, ClassNotFoundException
//	{
//		Classifier ret = new ClassifierFactory().getDefaultClassifier();
//		ObjectInputStream serStream = new ObjectInputStream(new FileInputStream(serializedSamples));
//		try
//		{
//			@SuppressWarnings("unchecked")
//			Vector<LabeledSample> samples = (Vector<LabeledSample>) serStream.readObject();
//			ret.train(samples);
//			OperationsScript<Info, EnglishNode> tempScript = new ScriptFactory(configurationFile).getDefaultScript();
//			tempScript.init();
//			try
//			{
//				ret.setFeaturesNames( ClassifierUtils.extendFeatureNames(Feature.toMapOfNames(), tempScript.getRuleBasesNames()));
//			}
//			finally
//			{
//				tempScript.cleanUp();
//			}
//			
//			
//			return ret;
//		}
//		finally
//		{
//			if (serStream!=null)
//				serStream.close();
//		}
//	}
	
	
	private class OneThreadTester implements Runnable
	{
		public OneThreadTester(List<ExtendedPairData> oneThreadPairsData)
		{
			this.oneThreadPairsData = oneThreadPairsData;
		}
		
		public void run()
		{
			try
			{
				processAllPairs();
			}
			catch (TeEngineMlException e)
			{
				ExceptionUtil.logException(e, logger);
				oneThreadException = e;
			}
			catch (Throwable e)
			{
				ExceptionUtil.logException(e, logger);
				oneThreadException = new TeEngineMlException("Processing of pairs failed. See nested exception.",e);
			}
		}
		
		public LinkedHashMap<ExtendedPairData, PairResult> getOneThreadResults()
		{
			return oneThreadResults;
		}
		

		public TeEngineMlException getOneThreadException()
		{
			return oneThreadException;
		}

		private void processAllPairs() throws OperationException, TeEngineMlException, ClassifierException, ScriptException, RuleBaseException, AnnotatorException
		{
			oneThreadScript = new ScriptFactory(configurationFile,teSystemEnvironment.getPluginRegistry(),teSystemEnvironment).getDefaultScript();
			oneThreadScript.init();
			try
			{
				oneThreadResults = new LinkedHashMap<ExtendedPairData, PairResult>();
				for (ExtendedPairData pairData : oneThreadPairsData)
				{
					Integer pairId = pairData.getPair().getId();
					logger.info("Working on pair: "+pairId);
					PairProcessor pairProcessor =
						new PairProcessor(pairData, classifierForSearch, lemmatizer, oneThreadScript, teSystemEnvironment);
					pairProcessor.process();
					logger.info("Processing of pair "+pairId+" is done. Memory used = "+Utils.stringMemoryUsedInMB());
					PairResult pairResult;
					if (BiuteeConstants.PRINT_TIME_STATISTICS)
					{
						pairResult = new PairResult(pairProcessor.getBestTree(), pairProcessor.getBestTreeSentence(), pairProcessor.getBestTreeHistory(),pairProcessor.getCpuTime(),pairProcessor.getWorldClockTime(),pairProcessor.getNumberOfExpandedElements(),pairProcessor.getNumberOfGeneratedElements());
					}
					else
					{
						pairResult = new PairResult(pairProcessor.getBestTree(), pairProcessor.getBestTreeSentence(), pairProcessor.getBestTreeHistory());
					}
					
					oneThreadResults.put(pairData, pairResult);
				}
			}
			finally
			{
				oneThreadScript.cleanUp();
			}
		}
		
		
		private List<ExtendedPairData> oneThreadPairsData;
		private OperationsScript<Info, BasicNode> oneThreadScript;
		
		private LinkedHashMap<ExtendedPairData, PairResult> oneThreadResults;
		private TeEngineMlException oneThreadException = null;
	}
	
	private static String setToString(Set<?> set)
	{
		if (null==set) return "";
		
		StringBuilder sb = new StringBuilder();
		boolean firstIteration = true;
		for (Object object : set)
		{
			if (firstIteration) {firstIteration=false;} else {sb.append(", ");}
			sb.append(object.toString());
		}
		return sb.toString();
	}
	

	private ArrayList<ExtendedPairData> pairsData;
	private LinearClassifier classifierForSearch;
	private Classifier classifierForPredictions;
	private int numberOfThreads;
	
	private LinkedHashSet<String> ruleBasesNames = null;
	
	private LinkedHashMap<ExtendedPairData, PairResult> pairsResults;
	
	private static Logger logger = Logger.getLogger(RTEPairsMultiThreadTester.class);


}
