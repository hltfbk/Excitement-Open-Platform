package eu.excitementproject.eop.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.distance.BagOfLemmasSimilarity;
import eu.excitementproject.eop.core.component.distance.BagOfWordsSimilarity;
import eu.excitementproject.eop.core.component.distance.DistanceCalculation;
import eu.excitementproject.eop.core.component.distance.DistanceValue;
import eu.excitementproject.eop.core.component.distance.FixedWeightTokenEditDistance;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.lappoc.ExampleLAP;

/**
 * The <code>ClassificationEDA</code> class implements the <code>EDABasic</code> interface.
 * 
 * For the proof of concept, the current implementation of the classifier is like kMeans.
 * 
 * The training calculates the average scores for all the features, and store them in the <code>model</code>.
 * 
 * The testing calculates the distance between the input instance and the model scores and select the closest label.
 * 
 * @author  Rui
 */
public class ClassificationEDA implements EDABasic<ClassificationTEDecision> {

//	list of components used in this EDA
	private List<DistanceCalculation> components;
	
//	the model trained, consisting of parameter name and value pairs
	private HashMap<String, Vector<Double>> model;
	
//	training data
	private List<JCas> trainingData;

	@Override
	public void initialize(CommonConfig config) throws ConfigurationException,
			EDAException, ComponentException {
		components = new ArrayList<DistanceCalculation>();
		DistanceCalculation component = new BagOfWordsSimilarity();
		component.initialize(config);
		DistanceCalculation component1 = new FixedWeightTokenEditDistance();
		component1.initialize(config);
		DistanceCalculation component2 = new BagOfLemmasSimilarity();
		component2.initialize(config);
		components.add(component);
		components.add(component1);
		components.add(component2);
		
		model = new HashMap<String, Vector<Double>>(); // or read in the model via configuration
		
		trainingData = new ArrayList<JCas>(2); // or read in the training data via configuration
		
        ExampleLAP lap = null; 
        try 
        {
        	lap = new ExampleLAP(); 
        }
        catch (LAPException e)
        {
        	System.err.println(e.getMessage()); 
        }
		
		try {
//			JCas jcas1 = lap.generateSingleTHPairCAS("a a a","a a a", "ENTAILMENT"); 
//			JCas jcas2 = lap.generateSingleTHPairCAS("b a a", "a a a", "ENTAILMENT"); 
//			JCas jcas3 = lap.generateSingleTHPairCAS("a a b","a a a", "ENTAILMENT"); 
//			JCas jcas4 = lap.generateSingleTHPairCAS("b b b", "a a a", "NONENTAILMENT"); 
//			JCas jcas5 = lap.generateSingleTHPairCAS("b b a","a a a", "NONENTAILMENT"); 
			JCas jcas3 = lap.generateSingleTHPairCAS("The person is hired as a postdoc.","The person is hired as a postdoc.", "ENTAILMENT"); 
			JCas jcas4 = lap.generateSingleTHPairCAS("The train was uncomfortable", "the train was comfortable", "NONENTAILMENT"); 
//			trainingData.add(jcas1); 
//			trainingData.add(jcas2); 
			trainingData.add(jcas3); 
			trainingData.add(jcas4);
//			trainingData.add(jcas5);
		} catch (LAPException e)
		{
			e.printStackTrace(); 
		}
	}

	@Override
	public ClassificationTEDecision process(JCas aCas) throws EDAException,
			ComponentException {		
		Vector<Double> featureVector = new Vector<Double>();
		for (DistanceCalculation component : components) {
			DistanceValue dValue = component.calculation(aCas);
			if (null == dValue.getDistanceVector() || dValue.getDistanceVector().size() == 0) {
				featureVector.add(dValue.getDistance());
				continue;
			}
			featureVector.addAll(dValue.getDistanceVector());
		}
		
		double minDistance = 2.0d;
		String currentLabel = null;
		for (String goldAnswer : model.keySet()) {
			if (goldAnswer.startsWith("#")) {
				continue;
			}
			Vector<Double> modelScore = model.get(goldAnswer);
			double distance = calculateDistance(modelScore, featureVector);
			if (distance <= minDistance) {
				minDistance = distance;
				currentLabel = goldAnswer;
			}
		}

		String pairId = getPairID(aCas);

		if (currentLabel.equals("ENTAILMENT")) {
			return new ClassificationTEDecision(DecisionLabel.Entailment, pairId);			
		} else if (currentLabel.equals("NONENTAILMENT")) {
			return new ClassificationTEDecision(DecisionLabel.NonEntailment, pairId);			
		} else {
			return new ClassificationTEDecision(DecisionLabel.Abstain, pairId);			
		}
		
	}

	@Override
	public void shutdown() {
		components.clear();
		model.clear();
		trainingData.clear();
	}

	@Override
	public void startTraining(CommonConfig c) throws ConfigurationException,
			EDAException, ComponentException {
		for (JCas cas : trainingData) {
			String goldAnswer = getGoldLabel(cas);
			if (null == getGoldLabel(cas)) {
				continue;
			}
			Vector<Double> featureVector = new Vector<Double>();
			if (model.containsKey(goldAnswer)) {
				// update the number of instances
				Vector<Double> number = model.get("#"+goldAnswer);
				number.set(0, number.get(0)+1.0d);
				model.put("#"+goldAnswer, number);

				// update the scores
				featureVector = model.get(goldAnswer);
				int index = 0;
				for (DistanceCalculation component : components) {
					DistanceValue dValue = component.calculation(cas);
					if (null == dValue.getDistanceVector() || dValue.getDistanceVector().size() == 0) {
						featureVector.set(index, featureVector.get(index) + dValue.getDistance());
						index ++;
						continue;
					}
					for (Double value : dValue.getDistanceVector()) {
						featureVector.set(index, featureVector.get(index) + value);
						index ++;
					}
				}
			} else {
				// first count
				Vector<Double> number = new Vector<Double>();
				number.add(1.0d);
				model.put("#"+goldAnswer, number);
				
				// first score
				for (DistanceCalculation component : components) {
					DistanceValue dValue = component.calculation(cas);
					if (null == dValue.getDistanceVector() || dValue.getDistanceVector().size() == 0) {
						featureVector.add(dValue.getDistance());
						continue;
					}
					featureVector.addAll(dValue.getDistanceVector());
				}
			}
			model.put(goldAnswer, featureVector);
		}
		
		// store all the averaged scores in model
		// the key starting with "#" stores the number of training instances
		for (String goldAnswer : model.keySet()) {
			if (goldAnswer.startsWith("#")) {
				continue;
			}
			Vector<Double> featureVector = model.get(goldAnswer);
			Vector<Double> number = model.get("#"+goldAnswer);
			for (int i=0;i<featureVector.size();i++) {
				featureVector.set(i, featureVector.get(i) / number.get(0));
			}
		}
	}
	
	/**
	 * @param aCas input T-H pair
	 * @return return the pairID of the pair
	 */
	private String getPairID(JCas aCas) {
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository().getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		return p.getPairID();
	}
	
	/**
	 * @param aCas input T-H pair
	 * @return if the pair contains the gold answer, return it; otherwise, return null
	 */
	private String getGoldLabel(JCas aCas) {		
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository().getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		if (null == p.getGoldAnswer() || p.getGoldAnswer().equals("") || p.getGoldAnswer().equals("ABSTAIN")) {
			return null;
		} else {
			return p.getGoldAnswer();
		}
	}
	
	/**
	 * @param a one vector
	 * @param b the other vector
	 * @return the inner product of Vector a and b
	 */
    private double calculateDotProduct(Vector<Double> a, Vector<Double> b) {
        double sum = 0.0d;
        for (int i=0; i<a.size();i++)
            sum += a.get(i) * b.get(i);
        return sum;
    }
    
//	/**
//	 * @param a one vector
//	 * @param b the other vector
//	 * @return the sum of Vector a and b
//	 */
//    private Vector<Double> calculateSum(Vector<Double> a, Vector<Double> b) {
//    	Vector<Double> sum = new Vector<Double>();
//        for (int i=0;i<a.size();i++) {
//        	sum.add(a.get(i) + b.get(i));
//        }
//        return sum;
//    }
    
	/**
	 * @param a one vector
	 * @param b the other vector
	 * @return the difference of Vector a and b
	 */
    private Vector<Double> calculateDiff(Vector<Double> a, Vector<Double> b) {
    	Vector<Double> diff = new Vector<Double>();
        for (int i=0;i<a.size();i++) {
        	diff.add(a.get(i) - b.get(i));
        }
        return diff;
    }
    
	/**
	 * @param a one vector
	 * @param b the other vector
	 * @return the distance of Vector a and b
	 */
    private double calculateDistance(Vector<Double> a, Vector<Double> b) {
    	return calculateMagnitude(calculateDiff(a, b));
    }
    
	/**
	 * @param a the vector
	 * @return the Euclidean norm of this Vector
	 */
    private double calculateMagnitude(Vector<Double> a) {
        return Math.sqrt(calculateDotProduct(a, a));
    }
    
}
