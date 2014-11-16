package eu.excitementproject.eop.core.component.scoring;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
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
	private int numOfFeats = 7;
	private NemexAligner aligner;
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

		String gazetteerFilePath = comp.getString("gazetteerFilePath");
		String delimiter = comp.getString("delimiter");
		Boolean delimiterSwitchOff = Boolean.valueOf(comp
				.getString("delimiterSwitchOff"));
		int nGramSize = Integer.parseInt(comp.getString("nGramSize"));
		Boolean ignoreDuplicateNgrams = Boolean.valueOf(comp
				.getString("ignoreDuplicateNgrams"));
		String similarityMeasure = comp.getString("similarityMeasure");
		double similarityThreshold = Double.parseDouble(comp
				.getString("similarityThreshold"));
		String chunkerModelPath = comp.getString("chunkerModelPath");
		String direction = comp.getString("direction");

		this.aligner = new NemexAligner(gazetteerFilePath, delimiter,
				delimiterSwitchOff, nGramSize, ignoreDuplicateNgrams,
				similarityMeasure, similarityThreshold, chunkerModelPath,
				direction);

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
				hView = cas.getView(LAP_ImplBase.HYPOTHESISVIEW);
				logger.info("Obtained hView:" + hView.getDocumentText());
			} catch (CASException e) {
				throw new AlignmentComponentException(
						"Failed to access the hypothesis view", e);
			}

			try {
				tView = cas.getView(LAP_ImplBase.TEXTVIEW);

			} catch (CASException e) {
				throw new AlignmentComponentException(
						"Failed to access the text view", e);
			}

			if (null != tView && null != hView) {
				Collection<Chunk> tChunks = JCasUtil.select(tView, Chunk.class);
				int tChunkNum = tChunks.size();

				if(0 == tChunkNum) {
					logger.info("No chunks found for T");
				}
				
				
				Collection<Chunk> hChunks = JCasUtil.select(hView, Chunk.class);
				int hChunkNum = hChunks.size();

				if (0 == hChunkNum) {
					logger.info("No chunks found for H");
				}
				
				logger.info("Getting links from hView now");
				Collection<Link> hLinks = JCasUtil.select(hView, Link.class);

				if (hLinks.size() > 0) {
					logger.info("Links found, adding scores");
					scoresVector.addAll(calculateSimilarity(tView, hLinks,
							tChunkNum, hChunkNum));
				}
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

			/*
			 * if(0 == hLinks.size()) {
			 * logger.info("No Links found for Hypo view ");
			 * 
			 * scoresVector.add(0d); scoresVector.add(0d); scoresVector.add(0d);
			 * 
			 * }
			 */
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

	/**
	 * Calculate the similarity between two bags of tokens
	 * 
	 * @param tBag
	 *            the bag of tokens of T stored in a HashMap
	 * @param hBag
	 *            the bag of tokens of H stored in a HashMap
	 * @return a vector of double values, which contains: 1) the ratio between
	 *         the number of overlapping tokens and the number of tokens in H;
	 *         2) the ratio between the number of overlapping tokens and the
	 *         number of tokens in T; 3) the product of the above two
	 */
	protected Vector<Double> calculateSimilarity(JCas tView,
			Collection<Link> hLinks, int tSize, int hSize) {
		double sum = 0.0d;

		for (final Iterator<Link> iter = hLinks.iterator(); iter.hasNext();) {
			Link hLink = iter.next();

			// JCas tTarget = null;

			// tTarget = (JCas) hLink.getTSideTarget().getView();

			// if (tTarget.equals(tView)) {
			logger.info("Incrementing sum");
			sum += 1;
			// } else
			// continue;

		}

		Vector<Double> returnValue = new Vector<Double>();
		returnValue.add(sum / hSize);
		returnValue.add(sum / tSize);
		returnValue.add(sum * sum / hSize / tSize);
		return returnValue;
	}

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
