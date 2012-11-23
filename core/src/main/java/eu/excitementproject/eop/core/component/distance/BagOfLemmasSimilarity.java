package eu.excitementproject.eop.core.component.distance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.core.component.scoring.ScoringComponentException;

public class BagOfLemmasSimilarity extends BagOfWordsSimilarity {

	@Override
	public DistanceValue calculation(JCas aCas)
			throws DistanceComponentException {
//		(1 - (T&H/H))
		double distance = 0.0d;
		
//		(T&H/H)
		double unnormalized = 0.0d;
		
//		all the values: (T&H/H), (T&H/T), and ((T&H/H)*(T&H/T))
		Vector<Double> distanceVector = new Vector<Double>();
		
		try {
			JCas tView = aCas.getView("TextView");
	    	HashMap<String, Integer> tBag = countTokens(tView);
	    	
			JCas hView = aCas.getView("HypothesisView");
	    	HashMap<String, Integer> hBag = countTokens(hView);

	    	distanceVector.addAll(calculateSimilarity(tBag, hBag));
	    	unnormalized = distanceVector.get(0);
	    	distance = 1.0d - unnormalized;
		}
		catch (CASException e) {
			throw new DistanceComponentException(e.getMessage());
		}

		// vector returning capability has moved out from DistanceValue to 
		// interface ScoringComponent (method calculateScores())   -- Gil 
		//return new BoWSimilarityValue(distance, unnormalized, distanceVector);
		return new BoWSimilarityValue(distance, unnormalized); 
	}

	// made-up from calculation, to support super class ScoringComponent 
	// -- Gil 
	@Override
	public Vector<Double> calculateScores(JCas aCas)
			throws ScoringComponentException {
//		(1 - (T&H/H))
//		double distance = 0.0d;
		
//		(T&H/H)
//		double unnormalized = 0.0d;
		
//		all the values: (T&H/H), (T&H/T), and ((T&H/H)*(T&H/T))
		Vector<Double> distanceVector = new Vector<Double>();
		
		try {
			JCas tView = aCas.getView("TextView");
	    	HashMap<String, Integer> tBag = countTokens(tView);
	    	
			JCas hView = aCas.getView("HypothesisView");
	    	HashMap<String, Integer> hBag = countTokens(hView);

	    	distanceVector.addAll(calculateSimilarity(tBag, hBag));
//	    	unnormalized = distanceVector.get(0);
//	    	distance = 1.0d - unnormalized;
		}
		catch (CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return distanceVector; 

	}

    /**
     * Count the tokens contained in a text and store the counts in a HashMap
     * 
     * @param text the input text represented in a JCas
     * @return a HashMap represents the bag of tokens contained in the text, in the form of <Token, Frequency>
     */
	private HashMap<String, Integer> countTokens(JCas text) {
		HashMap<String, Integer> tokenNumMap = new HashMap<String, Integer>();
    	Iterator<Annotation> tokenIter = text.getAnnotationIndex(Token.type).iterator();
    	while(tokenIter.hasNext()) {
    		Token curr = (Token) tokenIter.next();
    		String tokenText = curr.getLemma().getValue();
    		Integer num = tokenNumMap.get(tokenText);
    		if (null == num) {
    			tokenNumMap.put(tokenText, 1);
    		} else {
    			tokenNumMap.put(tokenText, num + 1);
    		}
    	}
		return tokenNumMap;
	}
}
