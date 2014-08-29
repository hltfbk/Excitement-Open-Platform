package eu.excitementproject.eop.core.component.alignment.nemex;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

public class NemexAlignerTestEN {

	static Logger logger;

	private NemexAligner aligner;

	

	@Test
	public void test() {
		try {
			
			logger = Logger.getLogger(NemexAligner.class.getName());
			logger.info("Initialize the Nemex Aligner");

			aligner = new NemexAligner(
					"src/test/resources/gazetteer/nemexAligner.txt", "#", true, 3,
					false, "DICE_SIMILARITY_MEASURE", 0.39);
			logger.info("Initialization finished");
			// prepare a JCas
			JCas aJCas = null;
			OpenNLPTaggerEN tokenizer = null;

			tokenizer = new OpenNLPTaggerEN();
			aJCas = tokenizer.generateSingleTHPairCAS("I saw a car.",
					"I saw an automobile");

			Logger.getRootLogger().setLevel(Level.INFO); // main log setting:
															// set as DEBUG to
															// see what's going
															// & debug.
			logger.info("Starting alignment for test JCas pair ");

			// align test JCas pair

			aligner.annotate(aJCas);

			logger.info("Finished alignment of test JCas pair");

			// Print the alignment of JCas pair

			JCas hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);

			boolean saw = false;
			
			for (Link link : JCasUtil.select(hypoView, Link.class)) {

				logger.info(String.format("Text phrase: %s, "
						+ "hypothesis phrase: %s, "
						+ "id: %s, confidence: %f, direction: %s", link
						.getTSideTarget().getCoveredText(), link
						.getHSideTarget().getCoveredText(), link.getID(), link
						.getStrength(), link.getDirection().toString()));
			
				saw = saw || ((link.getTSideTarget().getBegin() == 2) &&
						(link.getTSideTarget().getEnd() == 5) && 
						(link.getHSideTarget().getBegin() == 2) &&
						(link.getHSideTarget().getEnd() == 5));
				
				// Make sure the alignments contain the alignment of
				// saw and saw)
				if (!saw) {
					logger.info("There is no alignment link between 'saw' and 'saw'");
				}
				else
					logger.info("Saw successfully aligned");
			}

		} catch (Exception e) {
			logger.info("Could not align the JCas test pair");
		}
	}
}
