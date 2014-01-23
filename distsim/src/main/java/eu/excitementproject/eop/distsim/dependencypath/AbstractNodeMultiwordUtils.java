package eu.excitementproject.eop.distsim.dependencypath;

import java.util.Arrays;

import java.util.HashSet;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * <p>Utilities related to parse trees representing multiword expressions. 
 *
 * @author Erel Segal Halevi
 * @since 2012-12-19
 */
public class AbstractNodeMultiwordUtils {
	/**
	 * @param multiwordExpression e.g. "Team Manager" (split by a whitespace).
	 * @return true if the given parse tree may be the result of parsing the expression (i.e. contains exactly these words, in arbitrary order). 
	 */
	//AS consider ignoring case of letters.
	//AS Note that a parse tree for "the Team Manager" will not match here. How do
	// you handle this?
	public static <T extends Info, S extends AbstractNode<T,S>>  boolean parseTreeMatchesMultiwordExpression(S parseTree, String multiwordExpression) {
		HashSet<String> wordsInTree = setOfWordsAndLemmas(parseTree, true, true);
		HashSet<String> wordsInExpression = new HashSet<String>(Arrays.asList(multiwordExpression.split("\\s+")));
		return wordsInTree.equals(wordsInExpression);
	}

	/**
	 * @return the set of words and/or lemmas in the given parse tree (including the root and all descendants)
	 */
	public static <T extends Info, S extends AbstractNode<T,S>>  HashSet<String> setOfWordsAndLemmas(S parseTree, boolean withWords, boolean withLemmas) {
		return AbstractNodeMultiwordUtils.addWordsAndLemmasToSet(parseTree, new HashSet<String>(), withWords, withLemmas);
	}


	/**
	 * Add all words and lemmas of the given tree to the given set.
	 * @return the same set.
	 */
	protected static <T extends Info, S extends AbstractNode<T,S>> HashSet<String> addWordsAndLemmasToSet(AbstractNode<T,S> parseTree, HashSet<String> theSet, boolean withWords, boolean withLemmas) {
		if (withWords) theSet.add(InfoGetFields.getWord(parseTree.getInfo()));
		if (withLemmas) theSet.add(InfoGetFields.getLemma(parseTree.getInfo()));
		if (parseTree.getChildren()!=null)
			for (AbstractNode<T,S> child: parseTree.getChildren())
				addWordsAndLemmasToSet(child, theSet, withWords, withLemmas);
		return theSet;
	}
}
