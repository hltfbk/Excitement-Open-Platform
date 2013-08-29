package eu.excitementproject.eop.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.component.distance.DistanceCalculation;
import eu.excitementproject.eop.common.component.distance.DistanceValue;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.distance.BagOfWordsSimilarity;
import eu.excitementproject.eop.lap.PlatformCASProber;

//import eu.excitementproject.eop.core.component.distance.BagOfLemmasSimilarity;

/**
 * The <code>ClassificationEDA</code> class implements the <code>EDABasic</code>
 * interface.
 * 
 * For the proof of concept, the current implementation of the classifier is
 * like kMeans.
 * 
 * The training calculates the average scores for all the features, and store
 * them in the <code>model</code>.
 * 
 * The testing calculates the distance between the input instance and the model
 * scores and select the closest label.
 * 
 * @author Rui Wang
 * @since November 2012
 */
public class ClassificationEDA implements EDABasic<ClassificationTEDecision> {

	/**
	 * get the language flag
	 * 
	 * @return
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * get the model file
	 * 
	 * @return
	 */
	public String getModelFile() {
		return modelFile;
	}

	/**
	 * get the training data directory
	 * 
	 * @return
	 */
	public String getXmiDIR() {
		return xmiDIR;
	}

	/**
	 * list of components used in this EDA
	 */
	private List<DistanceCalculation> components;

	/**
	 * the language flag
	 */
	private String language;

	/**
	 * the model file, consisting of parameter name and value pairs
	 */
	private String modelFile;

	/**
	 * the trainding data directory
	 */
	private String xmiDIR;

	@Override
	public void initialize(CommonConfig config) throws ConfigurationException,
			EDAException, ComponentException {

		components = new ArrayList<DistanceCalculation>();
		DistanceCalculation component = new BagOfWordsSimilarity();
		// commented out, due to the failure of the test
		// DistanceCalculation component1 = new FixedWeightTokenEditDistance();
		// DistanceCalculation component2 = new BagOfLemmasSimilarity();
		components.add(component);
		// components.add(component1);
		// components.add(component2);

		language = "EN";

		modelFile = "./src/test/resources/ClassificationEDAModel" + language;

		xmiDIR = "./target/" + language + "/";
	}

	@SuppressWarnings("unchecked")
	@Override
	public ClassificationTEDecision process(JCas aCas) throws EDAException,
			ComponentException {
		// read in the model
		HashMap<String, Vector<Double>> model = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					modelFile));
			model = (HashMap<String, Vector<Double>>) in.readObject();
			in.close();
		} catch (IOException e) {
			throw new EDAException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new EDAException(e.getMessage());
		}
		if (null == model) {
			throw new EDAException("No model is loaded!");
		}

		Vector<Double> featureVector = new Vector<Double>();
		for (DistanceCalculation component : components) {
			DistanceValue dValue = component.calculation(aCas);
			Vector<Double> distanceVector = component.calculateScores(aCas);
			if (null == distanceVector || distanceVector.size() == 0) {
				featureVector.add(dValue.getDistance());
				continue;
			}
			featureVector.addAll(distanceVector);
		}

		double minDistance = 2.0d;
		String currentLabel = "ABSTAIN";
		for (final Iterator<Entry<String, Vector<Double>>> iter = model
				.entrySet().iterator(); iter.hasNext();) {
			Entry<String, Vector<Double>> entry = iter.next();
			final String goldAnswer = entry.getKey();
			if (goldAnswer.startsWith("#")) {
				continue;
			}
			final Vector<Double> modelScore = entry.getValue();
			double distance = calculateDistance(modelScore, featureVector);
			if (distance <= minDistance) {
				minDistance = distance;
				currentLabel = goldAnswer;
			}
		}

		String pairId = getPairID(aCas);

		if (currentLabel.equals("ENTAILMENT")) {
			return new ClassificationTEDecision(DecisionLabel.Entailment,
					pairId);
		} else if (currentLabel.equals("NONENTAILMENT")) {
			return new ClassificationTEDecision(DecisionLabel.NonEntailment,
					pairId);
		} else {
			return new ClassificationTEDecision(DecisionLabel.Abstain, pairId);
		}

	}

	@Override
	public void shutdown() {
		components.clear();
		modelFile = "";
		xmiDIR = "";
	}

	@Override
	public void startTraining(CommonConfig c) throws ConfigurationException,
			EDAException, ComponentException {
		JCas cas;
		// the model trained, consisting of parameter name and value pairs
		HashMap<String, Vector<Double>> model = new HashMap<String, Vector<Double>>();

		for (File xmi : new File(xmiDIR).listFiles()) {
			if (!xmi.getName().endsWith(".xmi")) {
				continue;
			}
			cas = PlatformCASProber.probeXmi(xmi, System.out);
			String goldAnswer = getGoldLabel(cas);
			if (null == getGoldLabel(cas)) {
				continue;
			}
			Vector<Double> featureVector = new Vector<Double>();
			if (model.containsKey(goldAnswer)) {
				// update the number of instances
				Vector<Double> number = model.get("#" + goldAnswer);
				number.set(0, number.get(0) + 1.0d);
				model.put("#" + goldAnswer, number);

				// update the scores
				featureVector = model.get(goldAnswer);
				int index = 0;
				for (DistanceCalculation component : components) {
					DistanceValue dValue = component.calculation(cas);
					Vector<Double> distanceVector = component
							.calculateScores(cas);
					// if (null == dValue.getDistanceVector() ||
					// dValue.getDistanceVector().size() == 0) {
					if (null == distanceVector || distanceVector.size() == 0) {
						featureVector.set(index, featureVector.get(index)
								+ dValue.getDistance());
						index++;
						continue;
					}
					for (Double value : distanceVector) {
						featureVector.set(index, featureVector.get(index)
								+ value);
						index++;
					}
				}
			} else {
				// first count
				Vector<Double> number = new Vector<Double>();
				number.add(1.0d);
				model.put("#" + goldAnswer, number);

				// first score
				for (DistanceCalculation component : components) {
					DistanceValue dValue = component.calculation(cas);
					Vector<Double> vec = component.calculateScores(cas);
					// if (null == dValue.getDistanceVector() ||
					// dValue.getDistanceVector().size() == 0) {
					if (null == vec || vec.size() == 0) {
						featureVector.add(dValue.getDistance());
						continue;
					}
					featureVector.addAll(vec);
				}
			}
			model.put(goldAnswer, featureVector);
			cas.reset();
		}

		// store all the averaged scores in model
		// the key starting with "#" stores the number of training instances
		for (final Iterator<Entry<String, Vector<Double>>> iter = model
				.entrySet().iterator(); iter.hasNext();) {
			Entry<String, Vector<Double>> entry = iter.next();
			final String goldAnswer = entry.getKey();
			if (goldAnswer.startsWith("#")) {
				continue;
			}
			final Vector<Double> featureVector = entry.getValue();
			final Vector<Double> number = model.get("#" + goldAnswer);
			for (int i = 0; i < featureVector.size(); i++) {
				featureVector.set(i, featureVector.get(i) / number.get(0));
			}
		}

		// serialize the model
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(modelFile));
			out.writeObject(model);
			out.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * @param aCas
	 *            input T-H pair
	 * @return return the pairID of the pair
	 */
	protected String getPairID(JCas aCas) {
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository()
				.getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		return p.getPairID();
	}

	/**
	 * @param aCas
	 *            input T-H pair
	 * @return if the pair contains the gold answer, return it; otherwise,
	 *         return null
	 */
	protected String getGoldLabel(JCas aCas) {
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository()
				.getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		if (null == p.getGoldAnswer() || p.getGoldAnswer().equals("")
				|| p.getGoldAnswer().equals("ABSTAIN")) {
			return null;
		} else {
			return p.getGoldAnswer();
		}
	}

	/**
	 * @param a
	 *            one vector
	 * @param b
	 *            the other vector
	 * @return the inner product of Vector a and b
	 */
	private double calculateDotProduct(Vector<Double> a, Vector<Double> b) {
		double sum = 0.0d;
		for (int i = 0; i < a.size(); i++) {
			sum += a.get(i) * b.get(i);
		}
		return sum;
	}

	// /**
	// * @param a one vector
	// * @param b the other vector
	// * @return the sum of Vector a and b
	// */
	// private Vector<Double> calculateSum(Vector<Double> a, Vector<Double> b) {
	// Vector<Double> sum = new Vector<Double>();
	// for (int i=0;i<a.size();i++) {
	// sum.add(a.get(i) + b.get(i));
	// }
	// return sum;
	// }

	/**
	 * @param a
	 *            one vector
	 * @param b
	 *            the other vector
	 * @return the difference of Vector a and b
	 */
	private Vector<Double> calculateDiff(Vector<Double> a, Vector<Double> b) {
		Vector<Double> diff = new Vector<Double>();
		for (int i = 0; i < a.size(); i++) {
			diff.add(a.get(i) - b.get(i));
		}
		return diff;
	}

	/**
	 * @param a
	 *            one vector
	 * @param b
	 *            the other vector
	 * @return the distance of Vector a and b
	 */
	private double calculateDistance(Vector<Double> a, Vector<Double> b) {
		return calculateMagnitude(calculateDiff(a, b));
	}

	/**
	 * @param a
	 *            the vector
	 * @return the Euclidean norm of this Vector
	 */
	private double calculateMagnitude(Vector<Double> a) {
		return Math.sqrt(calculateDotProduct(a, a));
	}

}
