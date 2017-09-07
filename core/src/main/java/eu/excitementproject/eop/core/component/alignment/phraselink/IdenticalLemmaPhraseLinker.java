package eu.excitementproject.eop.core.component.alignment.phraselink;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
//import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
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
 * TODO: FIXIT - this is only partially true -- each word in H side are checked again and again. 
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
		
	public IdenticalLemmaPhraseLinker() throws AlignmentComponentException
	{
//		// initialize nonContentPOS map 
//		isNonContentPos = new HashMap<String,Boolean>(); 
//		for (String s : nonContentPOSes)
//		{
//			isNonContentPos.put(s, true); 
//		}
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
		
		// post processing: let's save only the "maximum coverage" cases. (e.g. 
		// if this token has already been covered with identical (or longer) pattern, 
		// let's ignore "less-covered term". (e.g.  when we have  [this very moment -> this very moment]
		// we ignore [very moment -> very moment]. ) 

		int lastBestMatchPos = -1; 
		int lastBestMatchLen = 0; 
		int[] finalMatchingPhraseStartLocations = new int[hTokens.length]; // a value represent n-th token of TSide. . 
		int[] finalMatchingPhraseLengths = new int[hTokens.length]; // again, a value here means token-sequence length. 
		
		for(int i=0; i < hTokens.length; i++)
		{
			int bestMatchPos = matchingPhraseStartLocations[i]; 
			int bestMatchLen = matchingPhraseLengths[i]; 
			
			if ( (bestMatchPos == (lastBestMatchPos + 1)) && (lastBestMatchLen == (bestMatchLen + 1)) ) // essentially, previous one covered this, with exactly same sequence...
			{	// if that's the case, we ignore this link
				finalMatchingPhraseStartLocations[i] = -1; 
				finalMatchingPhraseLengths[i] = 0; 
			}
			else
			{  // otherwise, use it as is 
				finalMatchingPhraseStartLocations[i] = matchingPhraseStartLocations[i];
				finalMatchingPhraseLengths[i] = matchingPhraseLengths[i]; 
			}
			
			lastBestMatchPos = bestMatchPos;
			lastBestMatchLen = bestMatchLen; 
		}

		// Okay. we have the full information in the two arrays. 
		// matchingPhraseStartLocation and matchingPhraseLength 
		// -1 means none matching. 

		// Part two. annotating match with alignment.Link. 
		// We do this by calling a utility method with the above information.  
		addLinkAnnotations(aJCas, finalMatchingPhraseStartLocations, finalMatchingPhraseLengths, tTokens, hTokens); 
		
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
	private static void addLinkAnnotations(JCas aJCas, int[] matchingPhraseStartLocationsOnText, int[] matchingPhraseLengths, Token[] tTokens, Token hTokens[]) throws AlignmentComponentException
	{
		logger.debug("addLinnkAnnotations() called with the following info:"); 
		logger.debug("matchingPhraseStartingLocationsOnText:"  + Arrays.toString(matchingPhraseStartLocationsOnText)); 
		logger.debug("matchingPhraseLengths:" + Arrays.toString(matchingPhraseLengths)); 

		int countNewLinks = 0; 
		int ignoredNoncontentMatches = 0; 
		// Okay, we have enough information. 
		// Add alignment.Link annotations by utilizing static method 
		// MeteorPhraseResourceAligner.addOneAlignmentLinkOnTokenLevel(JCas textView, JCas hypoView, int fromBegin, int fromEnd, int toBegin, int toEnd, Link.Direction dir) throws CASException

		for (int i=0; i < matchingPhraseStartLocationsOnText.length; i++)
		{
			// i-th Token of Hypothesis, has no matching identical lemma word/phrase on Text. Pass. 
			if (matchingPhraseStartLocationsOnText[i] == -1) 
				continue; 
			
			// The best (longest) "identically matching" lemma-sequence of current token (ith, on Hypothesis) 
			// is starting on "startingTokenIdx" on TextTokens, and ends on "endingTokenIdx". 
			int startingTokenIdx = matchingPhraseStartLocationsOnText[i]; 
			int endingTokenIdx = startingTokenIdx + matchingPhraseLengths[i] - 1;

			String logstring = ""; 
			for(int j=0; j < matchingPhraseLengths[i]; j++)
			{
				logstring += hTokens[i+j].getCoveredText() + " "; 
			}

			logger.debug("addLinkAnnotations: considering the following sequence" + "\"" + logstring + "\""); 

			// check exclusion case. 
			if (containsOnlyNonContentPOSes (Arrays.copyOfRange(tTokens, startingTokenIdx, endingTokenIdx + 1)))
			{
				logger.debug("will not add an alignment.Link for this sequence."); 
				ignoredNoncontentMatches ++; 
				continue; 
			}
			
			// consider: remove punctuations at the ending of a sequence? hmm. maybe not. if to do so, here is the place 
			// yet another a boolean asking method.. 
			
			logger.debug("Adding an alignment.Link for the sequence."); 
			
			// Okay. it is normal, so let's prepare to add Token level alignment.Link 
			int tSideBegin; 
			int tSideEnd;
			int hSideBegin;
			int hSideEnd;

			try {
				tSideBegin = tTokens[startingTokenIdx].getBegin(); 
				tSideEnd = tTokens[endingTokenIdx].getEnd(); 
				hSideBegin = hTokens[i].getBegin(); 
				hSideEnd = hTokens[i + matchingPhraseLengths[i] -1].getEnd(); 
			} catch (ArrayIndexOutOfBoundsException e )
			{
				throw new AlignmentComponentException("Internal integrity failure: internal logic of annotate() generated wrong parameter for the utility static method.", e); 
			}
					
			// Now we can add Link itself... Borrowing a public static utility method from another module. 
			
			try {
				JCas textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
				JCas hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
				Link.Direction d = Link.Direction.Bidirection; // since it is "identical". 
								
				Link aNewLink = MeteorPhraseResourceAligner.addOneAlignmentLinkOnTokenLevel(textView, hypoView, tSideBegin, tSideEnd, hSideBegin, hSideEnd, d);
				
				// as the Javadoc of the above utility method says, we need to add 
				// The caller must do after the call .setStrength() .setAlignerID() .setAlignerVersion() .setLinkInfo(). (Also groupLabel, if using that) - (But this method does add the new Link to CAS annotation index)
				aNewLink.setStrength(DEFAULT_LINK_STR); 
				aNewLink.setAlignerID(ALIGNER_ID);
				aNewLink.setAlignerVersion(ALIGNER_VER);
				aNewLink.setLinkInfo(ALIGNER_LINK_INFO); 
				
				countNewLinks++; 
				
			} catch (CASException e)
			{
				throw new AlignmentComponentException("Adding link instance failed with a CAS Exception. Something wasn't right on the input CAS.", e); 
			}
		}		
		
		logger.info("added " + countNewLinks + " new links on the CAS" + " (ignored " + ignoredNoncontentMatches + " function-word only possible links)"); 
	}
	
	
	@Override
	public String getComponentName() {
		return this.getClass().getName(); // return class name as the component name 
	}

	@Override
	public String getInstanceName() {
		return null; // this module does not support multiple-instances (e.g. with different configurations) 
	}
	
	@Override
	public void close() throws AlignmentComponentException
	{
		// nothing to close in this aligner. 
	}

	private static Boolean containsOnlyNonContentPOSes(Token[] tokenArr) throws AlignmentComponentException
	{
		logger.debug("checking non content POSes only or not: "); 

		String logline=""; 
		Boolean nonContentPOSesOnly = true; 
		for(Token t : tokenArr)
		{
			POS p = t.getPos(); 
			if (p == null)
			{
				throw new AlignmentComponentException("Unable to Process this CAS: There is one (or more) token without POS annotation. The process requires POS and Lemma annotated.");
			}
			String s = p.getType().toString(); 	
			String typeString = s.substring(s.lastIndexOf(".") + 1); 
			logline += t.getCoveredText() + "/" + typeString + ", "; 
			if (!(isNonContentPos.containsKey(typeString)) )
			{
				nonContentPOSesOnly = false; 
				// break; // no need to continue. 
			}
		}
		logger.debug(logline + " => " + nonContentPOSesOnly.toString()); 

		return nonContentPOSesOnly; 
	}
	
	// logger 
	private final static Logger logger = Logger.getLogger(IdenticalLemmaPhraseLinker.class);


	// Non-configurable, (hard-coded) settings. 
	// non Content POS types. (among DKPro POS types that we use)  
	// Punctuation, Preposition, Others, Conjunction, and Articles.  
	final private static String[] nonContentPOSes = {"PUNC", "PP", "O", "CONJ", "ART"}; 	
	
	public static Map<String,Boolean> isNonContentPos = new HashMap<String, Boolean>(); 
	static {
		// initialize nonContentPOS map 
		isNonContentPos = new HashMap<String,Boolean>(); 
		for (String s : nonContentPOSes)
		{
			isNonContentPos.put(s, true); 
		}		
	}
	
	// meta-information that will be added on link instances added by the module. 
	final private static double DEFAULT_LINK_STR = 1.0; 
	final private static String ALIGNER_ID = "IdenticalLemmas";
	final private static String ALIGNER_VER = "1.0";
	final private static String ALIGNER_LINK_INFO = "SameLemma"; 
			
}
