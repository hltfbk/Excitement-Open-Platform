/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.conll;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.util.Map;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.WildcardPartOfSpeech;
import eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules.conll.RuleConllStringConverter;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * Implementation of {@link RuleConllStringConverter} that converts {@link ExtendedNode}s into CoNLL nodes with annotations
 * @author Amnon Lotan
 *
 * @since Jul 14, 2012
 */
public class AnnotatedConllStringConverter implements TreeConllStringConverter<ExtendedInfo, ExtendedNode> {

	/**
	 * The ordinal of the CT annotation in the tab sequence
	 */
	public static final int CT_TAB_NUMBER = 12;
	/**
	 * The ordinal of the PT annotation in the tab sequence
	 */
	public static final int PT_TAB_NUMBER = 13;
	/**
	 * The ordinal of the word annotation in the tab sequence
	 */
	public static final int WORD_TAB_NUMBER = 1;

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.generic.rule_compiler.entailment.conll.ConllStringConvertor#convert(ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode, java.util.Map)
	 */
	@Override
	public String convert(ExtendedNode node, Map<ExtendedNode, Integer> mapNodeToId, ExtendedNode antecedent) throws ConllConverterException {
		if (node == null)
			throw new ConllConverterException("null node");
		if (mapNodeToId == null)
			throw new ConllConverterException("null mapNodeToId");
		if (!mapNodeToId.containsKey(node))
			throw new ConllConverterException("the mapNodeToId doesn't contain a mapping for the given node: " + node);
		if (!mapNodeToId.containsKey(node.getAntecedent()))
			throw new ConllConverterException("the mapNodeToId doesn't contain a mapping for the given node's parent: " + node.getAntecedent());
		
		ExtendedInfo info = node.getInfo();
		int myId = mapNodeToId.get(node);
		String word = ExtendedInfoGetFields.getWord(info, UNDERSCORE).trim();
		if (word.isEmpty())
			word = UNDERSCORE;
		String lemma = ExtendedInfoGetFields.getLemma(info, UNDERSCORE).trim();
		if (lemma.isEmpty())
			lemma = UNDERSCORE;
		PartOfSpeech pos = ExtendedInfoGetFields.getPartOfSpeechObject(info);
		String canonicalPos = WildcardPartOfSpeech.isWildCardPOS(pos) ? pos.getStringRepresentation() :
				pos != null ? simplerPos(pos.getCanonicalPosTag()).name() : UNDERSCORE;
		int antecedentId = mapNodeToId.get(antecedent);
		String relation = antecedentId == ROOT_ID ? ROOT : ExtendedInfoGetFields.getRelation(info, ROOT);
		String signature = ExtendedInfoGetFields.getPredicateSignature(info, UNDERSCORE);
		String nu = ExtendedInfoGetFields.getNegationAndUncertainty(info, UNDERSCORE);
		String ct = ExtendedInfoGetFields.getClauseTruth(info, UNDERSCORE);
		String pt = ExtendedInfoGetFields.getPredTruth(info, UNDERSCORE);
		
		
		StringBuilder line = new StringBuilder();
		line.append(myId).append(TAB);
		line.append(word).append(TAB);
		line.append(lemma).append(TAB);
		line.append(canonicalPos).append(TAB);
		line.append(pos).append(TAB);
		line.append(UNDERSCORE).append(TAB);
		line.append(antecedentId).append(TAB);
		line.append(relation).append(TAB);
		line.append(UNDERSCORE).append(TAB);
		line.append(UNDERSCORE).append(TAB);
		// annotations:
		line.append(signature).append(TAB);
		line.append(nu).append(TAB);
		line.append(ct).append(TAB);
		line.append(pt);
		
		return line.toString();
	}
}
