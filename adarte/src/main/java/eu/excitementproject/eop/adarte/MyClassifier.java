package eu.excitementproject.eop.adarte;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.FastVector;
import weka.core.Instance;


/**
 * 
 * This class implements the classifier to be used for training and annotating
 * and the current implementation is based on weka.
 * 
 * @author roberto zanoli
 * @author silvia colombo
 * 
 * @since January 2015
 */
public class MyClassifier {
	
	//the classifier
	private AbstractClassifier classifier = null;
	//for cross validation
	private int numFolds = 10;
	//the feature set
	private Map<String,Integer> featuresSet;
	//the class labels
	private FastVector classesList;
	//the model of the classifier
	private String classifierModel;
	
	
	/**
	 * Build a new classifier
	 * 
	 * @param classifierName the classifier name, e.g. weka.classifiers.trees.RandomForest
	 * @param classifierParameters the classifier parameters, e.g. -I 1000 -K 0 -S 1
	 * @param classifierModel the path where the classifier model has to be stored
	 * 
	 */
	public MyClassifier (String classifierName, String[] classifierParameters, String classifierModel) throws Exception {
		
		this.classifierModel = classifierModel;
		
		try {
			
			Class<?> classifierClass = Class.forName(classifierName);
			Constructor<?> classifierClassConstructor = classifierClass.getConstructor();
			this.classifier = (AbstractClassifier) classifierClassConstructor.newInstance();
			if (classifierParameters != null && !classifierParameters.equals(""))
				this.classifier.setOptions(classifierParameters);
			String[] options = this.classifier.getOptions();
			StringBuffer optionsString = new StringBuffer(); 
			for (int i = 0; i < options.length; i++) {
				optionsString.append(options[i]);
				optionsString.append("");
			}
			
		} catch (Exception e) {
			
			throw new Exception(e.getMessage());
			
		}
		
		//initialize the feature set
		initFeaturesList();
		//initialize the structure containing the class labels
		initClassesList();
		
	}
	
	/**
	 * Load a pre-trained classifier
	 *
	 * @param classifierModel the classifier model
	 * 
	 */
	public MyClassifier (String classifierModel) throws Exception {
	
		this.classifierModel = classifierModel;
		
		try {
			
	    	if (this.classifier == null) {
	    		this.classifier = (AbstractClassifier) weka.core.SerializationHelper.read(classifierModel);
	    	}
	    	
	    	//load the feature set used for training the classifier
	    	loadFeaturesSet();
	    	//load the class set used for training the classifier
	    	loadClasses();
	    	
		} catch (Exception e) {
			
			throw new Exception(e.getMessage());
			
		}
		
	}
	

	/**
	 * Get the list of the features used for building the classifier
	 * 
	 * @return the list of features
	 * 
	 */
	protected Map<String,Integer> getFeaturesList() {
		
		return this.featuresSet;
		
	}
	
	
	/**
	 * Get the list of the classes used for building the classifier
	 * 
	 * @return the list of classes
	 * 
	 */
	protected FastVector getClassesList() {
	
		return this.classesList;
		
	}
	
	
	/**
	 * Evaluate the created model
	 * 
	 * @throws Exception
	 * 
	 */
	protected String evaluateModel(DataSet dataSet) throws Exception {
		
		StringBuffer result = new StringBuffer();
		
		try {
			
			Evaluation evaluation = new Evaluation(dataSet.getData());
	    	evaluation.crossValidateModel(classifier, dataSet.getData(), this.numFolds, new Random(1));
	    	result.append("evaluation summary:");
	    	result.append("\n");
	    	result.append(evaluation.toSummaryString());
	    	result.append("detailed accuracy:");
	    	result.append("\n");
	    	result.append(evaluation.toClassDetailsString());
	    	
		} catch (Exception e) {
		
			throw new Exception("Evaluation model error:" + e.getMessage());
		
		} 
		
		return result.toString();
		
	}
	
	
	/**
	 * Initialize the classes structure
	 */
	protected void initClassesList() {
		
		this.classesList = new FastVector();
		this.classesList.addElement("?");
		//this.classesList.addElement("fake_class");
		
	}

	
	/**
	 * Initialize the features set
	 */
	protected void initFeaturesList() {
		
		this.featuresSet = new HashMap<String,Integer>();
		this.featuresSet.put("fake_attribute", 0);
		//comment to not consider the distance
		//this.featuresList.put("distance", 1);
		
	}
	
	
	/**
	 * Check if the features set contains the feature
	 * 
	 * @param feature the feature
	 * 
	 * @return true if the feature exists, false otherwise
	 */
	protected boolean containsFeature(String feature) {
		
		return this.featuresSet.containsKey(feature);
		
	}
	
	
	/**
	 * Add a feature in the features set
	 * 
	 * @param feature the feature
	 * 
	 * @return true if the feature has been added, false otherwise
	 * 
	 */
	protected boolean addFeature(String feature) {
		
		if (!this.featuresSet.containsKey(feature)) {
				this.featuresSet.put(feature, getFeaturesSetSize());
				return true;
		}
		
		return false;
		
	}
	
	
	/**
	 * Check if the classes set contains the class
	 * 
	 * @param className the class
	 * 
	 * @return true if the class exists, false otherwise
	 */
	protected boolean containsClass(String className) {
		
		return this.classesList.contains(className);
		
	}
	
	
	/**
	 * Add a class in the classes set
	 * 
	 * @param className the class
	 * 
	 * @return true if the class has been added, false otherwise 
	 * 
	 */
	protected boolean addClass(String className) {
		
		if (!this.classesList.contains(className)) {
			this.classesList.addElement(className);
			return true;
		}
		
		return false;
		
	}
	
	
	/**
	 * Get the features set size
	 * 
	 * @return the features set size
	 * 
	 */
	protected int getFeaturesSetSize() {
		
		return featuresSet.size();
		
	}
	
	
	/**
	 * Get the classes set size
	 * 
	 * @return the classes set size
	 * 
	 */
	protected int getClassesListSize() {
		
		return this.classesList.size();
		
	}
	
	
	/**
	 * Save the feature set in a file
	 * 
	 */
	protected void saveFeaturesSet() throws Exception {
		
		BufferedWriter writer = null;
	    StringBuffer stringBuffer = new StringBuffer();
		
	    //print the number of features
	    stringBuffer.append(this.featuresSet.size());
	    stringBuffer.append("\n");
	    
	    /*
	    try {
	    		
	    	for (int i = 0; i < attributeNumber; i++) {
		    	String feature_i = dataSet.getData().attribute(i).name();
		    	if (feature_i.equals(attributeClassName))
		    			continue;
	    		stringBuffer.append(feature_i);
	    		stringBuffer.append("\n");
	    	}
	    	
	    	writer = new BufferedWriter(new OutputStreamWriter(
	                  new FileOutputStream(this.classifierModel + ".feature_list.txt", false), "UTF-8"));

	    	PrintWriter printout = new PrintWriter(writer);
	    	printout.print(stringBuffer);
	    	printout.close();
	    	
	    	writer.close();
		    	
	    } catch (Exception e) {
	    	
	    	throw new Exception("Saving features list error:" + e.getMessage());
	    	
	    }
	    
	    */
		
	    try {
	    	
	    	Iterator<String> iterator = this.featuresSet.keySet().iterator();
	    	while (iterator.hasNext()) {
	    		String feature_i = iterator.next();
	    		Integer feature_id = this.featuresSet.get(feature_i);
	    		stringBuffer.append(feature_i);
	    		stringBuffer.append("\t");
	    		stringBuffer.append(feature_id);
	    		stringBuffer.append("\n");
	    	}
	    	
	    	writer = new BufferedWriter(new OutputStreamWriter(
	                  new FileOutputStream(this.classifierModel + ".feature_set.txt", false), "UTF-8"));

	    	PrintWriter printout = new PrintWriter(writer);
	    	printout.print(stringBuffer);
	    	printout.close();
	    	
	    	writer.close();
		    	
	    } catch (Exception e) {
	    	
	    	throw new Exception("Saving the feature set error:" + e.getMessage());
	    	
	    }
		
		
	}
	
	/**
	 * 
	 * Load the feature set
	 * 
	 */
	protected void loadFeaturesSet() throws Exception {
		
		try {
			
			File fileDir = new File(this.classifierModel + ".feature_set.txt");
	 
			BufferedReader in = new BufferedReader(
			   new InputStreamReader(
	                      new FileInputStream(fileDir), "UTF8"));
	 
			String str;
			int lineCounter = 0;
	 
			while ((str = in.readLine()) != null) {
				if (lineCounter == 0) {
					int featuresNumber = Integer.parseInt(str);
					this.featuresSet = new HashMap<String,Integer>(featuresNumber);
				}
				else {
				    String[] splitLine = str.split("\t");
				    String feature_i = splitLine[0];
				    String featureId = splitLine[1];
				    this.featuresSet.put(feature_i, new Integer(featureId));
				}
				lineCounter++;
			}
	        
			in.close();
	                
		} catch (UnsupportedEncodingException e) {
				throw new Exception("Getting features list Unsupported Encoding Exception:" + e.getMessage());
		} catch (IOException e) {
		    	throw new Exception("Getting features list IOError:" + e.getMessage());
		} catch (Exception e) {
				throw new Exception("Getting features list error:" + e.getMessage());
		}
		
	}
	
	
	/**
	 * Save the class labels list
	 */
	protected void saveClasses() throws Exception {
		
		BufferedWriter writer = null;
	    StringBuffer stringBuffer = new StringBuffer();
	    
	    //print the number of classes
	    stringBuffer.append(this.classesList.size());
	    stringBuffer.append("\n");
		
	    try {
	    		
	    	for (int i = 0; i < this.classesList.size(); i++) {
	    		
	    		String classLabel_i = (String)this.classesList.elementAt(i);
	    		stringBuffer.append(classLabel_i);
	    		//stringBuffer.append("\t");
	    		//stringBuffer.append(i);
	    		stringBuffer.append("\n");
	    		
	    	}
	    	
	    	writer = new BufferedWriter(new OutputStreamWriter(
	                  new FileOutputStream(this.classifierModel + ".classes.txt", false), "UTF-8"));

	    	PrintWriter printout = new PrintWriter(writer);
	    	printout.print(stringBuffer);
	    	printout.close();
	    	
	    	writer.close();
		    	
	    } catch (Exception e) {
	    	
	    	throw new Exception("Saving the classes error:" + e.getMessage());
	    	
	    }
	    
	}
	
	
	/**
	 * Get the class labels
	 */
	protected void loadClasses() throws Exception {
		
		try {
			
			File fileDir = new File(this.classifierModel + ".classes.txt");
	 
			BufferedReader in = new BufferedReader(
			   new InputStreamReader(
	                      new FileInputStream(fileDir), "UTF8"));
	 
			String str;
			int lineCounter = 0;
			
			while ((str = in.readLine()) != null) {
				if (lineCounter == 0) {
					int classesNumber = Integer.parseInt(str);
					this.classesList = new FastVector(classesNumber);
				}
				else {
				    String classLabel_i = str;
				    this.classesList.addElement(classLabel_i);
				}
				lineCounter++;
			}
	        
			in.close();
		    
		} catch (UnsupportedEncodingException e) {
			throw new Exception("Unsupported Encoding Exception:" + e.getMessage());
		} catch (IOException e) {
			throw new Exception("IOError:" + e.getMessage());
		} catch (Exception e) {
			throw new Exception("Error while loading the classes:" + e.getMessage());
		}
		
	}
	
	
	/**
	 * Train the classifier
	 */
	protected void trainClassifier(DataSet dataSet) throws Exception {
		
        try {
            
        	//building the classifier
            this.classifier.buildClassifier(dataSet.getData());
            //storing the trained classifier to a file for future use
            weka.core.SerializationHelper.write(this.classifierModel, this.classifier);
            
			//save the list of the features with their index in a file to be used
			//during the test phase (see the process method)
			//saveFeaturesList(trainingDataSet);
			saveFeaturesSet();
			
			//save the list of the classes and their index in a file to be used
			//during the test phase (see the process method)
			saveClasses();
				
     
        } catch (Exception ex) {
        	
        	throw new Exception("Training classifier error:" + ex.getMessage());
        	
        }
        
    }
	
    
	/**
	 * Test the classifier
	 */
    protected double[] testClassifier(DataSet dataSet) throws Exception {
    	
    	//it contains a confidence value of each of the predicted classes
    	double[] score = null;
    	
        try {
        	
            for (int i = 0; i < dataSet.getData().numInstances(); i++) {
                
            	Instance instance_i = dataSet.getData().instance(i);
            	
            	//the predicted class, e.g. 1
                //double entailment_class = classifier.classifyInstance(instance_i);
            	//the class label, e.g. ENTAILMENT
            	//logger.info("predicted class:" + inputDataset.attribute("class").value((int)entailment_class));
            	
            	//the confidence values
                score = this.classifier.distributionForInstance(instance_i); 
       
            }
            
        } catch (Exception ex) {
        	
        	throw new Exception("Testing classifier error:" + ex.getMessage());
            
        }
        
        return score;
        
    }
    
    
    /**
     * Print the classifier type
     */
    public String toString() {
    	
    	return this.classifier.toString();
    	
    }
    
}
