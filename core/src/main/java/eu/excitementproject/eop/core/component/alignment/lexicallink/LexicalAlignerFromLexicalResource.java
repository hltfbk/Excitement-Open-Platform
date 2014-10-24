package eu.excitementproject.eop.core.component.alignment.lexicallink;

import java.util.Map;
import java.util.Set;

import org.apache.uima.jcas.JCas;

import eu.excitement.type.alignment.GroupLabelDomainLevel;
import eu.excitement.type.alignment.GroupLabelInferenceLevel;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;

/**
 * TODO full document
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
	 * Full Constructor for the class: use other convenient constructors, if you don't require of the fields. 
	 * 
	 * @param res 	the underlying LexicalResource. Cannot be null.  
	 * @param supportPhrase  The underlying resource supports phrases (multi-word expression)? 
	 * @param maxPhraseLen (checked only when supportPhrase is true) what is the length of maximum phrase in the underlying resource? (the aligner will lookup only to that length) 
	 * @param groupLabelMapI map -  which will let us know how resource specific "info" string would be mapped into canonical enum value that groups alignment.Link. This is for inference level map. (alignment, contradictory, etc --- generic ). The value is optional, and can be null. if null, the aligner won't add canonical relation of inference level group label).  
	 * @param groupLabelMapD map -  which will let us know how resource specific "info" string would be mapped into canonical enum value that groups alignment.Link. This is for inference level map. (alignment, contradictory, etc --- generic ). The value is optional, and can be null. if null, the aligner won't add canonical relation of domain level group label) 
 	 */
	public LexicalAlignerFromLexicalResource(LexicalResource<? extends RuleInfo> res, Boolean supportPhrase, int maxPhraseLen, Map<String,Set<GroupLabelInferenceLevel>> groupLabelMapI, Map<String,Set<GroupLabelDomainLevel>> groupLabelMapD) throws AlignmentComponentException
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
		// setting phrase flag 
		// (multiple lemmas forming one entry in the underlying resource or not 
		// if supported, set the length of maximum phrase, too) 
		if (supportPhrase && maxPhraseLen > 1)
		{
			this.supportPhraseLookup = true; 
			this.phraseMaxLen = maxPhraseLen; 
		}
		else
		{
			this.supportPhraseLookup = false; 
			this.phraseMaxLen = 0; 
		}			
	}

	@Override
	public void annotate(JCas aJCas) throws AlignmentComponentException {
		
		// TODO: start coding on this outline. 
		// 
		
		// do we accept phrases (the entry can be more than one lemma --- two or more lemmas)
		// do both. If no phrases, just first part. 
		
		// single-lemma route 
		// get H side lemmas as all possible candidates 
		// ready T side lemma as a Lemma list. 
		// 
		//   for all candidates 
		//     query getRulesForRight(lemma) 
		//     check each T side lemma list for applicable places
		//	     if found: add link. 
		//   (WE do check POS and Lemma for single lemma cases) 
		
		// phrase-checking route 
		// prepare T side lemma sequences as one string, so we can do quick look up of 
		// applicable or not. 
		// 
		// get H side lemma sequences as all possible phrase candidates 
		// for each candidates 
		//    query getRulesForRight(lemma-seq as one string)
		//    for the returned rules, string search on T-side lemma sequence  
		//       if match is there; 
		//       do real search on T side Lemma List, and link them.  
		
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
	private final Map<String,Set<GroupLabelInferenceLevel>> mapInfoToGroupLabelInference; 
	private final Map<String,Set<GroupLabelDomainLevel>> mapInfoToGroupLabelDomain; 

	private final Boolean supportPhraseLookup; 
	private final int phraseMaxLen; 

}
