 
package eu.excitementproject.eop.core;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.*;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
//import eu.excitementproject.eop.lap.textpro.*;
import eu.excitementproject.eop.lap.dkpro.*;

/**
* This class tests OpenNLP tagger for English, German and Italian on a small portion
* of the RTE-3 data set.
*/
public class LapTest {
		
	    @Ignore
		@Test
		public void test() {
			testLAP_IT();
			testLAP_EN();
			testLAP_DE();
		}
		
		
		public void testLAP_IT() {
			
			File inputFile = null;
			File outputDir = null;
			
			// generate XMI files for the training data
			inputFile = new File("./src/test/resources/data-set/ITA/Italian_dev_small.xml");
			//inputFile = new File("./src/test/resources/data-set/Italian_dev.xml");
			assertTrue(inputFile.exists());
			outputDir = new File("./src/test/resources/data-set/ITA/dev/");
			//outputDir = new File("/tmp/ITA/dev/");
		
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			assertTrue(outputDir.exists());

			LAPAccess lap = null;

			try {
				lap = new OpenNLPTaggerIT();
				lap.processRawInputFormat(inputFile, outputDir);
			} catch (LAPException e) {
				e.printStackTrace();
			}
			
			// generate XMI files for the testing data
			inputFile = new File("./src/test/resources/data-set/ITA/Italian_test_small.xml");
			//inputFile = new File("./src/test/resources/data-set/Italian_test.xml");
			assertTrue(inputFile.exists());
			outputDir = new File("./src/test/resources/data-set/ITA/test/");
			//outputDir = new File("/tmp/ITA/test/");
			
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			assertTrue(outputDir.exists());
			
			try {
				lap = new OpenNLPTaggerIT();
				lap.processRawInputFormat(inputFile, outputDir);
			} catch (LAPException e) {
				e.printStackTrace();
			}
		}
		
		public void testLAP_EN() {
			
			File inputFile = null;
			File outputDir = null;
			
			// generate XMI files for the training data
			inputFile = new File("./src/test/resources/data-set/ENG/English_dev_small.xml");
			//inputFile = new File("./src/test/resources/data-set/English_dev.xml");
			assertTrue(inputFile.exists());
			outputDir = new File("./src/test/resources/data-set/ENG/dev/");
			//outputDir = new File("/tmp/ENG/dev/");
		
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			assertTrue(outputDir.exists());

			LAPAccess lap = null;

			try {
				lap = new OpenNLPTaggerEN();
				lap.processRawInputFormat(inputFile, outputDir);
			} catch (LAPException e) {
				e.printStackTrace();
			}
			
			// generate XMI files for the testing data
			inputFile = new File("./src/test/resources/data-set/ENG/English_test_small.xml");
			//inputFile = new File("./src/test/resources/data-set/English_test.xml");
			assertTrue(inputFile.exists());
			outputDir = new File("./src/test/resources/data-set/ENG/test/");
			//outputDir = new File("/tmp/ENG/test/");
			
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			assertTrue(outputDir.exists());
			
			try {
				lap = new OpenNLPTaggerEN();
				lap.processRawInputFormat(inputFile, outputDir);
			} catch (LAPException e) {
				e.printStackTrace();
			}
		}
		
		public void testLAP_DE() {
			
			File inputFile = null;
			File outputDir = null;
			
			// generate XMI files for the training data
			inputFile = new File("./src/test/resources/data-set/GER/German_dev_small.xml");
			//inputFile = new File("./src/test/resources/data-set/German_dev.xml");
			assertTrue(inputFile.exists());
			outputDir = new File("./src/test/resources/data-set/GER/dev/");
			//outputDir = new File("/tmp/GER/dev/");
		
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			assertTrue(outputDir.exists());

			LAPAccess lap = null;

			try {
				lap = new OpenNLPTaggerDE();
				lap.processRawInputFormat(inputFile, outputDir);
			} catch (LAPException e) {
				e.printStackTrace();
			}
			
			// generate XMI files for the testing data
			inputFile = new File("./src/test/resources/data-set/GER/German_test_small.xml");
			//inputFile = new File("./src/test/resources/data-set/German_test.xml");
			assertTrue(inputFile.exists());
			outputDir = new File("./src/test/resources/data-set/GER/test/");
			//outputDir = new File("/tmp/GER/test/");
			
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			assertTrue(outputDir.exists());
			
			try {
				lap = new OpenNLPTaggerDE();
				lap.processRawInputFormat(inputFile, outputDir);
			} catch (LAPException e) {
				e.printStackTrace();
			}
		}
		
}

