package eu.excitementproject.eop.core.component.scoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.entailment.EntailmentMetadata;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.NemexClassificationEDA;
import eu.excitementproject.eop.core.component.alignment.nemex.NemexAligner;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

public class NemexAlignerScoring implements ScoringComponent {

	/**
	 * the number of features
	 */
	private int numOfFeats = 11;
	private NemexAligner aligner;
	private String direction;
	private boolean isBOChunks;
	public final static Logger logger = Logger
			.getLogger(NemexClassificationEDA.class.getName());

	/**
	 * get the number of features
	 * 
	 * @return
	 * @throws ConfigurationException
	 */

	public NemexAlignerScoring(CommonConfig config)
			throws ConfigurationException {
		NameValueTable comp = config.getSection("NemexAlignerScoring");

		boolean isBOW = Boolean.valueOf(comp.getString("isBOW"));
		boolean isBOL = Boolean.valueOf(comp.getString("isBOL"));
		this.isBOChunks = Boolean.valueOf(comp.getString("isBOChunks"));

		int numOfExtDicts = Integer.parseInt(comp.getString("numOfExtDicts"));
		String[] externalDictPath = comp.getString("externalDictPath").split(
				",");
		String[] similarityMeasureExtLookup = comp.getString(
				"similarityMeasureExtLookup").split(",");
		String[] thresholdStrings = comp.getString(
				"similarityThresholdExtLookup").split(",");
		double[] similarityThresholdExtLookup = new double[thresholdStrings.length];
		for (int i = 0; i < thresholdStrings.length; i++)
			similarityThresholdExtLookup[i] = Double
					.valueOf(thresholdStrings[i]);

		String gazetteerFilePath = comp.getString("gazetteerFilePath");
		String similarityMeasureAlignmentLookup = comp
				.getString("similarityMeasureAlignmentLookup");
		double similarityThresholdAlignmentLookup = Double.parseDouble(comp
				.getString("similarityThresholdAlignmentLookup"));

		String delimiter = comp.getString("delimiter");
		boolean delimiterSwitchOff = Boolean.valueOf(comp
				.getString("delimiterSwitchOff"));
		int nGramSize = Integer.parseInt(comp.getString("nGramSize"));
		boolean ignoreDuplicateNgrams = Boolean.valueOf(comp
				.getString("ignoreDuplicateNgrams"));

		this.direction = comp.getString("direction");

		String chunkerModelPath = comp.getString("chunkerModelPath");

		boolean isWN = Boolean.valueOf(comp.getString("isWN"));
		String WNRelations = comp.getString("WNRelations");
		boolean isWNCollapsed = Boolean
				.valueOf(comp.getString("isWNCollapsed"));
		boolean useFirstSenseOnlyLeft = Boolean.valueOf(comp
				.getString("useFirstSenseOnlyLeft"));
		boolean useFirstSenseOnlyRight = Boolean.valueOf(comp
				.getString("useFirstSenseOnlyRight"));
		String wnPath = comp.getString("wnPath");

		this.aligner = new NemexAligner(isBOW, isBOL, isBOChunks,
				numOfExtDicts, externalDictPath, similarityMeasureExtLookup,
				similarityThresholdExtLookup, gazetteerFilePath,
				similarityMeasureAlignmentLookup,
				similarityThresholdAlignmentLookup, delimiter,
				delimiterSwitchOff, nGramSize, ignoreDuplicateNgrams,
				chunkerModelPath, direction, isWN, WNRelations, isWNCollapsed,
				useFirstSenseOnlyLeft, useFirstSenseOnlyRight, wnPath);

	}

	public int getNumOfFeats() {
		return numOfFeats;
	}

	@Override
	public String getComponentName() {
		return "NemexAlignerScoring";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	/**
	 * close the component
	 * 
	 * @throws ScoringComponentException
	 */
	public void close() throws ScoringComponentException {

	}

	@Override
	public Vector<Double> calculateScores(JCas cas)
			throws ScoringComponentException {
		// all the values: (T&H/H), (T&H/T), and ((T&H/H)*(T&H/T))
		Vector<Double> scoresVector = new Vector<Double>();

		try {
			aligner.annotate(cas);

			JCas tView = null, hView = null;
			
			try {
				tView = cas.getView(LAP_ImplBase.TEXTVIEW);

			} catch (CASException e) {
				throw new AlignmentComponentException(
						"Failed to access the text view", e);
			}

			try {
				hView = cas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			} catch (CASException e) {
				throw new AlignmentComponentException(
						"Failed to access the hypothesis view", e);
			}

			
			if (null != tView && null != hView) {

				if (isBOChunks) {
					Collection<Chunk> tChunks = JCasUtil.select(tView,
							Chunk.class);

					int tChunkNum = tChunks.size();

					if (0 == tChunkNum) {
						logger.warning("No chunks found for T");
					}

					Collection<Chunk> hChunks = JCasUtil.select(hView,
							Chunk.class);
					int hChunkNum = hChunks.size();

					if (0 == hChunkNum) {
						logger.warning("No chunks found for H");
					}
				}
				scoresVector
						.addAll(calculateSimilarity(tView, hView, direction));

				/*
				 * if (direction == "HtoT") { Collection<Link> hLinks = JCasUtil
				 * .select(hView, Link.class);
				 * 
				 * if (hLinks.size() > 0) {
				 * scoresVector.addAll(calculateSimilarity(tView, hLinks,
				 * tChunkNum, hChunkNum)); }
				 * 
				 * }
				 * 
				 * else { Collection<Link> tLinks = JCasUtil .select(tView,
				 * Link.class);
				 * 
				 * if (tLinks.size() > 0) {
				 * scoresVector.addAll(calculateSimilarity(hView, tLinks,
				 * tChunkNum, hChunkNum)); } }
				 */
				String task = JCasUtil.select(cas, EntailmentMetadata.class)
						.iterator().next().getTask();
				if (null == task) {
					scoresVector.add(0d);
					scoresVector.add(0d);
					scoresVector.add(0d);
					scoresVector.add(0d);
				} else {
					scoresVector.add(isTaskIE(task));
					scoresVector.add(isTaskIR(task));
					scoresVector.add(isTaskQA(task));
					scoresVector.add(isTaskSUM(task));
				}

			}

		} catch (PairAnnotatorComponentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return scoresVector;
	}

	/**
	 * Count the tokens contained in a text and store the counts in a HashMap
	 * 
	 * @param text
	 *            the input text represented in a JCas
	 * @return a HashMap represents the bag of tokens contained in the text, in
	 *         the form of <Token, Frequency>
	 */
	/*
	 * protected HashSet<String> countLinks(Collection<Link> links) {
	 * HashSet<String> linkSet = new HashSet<String>(); Iterator<Link> linkIter
	 * = links.iterator();
	 * 
	 * while (linkIter.hasNext()) { Link curr = (Link) linkIter.next(); String
	 * linkID = curr.getAlignerID();
	 * 
	 * linkSet.add(linkID);
	 * 
	 * } return linkSet; }
	 */

	protected Vector<Double> calculateSimilarity(JCas tView, JCas hView,
			String direction) {

		HashMap<String, Integer> tWordsMap = new HashMap<String, Integer>();
		HashMap<String, Integer> tPosMap = new HashMap<String, Integer>();

		HashMap<String, Integer> hWordsMap = new HashMap<String, Integer>();
		HashMap<String, Integer> hPosMap = new HashMap<String, Integer>();

		Collection<Token> tTokens = JCasUtil.select(tView, Token.class);
		int numOfTTokens = tTokens.size();
		
		if(numOfTTokens == 0) {
			logger.warning("No tokens found for TEXT");
		}
		
		int numOfTContentWords = 0;
		int numOfTVerbs = 0;
		int numOfTProperNouns = 0;

		Collection<Token> hTokens = JCasUtil.select(hView, Token.class);
		int numOfHTokens = hTokens.size();
		
		if(numOfHTokens == 0) {
			logger.warning("No tokens found for HYPOTHESIS");
		}
		
		int numOfHContentWords = 0;
		int numOfHVerbs = 0;
		int numOfHProperNouns = 0;

		Set<String> contentTags = new HashSet<String>(Arrays.asList("NN",
				"NNS", "NNP", "NNPS", "VB", "VBG", "VBD", "VBN", "VBP", "VBZ",
				"JJ", "JJR", "JJS"));

		Set<String> verbTags = new HashSet<String>(Arrays.asList("VB", "VBG",
				"VBD", "VBN"));

		Set<String> properNounTags = new HashSet<String>(Arrays.asList("NNP",
				"NNPS"));

		for (final Iterator<Token> tIter = tTokens.iterator(); tIter.hasNext();) {
			Token t = tIter.next();
			if (contentTags.contains(t.getPos().getPosValue()))
				numOfTContentWords++;
			if (verbTags.contains(t.getPos().getPosValue()))
				numOfTVerbs++;
			if (properNounTags.contains(t.getPos().getPosValue()))
				numOfTProperNouns++;

		}

		for (final Iterator<Token> hIter = hTokens.iterator(); hIter.hasNext();) {
			Token t = hIter.next();
			if (contentTags.contains(t.getPos().getPosValue()))
				numOfHContentWords++;
			if (verbTags.contains(t.getPos().getPosValue()))
				numOfHVerbs++;
			if (properNounTags.contains(t.getPos().getPosValue()))
				numOfHProperNouns++;

		}

		Collection<Link> links = null;

		links = JCasUtil.select(hView, Link.class);
		
		if (links.size() == 0)
			logger.warning("No alignment link found");

		for (final Iterator<Link> iter = links.iterator(); iter.hasNext();) {
			Link link = iter.next();
			
			
			int tStartOffset = link.getTSideTarget().getBegin();
			int tEndOffset = link.getTSideTarget().getEnd();
			int hStartOffset = link.getHSideTarget().getBegin();
			int hEndOffset = link.getHSideTarget().getEnd();
			
			//logger.info("Link direction: " +link.getDirection());
			logger.info("TStart: "+tStartOffset+", TEnd: "+ tEndOffset);
			logger.info("HStart: "+hStartOffset+", HEnd: "+ hEndOffset);

			Collection<Token> tLinkCoveredTokens = new ArrayList<Token>();
			
			for(final Iterator<Token> tIter = tTokens.iterator(); tIter.hasNext();)
			{
				Token curToken = tIter.next();
				if(curToken.getBegin() >= tStartOffset && curToken.getEnd() <= tEndOffset)
					tLinkCoveredTokens.add(curToken);
			}

			if (tLinkCoveredTokens.size() == 0)
				logger.warning("No tokens covered under aligned data in TEXT.");
			
			Collection<Token> hLinkCoveredTokens = new ArrayList<Token>();

			for(final Iterator<Token> hIter = hTokens.iterator(); hIter.hasNext();)
			{
				Token curToken = hIter.next();
				if(curToken.getBegin() >= hStartOffset && curToken.getEnd() <= hEndOffset)
					hLinkCoveredTokens.add(curToken);
			}

			if (hLinkCoveredTokens.size() == 0)
				logger.warning("No tokens covered under aligned data in HYPOTHESIS.");

			addToWordsAndPosMap(tWordsMap, tPosMap, tLinkCoveredTokens);
			addToWordsAndPosMap(hWordsMap, hPosMap, hLinkCoveredTokens);

		}

		Vector<Double> returnValue = null;
		if (direction == "TtoH")
			returnValue = calculateOverlap(tWordsMap, tPosMap, new String[] {
					"word", "contentWord", "verb", "properNoun" }, new int[] {
					numOfTTokens, numOfTContentWords, numOfTVerbs,
					numOfTProperNouns }, contentTags, verbTags, properNounTags);
		else
			returnValue = calculateOverlap(hWordsMap, hPosMap, new String[] {
					"word", "contentWord", "verb", "properNoun" }, new int[] {
					numOfHTokens, numOfHContentWords, numOfHVerbs,
					numOfHProperNouns }, contentTags, verbTags, properNounTags);

		return returnValue;

	}

	private void addToWordsAndPosMap(HashMap<String, Integer> wordsMap,
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

	private Vector<Double> calculateOverlap(HashMap<String, Integer> wordsMap,
			HashMap<String, Integer> posMap, String[] overlaps, int[] num,
			Set<String> contentTags, Set<String> verbTags,
			Set<String> properNounTags) {

		Vector<Double> overlapScore = new Vector<Double>();

		for (int i = 0; i < overlaps.length; i++) {

			String overlapType = overlaps[i];
			double overlap = 0.0;

			if (overlapType.equalsIgnoreCase("word")) {
				for (Map.Entry<String, Integer> entry : wordsMap.entrySet()) {
					overlap += entry.getValue();
				}
			}

			else if (overlapType.equalsIgnoreCase("contentWord")) {

				for (Map.Entry<String, Integer> entry : posMap.entrySet()) {
					if (contentTags.contains(entry.getKey()))
						overlap += entry.getValue();
				}
			}

			else if (overlapType.equalsIgnoreCase("verb")) {

				for (Map.Entry<String, Integer> entry : posMap.entrySet()) {
					if (verbTags.contains(entry.getKey()))
						overlap += entry.getValue();
				}
			}

			else if (overlapType.equalsIgnoreCase("properNoun")) {

				for (Map.Entry<String, Integer> entry : posMap.entrySet()) {
					if (properNounTags.contains(entry.getKey()))
						overlap += entry.getValue();
				}
			}

			if (overlap != 0.0 && num[i] != 0)
				overlap = overlap / num[i];
			else
				overlap = 0.0;
			overlapScore.add(overlap);

		}

		return overlapScore;
	}

	/*
	 * protected Vector<Double> calculateSimilarity(JCas view, Collection<Link>
	 * links, int tSize, int hSize) { double sum = 0.0d;
	 * 
	 * for (final Iterator<Link> iter = links.iterator(); iter.hasNext();) {
	 * Link link = iter.next();
	 * 
	 * JCas target = null;
	 * 
	 * if(direction == "HtoT") target = (JCas) link.getTSideTarget().getView();
	 * else target = (JCas) link.getHSideTarget().getView();
	 * 
	 * if (target.equals(view)) { sum += 1; } else continue;
	 * 
	 * }
	 * 
	 * Vector<Double> returnValue = new Vector<Double>(); returnValue.add(sum /
	 * hSize); returnValue.add(sum / tSize); returnValue.add(sum * sum / hSize /
	 * tSize); return returnValue; }
	 */

	/**
	 * check whether the task is IE
	 * 
	 * @param task
	 * @return 1: yes; 0: no.
	 */

	protected double isTaskIE(String task) {
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
	protected double isTaskIR(String task) {
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
	protected double isTaskQA(String task) {
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
	protected double isTaskSUM(String task) {
		if (task.equalsIgnoreCase("SUM")) {
			return 1;
		}
		return 0;
	}

}
