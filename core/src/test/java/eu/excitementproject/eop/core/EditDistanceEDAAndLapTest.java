 
	package eu.excitementproject.eop.core;

	import static org.junit.Assert.assertTrue;

	import java.io.File;
	import java.util.logging.Logger;

	import org.apache.uima.jcas.JCas;
	import org.junit.Test;

	import eu.excitementproject.eop.common.configuration.CommonConfig;
	import eu.excitementproject.eop.lap.LAPAccess;
	import eu.excitementproject.eop.lap.LAPException;
	import eu.excitementproject.eop.lap.PlatformCASProber;
	import eu.excitementproject.eop.lap.textpro.*;
	import eu.excitementproject.eop.common.IEditDistanceTEDecision;

	/**
	 * The test contains three parts: 1) use LAP to do preprocessing and generate
	 * xmi files; 2) train a MaxEnt model; 3) test on the input example(s).
	 * 
	 * @author Rui
	 */
	public class EditDistanceEDAAndLapTest {
		
		static Logger logger = Logger.getLogger(EditDistanceEDAAndLapTest.class
				.getName());
		
		@Test
		public void test() {
			// testLAP_DE() is a very very long test. (More than build process itself) 
			// Commented for that reason. 
			// Uncomment the following tests to do the Full Test 
			// on MaxEntClassificationEDA --Gil 
					
			
			testLAP_IT(); 
			//testTraining_IT(); 
			//testTesting_SingleTH_IT(); 
			
			// Rui: if you want to test MaxEntClassificationEDA with different lexical resources, please check MaxEntClassificationEDA.initialize() for the moment
			// Rui: Make sure the trained model is consistent with the testing configuration!
		}
		
		//@Test 
		public void testLAP_IT() {
			
			File inputFile = null;
			File outputDir = null;
			
			// generate XMI files for the training data
			inputFile = new File("./src/test/resources/Italian_dev.xml");
			assertTrue(inputFile.exists());
			outputDir = new File("./target/IT/dev/");
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			assertTrue(outputDir.exists());

			LAPAccess lap = null;

			try {
				lap = new LAP_TextPro();
				lap.processRawInputFormat(inputFile, outputDir);
			} catch (LAPException e) {
				logger.info(e.getMessage());
			}
			
			// generate XMI files for the testing data
			inputFile = new File("./src/test/resources/Italian_test.xml");
			assertTrue(inputFile.exists());
			outputDir = new File("./target/IT/test/");
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			assertTrue(outputDir.exists());
			
			try {
				lap = new LAP_TextPro();
				lap.processRawInputFormat(inputFile, outputDir);
			} catch (LAPException e) {
				logger.info(e.getMessage());
			}
		}
		
		//@Test
		//
		public void testTraining_IT() {
			File trainingDir = null;
			trainingDir = new File("./target/IT/dev/");
			assertTrue(trainingDir.exists());
			
			@SuppressWarnings("rawtypes")
			EditDistanceEDA ed = new EditDistanceEDA();
			ed.setLanguage("IT");

			CommonConfig config = null;

			try {
				ed.setTrain(true);
				ed.initialize(config);
				File modelFile = new File(ed.getModelFile());
				//assertTrue(!modelFile.exists());

				ed.startTraining(config);
				assertTrue(modelFile.exists());
				logger.info("training done");
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		}
		
		//@Test
		public void testTesting_SingleTH_IT() {
			
			@SuppressWarnings("rawtypes")
			EditDistanceEDA  ed = new EditDistanceEDA ();
			ed.setLanguage("IT");
			
			CommonConfig config = null;
			
			//LAPAccess lap = null;
			
			try {
				ed.setTrain(false);
				ed.initialize(config);
				File modelFile = new File(ed.getModelFile());
				assertTrue(modelFile.exists());
				
				logger.info("build CASes for input sentence pairs:");
				//lap = new LAP_TextPro();
				//JCas test1Cas = lap.generateSingleTHPairCAS("Es wird die Registrierung der Software verlangt, diese ist jedoch nicht möglich. Es wird \"Fehlercode -27\" angezeigt.", "Fehlercode -27 erscheint beim Registrieren");
				//JCas test2Cas = lap.generateSingleTHPairCAS("Als ich heute noch einmal etwas weiter machen wollte, stellte ich fest, daß jetzt immer wenn ich den Startzeiger an eine Stelle setze und von dort aus die Preview ansehen möchte, das Anzeigen ganz von Beginn an startet.", "Startzeiger zeigt immer von Anfang an");

				File testDir = null;
				testDir = new File("./target/IT/test/");
				for (File xmi : (testDir.listFiles())) {
					if (!xmi.getName().endsWith(".xmi")) {
						continue;
					}
					JCas cas = PlatformCASProber.probeXmi(xmi, System.out);
					IEditDistanceTEDecision teDecision1 = ed.process(cas);
					// System.err.println(teDecision1.getDecision().toString()) ;
					System.err.println(teDecision1.getDecision().toString());
					
				}
				
				//logger.info("Answers are:");
				
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		}
		

	}

