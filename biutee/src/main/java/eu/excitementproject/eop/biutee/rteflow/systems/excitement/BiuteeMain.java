package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import java.io.File;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.biutee.rteflow.systems.excitement.ExcitementToBiuConfigurationFileConverter.ExcitementToBiuConfigurationFileConverterException;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.RTEPairsPreProcessor;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainReaderException;
import eu.excitementproject.eop.common.utilities.file.FileFilters;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.lap.biu.uima.BIUFullLAP;
import eu.excitementproject.eop.transformations.utilities.GlobalMessages;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Runs BIUTEE via EOP interfaces.
 * 
 * @author Ofer Bronstein
 * 
 *
 */
public class BiuteeMain {

	/**
	 * Run specific steps in the BIUTEE flow, according to specific values specified as a comma-separated list in parameter flowList:
	 * <tt>lap_train, train, lap_test, test</tt>, or <tt>full</tt> for all steps.<BR> 
	 */
	public static void runBiuteeCustomFlow(String configPath, String flowList) throws BiuteeMainException, EDAException, ComponentException, ConfigurationException, IOException, ExcitementToBiuConfigurationFileConverterException, ConfigurationFileDuplicateKeyException, TeEngineMlException, eu.excitementproject.eop.common.utilities.configuration.ConfigurationException, RTEMainReaderException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException  {
		// (Reminder: the first command-line parameter is the configuration file name).
		// Read the second command-line parameter. This might be something like "lap_train,train"
		CommonConfig config = init(configPath);
		Set<String> flow = new LinkedHashSet<String>(Arrays.asList(flowList.split(",")));
		if (flow.size()==0) {
			throw new BiuteeMainException("At least one flow step must be provided, got none.");
		}

		// Validate correctness of second command-line parameter
		Set<String> diff = new LinkedHashSet<String>(flow);
		diff.removeAll(ALLOWED_STEPS);
		if (diff.size() != 0) {
			throw new BiuteeMainException("Disallowed flow steps: " + StringUtil.join(diff, ","));
		}

		if (flow.contains("full") && flow.size()!=1) {
			throw new BiuteeMainException("Flow step \"full\" must not be provided with other steps.");
		}
		boolean hasFull = flow.contains("full");

		// Run the appropriate action, according to the second command-line parameter
		if (hasFull || flow.contains("lap_train"))	doLAPforTraining(config);
		if (hasFull || flow.contains("train"))		doTraining(config);
		if (hasFull || flow.contains("lap_test"))	doLAPforTesting(config);
		if (hasFull || flow.contains("test"))		doTesting(config);
	}

	

	public static CommonConfig init(String configPath) throws TeEngineMlException, IOException, ConfigurationException {
		CommonConfig config = new ImplCommonConfig(new File(configPath));
		new LogInitializer(configPath).init();
		return config;
	}
	
	private static void doLAPforTraining(CommonConfig config) throws IOException, ExcitementToBiuConfigurationFileConverterException, ConfigurationFileDuplicateKeyException, TeEngineMlException, eu.excitementproject.eop.common.utilities.configuration.ConfigurationException, RTEMainReaderException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException {
		logger.trace("Initializing preprocessor RTEPairsPreProcessor...");
		RTEPairsPreProcessor processor = new RTEPairsPreProcessor(config.getConfigurationFileName(), RTEPairsPreProcessor.TrainTestEnum.TRAIN.name());
		//RTEPairsPreProcessor processor = new RTEPairsPreProcessor(config.getConfigurationFileName());
		logger.trace("Preprocessing using preprocessor...");
		processor.preprocess();
		logger.trace("Done preprocessing.");
	}
	
	private static void doLAPforTesting(CommonConfig config) throws LAPException, ConfigurationException, IOException {
		// Set up environment
		clearDirectory(lapOutputFolder);
		File testData = config.getSection("rte_pairs_preprocess").getFile("test_data");
		
		logger.trace("Initializing BIUFullLAP...");
		BIUFullLAP lap = new BIUFullLAP(config);
		logger.trace(String.format("Running LAP BIUFullLAP on %s outputting to %s...", testData, lapOutputFolder));
		lap.processRawInputFormat(testData, lapOutputFolder);
		logger.trace("finished using BIUFullLAP.");
	}
	
	private static void doTraining(CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		BiuteeEDA eda = new BiuteeEDA();
		eda.startTraining(config);
	}
	
	private static void doTesting(CommonConfig config) throws EDAException, ComponentException, ConfigurationException, BiuteeMainException {
		BiuteeEDA eda = null;
		try {
			logger.trace("Initializing BiuteeEDA...");
			eda = new BiuteeEDA();
			eda.initialize(config);
			File[] xmiFiles = lapOutputFolder.listFiles(new FileFilters.ExtFileFilter("xmi"));
			if (xmiFiles == null) {
				throw new BiuteeMainException("Directory " + lapOutputFolder + " does not exist");
			}
			if (xmiFiles.length==0) {
				throw new BiuteeMainException("Must have at least one preprocessed XMI in " + lapOutputFolder);
			}
			logger.trace(String.format("Processing all %d xmi files in %s using BiuteeEDA...", xmiFiles.length, lapOutputFolder));
			
			double tp=0,fp=0,tn=0,fn=0;
			//List<Boolean> res = new ArrayList<Boolean>();
			for (File xmi : xmiFiles) {
				JCas jcas = PlatformCASProber.probeXmi(xmi, null);
				TEDecision decision = eda.process(jcas);
				DecisionLabel gold = readGoldLabel(jcas, xmi);
				//res.add(gold.equals(decision.getDecision()));
				
				if (gold.equals(decision.getDecision())) {
					if (decision.getDecision() == DecisionLabel.Entailment)
						tp++;
					else
						tn++;										
				} else {
					if (decision.getDecision() == DecisionLabel.Entailment)
						fp++;
					else
						fn++;															
				}
				
				logger.info(String.format("Decision for %s: label=%s, confidence=%f", xmi, decision.getDecision(), decision.getConfidence()));
			}
			
			double accuracy = (tp + tn) / (tp + fp + tn + fn);			
			double precision = tp / (tp + fp);
			double recall = tp / (tp + fn);			
			double f1 = 2 * ((precision * recall) / (precision + recall));
			
			logger.trace(String.format("Finished processing %d xmi files using BiuteeEDA.", xmiFiles.length));
			logger.info("Accuracy: " + accuracy);
			logger.info("Precision: " + precision);
			logger.info("Recall: " + recall);
			logger.info("F1: " + f1);
		}
		finally {
			if (eda != null) {
				eda.shutdown();
			}
		}
	}
	
/*	private static double calcAccuracy(List<Boolean> results) {
		int correct = 0;
		for (Boolean result : results) {
			if (result) {
				correct++;
			}
		}
		return ((double)correct) / results.size();
	}*/
	
	private static DecisionLabel readGoldLabel(JCas jcas, File xmi) throws EDAException {
		DecisionLabel goldLabel = null;
		Collection<Pair> pairs = JCasUtil.select(jcas, Pair.class);
		if (pairs.size() != 1) {
			throw new EDAException("Ambiguous gold answer for " + xmi);
		}
		for (Pair pair : pairs) {
			goldLabel = DecisionLabel.getLabelFor(pair.getGoldAnswer());
		}
		if (goldLabel == null) {
			throw new EDAException("Unknown gold answer for " + xmi);
		}
		return goldLabel;
	}
	
	private static void clearDirectory(File path) throws IOException {
		if (path.isDirectory() && !FileUtils.deleteDirectory(path)) {
			throw new IOException("Failed to delete path: " + path.getAbsolutePath());
		}
		path.mkdirs();
	}
	
	public static void main(String[] args)
	{
		// The only important line in this function is
		// runBiuteeCustomFlow(args[0], args[1]);
		try
		{
			try
			{
				if (args.length != 2) {
					throw new BiuteeMainException("Exactly 2 arguments must be provided: <configuration path> <flow list>");
				}
				runBiuteeCustomFlow(args[0], args[1]);
			}
			finally
			{
				if (logger!=null)
				{
					GlobalMessages.getInstance().addToLogAndExperimentManager(logger);
				}
			}
		}
		catch(Throwable t)
		{
			ExceptionUtil.outputException(t, System.out);
			if (logger!=null)
			{
				try{ExceptionUtil.logException(t, logger);}catch(Throwable tt){}
			}
		}
	}

	private static final File lapOutputFolder = new File("./lap_output");
	public static final Set<String> ALLOWED_STEPS = new LinkedHashSet<String>(Arrays.asList(new String[] {"full", "lap_train", "train", "lap_test", "test"}));
	
	private static final Logger logger = Logger.getLogger(BiuteeMain.class);

}
