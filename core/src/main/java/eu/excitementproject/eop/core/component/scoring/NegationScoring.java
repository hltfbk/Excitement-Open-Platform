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
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Calculates number of negation terms in H and T, and use the ratio of
 * normalized negation counts as score.
 * 
 * @author Madhumita
 * @since July 2015
 *
 */
public class NegationScoring implements ScoringComponent {

	public NegationScoring(CommonConfig config) throws ConfigurationException {
		NameValueTable comp = config.getSection("NegationScoring");

		this.negWords = new HashSet<String>();

		// Read negation words from file and add them to a set.
		try {
			for (String str : (Files.readAllLines(
					Paths.get(comp.getString("negWordPath")),
					Charset.forName("UTF-8"))))
				this.negWords.add(str.toLowerCase());
		} catch (IOException e1) {
			logger.error("Could not read negation words file");
		}

	}

	@Override
	public String getComponentName() {
		return "NegationScoring";
	}

	@Override
	public String getInstanceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<Double> calculateScores(JCas cas)
			throws ScoringComponentException {

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

		logger.info("Counting negation terms in Text");
		double tNegNormCount = countNeg(tView);
		
		logger.info("Counting negation terms in Hypo");
		double hNegNormCount = countNeg(hView);

		if(0 == tNegNormCount)
			scoresVector.add(hNegNormCount);
		else if(0 == hNegNormCount)
			scoresVector.add(tNegNormCount);
		else
			scoresVector.add(hNegNormCount/tNegNormCount);
		return scoresVector;
	}

	private double countNeg(JCas view) throws ScoringComponentException {

		// Get tokens in given view.
		Collection<Token> annots = JCasUtil.select(view, Token.class);
				
		// num of token annotations in total
		int size = annots.size();
		if (0 == size) {
			throw new ScoringComponentException("No tokens found.");
		}

		double negCount = 0d;
		
		for(Iterator<Token> iter = annots.iterator(); iter.hasNext();) {
			if(negWords.contains(iter.next().getCoveredText().toLowerCase()))
				negCount++;
		}
		
		negCount/=size;
		
		return negCount;
	}

	/**
	 * @return number of features
	 */
	public int getNumOfFeats() {
		return numOfFeats;
	}

	/**
	 * number of features
	 */
	int numOfFeats = 1;

	/**
	 * Negation words set
	 */
	private Set<String> negWords;

	/**
	 * The logger
	 */
	public final static Logger logger = Logger.getLogger(NegationScoring.class);

}
