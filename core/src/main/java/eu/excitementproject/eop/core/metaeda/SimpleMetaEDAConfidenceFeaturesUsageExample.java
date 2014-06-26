package eu.excitementproject.eop.core.metaeda;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
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
import eu.excitementproject.eop.core.ClassificationTEDecision;
import eu.excitementproject.eop.core.EditDistancePSOEDA;
import eu.excitementproject.eop.core.MaxEntClassificationEDA;
import eu.excitementproject.eop.core.metaeda.SimpleMetaEDAConfidenceFeatures;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.MaltParserDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

/**
 * This class performs as a usage example with tests for <code>MetaEDA</code>.
 * The user can test some sample configurations, modify them or create and test a MetaEDA with their own configurations.
 * 
 * <code>MetaEDA</code> performs as a higher level EDA. It implements the
 * <code>EDABasic</code> interface. 
 * 
 * It uses multiple initialized EDABasic instances and their classifying results as features to make its own
 * decision.  
 * It has two modes:
 * 1) voting: each EDA's DecisionLabel counts as vote for NonEntailment or Entailment.
 * 	MetaEDA goes with the majority. In case of a tie, it decides NonEntailment. 
 *  Note that there is no training in this mode.
 * 2) confidences as features: each EDA's decision and its confidence on this decision is taken as a feature
 * 	for a classifier which is then trained on the input pairs. 
 *  If the decision is "NonEntailment", the numerical feature is the confidence*(-1), if it is "Entailment", the feature is simply the confidence. 
 * 	The trained model is stored and can be loaded again to use it for classifying new data.
 *  Training is performed with a weka classifier.
 *   
 *  MetaEDA is initialized with a configuration file, where the following parameters need to be set:
 *  - "activatedEDA": the activated EDA, has to be eu.excitementproject.eop.core.MetaEDA
 *  - "language": "EN", "DE" or any other language supported in internal EDABasics
 *  - "confidenceAsFeature": defines the mode (1 or 2), see above
 *  - "overwrite": whether to overwrite an existing model with the same name or not
 *  - "modelFile": path to model file
 *  - "trainDir": path to training data directory
 *  - "testDir": path to test data directory
 *  A sample configuration file can be found in core/src/test/resources/configuration-file/MetaEDATest1_DE.xml
 *  
 *  Alternatively, it can be initialized with the parameters parameters listed above directly,
 *  calling <code>initialize(String language, boolean confidenceAsFeatures, boolean overwrite, String modelFile, String trainDir, String testDir)</code>.
 *  We assume here that the activatedEDA is this SimpleMetaEDAConfidenceFeatures and does therefore not require passing the parameter.
 *  
 *  Please note that the following steps need to be done before initializing a SimpleMetaEDAConfidenceFeatures instance:
 *  1) All EDABasic instances used for the MetaEDA must have been initialized correctly. 
 *     The MetaEDA does not check whether they are correctly initialized.
 *     Details about how to initialize an EDABasic correctly can be found in their documentation.
 *  2) Calling process() or startTraining() requires LAP annotations on test and training data (specified in testDir and trainDir) for the given EDABasic instances. 
 *     Again, the MetaEDA does not check whether the required annotation layers are there.
 *     For details about the annotation layers required by each EDABasic, refer to the specific EDABasic's documentation.
 *  
 *  Although the examples in this class do only cover English and German, the usage of SimpleMetaEDAConfidenceFeatures is not restricted to any language.
 *  In order to use SimpleMetaEDAConfidenceFeatures for a language, the user needs all EDABasic instances to be able to handle the given language, and corresponding test and training data in RTE-format.
 *
 * @author Julia Kreutzer
 *
 */
public class SimpleMetaEDAConfidenceFeaturesUsageExample {
	static Logger logger = Logger.getLogger(SimpleMetaEDAConfidenceFeaturesUsageExample.class
			.getName());
	
	/**
	 * The main method calls the two methods "testDE" and "testEN", methods for running German or English tests respectively.
	 * The user can add own test methods to run, or comment out test methods which they wish not to run.
	 * @param args
	 */
	public static void main(String[] args){
		logger.setLevel(Level.FINE); //change level to "INFO" if you want to skip detailed information about processes
		SimpleMetaEDAConfidenceFeaturesUsageExample test = new SimpleMetaEDAConfidenceFeaturesUsageExample();
		//meta eda eval
		test.testEvalDE();
		//perform tests contained in testDE method for German
		//test.testDE();
		//perform tests contained in testEN method for English
		//test.testEN();
	}
	
	/**
	 * tests for German with sample configurations
	 * - comment out tests you wish not to run
	 * - or: modify code in called test classed
	 * test1DE: majority vote, training and testing with TIE: Base and Edits: PSO
	 * test2DE: confidence as features, training and testing with TIE: Base+DB and Edits: PSO
	 * test3DE: confidence as features, loading the model created in test2DE and testing
	 */
	public void testDE(){
		test1DE();
		test2DE(); //running all three tests takes a long time
		test3DE(); //test 2 has to run before 3
	}
	
	/**
	 * tests for English with sample configurations
	 * - comment out tests you wish not to run!
	 * - or: modify code in called test classed
	 * test1EN: similar to test1DE (majority vote), training and testing, TIE: Base and Edits: PSO
	 * test2EN: similar to test2DE (confidence as features), training and testing with TIE: Base and Edits: PSO
	 * test3EN: similar to test3DE (confidence as features), loading the model created in test2En and testing
	 */
	public void testEN(){
		test1EN();
		test2EN(); //running all three tests takes a long time
		test3EN(); //test 2 has to run before 3
	}
	
	public void testEvalDE(){
		//test selected configurations and combinations
		
		//config file for majority vote mode
		File metaconfigFile1 = new File("./src/test/resources/configuration-file/MetaEDAEval1_DE.xml");
		Assume.assumeTrue(metaconfigFile1.exists());
		CommonConfig metaconfig1 = null;
		try {
			// read in the configuration from the file
			logger.info("Reading metaEDA config file1: "+metaconfigFile1);
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
			logger.info("Reading metaEDA config file2: "+metaconfigFile2);
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
		SimpleMetaEDAConfidenceFeatures meda1 = new SimpleMetaEDAConfidenceFeatures(edas);
		SimpleMetaEDAConfidenceFeatures meda2 = new SimpleMetaEDAConfidenceFeatures(edas);
		//preprocess test and training data
		try {
			preprocess(meda1);
			meda1.initialize(metaconfig1);
			meda2.initialize(metaconfig2);
			meda2.startTraining(metaconfig2);
			logger.info("Initialization done.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("\nResults for majority vote ");
		testMetaEDA(meda1);
		logger.info("\nResults for training with confidence as features ");
		testMetaEDA(meda2);

		meda1.shutdown();	
		meda2.shutdown();
		
	}
 
	public void testEvalEN(){
		//test selected configurations and combinations

	}
	/**
	 * Tests MetaEDA in mode 1 (majority vote) with two internal EDAs for German: 1)TIE:Base and 2)Edits:PSO.	
	 * 
	 * 1) loads MetaEDA configuration file   
	 * 2) initializes TIE and Edits instance
	 * 3) constructs a MetaEDA with the two EDABasics
	 * 4) preprocess test data by calling preprocess(MetaEDA) method
	 * 5) process test data with MetaEDA (majority vote) by calling testMetaEDA(MetaEDA) method and prints results to stdout
	 * 
	 * The sample configuration file is loaded from "./src/test/resources/configuration-file/MetaEDATest1_DE.xml"
	 * Test and training data, model file directory, overwrite mode are defined there.
	 * 
	 * Each testing data sample is first processed by both internal EDAs. 
	 * Their decisions are consequently used for the MetaEDA to find a meta decision, which goes with the majority.
	 * E.g. Both EDABasics return "NonEntailment", so MetaEDA will return "NonEntailment" as well.
	 * Or the opposite case: both return "Entailment", so MetaEDA decides "Entailment" as well.
	 * If one of them votes for "Entailment", and one for "NonEntailment", MetaEDA will decide "NonEntailment", as reaction to a tie.
	 * 
	 * Of course, more than two EDABasic instances can be included. 
	 * Note that they need to be initialized before constructing a MetaEDA.
	 * 
	 * Note that some EDABasics require certain linguistic preprocessing steps.
	 * The user has to provide these annotation layers on training and test data by calling linguistic preprocessing tools in the preprocess(MetaEDA) method.
	 * For this example, data is preprocessed with TreeTagger.
	 *
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test1DE(){
		
		logger.info("SimpleMetaEDAConfidenceFeatures test (mode 1) started");
		
		File metaconfigFile = new File("./src/test/resources/configuration-file/MetaEDATest1_DE.xml");
		
		Assume.assumeTrue(metaconfigFile.exists());
		CommonConfig metaconfig = null;
		try {
			//read in the configuration from the file
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

		//initialize EditDistancePSOEDA
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
		
		//construct SimpleMetaEDAConfidenceFeatures
		SimpleMetaEDAConfidenceFeatures meda = new SimpleMetaEDAConfidenceFeatures(edas);
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
	 * Tests SimpleMetaEDAConfidenceFeatures in training mode 2 (confidence as features) with two internal EDAs for German: 1)TIE:Base+DB and 2)Edits:PSO
	 * 
	 * 1) loads SimpleMetaEDAConfidenceFeatures configuration file   
	 * 2) initializes TIE and Edits instance
	 * 3) constructs a SimpleMetaEDAConfidenceFeatures with the two EDABasics
	 * 4) preprocess test data by calling preprocess(SimpleMetaEDAConfidenceFeatures) method
	 * 5) trains the SimpleMetaEDAConfidenceFeatures on training data with EDABasic confidences as features
	 * 5) process test data with SimpleMetaEDAConfidenceFeatures (majority vote) by calling testMetaEDA(MetaEDA) method and prints results to stdout
	 * 
	 * The sample configuration file is loaded from "./src/test/resources/configuration-file/MetaEDATest2_DE.xml"
	 * Test and training data, model file directory, overwrite mode are defined there.
	 * 
	 * Each training data sample is first processed by both internal EDAs. 
	 * Their decisions are consequently used for the SimpleMetaEDAConfidenceFeatures to find a meta decision by serving as features for training of a weka classifier.
	 * E.g. One EDABasic returns "NonEntailment" and the confidence 0.4, so SimpleMetaEDAConfidenceFeatures uses the feature "-0.4" for training.
	 * Another EDABasic returns "Entailment" with confidence 0.8, so SimpleMetaEDAConfidenceFeatures uses the feature "0.8" for training.
	 * After training, the MetaEDA model is serialized and stored in the file defined in the configuration.
	 * 
	 * Of course, more than two EDABasic instances can be included. 
	 * Note that they need to be initialized before constructing a SimpleMetaEDAConfidenceFeatures.
	 *
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test2DE() {
		
		logger.info("SimpleMetaEDAConfidenceFeatures test (mode 2) started");
		
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
		SimpleMetaEDAConfidenceFeatures meda = new SimpleMetaEDAConfidenceFeatures(edas);
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
	 * Tests SimpleMetaEDAConfidenceFeatures processing with model file created in test2DE for German (two internal EDAs for German: 1)TIE:Base+DB and 2)Edits:PSO).
	 * 
	 * 1) loads SimpleMetaEDAConfidenceFeatures configuration file   
	 * 2) initializes TIE and Edits instance
	 * 3) constructs a SimpleMetaEDAConfidenceFeatures with the two EDABasics and loads trained model
	 * 4) preprocess test data by calling preprocess(MetaEDA) method
	 * 5) process test data with SimpleMetaEDAConfidenceFeatures (majority vote) by calling testMetaEDA(MetaEDA) method and prints results to stdout
	 * 
	 * The sample configuration file is loaded from "./src/test/resources/configuration-file/MetaEDATest3_DE.xml"
	 * Test and training data, model file directory, overwrite mode are defined there.
	 * 
	 * Each test data sample is first processed by both internal EDAs. 
	 * Their decisions are consequently used for the SimpleMetaEDAConfidenceFeatures to find a meta decision by serving as features for classifying with the pre-trained weka classifier.
	 * E.g. One EDABasic returns "NonEntailment" and the confidence 0.4, so SimpleMetaEDAConfidenceFeatures uses the feature "-0.4" for classifying.
	 * Another EDABasic returns "Entailment" with confidence 0.8, so SimpleMetaEDAConfidenceFeatures uses the feature "0.8" for classifying.
	 * 
	 * For consistencies sake it is important to initialize and use the same EDABasics as in test2DE().
	 * Note that they need to be initialized before constructing a SimpleMetaEDAConfidenceFeatures.
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test3DE(){
		
		logger.info("SimpleMetaEDAConfidenceFeatures test (mode 2) processing started");
		
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
		SimpleMetaEDAConfidenceFeatures meda = new SimpleMetaEDAConfidenceFeatures(edas);
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
	 * Tests SimpleMetaEDAConfidenceFeatures in mode 1 (majority vote) with two internal EDAs for English: 1)TIE:Base and 2)Edits:PSO.	
	 * 
	 * 1) loads SimpleMetaEDAConfidenceFeatures configuration file   
	 * 2) initializes TIE and Edits instance
	 * 3) constructs a SimpleMetaEDAConfidenceFeatures with the two EDABasics
	 * 4) preprocess test data by calling preprocess(MetaEDA) method
	 * 5) process test data with SimpleMetaEDAConfidenceFeatures (majority vote) by calling testMetaEDA(MetaEDA) method and prints results to stdout
	 * 
	 * The sample configuration file is loaded from "./src/test/resources/configuration-file/MetaEDATest1_EN.xml"
	 * Test and training data, model file directory, overwrite mode are defined there.
	 * 
	 * Each testing data sample is first processed by both internal EDAs. 
	 * Their decisions are consequently used for the SimpleMetaEDAConfidenceFeatures to find a meta decision, which goes with the majority.
	 * E.g. Both EDABasics return "NonEntailment", so SimpleMetaEDAConfidenceFeatures will return "NonEntailment" as well.
	 * Or the opposite case: both return "Entailment", so SimpleMetaEDAConfidenceFeatures decides "Entailment" as well.
	 * If one of them votes for "Entailment", and one for "NonEntailment", SimpleMetaEDAConfidenceFeatures will decide "NonEntailment", as reaction to a tie.
	 * 
	 * Of course, more than two EDABasic instances can be included. 
	 * Note that they need to be initialized before constructing a SimpleMetaEDAConfidenceFeatures.
	 * 
	 * Note that some EDABasics require certain linguistic preprocessing steps.
	 * The user has to provide these annotation layers on training and test data by calling linguistic preprocessing tools in the preprocess(MetaEDA) method.
	 * For this example, data is preprocessed with TreeTagger.
	 *
	 * @throws EDAException
	 * @throws ComponentException
	 * @throws ConfigurationException 
	 */
	public void test1EN() {
		
		logger.info("SimpleMetaEDAConfidenceFeatures test (mode 1) started");
		
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
		SimpleMetaEDAConfidenceFeatures meda = new SimpleMetaEDAConfidenceFeatures(edas);
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
	 * Tests SimpleMetaEDAConfidenceFeatures in training mode 2 (confidence as features) with two internal EDAs for English: 1)TIE:Base+DB and 2)Edits:PSO
	 * 
	 * 1) loads SimpleMetaEDAConfidenceFeatures configuration file   
	 * 2) initializes TIE and Edits instance
	 * 3) constructs a SimpleMetaEDAConfidenceFeatures with the two EDABasics
	 * 4) preprocess test data by calling preprocess(MetaEDA) method
	 * 5) trains the SimpleMetaEDAConfidenceFeatures on training data with EDABasic confidences as features
	 * 5) process test data with SimpleMetaEDAConfidenceFeatures (majority vote) by calling testMetaEDA(MetaEDA) method and prints results to stdout
	 * 
	 * The sample configuration file is loaded from "./src/test/resources/configuration-file/MetaEDATest2_EN.xml"
	 * Test and training data, model file directory, overwrite mode are defined there.
	 * 
	 * Each training data sample is first processed by both internal EDAs. 
	 * Their decisions are consequently used for the SimpleMetaEDAConfidenceFeatures to find a meta decision by serving as features for training of a weka classifier.
	 * E.g. One EDABasic returns "NonEntailment" and the confidence 0.4, so SimpleMetaEDAConfidenceFeatures uses the feature "-0.4" for training.
	 * Another EDABasic returns "Entailment" with confidence 0.8, so SimpleMetaEDAConfidenceFeatures uses the feature "0.8" for training.
	 * After training, the SimpleMetaEDAConfidenceFeatures model is serialized and stored in the file defined in the configuration.
	 * 
	 * Of course, more than two EDABasic instances can be included. 
	 * Note that they need to be initialized before constructing a SimpleMetaEDAConfidenceFeatures.
	 *
	 * Note that some EDABasics require certain linguistic preprocessing steps.
	 * The user has to provide these annotation layers on training and test data by calling linguistic preprocessing tools in the preprocess(MetaEDA) method.
	 * For this example, data is preprocessed with TreeTagger.
	 * 
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test2EN(){
		
		logger.info("SimpleMetaEDAConfidenceFeatures test (mode 2) started");
		
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
		SimpleMetaEDAConfidenceFeatures meda = new SimpleMetaEDAConfidenceFeatures(edas);
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
	 * Tests SimpleMetaEDAConfidenceFeatures processing with model file created in test2EN for English (two internal EDAs for English: 1)TIE:Base+DB and 2)Edits:PSO).
	 * 
	 * 1) loads SimpleMetaEDAConfidenceFeatures configuration file   
	 * 2) initializes TIE and Edits instance
	 * 3) constructs a SimpleMetaEDAConfidenceFeatures with the two EDABasics and loads trained model
	 * 4) preprocess test data by calling preprocess(MetaEDA) method
	 * 5) process test data with SimpleMetaEDAConfidenceFeatures (majority vote) by calling testMetaEDA(MetaEDA) method and prints results to stdout
	 * 
	 * The sample configuration file is loaded from "./src/test/resources/configuration-file/MetaEDATest3_EN.xml"
	 * Test and training data, model file directory, overwrite mode are defined there.
	 * 
	 * Each test data sample is first processed by both internal EDAs. 
	 * Their decisions are consequently used for the SimpleMetaEDAConfidenceFeatures to find a meta decision by serving as features for classifying with the pre-trained weka classifier.
	 * E.g. One EDABasic returns "NonEntailment" and the confidence 0.4, so SimpleMetaEDAConfidenceFeatures uses the feature "-0.4" for classifying.
	 * Another EDABasic returns "Entailment" with confidence 0.8, so SimpleMetaEDAConfidenceFeatures uses the feature "0.8" for classifying.
	 * 
	 * For consistencies sake it is important to initialize and use the same EDABasics as in test2EN().
	 * Note that they need to be initialized before constructing a SimpleMetaEDAConfidenceFeatures.
	 * 
	 * Note that some EDABasics require certain linguistic preprocessing steps.
	 * The user has to provide these annotation layers on training and test data by calling linguistic preprocessing tools in the preprocess(MetaEDA) method.
	 * For this example, data is preprocessed with TreeTagger.
	 * 
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public void test3EN(){
		
		logger.info("SimpleMetaEDAConfidenceFeatures test (mode 2) processing started");
		
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
		SimpleMetaEDAConfidenceFeatures meda = new SimpleMetaEDAConfidenceFeatures(edas);
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
	 * set to true once pre-processing for German training and test data is done
	 */
	private boolean preprocessedDE = false; 

	/**
	 * set to true once pre-processing for English training and test data is done
	 */
	private boolean preprocessedEN = false;

	/**
	 * Performs test on testing data with initialized MetaEDA and
	 * prints results to stdout.
	 * 
	 * First, CASes are built for input data.
	 * Then, they are processed by MetaEDA.
	 * Finally, results are printed. This includes the number of correctly predicted T-H-pairs, the number of all input pairs, and the percentage of correct predictions.
	 * 
	 * @param meda initialized MetaEDA
	 * @throws EDAException
	 * @throws ComponentException
	 */
	private void testMetaEDA(SimpleMetaEDAConfidenceFeatures meda){
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
		System.out.println("sum "+sum+" - correct "+correct+" ("+score*100+"%) \n");
	}
	
	/**
	 * Pre-processes the T-H pairs with TreeTagger
	 *
	 * Note that some EDABasics require certain linguistic preprocessing steps.
	 * The user has to provide these annotation layers on training and test data by calling linguistic preprocessing tools here.
	 * 
	 * @param meda initialized MetaEDA
	 * @throws LAPException 
	 */
	private void preprocess(SimpleMetaEDAConfidenceFeatures meda){
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
