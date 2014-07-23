package eu.excitementproject.eop.core.metaeda;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
import eu.excitementproject.eop.core.EditDistanceEDA;
import eu.excitementproject.eop.core.MaxEntClassificationEDA;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.MaltParserDE;
import eu.excitementproject.eop.lap.dkpro.MaltParserEN;
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
		
		//for german
//		Set<Integer> edanrs_DE = new HashSet<Integer>();
		//add indices for edas 
//		edanrs_DE.add(1);
//		edanrs_DE.add(2);
//		edanrs_DE.add(3);
//		edanrs_DE.add(4);
//		edanrs_DE.add(5);
//		edanrs_DE.add(6);
		
		//for english
		Set<Integer> edanrs_EN = new HashSet<Integer>();
		//add indices for edas 
//		edanrs_EN.add(1);
//		edanrs_EN.add(2);
//		edanrs_EN.add(3);
//		edanrs_EN.add(4);
		edanrs_EN.add(5);

		
		//run test
//		test.testEvalDE(edanrs_DE);
		test.testEvalEN(edanrs_EN);
	}

	
	public void testEvalDE(Set<Integer> edanrs){
		//test selected configurations and combinations
		
		//TIE:
		//1) TIE	BL, TP, TPPOS, TS, TDMPOS	RTE-3 (DE)	63,50%
		//2) TIE	BL, TP, TPPOS, TS, DBPOS, TDMPOS	RTE-3 (DE)	63,00%
		//3) TIE	BL, TP, TPPOS, TS, GNPOS, DS, TDMPOS, DBPOS	RTE-3 (DE)	63,25%
		//4) TIE	BL, TP, TPPOS, TS, DBPOS, DS	RTE-3 (DE)	62,88%
		
		//EDITS:
		//5) EDITS	Basic Wordnet
		//6) EDITS	Basic WordNet (like 5)
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		
		//initialize TIE instance
		//1) TIE	BL, TP, TPPOS, TS, TDMPOS	RTE-3 (DE)	63,50%

		if (edanrs.contains(1)){
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
		}
		
		//another one
		//2) TIE	BL, TP, TPPOS, TS, DBPOS, TDMPOS	RTE-3 (DE)	63,00%
		if (edanrs.contains(2)){
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
		}
		
		//another one
		//3) TIE	BL, TP, TPPOS, TS, GNPOS, DS, TDMPOS, DBPOS	RTE-3 (DE)	63,25%
		if (edanrs.contains(3)){
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
		}
		
		//another one
		// 4) TIE	BL, TP, TPPOS, TS, DBPOS, DS	RTE-3 (DE)	62,88%
		if (edanrs.contains(4)){
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
		}
		
		//add a EDITS instance
		//5)	EDITS	Basic WordNet
		if (edanrs.contains(5)){
			EDABasic<ClassificationTEDecision> edits1 = new EditDistanceEDA();
			EDABasic<? extends TEDecision> editseda1 = edits1;
			File editsedaconfigfile1 = new File("./src/main/resources/configuration-file/EditDistanceEDA_DE.xml");
			CommonConfig editsedaconfig1 = null;
			try {
				// read in the configuration from the file
				editsedaconfig1 = new ImplCommonConfig(editsedaconfigfile1);
				logger.info("EditDistance EDA config file read");
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
			Assume.assumeNotNull(editsedaconfig1);
		
			logger.info("initialize EditDistanceEDA and load model");
			try {
				editseda1.initialize(editsedaconfig1);
			} catch (ConfigurationException | EDAException | ComponentException e1) {
				e1.printStackTrace();
			}
			edas.add(editseda1);		
		}
		
		
		//add a EDITS instance
                //6)    EDITS   Basic WordNet
                if (edanrs.contains(6)){
                        EDABasic<ClassificationTEDecision> edits2 = new EditDistanceEDA();
                        EDABasic<? extends TEDecision> editseda2 = edits2;
                        File editsedaconfigfile2 = new File("./src/main/resources/configuration-file/EditDistanceEDA_DE.xml");
                        CommonConfig editsedaconfig2 = null;
                        try {
                                // read in the configuration from the file
                                editsedaconfig2 = new ImplCommonConfig(editsedaconfigfile2);
                                logger.info("EditDistance EDA config file read");
                        } catch (ConfigurationException e) {
                                e.printStackTrace();
                        }       
                        Assume.assumeNotNull(editsedaconfig2);
                        
                        logger.info("initialize EditDistanceEDA and load model");
                        try {
                                editseda2.initialize(editsedaconfig2);
                        } catch (ConfigurationException | EDAException | ComponentException e1) {
                                e1.printStackTrace();
                        }       
                        edas.add(editseda2);
                }   
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
		logger.info("majority vote with "+edas.size()+"internalEDAs: sum "+sumcorrect2[0]+" - correct "+sumcorrect2[1]+" ("+score2*100+"%) \n");
		
		int[] sumcorrect1= testMetaEDA(meda1);
		logger.info("\nResults for training with confidence as features ");
		float score1 = (float)sumcorrect1[1]/sumcorrect1[0];
		logger.info("classifier training with "+edas.size()+"internal EDAs: sum "+sumcorrect1[0]+" - correct "+sumcorrect1[1]+" ("+score1*100+"%) \n");
		
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
//				} catch (AnalysisEngineProcessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			} catch (LAPException e) {
				e.printStackTrace();
			} 
			Pair pair = JCasUtil.selectSingle(jcas, Pair.class);
			int pairID = Integer.parseInt(pair.getPairID());
			DecisionLabel goldAnswer = null;
			try {
				goldAnswer = DecisionLabel.getLabelFor(pair.getGoldAnswer());
			} catch (EDAException e) {
				e.printStackTrace();
			} //get gold annotation
			TEDecision decision = null;
			try {
				decision = meda.process(jcas);
				if (goldAnswer.is(DecisionLabel.NonEntailment)){
					meda.getResults().get(pairID)[0]=-1;
				}
				else if (goldAnswer.is(DecisionLabel.Entailment)){
					meda.getResults().get(pairID)[0]=1;
				}
			} catch (EDAException | ComponentException e) {
				e.printStackTrace();
			}
			if (decision.getDecision().equals(goldAnswer)){
				correct += 1;
			}
			sum += 1;
		}
		float score = (float)correct/sum;

		//comment out if you do not want to get results printed
		printDecisionTable(meda);
		printResults(sum, correct, score);
		
		int[] r = new int[2];
		r[0]=sum;
		r[1]=correct;
		return r;
	}

	/**
	 * prints test results to stdout
	 * @param sum number of pairs tested
	 * @param correct number of correctly classified pairs
	 * @param score correct/sum
	 */
	private void printResults(int sum, int correct, float score) {
		//print test results
		System.out.println("\nsum "+sum+" - correct "+correct+" ("+score*100+"%)\n");
	}

	/**
	 * prints a table with detailed overview of decisions to stdout
	 * pairID | goldLabel | BasicEDAs' decisions | MetaEDA decision
	 * values <0 -> NonEntailment
	 * values >0 -> Entailment
	 * @param meda
	 */
	private void printDecisionTable(SimpleMetaEDAConfidenceFeatures meda) {
		
		if (meda.isConfidenceAsFeature()){
			System.out.println(Arrays.deepToString(meda.getClassifier().coefficients()));
		}
		//print detailed classification overview table for test data
		HashMap<Integer, double[]> results = meda.getResults();
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%30s", "PairID")+String.format("%30s", "GoldLabel"));
		
		for (int i=0; i<meda.getEdas().size(); i++){
			sb.append(String.format("%30s", meda.getEdas().get(i).getClass().getName().replace("eu.excitementproject.eop.core.", "")));
		}
		sb.append(String.format("%30s","MetaEDA"));
		System.out.println("\n"+sb.toString());
		StringBuffer sb2 = new StringBuffer();
		for (int pairID : results.keySet()){
			sb2.append(String.format("%30d", pairID));
			for (int j=0; j<results.get(pairID).length; j++){
				sb2.append(String.format("%30f", results.get(pairID)[j]));
			}
			System.out.println(sb2.toString());
			sb2.setLength(0);
		}		
	}

	public void testEvalEN(Set<Integer> edanrs){
		//test selected configurations and combinations
//		1) TIE	Baseline + TP + TPPos + TS	0.6475 
//		2) TIE Baseline + WN + VO + TP + TPPos + TS	0.64125
//		3) TIE Baseline + WN + VO		0.625	
//		4) EDITS	Treetagger - WN (synonyns+hypernyms)	RTE-3 (EN)	64,38%
//		5) EDITS	Treetagger - WN (synonyms+hypernyms), Wikipedia	RTE-3 (EN)	63,75%

		
		ArrayList<EDABasic<? extends TEDecision>> edas = new ArrayList<EDABasic<? extends TEDecision>>();
		
		
		//initialize TIE instance
//		1) TIE	Baseline + TP + TPPos + TS	0.6475 

		if (edanrs.contains(1)){
			EDABasic<ClassificationTEDecision> eda1 = new MaxEntClassificationEDA();
			EDABasic<? extends TEDecision> meceda = eda1;
			File mecedaconfigfile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TP+TPPos+TS_EN.xml");
			CommonConfig mecedaconfig = null;
			try {
				// read in the configuration from the file
				mecedaconfig = new ImplCommonConfig(mecedaconfigfile);
				logger.info("MaxEntClassification EDA config file read");
			} catch (ConfigurationException e) {
				System.out.println(mecedaconfigfile.toString());
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
		}
		
		//another one
//		2) TIE Baseline + WN + VO + TP + TPPos + TS	0.64125
		if (edanrs.contains(2)){
			EDABasic<ClassificationTEDecision> eda2 = new MaxEntClassificationEDA();
			EDABasic<? extends TEDecision> meceda2 = eda2;
			File mecedaconfigfile2 = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+WN+VO+TP+TPPos+TS_EN.xml");
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
		}
		
		//another one
		//3) TIE Baseline + WN + VO		0.625	
		if (edanrs.contains(3)){
			EDABasic<ClassificationTEDecision> eda3 = new MaxEntClassificationEDA();
			EDABasic<? extends TEDecision> meceda3 = eda3;
			File mecedaconfigfile3 = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+WN+VO_EN.xml");
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
		}
		
		//add a EDITS instance
		//4)	EDITS	Treetagger - WN (synonyns+hypernyms)	RTE-3 (EN)	64,38%
		if (edanrs.contains(4)){
			EDABasic<ClassificationTEDecision> edits1 = new EditDistanceEDA();
			EDABasic<? extends TEDecision> editseda1 = edits1;
			File editsedaconfigfile1 = new File("./src/main/resources/configuration-file/EditDistanceEDA_WN_EN.xml");
			CommonConfig editsedaconfig1 = null;
			try {
				// read in the configuration from the file
				editsedaconfig1 = new ImplCommonConfig(editsedaconfigfile1);
				logger.info("EditDistance EDA config file read");
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
			Assume.assumeNotNull(editsedaconfig1);
		
			logger.info("initialize EditDistanceEDA and load model");
			try {
				editseda1.initialize(editsedaconfig1);
			} catch (ConfigurationException | EDAException | ComponentException e1) {
				e1.printStackTrace();
			}
			edas.add(editseda1);		
		}
		
		//add a EDITS instance
		//5) Treetagger - WN (synonyms+hypernyms), Wikipedia	RTE-3 (EN)	63,75%
		if (edanrs.contains(5)){
			EDABasic<ClassificationTEDecision> edits2 = new EditDistanceEDA();
			EDABasic<? extends TEDecision> editseda2 = edits2;
			File editsedaconfigfile2 = new File("./src/main/resources/configuration-file/EditDistanceEDA_EN.xml");
			CommonConfig editsedaconfig2 = null;
			try {
				// read in the configuration from the file
				editsedaconfig2 = new ImplCommonConfig(editsedaconfigfile2);
				logger.info("EditDistance EDA config file read");
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
			Assume.assumeNotNull(editsedaconfig2);
		
			logger.info("initialize EditDistanceEDA and load model");
			try {
				editseda2.initialize(editsedaconfig2);
			} catch (ConfigurationException | EDAException | ComponentException e1) {
				e1.printStackTrace();
			}
			edas.add(editseda2);		
		}
		
		//construct meta EDAs with parameters, not from config file
		SimpleMetaEDAConfidenceFeatures meda1 = new SimpleMetaEDAConfidenceFeatures(edas);
		SimpleMetaEDAConfidenceFeatures meda2 = new SimpleMetaEDAConfidenceFeatures(edas);
		//preprocess test and training data
		try {
			meda1.initialize("EN", true, true, "./target/MEDAModelTest1_EN.model", "./target/EN/dev/", "./target/EN/test/");
			preprocess(meda1);
			meda2.initialize("EN", false, true, "./target/MEDAModelTest2_EN.model", "./target/EN/dev/", "./target/EN/test/");
			meda1.startTraining("EN", true, true, "./target/MEDAModelTest1_EN.model", "./target/EN/dev/", "./target/EN/test/");
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
					tLap = new MaltParserEN();
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

