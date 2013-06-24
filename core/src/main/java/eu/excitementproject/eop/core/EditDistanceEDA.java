package eu.excitementproject.eop.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import java.io.*;
import java.lang.reflect.Constructor;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
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
		implements EDABasic<EditDistanceTEDecision> {
	
	/**
	 * the threshold that has to be learnt on a training set and then used
	 * to annotate examples in the test set
	 */
	private double threshold;
	
	/**
	 * the edit distance component to be used
	 */
	private DistanceCalculation component;
	
	/**
	 * the logger
	 */
	static Logger logger = Logger.getLogger(EditDistanceEDA.class.getName());

	/**
	 * the language
	 */
	protected String language;

	/**
	 * the model file
	 */
	protected String modelFile;

	/**
	 * the training data directory
	 */
	protected String trainDIR;

	/**
	 * the test data directory
	 */
	protected String testDIR;


	/**
	 * get the language
	 * 
	 * @return the language
	 */
	public String getLanguage() {
		
		return this.language;
		
	}

	
	/**
	 * set the language
	 * 
	 * @param language the language
	 * 
	 * @return
	 */
	public void setLanguage(String language) {
		
		this.language = language;
		
	}

	
	/**
	 * get the model file
	 * 
	 * @return
	 */
	public String getModelFile() {
		
		return this.modelFile;
		
	}

	
	/**
	 * get the training data directory
	 * 
	 * @return the training directory
	 */
	public String getTrainDIR() {
		
		return this.trainDIR;
		
	}

	
	/**
	 * get the test data directory
	 * 
	 * @return
	 */
	public String getTestDIR() {
		
		return this.testDIR;
		
	}
	

	/**
	 * get the threshold value
	 * 
	 * @return the threshold
	 */
	public double getThreshold() {
		
		return this.threshold;
		
	}
	
	
	/**
	 * Construct an edit distance EDA.
	 */
	public EditDistanceEDA() {
    	
		logger.info("EditDistanceEDA()");
		this.threshold = -1.0;
		this.component = null;
		
    }

	
	@Override
	public void initialize(CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		
        try {
        	
        	logger.info("initialize()");
        	//logger.info("config:");
        	//logger.info(config.toString());
        	
			checkConfiguration(config);
			
			NameValueTable nameValueTable = config.getSection(this.getClass().getCanonicalName());
			//setting the training directory
			if (trainDIR == null)
				trainDIR = nameValueTable.getString("trainDir");
			logger.info("training directory: " + trainDIR);
			//setting the test directory
			if (testDIR == null)
				testDIR = nameValueTable.getString("testDir");
			logger.info("test directory: " + testDIR);
			//FixedWeightTokenEditDistance component initialization
			String componentName  = nameValueTable.getString("components");
			
			if (component == null) {
				try {
					Class<?> componentClass = Class.forName(componentName);
					Constructor<?> componentClassConstructor = componentClass.getConstructor(CommonConfig.class);
					component = (DistanceCalculation) componentClassConstructor.newInstance(config);
					//component = new FixedWeightTokenEditDistance(config);
					logger.info("component name: " + component.getComponentName());
				} catch (Exception e) {
					throw new ComponentException(e.getMessage());
				}
			}
			//setting the model file
			if (modelFile == null)
				modelFile = nameValueTable.getString("modelFile") + "_" + component.getInstanceName();
			logger.info("model file name: " + modelFile);
			
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new EDAException(e.getMessage());
		}
		
	}
	
	
	@Override
	public EditDistanceTEDecision process(JCas jcas) throws EDAException, ComponentException {
		
		try {
			
			if (threshold == -1.0) {
				
				threshold = loadModel(new File(modelFile));
				
			}
			
		} catch(IOException e) {
			throw new EDAException(e.getMessage());
		}
			
		String pairId = getPairId(jcas);
		
		DistanceValue distanceValue =  component.calculation(jcas);
		double distance = distanceValue.getDistance();
		
		// During the test phase the method applies the threshold, so that
		// pairs resulting in a distance below the threshold are classiﬁed as ENTAILMENT, while pairs 
		// above the threshold are classiﬁed as NONENTAILMENT.
		if (distance <= threshold)
			return new EditDistanceTEDecision(DecisionLabel.Entailment, pairId, threshold - distance);
		
		return new EditDistanceTEDecision(DecisionLabel.NonEntailment, pairId, distance - threshold);
		
	}
	
	
	@Override
	public void shutdown() {
		
		logger.info("shutdown()");
		
		if (component.getComponentName().equals("FixedWeightTokenEditDistance"))
			((FixedWeightTokenEditDistance)component).shutdown();
		else if (component.getComponentName().equals("FixedWeightLemmaEditDistance"))
			((FixedWeightLemmaEditDistance)component).shutdown();
		
		component = null;
		modelFile = null;
		trainDIR = null;
		testDIR = null;
		threshold = -1.0;
	}
	
	
	@Override
	public void startTraining(CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		
		logger.info("startTraining()");
		
		try {
			
			initialize(config);
			
			List<DistanceValue> distanceValueList = new ArrayList<DistanceValue>();
			List<String> entailmentValueList = new ArrayList<String>();
			
			for (File xmi : (new File(trainDIR)).listFiles()) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				getDistanceValues(cas, distanceValueList);
				getEntailmentAnnotation(cas, entailmentValueList);
			}
			
			threshold = sequentialSearch(distanceValueList, entailmentValueList);
			
			saveModel(new File(modelFile), threshold);
			
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
     * configuration is not compatible with this class
     * 
     * param config the configuration
     *
     * @throws ConfigurationException
     */
	private void checkConfiguration(CommonConfig config) 
			throws ConfigurationException {
		

		logger.info("checkConfiguration()");
		
	}
	
	
	/**
     * Returns the threshold that best separates the positive and negative examples in the training data
     * 
     * @return the threshold
     * 
     * @throws ComponentException, EDAException, Exception
     */
	private double sequentialSearch(List<DistanceValue> distanceValueList, List<String> entailmentValueList) 
			throws ComponentException, EDAException, Exception {
		
		double threshold = 0.0;
		
		try {
		
			List<DistanceValue> sortedDistanceValueList = sortDistanceValues(distanceValueList);
			
			// get the smallest distance value. It is the first element of the array.
			double min = getMinimum(sortedDistanceValueList);
			// get the largest distance value. It is the last element of the array.
			double max = getMaximum(sortedDistanceValueList);
			// get the increment
			double increment = getIncrement(sortedDistanceValueList)/2;
			
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
			
		} catch(Exception e) {
			throw e;
		}
		
		return threshold;
		
	}
	
	
	/**
     * Returns the distance between the two closest elements in the specified sorted list
     *
     * @param sortedDistanceValueList the sorted list
     * 
     * @return the distance
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
     * Returns the minimum value in the specified sorted list
     *
     * @param sortedDistanceValueList the sorted list
     * 
     * @return the minimum
     */
	private double getMinimum(List<DistanceValue> sortedDistanceValueList) {
		
		return sortedDistanceValueList.get(0).getDistance();
		
	}
	
	
	/**
     * Returns the maximum value in the specified sorted list
     *
     * @param sortedDistanceValueList the sorted list
     * 
     * @return the maximum
     */
	private double getMaximum(List<DistanceValue> sortedDistanceValueList) {
		
		return sortedDistanceValueList.get(sortedDistanceValueList.size() - 1).getDistance();
		
	}
	
	
	/**
     * Returns the pair identifier of the pair contained in the specified CAS
     *
     * @param jcas the CAS
     * 
     * @return the pair identifier
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
     * to largest
     *
     * @param distanceValues the list of distance values
     * 
     * @return a copy of the specified list sorted in increasing order
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
     * Puts distance values calculating for each of the pair T and H
     * of the specified list of Cas into the distanceValues list. 
     * Each of the Cas of the list contains a single pair T-H
     *
     * @param jcas the list of CAS
     * @param distanceValues the list of the distance values
     * 
     * @throws DistanceComponentException
     */
	private void getDistanceValues(JCas jcas, List<DistanceValue> distanceValues)
			throws DistanceComponentException {
	
		try {
			
				DistanceValue distanceValue = component.calculation(jcas);
				distanceValues.add(distanceValue);
			
		} catch(DistanceComponentException e) {
			throw e;
		}
			
	}
		
	
	/**
     * Puts the entailment annotations calculating of each of the pair T and H
     * of the specified list of Cas into the entailmentValueList list. 
     * Each of the Cas of the list contains a single pair T-H.
     *
     * @param jcas the list of CAS
     * @aram entailmentValueList the list of the entailment annotations
     * 
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
     * Load the model file
     *
     * @param modelFile the file
     * 
     * @return the model
     */
	private double loadModel(File modelFile) throws IOException {
		
		logger.info("loadModel()");
		
		double result = -1.0;
		
		BufferedReader reader = null; 
		
		try {
			
			//reader = new BufferedReader(new FileReader(modelFile));
			
			reader = new BufferedReader(
	                   new InputStreamReader(new FileInputStream(modelFile), "UTF-8"));
			
			String line = reader.readLine();
			
			while (line != null) {
				result = Double.parseDouble(line);
				break;
			}
		
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		} finally { 
			if (reader != null)
				reader.close();
		}
	
		return result;
		
	}
	
	
	/**
     * Save the model file containing the threshold value
     *
     * @param modelFile the file
     * @param threshold the threshold value
     * 
     */
	public void saveModel(File modelFile, double threshold) throws IOException {
    	
		logger.info("saveModel()");
		
    	BufferedWriter writer = null;
    	
    	try {
    		
	    	//writer = new BufferedWriter(new FileWriter(modelFile));
	    	
	    	writer = new BufferedWriter(new OutputStreamWriter(
	                  new FileOutputStream(modelFile), "UTF-8"));

	    	
	    	PrintWriter printout = new PrintWriter(writer);
	    	printout.print(threshold);
	    	printout.close();
	    	
    	} catch (Exception e) {
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
