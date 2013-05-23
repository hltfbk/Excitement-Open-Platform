package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.biutee.rteflow.systems.excitement.ExcitementToBiuConfigurationFileConverter.ExcitementToBiuConfigurationFileConverterException;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.RTEPairsPreProcessor;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
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
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainReaderException;
import eu.excitementproject.eop.common.utilities.file.FileFilters;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.core.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.lap.biu.uima.BIUFullLAP;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

public class BiuteeMain {

	/**
	 * Run specific steps in the BIUTEE flow, according to specific values specified as a comma-separated list in parameter flowList:
	 * <tt>lap_train, train, lap_test, test</tt>, or <tt>full</tt> for all steps.<BR> 
	 */
	public static void runBiuteeCustomFlow(String configPath, String flowList) throws BiuteeMainException, EDAException, ComponentException, ConfigurationException, IOException, ExcitementToBiuConfigurationFileConverterException, ConfigurationFileDuplicateKeyException, TeEngineMlException, eu.excitementproject.eop.common.utilities.configuration.ConfigurationException, RTEMainReaderException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException  {
		
		try {
			CommonConfig config = init(configPath);
			Set<String> flow = new HashSet<String>(Arrays.asList(flowList.split(",")));
			if (flow.size()==0) {
				throw new BiuteeMainException("At least one flow step must be provided, got none.");
			}
			
			Set<String> diff = new HashSet<String>(flow);
			diff.removeAll(ALLOWED_STEPS);
			if (diff.size() != 0) {
				throw new BiuteeMainException("Disallowed flow steps: " + StringUtil.join(diff, ","));
			}
			
			if (flow.contains("full") && flow.size()!=1) {
				throw new BiuteeMainException("Flow step \"full\" must not be provided with other steps.");
			}
			boolean hasFull = flow.contains("full");
			
			if (hasFull || flow.contains("lap_train"))	doLAPforTraining(config);
			if (hasFull || flow.contains("train"))		doTraining(config);
			if (hasFull || flow.contains("lap_test"))	doLAPforTesting(config);
			if (hasFull || flow.contains("test"))		doTesting(config);
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
			throw t;
		}

	}
	

	public static CommonConfig init(String configPath) throws TeEngineMlException, IOException, ConfigurationException {
		CommonConfig config = new ImplCommonConfig(new File(configPath));
		new LogInitializer(configPath).init();
		return config;
	}
	
	private static void doLAPforTraining(CommonConfig config) throws IOException, ExcitementToBiuConfigurationFileConverterException, ConfigurationFileDuplicateKeyException, TeEngineMlException, eu.excitementproject.eop.common.utilities.configuration.ConfigurationException, RTEMainReaderException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException {
		File biuConfigurationFile = File.createTempFile(BiuteeEDA.TEMPORARY_CONFIGURATION_FILE_PREFIX, BiuteeEDA.TEMPORARY_CONFIGURATION_FILE_SUFFIX);
		logger.trace("Converting EOP-format configuration file to BIU-format in temporary file: " + biuConfigurationFile.getAbsolutePath());
		BiuteeEdaUtilities.convertExcitementConfigurationFileToBiuConfigurationFile(new File(config.getConfigurationFileName()), biuConfigurationFile);

		logger.trace("Initializing preprocessor RTEPairsPreProcessor...");
		RTEPairsPreProcessor processor = new RTEPairsPreProcessor(biuConfigurationFile.getAbsolutePath());
		//RTEPairsPreProcessor processor = new RTEPairsPreProcessor(config.getConfigurationFileName());
		logger.trace("Preprocessing using preprocessor...");
		processor.preprocess();
		logger.trace("Done preprocessing.");
	}
	
	private static void doLAPforTesting(CommonConfig config) throws LAPException, ConfigurationException, IOException {
		// Set up environment
		clearDirectory(lapOutputFolder);
		File testData = config.getSection("rte_pairs_preprocess").getFile("dataset");
		
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
			for (File xmi : xmiFiles) {
				JCas jcas = PlatformCASProber.probeXmi(xmi, null);
				TEDecision decision = eda.process(jcas);
				logger.info(String.format("Decision for %s: label=%s, confidence=%f", xmi, decision.getDecision(), decision.getConfidence()));
			}
			
			logger.trace(String.format("Finished processing %d xmi files using BiuteeEDA.", xmiFiles.length));
		}
		finally {
			if (eda != null) {
				eda.shutdown();
			}
		}
	
	}
	
	private static void clearDirectory(File path) throws IOException {
		if (path.isDirectory() && !FileUtils.deleteDirectory(path)) {
			throw new IOException("Failed to delete path: " + path.getAbsolutePath());
		}
		path.mkdirs();
	}
	
	public static void main(String[] args) throws EDAException, ComponentException, ConfigurationException, ConfigurationFileDuplicateKeyException, TeEngineMlException, BiuteeMainException, IOException, ExcitementToBiuConfigurationFileConverterException, eu.excitementproject.eop.common.utilities.configuration.ConfigurationException, RTEMainReaderException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException {
		if (args.length != 2) {
			throw new BiuteeMainException("Exactly 2 arguments must be provided: <configuration path> <flow list>");
		}
		runBiuteeCustomFlow(args[0], args[1]);
	}

	private static final File lapOutputFolder = new File("./lap_output");
	public static final Set<String> ALLOWED_STEPS = new HashSet<String>(Arrays.asList(new String[] {"full", "lap_train", "train", "lap_test", "test"}));
	
	private static final Logger logger = Logger.getLogger(BiuteeMain.class);

}
