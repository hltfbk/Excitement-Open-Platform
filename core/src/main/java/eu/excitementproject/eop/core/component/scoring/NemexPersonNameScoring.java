package eu.excitementproject.eop.core.component.scoring;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.alignment.nemex.NemexPersonNameAligner;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

public class NemexPersonNameScoring implements ScoringComponent {

	public NemexPersonNameScoring(CommonConfig config)
			throws ConfigurationException {

		this.aligner = new NemexPersonNameAligner(config);

		// Load the NE model for person name
		loadPersonNameModel(config);
	}

	/**
	 * Load the trained opennlp model file to identify person names
	 * 
	 * @param config
	 *            the configuration file
	 * @throws ConfigurationException
	 */
	private void loadPersonNameModel(CommonConfig config)
			throws ConfigurationException {

		NameValueTable comp = config.getSection("NemexPersonNameScoring");
		String modelName = comp.getString("personNameModelPath");

		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream(modelName);
		} catch (FileNotFoundException e1) {
			logger.warn("Please specify the correct model path for person name recognition");
		}

		try {
			this.model = new TokenNameFinderModel(modelIn);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

	}

	/**
	 * Returns the number of feature values in the scorer.
	 * 
	 * @return number of features
	 */

	public int getNumOfFeats() {
		return numOfFeats;
	}

	@Override
	public String getComponentName() {
		return "NemexPersonNameScoring";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	@Override
	public Vector<Double> calculateScores(JCas cas)
			throws ScoringComponentException {
		// all the values: (T&H/H), (T&H/T), and ((T&H/H)*(T&H/T))

		Vector<Double> scoresVector = new Vector<Double>();

		try {
			this.aligner.annotate(cas);

			JCas tView = cas.getView(LAP_ImplBase.TEXTVIEW);
			JCas hView = cas.getView(LAP_ImplBase.HYPOTHESISVIEW);

			if (null != tView && null != hView) {
				scoresVector.addAll(calculateSimilarity(tView, hView));
			}

		} catch (PairAnnotatorComponentException | CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;

	}

	/**
	 * 
	 * @param tView
	 * @param hView
	 * @return vector of feature scores
	 */
	private Vector<Double> calculateSimilarity(JCas tView, JCas hView) {
		Collection<Token> tTokens = JCasUtil.select(tView, Token.class);
		int numOfTTokens = tTokens.size();

		if (numOfTTokens == 0) {
			logger.warn("No tokens found for TEXT");
		}

		Collection<Token> hTokens = JCasUtil.select(hView, Token.class);
		int numOfHTokens = hTokens.size();

		if (numOfHTokens == 0) {
			logger.warn("No tokens found for HYPOTHESIS");
		}

		Collection<Link> links = null;

		links = JCasUtil.select(hView, Link.class);

		double numOfLinks = links.size();

		if (0 == links.size())
			logger.warn("No alignment links found");

		Vector<Double> returnValue = new Vector<Double>();

		// Recognize names in text
		Span[] tNameSpan = identifyName(tView);
		Span[] hNameSpan = identifyName(hView);

		if(0==tNameSpan.length)
			returnValue.add(0d);
		else 
			returnValue.add(numOfLinks / tNameSpan.length);
		
		if(0 == hNameSpan.length)
			returnValue.add(0d);
		else
			returnValue.add(numOfLinks / hNameSpan.length);
		
		if(0 == tNameSpan.length || 0 == hNameSpan.length)
			returnValue.add(0d);
		else
			returnValue.add(numOfLinks * numOfLinks / tNameSpan.length
				/ hNameSpan.length);

		return returnValue;
	}

	/**
	 * Identify all occuring names
	 * 
	 * @param view
	 *            view to identify names from
	 * @return span of names wrt token indices
	 */
	private Span[] identifyName(JCas view) {
		NameFinderME nameFinder = new NameFinderME(model);

		// get all tokens in given view
		Collection<Token> tokens = JCasUtil.select(view, Token.class);

		String[] tokenStr = new String[tokens.size()];

		int i = 0;
		for (Iterator<Token> iter = tokens.iterator(); iter.hasNext();) {
			tokenStr[i++] = iter.next().getCoveredText();
		}

		Span nameSpans[] = nameFinder.find(tokenStr);

		return nameSpans;
	}

	private int numOfFeats = 3;

	private NemexPersonNameAligner aligner;

	private TokenNameFinderModel model;

	public final static Logger logger = Logger
			.getLogger(NemexPersonNameScoring.class);

}
