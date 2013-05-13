package eu.excitementproject.eop.biutee;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.excitementproject.eop.biutee.rteflow.systems.excitement.BiuteeEDA;
import eu.excitementproject.eop.biutee.rteflow.systems.excitement.BiuteeEdaUtilities;
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

/**
 * Tests for BIUTEE in EOP.<BR>
 * This class provides tests for running different steps of the system, all the steps, or a custom comination of the steps.<BR>
 * Requires a system property named {@value #CONFIG_PROP_NAME} providing the configuration file path.
 * Should be run from the <tt>workdir</tt> folder in the BIUTEE distribution.
 * 
 * @author Ofer Bronstein
 * @since May 2013
 */
public class TestBiuteeUsage {
	
	/**
	 * This class only works under the BIU environment.
	 */
	@BeforeClass
	public static void prepareBiuteeRun() {
		File workingFolder = new File(System.getProperty("user.dir"));
		Assume.assumeTrue(workingFolder.getName().toLowerCase().startsWith("workdir"));
	}

	/**
	 * Run full BIUTEE: LAP for training, training, LAP for testing, testing.
	 */
	@Test
	public void runFullBiutee() throws EDAException, ComponentException, ConfigurationException, IOException, ExcitementToBiuConfigurationFileConverterException, ConfigurationFileDuplicateKeyException, TeEngineMlException, eu.excitementproject.eop.common.utilities.configuration.ConfigurationException, RTEMainReaderException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException  {
		
		try {
			CommonConfig config = init();
			doLAPforTraining(config);
			doTraining(config);
			doLAPforTesting(config);
			doTesting(config);
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
			throw t;
		}

	}
	
	/**
	 * Run specific steps in the BIUTEE flow, according to specific values specified as a comma-separated list in the property "<tt>flow</tt>":
	 * <tt>lap_train, train, lap_test, test</tt>.<BR> 
	 */
	@Test
	public void runBiuteeCustomFlow() throws EDAException, ComponentException, ConfigurationException, IOException, ExcitementToBiuConfigurationFileConverterException, ConfigurationFileDuplicateKeyException, TeEngineMlException, eu.excitementproject.eop.common.utilities.configuration.ConfigurationException, RTEMainReaderException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException  {
		
		try {
			CommonConfig config = init();
			List<String> flow = Arrays.asList(System.getProperty(FLOW_PROP_NAME).split(","));
			if (flow.contains("lap_train"))	doLAPforTraining(config);
			if (flow.contains("train"))		doTraining(config);
			if (flow.contains("lap_test"))	doLAPforTesting(config);
			if (flow.contains("test"))		doTesting(config);
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
			throw t;
		}

	}
	
	/**
	 * Run BIUTEE testing-LAP and testing.
	 */
	@Test
	public void runBiuteeTestingWithLAP() throws ConfigurationException, IOException, EDAException, ComponentException, TeEngineMlException {
		
		try {
			CommonConfig config = init();
			doLAPforTesting(config);
			doTesting(config);
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
			throw t;
		}

	}
	
	/**
	 * Run BIUTEE training-LAP and training.
	 */
	@Test
	public void runBiuteeTrainingWithLAP() throws EDAException, ComponentException, ConfigurationException, IOException, ExcitementToBiuConfigurationFileConverterException, ConfigurationFileDuplicateKeyException, TeEngineMlException, eu.excitementproject.eop.common.utilities.configuration.ConfigurationException, RTEMainReaderException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException  {
		
		try {
			CommonConfig config = init();
			doLAPforTraining(config);
			doTraining(config);
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
			throw t;
		}

	}
	
	/**
	 * Run BIUTEE training-LAP.
	 */
	@Test
	public void runBiuteeLAPforTraining() throws ConfigurationException, IOException, ExcitementToBiuConfigurationFileConverterException, ConfigurationFileDuplicateKeyException, TeEngineMlException, eu.excitementproject.eop.common.utilities.configuration.ConfigurationException, RTEMainReaderException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException  {
		
		try {
			CommonConfig config = init();
			doLAPforTraining(config);
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
			throw t;
		}

	}
	
	/**
	 * Run BIUTEE testing-LAP.
	 */
	@Test
	public void runBiuteeLAPforTesting() throws ConfigurationException, IOException, EDAException, ComponentException, TeEngineMlException {
		
		try {
			CommonConfig config = init();
			doLAPforTesting(config);
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
			throw t;
		}

	}
	
	/**
	 * Run BIUTEE training. Assumes training-LAP was previously run.
	 */
	@Test
	public void runBiuteeTraining() throws ConfigurationException, IOException, EDAException, ComponentException, TeEngineMlException {
		
		try {
			CommonConfig config = init();
			doTraining(config);
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
			throw t;
		}

	}
	
	/**
	 * Run BIUTEE testing. Assumes testing-LAP was previously run, and its output (XMI files)
	 * is stored in <code>lapOutputFolder</code> folder. Also assumes BIUTEE training was
	 * previously run.
	 */
	@Test
	public void runBiuteeTesting() throws ConfigurationException, IOException, EDAException, ComponentException, TeEngineMlException {
		
		try {
			CommonConfig config = init();
			doTesting(config);
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
			throw t;
		}

	}
	
	private CommonConfig init() throws TeEngineMlException, IOException, ConfigurationException {
		String configPath = System.getProperty(CONFIG_PROP_NAME);
		Assert.assertNotNull("Path to configuration file must by passed via the '" + CONFIG_PROP_NAME + "' property.", configPath);
		CommonConfig config = new ImplCommonConfig(new File(configPath));
		new LogInitializer(configPath).init();
		return config;
	}
	
	private void doLAPforTraining(CommonConfig config) throws IOException, ExcitementToBiuConfigurationFileConverterException, ConfigurationFileDuplicateKeyException, TeEngineMlException, eu.excitementproject.eop.common.utilities.configuration.ConfigurationException, RTEMainReaderException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException {
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
	
	private void doLAPforTesting(CommonConfig config) throws LAPException, ConfigurationException, IOException {
		// Set up environment
		clearDirectory(lapOutputFolder);
		File testData = config.getSection("rte_pairs_preprocess").getFile("dataset");
		
		logger.trace("Initializing BIUFullLAP...");
		BIUFullLAP lap = new BIUFullLAP(config);
		logger.trace(String.format("Running LAP BIUFullLAP on %s outputting to %s...", testData, lapOutputFolder));
		lap.processRawInputFormat(testData, lapOutputFolder);
		logger.trace("finished using BIUFullLAP.");
	}
	
	private void doTraining(CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		BiuteeEDA eda = new BiuteeEDA();
		eda.startTraining(config);
	}
	
	private void doTesting(CommonConfig config) throws EDAException, ComponentException, ConfigurationException {
		BiuteeEDA eda = null;
		try {
			logger.trace("Initializing BiuteeEDA...");
			eda = new BiuteeEDA();
			eda.initialize(config);
			File[] xmiFiles = lapOutputFolder.listFiles(new FileFilters.ExtFileFilter("xmi"));
			Assert.assertTrue("Directory " + lapOutputFolder + " does not exist", xmiFiles != null);
			Assert.assertTrue("Must have at least one preprocessed XMI in " + lapOutputFolder, xmiFiles.length>0);
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
	
	private void clearDirectory(File path) throws IOException {
		if (path.isDirectory() && !FileUtils.deleteDirectory(path)) {
			throw new IOException("Failed to delete path: " + path.getAbsolutePath());
		}
		path.mkdirs();
	}
	
	private static final File lapOutputFolder = new File("./lap_output");
	public static final String CONFIG_PROP_NAME = "config";
	public static final String FLOW_PROP_NAME = "flow";
	
	private static final Logger logger = Logger.getLogger(TestBiuteeUsage.class);
}
