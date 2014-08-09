package alignment.predicatetruthlink;

import java.io.IOException;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.core.component.alignment.predicatetruthlink.PredicateTruthAligner;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;
import eu.excitementproject.eop.transformations.uima.BIUFullLAPWithTruthTellerConfigured;

public class PredicateTruthAlignerTest {
	
	
	@BeforeClass
	public static void beforeClass() throws IOException {
		try {
			// create a lap with truth teller annotator
			lap = new BIUFullLAPWithTruthTellerConfigured();
			// create an aligner
			aligner = new PredicateTruthAligner();
			// annotations for reference text - all tests will examine this result 
			jcas = lap.generateSingleTHPairCAS(testText, testHypothesis);
			tView = jcas.getView(LAP_ImplBase.TEXTVIEW);
			hView = jcas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		} catch (LAPException | CASException e) {
			throw new IOException(e);
		}
		 
	}
	@Test
	public void testAgreeingPositive() throws Exception {
		aligner.annotate(jcas);
		JCas hypoView = jcas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		for (Link link : JCasUtil.select(hypoView, Link.class)) {
			System.out.println(String.format("Text phrase: %s, " +
					"hypothesis phrase: %s, " + 
					"id: %s, confidence: %f, direction: %s", 
					link.getTSideTarget().getCoveredText(),
					link.getHSideTarget().getCoveredText(),
					link.getID(), link.getStrength(),
					link.getDirection().toString()));
		}
	}
	
	static LAPAccess lap;
	static JCas jcas,tView,hView;
	static AlignmentComponent aligner;
	public static final String testText = "John refused to dance";
	public static final String testHypothesis = "John did not dance";
	 

}
