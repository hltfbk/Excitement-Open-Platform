package eu.excitementproject.eop.alignmentedas.p1eda.inspector;

import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;

/**
 * 
 * This class holds (mostly) static methods to look into a result of P1EDA SimpleWordCovereage configurations. 
 * 
 * P1EDA returns TEDecisionWithAlignment type, which holds JCas (that holds all alignments and annotations of T-H), 
 * and feature values used in the configuration. 
 * 
 * The utility methods in this class uses those two returned data type to look-into why the EDA 
 * decided the decision. 
 * 
 * @author Tae-Gil Noh 
 *
 */
public class InspectorForSimpleWordCoverageP1EDA {

	/**
	 * Okay, this is a method designed to inspect a decision on a T-H pair, from a P1EDA. 
	 * Pass TEDecisionWithAlignment object, the method will output various useful information 
	 * to the STDOUT. Hopefully, the information would help you understand why the EDA made 
	 * such a decision --- and improve aligners, features, and so on for the EDA. 
	 * 
	 * More specifically, it outputs the following informations (in this order, as texts). 
	 * 
	 * - a. (short) summary of JCas content & annotations. 
	 * - b. (indexed) list of alignment.Link in the JCas. 
	 * - c. (per H-words) coverage histogram. (how much this word is covered from T, (reconstructed) EDA's internal belief.) 
	 * - d. (per H-words) covering links. (evidences for the coverage histogram, which links cover this word?) 
	 * - e. (a feature vector) feature value that represents the pair, as used by EDA's underlying classifier. 
	 * 
	 * TODO MAYBE - f. some info for classifier model? for better understanding effect of feature values? 
	 * 
	 * @param decision
	 */
	public static void inspectDecision(TEDecisionWithAlignment decision)
	{
		
		
	}
	
	
	

}
