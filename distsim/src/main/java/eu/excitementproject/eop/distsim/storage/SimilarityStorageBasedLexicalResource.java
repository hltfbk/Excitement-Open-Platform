/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.domains.FilterType;
import eu.excitementproject.eop.distsim.domains.RuleDirection;
import eu.excitementproject.eop.distsim.items.LemmaPos;
import eu.excitementproject.eop.distsim.items.LemmaPosBasedElement;
import eu.excitementproject.eop.distsim.scoring.ElementsSimilarityMeasure;

/**
 * An implementation of the LexicalResources interface, based on a given SimilarityStorage.
 *  
 * 
 * @author Meni Adler
 * @since 31/12/2012
 *
 */
public class SimilarityStorageBasedLexicalResource implements LexicalResource<RuleInfo> {
	
	/**
	 * Construct a lexical resource from an existing, initialized similarity storage, without a rule-count limit.
	 */
	public SimilarityStorageBasedLexicalResource(SimilarityStorage similarityStorage) {
		this(similarityStorage,null);
	}


	
	/**
	 * Construct a lexical resource from configuration params, by constructing a new similarity storage from these params.
	 * <p>Additionally, uses the param "limit on retrieved rules" to limit the number of retrieved rules.
	 * @see DefaultSimilarityStorage#DefaultSimilarityStorage(ConfigurationParams)
	 */
	public SimilarityStorageBasedLexicalResource(ConfigurationParams params) throws ConfigurationException {
		this(
				new DefaultSimilarityStorage(params),
				params.getInt("limit on retrieved rules"));
	}
	
	public SimilarityStorageBasedLexicalResource(SimilarityStorage similarityStorage, Integer maxNumOfRetrievedRules) {
		this.similarityStorage = similarityStorage;
		this.maxNumOfRetrievedRules = maxNumOfRetrievedRules;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRulesForLeft(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends RuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRulesForSide(lemma,pos, RuleDirection.LEFT_TO_RIGHT);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRulesForRight(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends RuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		//long t1 = System.currentTimeMillis();
		List<LexicalRule<? extends RuleInfo>> ret = getRulesForSide(lemma,pos, RuleDirection.RIGHT_TO_LEFT);
		//long t2 = System.currentTimeMillis();
		//System.out.println("getRulesForRight time: " + (t2-t1) + " ms");
		return ret;
	}
	
	protected List<LexicalRule<? extends RuleInfo>> getRulesForSide(String lemma, PartOfSpeech pos, RuleDirection ruleDirection) throws LexicalResourceException {
		try {
			LemmaPosBasedElement element1 = new LemmaPosBasedElement(new LemmaPos(lemma, (pos == null ? null : pos.getCanonicalPosTag())));
			List<LexicalRule<? extends RuleInfo>> ret = new LinkedList<LexicalRule<? extends RuleInfo>>();
			
			List<ElementsSimilarityMeasure> rules = (maxNumOfRetrievedRules==null? 
					similarityStorage.getSimilarityMeasure(element1, ruleDirection):
					similarityStorage.getSimilarityMeasure(element1, ruleDirection, FilterType.TOP_N, maxNumOfRetrievedRules));
			
			//long t1 = System.currentTimeMillis();

			for (ElementsSimilarityMeasure elemenstSimilarityMeasure : rules
					/*similarityStorage.getSimilarityMeasure(element1.toKey(), ruleDirection)*/) {
				LemmaPosBasedElement left = (LemmaPosBasedElement)elemenstSimilarityMeasure.getLeftElement();
				LemmaPosBasedElement right = (LemmaPosBasedElement)elemenstSimilarityMeasure.getRightElement();
				LexicalRule<RuleInfo> rule = 
					new LexicalRule<RuleInfo>(
							left.getData().getLemma(), new ByCanonicalPartOfSpeech(left.getData().getPOS().name()),
							right.getData().getLemma(), new ByCanonicalPartOfSpeech(right.getData().getPOS().name()),
							elemenstSimilarityMeasure.getSimilarityMeasure(), 
							null, similarityStorage.getResourceName(), DistSimRuleInfo.getInstance());
				ret.add(rule);
			}
			
			//long t2 = System.currentTimeMillis();
			//System.out.println("XXX: " + (t2-t1));

			return ret;
		} catch (Exception e) {
			throw new LexicalResourceException(e.toString(),e);
			//throw new LexicalResourceException(e.toString());
		}
	}
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRules(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech, java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends RuleInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException {
		try {
			List<LexicalRule<? extends RuleInfo>> ret = new LinkedList<LexicalRule<? extends RuleInfo>>();
			LemmaPosBasedElement leftElement = new LemmaPosBasedElement(new LemmaPos(leftLemma, (leftPos == null ? null : leftPos.getCanonicalPosTag())));
			LemmaPosBasedElement rightElement = new LemmaPosBasedElement(new LemmaPos(rightLemma, (rightPos == null ? null : rightPos.getCanonicalPosTag())));
			for (ElementsSimilarityMeasure similarityRule : similarityStorage.getSimilarityMeasure(leftElement, rightElement)) {
				LemmaPosBasedElement left = (LemmaPosBasedElement)similarityRule.getLeftElement();
				LemmaPosBasedElement right = (LemmaPosBasedElement)similarityRule.getRightElement();
				ret.add(new LexicalRule<RuleInfo>(left.getData().getLemma(), new ByCanonicalPartOfSpeech(left.getData().getPOS().name()), right.getData().getLemma(), new ByCanonicalPartOfSpeech(right.getData().getPOS().name()), similarityRule.getSimilarityMeasure(), null, similarityStorage.getResourceName(), null));
			}
			return ret;
		} catch (Exception e) {
			throw new LexicalResourceException(ExceptionUtil.getStackTrace(e));
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#close()
	 */
	@Override
	public void close() throws LexicalResourceCloseException {
	}

	SimilarityStorage similarityStorage;
	Integer maxNumOfRetrievedRules;

}
