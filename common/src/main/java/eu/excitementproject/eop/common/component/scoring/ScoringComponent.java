/**
 * 
 */
package eu.excitementproject.eop.common.component.scoring;
import java.util.Vector;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.Component;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;

/**
 * <P>
 * This component accepts a T-H pair (represented in a JCas) as the input, and returns 
 * a vector of scores (as Vector<Double>). The scores can be anything that represent 
 * some aspects of the given T-H pair. We can easily imagine various scores that report 
 * possible interesting "features" of the given pair. For example, number of shared 
 * arguments and predicates, location of main predicate, length (or length difference) 
 * of T/H, etc.
 * <P> In a sense, you can regard this type of component as a "feature extraction 
 * component" where the extracted features are reported back as a vector of numbers. 
 * What each number of the resulting vector means is different among components. This, 
 * should be documented by each component implementers in JavaDoc of the component.
 * <P> One special case for reporting back scores, is the distance calculation 
 * (or similarity calculation) between a T-H pair. Since distance calculation between 
 * T-H is such a common capability needed for various entailment methods, we define it 
 * as a separate interface that extends the scoring component interface. 
 * 
 * @See interface DistanceCalculation 
 * 
 * @author Gil
 *
 */
public interface ScoringComponent extends Component {
	
	public Vector<Double> calculateScores(JCas cas) throws ScoringComponentException; 

}
