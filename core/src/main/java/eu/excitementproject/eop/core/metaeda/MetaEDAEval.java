package eu.excitementproject.eop.core.metaeda;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
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
import eu.excitementproject.eop.core.ClassificationTEDecision;
import eu.excitementproject.eop.core.MaxEntClassificationEDA;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.MaltParserDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

public class MetaEDAEval {

	
	static Logger logger = Logger.getLogger(SimpleMetaEDAConfidenceFeaturesUsageExample.class.getName());
	private boolean preprocessedDE = false; 
	private boolean preprocessedEN = false;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.setLevel(Level.FINE);
		
		MetaEDAEval test = new MetaEDAEval();
		//test eval method
		test.testEvalDE();
	}

	
	public void testEvalDE(){
		//test selected configurations and combinations
		
//		//config file for majority vote mode
//		File metaconfigFile1 = new File("./src/test/resources/configuration-file/MetaEDAEval1_DE.xml");
//		Assume.assumeTrue(metaconfigFile1.exists());
//		CommonConfig metaconfig1 = null;
//		try {
//			// read in the configuration from the file
//			metaconfig1 = new ImplCommonConfig(metaconfigFile1);
//		} catch (ConfigurationException e) {
//			e.printStackTrace();
//		}
//		Assume.assumeNotNull(metaconfig1);
//		
//		//config file for confidence as features mode
//		File metaconfigFile2 = new File("./src/test/resources/configuration-file/MetaEDAEval2_DE.xml");
//		Assume.assumeTrue(metaconfigFile2.exists());
//		CommonConfig metaconfig2 = null;
//		try {
//			// read in the configuration from the file
//			metaconfig2 = new ImplCommonConfig(metaconfigFile2);
//		} catch (ConfigurationException e) {
//			e.printStackTrace();
//		}
//		Assume.assumeNotNull(metaconfig2);
		
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		
		//initialize TIE instance
		//1) TIE	BL, TP, TPPOS, TS, TDMPOS	RTE-3 (DE)	63,50%

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
		//2) TIE	BL, TP, TPPOS, TS, DBPOS, TDMPOS	RTE-3 (DE)	63,00%

		EDABasic<ClassificationTEDecision> eda2 = new MaxEntClassificationEDA();
		EDABasic<? extends TEDecision> meceda2 = eda2;
		File mecedaconfigfile2 = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DBPos+TransDmPos+TP+TPPos+TS_DE.xml");
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
		//3) TIE	BL, TP, TPPOS, TS, GNPOS, DS, TDMPOS, DBPOS	RTE-3 (DE)	63,25%

		EDABasic<ClassificationTEDecision> eda3 = new MaxEntClassificationEDA();
		EDABasic<? extends TEDecision> meceda3 = eda3;
		File mecedaconfigfile3 = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GNPos+DS+DBPos+TransDmPos+TP+TPPos+TS_DE.xml");
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
		
		//another one
		// 4) TIE	BL, TP, TPPOS, TS, DBPOS, DS	RTE-3 (DE)	62,88%

		EDABasic<ClassificationTEDecision> eda4 = new MaxEntClassificationEDA();
		EDABasic<? extends TEDecision> meceda4 = eda4;
		File mecedaconfigfile4 = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DS+DBPos+TP+TPPos+TS_DE.xml");
		CommonConfig mecedaconfig4 = null;
		try {
			// read in the configuration from the file
			mecedaconfig4 = new ImplCommonConfig(mecedaconfigfile4);
			logger.info("MaxEntClassification EDA config file read");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(mecedaconfig4);
	
		logger.info("initialize MaxEntClassification and load model");
		try {
			meceda4.initialize(mecedaconfig4);
		} catch (ConfigurationException | EDAException | ComponentException e1) {
			e1.printStackTrace();
		}
		edas.add(meceda4);		
		
		//construct meta EDAs with parameters, not from config file
		SimpleMetaEDAConfidenceFeatures meda1 = new SimpleMetaEDAConfidenceFeatures(edas);
		SimpleMetaEDAConfidenceFeatures meda2 = new SimpleMetaEDAConfidenceFeatures(edas);
		
		
		try {
			meda1.initialize("DE", true, true, "./target/MEDAModelTest1_DE.model", "./target/DE/dev/", "./target/DE/test/");
			preprocess(meda1);
			meda2.initialize("DE", false, true, "./target/MEDAModelTest2_DE.model", "./target/DE/dev/", "./target/DE/test/");
			meda1.startTraining("DE", true, true, "./target/MEDAModelTest1_DE.model", "./target/DE/dev/", "./target/DE/test/");
			logger.info("Initialization done.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		int[] sumcorrect2 = testMetaEDA(meda2);
		float score2 = (float)sumcorrect2[1]/sumcorrect2[0];
		logger.info("\nResults for majority vote ");
		logger.info("sum "+sumcorrect2[0]+" - correct "+sumcorrect2[1]+" ("+score2*100+"%) \n");
		
		int[] sumcorrect1= testMetaEDA(meda1);
		logger.info("\nResults for training with confidence as features ");
		float score1 = (float)sumcorrect1[1]/sumcorrect1[0];
		logger.info("sum "+sumcorrect1[0]+" - correct "+sumcorrect1[1]+" ("+score1*100+"%) \n");
		
		meda1.shutdown();	
		meda2.shutdown();
		
	}
	
	private int[] testMetaEDA(SimpleMetaEDAConfidenceFeatures meda){
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
		int[] sumcorrect = new int[2];
		sumcorrect[0] = sum;
		sumcorrect[1] = correct;
		return sumcorrect;
	}
	
	private void preprocess(SimpleMetaEDAConfidenceFeatures meda){
		if (!this.preprocessedDE){
			if (meda.getLanguage().equals("DE")){
				logger.info("preprocessing German training and test data.");
//				LAPAccess tlap = null;
				LAPAccess mlap = null;
				try {
//					tlap = new TreeTaggerDE();
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
//					tlap.processRawInputFormat(f, outputDirTest);
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
//					tlap.processRawInputFormat(g, outputDirTrain);
					mlap.processRawInputFormat(g, outputDirTrain);
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
