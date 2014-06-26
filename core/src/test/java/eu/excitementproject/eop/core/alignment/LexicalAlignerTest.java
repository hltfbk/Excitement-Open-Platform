package eu.excitementproject.eop.core.alignment;

import java.io.File;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Ignore;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.core.component.alignment.LexicalAligner;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Test class to {@link eu.excitementproject.eop.core.component.alignment.LexicalAligner}.
 * @author Vered Shwartz
 *
 */
public class LexicalAlignerTest {

	static Logger logger = Logger.getLogger(LexicalAligner.class.getName());
	
	@Ignore
	@Test
	public void test() {
		
		try {
			
			// Create and initialize the aligner
			File configFile = new File("src/test/resources/configuration-file/LexicalAligner_EN.xml");
			ImplCommonConfig commonConfig = new ImplCommonConfig(configFile);
			LexicalAligner aligner = new LexicalAligner(commonConfig);
			
			// Create a sentence pair example and annotate with tokens and lemmas
			String t1 = "The assassin was convicted and sentenced to death penalty";
			String h1 = "The killer has been accused of murder and doomed to capital punishment";
			
			String t2 = "Kennedy was killed in Dallas";
			String h2 = "Kennedy was wounded and died in Texas";
			
			logger.info("Tokenize and lemmatize the sentence pairs");
			
			// Tokenize and lemmatize
			TreeTaggerEN lap = null; 
	        try {
	        	lap = new TreeTaggerEN();
	        } catch (LAPException e) {
	        	logger.info("Could not load the tokenizer and lemmatizer. " + e.getMessage()); 
	        }
	        
			JCas pair1 = lap.generateSingleTHPairCAS(t1, h1);
			JCas pair2 = lap.generateSingleTHPairCAS(t2, h2);
			
			boolean assassinKiller = false, deathPenaltyCapitalPunishment = false,
					killedWounded = false, dallasTexas = false;
			
			// Call the aligner to align T and H of pair 1
			logger.info("Started annotating a text and hypothesis pair using lexical aligner");
			aligner.annotate(pair1);
			logger.info("Finished annotating a text and hypothesis pair using lexical aligner");
						
			// Print the alignment of pair 1
			JCas hypoView = pair1.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
			logger.info("Pair 1:");
			logger.info("T: " + t1); 
			logger.info("H: " + h1);
			
			for (Link link : JCasUtil.select(hypoView, Link.class)) {
				
				logger.info(String.format("Text phrase: %s, " +
									"hypothesis phrase: %s, " + 
									"id: %s, confidence: %f", 
									link.getTSideTarget().getCoveredText(),
									link.getHSideTarget().getCoveredText(),
									link.getID(),
									link.getStrength()));
				
				assassinKiller = assassinKiller || ((link.getTSideTarget().getBegin() == 4) &&
													(link.getTSideTarget().getEnd() == 12) && 
													(link.getHSideTarget().getBegin() == 4) &&
													(link.getHSideTarget().getEnd() == 10));
				
				deathPenaltyCapitalPunishment = deathPenaltyCapitalPunishment || 
						((link.getTSideTarget().getBegin() == 44) &&
						(link.getTSideTarget().getEnd() == 57) && 
						(link.getHSideTarget().getBegin() == 52) &&
						(link.getHSideTarget().getEnd() == 70));
			}
			
			// Call the aligner to align T and H of pair 2
			logger.info("Started annotating a text and hypothesis pair using lexical aligner");
			aligner.annotate(pair2);
			logger.info("Finished annotating a text and hypothesis pair using lexical aligner");
						
			// Print the alignment of pair 2
			hypoView = pair2.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
			logger.info("Pair 2:");
			logger.info("T: " + t2); 
			logger.info("H: " + h2);
			
			for (Link link : JCasUtil.select(hypoView, Link.class)) {
				
				logger.info(String.format("Text phrase: %s, " +
									"hypothesis phrase: %s, " + 
									"id: %s, confidence: %f", 
									link.getTSideTarget().getCoveredText(),
									link.getHSideTarget().getCoveredText(),
									link.getID(),
									link.getStrength()));
				
				killedWounded = killedWounded || ((link.getTSideTarget().getBegin() == 12) &&
						(link.getTSideTarget().getEnd() == 18) && 
						(link.getHSideTarget().getBegin() == 12) &&
						(link.getHSideTarget().getEnd() == 19));

				dallasTexas = dallasTexas || ((link.getTSideTarget().getBegin() == 22) &&
						(link.getTSideTarget().getEnd() == 28) && 
						(link.getHSideTarget().getBegin() == 32) &&
						(link.getHSideTarget().getEnd() == 37));
			}
			
			// Make sure the alignments contain some expected alignments
			assert(assassinKiller);
			assert(deathPenaltyCapitalPunishment);
			assert(killedWounded);
			assert(dallasTexas);
			
			// Dispose the aligner
			aligner.cleanUp();
			
		} catch (Exception e) {
			 
			logger.info("Test failed with exception: " + e.getMessage());
		}
	}
}
