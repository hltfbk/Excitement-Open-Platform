package eu.excitementproject.eop.core;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
//import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.MaltParserDE;
import eu.excitementproject.eop.lap.dkpro.MaltParserEN;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

/**
 * The test contains several tests:
 * 0) The main entrance test is <code>test()</code>.
 * 1) use LAP to do preprocessing and generate the XMI files (<code>testLAP_DE()</code> for German and <code>testLAP_EN()</code> for English) in the directory "./target/";
 * 2) train MaxEnt models using the configuration file (<code>testTraining()</code>);
 * 3) test the input example(s) using trained model via configuration file (<code>testTesting_SingleTH</code>);
 * 4) batch process the examples using trained model via configuration file (<code>testTesting_MultiTH</code>);
 * 5) batch process the examples using trained model via configuration file and output the result in txt file (<code>testTesting_MultiTH_AND_Output</code>);
 * 
 * Note that in order to run tests 2)-5), you MUST generate the XMI files first (e.g., using 1)). And test 1) takes quite a long time.
 * 
 * Make sure that the trained model is consistent with the testing configuration, in particular the language flag!
 * 
 * @author Rui
 */
public class MaxEntClassificationEDATest {
	static Logger logger = Logger.getLogger(MaxEntClassificationEDATest.class
			.getName());
	
	@Test
	public void test() {		

		/** German */
		/* Baseline: BagOfWords, BagOfLemmas */
		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base_DE.xml");
//		0.61125
		
		/* GermaNet: GermaNet without POS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_GN_DE.xml");
//		0.51125
		
		/* GermaNetPos: GermaNet with POS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_GNPos_DE.xml");
//		0.51125
		
		/* Baseline + GermaNet */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GN_DE.xml");
//		0.61875
		
		/* Baseline + GermaNetPos */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GNPos_DE.xml");
//		0.615
		
		/* DistSim: distributional similarity */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_DS_DE.xml");
//		0.51125
		
		/* Baseline + DistSim */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DS_DE.xml");
//		0.6125
		
		/* DBPos: DerivBase with POS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_DBPos_DE.xml");
//		0.51125
		
		/* Baseline + DBPos */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DBPos_DE.xml");
//		0.61375
		
		/* Baseline + GermaNet + DistSim */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GN+DS_DE.xml");
//		0.62
		
		/* Baseline + GermaNetPos + DistSim */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GNPos+DS_DE.xml");
//		0.61375
		
		/* Baseline + DistSim + DBPos */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DS+DBPos_DE.xml");
//		0.60875
		
		/* Baseline + GermaNetPos + DistSim + DBPos */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GNPos+DS+DBPos_DE.xml");
//		0.61625
		
		/* TP: dependency triples without POS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_TP_DE.xml");
//		0.58625
		
		/* TPPos: dependency triples with POS */		
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_TPPos_DE.xml");
//		0.5875
		
		/* Baseline + TP */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TP_DE.xml");
//		0.62375
		
		/* Baseline + TPPos */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TPPos_DE.xml");
//		0.63125
		
		/* TS: tree skeleton scoring */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_TS_DE.xml");
//		0.58
		
		/* Baseline + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TS_DE.xml");
//		0.625
		
		/* Baseline + TP + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TP+TS_DE.xml");
//		0.62875
		
		/* Baseline + TPPos + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TPPos+TS_DE.xml");
//		0.63375
		
		/* Baseline + TP + TPPos + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TP+TPPos+TS_DE.xml");
//		0.63375
		
		/* Baseline + DS + TPPos + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DS+TPPos+TS_DE.xml");
//		0.62375
		
		/* Baseline + GNPos + DS + TPPos + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GNPos+DS+TPPos+TS_DE.xml");
//		0.63
		
		/* Baseline + GNPos + DS + DBPos + TPPos + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GNPos+DS+DBPos+TPPos+TS_DE.xml");
//		0.63125
				
		/* TransDm */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_TransDm_DE.xml");
//		0.51125
		
		/* TransDm with POS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_TransDmPos_DE.xml");
//		0.51125
		
		/* Baseline + TransDm */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TransDm_DE.xml");
//		0.61625
		
		/* Baseline + TransDm with POS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TransDmPos_DE.xml");
//		0.62
		
		/* Baseline + TransDm with POS + TP + TPPOS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TransDmPos+TP+TPPos_DE.xml");
//		0.6275
		
		/* Baseline + TransDm with POS + TP + TPPOS + TS  */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TransDmPos+TP+TPPos+TS_DE.xml");
//		0.635
		
		/* Baseline + GermaNet + DerivBase + TransDm with POS + TP + TPPOS + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GNPos+DBPos+TransDmPos+TP+TPPos+TS_DE.xml");
//		0.635
		
		/* Baseline + GermaNet + TransDm with POS + TP + TPPOS + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GNPos+TransDmPos+TP+TPPos+TS_DE.xml");
//		0.63625
				
		/* Baseline + DerivBase + TransDm with POS + TP + TPPOS + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DBPos+TransDmPos+TP+TPPos+TS_DE.xml");
//		0.63	
		
		/* Baseline + GermaNet + DistSim + DerivBase + TransDm with POS + TP + TPPOS + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GNPos+DS+DBPos+TransDmPos+TP+TPPos+TS_DE.xml");
//		0.6325
		
		/* Baseline + DistSim + TP + TPPOS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DS+TP+TPPos_DE.xml");
//		0.62625
		
		/* Baseline + DistSim + TP + TPPOS + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+DS+TP+TPPos+TS_DE.xml");
//		0.6275
				
		/** English */
		/* Baseline */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base_EN.xml");
//		0.625
		
		/* WN: WordNet */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_WN_EN.xml");
//		0.5125
		
		/* Baseline + WN */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+WN_EN.xml");
//		0.63 false false false
//		0.6325 true false false (default)
//		0.62375 false true true
//		0.62625 true true true
		
		/* VO: VerbOcean */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_VO_EN.xml");
//		0.5125 true (default)
//		0.5075 false
		
		/* Baseline + VO */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+VO_EN.xml");
//		0.6225 true (default)
//		0.6225 false

		/* Baseline + WN + VO */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+WN+VO_EN.xml");
//		0.62375
		
		/* TP: dependency triples without POS */
		
		/* TPPos: dependency triples with POS */
		
		/* Baseline + TP */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TP_EN.xml");
//		0.63125
		
		/* Baseline + TPPos */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TPPos_EN.xml");
//		0.63
		
		/* TS: tree skeleton scoring */
		
		/* Baseline + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TS_EN.xml");
//		0.63625
		
		/* Baseline + TP + TS */
		
		/* Baseline + TPPos + TS */
		
		/* Baseline + TP + TPPos + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+TP+TPPos+TS_EN.xml");
//		0.64375
		
		/* Baseline + WN + VO + TP + TPPos + TS */
//		File configFile = new File("./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+WN+VO+TP+TPPos+TS_EN.xml");
//		0.645

		Assume.assumeTrue(configFile.exists());
		CommonConfig config = null;
		try {
			// read in the configuration from the file
			config = new ImplCommonConfig(configFile);
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(config);
				
		// Gil: testLAP_DE() is a very very long test. (More than build process itself) 
		//German RTE tests
//		testLAP_DE();
//		testParser_DE();
//		testTraining(config);
//		testTesting_SingleTH(config); 
//		testTesting_MultiTH(config); 
//		testTesting_MultiTH_AND_Output(config);
		
		
		// Rui: testLAP_EN(), testTraining_EN(), and testTesting_MultiTH_EN() also take long time
		/* English RTE tests
		testLAP_EN();
		testParser_EN("poly");
		testTraining(config);
		testTesting_SingleTH(config);
		testTesting_MultiTH(config);
		testTesting_MultiTH_AND_Output(config);
		 */
	}
	
	public void testLAP_DE() {
		File inputFile = null;
		File outputDir = null;
		
		// generate XMI files for the training data
		inputFile = new File("./src/main/resources/data-set/German_dev.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/DE/dev/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());

		LAPAccess lap = null;

		try {
			lap = new TreeTaggerDE();
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
		
		// generate XMI files for the testing data
		inputFile = new File("./src/main/resources/data-set/German_test.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/DE/test/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());
		
		try {
			lap = new TreeTaggerDE();
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testLAP_EN() {
		File inputFile = null;
		File outputDir = null;
		
		// generate XMI files for the training data
		inputFile = new File("./src/main/resources/data-set/English_dev.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/EN/dev/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());

		LAPAccess lap = null;

		try {
			lap = new TreeTaggerEN();
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
		
		// generate XMI files for the testing data
		inputFile = new File("./src/main/resources/data-set/English_test.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/EN/test/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());
		
		try {
			lap = new TreeTaggerEN();
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testParser_DE() {
		File inputFile = null;
		File outputDir = null;
		
		// generate XMI files for the training data
		inputFile = new File("./src/main/resources/data-set/German_dev.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/DE/dev/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());

		LAPAccess lap = null;

		try {
			lap = new MaltParserDE();
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
		
		// generate XMI files for the testing data
		inputFile = new File("./src/main/resources/data-set/German_test.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/DE/test/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());
		
		try {
			lap = new MaltParserDE();
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testParser_EN(String aVariant) {
		File inputFile = null;
		File outputDir = null;
		
		// generate XMI files for the training data
		inputFile = new File("./src/main/resources/data-set/English_dev.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/EN/dev/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());

		LAPAccess lap = null;

		try {
			// previously // lap = new MaltParserEN(aVariant);
			// now either this; if model variants are needed ... 
			//HashMap<String, String> descArgs = new HashMap<String,String>(); 
			//descArgs.put("PARSER_MODEL_VARIANT", aVariant); 
			lap = new MaltParserEN(aVariant); 
			// or you can say, // lap = new MaltParserEN(); // this will load default model. 
			
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
		
		// generate XMI files for the testing data
		inputFile = new File("./src/main/resources/data-set/English_test.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/EN/test/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());
		
		try {
			lap = new MaltParserEN(); // Default model 
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testTraining(CommonConfig config) {	
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();
		try {
			meceda.startTraining(config);
			logger.info("training done.");
			
			meceda.shutdown();
			logger.info("EDA shuts down.");
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
	}
	
	public void testTesting_SingleTH(CommonConfig config) {
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();		
		LAPAccess lap = null;
		try {
			meceda.initialize(config);
			
			JCas test1Cas;
			JCas test2Cas;
			logger.info("build CASes for input sentence pairs:");
			
			if (meceda.getLanguage().equalsIgnoreCase("DE")) {
				lap = new TreeTaggerDE();
				
				test1Cas = lap.generateSingleTHPairCAS("Es wird die Registrierung der Software verlangt, diese ist jedoch nicht möglich. Es wird \"Fehlercode -27\" angezeigt.", "Fehlercode -27 erscheint beim Registrieren");
				test2Cas = lap.generateSingleTHPairCAS("Als ich heute noch einmal etwas weiter machen wollte, stellte ich fest, daß jetzt immer wenn ich den Startzeiger an eine Stelle setze und von dort aus die Preview ansehen möchte, das Anzeigen ganz von Beginn an startet.", "Startzeiger zeigt immer von Anfang an");
//				test1Cas = lap.generateSingleTHPairCAS("Wenn ich auf Preview drücke, beginnt die Anzeige immer von vorn bei Datensatz 1, statt an derStelle wo ich zuletzt bearbeitet habe.", "Startzeiger zeigt immer von Anfang an");
//				test2Cas = lap.generateSingleTHPairCAS("Beim Öffnen des Programmes erscheint die Fehlermeldung\" das smartcard device...\"", "Leider erscheint die Fehlermeldung -9 beim ausführen.");
//				test1Cas = lap.generateSingleTHPairCAS("Leider erscheint die Fehlermeldung -9 beim ausführen.", "Beim Öffnen des Programmes erscheint die Fehlermeldung\" das smartcard device...\"");
//				test2Cas = lap.generateSingleTHPairCAS("Beim Öffnen des Programmes erscheint die Fehlermeldung\" das smartcard device...\"", "Leider erscheint die Fehlermeldung -9 beim ausführen.");
//				test1Cas = lap.generateSingleTHPairCAS("Fehlercode 27 erscheint beim Registrieren", "ein Fehlercode 27 aufgetreten ist");
//				test1Cas = lap.generateSingleTHPairCAS("Fehlercode 27 erscheint beim Registrieren", "meldet das Programm Fehlercode 27");
//				test2Cas = lap.generateSingleTHPairCAS("Fehlercode 27 erscheint beim Registrieren", "bekomme ich den Fehlercode 9");
			} else {
				lap = new TreeTaggerEN();
				
				// ENTAILMENT
//				test1Cas = lap.generateSingleTHPairCAS("The person is hired as a postdoc.","The person is hired as a postdoc.");
				// NONENTAILMENT
//				test2Cas = lap.generateSingleTHPairCAS("The train was uncomfortable", "The train was comfortable");
//				test1Cas = lap.generateSingleTHPairCAS("I visit Jerusalem","I visit Israel");
//				test2Cas = lap.generateSingleTHPairCAS("I visit Jerusalem", "I visit Iran");
				test1Cas = lap.generateSingleTHPairCAS("I saw a car","I saw an automobile");
				test2Cas = lap.generateSingleTHPairCAS("I saw a car", "I saw a cat");			
			}

			logger.info("Answers are:");
			ClassificationTEDecision decision1 = meceda.process(test1Cas);
			logger.info(decision1.getDecision().toString());
			logger.info(String.valueOf(decision1.getConfidence()));
			ClassificationTEDecision decision2 = meceda.process(test2Cas);
			logger.info(decision2.getDecision().toString());
			logger.info(String.valueOf(decision2.getConfidence()));
			
			meceda.shutdown();
			logger.info("EDA shuts down.");
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testTesting_MultiTH(CommonConfig config) {
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();		
		try {
			meceda.initialize(config);
			// check the test data directory
			meceda.initializeData(config, false);
			
			int correct = 0;
			int sum = 0;
			logger.info("build CASes for input sentence pairs:");
			for (File file : (new File(meceda.getTestDIR())).listFiles()) {
				// ignore all the non-xmi files
				if (!file.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(file, null);
				ClassificationTEDecision decision = meceda.process(cas);
				logger.info(decision.getPairID());
				logger.info(meceda.getGoldLabel(cas));
				logger.info(decision.getDecision().toString());
				logger.info(String.valueOf(decision.getConfidence()));
				if (meceda.getGoldLabel(cas).equalsIgnoreCase(decision.getDecision().toString())) {
					correct ++;
				}
				sum ++;
			}
			logger.info("The correctly predicted pairs are " + correct + " / " + sum);
			
			meceda.shutdown();
			logger.info("EDA shuts down.");
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

	public void testTesting_MultiTH_AND_Output(CommonConfig config) {
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();

		BufferedWriter output = null;
		
		try {
			meceda.initialize(config);
			// check the test data directory
			meceda.initializeData(config, false);
			
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getConfigurationFileName().replace("configuration-file", "results") + "_Result.txt"), "UTF-8"));
			logger.info("build CASes for input sentence pairs:");
			int correct = 0;
			int sum = 0;			
			for (File file : (new File(meceda.getTestDIR())).listFiles()) {
				// ignore all the non-xmi files
				if (!file.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(file, null);
				ClassificationTEDecision decision = meceda.process(cas);
				output.write(decision.getPairID());
				output.write("\t");
				output.write(meceda.getGoldLabel(cas).toUpperCase());
				output.write("\t");
				output.write(decision.getDecision().toString().toUpperCase());
				output.write("\t");
				output.write(String.valueOf(decision.getConfidence()));
				output.newLine();
				logger.info("Pair " + decision.getPairID() + " is done.");
				if (meceda.getGoldLabel(cas).equalsIgnoreCase(decision.getDecision().toString())) {
					correct ++;
				}
				sum ++;
			}
			output.close();
			logger.info("The correctly predicted pairs are " + correct + " / " + sum);
			meceda.shutdown();
			logger.info("EDA shuts down.");
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
