package eu.excitementproject.eop.core.component.alignment.lexicallink;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;
import org.apache.uima.fit.util.JCasUtil;

import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
//import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
//import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.RelationType;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.VerbOceanLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.VerbOceanRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

public class LexicalAlignerFromLexicalResourceTest {

	// Private Members
	private AlignmentComponent alignerWordNetEN; 
	private AlignmentComponent alignerVerbOceanEN; 

	private LAPAccess lap; 
	private String t1 = "The assassin was convicted and sentenced to death penalty";
	private String h1 = "The killer has been accused of murder and doomed to capital punishment";
	private String t2 = "Kennedy was killed in Dallas";
	private String h2 = "Kennedy was wounded and died in Texas";
	
	static Logger logger = Logger.getLogger(LexicalAlignerFromLexicalResourceTest.class);
	
	// some configurations to be used for lexical resources 
	// WordNet 
	private static LexicalResource<WordnetRuleInfo> wordNet = null; 
	private static final String wnPath = "../core/src/main/resources/ontologies/EnglishWordNet-dict"; 
                                            //	<property name = "entailing-relations">SYNONYM,DERIVATIONALLY_RELATED,HYPERNYM,INSTANCE_HYPERNYM,MEMBER_HOLONYM,PART_HOLONYM,ENTAILMENT,SUBSTANCE_MERONYM</property>
	private static final WordNetRelation[] entailingRelations = new WordNetRelation[] { WordNetRelation.SYNONYM, WordNetRelation.DERIVATIONALLY_RELATED, WordNetRelation.HYPERNYM, WordNetRelation.INSTANCE_HYPERNYM, WordNetRelation.MEMBER_HOLONYM, WordNetRelation.PART_HOLONYM, WordNetRelation.ENTAILMENT, WordNetRelation.SUBSTANCE_MERONYM }; 
	private static final Set<WordNetRelation> entailingRelationSet = new HashSet<WordNetRelation>(Arrays.asList(entailingRelations)); 

	// VerbOcean
	private static final String verbOceanDefaultPath = "../core/src/main/resources/VerbOcean/verbocean.unrefined.2004-05-20.txt"; 
	private static final HashSet<RelationType> verbOceanDefaultRelations = new HashSet<RelationType>(Arrays.asList(RelationType.STRONGER_THAN)); 

	
	/**
	 * Initialize the lexical aligner and prepare the tests
	 */
	public LexicalAlignerFromLexicalResourceTest() {
		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.DEBUG); 

		// Create and initialize the two aligner
		logger.info("Initialize the Lexical Aligner and required LAPs");
		
		// wordNet and aligner based on it ... 
		try {
			wordNet = new WordnetLexicalResource(new File(wnPath), true, true, entailingRelationSet, 2); 
			alignerWordNetEN = new LexicalAlignerFromLexicalResource(wordNet, true, 5, null, null, null); 
			
			//alignerWordNetEN = new WordNetENLinker(null); 
			
		} 
		catch (LexicalResourceException e)
		{
			fail("failed to initialize WordNet LexicalResource: " + e.getMessage()); 
		} 
		catch (AlignmentComponentException ae)
		{
			fail("failed to initialize lexical aligner: " + ae.getMessage()); 
		}
		
		// initialize VerbOcean & aligner for that 
		
		try {
			LexicalResource<VerbOceanRuleInfo> vOcean = new VerbOceanLexicalResource(1.0, new File(verbOceanDefaultPath), verbOceanDefaultRelations); 
			alignerVerbOceanEN = new LexicalAlignerFromLexicalResource(vOcean, false, 1, null, null, null); 
		}
		catch (LexicalResourceException e)
		{
			fail("failed to initialize VerbOcean LexicalResource: " + e.getMessage()); 
		} 
		catch (AlignmentComponentException ae)
		{
			fail("failed to initialize lexical aligner: " + ae.getMessage()); 
		}		
		
		// initialize treetagger lap 
		try {
			lap = new TreeTaggerEN(); 
			
			// one of the LAPAccess interface: that generates single TH CAS. 
			lap.generateSingleTHPairCAS("Bush used his weekly radio address to try to build support for his plan to allow workers to divert part of their Social Security payroll taxes into private investment accounts", "Mr. Bush is proposing that workers be allowed to divert their payroll taxes into private accounts."); 
		}
		catch(LAPException e)
		{
			// check if this is due to missing TreeTagger binary and model. 
			// In such a case, we just skip this test. 
			// (see /lap/src/scripts/treetagger/README.txt to how to install TreeTagger) 
			if (ExceptionUtils.getRootCause(e) instanceof java.io.IOException) 
			{
				logger.info("Skipping the test: TreeTagger binary and/or models missing. \n To run this testcase, TreeTagger installation is needed. (see /lap/src/scripts/treetagger/README.txt)");  
				Assume.assumeTrue(false); // we won't test this test case any longer. 
			}
			// if this is some other exception, the test will fail  
			fail(e.getMessage()); 
		}
	}
	
	@Test 
	public void test1() {
		
		try {
			
			// Annotate the first pair with tokens and lemmas
	     	logger.info("Tokenize and lemmatize the sentence pair #1");
			JCas pair1 = lap.generateSingleTHPairCAS(t1, h1);
			
			// Call the aligner to align T and H of pair 1
			logger.info("Aligning pair #1");
			alignerWordNetEN.annotate(pair1);
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
			e.printStackTrace();
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
			alignerWordNetEN.annotate(pair2); 
			alignerVerbOceanEN.annotate(pair2);
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

}
