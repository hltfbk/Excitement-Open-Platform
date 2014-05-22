/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionTreeAndParentMap;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech.PennPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryException;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl.ParseTreeUtils.ParseTreeException;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.lap.biu.en.postagger.stanford.MaxentPosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;

/**
 * This class extracts hypernyms of words out of their wiki sense descriptions. It does so by prefixing gloss descriptions with "WORD is ..." and parsing 
 * the new copular sentence, and extracting the predicate and its modifiers.
 * <p>
 * For (a trivial ) example, for "dog: A coward" you parse "dog is a coward" and extract the predicate "coward".
 * Insofar it's mainly used for parsing out hypernyms in {@link JwktlSense#getRelatedWords(WiktionaryRelation)}.
 * @author Amnon Lotan
 * @since 28/06/2011
 * 
 */
public class WktGlossParser {
	private static final String COMMA = ",";
	private static final String VERB_GLOSS_SPLIT_REGEX = "[,;\\.]";
	private static final char PERIOD = '.';
	private static final String NOUN_GLOSS_SPLIT_REGEX = "[\\.;]";

	private EasyFirstParser parser;
	private PosTagger posTagger;

	/**
	 * Ctor
	 * 
	 * @param posTaggerModelFile e.g. "b:/jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger" 
	 * @throws JwktlException 
	 */
	public WktGlossParser(String posTaggerModelFile) throws JwktlException {
		try {
			parser = new EasyFirstParser(posTaggerModelFile );
			parser.init();
		} catch (ParserRunException e) {
			throw new JwktlException("could not init the parser", e);
		}
		try {
			posTagger = new MaxentPosTagger(posTaggerModelFile);
			posTagger.init();
		} catch (PosTaggerException e) {
			throw new JwktlException("POS tagger error", e);
		}
	}

	/**
	 * Extract hypernyms out of the wiki sense descriptions, by prefixing them with "X is" and parsing 
	 * the new copular sentence, and extracting the predicate and its modifiers.
	 * <p>
	 * For instance, for "dog: A coward" you parse "dog is a coward" and extract the predicate "coward". 
	 * @param term 
	 * @param 
	 * @return
	 * @throws WiktionaryException 
	 */
	public List<String> parseGloss(String term, PartOfSpeech pos, String gloss) throws WiktionaryException {
		if (term == null)
			throw new JwktlException("Got null word");
		if (gloss == null)
			throw new JwktlException("Got null gloss");
		
		List<String> hypernyms = new Vector<String>();
		
		List<String> sentences = splitIntoSentences(term, pos, gloss);
		// parse and parse-out-hypernyms for each sentence
		for (String sentence : sentences)
		{
			parser.setSentence(sentence);
			List<BasicConstructionNode> nodesList;
			BasicConstructionTreeAndParentMap treeAndParentMap ;
			BasicConstructionNode parseTree;
			try {
				parser.parse();
				parseTree = parser.getMutableParseTree();
				treeAndParentMap = new BasicConstructionTreeAndParentMap(parseTree);
				nodesList = parser.getNodesOrderedByWords();
				
			} catch (ParserRunException e) {
				throw new WiktionaryException("Error parsing the sentence: " + sentence, e);
			} catch (TreeAndParentMapException e) {
				throw new WiktionaryException("Error constructing EnglishTreeAndParentMap out of the sentence: " + sentence, e);
			}
			
			if (!simplerPos(pos.getCanonicalPosTag()).equals(SimplerCanonicalPosTag.VERB))
				// noun glosses are parsed differently, and the relevant subtree needs to be pinpointed
				parseTree = findParentOfWord(term, sentence, nodesList, treeAndParentMap );	
			
			List<String> localHypernyms;
			// add the main predicate and all its governed entailed words
			try { localHypernyms = ParseTreeUtils.getEntailedModifiersOf(parseTree);	}
			catch (ParseTreeException e) {	throw new JwktlException("see nested", e);	}
			hypernyms.addAll(localHypernyms);
		}		
		
		return hypernyms;
	}

	/**
	 * @param term
	 * @param pos
	 * @param gloss
	 * @return
	 * @throws JwktlException 
	 */
	private List<String> splitIntoSentences(String term, PartOfSpeech pos, String gloss) throws JwktlException {
		if (gloss.length() == 0)
			return new Vector<String>();
		if (simplerPos(pos.getCanonicalPosTag()).equals(SimplerCanonicalPosTag.VERB))
			return splitIntoSentencesForVerb(gloss);
		// else
			return splitIntoSentencesForNoun(term, gloss);
	}

	/**
	 * For a noun's gloss, 
	 * @param gloss
	 * @param term 
	 * @return
	 * @throws JwktlException 
	 */
	private List<String> splitIntoSentencesForNoun( String term, String gloss) throws JwktlException {
		// TODO think what to do with the second and third sentences in noun glosses
		String[] glossParts = gloss.split(NOUN_GLOSS_SPLIT_REGEX);
		String firstWord = determineFirstWord(term, gloss);
		// NP glosses probably parse better as a "an X is a Y" sentence
		String sentence = firstWord + ' ' + term + " is " + glossParts[0] + PERIOD;			// the parser likes terminating periods	 
		
		List<String> sentences = new Vector<String>();
		sentences.add(sentence);
		return sentences;
	}

	/**
	 * Split the given verb gloss around each punctuation mark that is followed by (infinitival) �to" or by a verb. each split part is 
	 * supposed to be an independent, equivalent gloss.<br>
	 * E.g. "To follow in an annoying way, to constantly be affected by." has two separate equivalent glosses. Guess what the entry word is!
	 *   
	 * @param gloss
	 * @return
	 * @throws JwktlException 
	 */
	private List<String> splitIntoSentencesForVerb(String gloss) throws JwktlException {
		String[] parts = gloss.split(VERB_GLOSS_SPLIT_REGEX);
		List<String> glosses = new Vector<String>();
		StringBuffer currGloss = new StringBuffer(parts[0].trim());
		for (int i = 1; i < parts.length; i++)
		{
			String part = parts[i].trim(); 					// trim is important
			String firstWord = part.split(" ")[0];
			if (firstWord.toLowerCase().equals("to") || 
					simplerPos(posTag(part).get(0).getPartOfSpeech().getCanonicalPosTag()).equals(SimplerCanonicalPosTag.VERB))	// first word is a verb
			{
				// open a new sentence
				glosses.add(currGloss.toString() + PERIOD);
				currGloss = new StringBuffer(part);
			}
			else	// parts[i] is just the continuation of currGloss
				currGloss.append(COMMA + ' ' + part);
		}
		glosses.add(currGloss.toString() + PERIOD);
		return glosses;
	}

	/**
	 * @param string
	 * @return
	 * @throws JwktlException 
	 */
	private List<PosTaggedToken> posTag(String string) throws JwktlException {
		try {
			// must pos tag the term even though wiki already has, cos the wiki POS don't tell us if the noun is singular
			posTagger.setTokenizedSentence(string.trim());
			posTagger.process();
			return posTagger.getPosTaggedTokens();
		} catch (PosTaggerException e) {
			throw new JwktlException("POS tagger error", e);
		}
	}

	/**
	 * POS tag this term, and return a determiner, or "to", according to the POS tag 
	 * @param term
	 * @param gloss 
	 * @return
	 * @throws JwktlException 
	 */
	private String determineFirstWord(String term, String gloss) throws JwktlException {
		String prefixWord = "";

		{
			// if the last word is a noun-singular. it's a noun phrase, and needs an 'a'
			// must pos tag the term even though wiki already has, cos the wiki POS don't tell us if the noun is singular
			List<PosTaggedToken> tags = posTag(term);
			// note that term may have several words.
			if (tags.get(tags.size()-1).getPartOfSpeech().getStringRepresentation().equals(PennPosTag.NN.name()))
				prefixWord = "a";
		}
		return prefixWord;
	}

	/**
	 * Find the first node in the sentence that contains 'word'. 
	 * @param sentence 
	 * @param nodesList 
	 * 
	 * @param nodesList
	 * @return
	 * @throws JwktlException 
	 */
	private BasicConstructionNode findParentOfWord(String word, String sentence, List<BasicConstructionNode> nodesList, 
			BasicConstructionTreeAndParentMap treeAndParentMap) throws JwktlException {

		// find the first node in the sentence that contains 'word' (search in the order of the words in the sentence)
		Map<BasicConstructionNode, BasicConstructionNode> nodeToParentMap = treeAndParentMap.getParentMap();
		BasicConstructionNode entryNode = null;
 
		// find the word's index in the sentence, 
		String[] words = sentence.trim().split(" ");
		int indexOfTheWord;
		for (indexOfTheWord = 0; !words[indexOfTheWord].equals(word); indexOfTheWord++)
			;
		entryNode = nodesList.get(indexOfTheWord);
		if (entryNode == null || !word.equals(entryNode.getInfo().getNodeInfo().getWord()))
			throw new JwktlException("Internal error: couldn't find '"+word+"'s node in the parse tree of the sentence: " + sentence);

		// in case the first node in the sentence has no parent (probably cos it's the root), use it as the relevant root 
		BasicConstructionNode parentNode = nodeToParentMap.containsKey(entryNode) ? nodeToParentMap.get(entryNode) : entryNode;
		return parentNode;
		
	
	}

}
