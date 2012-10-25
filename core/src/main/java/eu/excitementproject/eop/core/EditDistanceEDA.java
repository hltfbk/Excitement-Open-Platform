
package eu.excitementproject.eop.core;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.distance.*;
import eu.excitement.type.entailment.Pair;


/**
 * The <code>EditDistanceEDA</code> class implements the <code>EDABasic</code> interface.
 * Given a certain conﬁguration, it can be trained over a speciﬁc dataset in order to optimize its
 * performance. In the training phase this class produces a distance model for the dataset, which
 * includes a distance threshold that best separates the positive and negative examples in the training data.
 * During the test phase it applies the calculated threshold, so that pairs resulting in a distance below the
 * threshold are classiﬁed as ENTAILMENT, while pairs above the threshold are classiﬁed as NONENTAILMENT.
 * <code>EditDistanceEDA</code> uses <code>FixedWeightTokenEditDistance</code> for calculating edit distance
 * between each pair of T and H. 
 * 
 * Some parts of this code have been pulled from the EDITS software: http://edits.fbk.eu/.
 *
 * <B>Not thread safe!</B>
 * 
 * @author  Roberto Zanoli
 * @version 0.1
 */
public class EditDistanceEDA<T extends TEDecision>
		implements EDABasic<IEditDistanceTEDecision> {
	
	// the threshold that has to be learnt on a training set and then used
	// to annotate examples in the test set
	private double threshold;
	// the edit distance component to be used
	private DistanceCalculation component;
	// this is a temporary training set to train and test the system
	private List<JCas> trainingSet;
	
	
	/**
	 * Construct an edit distance EDA.
	 */
	public EditDistanceEDA() {
    	
		this.threshold = 0.0;
		this.component = null;
		this.trainingSet = null;
		
    }
	
	/**
	 * Construct an edit distance EDA.
	 * It is a temporary solution to test the class providing it with a training data set
	 */
	public EditDistanceEDA(List<JCas> traininSet) {
    	
		this.threshold = 0.0;
		this.component = null;
		this.trainingSet = traininSet;
		
    }
	
	
	/* 
	 * @see EDABasic#initialize()
	 */
	public void initialize (CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		
		checkConfiguration(config);
		component = new FixedWeightTokenEditDistance();
		component.initialize(config);
		
	}
	
	
	/* 
	 * @see EDABasic#process()
	 */
	public IEditDistanceTEDecision process(JCas aCas) throws EDAException, ComponentException {
	
		String pairId = getPairId(aCas);
		
		DistanceValue distanceValue =  component.calculation(aCas);
		double distance = distanceValue.getDistance();
		
		// During the test phase the method applies the threshold, so that
		// pairs resulting in a distance below the threshold are classiﬁed as ENTAILMENT, while pairs 
		// above the threshold are classiﬁed as NONENTAILMENT.
		if (distance <= threshold)
			return new EditDistanceTEDecision(DecisionLabel.Entailment, pairId);
		
		return new EditDistanceTEDecision(DecisionLabel.NonEntailment, pairId);
		
	}
	
	
	/* 
	 * @see EDABasic#shutdown()
	 */
	public void shutdown() {
		
	}
	
	
	/* 
	 * @see EDABasic#startTraining()
	 */
	public void startTraining(CommonConfig c) throws ConfigurationException, EDAException, ComponentException {
		
		try {
			
			//it is a temporary solution to train and test the system.
			threshold = sequentialSearch(trainingSet);
			
		} catch (ConfigurationException e) {
			throw e;
		} catch (EDAException e) {
			throw e;
		} catch (ComponentException e) {
			throw e;
		} catch (Exception e) {
			throw new EDAException(e.getMessage());
		}
		
	}
	
	
	/**
     * Checks the configuration and raise exceptions if the provided
     * configuration is not compatible with this class.
     *
     * @throws ConfigurationException If an input or output exception occurred.
     */
	private void checkConfiguration(CommonConfig config) 
			throws ConfigurationException {
		
	}
	
	
	/**
     * Returns the threshold that best separates the positive and negative examples in the training data.
     * 
     * @return The threshold
     * @throws ComponentException, EDAException, Exception
     */
	private double sequentialSearch(List<JCas> casList) 
			throws ComponentException, EDAException, Exception {
		
		double threshold = 0.0;
		
		try {
		
			List<DistanceValue> distanceValueList = getDistanceValues(casList);
			List<String> entailmentValueList = getEntailmentAnnotation(casList);
			
			// the distanceValueList sorted in increasing order
			List<DistanceValue> sortedDistanceValueList = sortDistanceValues(distanceValueList);
			
			// get the smallest distance value. It is the first element of the array.
			double min = getMinimum(sortedDistanceValueList);
			// System.out.println("min:" + min);
			// get the largest distance value. It is the last element of the array.
			double max = getMaximum(sortedDistanceValueList);
			// System.out.println("max:" + max);
			// get the increment
			double increment = getIncrement(sortedDistanceValueList)/2;
			// System.out.println("increment:" + increment);
			
			double accuracy = 0.0;
			double maxAccuracy = 0.0;
			// true positive
			double tp = 0; 
			// false positive
			double fp = 0; 
			// true negative
			double tn = 0; 
			// false negative
			double fn = 0;

			// Searching the threshold begins at a lower bound (i.e. min) and
			// increments by a step size up to an upper bound (i.e. max). 
			for (double i = min; i <= max; i = i + increment) {
				for (int j = 0; j < distanceValueList.size(); j++) {
					double distanceValue = distanceValueList.get(j).getDistance();
					String entailmentValue = entailmentValueList.get(j);
					if (distanceValue <= i)
						if (entailmentValue.equals("ENTAILMENT"))
							tp = tp + 1;
						else
							fp = fp + 1;
					else
						if (entailmentValue.equals("ENTAILMENT"))
							fn = fn + 1;
						else
							tn = tn + 1;
				}
				accuracy = (tp+tn)/(tp + fp + fn + tn);
				if (accuracy >= maxAccuracy) {
					maxAccuracy = accuracy;
					threshold = i;
				}
				
			    tp = 0; 
				fp = 0; 
				tn = 0; 
				fn = 0;
						 	
			}
			
		} catch(EDAException e) {
			throw e;
		} catch(ComponentException e) {
			throw e;
		} catch(Exception e) {
			throw e;
		}
		
		return threshold;
		
	}
	
	
	/**
     * Returns the distance between the two closest elements in the specified sorted list.
     *
     * @param sortedDistanceValueList The sorted list
     * @return The distance.
     */
	private double getIncrement(List<DistanceValue> sortedDistanceValueList) {
		
		double result = Double.MAX_VALUE;
		
		for (int i = 1; i < sortedDistanceValueList.size(); i++) {
			double diff = sortedDistanceValueList.get(i).getDistance() - 
					sortedDistanceValueList.get(i-1).getDistance();
			if (diff < result)
				result = diff;
		}
		
		return result;
		
	}
	
	
	/**
     * Returns the minimum value in the specified sorted list.
     *
     * @param sortedDistanceValueList The sorted list.
     * @return The minimum.
     */
	private double getMinimum(List<DistanceValue> sortedDistanceValueList) {
		
		return sortedDistanceValueList.get(0).getDistance();
		
	}
	
	
	/**
     * Returns the maximum value in the specified sorted list.
     *
     * @param sortedDistanceValueList The sorted list.
     * @return The maximum.
     */
	private double getMaximum(List<DistanceValue> sortedDistanceValueList) {
		
		return sortedDistanceValueList.get(sortedDistanceValueList.size() - 1).getDistance();
		
	}
	
	
	/**
     * Returns the pair identifier of the pair contained in the specified CAS
     *
     * @param aCas The CAS
     * @return The pair identifier
     */
	private String getPairId(JCas aCas) {
		
		Pair p = null;
		
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository().getAllIndexedFS(Pair.type);
		while(pairIter.hasNext()) {
			p = (Pair) pairIter.next();
		}
		
		return p.getPairID();
	
	}
	
	
	/**
     * Returns a copy of the specified list sorted in increasing order from smallest
     * to largest.
     *
     * @param distanceValues The list of distance values.
     * @return A copy of the specified list sorted in increasing order.
     */
	private List<DistanceValue> sortDistanceValues(List<DistanceValue> distanceValues) {
		
		List<DistanceValue> newDistanceValues = new ArrayList<DistanceValue>(distanceValues);
		
		Collections.copy(newDistanceValues, distanceValues);
				
		Collections.sort(newDistanceValues, new Comparator<DistanceValue>(){
			 
            public int compare(DistanceValue d1,  DistanceValue d2) {
                return (d1.getDistance() > d1.getDistance() ? 1 :
                	(d1.getDistance() == d2.getDistance() ? 0 : -1));
            }
  
        });
		
		return newDistanceValues;
		
	}
	
	
	/**
     * Returns the list of distance values calculating for each of the pair T and H
     * of the specified list of Cas. Each of the Cas of the list contains a single pair T-H.
     *
     * @param aCasList The specified list of Cas.
     * @return The list of distance values.
     * @throws DistanceComponentException
     */
	private List<DistanceValue> getDistanceValues(List<JCas> aCasList)
			throws DistanceComponentException {
	
		List<DistanceValue> distanceValues = new ArrayList<DistanceValue>(aCasList.size());
		
		try {
			Iterator<JCas> iterator = aCasList.iterator();
			while (iterator.hasNext()) {
				JCas aCas = iterator.next();
				DistanceValue distanceValue = component.calculation(aCas);
				distanceValues.add(distanceValue);
			}
			
		} catch(DistanceComponentException e) {
			throw e;
		}
		
		return distanceValues;
			
	}
		
	
	/**
     * Returns the list of entailment annotations calculating of each of the pair T and H
     * of the specified list of Cas. Each of the Cas of the list contains a single pair T-H.
     *
     * @param aCasList The specified list of Cas.
     * @return The list of the annotations.
     * @throws Exception
     */
	private List<String> getEntailmentAnnotation(List<JCas> aCasList) 
			throws Exception {
			
		List<String> entailmentValueList = new ArrayList<String>(aCasList.size());
			
		try {
			
			Iterator<JCas> iterator = aCasList.iterator();
			while (iterator.hasNext()) {
				Pair p = null;
				JCas aCas = iterator.next();
				FSIterator<TOP> pairIter = aCas.getJFSIndexRepository().getAllIndexedFS(Pair.type);
				p = (Pair) pairIter.next();
				String goldAnswer = p.getGoldAnswer();
				entailmentValueList.add(goldAnswer);
			}
				
		} catch(Exception e) {
			throw e;
		}
			
		return entailmentValueList;
				
	}
			
	
	/**
	 * The pocket algorithm ia a variant of the perceptron algorithm that can be used also for non-separable data sets.
	 * It keeps the best solution seen so far "in its pocket". The pocket algorithm then returns the solution in the pocket, 
	 * rather than the last solution.
	 *
	 * @param tDeleted Token deleted.
	 * @return Weight of deleting token.
	 */
	public double[] pocketAlgortihm() {
		
		double threshold = 0.5;
		int maxNumberOfIterations = 1000;
		double learning_rate = 0.01;
		
		double[][] training = {{1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}};
		int[] annotation = {0, 0, 0, 1, 1};
		
		int bestRunLength = 0;
        int currentRunLength = 0;
		double[] bestWeights = {0, 0};
		double[] weights = {0, 0};
		int k = 0;
		double error = 0.0;
		double result;
		
		while (true) {
			k = k + 1;
			int errorCount = 0;
			for (int i = 0; i < training.length; i++) {
				if (sum(training[i], weights) > threshold)
					result = 1.0;
				else
					result = 0.0;
				error = annotation[i] - result;
				if (error != 0.0) {
					errorCount = errorCount + 1;
					for (int j = 0; j < training[i].length; j++) {
						double value = training[i][j];
						weights[j] = weights[j] + learning_rate * error * value;
					}
				}           
				else {
					currentRunLength = currentRunLength + 1;
					if (bestRunLength < currentRunLength) {
						bestRunLength = currentRunLength;
						currentRunLength = 0;
						bestWeights = Arrays.copyOf(weights, weights.length);
					}
				}
				
			}
						
			if (errorCount == 0 || k > maxNumberOfIterations)
				break;
			
		}
		
		return bestWeights;
		
	}
	
	
	/**
     * Returns the constant weight of deleting the specified token.
     *
     * @param tDeleted Token deleted.
     * @return Weight of deleting token.
     */
	private double sum(double[] trainingExample, double[] weights) {
		
		double sum = 0;
		
		for (int i = 0; i < trainingExample.length; i++) {
			
			sum = sum + trainingExample[i] * weights[i];
			
		}
		
		return sum;
		
	}
	
	// public static void main(String[] args) {
		
		
	// 	EditDistanceEDA edit = new EditDistanceEDA();
	// 	CommonConfig config = null;
		
	// 	try {
			
	// 		edit.initialize(config);
	// 		System.out.println("training ...");
	// 		edit.startTraining(config);
	// 		System.out.println("calculated threshold:" + edit.threshold);
	
	// 		String t = "The train was unconfortable.";
	// 		String h = "The train was expensive.";
		
	// 		CasCreation cas1 = new CasCreation(t, h, "NONENTAILMENT");
			
	// 		System.out.println("annotating:");
	// 		System.out.println("T:" + t);
	// 		System.out.println("H:" + h);
	// 		System.out.println("decision:" + edit.process(cas1.create()).getDecision());
		
	// 	}catch(Exception e) {
	// 		e.printStackTrace();
	// 	}
		
	// }
	
}
