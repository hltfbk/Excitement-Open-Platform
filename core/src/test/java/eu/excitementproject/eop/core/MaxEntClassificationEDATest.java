package eu.excitementproject.eop.core;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

/**
 * The test contains three parts: 1) use LAP to do preprocessing and generate
 * xmi files; 2) train a MaxEnt model; 3) test on the input example(s).
 * 
 * @author Rui
 */
public class MaxEntClassificationEDATest {
	static Logger logger = Logger.getLogger(MaxEntClassificationEDATest.class
			.getName());

	@Test
	public void test() {

		boolean isEN = false;

		boolean isTrain = false;

		File inputFile = null;
		File outputDir = null;
		if (isEN) {
			// inputFile = new File("./src/test/resources/small.xml");
			inputFile = new File("./src/test/resources/English_dev.xml");
			outputDir = new File("./target/EN/");
		} else {
			inputFile = new File("./src/test/resources/German_dev.xml");
			outputDir = new File("./target/DE/");
		}
		assertTrue(inputFile.exists());

		boolean isPre = false;

		if (!outputDir.exists() && isTrain) {
			outputDir.mkdirs();
			isPre = true;
		}

		LAPAccess lap = null;

		try {
			// LAP
			if (isEN) {
				lap = new TreeTaggerEN();
			} else {
				lap = new TreeTaggerDE();
			}
			if (isPre) {
				lap.processRawInputFormat(inputFile, outputDir);
			}
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}

		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();
		if (isEN) {
			meceda.setLanguage("EN");
		} else {
			meceda.setLanguage("DE");
		}

		CommonConfig config = null;

		try {
			if (isTrain) {
				meceda.setTrain(true);
				meceda.initialize(config);
			} else {
				meceda.setTrain(false);
				meceda.initialize(config);
			}
			File modelFile = new File(meceda.getModelFile());
			if (!modelFile.exists()) {
				isTrain = true;
				meceda.setTrain(true);
			}

			if (isTrain) {
				// training
				meceda.startTraining(config);
				assertTrue(modelFile.exists());
				logger.info("training done");
			} else {
				// testing
				logger.info("build CASes for input sentence pairs:");
				// JCas aCas =
				// lap.generateSingleTHPairCAS("The train was uncomfortable",
				// "the train was comfortable");
				// JCas bCas =
				// lap.generateSingleTHPairCAS("The person is hired as a postdoc.","The person is hired as a postdoc.");
				JCas aCas = lap.generateSingleTHPairCAS(
						"Ich bin ein Student .", "Er ist kein Person .");

				logger.info("Answers are:");
				ClassificationTEDecision decision1 = meceda.process(aCas);
				System.out.println(decision1.getDecision().toString());
				// ClassificationTEDecision decision2 = meceda.process(bCas);
				// System.out.println(decision2.getDecision().toString());
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}

	}
}
