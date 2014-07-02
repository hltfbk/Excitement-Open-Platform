package eu.excitementproject.eop.core.component.alignment.lexicallink;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.test.BIUFullLAPConfigured;
import eu.excitementproject.eop.lap.biu.test.BiuTestUtils;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Test class to {@link eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAligner}.
 * @author Vered Shwartz
 *
 */
public class LexicalAlignerTest {

	// Private Members
	private LexicalAligner aligner;
	private LAPAccess lap; 
	private String t1 = "The assassin was convicted and sentenced to death penalty";
	private String h1 = "The killer has been accused of murder and doomed to capital punishment";
	private String t2 = "Kennedy was killed in Dallas";
	private String h2 = "Kennedy was wounded and died in Texas";
	
	static Logger logger = Logger.getLogger(LexicalAligner.class.getName());
	
	/**
	 * Initialize the lexical aligner and prepare the tests
	 */
	public LexicalAlignerTest() {
		
		try {
			
			// Create and initialize the aligner
			logger.info("Initialize the Lexical Aligner");
			URL configFileURL = getClass().getResource("/configuration-file/LexicalAligner_EN.xml");
			File configFile = new File(configFileURL.getFile());
			ImplCommonConfig commonConfig = new ImplCommonConfig(configFile);
			aligner = new LexicalAligner(commonConfig);
			
			// Load the tokenizer and lemmatizer
	        try {
	        	lap = new BIUFullLAPConfigured();
	        } catch (LAPException e) {
	        	logger.info("Could not load the tokenizer and lemmatizer. " + 
	        			e.getMessage()); 
	        }			
		} catch (Exception e) {
			 
			logger.info("Failed initializing the LexicalAligner tests: " + 
					e.getMessage());
		}
	}
	
	@BeforeClass
	public static void beforeClass() throws IOException {
		
		// Run test only under BIU environment
		BiuTestUtils.assumeBiuEnvironment();
	}
	
	@Test
	public void test1() {
		
		try {
			
			// Annotate the first pair with tokens and lemmas
	     	logger.info("Tokenize and lemmatize the sentence pair #1");
			JCas pair1 = lap.generateSingleTHPairCAS(t1, h1);
			
			// Call the aligner to align T and H of pair 1
			logger.info("Aligning pair #1");
			aligner.annotate(pair1);
			logger.info("Finished aligning pair #1");
						
			// Print the alignment of pair 1
			JCas hypoView = pair1.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
			logger.info("Pair 1:");
			logger.info("T: " + t1); 
			logger.info("H: " + h1);
			
			boolean assassinKiller = false, deathPenaltyCapitalPunishment = false;
			
			for (Link link : JCasUtil.select(hypoView, Link.class)) {
				
				logger.info(String.format("Text phrase: %s, " +
						"hypothesis phrase: %s, " + 
						"id: %s, confidence: %f, direction: %s", 
						link.getTSideTarget().getCoveredText(),
						link.getHSideTarget().getCoveredText(),
						link.getID(), link.getStrength(),
						link.getDirection().toString()));
				
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
			
			// Make sure the alignments contain the alignment of
			// "assassin" to "killer"
			if (!assassinKiller) {
				fail("There is no alignment link between 'assassin' and 'killer'." +
						" This alignment link appears in: WordNet, BAP, Lin Dependency," +
						" Lin Proximity and Wikipedia. " +
						" Make sure that at least some of these resources were loaded correctly.");
			}
			
			// Make sure the alignments contain the alignment of
			// "death penalty" to "capital punishment"
			if (!deathPenaltyCapitalPunishment) {
				fail("There is no alignment link between 'death penalty' and 'capital punishment'." +
						" This alignment link appears in: WordNet and Wikipedia. " +
						" Make sure that at least one of these resources was loaded correctly.");
			}
			
		} catch (Exception e) {
			logger.info("Could not process first pair. " + e.getMessage());
		}
	}
	
	@Test
	public void test2() {
		
		try {
			
			// Annotate the first pair with tokens and lemmas
	     	logger.info("Tokenize and lemmatize the sentence pair #2");
	     	JCas pair2 = lap.generateSingleTHPairCAS(t2, h2);
			
			// Call the aligner to align T and H of pair 2
			logger.info("Aligning pair #2");
			aligner.annotate(pair2);
			logger.info("Finished aligning pair #2");
						
			// Print the alignment of pair 1
			JCas hypoView = pair2.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
			logger.info("Pair 2:");
			logger.info("T: " + t2); 
			logger.info("H: " + h2);
			
			boolean killedWounded = false, dallasTexas = false;
			
			for (Link link : JCasUtil.select(hypoView, Link.class)) {
				
				logger.info(String.format("Text phrase: %s, " +
									"hypothesis phrase: %s, " + 
									"id: %s, confidence: %f, direction: %s", 
									link.getTSideTarget().getCoveredText(),
									link.getHSideTarget().getCoveredText(),
									link.getID(), link.getStrength(),
									link.getDirection().toString()));
				
				killedWounded = killedWounded || ((link.getTSideTarget().getBegin() == 12) &&
						(link.getTSideTarget().getEnd() == 18) && 
						(link.getHSideTarget().getBegin() == 12) &&
						(link.getHSideTarget().getEnd() == 19));

				dallasTexas = dallasTexas || ((link.getTSideTarget().getBegin() == 22) &&
						(link.getTSideTarget().getEnd() == 28) && 
						(link.getHSideTarget().getBegin() == 32) &&
						(link.getHSideTarget().getEnd() == 37));
			}
			
			// Make sure the alignments contain the alignment of
			// "killed" to "wounded"
			if (!killedWounded) {
				fail("There is no alignment link between 'killed' and 'wounded'." +
						" This alignment link appears in VerbOcean." +
						" Make sure that this resource was loaded correctly.");
			}
			
			// Make sure the alignments contain the alignment of
			// "Dallas" to "Texas"
			if (!dallasTexas) {
				fail("There is no alignment link between 'Dallas' and 'Texas'." +
						" This alignment link appears in: WordNet and GEO. " +
						" Make sure that at least one of these resources was loaded correctly.");
			}
			
		} catch (Exception e) {
			logger.info("Could not process first pair. " + e.getMessage());
		}
	}

	@Override
	protected void finalize() throws Throwable {
		
		// Dispose the aligner
		aligner.cleanUp();
					
		super.finalize();
	}
}
