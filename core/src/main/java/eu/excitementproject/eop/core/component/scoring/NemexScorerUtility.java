package eu.excitementproject.eop.core.component.scoring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class NemexScorerUtility {

	/**
	 * Create a map of content words, verbs and proper nouns and their
	 * frequencies in given token set.
	 * 
	 * @param tokens
	 *            set of tokens in T or H
	 * @param covPosMap
	 *            map of coverage terms
	 */
	static void createCovPosMap(Collection<Token> tokens,
			HashMap<String, Integer> covPosMap) {

		// all coverage terms initialized to 0
		covPosMap.put("word", tokens.size());
		covPosMap.put("content", 0);
		covPosMap.put("verb", 0);
		covPosMap.put("properNoun", 0);

		for (final Iterator<Token> tIter = tokens.iterator(); tIter.hasNext();) {
			Token t = tIter.next();
			String curTag = t.getPos().getPosValue();

			// content word
			if (curTag.startsWith("NN") || curTag.startsWith("VB")
					|| curTag.startsWith("JJ"))
				covPosMap.put("content", covPosMap.get("content") + 1);

			// verb
			if (curTag.startsWith("VB"))
				covPosMap.put("verb", covPosMap.get("verb") + 1);

			// proper noun
			if (curTag.startsWith("NNP"))
				covPosMap.put("properNoun", covPosMap.get("properNoun") + 1);
		}

	}

	/**
	 * Generate HashMap for word and POS frequency in the given collection of
	 * tokens.
	 * 
	 * @param wordsMap
	 *            map of word and its frequency
	 * @param posMap
	 *            map of POS and frequency
	 * @param linkCoveredTokens
	 *            tokens covered under alignment
	 */
	static void addToWordsAndPosMap(HashMap<String, Integer> wordsMap,
			HashMap<String, Integer> posMap, Collection<Token> linkCoveredTokens) {

		for (final Iterator<Token> tokensIter = linkCoveredTokens.iterator(); tokensIter
				.hasNext();) {
			Token token = tokensIter.next();
			String curToken = token.getCoveredText().toLowerCase();

			String curPOS = token.getPos().getPosValue();

			if (wordsMap.containsKey(curToken)) {
				wordsMap.put(curToken, wordsMap.get(curToken) + 1);
			} else
				wordsMap.put(curToken, 1);

			if (posMap.containsKey(curPOS)) {
				posMap.put(curPOS, posMap.get(curPOS) + 1);
			} else
				posMap.put(curPOS, 1);

		}

	}

	/**
	 * Calculates percentage of overlap of required features (word, content
	 * word, verb, proper noun) under alignment.
	 * 
	 * @param wordsMap
	 *            Map of words in T/H and their frequency, covered under
	 *            alignment
	 * @param posMap
	 *            Map of POS in T/H and their frequency, covered under alignment
	 * @param overlaps
	 *            overlap features to score
	 * @param covFeatMap
	 *            Map of words, content words, verbs and proper nouns in H/T and their frequency
	 * 
	 * @return vector of scores based on overlap features
	 */
	static Vector<Double> calculateOverlap(HashMap<String, Integer> wordsMap,
			HashMap<String, Integer> posMap, String[] overlaps, HashMap<String, Integer> covFeatMap) {

		Vector<Double> overlapScore = new Vector<Double>();

		// Iterating over all coverage features
		for (int i = 0; i < overlaps.length; i++) {

			String overlapType = overlaps[i];
			double overlap = 0.0;

			if (overlapType.equalsIgnoreCase("word")) {
				for (Map.Entry<String, Integer> entry : wordsMap.entrySet()) {
					//total no. of words under alignment
					overlap += entry.getValue();
				}
			}

			else if (overlapType.equalsIgnoreCase("content")) {

				for (Map.Entry<String, Integer> entry : posMap.entrySet()) {
					if (entry.getKey().startsWith("NN")
							|| entry.getKey().startsWith("VB")
							|| entry.getKey().startsWith("JJ"))
						//total no. of content words under alignment
						overlap += entry.getValue();
				}
			}

			else if (overlapType.equalsIgnoreCase("verb")) {

				//total no. of verbs under alignment
				for (Map.Entry<String, Integer> entry : posMap.entrySet()) {
					if (entry.getKey().startsWith("VB"))
						overlap += entry.getValue();
				}
			}

			else if (overlapType.equalsIgnoreCase("properNoun")) {

				//total no. of proper nouns under alignment
				for (Map.Entry<String, Integer> entry : posMap.entrySet()) {
					if (entry.getKey().startsWith("NNP"))
						overlap += entry.getValue();
				}
			}

			//total frequency of words of given overlap type in T/H
			int totalNum = covFeatMap.get(overlapType);
			
			if (overlap != 0.0 && totalNum != 0)
				overlap = overlap / totalNum;
			else
				overlap = 0.0;
			
			overlapScore.add(overlap);

		}

		return overlapScore;
	}

	/**
	 * check whether the task is IE
	 * 
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	static double isTaskIE(String task) {
		if (task.equalsIgnoreCase("IE")) {
			return 1;
		}
		return 0;
	}

	/**
	 * check whether the task is IR
	 * 
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	static double isTaskIR(String task) {
		if (task.equalsIgnoreCase("IR")) {
			return 1;
		}
		return 0;
	}

	/**
	 * check whether the task is QA
	 * 
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	static double isTaskQA(String task) {
		if (task.equalsIgnoreCase("QA")) {
			return 1;
		}
		return 0;
	}

	/**
	 * check whether the task is SUM
	 * 
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	static double isTaskSUM(String task) {
		if (task.equalsIgnoreCase("SUM")) {
			return 1;
		}
		return 0;
	}

}
