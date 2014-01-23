package eu.excitementproject.eop.distsim.dependencypath;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.codeannotations.StandardSpecific;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * <p>Several utils related to converting general parse trees to strings of various types: Tree-like, natural-sentences
 *
 * @author Erel Segal Halevi
 * @since 2012-01-03
 */
public class AbstractNodeStringUtils {

	//AS We already have toString() method on DefaultInfo. Not sure this method is
	// necessary. Anyway, if we decide to integrate this project into
	// infrastructure - this method shouldn't be there (but it can be somewhere
	// in your private projects, if you need it).
	/**
	 * @return a simple string representing the given info (serial, word, lemma, POS, dependency).
	 * Based on {@link eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils#stringOfNode}
	 */
	public static String toString(Info info) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if (info == null){ sb.append("null"); }
		else{
			NodeInfo nodeInfo = info.getNodeInfo();
			if (nodeInfo == null){ sb.append(", "); }
			else{
				sb.append(info.getId()).append(", ");  // info.getId() may be different than nodeInfo.getSerial() in case of variable substitutions!  
				sb.append(InfoGetFields.getWord(info)).append(", ");
				sb.append(InfoGetFields.getLemma(info)).append(", ");
				sb.append(InfoGetFields.getPartOfSpeech(info)).append(", ");
				sb.append(InfoGetFields.getNamedEntityAnnotation(info)).append(", ");
			}
			sb.append(InfoGetFields.getRelation(info));
		}
		sb.append("]");
		return sb.toString();
	}



	//AS I assume this method is also unnecessary for other utilites,
	// except testings and debuggings. Correct me if I'm wrong. If I'm not wrong - then
	// could you please remember to remove it, if the project is integrated into
	// infrastructure?
	/**
	 * @param parseTree any type of parse-tree.
	 * @param prefix string to print before the current node (for indentation).
	 * @param infoToStringConverter used for printing the info of each node.
	 * @return a multi-line indented presentation of the given parse tree. Subroutine of {@link #toIndentedString(AbstractNode)}
	 */
	protected static <T extends Info,S extends AbstractNode<T,S>> String toIndentedString(S parseTree, String prefix, Transformer<T, String> infoToStringConverter) {
		if (parseTree==null)
			return "null";
		StringBuffer sb = new StringBuffer()
		.append(prefix)
		.append(infoToStringConverter.transform(parseTree.getInfo()))
		.append("\r\n");
		if (parseTree.getChildren()!=null)
			for (S child: parseTree.getChildren())
				sb.append(toIndentedString(child, prefix+" ", infoToStringConverter));
		return sb.toString();
	}

	//AS same as above.
	/**
	 * @return a multi-line indented presentation of the given parse tree. Allows customization of the string representation of nodes.
	 * @note {@link eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils#stringOfNode} returns a single-line string.
	 */
	public static <T extends Info,S extends AbstractNode<T,S>> String toIndentedString(S parseTree, Transformer<T, String> infoToStringConverter) {
		return toIndentedString(parseTree, "", infoToStringConverter).trim();
	}

	
	//AS same as above.
	/**
	 * @return a multi-line indented presentation of the given parse tree, with default representation of nodes.
	 * @note {@link eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils#stringOfNode} returns a single-line string.
	 */
	public static <T extends Info,S extends AbstractNode<T,S>> String toIndentedString(S parseTree) {
		return toIndentedString(parseTree, new Transformer<T, String>() {
			@Override
			public String transform(T input) {
				return AbstractNodeStringUtils.toString(input);
			}});
	}

	

	//AS Is it language-dependent? If it is - the class and the method should be
	// annotated with @LanguageDependent.
	//
	// Anyway, I'm not sure we can adopt the heuristics here.
	// Please remember that in dependency parse trees there is not meaning to the
	// node ordering. So, this is a kind of a heuristics, and it seems that it cannot
	// be part of infrastructure.
	/**
	 * @return an English sentence that is approximately equivalent to the given parse tree.
	 * @note Currently handles only subject/object issues.
	 */
	@StandardSpecific(value = { "Stanford" })
	public static <T extends Info,S extends AbstractNode<T,S>> String toEnglishSentence(S parseTree) {
		if (parseTree==null) 
			throw new NullPointerException("parseTree is null");
		List<String> words = new ArrayList<String> ();
		addWordsOfEnglishSentence(parseTree, words);
		String sentence = StringUtil.join(words, " ");
		sentence = sentence.toLowerCase();
		sentence = sentence.replaceAll(" ([,.';:\\)])", "$1");
		return sentence;
	}

	//AS Seems like standard-specific on Stanford dependencies.
	// Should be annotated with @StandardSpecific. Not sure, however, that this
	// method will be part of infrastructure.
	/**
	 * Recursively add the words of the given parseTree to the given words list, in the correct grammatical order. Subroutine of {@link #toEnglishSentence(AbstractNode)}.
	 * @param parseTree [INPUT]
	 * @param words [OUTPUT]
	 */
	@StandardSpecific(value = { "Stanford" })
	protected static <T extends Info,S extends AbstractNode<T,S>> void addWordsOfEnglishSentence(AbstractNode<T,S> parseTree, List<String> words) {
		if (parseTree==null) 
			throw new NullPointerException("parseTree is null");
		if (parseTree.getChildren()==null) {
			words.add(InfoGetFields.getWord(parseTree.getInfo()));
		} else {

			// add subject nodes before the verb:
			for (S child: parseTree.getChildren()) {
				if (relationTypesBeforeHead.contains(InfoGetFields.getRelation(child.getInfo())))
					addWordsOfEnglishSentence(child, words);
			}
			words.add(InfoGetFields.getWord(parseTree.getInfo())); // add the root word

			// add non-subject nodes after the verb:
			for (S child: parseTree.getChildren()) {
				if (relationTypesBeforeHead.contains(InfoGetFields.getRelation(child.getInfo())))
					continue;
				if (relationTypesToIgnore.contains(InfoGetFields.getRelation(child.getInfo())))
					continue;
				addWordsOfEnglishSentence(child, words);
			}
		}
	}

	//AS @StandardSpecific - should annotate the class and this field.
	// However, it seems that we cannot adopt it for infrastructure.
	@StandardSpecific(value = { "Stanford" })
	protected static final Set<String> relationTypesBeforeHead = new HashSet<String>(Arrays.asList(
			"num", "subj", "nsubj", "nsubjpass", "csubj", "csubjpass", "nn", "det", "amod", "advmod", "poss", "aux", "auxpass", "mark", "cop"));

	//AS @StandardSpecific - should annotate the class and this field.
	// However, it seems that we cannot adopt it for infrastructure.
	@StandardSpecific(value = { "Stanford" })
	protected static final Set<String> relationTypesToIgnore = new HashSet<String>(Arrays.asList(
			"xsubj"));

	/**
	 * @return A string id that, hopefully, uniquely identifies a certain subtree. This is useful, for example, for differentiating between assignments to variables, etc.
	 * @note id is not enough, because ids are not guaranteed to be unique. 
	 */
	public static <T extends Info,S extends AbstractNode<T,S>> String uniqueSubtreeId(S parseTree) {
		return parseTree==null? null: parseTree.getInfo().getId()+" "+toEnglishSentence(parseTree);
	}

	/**
	 * @return A string id that, hopefully, uniquely identifies a certain subtree. This is useful, for example, for differentiating between assignments to variables, etc.
	 * @note id is not enough, because ids are not guaranteed to be unique. 
	 */
	public static <T extends Info,S extends AbstractNode<T,S>> String uniqueTreesId(List<S> parseTrees) {
		if (parseTrees==null) return null;
		List<String> ids = new ArrayList<String>();
		for (S node: parseTrees)
			ids.add(AbstractNodeStringUtils.uniqueSubtreeId(node));
		return StringUtil.join(ids, ". ");
	}


	//AS General comments - please keep all the public components together,
	// and all private and protected together.


}
