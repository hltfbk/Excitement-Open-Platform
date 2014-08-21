package eu.excitementproject.eop.alignmentedas.p1eda.scorers;

import java.util.Vector;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;

/**
 * This is a very simple "alignment evaluator" which reports two numbers; 
 * "number of covered content word in H", "number of content words in H".
 * 
 * @author Tae-Gil Noh
 *
 */
public class SimpleWordCoverageCounter implements ScoringComponent {

	/**
	 *  Argument version: gets one linkID of alignment.Link. Then the module uses alignment.Link instances 
	 *  with that ID, to calculate "coverage". 
	 *  If null given, the module uses, *all* link instances to calculate coverage. 
	 *  
	 */
	public SimpleWordCoverageCounter(String linkID) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getComponentName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInstanceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<Double> calculateScores(JCas cas)
			throws ScoringComponentException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
