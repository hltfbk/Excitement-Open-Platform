package eu.excitementproject.eop.core;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;

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
import eu.excitementproject.eop.lap.dkpro.MaltParserDE;
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
	
	@Test
	public void testEvalDE(){
		//test selected configurations and combinations
		
		//config file for majority vote mode
		File metaconfigFile1 = new File("./src/test/resources/configuration-file/MetaEDAEval1_DE.xml");
		Assume.assumeTrue(metaconfigFile1.exists());
		CommonConfig metaconfig1 = null;
		try {
			// read in the configuration from the file
			metaconfig1 = new ImplCommonConfig(metaconfigFile1);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(metaconfig1);
		
		//config file for confidence as features mode
		File metaconfigFile2 = new File("./src/test/resources/configuration-file/MetaEDAEval2_DE.xml");
		Assume.assumeTrue(metaconfigFile2.exists());
		CommonConfig metaconfig2 = null;
		try {
			// read in the configuration from the file
			metaconfig2 = new ImplCommonConfig(metaconfigFile2);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(metaconfig2);
		
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		
		//initialize TIE instance
		//TIE	BL, TP, TPPOS, TS, TDMPOS	RTE-3 (DE)	63,50%

		EDABasic<ClassificationTEDecision> eda1 = new MaxEntClassificationEDA();
		EDABasic<? extends TEDecision> meceda = eda1;
		File mecedaconfigfile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TransDmPos+TP+TPPos+TS_DE.xml");
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
		
		//another one
		//TIE	BL, TP, TPPOS, TS, GNPOS, DS, TDMPOS, DBPOS	RTE-3 (DE)	63,25%

		EDABasic<ClassificationTEDecision> eda2 = new MaxEntClassificationEDA();
		EDABasic<? extends TEDecision> meceda2 = eda2;
		File mecedaconfigfile2 = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GNPos+DS+DBPos+TransDmPos+TP+TPPos+TS_DE.xml");
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
		
		//another one
		// TIE	BL, TP, TPPOS, TS, DBPOS, DS	RTE-3 (DE)	62,88%

		EDABasic<ClassificationTEDecision> eda3 = new MaxEntClassificationEDA();
		EDABasic<? extends TEDecision> meceda3 = eda3;
		File mecedaconfigfile3 = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DS+DBPos+TP+TPPos+TS_DE.xml");
		CommonConfig mecedaconfig3 = null;
		try {
			// read in the configuration from the file
			mecedaconfig3 = new ImplCommonConfig(mecedaconfigfile3);
			logger.info("MaxEntClassification EDA config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(mecedaconfig3);
	
		logger.info("initialize MaxEntClassification and load model");
		try {
			meceda3.initialize(mecedaconfig3);
		} catch (ConfigurationException | EDAException | ComponentException e1) {
			e1.printStackTrace();
		}
		edas.add(meceda3);		
		
		//construct meta EDAs
		MetaEDA meda1 = new MetaEDA(edas);
		MetaEDA meda2 = new MetaEDA(edas);
		//preprocess test and training data
		try {
			meda1.initialize(metaconfig1);
			meda2.initialize(metaconfig2);
			meda2.startTraining(metaconfig2);
//			preprocess(meda1);
			logger.info("Initialization done.");
		} catch (Exception e) {
			e.printStackTrace();
		}

//		logger.info("\nResults for majority vote ");
//		testMetaEDA(meda1);
		logger.info("\nResults for training with confidence as features ");
		testMetaEDA(meda2);

		meda1.shutdown();	
		meda2.shutdown();
		
	}
	
	@Test 
	public void testEvalEN(){
		//test selected configurations and combinations

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
		
//		MaltParser mp = new MaltParser();
		
		for (File xmi : FileUtils.listFiles(new File(meda.getTestDir()), new String[] {"xmi"}, false)){
			JCas jcas = null;
			try {
				jcas = PlatformCASProber.probeXmi(xmi, null);
//				System.out.println(jcas.getDocumentLanguage());
//				System.out.println(jcas.getDocumentText());
//				System.out.println(jcas.getSofaDataString());
//				PlatformCASProber.probeCas(jcas, null);
//				try {
//					mp.process(jcas);
//				} catch (AnalysisEngineProcessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
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
				LAPAccess mlap = null;
				try {
					tlap = new TreeTaggerDE();
					mlap = new MaltParserDE();
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
					mlap.processRawInputFormat(f, outputDirTest);
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
					mlap.processRawInputFormat(outputDirTrain, outputDirTrain);
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
