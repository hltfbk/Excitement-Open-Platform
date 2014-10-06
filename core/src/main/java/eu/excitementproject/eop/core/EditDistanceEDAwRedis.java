package eu.excitementproject.eop.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.component.distance.DistanceComponentException;
import eu.excitementproject.eop.common.component.distance.DistanceValue;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.distance.FixedWeightEditDistancewRedis;
import eu.excitementproject.eop.lap.PlatformCASProber;


/**
 * The <code>EditDistanceEDA</code> class implements the <code>EDABasic</code> interface.
 * Given a certain configuration, it can be trained over a specific data set in order to optimize its
 * performance. In the training phase this class produces a distance model for the data set, which
 * includes a distance threshold that best separates the positive and negative examples in the training data.
 * During the test phase it applies the calculated threshold, so that pairs resulting in a distance below the
 * threshold are classified as ENTAILMENT, while pairs above the threshold are classified as NONENTAILMENT.
 * <code>EditDistanceEDA</code> uses <code>FixedWeightEditDistance</code> for calculating edit distance
 * between each pair of T and H.
 * 
 * @author Roberto Zanoli
 * 
 */
public class EditDistanceEDAwRedis<T extends TEDecision>
		extends EditDistanceEDA<EditDistanceTEDecision> {
		
	/**
	 * the logger
	 */
	static Logger logger = Logger.getLogger(EditDistanceEDAwRedis.class.getName());

	protected FixedWeightEditDistancewRedis component;
	
	/**
	 * Construct an edit distance EDA.
	 */
	public EditDistanceEDAwRedis() {
		super();
    }
	
	/**
	 * Construct an edit distance EDA with the weights of the edit distance operations set
	 * 
	 * @param mMatchWeight weight for match
	 * @param mDeleteWeight weight for delete
	 * @param mInsertWeight weight for insert
	 * @param mSubstituteWeight weight for substitute
	 * 
	 */
	public EditDistanceEDAwRedis(double mMatchWeight, double mDeleteWeight, double mInsertWeight, double mSubstituteWeight) {
		
		super(mMatchWeight, mDeleteWeight, mInsertWeight, mSubstituteWeight);	
	}

	@Override
	public void initialize(CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		
		logger.info("Initialization ...");
		
		try {
        	
        	//checking the configuration file
			checkConfiguration(config);
			
			logger.info("Getting section: " + this.getType());
			
			//getting the name value table of the EDA
			NameValueTable nameValueTable = config.getSection(this.getType());
			
			logger.info("Setting the train and test dirs for LAP");
			
			//setting the training directory
			if (this.trainDIR == null)
				this.trainDIR = nameValueTable.getString("trainDir");
			
			//setting the test directory
			if (this.testDIR == null)
				this.testDIR = nameValueTable.getString("testDir");
			
			//initializing the threshold value
			initializeThreshold(config);
			
			//initializing the weight of the edit distant operations
			initializeWeights(config);
			
			//component initialization
			String componentName  = nameValueTable.getString("components");
			
			logger.info("Will initialize component " + componentName);
			
			if (component == null) {
				
				try {
					
					Class<?> componentClass = Class.forName(componentName);
					logger.info("Using:" + componentClass.getCanonicalName());
					Constructor<?> componentClassConstructor = componentClass.getConstructor(CommonConfig.class);
					this.component = (FixedWeightEditDistancewRedis) componentClassConstructor.newInstance(config);
					this.component.setmMatchWeight(mMatchWeight);
					this.component.setmDeleteWeight(mDeleteWeight);
					this.component.setmInsertWeight(mInsertWeight);
					this.component.setmSubstituteWeight(mSubstituteWeight);
					
				} catch (Exception e) {
					throw new ComponentException(e.getMessage());
				}
				
			}
			
			//setting the measure to be optimized
			if (this.measureToOptimize == null)
				this.measureToOptimize = nameValueTable.getString("measure");
			
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new EDAException(e.getMessage());
		}
		
		logger.info("done.");
	
	}
	
	@Override
	public EditDistanceTEDecision process(JCas jcas) throws EDAException, ComponentException {
		
		String pairId = getPairId(jcas);
		
		//the distance between the T-H pair
		DistanceValue distanceValue = component.calculation(jcas);
		double distance = distanceValue.getDistance();
		
		// During the test phase the method applies the threshold, so that
		// pairs resulting in a distance below the threshold are classiﬁed as ENTAILMENT, while pairs 
		// above the threshold are classiﬁed as NONENTAILMENT.
		if (distance <= this.threshold)
			return new EditDistanceTEDecision(DecisionLabel.Entailment, pairId, threshold - distance);
		
		return new EditDistanceTEDecision(DecisionLabel.NonEntailment, pairId, distance - threshold);
		
	}
	
	@Override
	public void shutdown() {
		
		logger.info("Shutting down ...");
	    logger.info("Number of rules used: " + FixedWeightEditDistancewRedis.ruleCounter);   
		
		if (component != null)
			component.shutdown();
		
		this.threshold = Double.NaN;
		this.component = null;
        this.writeModel = true;
        this.mMatchWeight = Double.NaN;
        this.mDeleteWeight = Double.NaN;
        this.mInsertWeight = Double.NaN;
        this.mSubstituteWeight = Double.NaN;
        this.trainDIR = null;
        this.trainDIR = null;
        this.language = null;
        this.measureToOptimize = null;
        
        logger.info("done.");
	}
	
	@Override
	public void startTraining(CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		
		logger.info("Training ...");
		
		try {
			
			initialize(config);
			
			//contains the distance between each pair of T-H
			List<DistanceValue> distanceValueList = new ArrayList<DistanceValue>();
			//contains the entailment annotation between each pair of T-H
			List<String> entailmentValueList = new ArrayList<String>();
			//contains the entailment annotation and the distance between each pair of T-H
			List<Annotation> annotationList;
			
			File f = new File(trainDIR);
			if (f.exists() == false) {
				throw new ConfigurationException("trainDIR:" + f.getAbsolutePath() + " not found!");
			}
			
			logger.info(f.listFiles().length + " files to process from " + f.getName() );
			
			int filesCounter = 0;
			for (File xmi : f.listFiles()) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
					
				getDistanceValues(cas, distanceValueList);
				getEntailmentAnnotation(cas, entailmentValueList);
				
				filesCounter++;
			}
			
			annotationList = merge(distanceValueList, entailmentValueList);
		
			if (filesCounter == 0)
				throw new ConfigurationException("trainDIR:" + f.getAbsolutePath() + " empty!");
			
			//array of two elements; the first element is the calculated threshold whereas the
			//second one is the obtained accuracy
			double[] thresholdAndAccuracy = 
					//sequentialSearch(distanceValueList, entailmentValueList, measureToOptimize);
					sequentialSearch(annotationList, measureToOptimize);
			
			this.threshold = thresholdAndAccuracy[0];
			this.trainingAccuracy = thresholdAndAccuracy[1];
			
			//it saves the calculated model into the configuration file itself
			if (this.writeModel == true)
				saveModel(config);	
			
		} catch (ConfigurationException e) {
			throw e;
		} catch (EDAException e) {
			throw e;
		} catch (ComponentException e) {
			throw e;
		} catch (Exception e) {
			throw new EDAException(e.getMessage());
		}
		
		logger.info("done.");
		logger.info("Number of LR rules used in training: " + FixedWeightEditDistancewRedis.ruleCounter);
		FixedWeightEditDistancewRedis.ruleCounter = 0;
		
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
	@Override
	protected void getDistanceValues(JCas jcas, List<DistanceValue> distanceValues)
			throws DistanceComponentException {
	
		try {

				DistanceValue distanceValue = component.calculation(jcas);
				distanceValues.add(distanceValue);

		} catch(DistanceComponentException e) {
			throw e;
		}
			
	}
		
	
}
