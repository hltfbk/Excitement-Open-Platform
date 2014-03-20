package eu.excitementproject.eop.core.component.scoring;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSim;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.RelationType;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

public class BagOfWordsScoringTest {

	static Logger logger = Logger.getLogger(BagOfWordsScoringTest.class
			.getName());

	@Test
	public void test() throws LexicalResourceException {
		testDE();
//		 testEN();
	}

	public void testDE() throws LexicalResourceException {
		// to test whether GermanDistSim is there
		GermanDistSim gds = null;
		try {
			// gds = new
			// GermanDistSim("./src/main/resources/dewakdistributional-data/");
			gds = new GermanDistSim(); // Gil - GermanDistSim no longer supports
										// choice of resource loading. only 10k
										// now available.
		} catch (GermanDistSimNotInstalledException e) {
			logger.warning("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly to the component.");
			// throw e;
		} catch (BaseException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(gds); // if gds is null, the following tests will
									// not be run.

		// to test whether GermaNet is there
		GermaNetWrapper gnw = null;
		try {
			gnw = new GermaNetWrapper(
					"./src/main/resources/ontologies/germanet-7.0/GN_V70/GN_V70_XML/");
		} catch (GermaNetNotInstalledException e) {
			logger.warning("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
			// throw e;
		} catch (BaseException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(gnw); // if gnw is null, the following tests will
									// not be run.

		// test the configuration file
		File configFile = new File(
				"./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+GNPos+DS+DBPos_DE.xml");
		Assume.assumeTrue(configFile.exists());
		CommonConfig config = null;
		try {
			// read in the configuration from the file
			config = new ImplCommonConfig(configFile);
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(config);

		BagOfWordsScoring bows = new BagOfWordsScoring();
		BagOfLemmasScoring bols = new BagOfLemmasScoring();
		BagOfLexesScoringDE bolexs = null;
		BagOfLexesPosScoringDE bolexposs = null;
		try {
//			bolexs = new BagOfLexesScoringDE(config);
			bolexs = new BagOfLexesScoringDE(true, false, "", true, new String[]{"Causes","Entails","Has_Hypernym","Has_Synonym"}, "./src/main/resources/ontologies/germanet-7.0/GN_V70/GN_V70_XML/", false);
			
//			bolexposs = new BagOfLexesPosScoringDE(config);
			bolexposs = new BagOfLexesPosScoringDE(true, false, "", true, new String[]{"Causes","Entails","Has_Hypernym","Has_Synonym"}, "./src/main/resources/ontologies/germanet-7.0/GN_V70/GN_V70_XML/", true, true, 10);

		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(bolexs);
		Assume.assumeNotNull(bolexposs);

		JCas aCas = null;
		LAPAccess lap = null;

		try {
			lap = new TreeTaggerDE();
			aCas = lap.generateSingleTHPairCAS("Ich bin ein Student .",
					"Er ist kein Person .");
			Vector<Double> scoresVector1 = bows.calculateScores(aCas);
			Vector<Double> scoresVector2 = bols.calculateScores(aCas);
			Vector<Double> scoresVector3 = bolexs.calculateScores(aCas);
			Vector<Double> scoresVector4 = bolexposs.calculateScores(aCas);
			logger.info("The bag of words scores:");
			for (Double score : scoresVector1) {
				logger.info(String.valueOf(score));
			}
			logger.info("The bag of lemmas scores:");
			for (Double score : scoresVector2) {
				logger.info(String.valueOf(score));
			}
			logger.info("The bag of lexical scores:");
			for (Double score : scoresVector3) {
				logger.info(String.valueOf(score));
			}
			logger.info("The bag of lexical+pos scores:");
			for (Double score : scoresVector4) {
				logger.info(String.valueOf(score));
			}
		} catch (LAPException e) {
			// check if this is due to missing TreeTagger binary and model.
			// In such a case, we just skip this test.
			// (see /lap/src/scripts/treetagger/README.txt to how to install
			// TreeTagger)
			if (ExceptionUtils.getRootCause(e) instanceof java.io.IOException) {
				logger.info("Skipping the test: TreeTagger binary and/or models missing. \n To run this testcase, TreeTagger installation is needed. (see /lap/src/scripts/treetagger/README.txt)");
				Assume.assumeTrue(false); // we won't test this test case any
											// longer.
			}

			// if this is some other exception, the test will fail

			fail(e.getMessage());
		} catch (ScoringComponentException e) {
			logger.info(e.getMessage());
		}

		try {
			bolexs.close();
			bolexposs.close();
			logger.info("Components and lexical resources are closed.");
		} catch (ScoringComponentException e) {
			logger.info(e.getMessage());
		}
	}

	public void testEN() throws LexicalResourceException {
		File wnPath = new File("./src/main/resources/ontologies/EnglishWordNet-dict/");
		if (!wnPath.exists()) {
			logger.warning("WARNING: English WordNet is not found. Please install it properly, and pass its location correctly to the component.");
		}
		Assume.assumeTrue(wnPath.exists());

		File voPath = new File(
				"./src/main/resources/VerbOcean/verbocean.unrefined.2004-05-20.txt");
		if (!voPath.exists()) {
			logger.warning("WARNING: VerbOcean is not found. Please install it properly, and pass its location correctly to the component.");
		}
		Assume.assumeTrue(voPath.exists());

		BagOfWordsScoring bows = new BagOfWordsScoring();
		BagOfLemmasScoring bols = new BagOfLemmasScoring();

		Set<WordNetRelation> wnRelSet = new HashSet<WordNetRelation>();
		wnRelSet.add(WordNetRelation.HYPERNYM);
		wnRelSet.add(WordNetRelation.SYNONYM);

		Set<RelationType> voRelSet = new HashSet<RelationType>();
		voRelSet.add(RelationType.STRONGER_THAN);
		voRelSet.add(RelationType.CAN_RESULT_IN);
		voRelSet.add(RelationType.SIMILAR);

		// test the configuration file
		File configFile = new File(
				"./src/main/resources/configuration-file/MaxEntClassificationEDA_Base+WN+VO_EN.xml");
		Assume.assumeTrue(configFile.exists());
		CommonConfig config = null;
		try {
			// read in the configuration from the file
			config = new ImplCommonConfig(configFile);
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(config);

		BagOfLexesScoringEN bolexs = null;
		try {
//			bolexs = new BagOfLexesScoringEN(config);
			bolexs = new BagOfLexesScoringEN(true, new String[]{"HYPERNYM", "SYNONYM", "PART_HOLONYM"}, true, false, false, "./src/main/resources/ontologies/EnglishWordNet-dict/", true, new String[]{"StrongerThan", "CanResultIn", "Similar"}, true, "./src/main/resources/VerbOcean/verbocean.unrefined.2004-05-20.txt");
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		Assume.assumeNotNull(bolexs);

		JCas aCas = null;
		LAPAccess lap = null;

		try {
			lap = new TreeTaggerEN();
			// Entailment
			aCas = lap.generateSingleTHPairCAS(
					"The person is hired as a postdoc.",
					"The person must have a PhD.");
			Vector<Double> scoresVector1 = bows.calculateScores(aCas);
			Vector<Double> scoresVector2 = bols.calculateScores(aCas);
			Vector<Double> scoresVector3 = bolexs.calculateScores(aCas);
			logger.info("The bag of words scores:");
			for (Double score : scoresVector1) {
				logger.info(String.valueOf(score));
			}
			logger.info("The bag of lemmas scores:");
			for (Double score : scoresVector2) {
				logger.info(String.valueOf(score));
			}
			logger.info("The bag of lexical scores:");
			for (Double score : scoresVector3) {
				logger.info(String.valueOf(score));
			}
		} catch (LAPException e) {
			// check if this is due to missing TreeTagger binary and model.
			// In such a case, we just skip this test.
			// (see /lap/src/scripts/treetagger/README.txt to how to install
			// TreeTagger)
			if (ExceptionUtils.getRootCause(e) instanceof java.io.IOException) {
				logger.info("Skipping the test: TreeTagger binary and/or models missing. \n To run this testcase, TreeTagger installation is needed. (see /lap/src/scripts/treetagger/README.txt)");
				Assume.assumeTrue(false); // we won't test this test case any
											// longer.
			}

			// if this is some other exception, the test will fail

			fail(e.getMessage());
		} catch (ScoringComponentException e) {
			logger.info(e.getMessage());
		}

		try {
			bolexs.close();
			logger.info("Components and lexical resources are closed.");
		} catch (ScoringComponentException e) {
			logger.info(e.getMessage());
		}
	}
}
