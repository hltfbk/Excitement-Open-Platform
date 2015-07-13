package eu.excitementproject.eop.core.component.scoring;

import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.alignment.vectorlink.BagOfChunkVectorAligner;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

public class BagOfChunkVectorScoring implements ScoringComponent {

	public BagOfChunkVectorScoring(CommonConfig config)
			throws ConfigurationException, IOException {

		this.aligner = new BagOfChunkVectorAligner(config);

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

		// num of Chunks in text
		int tSize = JCasUtil.select(tView, Chunk.class).size();
		if (0 == tSize) {
			logger.warn("No chunks found for text.");
		}

		// num of Chunks in hypothesis
		int hSize = JCasUtil.select(hView, Chunk.class).size();
		if (0 == hSize) {
			logger.warn("No chunks found for hypothesis");
		}

		// num of alignment links between text and hypothesis
		int numOfLinks = JCasUtil.select(hView, Link.class).size();

		// Scores: num of alignments/num of T chunks, num of alignments/num of H
		// chunks, product of the two.
		scoresVector.add((double) numOfLinks / tSize);
		scoresVector.add((double) numOfLinks / hSize);
		scoresVector.add((double) numOfLinks * numOfLinks / tSize / hSize);

		return scoresVector;
	}

	@Override
	public String getComponentName() {
		return "BagOfChunkVectorAligner";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	public int getNumOfFeats() {
		return numOfFeats;
	}

	int numOfFeats = 3;

	BagOfChunkVectorAligner aligner;

	public final static Logger logger = Logger
			.getLogger(BagOfChunkVectorScoring.class);

}
