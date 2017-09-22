package eu.excitementproject.eop.alignmentedas.p1eda.insepctor;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import static eu.excitementproject.eop.alignmentedas.p1eda.inspector.CompareTwoEDAs.*; 
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.Target;
//import static eu.excitementproject.eop.alignmentedas.p1eda.inspector.InspectJCasAndAlignment.*; 
import eu.excitementproject.eop.alignmentedas.P1EdaRTERunner;
import eu.excitementproject.eop.alignmentedas.p1eda.P1EDATemplate;
import eu.excitementproject.eop.alignmentedas.p1eda.inspector.InspectUtilsJCasAndLinks;
import eu.excitementproject.eop.alignmentedas.p1eda.sandbox.WithVO;
import eu.excitementproject.eop.alignmentedas.p1eda.sandbox.WithoutVO;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAlignerFromLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.RelationType;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.VerbOceanLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.VerbOceanRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.lap.dkpro.MaltParserEN;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

// note that, this test requires TreeTagger dependencies in LAP pom. 
// Also note that, this test case takes a long time (due to its set-related 
// method tests) --- not every build requires this test, and the test is @ignored 
// by default. 

@SuppressWarnings("unused")
public class InspectorUtilityMethodsTest {

	@BeforeClass 
	public static void testPrep() 
	{		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.DEBUG);  // set INFO to hide Debug  
		logger = Logger.getLogger(InspectorUtilityMethodsTest.class); 
		
		logger.info("hello"); 
				
		
		// prepare an LAP 
		MaltParserEN lap = null; 
		try 
		{
			lap = new MaltParserEN(); 
			lap.generateSingleTHPairCAS("this is a test.", "TreeTagger in sight?"); 
		}
		catch (Exception e)
		{
			// check if this is due to missing TreeTagger binary and model. 
			// In such a case, we just skip this test. 
			// (see /lap/src/scripts/treetagger/README.txt to how to install TreeTagger) 
			if (ExceptionUtils.getRootCause(e) instanceof java.io.IOException) 
			{
				logger.info("Skipping the test: TreeTagger binary and/or models missing. \n To run this testcase, TreeTagger installation is needed. (see /lap/src/scripts/treetagger/README.txt)");  
				Assume.assumeTrue(false); // we won't test this test case any longer. 
			}
		}
		
		// prepare a few links
		// first prepare some aliners... 
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

		// now have some aligned data. 
		try 
		{
			// Annotate the two pairs, and add some lexical links 
			pair1 = lap.generateSingleTHPairCAS(t1, h1);
			pair2 = lap.generateSingleTHPairCAS(t2, h2);
	     	
			alignerWordNetEN.annotate(pair1);						
			alignerWordNetEN.annotate(pair2); 
			alignerVerbOceanEN.annotate(pair2);

			// and some dependency links on pair 2
			// (just first dep of T to second dep of H) 
			
			JCas pair2TView = pair2.getView(LAP_ImplBase.TEXTVIEW); 
			JCas pair2HView = pair2.getView(LAP_ImplBase.HYPOTHESISVIEW); 
			Collection<Dependency> tDeps = JCasUtil.select(pair2TView, Dependency.class); 
			Collection<Dependency> hDeps = JCasUtil.select(pair2HView, Dependency.class); 
			
			Iterator<Dependency> tDepItr = tDeps.iterator(); 
			Iterator<Dependency> hDepItr = hDeps.iterator(); 
			
			Dependency t = tDepItr.next(); 
			hDepItr.next(); 
			Dependency h = hDepItr.next(); 
			
			Token ta = t.getGovernor(); 
			Token tb = t.getDependent(); 
			Token ha = h.getGovernor(); 
			Token hb = h.getDependent(); 
			
			Target triple_on_t = new Target(pair2TView);
			FSArray fs_t = new FSArray(pair2TView, 3); 
			fs_t.set(0, ta);
			fs_t.set(1, t);
			fs_t.set(2, tb);
			triple_on_t.setTargetAnnotations(fs_t);
			triple_on_t.setBegin(ta.getBegin()); 
			triple_on_t.setEnd(tb.getEnd()); 
			
			Target triple_on_h = new Target(pair2HView); 
			FSArray fs_h = new FSArray(pair2HView, 3); 
			fs_h.set(0, ha); 
			fs_h.set(1, h); 
			fs_h.set(2, hb);			
			triple_on_h.setTargetAnnotations(fs_h);
			triple_on_h.setBegin(ha.getBegin()); 
			triple_on_h.setEnd(hb.getEnd()); 
			
			Link linkd = new Link(pair2HView); // note that, we will add the Link to HYPOTHESIS VIEW 
			linkd.setTSideTarget(triple_on_t);
			linkd.setHSideTarget(triple_on_h); 
			linkd.setDirection(Link.Direction.TtoH); 
			linkd.setStrength(1.0); 
			linkd.setAlignerID("RandomDep"); // ID of the alinger, or the resource behind the alinger
			linkd.setAlignerVersion("3.1415"); // version number of the aligner, or the resource
			linkd.setLinkInfo("rand"); // detailed information about the relation. 
			linkd.setBegin(triple_on_h.getBegin());
			linkd.setEnd(triple_on_h.getEnd());
			linkd.addToIndexes(); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		
	}
	
	private static JCas pair1; 
	private static JCas pair2; 
	
	public void prepareTestXmis() {

		// pre-process RTE English testset for the test
		File rteTestingXML = new File("../core/src/main/resources/data-set/English_test.xml");
		File evalXmiDir = new File("target/testingXmis");
		
		try {
			LAP_ImplBase lapEN = new TreeTaggerEN(); 
			P1EdaRTERunner.runLAPForXmis(lapEN, rteTestingXML, evalXmiDir);
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
	}
	
		
	// a long test. let's ignore this by default.
	@Ignore("a long test; ignored on default.") @Test
	public void testDiffPairs()
	{
		prepareTestXmis(); 		

		try {
			P1EDATemplate withVO = new WithVO(); 
			P1EDATemplate withoutVO = new WithoutVO(); 
	
			withVO.initialize(new File("src/test/resources/withVO.cmodel")); 
			withoutVO.initialize(new File("src/test/resources/withoutVO.cmodel")); 
			
			getDiffPairs(withVO, withoutVO, new File("target/testingXmis")); 
		}
		catch (Exception e)
		{
			System.err.println("Run stopped with Exception: " + e.getMessage()); 
		}
	}	
	
	@Test
	public void testSummarizeLinks() 
	{
		try {
			String out = InspectUtilsJCasAndLinks.summarizeAlignmentLinks(pair2); 
			System.out.print(out); 
			out = InspectUtilsJCasAndLinks.summarizeAlignmentLinks(pair1); 
			System.out.print(out); 

		}
		catch (Exception e)
		{
			e.printStackTrace(System.out); 
			fail (); 
		}
	}
	@Test
	public void testSummarizeJCasWordLevel()
	{
		try {
			String out = InspectUtilsJCasAndLinks.summarizeJCasWordLevel(pair2); 
			System.out.print(out); 
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail (); 
		}
	}
	
	// logger 
	private static Logger logger; 
	private boolean b; 
	
	// 
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

	// and the alingers... 
	private static AlignmentComponent alignerWordNetEN; 
	private static AlignmentComponent alignerVerbOceanEN; 

	// and test strings 
	private static String t1 = "The assassin was convicted and sentenced to death penalty";
	private static String h1 = "The killer has been accused of murder and doomed to capital punishment";
	private static String t2 = "Kennedy was killed in Dallas";
	private static String h2 = "Kennedy was wounded and died in Texas";


}
