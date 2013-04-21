package eu.excitementproject.eop.core.component.scoring;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import com.aliasi.util.Strings;

import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;

/**
 * The re-implementation of (Wang and Neumann, 2007)'s work. Basically, the
 * algorithm extracts dependency paths from dependency trees of T and H, and
 * then compute the feature scores. Please refer to the original paper for more
 * details.
 * 
 * The implementation is <b>NOT</b> complete yet.
 * 
 * @author Rui
 * 
 */
public class TreeSkeletonScoring extends BagOfDepsScoring {

	// TODO: replace to configuration in the future
	/**
	 * the stop word file path
	 */
	protected static final String STOP_WORD_PATH = "./src/main/resources/external-data/stopwords_EN.txt";

	/**
	 * group words together having the following POSes
	 */
	protected static final String[] ALLOWED_POS_PREFIXES = { "N", "CD" };
	// protected static final String[] ALLOWED_POS_PREFIXES = {"N", "CD", "JJ"};
	// //lower "no_cover" from 23 to 14

	/**
	 * the stop dependency relation list
	 */
	protected static final String[] STOP_DEP_RELS = { "ROOT" };

	/**
	 * the stop POS list
	 */
	protected static final String[] STOP_POSES = { "NULL" };

	/**
	 * the set of stop words
	 */
	protected Set<String> stopWordSet = new HashSet<String>();

	/**
	 * the number of features
	 */
	private int numOfFeats = 12;

	/**
	 * get the number of features
	 */
	@Override
	public int getNumOfFeats() {
		return numOfFeats;
	}

	@Override
	public String getComponentName() {
		return "TreeSkeletonScoring";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	// TODO: input configuration as parameter
	/**
	 * the constructor and initializer of the stop word list
	 * 
	 * @throws ConfigurationException
	 */
	public TreeSkeletonScoring() throws ConfigurationException {
		try (BufferedReader br = new BufferedReader(new FileReader(
				STOP_WORD_PATH))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.trim().length() == 0) {
					continue;
				}
				stopWordSet.add(sCurrentLine.trim());
			}
		} catch (IOException e) {
			throw new ConfigurationException(e.getMessage());
		}
	}

	@Override
	public Vector<Double> calculateScores(JCas aCas)
			throws ScoringComponentException {
		Vector<Double> scoresVector = new Vector<Double>();

		try {
			JCas tView = aCas.getView("TextView");
			JCas hView = aCas.getView("HypothesisView");

			scoresVector.addAll(calculateTSScores(tView, hView));

			// add the backup values
			scoresVector.addAll(super.calculateScores(aCas));

		} catch (CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;
	}

	/**
	 * The main function to calculate the Tree Skeleton features.
	 * 
	 * @param tView
	 *            the <code>JCas</code> object of Text
	 * @param hView
	 *            the <code>JCas</code> object of Hypothesis
	 * @return the feature values (including non-similarity scores)
	 */
	protected Vector<Double> calculateTSScores(JCas tView, JCas hView) {
		Vector<Double> scoresVector = new Vector<Double>();

		Map<String, Integer> tTokenPoses = countTokenPoses(tView);
		Map<String, Integer> hTokenPoses = countTokenPoses(hView);

		// 1. extract candidate anchors in H: a list of words
		// 2. more candidate anchors (fuzzy match with T)
		Set<String> wordPosPairSet = extractWordPairList(tTokenPoses,
				hTokenPoses, true, true, true, false);
		scoresVector.add((double) wordPosPairSet.size());

		Map<String, String> tDeps = indexDepTree(tView);
		Map<String, String> hDeps = indexDepTree(hView);

		// 3. extract dep path
		Set<String> pathPairSet = extractPathPairList(tDeps, hDeps,
				wordPosPairSet);
		scoresVector.add((double) pathPairSet.size());

		// 4. combining the paths
		// 5. path filtering and generalize path
		// 6. connecting path into tree skeletons
		Set<String> mergedPairSet = mergePaths(pathPairSet);
		scoresVector.add((double) mergedPairSet.size());

		Set<String> pathDiffSet = pathDiffs(mergedPairSet);
		scoresVector.add((double) pathDiffSet.size());

		numOfFeats += scoresVector.size();

		return scoresVector;
	}

	/**
	 * To extract the candidate anchor pairs in T and H.
	 * 
	 * @param tTokenPoses
	 *            tokens and their POSes of T
	 * @param hTokenPoses
	 *            tokens and their POSes of H
	 * @param isStop
	 *            whether to filter out the stop words
	 * @param isContain
	 *            whether to turn on substring match
	 * @param isFuzzy
	 *            whether to turn on fuzzy match
	 * @param isAbbrev
	 *            whether to turn on abbreviation match
	 * @return the set of (anchored) word pairs
	 */
	protected Set<String> extractWordPairList(Map<String, Integer> tTokenPoses,
			Map<String, Integer> hTokenPoses, boolean isStop,
			boolean isContain, boolean isFuzzy, boolean isAbbrev) {
		Set<String> wordPairSet = new HashSet<String>();
		for (String tWordPos : tTokenPoses.keySet()) {
			String tPos = tWordPos.split(" ### ")[1];
			if (!isAllowedPOSPrefix(tPos)) {
				continue;
			}
			for (String hWordPos : hTokenPoses.keySet()) {
				String hPos = hWordPos.split(" ### ")[1];
				if (!isAllowedPOSPrefix(hPos)) {
					continue;
				}
				String tWordLow = tWordPos.split(" ### ")[0].toLowerCase();
				String hWordLow = hWordPos.split(" ### ")[0].toLowerCase();
				if (isStop) {
					if (stopWordSet.contains(tWordLow)
							|| stopWordSet.contains(hWordLow)) {
						continue;
					}
				}
				if (tWordLow.equals(hWordLow)) {
					wordPairSet.add(tWordPos + " ## " + hWordPos);
					continue;
				}
				if (isContain) {
					if (tWordLow.startsWith(hWordLow)
							|| tWordLow.endsWith(hWordLow)
							|| hWordLow.startsWith(tWordLow)
							|| hWordLow.endsWith(tWordLow)) {
						wordPairSet.add(tWordPos + " ## " + hWordPos);
						continue;
					}
				}
				if (isFuzzy) {
					if (isFuzzyMatch(tWordLow, hWordLow, 3, 3)) {
						wordPairSet.add(tWordPos + " ## " + hWordPos);
						continue;
					}
				}
				if (isAbbrev) {
					if (isAbbrevMatch(tWordPos.split(" ### ")[0],
							hWordPos.split(" ### ")[0])) {
						wordPairSet.add(tWordPos + " ## " + hWordPos);
						continue;
					}
				}
			}
		}
		return wordPairSet;
	}

	private boolean isAllowedPOSPrefix(String POS) {
		for (String allowedPOS : ALLOWED_POS_PREFIXES) {
			if (POS.startsWith(allowedPOS)) {
				return true;
			}
		}
		return false;
	}

	private boolean isFuzzyMatch(String tWordLow, String hWordLow,
			int leftLevel, int rightLevel) {
		for (float m = 0.0f; m < leftLevel + 1; m = m + 1.0f) {
			if (m >= hWordLow.length()) {
				m = leftLevel + 1;
				continue;
			}
			for (float n = 0.0f; n < rightLevel + 1; n = n + 1.0f) {
				if ((hWordLow.length() - m - n) / hWordLow.length() < 0.6) {
					n = rightLevel + 1;
					continue;
				}
				if (hWordLow.length() <= n) {
					n = rightLevel + 1;
					continue;
				}
				String temp = hWordLow.substring((int) m, hWordLow.length()
						- (int) n);
				if (temp.length() <= 3) {
					n = rightLevel + 1;
					continue;
				}
				if (tWordLow.contains(temp)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isAbbrevMatch(String tWord, String hWord) {
		if (Character.isUpperCase(tWord.charAt(0))
				&& Strings.allUpperCase(hWord.toCharArray())
				&& hWord.indexOf(tWord.charAt(0)) != -1
				|| Character.isUpperCase(hWord.charAt(0))
				&& Strings.allUpperCase(tWord.toCharArray())
				&& tWord.indexOf(hWord.charAt(0)) != -1) {
			return true;
		}
		return false;
	}

	/**
	 * To extract the dependency path pairs based on the anchored word pairs.
	 * 
	 * @param tDeps
	 *            the dependency tree of T
	 * @param hDeps
	 *            the dependency tree of H
	 * @param wordPosPairSet
	 *            the set of anchored word pairs
	 * @return the set of path pairs
	 */
	protected Set<String> extractPathPairList(Map<String, String> tDeps,
			Map<String, String> hDeps, Set<String> wordPosPairSet) {
		Set<String> pathPairSet = new HashSet<String>();
		Set<String> tWordPosSet = new HashSet<String>();
		Set<String> hWordPosSet = new HashSet<String>();
		Set<String> newWordPosPairSet = new HashSet<String>();
		for (String wordPosPair : wordPosPairSet) {
			String tWordPos = wordPosPair.split(" ## ")[0];
			String hWordPos = wordPosPair.split(" ## ")[1];
			// tWordPosSet.add(wordPosPair.split(" ## ")[0]);
			// hWordPosSet.add(wordPosPair.split(" ## ")[1]);
			for (String tLeft : tDeps.keySet()) {
				if (!tLeft.endsWith(tWordPos)) {
					continue;
				}
				for (String hLeft : hDeps.keySet()) {
					if (!hLeft.endsWith(hWordPos)) {
						continue;
					}
					while (tDeps.containsKey(tLeft)
							&& isAllowedPOSPrefix(tDeps.get(tLeft).split(
									" ### ")[2])) {
						tLeft = tDeps.get(tLeft);
					}
					tWordPos = tLeft.substring(tLeft.indexOf(" ### ")
							+ " ### ".length());
					tWordPosSet.add(tWordPos);
					while (hDeps.containsKey(hLeft)
							&& isAllowedPOSPrefix(hDeps.get(hLeft).split(
									" ### ")[2])) {
						hLeft = hDeps.get(hLeft);
					}
					hWordPos = hLeft.substring(hLeft.indexOf(" ### ")
							+ " ### ".length());
					hWordPosSet.add(hWordPos);
					newWordPosPairSet.add(tWordPos + " ## " + hWordPos);
				}
			}
		}
		for (String wordPosPair : newWordPosPairSet) {
			String tWordPos = wordPosPair.split(" ## ")[0];
			String hWordPos = wordPosPair.split(" ## ")[1];
			for (String tLeft : tDeps.keySet()) {
				if (!tLeft.endsWith(tWordPos)) {
					continue;
				}
				for (String hLeft : hDeps.keySet()) {
					if (!hLeft.endsWith(hWordPos)) {
						continue;
					}
					tWordPosSet.remove(tWordPos);
					hWordPosSet.remove(hWordPos);
					pathPairSet
							.add(extractPath(tDeps, tLeft, tWordPosSet)
									.toString()
									+ " ||| "
									+ extractPath(hDeps, hLeft, hWordPosSet)
											.toString());
					tWordPosSet.add(tWordPos);
					hWordPosSet.add(hWordPos);
				}
			}
		}

		return pathPairSet;
	}

	private StringBuffer extractPath(Map<String, String> deps, String wordPos,
			Set<String> anchorSet) {
		StringBuffer returnSB = new StringBuffer();
		String currentWordPos = wordPos.substring(wordPos.indexOf(" ### ")
				+ " ### ".length());
		returnSB.append(currentWordPos);
		if (anchorSet.contains(currentWordPos)) {
			return returnSB;
		}
		if (!deps.containsKey(wordPos)) {
			return returnSB;
		}
		returnSB.append(" ## ");
		String right = deps.get(wordPos);
		returnSB.append(right.split(" ## ")[0]);
		returnSB.append(" ## ");
		returnSB.append(extractPath(deps, right.split(" ## ")[1], anchorSet));
		return returnSB;
	}

	/**
	 * To merge the "similar" paths.
	 * 
	 * @param pathPairSet
	 *            the set of path pairs
	 * @return the set of merged path pairs
	 */
	protected Set<String> mergePaths(Set<String> pathPairSet) {
		// TODO: check T-paths and H-paths first, filter out those general
		// paths, leaving only the most specific ones
		Set<String> returnPairSet = new HashSet<String>();
		if (pathPairSet.size() <= 1) {
			return pathPairSet;
		}
		// Set<String> tPaths = new HashSet<String>();
		// Set<String> hPaths = new HashSet<String>();
		// for (String pathPair : pathPairSet) {
		// String[] paths = pathPair.split(" \\|\\|\\| ");
		// tPaths.add(paths[0]);
		// hPaths.add(paths[1]);
		// }
		Set<String> remainPairSet = new HashSet<String>();
		remainPairSet.addAll(pathPairSet);

		for (String pathPair1 : pathPairSet) {
			String[] paths1 = pathPair1.split(" \\|\\|\\| ");
			if (!paths1[0].endsWith("ROOT ## NULL ### NULL")
					|| !paths1[1].endsWith("ROOT ## NULL ### NULL")) {
				continue;
			}
			for (String pathPair2 : pathPairSet) {
				if (pathPair1.equals(pathPair2)) {
					continue;
				}
				String[] paths2 = pathPair2.split(" \\|\\|\\| ");
				if (!paths2[0].endsWith("ROOT ## NULL ### NULL")
						|| !paths2[1].endsWith("ROOT ## NULL ### NULL")) {
					continue;
				}
				String mergedPath1 = combineTwoPaths(paths1[0], paths2[0]);
				String mergedPath2 = combineTwoPaths(paths1[1], paths2[1]);
				if (!mergedPath1.equals(mergedPath2)
						&& !returnPairSet.contains(reversePath(mergedPath1)
								+ " ||| " + reversePath(mergedPath2))
						&& !returnPairSet.contains(mergedPath1 + " ||| "
								+ reversePath(mergedPath2))
						&& !returnPairSet.contains(reversePath(mergedPath1)
								+ " ||| " + mergedPath2)) {
					returnPairSet.add(mergedPath1 + " ||| " + mergedPath2);
					remainPairSet.remove(pathPair1);
					remainPairSet.remove(pathPair2);
				}
			}
		}
		for (String remainPair : remainPairSet) {
			String[] remainPaths = remainPair.split(" \\|\\|\\| ");
			if (remainPaths[0].endsWith("ROOT ## NULL ### NULL")
					&& !remainPaths[1].endsWith("ROOT ## NULL ### NULL")
					|| !remainPaths[0].endsWith("ROOT ## NULL ### NULL")
					&& remainPaths[1].endsWith("ROOT ## NULL ### NULL")) {
				continue;
			}
			if (!compareMiddlePaths(remainPaths[0], remainPaths[1])) {
				returnPairSet.add(remainPair);
			}
		}

		return returnPairSet;
	}

	private boolean compareMiddlePaths(String path1, String path2) {
		String[] items1 = path1.split(" ## ");
		String[] items2 = path2.split(" ## ");
		if (items1.length < 3 || items2.length < 3) {
			return true;
		}
		if (items1.length != items2.length) {
			return false;
		}
		String middle1 = path1.substring(
				path1.indexOf(" ## ") + " ## ".length(),
				path1.lastIndexOf(" ## "));
		String middle2 = path2.substring(
				path2.indexOf(" ## ") + " ## ".length(),
				path2.lastIndexOf(" ## "));
		if (middle1.equalsIgnoreCase(middle2)) {
			return true;
		}
		return false;
	}

	private String combineTwoPaths(String path1, String path2) {
		if (path1.equals(path2)) {
			return path1;
		}
		if (path1.endsWith(path2)) {
			String anchor = path2.split(" ## ")[0];
			return path1.substring(0, path1.indexOf(anchor) + anchor.length());
		}
		if (path2.endsWith(path1)) {
			return combineTwoPaths(path2, path1);
		}
		String[] items1 = path1.split(" ## ");
		String[] items2 = path2.split(" ## ");
		if (items1.length < items2.length) {
			return reversePath(combineTwoPaths(path2, path1));
		}
		int common = items2.length - 1;
		int lengDiff = items1.length - items2.length;
		for (int i = common; i >= 0; i -= 2) {
			if (!items1[i + lengDiff].equals(items2[i])) {
				common = i + 2;
				break;
			}
		}
		// make sure the array boundary
		common = Math.min(common, items2.length - 1);
		StringBuffer returnPath = new StringBuffer();
		for (int i = 0; i < common + lengDiff; i++) {
			returnPath.append(items1[i]);
			returnPath.append(" ## ");
		}
		returnPath.append("*");
		returnPath.append(items1[common + lengDiff]);
		returnPath.append("*");
		returnPath.append(" ## ");
		for (int i = common - 1; i > 0; i--) {
			returnPath.append(items2[i]);
			returnPath.append(" ## ");
		}
		returnPath.append(items2[0]);
		return returnPath.toString();
	}

	private String reversePath(String path) {
		List<String> pathItemList = Arrays.asList(path.split(" ## "));
		Collections.reverse(pathItemList);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < pathItemList.size() - 1; i++) {
			sb.append(pathItemList.get(i));
			sb.append(" ## ");
		}
		sb.append(pathItemList.get(pathItemList.size() - 1));
		return sb.toString();
	}

	/**
	 * To compute the path difference.
	 * 
	 * @param mergePathPairSet
	 *            the set of merged path pairs
	 * @return the set of differences of the path pairs
	 */
	protected Set<String> pathDiffs(Set<String> mergePathPairSet) {
		Set<String> returnPathDiffSet = new HashSet<String>();
		for (String pathPair : mergePathPairSet) {
			String[] paths = pathPair.split(" \\|\\|\\| ");
			returnPathDiffSet.add(extractPathDiff(paths[0], paths[1]));
		}
		return returnPathDiffSet;
	}

	private String extractPathDiff(String path1, String path2) {
		String[] items1 = path1.split(" ## ");
		String[] items2 = path2.split(" ## ");
		if (items1.length < 3) {
			if (items2.length < 3) {
				return "";
			}
			return path2.substring(path2.indexOf(" ## ") + " ## ".length(),
					path2.lastIndexOf(" ## "));
		}
		if (items2.length < 3) {
			return path1.substring(path1.indexOf(" ## ") + " ## ".length(),
					path1.lastIndexOf(" ## "));
		}
		return extractRemainPathDiff(
				path1.substring(path1.indexOf(" ## ") + " ## ".length(),
						path1.lastIndexOf(" ## ")),
				path2.substring(path2.indexOf(" ## ") + " ## ".length(),
						path2.lastIndexOf(" ## ")));
	}

	private String extractRemainPathDiff(String path1, String path2) {
		String[] items1 = path1.split(" ## ");
		String[] items2 = path2.split(" ## ");
		if (items1.length == 1) {
			if (items2.length == 1) {
				// compare the two dep relations
				if (compareTwoNodes(items1[0], items2[0])) {
					return "";
				} else {
					return items1[0] + " ||| " + items2[0];
				}
			}
			if (path2.startsWith(items1[0])) {
				return " ||| "
						+ path2.substring(path2.indexOf(" ## ")
								+ " ## ".length());
			} else if (path2.endsWith(items1[0])) {
				return " ||| " + path2.substring(0, path2.lastIndexOf(" ## "));
			} else {
				return items1[0] + " ||| " + path2;
			}
		}
		if (items2.length == 1) {
			if (path1.startsWith(items2[0])) {
				return path1.substring(path1.indexOf(" ## ") + " ## ".length())
						+ " ||| ";
			} else if (path1.endsWith(items2[0])) {
				return path1.substring(0, path1.lastIndexOf(" ## ")) + " ||| ";
			} else {
				return path1 + " ||| " + items2[0];
			}
		}
		// compare the left most nodes
		if (compareTwoNodes(items1[0], items2[0])) {
			return extractRemainPathDiff(
					path1.substring(path1.indexOf(" ## ") + " ## ".length()),
					path2.substring(path2.indexOf(" ## ") + " ## ".length()));
		}
		// compare the right most nodes
		if (compareTwoNodes(items1[items1.length - 1],
				items2[items2.length - 1])) {
			return extractRemainPathDiff(
					path1.substring(0, path1.lastIndexOf(" ## ")),
					path2.substring(0, path2.lastIndexOf(" ## ")));
		}
		return path1 + " ||| " + path2;
	}

	private boolean compareTwoNodes(String node1, String node2) {
		String[] items1 = node1.split(" ### ");
		String[] items2 = node2.split(" ### ");
		if (items1.length != items2.length || items1.length > 2
				|| items2.length > 2) {
			// TODO: invalid comparison!
			return false;
		}
		if (items1.length == 1 && items2.length == 1) {
			// compare two dep relations
			if (items1[0].equals(items2[0])) {
				return true;
			} else {
				return false;
			}
		}
		// compare the POSes
		// if (items1[1].charAt(0) != items2[1].charAt(0)) {
		// return false;
		// }
		// compare the words
		if (items1[0].equalsIgnoreCase(items2[0])) {
			return true;
		}
		return false;
	}

}
