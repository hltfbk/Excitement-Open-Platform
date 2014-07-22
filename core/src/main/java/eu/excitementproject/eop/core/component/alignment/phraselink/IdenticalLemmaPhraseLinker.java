package eu.excitementproject.eop.core.component.alignment.phraselink;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * This is a surface level aligner that aligns "identical lemma sequences" found in 
 * TextView and HypothesisView. 
 * 
 * The module add Alignment.Link instances where its target holds token sequences (longer than 1 
 * tokens). The two token sequences (one in T, the other in H) are linked only if they have 
 * identical lemma sequences. 
 * 
 * The module is language-free (you can pass CAS with any language): it simply trust the 
 * annotated lemma annotations to identify "same word". 
 * 
 * Note that, the module will annotate only the "longest lemma sequence". That is, 
 * if the CAS has T: "I have a dog.", H: "She has a dog too". 
 * It module will add only *one link* that connects three tokens of T ([have a dog]) to 
 * three tokens of H ([has a dog]). It won't link (have -> has), or (dog -> dog). 
 * 
 * ( Also note that, the module does not annotate "function words only" sequences. That is 
 * it will add links between "to emphasize" -> "to emphasize", but not "to the" -> "to the". ) 
 * 
 * Naturally, the module depends on the existence of "Lemma" annotations. If there is no Lemma 
 * in the give CAS, it will raise an exception. 
 * 
 * @author Tae-Gil Noh
 *
 */
/**
 * @author tailblues
 *
 */
public class IdenticalLemmaPhraseLinker implements AlignmentComponent {
		
	public IdenticalLemmaPhraseLinker()
	{
		// initialize nonContentPOS map 
		isNonContentPos = new HashMap<String,Boolean>(); 
		for (String s : nonContentPOSes)
		{
			isNonContentPos.put(s, true); 
		}
	}
	
	
	@Override
	public void annotate(JCas aJCas) throws AlignmentComponentException {
		
		
		if (aJCas == null)
			throw new AlignmentComponentException("annotate() got a null JCas object."); 
		
		JCas textView; 
		JCas hypoView; 
		try {
			textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
			hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		}
		catch (CASException e)
		{
			throw new AlignmentComponentException("Failed to access the Two views (TEXTVIEW, HYPOTHESISVIEW)", e); 
		}

		logger.info("annotate() called with a JCas with the following T and H;  ");   
		logger.info("TEXT: " + textView.getDocumentText()); 
		logger.info("HYPO: " + hypoView.getDocumentText());  

		// Note: we *do not* do any language check in this module. 
		// (But we do check the existence of lemma and token, since that's what we need) 

		// Get Token lists, TextTokens and HypoTokens. in order of appearance. 
		// ok. work. first; 
		// get all Tokens (by appearing orders...) 
		Collection<Token> t;  
		t = JCasUtil.select(textView, Token.class); 
		Token[] tTokens = t.toArray(new Token[t.size()]); 
		t = JCasUtil.select(hypoView, Token.class); 
		Token[] hTokens = t.toArray(new Token[t.size()]); 
		
		// matching result will be written here... 
		int[] matchingPhraseStartLocations = new int[hTokens.length]; // a value represent n-th token of TSide. . 
		int[] matchingPhraseLengths = new int[hTokens.length]; // again, a value here means token length. 
		
		// Okay, we have two list of tokens (that has access to lemma & pos)  
		// T and H. 
		// Okay, we start on H sequence, pos = 0 (first word). 
		// we start finding "longest identical sequence" from the position this pos. 
		
		for (int i=0; i < hTokens.length; i++) 
		// loop on H tokens, i is each possible "start" position for phrase. 
		{	
			int bestMatchTextPosition = -1; // -1 == we have no match 
			int bestMatchLength = 0;   

			for (int j=0; j < tTokens.length; j++)
			{	// j iterates on text tokens ... 

				int currentMatchLen = 0; 

				// call, matchOnPositions(). This utility method returns 
				// the length of "maximum" identical sequence. 
				// 0 if, match didn't even start on the position. 
				currentMatchLen = maxMatchOnPositions(i,j, hTokens, tTokens); 
				if (currentMatchLen > bestMatchLength)
				{
					bestMatchTextPosition = j; 
					bestMatchLength = currentMatchLen; 
				}
			}	
			
			// record the best match for this position (i) 
			matchingPhraseStartLocations[i] = bestMatchTextPosition; 
			matchingPhraseLengths[i] = bestMatchLength; 			
		}

		// Okay. we have the full information in the two arrays. 
		// matchingPhraseStartLocation and matchingPhraseLength 
		// -1 means none matching. 

		// Part two. annotating match with alignment.Link. 
		// We do this by calling a utility method with the above information.  
		addLinkAnnotations(aJCas, matchingPhraseStartLocations, matchingPhraseLengths); 
		
	}

	/**
	 * A utility method that matches the "longest" lemma match 
	 * on the given positions (on the two arrays) 
	 * 
	 * A call with (i,j, iArray, jArray) means 
	 *   "compare and find me the longest lemma match
	 *        that starts on position i of iArray, and on position j of jArray"  
	 * 
	 * @param i 
	 * @param j
	 * @param iArray
	 * @param jArray
	 * @return
	 * @throws AlignmentComponentException
	 */
	public static int maxMatchOnPositions(int i, int j, Token[] iArray, Token[] jArray) throws AlignmentComponentException 
	{		
		// boundary check, (is it valid?) 
		// if not, return 0. (no match) 
		if ( (i >= iArray.length) || (j >= jArray.length) )
		{
			return 0; 
		}
		
		// well, try match. 
		Lemma iLemma = iArray[i].getLemma(); 
		Lemma jLemma = jArray[j].getLemma(); 
//		POS iPos = iArray[i].getPos(); 
//		POS jPos = jArray[j].getPos(); 
				
		// sanity chcek 
		//if ((iLemma == null) || (jLemma == null) || (iPos == null) || (jPos == null))
		if ((iLemma == null) || (jLemma == null))
		{
			throw new AlignmentComponentException("The JCas must have Lemmas and POSes annotated (connected) to Tokens."); 
		}
		
		if (iLemma.getValue().equals(jLemma.getValue()))
		{   // we got a match. - add 1, and recurse. 
			return (1 + maxMatchOnPositions(i+1, j+1, iArray, jArray)); 
		}
		else 
		{ // no match. 
			return 0; 
		}
	}
	
	/**
	 * A utility method, that adds Alignment.Link instances for the given 
	 * information. 
	 * 
	 * @param aJCas 
	 * @param matchingPhraseStartLocationsOnText index "n" of this array is for n-th token of HSide. The value means m-th token on Tside. -1 means, no match. 
	 * @param matchingPhraseLengths index "n" of this array is for n-th token of HSide. The value means length of matching tokens.  
	 */
	private static void addLinkAnnotations(JCas aJCas, int[] matchingPhraseStartLocationsOnText, int[] matchingPhraseLengths)
	{
		logger.info("addLinnkAnnotations() called with the following info:"); 
		logger.info("matchingPhraseStartingLocationsOnText:"  + Arrays.toString(matchingPhraseStartLocationsOnText)); 
		logger.info("matchingPhraseLengths:" + Arrays.toString(matchingPhraseLengths)); 

		// TODO write the code, utilize 
		// MeteorPhraseResourceAligner.addOneAlignmentLinkOnTokenLevel(JCas textView, JCas hypoView, int fromBegin, int fromEnd, int toBegin, int toEnd, Link.Direction dir) throws CASException

		// TODO make sure don't add links that only has non 
		
	}
	
	
	@Override
	public String getComponentName() {
		return this.getClass().getName(); // return class name as the component name 
	}

	@Override
	public String getInstanceName() {
		return null; // this module does not support multiple-instances (e.g. with different configurations) 
	}
	
	public static Boolean containsOnlyNonContentPOSes(Token[] tokenArr)
	{
		
//		for (Token t : tokenArr)
//		{
//			// TODO write this 
//		}

		return false; 
	}
	
	// logger 
	private final static Logger logger = Logger.getLogger(MeteorPhraseResourceAligner.class);

	// 
	final private Map<String,Boolean> isNonContentPos; 

	// Non-configurable, (hard-coded) settings. 
	// non Content POS types. (among DKPro POS types that we use)  
	// Punctuation, Preposition, Others, Conjunction, and Articles.  
	final private static String[] nonContentPOSes = {"PUNC", "PP", "O", "CONJ", "ART"}; 	
}
