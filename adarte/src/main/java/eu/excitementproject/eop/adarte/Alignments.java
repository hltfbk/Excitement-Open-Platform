package eu.excitementproject.eop.adarte;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAligner;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;
import eu.excitement.type.alignment.GroupLabelInferenceLevel;
import eu.excitement.type.alignment.Link;

/**
 * 
 * This class contains the alignments produced by the lexical aligner between T and H.
 * 
 * 3 different types of alignments have been defined: 
 * LOCAL_ENTAILMENT (e.g. WORDNET__SYNONYM)
 * LOCAL_CONTRADICTION (e.g. WORDNET__ANTONYM)
 * LOCAL_SIMILARITY (e.g. WORDNET__SIMILAR_TO)
 * 
 * Given two words, the method getAlignmentType of the class returns the type of alignment that there
 * exists between the two tokens.
 * 
 * @author roberto zanoli
 * @author silvia colombo
 * 
 * @since January 2015
*/

// The used aligner component has been deprecated and it will be replaced in the next code release.
@SuppressWarnings("deprecation")
public class Alignments {
	
	// Entailment relations type
	public static String LOCAL_ENTAILMENT = GroupLabelInferenceLevel.LOCAL_ENTAILMENT.toString();
	public static String LOCAL_CONTRADICTION = GroupLabelInferenceLevel.LOCAL_CONTRADICTION.toString();
	public static String LOCAL_SIMILARITY = GroupLabelInferenceLevel.LOCAL_SIMILARITY.toString();
	
	// Entailment relations direction
	public static String DIRECTION_HtoT = Link.Direction.HtoT.toString();
	public static String DIRECTION_TtoH = Link.Direction.TtoH.toString();
	public static String DIRECTION_Bidirection = Link.Direction.Bidirection.toString();
	
	// It contains the words that have been aligned with their links
	// The key is like: token1__token2, where `___` separates the 2 tokens, e.g. assassin__killer 
	// while the value is the link produced by the alingner component,
	private Map<String,Link> alignments;
	
	// The lexical aligner component to be used to create the alignments
	private LexicalAligner aligner;
	
	/**
     * The constructor
     */
	public Alignments() {
		
		alignments = new HashMap<String,Link>();
		
	}

	/**
     * The constructor
     * 
     * @param aligner The lexical aligner to be used for creating the alignments
     * @param jcas the CAS containing T and H to be aligned.
     * 
     */
	public Alignments(LexicalAligner aligner, JCas jcas) throws Exception {
		
    	this();
    	
    	this.aligner = aligner;
    	
		try {
			
			// Call the aligner component to get the alignments between T and H
			if (this.aligner != null) {
				//logger.finer("\ngetting the alignments ...");
				this.aligner.annotate(jcas);
				//logger.finer("done.");
			}
			
			//get the HYPOTHESIS view
			JCas hypoView = jcas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
			//cycle through the alignments
			for (Link link : JCasUtil.select(hypoView, Link.class)) {
				
				String key = link.getTSideTarget().getCoveredText().replaceAll(" ", "_|_") +
				        		"__" + 
				        		link.getHSideTarget().getCoveredText().replaceAll(" ", "_|_");
				     
				//for a couple of tokens it can save a type of alignment only (the first) in the
				//order as provided by the aligner component.
				if (!alignments.containsKey(key))
					alignments.put(key, link);
				
			}
			
		} catch (Exception e) {
			
			throw new Exception(e.getMessage());
			
		}
		
	}
    
    
	/**
     * Given 2 tokens, token1 and token2 it says if there is a local LOCAL_ENTAILMENT, LOCAL_CONTRADICTION or LOCAL_SIMILARITY
     * between the 2 tokens.
     * 
     * @param token1
     * @param token2
     * 
     * @return an array of 3 values: 
     * 
     * 1) The type of alignment: LOCAL_ENTAILMENT | LOCAL_CONTRADICTION | LOCAL_SIMILARITY
     * 2) The direction of the entailment: TtoH | HtoT | Bidirectional
     * 3) The alignment relation rule: // HYPERNYM | SYNONYM | ...
     * 
     */
	protected String[] getAlignment(FToken token1, FToken token2, String wordMatch) {
    	
		String[] result = new String[3];
		
		String alignmentType = null;;
		
		//if there is a match between the two tokens we have LOCAL_ENTAILMENT
		if (token1.match(token2, wordMatch)) {
		    	alignmentType = LOCAL_ENTAILMENT;
		    	result[0] = alignmentType;
		    	result[1] = DIRECTION_Bidirection;
		    	//result[2] = null; //default value
		}
		// possible ALIGNMENTS only when the dprel relation (see dependency parsing relations) of the 2 tokens
		// is the same.
		else if (token1.getDprel().equals(token2.getDprel())) {
		
			//the alignment between token1 and token2
			Link alignment = alignments.get(token1.getForm() + "__" + token2.getForm());
			
			// LOCAL_ENTAILMENT | LOCAL_CONTRADICTION | LOCAL_SIMILARITY
		    if (alignment != null && alignment.getGroupLabelsInferenceLevel().size() != 0) {
		    	
		    	//take the first valid alignment; this could be an issue in case of contrasting multiple alignments.
		    	alignmentType = (alignment.getGroupLabelsInferenceLevel().iterator().next().toString());
		    	
		    	result[0] = alignmentType; //LOCAL_ENTAILMENT | LOCAL_CONTRADICTION  | LOCAL_SIMILARITY
		    	result[1] = alignment.getDirectionString(); // TtoH | HtoT | Bidirectional
		    	result[2] = alignment.getLinkInfo(); // e.g. HYPERNYM | SYNONYM in WordNet based aligner
		    	
		    }
		    	
		}
    		
    	return result;
    	
	}
    
	
}
