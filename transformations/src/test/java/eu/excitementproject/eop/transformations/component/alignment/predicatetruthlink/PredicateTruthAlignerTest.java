package eu.excitementproject.eop.transformations.component.alignment.predicatetruthlink;

import static eu.excitementproject.eop.transformations.component.alignment.predicatetruthlink.PredicateTruthAligner.ALIGNEMNT_TYPE_AGREEING_NEGATIVE;
import static eu.excitementproject.eop.transformations.component.alignment.predicatetruthlink.PredicateTruthAligner.ALIGNEMNT_TYPE_AGREEING_POSITIVE;
import static eu.excitementproject.eop.transformations.component.alignment.predicatetruthlink.PredicateTruthAligner.ALIGNEMNT_TYPE_DISAGREEING;
import static eu.excitementproject.eop.transformations.component.alignment.predicatetruthlink.PredicateTruthAligner.ALIGNEMNT_TYPE_NON_MATCHING;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.uima.fit.util.JCasUtil;

import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.Link.Direction;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.test.BiuTestUtils;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;
import eu.excitementproject.eop.transformations.component.alignment.predicatetruthlink.PredicateTruthAligner;
import eu.excitementproject.eop.transformations.uima.BIUFullLAPWithTruthTellerConfigured;
/** 
 * A test class for {@link PredicateTruthAligner}  
 * This test class must reside within transformations since it makes use the truthteller annotator, 
 * which is not accessible from core (where the aligner is implemented)
 * @author Gabi Stanovsky
 * @since Aug 2014
 *
 */
public class PredicateTruthAlignerTest {
	
	@BeforeClass
	public static void beforeClass() throws IOException, LAPException, PairAnnotatorComponentException, CASException {
		// Run test only under BIU environment
		BiuTestUtils.assumeBiuEnvironment();
		// create a lap with truth teller annotator
		lap = new BIUFullLAPWithTruthTellerConfigured();
		// create an aligner
		aligner = new PredicateTruthAligner();
		// annotations for reference text - all tests will examine this result 
		jcas = lap.generateSingleTHPairCAS(testText, testHypothesis);
		aligner.annotate(jcas);
		hypoView = jcas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		 
	}
	
	@Test
	public void testAgreeingPositive() throws Exception {
		List<Link> observed = new ArrayList<Link>();
		// get all positive agreeing links
		for (Link link : JCasUtil.select(hypoView, Link.class)) {
			if (link.getLinkInfo().equals(ALIGNEMNT_TYPE_AGREEING_POSITIVE)){
				observed.add(link);
			}
		}
		// verify that the observed links match the expected
		assertAlignmentListEqual(observed, positiveAgreeingExpectedText, positiveAgreeingExpectedHypo);	
	}
	
	@Test
	public void testAgreeingNegative() throws Exception {
		List<Link> observed = new ArrayList<Link>();
		// get all positive agreeing links
		for (Link link : JCasUtil.select(hypoView, Link.class)) {
			if (link.getLinkInfo().equals(ALIGNEMNT_TYPE_AGREEING_NEGATIVE)){
				observed.add(link);
			}
		}
		// verify that the observed links match the expected
		assertAlignmentListEqual(observed, negativeAgreeingExpectedText, negativeAgreeingExpectedHypo);	
	}
	
	@Test
	public void testDisagreeing() throws Exception {
		List<Link> observed = new ArrayList<Link>();
		// get all positive agreeing links
		for (Link link : JCasUtil.select(hypoView, Link.class)) {
			if (link.getLinkInfo().equals(ALIGNEMNT_TYPE_DISAGREEING)){
				observed.add(link);
			}
		}
		// verify that the observed links match the expected
		assertAlignmentListEqual(observed, disagreeingExpectedText, disagreeingExpectedHypo);	
	}
	
	@Test
	public void testNonMatching() throws Exception {
		List<Link> observed = new ArrayList<Link>();
		// get all positive agreeing links
		for (Link link : JCasUtil.select(hypoView, Link.class)) {
			if (link.getLinkInfo().equals(ALIGNEMNT_TYPE_NON_MATCHING)){
				observed.add(link);
			}
		}
		// verify that the observed links match the expected
		assertAlignmentListEqual(observed, nonMatchingExpectedText, nonMatchingExpectedHypo);	
	}
	
	/**
	 * Verify that an observed list of annotations covers an expected list of strings
	 * @param observed
	 * @param expected
	 */
	private void assertAlignmentListEqual(Collection<Link> observed, List<String> expectedText,List<String> expectedHypo){
		// assert expected and observed annotations are of the same size
		int s = observed.size();
		assertEquals(s,expectedText.size());
		
		//iterate over expected and observed annotations and assert all are equal
		Iterator<String> expectedTextIter = expectedText.iterator();
		Iterator<String> expectedHypoIter = expectedHypo.iterator();
		Iterator<Link> observedIter = observed.iterator();
		
		for (int i=0;i<s;i++){
			String expectedTextStr = expectedTextIter.next();
			String expectedHypoStr = expectedHypoIter.next();
			Link link  =  observedIter.next();
			assertEquals(link.getTSideTarget().getCoveredText(),expectedTextStr);
			assertEquals(link.getHSideTarget().getCoveredText(),expectedHypoStr);
			assertEquals(link.getDirection(), Direction.Bidirection);			
			assertEquals(link.getStrength(),1.0,EPSILON);
		}
	}
	
	
	private static LAPAccess lap;
	private static JCas jcas;
	private static JCas hypoView;
	private static AlignmentComponent aligner;
	
	/* 
	 * when adding new sentences: 
	 * make sure that no sentence has any duplicate word
	 */  
	
	private static final String testText = "John refused to dance and instead thought of jumping";
	private static final String testHypothesis = "John did not dance";
	private static final double EPSILON = 0.1;
	
	// Agreeing Positive
	private static List<String> positiveAgreeingExpectedText = new ArrayList<String>();
	private static List<String> positiveAgreeingExpectedHypo = new ArrayList<String>();
	
	//Agreeing Negative
	private static List<String> negativeAgreeingExpectedText = new ArrayList<String>();
	private static List<String> negativeAgreeingExpectedHypo = new ArrayList<String>();
	
	// Disagreeing
	private static List<String> disagreeingExpectedText = new ArrayList<String>();
	private static List<String> disagreeingExpectedHypo = new ArrayList<String>();
	
	// Non Matching 
	private static List<String> nonMatchingExpectedText = new ArrayList<String>();
	private static List<String> nonMatchingExpectedHypo = new ArrayList<String>();
	
	 
	
	 static{
		 positiveAgreeingExpectedText.add("refused");
		 positiveAgreeingExpectedHypo.add("did");
		 positiveAgreeingExpectedText.add("thought");
		 positiveAgreeingExpectedHypo.add("did");
		 
		 negativeAgreeingExpectedText.add("dance");
		 negativeAgreeingExpectedHypo.add("dance");
		 
		 disagreeingExpectedText.add("dance");
		 disagreeingExpectedHypo.add("did");
		 disagreeingExpectedText.add("thought");
		 disagreeingExpectedHypo.add("dance");
		 disagreeingExpectedText.add("refused");
		 disagreeingExpectedHypo.add("dance");
		 
		 nonMatchingExpectedText.add("jumping");
		 nonMatchingExpectedHypo.add("did");
		 nonMatchingExpectedText.add("jumping");
		 nonMatchingExpectedHypo.add("dance");
		 
	 }
	 

}


