package eu.excitementproject.eop.core.component.distance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.scoring.ScoringComponentException;

/**
 * The <code>BagOfWordsSimilarity</code> class implements the DistanceCalculation interface.
 * It takes a T-H pair as input, represents each text into a bag of tokens (store it in a HashMap), and calculate several similarity scores of the pair.
 * 
 * The HashMap takes the token text as key, and its frequency in the sentence as value.
 * 
 * The similarity scores include:
 * 1) the ratio between the number of overlapping tokens and the number of tokens in H;
 * 2) the ratio between the number of overlapping tokens and the number of tokens in T;
 * 3) the product of the above two
 * 
 * The main distance value is 1) after normalization
 * 
 * @author  Rui
 */
public class BagOfWordsSimilarity implements DistanceCalculation {

	public BagOfWordsSimilarity() {
		super();
	}
	
    //@Override //Gil: initialize is removed from interface Component
	public void initialize(CommonConfig config) throws ConfigurationException,
			ComponentException {
		
	}

	@Override
	public String getComponentName() {
		return "BagOfWordsSimilarity";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

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

		//return new BoWSimilarityValue(distance, unnormalized, distanceVector);
		// vector retuning moved into calculateScores() - Gil 
		return new BoWSimilarityValue(distance, unnormalized); 
	}
	
	@Override
	public Vector<Double> calculateScores(JCas aCas)
			throws ScoringComponentException {
		// Generated this vector retuning from previous distance calculation() vector part. 
		// - Gil 
		
//		(1 - (T&H/H))
		//double distance = 0.0d;
		
//		(T&H/H)
		//double unnormalized = 0.0d;
		
//		all the values: (T&H/H), (T&H/T), and ((T&H/H)*(T&H/T))
		Vector<Double> distanceVector = new Vector<Double>();
		
		try {
			JCas tView = aCas.getView("TextView");
	    	HashMap<String, Integer> tBag = countTokens(tView);
	    	
			JCas hView = aCas.getView("HypothesisView");
	    	HashMap<String, Integer> hBag = countTokens(hView);

	    	distanceVector.addAll(calculateSimilarity(tBag, hBag));
	    	//unnormalized = distanceVector.get(0);
	    	//distance = 1.0d - unnormalized;
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
    		String tokenText = curr.getCoveredText();
    		Integer num = tokenNumMap.get(tokenText);
    		if (null == num) {
    			tokenNumMap.put(tokenText, 1);
    		} else {
    			tokenNumMap.put(tokenText, num + 1);
    		}
    	}
		return tokenNumMap;
	}
	
    /**
     * Calculate the similarity between two bags of tokens
     * 
     * @param tBag the bag of tokens of T stored in a HashMap
     * @param hBag the bag of tokens of H stored in a HashMap
     * @return a vector of double values, which contains: 1) the ratio between the number of overlapping tokens and the number of tokens in H; 2) the ratio between the number of overlapping tokens and the number of tokens in T; 3) the product of the above two
     */
	protected Vector<Double> calculateSimilarity(HashMap<String, Integer> tBag, HashMap<String, Integer> hBag) {
		double sum = 0.0d;
		int hSize = 0;
		int tSize = 0;
		for (String hToken : hBag.keySet()) {
			hSize += hBag.get(hToken).intValue();
			if (!tBag.keySet().contains(hToken)) {
				continue;
			}
			sum += Math.min(hBag.get(hToken).intValue(), tBag.get(hToken).intValue());
		}
		for (String tToken : tBag.keySet()) {
			tSize += tBag.get(tToken).intValue();
		}
		Vector<Double> returnValue = new Vector<Double>();
		returnValue.add(sum / hSize);
		returnValue.add(sum / tSize);
		returnValue.add(sum * sum / hSize / tSize);
		return returnValue;
	}
	
    /**
     * BoWSimilarityValue extends DistanceValue
     */
    protected class BoWSimilarityValue extends DistanceValue {

    	public BoWSimilarityValue(double distance, double rawValue)
    	{
    		//super(distance, true, rawValue, null); // vector removed from DistanceValue - Gil 
    		super(distance,true,rawValue); 
    	}
    
    	// Feature vector removed from DistanceValue - Gil 
    	//public BoWSimilarityValue(double distance, double rawValue, Vector<Double> distanceVector)
    	//{
    	//	super(distance, true, rawValue, distanceVector); 
    	//}
    	
    }

}
