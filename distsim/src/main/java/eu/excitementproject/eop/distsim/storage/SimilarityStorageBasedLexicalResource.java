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
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
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
	
	public SimilarityStorageBasedLexicalResource(SimilarityStorage similarityStorage) {
		this.similarityStorage = similarityStorage;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRulesForLeft(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends RuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRulesForLeft(lemma,pos, RuleDirection.LEFT_TO_RIGHT);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRulesForRight(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends RuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRulesForLeft(lemma,pos, RuleDirection.RIGHT_TO_LEFT);
	}
	
	protected List<LexicalRule<? extends RuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos, RuleDirection ruleDirection) throws LexicalResourceException {
		try {
			LemmaPosBasedElement element1 = new LemmaPosBasedElement(new LemmaPos(lemma, (pos == null ? null : pos.getCanonicalPosTag())));
			List<LexicalRule<? extends RuleInfo>> ret = new LinkedList<LexicalRule<? extends RuleInfo>>();
			for (ElementsSimilarityMeasure elemenstSimilarityMeasure : similarityStorage.getSimilarityMeasure(element1.toKey(), ruleDirection)) {
				LemmaPosBasedElement left = (LemmaPosBasedElement)elemenstSimilarityMeasure.getLeftElement();
				LemmaPosBasedElement right = (LemmaPosBasedElement)elemenstSimilarityMeasure.getRightElement();
				LexicalRule<RuleInfo> rule = 
					new LexicalRule<RuleInfo>(
							left.getData().getLemma(), new ByCanonicalPartOfSpeech(left.getData().getPOS().name()),
							right.getData().getLemma(), new ByCanonicalPartOfSpeech(right.getData().getPOS().name()),
							elemenstSimilarityMeasure.getSimilarityMeasure(), 
							null, similarityStorage.getResourceName(), null);
				ret.add(rule);
			} 
			return ret;
		} catch (Exception e) {
			throw new LexicalResourceException(e.toString());
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
			for (ElementsSimilarityMeasure similarityRule : similarityStorage.getSimilarityMeasure(leftElement.toKey(), rightElement.toKey())) {
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

}
