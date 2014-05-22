package eu.excitementproject.eop.core;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;

public class MetaEDATest {
	static Logger logger = Logger.getLogger(MetaEDATest.class
			.getName());

	@Test
	/**
	 * test MetaEDA in mode 2 (majority vote) with two internal EDAs
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test1() throws EDAException, ComponentException {
		
		logger.info("MetaEDA test (mode 1) started");
		
		File metaconfigFile = new File("./src/test/resources/configuration-file/MetaEDATest1_DE.xml");
		
		Assume.assumeTrue(metaconfigFile.exists());
		CommonConfig metaconfig = null;
		try {
			// read in the configuration from the file
			metaconfig = new ImplCommonConfig(metaconfigFile);
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(metaconfig);
		
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		//initialize TIE instance
		EDABasic<ClassificationTEDecision> tieEDA3 = new MaxEntClassificationEDA();
		EDABasic<? extends TEDecision> meceda = tieEDA3;
		File mecedaconfigfile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base_DE.xml");
		CommonConfig mecedaconfig = null;
		try {
			// read in the configuration from the file
			mecedaconfig = new ImplCommonConfig(mecedaconfigfile);
			logger.info("MaxEntClassification EDA config file read");
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(mecedaconfig);
	
		logger.info("initialize MaxEntClassification and load model");
		try {
			meceda.initialize(mecedaconfig);
		} catch (ConfigurationException | EDAException | ComponentException e1) {
			e1.printStackTrace();
		}
		edas.add(meceda);

//		//initialize EditDistancePSOEDA
		EDABasic<TEDecision> edpsoeda = new EditDistancePSOEDA<TEDecision>();
		File edpsoedaconfigfile = new File("./src/main/resources/configuration-file/EditDistancePSOEDA_DE.xml");
		CommonConfig edpsoedaconfig = null;
		try {
			// read in the configuration from the file
			edpsoedaconfig = new ImplCommonConfig(edpsoedaconfigfile);
			logger.info("EditDistancePSO EDA config file read");
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(edpsoedaconfig);
	
		logger.info("initialize EditDistancePSO and load model");
		try {
			edpsoeda.initialize(edpsoedaconfig);
		} catch (ConfigurationException | EDAException | ComponentException e1) {
			e1.printStackTrace();
		}
		edas.add(edpsoeda);
		
		//construct meta EDA
		MetaEDA meda = new MetaEDA(edas);
		try {
			meda.initialize(metaconfig);
			logger.info("Initialization done.");
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}

		testMetaEDA(meda);

		meda.shutdown();
		logger.info("EDA shuts down.");
	}

	@Test
	/**
	 * test MetaEDA in training mode 1 (confidence as features) with two internal EDAs
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test2() throws EDAException, ComponentException {
		
		logger.info("MetaEDA test (mode 2) started");
		
		File metaconfigFile = new File("./src/test/resources/configuration-file/MetaEDATest2_DE.xml");
		
		Assume.assumeTrue(metaconfigFile.exists());
		CommonConfig metaconfig = null;
		try {
			// read in the configuration from the file
			metaconfig = new ImplCommonConfig(metaconfigFile);
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(metaconfig);
		
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		//initialize TIE instance
		EDABasic<ClassificationTEDecision> tieEDA = new MaxEntClassificationEDA(); 
		EDABasic<? extends TEDecision> meceda2 = tieEDA; 
		File mecedaconfigfile2 = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DS_DE.xml");
		CommonConfig mecedaconfig2 = null;
		try {
			// read in the configuration from the file
			mecedaconfig2 = new ImplCommonConfig(mecedaconfigfile2);
			logger.info("MaxEntClassification EDA 2 config file read");
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(mecedaconfig2);
	
		logger.info("initialize MaxEntClassification 2 and load model");
		try {
			meceda2.initialize(mecedaconfig2);
		} catch (ConfigurationException | EDAException | ComponentException e1) {
			e1.printStackTrace();
		}
		edas.add(meceda2);

		//initialize EditDistancePSOEDA
		EDABasic<TEDecision> edpsoeda = new EditDistancePSOEDA<TEDecision>();
		File edpsoedaconfigfile = new File("./src/main/resources/configuration-file/EditDistancePSOEDA_DE.xml");
		CommonConfig edpsoedaconfig = null;
		try {
			// read in the configuration from the file
			edpsoedaconfig = new ImplCommonConfig(edpsoedaconfigfile);
			logger.info("EditDistancePSO EDA config file read");
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(edpsoedaconfig);
	
		logger.info("initialize EditDistancePSO and load model");
		try {
			edpsoeda.initialize(edpsoedaconfig);
		} catch (ConfigurationException | EDAException | ComponentException e1) {
			e1.printStackTrace();
		}
		edas.add(edpsoeda);
		
		//construct meta EDA
		MetaEDA meda = new MetaEDA(edas);
		try {
			meda.startTraining(metaconfig);
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
		testMetaEDA(meda);

		meda.shutdown();
		logger.info("EDA shuts down.");
	}
	
	@Test
	/**
	 * test MetaEDA processing with model file created in test2
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test3() throws EDAException, ComponentException {
		
		logger.info("MetaEDA test (mode 2) processing started");
		
		File metaconfigFile = new File("./src/test/resources/configuration-file/MetaEDATest2_DE.xml");
		
		Assume.assumeTrue(metaconfigFile.exists());
		CommonConfig metaconfig = null;
		try {
			// read in the configuration from the file
			metaconfig = new ImplCommonConfig(metaconfigFile);
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(metaconfig);
		
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		//initialize TIE instance
		EDABasic<ClassificationTEDecision> tieEDA = new MaxEntClassificationEDA(); 
		EDABasic<? extends TEDecision> meceda2 = tieEDA; 
		File mecedaconfigfile2 = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DS_DE.xml");
		CommonConfig mecedaconfig2 = null;
		try {
			// read in the configuration from the file
			mecedaconfig2 = new ImplCommonConfig(mecedaconfigfile2);
			logger.info("MaxEntClassification EDA 2 config file read");
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(mecedaconfig2);
	
		logger.info("initialize MaxEntClassification 2 and load model");
		try {
			meceda2.initialize(mecedaconfig2);
		} catch (ConfigurationException | EDAException | ComponentException e1) {
			e1.printStackTrace();
		}
		edas.add(meceda2);

		//initialize EditDistancePSOEDA
		EDABasic<TEDecision> edpsoeda = new EditDistancePSOEDA<TEDecision>();
		File edpsoedaconfigfile = new File("./src/main/resources/configuration-file/EditDistancePSOEDA_DE.xml");
		CommonConfig edpsoedaconfig = null;
		try {
			// read in the configuration from the file
			edpsoedaconfig = new ImplCommonConfig(edpsoedaconfigfile);
			logger.info("EditDistancePSO EDA config file read");
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(edpsoedaconfig);
	
		logger.info("initialize EditDistancePSO and load model");
		try {
			edpsoeda.initialize(edpsoedaconfig);
		} catch (ConfigurationException | EDAException | ComponentException e1) {
			e1.printStackTrace();
		}
		edas.add(edpsoeda);
		
		//construct meta EDA
		MetaEDA meda = new MetaEDA(edas);
		try {
			meda.initialize(metaconfig);
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
		testMetaEDA(meda);

		meda.shutdown();
		logger.info("EDA shuts down.");
	}
	
	/**
	 * performs test on testing data with initialized MetaEDA
	 * prints results to stdout
	 * @param meda
	 * @throws EDAException
	 * @throws ComponentException
	 */
	private void testMetaEDA(MetaEDA meda) throws EDAException, ComponentException {
		int correct = 0;
		int sum = 0;
		logger.info("build CASes for input sentence pairs");
		
		for (File xmi : FileUtils.listFiles(new File(meda.getTestDir()), new String[] {"xmi"}, false)){
			JCas jcas = null;
			try {
				jcas = PlatformCASProber.probeXmi(xmi, null);
			} catch (LAPException e) {
				e.printStackTrace();
			} 
			Pair pair = JCasUtil.selectSingle(jcas, Pair.class);
			DecisionLabel goldAnswer = DecisionLabel.getLabelFor(pair.getGoldAnswer()); //get gold annotation
			
			TEDecision decision = meda.process(jcas);
			if (decision.getDecision().equals(goldAnswer)){
				correct += 1;
			}
			sum += 1;
		}
		float score = (float)correct/sum;
		System.out.println("sum "+sum+" - correct "+correct+" - correct/sum = "+score+"\n");
	}
	
	
	
}
