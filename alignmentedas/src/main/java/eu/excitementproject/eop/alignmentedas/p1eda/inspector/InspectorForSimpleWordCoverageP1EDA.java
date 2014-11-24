package eu.excitementproject.eop.alignmentedas.p1eda.inspector;

//import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

import eu.excitement.type.alignment.Link;
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
	public static void inspectDecision(TEDecisionWithAlignment decision, PrintStream out)
	{
		// a. get JCas, output JCas summary 
		// calling summarizeJCasWordLevel 
		
		// b. output alignment.Link summary 
		// calling summarizeAlignmentLinks
		
		// c & d:  coverage histogram and covering links 
		
		// 1) first get covering links per word 
		// calling getCoveringLinksTokenLevel 
		
		// 2) fill up an array with coverage numbers. 
		// (call?)  
		
		// 3) make linkIndex (a hashmap where  linkIndex.get(Link l) returns numeric id of alignment.Link as reported in step b.
		// (call?) 

		// output. "coverage and covering links" 
		// (c. & d.) 
		// one word per line \t coverage score \t <link ids> < > < >
		
		// d. output covering links (vertical) 
		//   one word per line
		
		// e. output feature vector, as-is. 
	}

	@SuppressWarnings("unused")
	private static HashMap<Link, Integer> makeLinkIndex(List<Link> allLinks)
	{
		// TODO 
		return null; 
	}
	
	@SuppressWarnings("unused")
	private List<Double> coveragePerWord(List<List<Link>> coveringLinks) 
	{
		//TODO 
		return null; 
	}
}
