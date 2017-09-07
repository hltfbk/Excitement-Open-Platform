package eu.excitementproject.eop.transformations.uima.ae.truthteller;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.uima.fit.util.JCasUtil;

import eu.excitement.type.predicatetruth.ClauseTruthNegative;
import eu.excitement.type.predicatetruth.NegationAndUncertaintyNegative;
import eu.excitement.type.predicatetruth.PredicateTruth;
import eu.excitement.type.predicatetruth.PredicateTruthNegative;
import eu.excitement.type.predicatetruth.PredicateTruthPositive;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.test.BiuTestUtils;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;
import eu.excitementproject.eop.transformations.biu.en.predicatetruth.TruthTellerAnnotatorAE;
import eu.excitementproject.eop.transformations.uima.BIUFullLAPWithTruthTellerConfigured;

/** 
 * A test class for {@link TruthTellerAnnotatorAE}  
 * @author Gabi Stanovsky
 * @since Aug 2014
 *
 */
public class PredicateTruthAETest {
	
	@BeforeClass
	public static void beforeClass() throws LAPException, CASException, IOException {
		// Run test only under BIU environment
		BiuTestUtils.assumeBiuEnvironment();
		// create a lap with truth teller annotator
		lap = new BIUFullLAPWithTruthTellerConfigured();
		// annotations for reference text - all tests will examine this result 
		jcas = lap.generateSingleTHPairCAS(testText, testHypothesis);
		tView = jcas.getView(LAP_ImplBase.TEXTVIEW);
		hView = jcas.getView(LAP_ImplBase.HYPOTHESISVIEW);
	}
	
	@Test
	public void testPT() throws Exception {
		Collection<Annotation> annotations = new ArrayList<Annotation>(JCasUtil.select(tView, PredicateTruth.class));
		assertPTListEqual(annotations,ptExpected);
	}
	
	@Test
	public void testPTPositive() throws Exception {
		Collection<Annotation> annotations = new ArrayList<Annotation>(JCasUtil.select(tView, PredicateTruthPositive.class));
		assertPTListEqual(annotations,ptPositiveExpected);
	}
	
	@Test
	public void testPTNegative() throws Exception {
		Collection<Annotation> annotations = new ArrayList<Annotation>(JCasUtil.select(tView, PredicateTruthNegative.class));
		assertPTListEqual(annotations,ptNegativeExpected);
	}
	
	@Test
	public void testNU() throws Exception {
		Collection<Annotation> annotations = new ArrayList<Annotation>(JCasUtil.select(hView, NegationAndUncertaintyNegative.class));
		assertPTListEqual(annotations,nuNegativeExpected);
	}
	
	@Test
	public void testCTNegative() throws Exception{
		Collection<Annotation> annotations = new ArrayList<Annotation>(JCasUtil.select(tView, ClauseTruthNegative.class));
		assertPTListEqual(annotations,ctNegativeExpected);	
	}
	
	/**
	 * Verify that an observed list of annotations covers an expected list of strings
	 * @param observed
	 * @param expected
	 */
	private static void assertPTListEqual(Collection<Annotation> observed, List<String> expected){
		// assert expected and observed annotations are of the same size
		int s = observed.size();
		assertEquals(s,expected.size());
		
		//iterate over expected and observed annotations and assert all are equal
		Iterator<Annotation> observedIter = observed.iterator();
		Iterator<String> expectedIter = expected.iterator();
		for (int i=0;i<s;i++){
			String observedStr = observedIter.next().getCoveredText();
			String expectedStr = expectedIter.next();
			assertEquals(observedStr,expectedStr);
		}
	}
	
	// strings for test
	 static LAPAccess lap;
	 static JCas jcas,tView,hView;
	 private static final String testText = "John refused to dance";
	 private static final String testHypothesis = "John did not dance";
	 private static List<String> ptExpected = new ArrayList<String>();
	 private static List<String> ptPositiveExpected = new ArrayList<String>();
	 private static List<String> ptNegativeExpected = new ArrayList<String>();
	 private static List<String> nuNegativeExpected = new ArrayList<String>();
	 private static List<String> ctNegativeExpected = new ArrayList<String>();
	 
	 static{
		 ptExpected.add("refused");
		 ptExpected.add("dance");
		 ptPositiveExpected.add("refused");
		 ptNegativeExpected.add("dance");
		 nuNegativeExpected.add("dance");
		 ctNegativeExpected.add("dance");
	 }
}
