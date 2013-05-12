package eu.excitementproject.eop.biutee;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import eu.excitementproject.eop.biutee.rteflow.systems.excitement.BiuteeEDA;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.file.FileFilters;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.core.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.biu.BIUFullLAP;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Tests a full run of BIUTEE in EOP. Assumes BIUTEE is already trained.<BR>
 * Requires a system property named {@value #CONFIG_PROP_NAME} providing the configuration file path.
 * 
 * Tests:
 * <ul>
 * <li> Only LAP (preprocessing)
 * <li> Only EDA
 * <li> LAP and then EDA
 * </ul>
 * 
 * 
 * @author Ofer Bronstein
 * @since May 2013
 */
@Ignore("Environment doesn't support yet storing model files + running easyfirst")
public class TestBiuteeUsage {
	
	@BeforeClass
	public static void prepareBiuteeRun() {
		
	}

	/**
	 * Run BIU LAP and then BIUTEE EDA.
	 * @throws ConfigurationException
	 * @throws IOException
	 * @throws EDAException
	 * @throws ComponentException
	 * @throws TeEngineMlException
	 */
	@Test
	public void runFullBiutee() throws ConfigurationException, IOException, EDAException, ComponentException, TeEngineMlException {
		
		try {
			CommonConfig config = init();
			doPreprocessing(config);
			doTesting(config);
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
			throw t;
		}

	}
	
	/**
	 * Run only BIU LAP.
	 * @throws ConfigurationException
	 * @throws IOException
	 * @throws EDAException
	 * @throws ComponentException
	 * @throws TeEngineMlException
	 */
	@Test
	public void runBiuteePreprocessing() throws ConfigurationException, IOException, EDAException, ComponentException, TeEngineMlException {
		
		try {
			CommonConfig config = init();
			doPreprocessing(config);
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
			throw t;
		}

	}
	
	/**
	 * Run only BIUTEE EDA. Assumes LAP was previously run, and its output (XMI files)
	 * is stored in <code>lapOutputFolder</code> folder.
	 * @throws ConfigurationException
	 * @throws IOException
	 * @throws EDAException
	 * @throws ComponentException
	 * @throws TeEngineMlException
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
	
	private void doPreprocessing(CommonConfig config) throws LAPException, ConfigurationException, IOException {
		// Set up environment
		clearDirectory(lapOutputFolder);
		File testData = config.getSection("rte_pairs_preprocess").getFile("dataset");
		
		logger.trace("Initializing BIUFullLAP...");
		BIUFullLAP lap = new BIUFullLAP(config);
		logger.trace(String.format("Running LAP BIUFullLAP on %s outputting to %s...", testData, lapOutputFolder));
		lap.processRawInputFormat(testData, lapOutputFolder);
		logger.trace("finished using BIUFullLAP.");
	}
	
	private void doTesting(CommonConfig config) throws EDAException, ComponentException, ConfigurationException {
		logger.trace("Initializing BiuteeEDA...");
		BiuteeEDA eda = new BiuteeEDA();
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
	
	private void clearDirectory(File path) throws IOException {
		if (path.isDirectory() && !FileUtils.deleteDirectory(path)) {
			throw new IOException("Failed to delete path: " + path.getAbsolutePath());
		}
		path.mkdirs();
	}
	
	private static final File lapOutputFolder = new File("./lap_output");
	public static final String CONFIG_PROP_NAME = "config";
	
	private static final Logger logger = Logger.getLogger(TestBiuteeUsage.class);
}
