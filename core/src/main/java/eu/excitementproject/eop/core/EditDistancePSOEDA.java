package eu.excitementproject.eop.core;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import eu.fbk.hlt.pso.*;

import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import org.w3c.dom.*;

/**
 * The <code>EditDistancePSOEDA</code> class extends the <code>EditDistanceEDA</code> class.
 * Given a certain configuration, it can be trained over a specific data set in order to optimize its
 * performance. In the training phase this class produces a distance model for the data set, which
 * includes a distance threshold that best separates the positive and negative examples in the training data.
 * 
 * Differently to the class EditDistanceEDA the weights of the edit distance operations are calculated
 * and optimized by Particle Swarm Optimization (PSO): Kennedy, J.; Eberhart, R. (1995). Particle Swarm 
 * Optimization. Proceedings of IEEE International Conference on Neural Networks IV. pp. 1942–1948.
 * 
 * During the test phase it applies the calculated threshold, so that pairs resulting in a distance below the
 * threshold are classified as ENTAILMENT, while pairs above the threshold are classified as NONENTAILMENT.
 * <code>EditDistanceEDA</code> uses <code>FixedWeightEditDistance</code> for calculating edit distance
 * between each pair of T and H. 
 * 
 * @author Roberto Zanoli
 * 
 */
public class EditDistancePSOEDA<T extends TEDecision> extends EditDistanceEDA<T> {
	
	//The following 3 parameters determines when PSO stops
	//max number of iteration of PSO
	private int maxIteration = -1;
	//max number of iteration without any changes in results
	private int maxIterationWithoutChanges = -1;
	//the error tolerance used by PSO to terminate 
	private double errorTolerance = -1;
	
	//the swarm size of PSO
	private int swarmSize = -1;
	//the number of processors to be used for running PSO
	private int processors = -1;
	
	
	/**
	 * Construct an edit distance EDA.
	 */
	public EditDistancePSOEDA() {
		
		super();
		
	}
	
	/**
	 * get the type of component (i.e. EditDistanceEDA)
	 * 
	 * @return the type of component
	 */
	@SuppressWarnings("rawtypes")
	protected String getType() {
    	
		String name = null;
		
		try {
			
			Class c = Class.forName(this.getClass().getCanonicalName());
			name = c.getSuperclass().getCanonicalName();
			
		} catch (Exception e) {
			logger.severe("Error in getting the type of the EDA!");
		}
		
    	return name;
    	
    }
	
	/*
	 * PSO initialization
	 * 
	 * @param config The configuration file
	 * 
	 * @return an instance of PSO
	 */
	private PSO initializePSO(CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		
		PSO pso = null;
		
		//EDA initialization
		initialize(config);
		
		try {
			
			//the range of values of the delete operation where PSO has to select a value from
			Value mDeleteWeighValue = null;
			//the range of values of the insert operation where PSO has to select a value from
			Value mInsertWeighValue = null;
			//the range of values of the substitute operation where PSO has to select a value from
		    Value mSubstituteWeighValue = null;
			
			//getting the name value table
			NameValueTable nameValueTable = config.getSection(this.getType());
			
			//the max number of the iteration of PSO
			if (this.maxIteration == -1)
				this.maxIteration = Integer.parseInt(nameValueTable.getString("maxIteration"));
			//the swarm size
			if (this.swarmSize == -1)
				this.swarmSize = Integer.parseInt(nameValueTable.getString("swarmSize"));
			//processors to be used to run PSO
			if (this.processors == -1)
				this.processors = Integer.parseInt(nameValueTable.getString("processors"));
			//the tolerance error 
			if (this.errorTolerance == -1)
				this.errorTolerance = Double.parseDouble(nameValueTable.getString("errorTolerance"));
			if (this.maxIterationWithoutChanges == -1)
				this.maxIterationWithoutChanges = Integer.parseInt(nameValueTable.getString("maxIterationWithoutChanges"));
			
			//the range of values of the delete operation
			if (mDeleteWeighValue == null) {
				String deleteValuesRange = nameValueTable.getString("deleteValuesRange");
				mDeleteWeighValue = 
						new Value(Double.parseDouble(deleteValuesRange.split(",")[0]), 
								Double.parseDouble(deleteValuesRange.split(",")[1]));
			}
			//the range of values of the insert operation
			if (mInsertWeighValue == null) {
				String insertValuesRange = nameValueTable.getString("insertValuesRange");
				mInsertWeighValue = new Value(Double.parseDouble(insertValuesRange.split(",")[0]), 
						Double.parseDouble(insertValuesRange.split(",")[1]));
			}
			//the range of values of the substitute operation
			if (mSubstituteWeighValue == null) {
				String substituteValuesRange = nameValueTable.getString("substituteValuesRange");
				mSubstituteWeighValue = new Value(Double.parseDouble(substituteValuesRange.split(",")[0]), 
						Double.parseDouble(substituteValuesRange.split(",")[1]));
			}
			
			//EditDistanceFitnessFunction represents the function to be optimized by PSO
			EditDistanceFitnessFunction.CONFIG = config;
			//EditDistanceFitnessFunction.CANONICAL_NAME = getCanonicalName();
			
			Class<?> fitnessFunction = EditDistanceFitnessFunction.class;
			
			//this is the space of the problem to be solved by PSO
			//in the present work it has 3 dimensions: delete, insert, substitute
			List<Value> problemSpace = new ArrayList<Value>();	
			//adding the 3 dimensions
			problemSpace.add(mDeleteWeighValue );
			problemSpace.add(mInsertWeighValue );
			problemSpace.add(mSubstituteWeighValue);
			
			//problem initialization
			ProblemSet problemSet = new ProblemSet(fitnessFunction, problemSpace);
			problemSet.setMaxIteration(this.maxIteration);
			problemSet.setMaxNumberOfIterationsWithoutChanges(this.maxIterationWithoutChanges);
			problemSet.setSwarmSize(this.swarmSize);
			problemSet.setProcessors(this.processors);
			problemSet.setErrorTolerance(this.errorTolerance);
			
			//initialize PSO and run it
			pso = new PSO(problemSet);
			
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new EDAException(e.getMessage());
		}
		
		return pso;
		
	}
	
	@Override
	public void startTraining(CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		
		try {
		
			PSO pso = initializePSO(config);
			Position gBestPosition = pso.execute();
			
			//these are the optimized values of the edit distance operations calculated by PSO
			setmMatchWeight(0.0);
			setmDeleteWeight(gBestPosition.getValue()[0]);
			setmInsertWeight(gBestPosition.getValue()[1]);
			setmSubstituteWeight(gBestPosition.getValue()[2]);
			
			//set the edit distance component with the optimized weights calculated by PSO
			getComponent().setmMatchWeight(getmMatchWeight());
			getComponent().setmDeleteWeight(getmDeleteWeight());
			getComponent().setmInsertWeight(getmInsertWeight());
			getComponent().setmSubstituteWeight(getmSubstituteWeight());
			
			//train EditDistanceEDA with the optimized weights
			super.startTraining(config);
			
		} catch (Exception e) {
			throw new EDAException(e.getMessage());
		}
		
	}
	
	/**
     * Initialize the weights of the edit distance operations getting them form the configuration file
     * 
     * If all the values of the 4 edit distance operation are defined they are used
     * to calculate edit distance. Otherwise the weights are read from the configuration file.  
     * 
     * @param config the configuration
     * 
     */
    protected void initializeWeights(CommonConfig config) {

    	try{ 
    		
    		//get the values from the model section in the configuration file
    		NameValueTable weightsTable = config.getSection("model");
    		
    		//if they weights have already been set use those weights 
        	if (Double.isNaN(this.getmMatchWeight()) == false && Double.isNaN(this.getmDeleteWeight()) == false &&
        			Double.isNaN(this.getmInsertWeight()) == false && Double.isNaN(this.getmSubstituteWeight()) == false)
        		return;
    		
        	//set the weights of the edit distance operations
    		this.setmMatchWeight(weightsTable.getDouble("match"));
    		this.setmDeleteWeight(weightsTable.getDouble("delete"));
    		this.setmInsertWeight(weightsTable.getDouble("insert"));
    		this.setmSubstituteWeight(weightsTable.getDouble("substitute"));
    		
    	} catch (Exception e) {
    		
    		logger.info("Could not find weights section in configuration file, using defaults");
    		this.setmMatchWeight(0.0);
    		this.setmDeleteWeight(0.0);
    		this.setmInsertWeight(1.0);
    		this.setmSubstituteWeight(1.0);
    		
    	}
    	
    }
	
	/**
     * Reads the configuration file and adds the calculated optimized parameters. The optimized 
     * parameters includes the value of the edit distance operations as well as the threshold calculated on the
     * training data set.
     * 
     * @return the configuration file with the optimized parameters added
     * 
     */
    protected String updateConfigurationFile(CommonConfig config) throws IOException {
    
    	StreamResult result = new StreamResult(new StringWriter());
    	
    	try {
    			
    		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    		Document document = docBuilder.parse(new File(config.getConfigurationFileName()));

    		XPathFactory xpathFactory = XPathFactory.newInstance();
    		XPath xpath = xpathFactory.newXPath();
    		
    		//updating the threshold value and the training accuracy
    		Node thresholdNode = (Node) xpath.evaluate("//*[@name='model']/*[@name='threshold']", document, XPathConstants.NODE);
    		thresholdNode.setTextContent(String.valueOf(this.getThreshold()));
    		Node trainingAccuracyNode = (Node) xpath.evaluate("//*[@name='model']/*[@name='trainingAccuracy']", document, XPathConstants.NODE);
    		trainingAccuracyNode.setTextContent(String.valueOf(this.getTrainingAccuracy()));
    		
    		//updating the weights of the edit distance operations
    		Node matchWeightNode = (Node) xpath.evaluate("//*[@name='model']/*[@name='match']", document, XPathConstants.NODE);
    		matchWeightNode.setTextContent(String.valueOf(this.getmMatchWeight()));
    		Node deleteWeightNode = (Node) xpath.evaluate("//*[@name='model']/*[@name='delete']", document, XPathConstants.NODE);
    		deleteWeightNode.setTextContent(String.valueOf(this.getmDeleteWeight()));
    		Node insertWeightNode = (Node) xpath.evaluate("//*[@name='model']/*[@name='insert']", document, XPathConstants.NODE);
    		insertWeightNode.setTextContent(String.valueOf(this.getmInsertWeight()));
    		Node substituteWeightNode = (Node) xpath.evaluate("//*[@name='model']/*[@name='substitute']", document, XPathConstants.NODE);
    		substituteWeightNode.setTextContent(String.valueOf(this.getmSubstituteWeight()));
    		
    		TransformerFactory tf = TransformerFactory.newInstance();
    		Transformer t = tf.newTransformer();
    		t.transform(new DOMSource(document), result);
    			       			
    	}catch (Exception e) {
    		throw new IOException(e.getMessage());
    	}
    	
    	return result.getWriter().toString();
	    
    }
	
}
