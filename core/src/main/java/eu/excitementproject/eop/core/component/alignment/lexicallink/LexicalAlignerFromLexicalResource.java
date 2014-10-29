package eu.excitementproject.eop.core.component.alignment.lexicallink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
//import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.GroupLabelDomainLevel;
import eu.excitement.type.alignment.GroupLabelInferenceLevel;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.Link.Direction;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;
import static eu.excitementproject.eop.core.component.alignment.phraselink.MeteorPhraseResourceAligner.addOneAlignmentLinkOnTokenLevel; 

/**
 *  
 * TODO full JavaDoc document
 * (what this modules does) 
 * (goal: use rich lexical resources as lexical aligner) 
 * 
 * (how it does what it does) 
 * 
 * <H3> About passing two group label mappings </H3> 
 * 
 * <P>
 * Constructors can accept group label mappings, for adding "canonical relations" 
 * to the alignment.Link. Key of such map should have "info" field string of the
 * underlying resource (e.g. "synonym") as key, and the value should have one or 
 * more GroupLabel (as set). (e.g. { "LOCAL_ENTAILMENT", "LOCAL_SIMILARITY" } )
 * For the above example, when the aligner adds links with lexical rule of info field
 * (of LexicalRule) "synonym", it will add canonical relation (enum) "LOCAL_ENTAILMENT", 
 * "LOCAL_SIMILARITY" on that link. You can pass two maps: one for inference level 
 * (generic) relation, one for domain level (more specific) canonical relation. 
 * Note that both mappings are optional (can be null). If so, that groupLabels will 
 * simply not added.  
 * 
 * <P> To check available canonical relations see GroupLabelDomainLevel and 
 * GroupLabelInferenceLabel. </P>  
 * 
 * <P> 
 * Memo: no need to care about "only this POS" feature of old Lexical Aligner. 
 * 
 * 
 * @author Tae-Gil Noh
 *
 */
public class LexicalAlignerFromLexicalResource implements AlignmentComponent {

	
	
	/**
	 * @param res the underlying LexicalResource. Cannot be null
	 * @param defaultGroupLabel a set of inference level group label, and they will be set as default value for alignment.Links of this aligner (e.g. the provided group label will be added when the map groupLabelMapI is not provided, or map look up does not yield for LexicalRule relation)
	 * @param maxPhraseLen what is the length of maximum phrase in the underlying resource? set only when the underlying resource supports multi-word expressions 
	 * @throws AlignmentComponentException
	 */
	public LexicalAlignerFromLexicalResource(LexicalResource<? extends RuleInfo> res, Set<GroupLabelInferenceLevel> defaultGroupLabel, int maxPhraseLen) throws AlignmentComponentException
	{
		this(res, true, maxPhraseLen, null, null, defaultGroupLabel); 		
	}

	/**
	 * @param res the underlying LexicalResource. Cannot be null
	 * @param maxPhraseLen what is the length of maximum phrase in the underlying resource? set only when the underlying resource supports multi-word expressions 
	 * @throws AlignmentComponentException
	 */
	public LexicalAlignerFromLexicalResource(LexicalResource<? extends RuleInfo> res, int maxPhraseLen) throws AlignmentComponentException
	{
		this(res, true, maxPhraseLen, null, null, null); 		
	}
	
	/**
	 * @param res the underlying LexicalResource. Cannot be null
	 * @throws AlignmentComponentException
	 */
	public LexicalAlignerFromLexicalResource(LexicalResource<? extends RuleInfo> res) throws AlignmentComponentException
	{
		this(res, false, 1, null, null, null); 		
	}
	
	/**
	 * @param res the underlying LexicalResource. Cannot be null
	 * @param defaultGroupLabel a set of inference level group label, and they will be set as default value for alignment.Links of this aligner (e.g. the provided group label will be added when the map groupLabelMapI is not provided, or map look up does not yield for LexicalRule relation)
	 * @throws AlignmentComponentException
	 */
	public LexicalAlignerFromLexicalResource(LexicalResource<? extends RuleInfo> res, Set<GroupLabelInferenceLevel> defaultGroupLabel) throws AlignmentComponentException
	{
		this(res, false, 1, null, null, defaultGroupLabel); 
	}
	
	/**
	 * Full Constructor for the class: use other convenient constructors, if you don't require of the fields. 
	 * 
	 * @param res 	the underlying LexicalResource. Cannot be null.  
	 * @param supportPhrase  The underlying resource supports phrases (multi-word expression)? 
	 * @param maxPhraseLen (checked only when supportPhrase is true) what is the length of maximum phrase in the underlying resource? (the aligner will lookup only to that length) 
	 * @param groupLabelMapI map -  which will let us know how resource specific "info" string would be mapped into canonical enum value that groups alignment.Link. This is for inference level map. (alignment, contradictory, etc --- generic ). The value is optional, and can be null. if null, the aligner won't add canonical relation of inference level group label).  
	 * @param groupLabelMapD map -  which will let us know how resource specific "info" string would be mapped into canonical enum value that groups alignment.Link. This is for inference level map. (alignment, contradictory, etc --- generic ). The value is optional, and can be null. if null, the aligner won't add canonical relation of domain level group label) 
	 * @param defaultGroupLabel - a set of inference level group label, and they will be set as default value for alignment.Links of this aligner (e.g. the provided group label will be added when the map groupLabelMapI is not provided, or map look up does not yield for LexicalRule relation) 
	 * 
	 */
	public LexicalAlignerFromLexicalResource(LexicalResource<? extends RuleInfo> res, Boolean supportPhrase, int maxPhraseLen, Map<String,Set<GroupLabelInferenceLevel>> groupLabelMapI, Map<String,Set<GroupLabelDomainLevel>> groupLabelMapD, Set<GroupLabelInferenceLevel> defaultGroupLabel) throws AlignmentComponentException
	{
		// set underlying LexicalResource
		if (res != null)
		{
			this.underlyingResource = res; 
		}
		else
		{
			throw new AlignmentComponentException("Unable to build an aligner from null Lexical Resource: lexicalResource passed was null"); 
		}
		
		// maps (which will let us know how resource specific "info" string would be 
		// mapped into canonical enum values. 
		// Both can be null (optional values) 
		this.mapInfoToGroupLabelInference = groupLabelMapI; // inference level map. (alignment, contradictory, etc --- generic ) 
		this.mapInfoToGroupLabelDomain = groupLabelMapD; // domain level map. (lexical relations ... predicate relations, etc --- specific ) 
		this.defaultGroupLabel = defaultGroupLabel; 
		
		// setting phrase flag 
		// (multiple lemmas forming one entry in the underlying resource or not 
		// if supported, set the length of maximum phrase, too) 
		if (supportPhrase && maxPhraseLen > 1)
		{
			this.supportPhrases = true; 
			this.phraseMaxLen = maxPhraseLen; 
		}
		else
		{
			this.supportPhrases = false; 
			this.phraseMaxLen = 1; 
		}			
	}

	@Override
	public void annotate(JCas aJCas) throws AlignmentComponentException {
				
		// get T lemma sequence as one single string 
		String tLemmaSeq = null; // TEXTVIEW Lemma sequences (ordered) as one string, for quick existence check 
//		String hLemmaSeq = null; // HYPOTHESISVIEW Lemma sequences (ordered) as one string, for candidate generation  
		Lemma[] tSideLemmas = null; 
//		Lemma[] hSideLemmas = null; 
		List<Lemma[]> allHSideCandidates = null; 
		
		try {
			tLemmaSeq = getLemmasAsStringSequence(aJCas.getView(LAP_ImplBase.TEXTVIEW));
			Collection<Lemma> lemmas = JCasUtil.select(aJCas.getView(LAP_ImplBase.TEXTVIEW), Lemma.class); 
			tSideLemmas = lemmas.toArray(new Lemma[lemmas.size()]); 

//			hLemmaSeq = getLemmasAsStringSequence(aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW));
			allHSideCandidates = getAllPossibleCandidates(aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW)); 
		}
		catch (CASException ce)
		{
			throw new AlignmentComponentException("unable to access views (text or hypothesis) of the given CAS");
		}
		
		// for each candidate, query the underlying resource,  
		// check for applicable rules; and if found, add alignment.link. 
		try {
			for (Lemma[] oneCand : allHSideCandidates)
			{		
				// TEXT -> HYPOTHESIS SIDE first 
				String candVal = lemmaArrAsString(oneCand); 
				//logger.debug("check cand: " + candVal); 
				for (LexicalRule<? extends RuleInfo> rule : underlyingResource.getRulesForRight(candVal, null))
				{
					// does this rule applicable in this T-H pair?  
					if (tLemmaSeq.contains(rule.getLLemma()))  
					{
						// if so, add an alignment link! 
						addAlignmentLinkT2H(aJCas, rule, tSideLemmas, oneCand); 
					}
				}
				// TODO (MAYBE?) Reflect POS (especially one-word entry)  
				// please note that here, getRulesForRight call is done without POS info. (null) 
				// This is mainly because that we do "phrase" level support where we 
				// can't really tell POS apart (multiple POSes). 
				// But for single-word queries, we might still can use POS; but for now, 
				// we don't pass POS, just as old lexical aligner did not. 
				
				// TODO (PROLLY)  
				// HYPOTHESIS -> TEXT side too, if we need.  
			}
		} catch (LexicalResourceException le)
		{
			throw new AlignmentComponentException("Underlying Lexical Resource raised an exception: " + le.getMessage(), le); 
		}
				
	}	
	
	// utility methods 
	private void addAlignmentLinkT2H(JCas aJCas, LexicalRule<?> rule, Lemma[] tLemmasToBeMatched, Lemma[] hSideTarget) throws AlignmentComponentException
	{
		
		logger.debug("addAlignmentLinkT2H: got request of adding links for rule { " + rule.getLLemma() + " => " + rule.getRLemma() + " } on H-SOFA " + hSideTarget[0].getBegin() + " <-> " + hSideTarget[hSideTarget.length -1].getEnd());
		
		// special workaround code for WordNet --- wordNet tries non-exact matching for phrases. 
		// the following method will detect any non-exact match. 
		if (isRuleRHSWordnetNonExactMatch(rule, hSideTarget))
		{
			logger.debug("Skipping non-exact matching rule of WordNet"); 
			return; // we ignore such non-exact match phrase. 
		}
		
		// okay. Prepare link. First, find all applicable locations on TEXT side. 	
		
		String[] ruleLeft = rule.getLLemma().split("\\s+"); // Hmm. this assumption of white space and lemma separations are okay for current languages... 
		List<Integer> matches = findAllMatches(tLemmasToBeMatched, ruleLeft);
	
		// can't be zero. if so, internal integrity failure. 
		assert(matches.isEmpty() != true); 
		logger.debug("addAlignmentLinkT2H: found " + matches.size() + " applicable places on T side"); 
		
		for (int matchLoc : matches)
		{
			// get begin-end locations and call 
			// addOneAlignmentLinkOnTokenLevel
			int toBegin = hSideTarget[0].getBegin(); 
			int toEnd = hSideTarget[hSideTarget.length - 1].getEnd(); 
			int fromBegin = tLemmasToBeMatched[matchLoc].getBegin(); 
			int fromEnd = tLemmasToBeMatched[matchLoc + ruleLeft.length -1].getEnd(); 
			
			JCas textView; 
			JCas hypoView;
			Direction dir = Direction.TtoH; // TODO: (reminder) you add HtoT also, this should be checked and updated (one, not two TtoH, HtoT) - on HtoT side check. 
			try {
				textView = aJCas.getView(LAP_ImplBase.TEXTVIEW); 
				hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW); 
			} catch (CASException ce)
			{
				throw new AlignmentComponentException("unable to access views (text or hypothesis) of the given CAS");
			}
			
			// finally, call the actual worker to add alignment link... 
			Link newLink = null; 
			try {
				newLink = addOneAlignmentLinkOnTokenLevel(textView, hypoView, fromBegin, fromEnd, toBegin, toEnd, dir); 
			} catch (CASException ce)
			{
				throw new AlignmentComponentException("Adding Alignment.Link instance failed in CAS access: " + ce.getMessage(), ce);
			}
			
			// addOneAlingmentLinkOnTokenLevel does not add the following MetaInfo. 
			// add them accordingly... 
			newLink.setStrength(rule.getConfidence()); 
			newLink.setAlignerID(rule.getResourceName()); 
			newLink.setAlignerVersion(""); 
			newLink.setLinkInfo(rule.getRelation()); 	
			
			// and finally, add GroupLabel 
			addGroupLabel(rule, newLink); 
		}
	}
	
	private void addGroupLabel(LexicalRule<?> rule, Link aLink)
	{
		// TODO: code for add Group Label  
		// inference level groupLabel 
		// if table is given, look up the table, and add accoring to table, 
		// if table is not given, use default 
		
	}
	
	private boolean isRuleRHSWordnetNonExactMatch(LexicalRule<? extends RuleInfo> rule, Lemma[] rhsTarget) throws AlignmentComponentException
	{
		if (!rule.getResourceName().contains("WORDNET"))
		{
			return false; 
		}
		
		// So, this is wordnet, check synset. 
		WordnetRuleInfo ruleInfo = (WordnetRuleInfo)rule.getInfo();
		String rhs = lemmaArrAsString(rhsTarget); 
		
		// Exact match? 
		try {
			if (ruleInfo.getRightSense().getWords().contains(rhs) )
			{
				// then, this is not non-exact match 
				return false; 
			}
		}
		catch (WordNetException we)
		{
			throw new AlignmentComponentException("Underlying Resource (wordnet) raised an exception" + we.getMessage(), we); 
		}
		
		// otherwise, non-exact match. 					
		return true; 
	}
	
	/**
	 * @param sequences
	 * @param target
	 * @return
	 */
	private List<Integer> findAllMatches(Lemma[] sequences, String[] ruleLeft)
	{
		ArrayList<Integer> result = new ArrayList<Integer>(); 
		
		for(int i=0; i < sequences.length; i++)
		{
			boolean passAtI = true; 
			for (int j=0; j < ruleLeft.length; j++)
			{
				if (! (ruleLeft[j].equals(sequences[i+j].getValue())))
				{
					passAtI = false; 
					break; 
				}
			}
			if (passAtI)
			{
				result.add(i); 
			}
		}
		
		return result; 
	}
	
	/**
	 * @param aView
	 * @return
	 */
	private String getLemmasAsStringSequence(JCas aView)
	{
		String result=""; 
		
		// get all Lemmas 
		Collection<Lemma> lemmas = JCasUtil.select(aView, Lemma.class); 
		
		for(Lemma cur : lemmas )
		{
			result += cur.getValue() + " "; 
		}
		return result; 
	}
	
	/**
	 * This method returns all possible consecutive sequences from 
	 * Lemmas of a "View", as possible phrase. Phrases also includes 
	 * single-len (word) case. Note that, if phraseMaxLen is 1, all 
	 * this method return would be length 1 phrases (words)  
	 * 
	 * @param aView aJCas view with lemmas 
	 * @return
	 * @throws AlignmentComponentException
	 */
	private List<Lemma[]> getAllPossibleCandidates(JCas aView) throws AlignmentComponentException
	{
		// sanity check 
		assert(aView != null); 		
		
		// the result will be stored here... 
		ArrayList<Lemma[]> result = new ArrayList<Lemma[]>(); 
		
		// Okay. 
		// Let's start with the all lemmas, in order, convert them to indexed array... 
		Collection<Lemma> clem = JCasUtil.select(aView, Lemma.class); 
		Lemma[] lems = clem.toArray(new Lemma[clem.size()]); 
		
		// and generate all candidates, upto phraseMaxLen
		for (int i=0; i < lems.length; i++)
		{
			for(int j=1; (j <= phraseMaxLen) && (i+j <= lems.length); j++ )
			{
				Lemma[] oneCand = Arrays.copyOfRange(lems, i, i+j); 
				result.add(oneCand); 
			}			
		}		
		return result; 
	}
	
	/**
	 * Makes Lemma array as Lemma-sequence (as one String) - whitespace as separator. 
	 * @param lem
	 * @return
	 */
	private String lemmaArrAsString(Lemma[] lem)
	{
		try {
			String result = lem[0].getValue(); 
		
			for(int j = 1; j < lem.length; j++) 
			{
				result += " " + lem[j].getValue(); 
			}

			return result; 
		}
		catch (ArrayIndexOutOfBoundsException ae)
		{
			logger.warn("OutofBOunds?!?!?"); 
			logger.warn("lem size:" + lem.length); 
			for (Lemma l : lem)
			{
				logger.info(l.getValue()); 
			}
			return lem[0].getValue(); 
		}
	}
	
	/**
	 * close() 
	 */
	public void close() throws AlignmentComponentException
	{
		try {
			underlyingResource.close(); 
		} 
		catch (LexicalResourceCloseException le)
		{
			throw new AlignmentComponentException("closing the underlying lexical resource failed: " + le.getMessage(), le); 
		}
		
	}
	
	
	@Override
	public String getComponentName()
	{
		return this.getClass().getName(); 
	}

	@Override
	public String getInstanceName()
	{
		return null; 
	}
	
	// private data
	private final LexicalResource<? extends RuleInfo> underlyingResource; 
	@SuppressWarnings("unused")
	private final Map<String,Set<GroupLabelInferenceLevel>> mapInfoToGroupLabelInference; 
	@SuppressWarnings("unused")
	private final Map<String,Set<GroupLabelDomainLevel>> mapInfoToGroupLabelDomain; 

	@SuppressWarnings("unused")
	private final Boolean supportPhrases; 
	private final int phraseMaxLen; 
	@SuppressWarnings("unused")
	private final Set<GroupLabelInferenceLevel> defaultGroupLabel; 
	
	private final static Logger logger = Logger.getLogger(LexicalAlignerFromLexicalResource.class);
}
