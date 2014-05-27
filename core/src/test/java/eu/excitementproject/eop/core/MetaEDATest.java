package eu.excitementproject.eop.core;

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
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

public class MetaEDATest {
	static Logger logger = Logger.getLogger(MetaEDATest.class
			.getName());
	private boolean preprocessedDE = false; //set to true once preprocessing for train and test data is done
	private boolean preprocessedEN = false;
	
	@Test
	public void testDE(){
		test1DE();
//		test2DE(); //running all three tests takes a long time
//		test3DE(); //test 2 has to run before 3
	}
	
	@Test
	public void testEN(){
		test1EN();
//		test2EN(); //running all three tests takes a long time
//		test3EN(); //test 2 has to run before 3
	}
	
	
	/**
	 * test MetaEDA in mode 1 (majority vote) with two internal EDAs for German
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test1DE(){
		
		logger.info("MetaEDA test (mode 1) started");
		
		File metaconfigFile = new File("./src/test/resources/configuration-file/MetaEDATest1_DE.xml");
		
		Assume.assumeTrue(metaconfigFile.exists());
		CommonConfig metaconfig = null;
		try {
			// read in the configuration from the file
			metaconfig = new ImplCommonConfig(metaconfigFile);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(metaconfig);
		
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		//initialize TIE instance
		EDABasic<ClassificationTEDecision> eda1 = new MaxEntClassificationEDA();
		EDABasic<? extends TEDecision> meceda = eda1;
		File mecedaconfigfile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base_DE.xml");
		CommonConfig mecedaconfig = null;
		try {
			// read in the configuration from the file
			mecedaconfig = new ImplCommonConfig(mecedaconfigfile);
			logger.info("MaxEntClassification EDA config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
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
		EditDistancePSOEDA<TEDecision> eda2 = new EditDistancePSOEDA<TEDecision>();
		EDABasic<? extends TEDecision> edpsoeda = eda2;
		File edpsoedaconfigfile = new File("./src/main/resources/configuration-file/EditDistancePSOEDA_DE.xml");
		CommonConfig edpsoedaconfig = null;
		try {
			// read in the configuration from the file
			edpsoedaconfig = new ImplCommonConfig(edpsoedaconfigfile);
			logger.info("EditDistancePSO EDA config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
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
		//preprocess test and training data
		try {
			meda.initialize(metaconfig);
			preprocess(meda);
			logger.info("Initialization done.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		testMetaEDA(meda);

		meda.shutdown();
		logger.info("EDA shuts down.");
	}


	/**
	 * test MetaEDA in training mode 2 (confidence as features) with two internal EDAs for German
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test2DE() {
		
		logger.info("MetaEDA test (mode 2) started");
		
		File metaconfigFile = new File("./src/test/resources/configuration-file/MetaEDATest2_DE.xml");
		
		Assume.assumeTrue(metaconfigFile.exists());
		CommonConfig metaconfig = null;
		try {
			// read in the configuration from the file
			metaconfig = new ImplCommonConfig(metaconfigFile);
		} catch (ConfigurationException e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
		EditDistancePSOEDA<TEDecision> eda3 = new EditDistancePSOEDA<TEDecision>();
		EDABasic<? extends TEDecision> edpsoeda = eda3;
		File edpsoedaconfigfile = new File("./src/main/resources/configuration-file/EditDistancePSOEDA_DE.xml");
		CommonConfig edpsoedaconfig = null;
		try {
			// read in the configuration from the file
			edpsoedaconfig = new ImplCommonConfig(edpsoedaconfigfile);
			logger.info("EditDistancePSO EDA config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
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
		//preprocess test and training data
		try {
			meda.initialize(metaconfig);
			preprocess(meda);
			meda.startTraining(metaconfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		testMetaEDA(meda);

		meda.shutdown();
		logger.info("EDA shuts down.");
	}
	

	/**
	 * test MetaEDA processing with model file created in test2DE for German
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test3DE(){
		
		logger.info("MetaEDA test (mode 2) processing started");
		
		File metaconfigFile = new File("./src/test/resources/configuration-file/MetaEDATest2_DE.xml");
		
		Assume.assumeTrue(metaconfigFile.exists());
		CommonConfig metaconfig = null;
		try {
			// read in the configuration from the file
			metaconfig = new ImplCommonConfig(metaconfigFile);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(metaconfig);
		
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		//initialize TIE instance
		EDABasic<ClassificationTEDecision> eda4 = new MaxEntClassificationEDA(); 
		EDABasic<? extends TEDecision> meceda2 = eda4; 
		File mecedaconfigfile2 = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DS_DE.xml");
		CommonConfig mecedaconfig2 = null;
		try {
			// read in the configuration from the file
			mecedaconfig2 = new ImplCommonConfig(mecedaconfigfile2);
			logger.info("MaxEntClassification EDA config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(mecedaconfig2);
	
		logger.info("initialize MaxEntClassification and load model");
		try {
			meceda2.initialize(mecedaconfig2);
		} catch (ConfigurationException | EDAException | ComponentException e1) {
			e1.printStackTrace();
		}
		edas.add(meceda2);

		//initialize EditDistancePSOEDA
		EditDistancePSOEDA<TEDecision> eda5 = new EditDistancePSOEDA<TEDecision>();
		EDABasic<? extends TEDecision> edpsoeda = eda5;
		File edpsoedaconfigfile = new File("./src/main/resources/configuration-file/EditDistancePSOEDA_DE.xml");
		CommonConfig edpsoedaconfig = null;
		try {
			// read in the configuration from the file
			edpsoedaconfig = new ImplCommonConfig(edpsoedaconfigfile);
			logger.info("EditDistancePSO EDA config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
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
		//preprocess test and training data
		try {
			meda.setTest(true);
			meda.initialize(metaconfig);
			preprocess(meda);
		} catch (Exception e) {
			e.printStackTrace();
		}
		testMetaEDA(meda);

		meda.shutdown();
		logger.info("EDA shuts down.");
	}
	
	/**
	 * test MetaEDA in mode 1 (majority vote) with two internal EDAs for English
	 * @throws EDAException
	 * @throws ComponentException
	 * @throws ConfigurationException 
	 */
	public void test1EN() {
		
		logger.info("MetaEDA test (mode 1) started");
		
		File metaconfigFile = new File("./src/test/resources/configuration-file/MetaEDATest1_EN.xml");
		
		Assume.assumeTrue(metaconfigFile.exists());
		CommonConfig metaconfig = null;
		// read in the configuration from the file
		try {
			metaconfig = new ImplCommonConfig(metaconfigFile);
		} catch (ConfigurationException e2) {
			e2.printStackTrace();
		}
		Assume.assumeNotNull(metaconfig);
		
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		//initialize TIE instance
		EDABasic<ClassificationTEDecision> eda1 = new MaxEntClassificationEDA();
		EDABasic<? extends TEDecision> meceda = eda1;
		File mecedaconfigfile = new File("./src/test/resources/configuration-file/MaxEntClassificationEDA_Base_EN.xml");
		CommonConfig mecedaconfig = null;
		// read in the configuration from the file
		try {
			mecedaconfig = new ImplCommonConfig(mecedaconfigfile);
		} catch (ConfigurationException e2) {
			e2.printStackTrace();
		}
		logger.info("MaxEntClassification EDA config file read");
		Assume.assumeNotNull(mecedaconfig);
	
		logger.info("initialize MaxEntClassification and load model");
		try {
			meceda.initialize(mecedaconfig);
		} catch (ConfigurationException | EDAException | ComponentException e1) {
			e1.printStackTrace();
		}
		edas.add(meceda);

//		//initialize EditDistancePSOEDA
		EditDistancePSOEDA<TEDecision> eda2 = new EditDistancePSOEDA<TEDecision>();
		EDABasic<? extends TEDecision> edpsoeda = eda2;
		File edpsoedaconfigfile = new File("./src/main/resources/configuration-file/EditDistancePSOEDA_EN.xml");
		CommonConfig edpsoedaconfig = null;
		try {
			// read in the configuration from the file
			edpsoedaconfig = new ImplCommonConfig(edpsoedaconfigfile);
			logger.info("EditDistancePSO EDA config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
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
		//preprocess test and training data
		try {
			meda.initialize(metaconfig);
			preprocess(meda);
			logger.info("Initialization done.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		testMetaEDA(meda);

		meda.shutdown();
		logger.info("EDA shuts down.");
	}

	/**
	 * test MetaEDA in training mode 2 (confidence as features) with two internal EDAs for English
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test2EN(){
		
		logger.info("MetaEDA test (mode 2) started");
		
		File metaconfigFile = new File("./src/test/resources/configuration-file/MetaEDATest2_EN.xml");
		
		Assume.assumeTrue(metaconfigFile.exists());
		CommonConfig metaconfig = null;
		try {
			// read in the configuration from the file
			metaconfig = new ImplCommonConfig(metaconfigFile);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(metaconfig);
		
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		//initialize TIE instance
		EDABasic<ClassificationTEDecision> tieEDA = new MaxEntClassificationEDA(); 
		EDABasic<? extends TEDecision> meceda2 = tieEDA; 
		File mecedaconfigfile2 = new File("./src/test/resources/configuration-file/MaxEntClassificationEDA_Base_EN.xml");
		CommonConfig mecedaconfig2 = null;
		try {
			// read in the configuration from the file
			mecedaconfig2 = new ImplCommonConfig(mecedaconfigfile2);
			logger.info("MaxEntClassification EDA 2 config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
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
		EditDistancePSOEDA<TEDecision> eda3 = new EditDistancePSOEDA<TEDecision>();
		EDABasic<? extends TEDecision> edpsoeda = eda3;
		File edpsoedaconfigfile = new File("./src/main/resources/configuration-file/EditDistancePSOEDA_EN.xml");
		CommonConfig edpsoedaconfig = null;
		try {
			// read in the configuration from the file
			edpsoedaconfig = new ImplCommonConfig(edpsoedaconfigfile);
			logger.info("EditDistancePSO EDA config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
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
		//preprocess test and training data
		try {
			meda.initialize(metaconfig);
			preprocess(meda);
			meda.startTraining(metaconfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		testMetaEDA(meda);

		meda.shutdown();
		logger.info("EDA shuts down.");
	}
	

	/**
	 * test MetaEDA processing with model file created in test2EN for English
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test3EN(){
		
		logger.info("MetaEDA test (mode 2) processing started");
		
		File metaconfigFile = new File("./src/test/resources/configuration-file/MetaEDATest2_EN.xml");
		
		Assume.assumeTrue(metaconfigFile.exists());
		CommonConfig metaconfig = null;
		try {
			// read in the configuration from the file
			metaconfig = new ImplCommonConfig(metaconfigFile);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(metaconfig);
		
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		//initialize TIE instance
		EDABasic<ClassificationTEDecision> eda4 = new MaxEntClassificationEDA(); 
		EDABasic<? extends TEDecision> meceda2 = eda4; 
		File mecedaconfigfile2 = new File("./src/test/resources/configuration-file/MaxEntClassificationEDA_Base_EN.xml");
		CommonConfig mecedaconfig2 = null;
		try {
			// read in the configuration from the file
			mecedaconfig2 = new ImplCommonConfig(mecedaconfigfile2);
			logger.info("MaxEntClassification EDA 2 config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
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
		EditDistancePSOEDA<TEDecision> eda5 = new EditDistancePSOEDA<TEDecision>();
		EDABasic<? extends TEDecision> edpsoeda = eda5;
		File edpsoedaconfigfile = new File("./src/main/resources/configuration-file/EditDistancePSOEDA_EN.xml");
		CommonConfig edpsoedaconfig = null;
		try {
			// read in the configuration from the file
			edpsoedaconfig = new ImplCommonConfig(edpsoedaconfigfile);
			logger.info("EditDistancePSO EDA config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
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
		//preprocess test and training data
		try {
			meda.setTest(true);
			meda.initialize(metaconfig);
			preprocess(meda);
		} catch (Exception e) {
			e.printStackTrace();
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
	private void testMetaEDA(MetaEDA meda){
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
			DecisionLabel goldAnswer = null;
			try {
				goldAnswer = DecisionLabel.getLabelFor(pair.getGoldAnswer());
			} catch (EDAException e) {
				e.printStackTrace();
			} //get gold annotation
			TEDecision decision = null;
			try {
				decision = meda.process(jcas);
			} catch (EDAException | ComponentException e) {
				e.printStackTrace();
			}
			if (decision.getDecision().equals(goldAnswer)){
				correct += 1;
			}
			sum += 1;
		}
		float score = (float)correct/sum;
		System.out.println("sum "+sum+" - correct "+correct+" - correct/sum = "+score+"\n");
	}
	
	/**
	 * preprocesses the T-H pairs with TreeTagger
	 * @param meda
	 * @throws LAPException 
	 */
	private void preprocess(MetaEDA meda){
		if (!this.preprocessedDE){
			if (meda.getLanguage().equals("DE")){
				logger.info("preprocessing German training and test data.");
				LAPAccess tlap = null;
				try {
					tlap = new TreeTaggerDE();
				} catch (LAPException e) {
					e.printStackTrace();
				}
				 
				File f = new File("./src/main/resources/data-set/German_test.xml");
				File outputDirTest = new File(meda.getTestDir());
				if (!outputDirTest.exists()) {
					outputDirTest.mkdirs();
				}
				//file pre-processing
				try {
					tlap.processRawInputFormat(f, outputDirTest);
				} catch (LAPException e) {
					e.printStackTrace();
				}
				 
				File g = new File("./src/main/resources/data-set/German_dev.xml");
				File outputDirTrain = new File(meda.getTrainDir());
				if (!outputDirTrain.exists()) {
					outputDirTrain.mkdirs();
				}
				try {
					tlap.processRawInputFormat(g, outputDirTrain);
				} catch (LAPException e) {
					e.printStackTrace();
				}
				this.preprocessedDE = true;
			}
		}
		if (!this.preprocessedEN){
			if (meda.getLanguage().equals("EN")){
				logger.info("preprocessing English training and test data.");
				LAPAccess tLap = null;
				try {
					tLap = new TreeTaggerEN();
				} catch (LAPException e) {
					e.printStackTrace();
				}
				
				File f = new File("./src/main/resources/data-set/English_test.xml");
				File outputDirTest = new File(meda.getTestDir());
				if (!outputDirTest.exists()) {
				 outputDirTest.mkdirs();
				}
				//file pre-processing
				try {
					tLap.processRawInputFormat(f, outputDirTest);
				} catch (LAPException e) {
					e.printStackTrace();
				}
				
				File g = new File("./src/main/resources/data-set/English_dev.xml");
				File outputDirTrain = new File(meda.getTrainDir());
				if (!outputDirTrain.exists()) {
					outputDirTrain.mkdirs();
				}
				try {
					tLap.processRawInputFormat(g, outputDirTrain);
				} catch (LAPException e) {
					e.printStackTrace();
				}
				this.preprocessedEN = true;
			}	
		}
	}
	
}
