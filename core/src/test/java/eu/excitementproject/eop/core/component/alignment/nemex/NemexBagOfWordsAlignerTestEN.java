package eu.excitementproject.eop.core.component.alignment.nemex;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import eu.excitement.type.alignment.Link;
//import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

public class NemexBagOfWordsAlignerTestEN {

	static Logger logger;

	private NemexAligner aligner;

	@Test
	public void test() {
		try {

			logger = Logger.getLogger(NemexAligner.class.getName());

			// prepare JCas

			OpenNLPTaggerEN tokenizer = null;
			tokenizer = new OpenNLPTaggerEN();

			/*
			 * JCas aJCas1 = tokenizer.generateSingleTHPairCAS(
			 * "Gabriel Garcia Marquez was a liberal thinker whose left-wing politics angered many conservative politicians and heads of state. His job as a reporter for the Cuban news agency Prensa Latina, in 1960, and and friendship with Fidel Castro resulted in his being ultimately denied entry to the United States for political reasons."
			 * , "Gabriel Garcia Marquez was a conservative politician.");
			 * 
			 * Logger.getRootLogger().setLevel(Level.INFO); // main log setting:
			 * // set as DEBUG to // see what's going // & debug.
			 * 
			 * logger.info("Starting alignment for test JCas pair 1");
			 * alignAndPrint(aJCas1);
			 * logger.info("Finished alignment of test JCas pair 1");
			 * 
			 * JCas aJCas2 = tokenizer .generateSingleTHPairCAS(
			 * "Judge Drew served as Justice until Kennon returned to claim his seat in 1945."
			 * , "Kennon served as Justice.");
			 * 
			 * logger.info("Starting alignment for test JCas pair 2");
			 * alignAndPrint(aJCas2);
			 * logger.info("Finished alignment of test JCas pair 2");
			 * 
			 * 
			 * JCas aJCas3 = tokenizer .generateSingleTHPairCAS(
			 * "Ms. Minton left Australia in 1961 to pursue her studies in London."
			 * , "Ms. Minton was born in Australia.");
			 * 
			 * logger.info("Starting alignment for test JCas pair 3");
			 * alignAndPrint(aJCas3);
			 * logger.info("Finished alignment of test JCas pair 3");
			 * 
			 * 
			 * JCas aJCas4 = tokenizer .generateSingleTHPairCAS(
			 * "Robinson's garden style can be seen today at Gravetye Manor, West Sussex, England, though it is more manicured than it was in Robinson's time."
			 * , "Gravetye Manor is located in West Sussex.");
			 * 
			 * logger.info("Starting alignment for test JCas pair 4");
			 * alignAndPrint(aJCas4);
			 * logger.info("Finished alignment of test JCas pair 4");
			 */

			JCas aJCas5 = tokenizer
					.generateSingleTHPairCAS(
							"Relations between Argentina and Britain were soured again last May when Britain decided to extend territorial waters to 200 miles around South Georgia and the South Sandwich Islands.",
							"Britain angered Argentina.");

			Logger.getRootLogger().setLevel(Level.INFO); // main log setting:
															// set as DEBUG to
															// see what's going
															// & debug.

			logger.info("Starting alignment for test JCas pair 5");
			alignAndPrint(aJCas5);
			logger.info("Finished alignment of test JCas pair 5");

		} catch (Exception e) {
			logger.info("Could not align the JCas test pair");
		}
	}

	private void alignAndPrint(JCas aJCas)
			throws PairAnnotatorComponentException {
		try {

			logger.info("Initialize the Nemex Aligner");
			
			// test the configuration file
			File configFile = new File(
					"./src/main/resources/configuration-file/NemexWekaClassificationEDA_EN.xml");
			Assume.assumeTrue(configFile.exists());
			CommonConfig config = null;
			try {
				// read in the configuration from the file
				config = new ImplCommonConfig(configFile);
			} catch (ConfigurationException e) {
				logger.warn(e.getMessage());
			}
			Assume.assumeNotNull(config);

			aligner = new NemexBagOfWordsAligner(
					config, false, null);
			logger.info("Initialization finished");

			// align test JCas pair

			aligner.annotate(aJCas);

			// Print the alignment of JCas pair

			JCas hView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			JCas tView = aJCas.getView(LAP_ImplBase.TEXTVIEW);

			for (Link link : JCasUtil.select(hView, Link.class)) {

				logger.info(String.format("Text phrase: %s, "
						+ "hypothesis phrase: %s, "
						+ "id: %s, confidence: %f, direction: %s", link
						.getTSideTarget().getCoveredText(), link
						.getHSideTarget().getCoveredText(), link.getID(), link
						.getStrength(), link.getDirection().toString()));

				// int tStart = link.getTSideTarget().getBegin();
				// int tEnd = link.getTSideTarget().getEnd();
				// int hStart = link.getHSideTarget().getBegin();
				// int hEnd = link.getHSideTarget().getEnd();
				
				if(link.getTSideTarget().getView().getCurrentView().equals(tView)){
						logger.info("Same view");}
				//
				// Collection<Token> hTokens = JCasUtil.selectCovered(hView,
				// Token.class, hStart, hEnd);
				//
				// for(java.util.Iterator<Token> iter = hTokens.iterator();
				// iter.hasNext();) {
				// logger.info("Token covered by link on H: "+iter.next().getCoveredText());
				// }
				/*
				 * JCas targetView =
				 * link.getHSideTarget().getCASImpl().getExistingJCas(); JCas
				 * hView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
				 * if(targetView.equals(hView)) { logger.info("Same as hiew"); }
				 */

			}
		} catch (Exception e) {
			logger.info("Alignment failed");
			e.printStackTrace();

		}
	}
}
