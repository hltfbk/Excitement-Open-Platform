
package eu.excitementproject.eop.core;

//import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import java.io.*;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.IEditDistanceTEDecision;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.component.distance.DistanceCalculation;
import eu.excitementproject.eop.common.component.distance.DistanceComponentException;
import eu.excitementproject.eop.common.component.distance.DistanceValue;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.distance.*;
import eu.excitement.type.entailment.Pair;
//import eu.excitementproject.eop.lap.lappoc.ExampleLAP;
//import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;



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
	
	static Logger logger = Logger.getLogger(EditDistanceEDA.class
			.getName());

	// whether it's training or testing
	protected boolean isTrain;
	// language flag
	
	protected String language;

	// the model file, consisting of parameter name and value pairs
	protected String modelFile;

	// training data directory
	protected String trainDIR;

	// testing data directory
	protected String testDIR;

	public boolean isTrain() {
		return this.isTrain;
	}

	public void setTrain(boolean isTrain) {
		this.isTrain = isTrain;
	}
	
	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getModelFile() {
		return this.modelFile;
	}

	public String getTrainDIR() {
		return this.trainDIR;
	}

	public String getTestDIR() {
		return this.testDIR;
	}

	public double getThreshold() {
		return this.threshold;
	}
	
	
	/**
	 * Construct an edit distance EDA.
	 */
	public EditDistanceEDA() {
    	
		this.threshold = -1.0;
		this.component = null;
		
    }

	
	/* 
	 * @see EDABasic#initialize()
	 */
	public void initialize (CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		
		//ExampleLAP lap = null; 
		
        try {
        	
        	//modelFile = "./src/test/resources/EditDistanceEDA"
    			//+ language;

    		//trainDIR = "./target/" + language + "/dev/";
    		//testDIR = "./target/" + language + "/test/";

    		// initialize the model: if it's training, check the model file exsits;
    		// if it's testing, read in the model
    		
        	// add 2 examples in the training set; it is a temporary solution to have a training set
        	// for training the algorithm.
    		/*
        	lap = new ExampleLAP();
			JCas jcas1 = lap.generateSingleTHPairCAS("The person is hired as a postdoc.","The person is hired as a postdoc.", "ENTAILMENT"); 
			JCas jcas2 = lap.generateSingleTHPairCAS("The train was uncomfortable", "The train was comfortable", "NONENTAILMENT"); 
			trainingSet = new ArrayList<JCas>(2);
			trainingSet.add(jcas1); 
			trainingSet.add(jcas2);
			*/
    		
			checkConfiguration(config);
			
			//File f = new File("./src/test/resources/example_of_configuration_file.xml");
			//ImplCommonConfig commonConfig = new ImplCommonConfig(f);
			
			NameValueTable nameValueTable = config.getSection(this.getClass().getCanonicalName());
			
			modelFile = nameValueTable.getString("modelFile");
			//training or test
			
			trainDIR = nameValueTable.getString("trainDir");
			
			testDIR = nameValueTable.getString("testDir");
			
			//nameValueTable = config.getSection("FixedWeightTokenEditDistance");
			component = new FixedWeightTokenEditDistance(config);
			
		} catch (ConfigurationException e) {
			throw e;
		} catch (ComponentException e) {
			throw e;
		} catch (Exception e) {
			throw new EDAException(e.getMessage());
		}
		
	}
	
	
	/* 
	 * @see EDABasic#process()
	 */
	public IEditDistanceTEDecision process(JCas jcas) throws EDAException, ComponentException {
		
		try {
			if (threshold == -1.0) {
				//System.err.println("loading model ...");
				threshold = loadModel(new File(modelFile));
				//System.err.println("done.");
			}
		} catch(IOException e) {
			throw new EDAException(e.getMessage());
		}
			
		String pairId = getPairId(jcas);
		
		DistanceValue distanceValue =  component.calculation(jcas);
		double distance = distanceValue.getDistance();
		
		// System.err.println("distance:" + distance);
		
		// During the test phase the method applies the threshold, so that
		// pairs resulting in a distance below the threshold are classiﬁed as ENTAILMENT, while pairs 
		// above the threshold are classiﬁed as NONENTAILMENT.
		if (distance <= threshold)
			return new EditDistanceTEDecision(DecisionLabel.Entailment, pairId, threshold - distance);
		
		return new EditDistanceTEDecision(DecisionLabel.NonEntailment, pairId, distance - threshold);
		
	}
	
	
	/* 
	 * @see EDABasic#shutdown()
	 */
	public void shutdown() {
		
		if (component.getComponentName().equals("FixedWeightTokenEditDistance"))
			((FixedWeightTokenEditDistance)component).shutdown();
		
	}
	
	
	/* 
	 * @see EDABasic#startTraining()
	 */
	public void startTraining(CommonConfig c) throws ConfigurationException, EDAException, ComponentException {
		
		try {
			logger.info("The trained model will be stored in "
					+ modelFile);
			logger.info("Start training ...");
			//threshold = loadModel(new File(modelFile));
			List<DistanceValue> distanceValueList = new ArrayList<DistanceValue>();
			List<String> entailmentValueList = new ArrayList<String>();
			
			for (File xmi : (new File(trainDIR)).listFiles()) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				getDistanceValues(cas, distanceValueList);
				getEntailmentAnnotation(cas, entailmentValueList);
				//System.err.println(distanceValueList.size());
			}
			
			threshold = sequentialSearch(distanceValueList, entailmentValueList);
			
			saveModel(new File(modelFile), threshold);
			// System.err.println("threshold:" + threshold);
			
			logger.info("done.");
			
		} catch (ConfigurationException e) {
			throw e;
		} catch (EDAException e) {
			throw e;
		} catch (ComponentException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
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
	private double sequentialSearch(List<DistanceValue> distanceValueList, List<String> entailmentValueList) 
			throws ComponentException, EDAException, Exception {
		
		//System.err.println("sequential search ...");
		
		double threshold = 0.0;
		
		try {
		
			//List<DistanceValue> distanceValueList = getDistanceValues(jcasList);
			//List<String> entailmentValueList = getEntailmentAnnotation(jcasList);
			
			// the distanceValueList sorted in increasing order
			List<DistanceValue> sortedDistanceValueList = sortDistanceValues(distanceValueList);
			
			//System.err.println(sortedDistanceValueList.get(0).getDistance());
			//System.err.println(sortedDistanceValueList.get(1).getDistance());
			//System.err.println(sortedDistanceValueList.get(2).getDistance());
			//System.err.println(sortedDistanceValueList.get(3).getDistance());
			//System.exit(0);
			
			// get the smallest distance value. It is the first element of the array.
			double min = getMinimum(sortedDistanceValueList);
			// System.err.println("min:" + min);
			// get the largest distance value. It is the last element of the array.
			double max = getMaximum(sortedDistanceValueList);
			// System.err.println("max:" + max);
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
			//System.err.println("min:" + min + "\t" + "max:" +max + "\t" + "increment:" + increment);
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
			
			//System.err.println(maxAccuracy);
			//System.err.println(threshold);
			
		//} catch(EDAException e) {
			//throw e;
		//} catch(ComponentException e) {
			//throw e;
		} catch(Exception e) {
			throw e;
		}
		
		//System.err.println("done.");
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
			if (diff != 0 && diff < result)
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
	private String getPairId(JCas jcas) {
		
		Pair p = null;
		
		FSIterator<TOP> pairIter = jcas.getJFSIndexRepository().getAllIndexedFS(Pair.type);
		
		if (pairIter.hasNext())
			p = (Pair)pairIter.next();
		
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
                return (d1.getDistance() > d2.getDistance() ? 1 :
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
	private void getDistanceValues(JCas jcas, List<DistanceValue> distanceValues)
			throws DistanceComponentException {
	
		try {
			
				DistanceValue distanceValue = component.calculation(jcas);
				//System.err.println(distanceValue.getDistance());
				distanceValues.add(distanceValue);
			
		} catch(DistanceComponentException e) {
			throw e;
		}
			
	}
		
	
	/**
     * Returns the list of entailment annotations calculating of each of the pair T and H
     * of the specified list of Cas. Each of the Cas of the list contains a single pair T-H.
     *
     * @param aCasList The specified list of Cas.
     * @return The list of the annotations.
     * @throws Exception
     */
	private void getEntailmentAnnotation(JCas jcas, List<String> entailmentValueList) 
			throws Exception {
			
		try {
			
				Pair p = null;
				FSIterator<TOP> pairIter = jcas.getJFSIndexRepository().getAllIndexedFS(Pair.type);
				p = (Pair) pairIter.next();
				String goldAnswer = p.getGoldAnswer();
				entailmentValueList.add(goldAnswer);
			
				
		} catch(Exception e) {
			throw e;
		}
			
				
	}
			
	
	/**
	 * The pocket algorithm ia a variant of the perceptron algorithm that can be used also for non-separable data sets.
	 * It keeps the best solution seen so far "in its pocket". The pocket algorithm then returns the solution in the pocket, 
	 * rather than the last solution.
	 *
	 * @param tDeleted Token deleted.
	 * @return Weight of deleting token.
	 */
	public double[] pocketAlgortihm(List<JCas> jcasList) 
			throws ComponentException, EDAException, Exception {
		
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
	
	
	private double loadModel(File modelFile) throws IOException {
		
		double result = -1.0;
		
		BufferedReader reader = null; 
		
		try {
			
			reader = new BufferedReader(new FileReader(modelFile));
			String line = reader.readLine();
			//lettura delle linee del file
			while (line != null) {
				result = Double.parseDouble(line);
				break;
			}
		
		} catch (Exception e) {
			//System.err.println(e.getMessage());
			throw new IOException(e.getMessage());
		} finally { 
			if (reader != null)
				reader.close();
		}
	
		return result;
		
	}
	
	
	public void saveModel(File modelFile, double threshold) throws IOException {
    	
		//System.err.println("save model:" + modelFile.getCanonicalPath());
		
    	BufferedWriter writer = null;
    	
    	try {
    		
    		//creo un oggetto FileWriter...
	    	// ... che incapsulo in un BufferedWriter...
	    	writer = new BufferedWriter(new FileWriter(modelFile));
	    	// ... che incapsulo in un PrintWriter
	    	PrintWriter printout = new PrintWriter(writer);
	    	printout.print(threshold);
	    	printout.close();
	    	
    	} catch (Exception e) {
    		//System.err.println(e.getMessage());
    		throw new IOException(e.getMessage());
    	} finally {
    		if (writer != null)
    			writer.close();
    	}

    }
	
	
    // public static void main(String[] args) {
		
		
		// EditDistanceEDA edit = new EditDistanceEDA();
		// double[] result = edit.pocketAlgortihm();
		// System.err.println(result[0] + " " + result[1]);
		
		
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
		
	//}
	
}
