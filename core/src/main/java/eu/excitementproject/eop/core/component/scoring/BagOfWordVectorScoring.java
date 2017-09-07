package eu.excitementproject.eop.core.component.scoring;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.alignment.vectorlink.BagOfWordVectorAligner;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Calculate similarity scores between T and H based on alignment of words
 * through word vectors.
 * 
 * @author Madhumita
 * @since July 2015
 */
public class BagOfWordVectorScoring implements ScoringComponent {

	public BagOfWordVectorScoring(CommonConfig config)
			throws ConfigurationException, IOException {

		NameValueTable comp = config.getSection("BagOfWordVectorScoring");

		this.removeStopWords = Boolean.valueOf(comp
				.getString("removeStopWords"));

		/*
		 * If remove stop words is true, create a stopwords set.
		 */
		if (removeStopWords) {
			this.stopWords = new HashSet<String>();
			try {
				for (String str : (Files.readAllLines(
						Paths.get(comp.getString("stopWordPath")),
						Charset.forName("UTF-8"))))
					this.stopWords.add(str.toLowerCase());
			} catch (IOException e1) {
				logger.error("Could not read stop words file");
			}
		}

		this.aligner = new BagOfWordVectorAligner(config, removeStopWords,
				stopWords);

	}

	@Override
	public Vector<Double> calculateScores(JCas cas)
			throws ScoringComponentException {

		try {
			// add alignment links to cas pair.
			this.aligner.annotate(cas);
		} catch (PairAnnotatorComponentException e) {
			throw new ScoringComponentException(
					"Could not add alignment links to T and H pair.");
		}

		Vector<Double> scoresVector = new Vector<Double>();

		// Read text and hypothesis views
		if (null == cas)
			throw new ScoringComponentException(
					"calculateScores() got a null JCas object.");

		JCas tView;
		JCas hView;
		try {
			tView = cas.getView(LAP_ImplBase.TEXTVIEW);
			hView = cas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		} catch (CASException e) {
			throw new ScoringComponentException(
					"Failed to access the Two views (TEXTVIEW, HYPOTHESISVIEW)",
					e);
		}

		logger.info("TEXT: " + tView.getDocumentText());
		logger.info("HYPO: " + hView.getDocumentText());

		Collection<Token> tAnnots = JCasUtil.select(tView, Token.class);
		Collection<Token> hAnnots = JCasUtil.select(hView, Token.class);
		
		// num of words in text
		int tSize = tAnnots.size();
		if (0 == tSize) {
			logger.warn("No tokens found for text.");
		}

		// num of words in hypothesis
		int hSize = hAnnots.size();
		if (0 == hSize) {
			logger.warn("No tokens found for hypothesis");
		}
		
		if (removeStopWords) {
			
			//subtract num of stopwords to get num of tokens without stopwords.
			
			tSize = subtractStopwordSize(tAnnots,tSize);
			hSize = subtractStopwordSize(hAnnots,hSize);
		} 
		
		// num of alignment links between text and hypothesis
		int numOfLinks = JCasUtil.select(hView, Link.class).size();

		// Scores: num of alignments/num of T tokens, num of alignments/num of H
		// tokens, product of the two.
		scoresVector.add((double) numOfLinks / tSize);
		scoresVector.add((double) numOfLinks / hSize);
		scoresVector.add((double) numOfLinks * numOfLinks / tSize / hSize);

		return scoresVector;
	}

	/**
	 * Update size of T or H to return size without counting stopwords.
	 * @param annots Annotations to iterate on, T or H tokens
	 * @param size Num of T or H tokens
	 * @return size without stopwords
	 */
	private int subtractStopwordSize(Collection<Token> annots, int size) {
		for (Iterator<Token> tIter = annots.iterator(); tIter.hasNext();) {
			Annotation curTAnnot = tIter.next();
			String str = curTAnnot.getCoveredText();
			if(stopWords.contains(str)) {
				size--;
			}
		}
		
		return size;
	}

	@Override
	public String getComponentName() {
		return "BagOfWordVectorScoring";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	/**
	 * 
	 * @return number of features
	 */
	public int getNumOfFeats() {
		return numOfFeats;
	}

	/**
	 * number of features
	 */
	int numOfFeats = 3;

	/**
	 * Stopwords set
	 */
	private Set<String> stopWords;

	/**
	 * whether stopwords should be removed
	 */
	private boolean removeStopWords;

	/**
	 * aligner to add alignment links between T and H.
	 */
	BagOfWordVectorAligner aligner;

	/**
	 * The logger
	 */
	public final static Logger logger = Logger
			.getLogger(BagOfWordVectorScoring.class);

}
