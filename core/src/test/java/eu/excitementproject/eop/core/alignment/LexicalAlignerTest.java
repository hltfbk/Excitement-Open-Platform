package eu.excitementproject.eop.core.alignment;

import java.io.File;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.uima.BIUFullLAP;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Test class to {@link eu.excitementproject.eop.core.alignment.LexicalAligner}.
 * @author Vered Shwartz
 *
 */
public class LexicalAlignerTest {

	static Logger logger = Logger.getLogger(LexicalAligner.class.getName());
	
	@Test
	public void test() {
		
		try {
			
			// Create and initialize the aligner
			LexicalAligner aligner = new LexicalAligner();
			File configFile = new File("src/test/resources/configuration-file/LexicalAligner_EN.xml");
			ImplCommonConfig commonConfig = new ImplCommonConfig(configFile);
			aligner.init(commonConfig);
						
			// Create a sentence pair example and annotate with tokens
			String t1 = "The assassin was convicted and sentenced to death penalty";
			String h1 = "The killer has been accused of murder and doomed to capital punishment";
			
			String t2 = "Kennedy was killed in Dallas";
			String h2 = "Kennedy was wounded and died in Texas";
			
			logger.info("Tokenize the sentence pairs");
			
			OpenNLPTaggerEN lap = null; 
	        try {
	        	lap = new OpenNLPTaggerEN();
	        }
	        catch (LAPException e) {
	        	System.err.println(e.getMessage()); 
	        }
	        
			JCas pair1 = lap.generateSingleTHPairCAS(t1, h1);
			JCas pair2 = lap.generateSingleTHPairCAS(t2, h2);
			
			boolean assassinKiller = false, deathPenaltyCapitalPunishment = false,
					killedWounded = false, dallasTexas = false;
			
			// Call the aligner to align T and H of pair 1
			aligner.annotate(pair1);
						
			// Print the alignment of pair 1
			JCas hypoView = pair1.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
			logger.info("Pair 1:");
			logger.info("T: " + t1); 
			logger.info("H: " + h1);
			
			for (Link link : JCasUtil.select(hypoView, Link.class)) {
				
				logger.info(String.format("Text phrase: %s, " +
									"hypothesis phrase: %s, " + 
									"resource: %s, confidence: %f", 
									link.getTSideTarget().getCoveredText(),
									link.getHSideTarget().getCoveredText(),
									link.getAlignerID(),
									link.getStrength()));
				
				assassinKiller = assassinKiller || ((link.getTSideTarget().getBegin() == 1) &&
													(link.getTSideTarget().getEnd() == 1) && 
													(link.getHSideTarget().getBegin() == 1) &&
													(link.getHSideTarget().getEnd() == 1));
				
				deathPenaltyCapitalPunishment = deathPenaltyCapitalPunishment || 
						((link.getTSideTarget().getBegin() == 7) &&
						(link.getTSideTarget().getEnd() == 8) && 
						(link.getHSideTarget().getBegin() == 10) &&
						(link.getHSideTarget().getEnd() == 11));
			}
			
			// Call the aligner to align T and H of pair 2
			aligner.annotate(pair2);
			
			// Print the alignment of pair 2
			hypoView = pair2.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
			logger.info("Pair 2:");
			logger.info("T: " + t2); 
			logger.info("H: " + h2);
			
			for (Link link : JCasUtil.select(hypoView, Link.class)) {
				
				logger.info(String.format("Text phrase: %s, " +
									"hypothesis phrase: %s, " + 
									"resource: %s, confidence: %f", 
									link.getTSideTarget().getCoveredText(),
									link.getHSideTarget().getCoveredText(),
									link.getAlignerID(),
									link.getStrength()));
				
				killedWounded = killedWounded || ((link.getTSideTarget().getBegin() == 2) &&
						(link.getTSideTarget().getEnd() == 2) && 
						(link.getHSideTarget().getBegin() == 2) &&
						(link.getHSideTarget().getEnd() == 2));

				dallasTexas = dallasTexas || ((link.getTSideTarget().getBegin() == 4) &&
						(link.getTSideTarget().getEnd() == 4) && 
						(link.getHSideTarget().getBegin() == 6) &&
						(link.getHSideTarget().getEnd() == 6));
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
