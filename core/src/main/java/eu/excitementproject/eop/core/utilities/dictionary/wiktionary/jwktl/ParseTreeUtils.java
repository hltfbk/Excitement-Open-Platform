/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.StanfordDependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.StanfordDependencyRelation.StanfordDepedencyRelationType;
import eu.excitementproject.eop.common.representation.parse.representation.basic.StanfordDependencyRelation.StanfordDependencyException;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.utilities.AllChoices;
import eu.excitementproject.eop.common.utilities.AllChoices.AllChoicesException;
import eu.excitementproject.eop.common.utilities.ChoiceHandler;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryRelation;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;

/**
 * A handy set of utilities that scan an {@link AbstractNode}'s subtree, parsed by {@link EasyFirstParser}, for certain dominated words. 
 * Insofar it's mainly used by {@link WktGlossParser}, on order to parse out hypernyms in {@link JwktlSense#getRelatedWords(WiktionaryRelation)}. 
 * @author Amnon Lotan
 * @since 26/06/2011
 * 
 */
public class ParseTreeUtils {

	private static final String BE = "be";
	private static final Set<String> NEG_WORDS = 
		Utils.arrayToCollection(new String[]{"not","neither", "nor", "no", "n't", "never", "nowhere"}, new HashSet<String>());

	public static class ParseTreeException extends Exception {

		private static final long serialVersionUID = 1411176851578284117L;
		/**
		 * Ctor
		 * @param message
		 */
		public ParseTreeException(String message) {
			super(message);
		}
		/**
		 * Ctor
		 * @param message
		 * @param cause
		 */
		public ParseTreeException(String message, Throwable cause) {
			super(message, cause);
		}
	}
	
	/**
	 * recursively get the words of all the modifiers (amod) of the node, inc. its conjuncts (conj) and appositions (appos). Preserves the
	 * original order of the words in the sentence. 
	 * @param <T>
	 * @param <S>
	 * @param node
	 * @param nonVerbDependancyRelations 
	 * @param selectOffspring 
	 * @return
	 * @throws ParseTreeException 
	 */
	public static <T extends Info,S extends AbstractNode<T,S>> List<String> getEntailedModifiersOf(S node) throws ParseTreeException {
		if (!isNegated(node))
			return getEntailedModifiersOfNonNegatedNode(node);
		// what about negated modifiers??
		
		return new Vector<String>();		
	}

	/**
	 * return true iff the node is negated
	 * @param node
	 * @return
	 */
	public static <T extends Info, S extends AbstractNode<T,S>> boolean isNegated(S node) {
		if (node.getChildren() != null)
			for( S child : node.getChildren())
				if (NEG_WORDS.contains(child.getInfo().getNodeInfo().getWordLemma()))
					return true;
		return false;
	}	

	////////////////////////////////////////////////// PARIVATE	////////////////////////////////////////////////////////////////////
	
	private static <T extends Info,S extends AbstractNode<T,S>> List<String> getEntailedModifiersOfNonNegatedNode(S node) 
		throws ParseTreeException 
	{
		if (node.getChildren() != null)
			return getEntailedModifiersOfNonNegatedParent(node);	// recurse
		//	else
		{	// this is a leaf, return only the leaf's word
			List<String> selectOffspring = new Vector<String>();
			String parentWord = node.getInfo().getNodeInfo().getWord();
			selectOffspring.add(parentWord);	
			return selectOffspring;
		}
	}
	
	private static <T extends Info,S extends AbstractNode<T,S>> List<String> getEntailedModifiersOfNonNegatedParent(S node) 
		throws ParseTreeException 
	{
		LinkedList<String> modifiers = new LinkedList<String>();	// use Stack to preserve the word order between the modifiers and their parent
		List<List<String>> argumentLists = new Vector<List<String>>();	// holds the (hypernym sets of the) arguments, in order
		boolean argumentsAreOptional = true;
		StringBuffer mandatoryAdverbsBuf = new StringBuffer();
		List<String> parentTerms = new LinkedList<String>();
		if (!isBE(node))					// 'to be' can't be a hypernym
		{
			String parentWord = node.getInfo().getNodeInfo().getWord();
			parentTerms.add(parentWord);	// add the node's word to the list
		}
		for( S child : node.getChildren())	
		{
			StanfordDepedencyRelationType relationToChild;
			try {	relationToChild = new StanfordDependencyRelation(InfoGetFields.getRelation(child.getInfo())).stanfordDependancyRelationTag;	}
			catch (StanfordDependencyException e) {	throw new ParseTreeException("Somehow the parser used '"+InfoGetFields.getRelation(child.getInfo())+"' which isn't a StanfordDependencyRelation");	}
			switch(relationToChild)
			{
			case advmod: case auxpass:		// relations that define modifiers that are not entailed by their parent, but should prefix it
				// if the child is an adverb, replace the 'node word' with 'adverb' + ' node word', because many adverbs are DOWN
				mandatoryAdverbsBuf.append(InfoGetFields.getWord(child.getInfo()) + " ");
				break;
			case appos:	case conj:	case xcomp:
				// TODO be sensitive to DOWN monotonic modifiers? �chips are fake money� -->? �chips are money� -->? �chips are fake�   ?
				// TODO problematic assumption here, that all the args and modifiers are attached directly to the parent 'node' and not to any of these
				// conjuncts and appositions can replace the headWord 
				parentTerms.addAll(getEntailedModifiersOf(child));
				break;
			case rcmod:		//relations that define quasi entailment from parent to child, so long as the child isn't a verb
				if(	!InfoGetFields.getPartOfSpeechObject(child.getInfo()).getCanonicalPosTag().equals(SimplerCanonicalPosTag.VERB)	)
					modifiers.addAll(	getEntailedModifiersOf(child));	
				break;
			case amod:	case nn:
				// add NP modifiers, all subsets thereof, concatenated with the modified node
				modifiers.addAll(getEntailedModifiersOf(child));
				break;
			case pobj: case pcomp:		// pcomp and pobj are the only children of a prep, and the parent cannot appear w/o the child
				argumentsAreOptional = false;			// prevent the parent prep from being returned without its pobj/pcomp arg
				argumentLists.add(getEntailedModifiersOf(child));
				break;
			case dobj:	case prep:
				if (isBE(node))	// the parent is a BE-verb, so assure it isn't returned, i.e. Its args will be returned without the BE.  //assume its dobj (the copular predicate) is the main hypernym
				{	// instead of returning parentWord, return an empty string
					parentTerms.clear();
					parentTerms.add("");	
				}
				argumentLists.add(getEntailedModifiersOf(child));
				break;
			default:
				break;
			}
		}

		// combining all the components into phrases		
		return combineSubtrees(mandatoryAdverbsBuf.toString(), modifiers, parentTerms, argumentLists, argumentsAreOptional);
	}
	
	/**
	 * combining all the components into phrases
	 * @param mandatoryAdverbs
	 * @param modifiers
	 * @param parentTerms
	 * @param argumentLists
	 * @param argumentsAreOptional
	 * @return
	 * @throws ParseTreeException 
	 */
	private static List<String> combineSubtrees(String mandatoryAdverbs, LinkedList<String> modifiers, List<String> parentTerms, 
			List<List<String>> argumentLists, boolean argumentsAreOptional) throws ParseTreeException 
	{
		List<String> entailedPhrases = new Vector<String>();
		List<String> argumentsCombinations = getAllChoicesOverSets(argumentLists);
		if (argumentsAreOptional)
			argumentsCombinations.add("");		// the empty combination allow the parent to be returned with no arguments
		for (String parentTerm : parentTerms)
		{
			// append the parent term to the right (last element) of its NP modifiers, and all subsets thereof
			modifiers.offer(parentTerm);
			for (String headAndModifiersCombination : getSubsetsAsStrings(modifiers))		// TODO combine this with a call to getAllChoicesOverSets
				for (String argumentsCombination : argumentsCombinations)
				{
					String entailedPhrase = (mandatoryAdverbs + headAndModifiersCombination + ' ' + argumentsCombination).trim();
					if (!(entailedPhrase.length() == 0))							// don't record empty combinations
						entailedPhrases.add(entailedPhrase);
				}
			modifiers.removeLast();	// pop the parentTerm
		}
		return entailedPhrases;
	}

	/**
	 * Return all the choices of strings over the given sets
	 * @param setsOfStrings
	 * @return
	 * @throws ParseTreeException 
	 */
	private static List<String> getAllChoicesOverSets(List<List<String>> setsOfStrings) throws ParseTreeException {
		@SuppressWarnings("unchecked")
		Iterable<String>[] iterablesArray = new Iterable[setsOfStrings.size()];
		int i = 0;
		for (List<String> stringSet : setsOfStrings)
			iterablesArray[i++] = stringSet;
		
		final List<String> allChoices = new Vector<String>();		
		ChoiceHandler<String> handler = new ChoiceHandler<String>()
		{
			public void handleChoice(List<String> choice)
			{
				StringBuffer buf = new StringBuffer();
				for (String s : choice)
				{
					if (s.length() > 0)
						buf.append(s + ' ');
				}
				allChoices.add(buf.toString().trim());
			}
		};
		AllChoices<String> ac;
		try {	ac = new AllChoices<String>(iterablesArray, handler);	}
		catch (AllChoicesException e) {	throw new ParseTreeException("Error calculating all choices over these sets: " + setsOfStrings, e);	}
		ac.run();
		
		return allChoices;
	}

	/**
	 * Return the given words in all combinations, where the inner oder in which they are given is preserved
	 * @param modifiers
	 * @return
	 */
	private static List<String> getSubsetsAsStrings(List<String> modifiers) {
		List<String> subsetsAsStrings = new Vector<String>();
		ArrayList<ArrayList<String>> subsets = getSubsets(modifiers);
		// print out the subsets
		for (ArrayList<String> subset : subsets)
		{
			StringBuffer subsetAsString = new StringBuffer();
			for (String word : subset)
				subsetAsString.append(word + ' ');
			if (subsetAsString.length() > 0)
			{
				subsetAsString.deleteCharAt(subsetAsString.length()-1);		// loose the last space char
				subsetsAsStrings.add(subsetAsString.toString());
			}
		}
		return subsetsAsStrings;
	}

	private static <T extends Info,S extends AbstractNode<T,S>> boolean isBE(S node)
	{
		return InfoGetFields.getLemma(node.getInfo()).equals(BE);
	}
	
	/**
	 * return all the subsets of the set
	 * @param <O> some Object
	 * @param modifiers
	 * @return
	 * @see http://911programming.wordpress.com/2010/06/07/java-extracting-all-subsets-of-a-set-using-arraylist/
	 */
	private static <O> ArrayList<ArrayList<O>> getSubsets( List<O> modifiers) {

		ArrayList<ArrayList<O>> subsetCollection = new ArrayList<ArrayList<O>>();

		if (modifiers.size() == 0) {
			subsetCollection.add(new ArrayList<O>());
		} else {
			ArrayList<O> reducedSet = new ArrayList<O>();

			reducedSet.addAll(modifiers);

			O first = reducedSet.remove(0);
			ArrayList<ArrayList<O>> subsets = getSubsets(reducedSet);
			subsetCollection.addAll(subsets);
			
			subsets = getSubsets(reducedSet);	// second call is instead of deep copy

			for (ArrayList<O> subset : subsets) {
				subset.add(0, first);
			}

			subsetCollection.addAll(subsets);
		}

		return subsetCollection;
	}
}
