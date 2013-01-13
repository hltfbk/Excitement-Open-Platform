/**
 * 
 */
package ac.biu.nlp.nlp.engineml.generic.rule_compiler.entailmentrules.conll;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.util.Map;

import ac.biu.nlp.nlp.engineml.generic.truthteller.conll.TreeConllStringConverter;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.WildcardPartOfSpeech;


/**
 * @author Amnon Lotan
 *
 * @since Jul 14, 2012
 */
public class BasicConllStringConvertor implements RuleConllStringConverter<Info, BasicNode> {

	private static final String UNDERSCORE = TreeConllStringConverter.UNDERSCORE;
	private static final char TAB = TreeConllStringConverter.TAB;

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.generic.rule_compiler.entailment.conll.ConllStringConvertor#convert(ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode, java.util.Map)
	 */
	@Override
	public String convert(BasicNode node, Map<BasicNode, Integer> mapNodeToId, int variableId) throws ConllConverterException {
		if (node == null)
			throw new ConllConverterException("null node");
		if (mapNodeToId == null)
			throw new ConllConverterException("null mapNodeToId");
		if (!mapNodeToId.containsKey(node))
			throw new ConllConverterException("the mapNodeToId doesn't contain a mapping for the given node: " + node);
		if (!mapNodeToId.containsKey(node.getAntecedent()))
			throw new ConllConverterException("the mapNodeToId doesn't contain a mapping for the given node's parent: " + node.getAntecedent());
		
		Info info = node.getInfo();
		int myId = mapNodeToId.get(node);
		String word = InfoGetFields.getWord(info, UNDERSCORE).trim();
		if (word.isEmpty())
			word = UNDERSCORE;
		String lemma = InfoGetFields.getLemma(info, UNDERSCORE).trim();
		if (lemma.isEmpty())
			lemma = UNDERSCORE;
		PartOfSpeech pos = InfoGetFields.getPartOfSpeechObject(info);
		String canonicalPos = WildcardPartOfSpeech.isWildCardPOS(pos) ? pos.getStringRepresentation() :
				pos != null ? simplerPos(pos.getCanonicalPosTag()).name() : UNDERSCORE;
		int antecedentId = mapNodeToId.get(node.getAntecedent());
		String relation = antecedentId == TreeConllStringConverter.ROOT_ID ? TreeConllStringConverter.ROOT : 
			InfoGetFields.getRelation(info, TreeConllStringConverter.ROOT);
		
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
		line.append(variableId > 0 ? variableId : UNDERSCORE).append(TAB);
		
		return line.toString();
	}
}
