package eu.excitementproject.eop.adarte;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;


/**
 * 
 * The data set to train and test the classifier
 * 
 * @author roberto zanoli
 * @author silvia colombo
 * 
 * @since January 2015
 * 
 */
public class DataSet {

	/** 
	 * The data set (it is build by using the weka data structures), 
	 */
    private Instances data;
    //the features
    private Map<String,Integer> featuresSet;
    //the list of class labels
    private FastVector classLabels;
    
  
    /**
     * Get the data set
     */
    protected Instances getData() {
    	
    	return this.data;
    	
    }

	/**
	 * The constructor that initializes the data set declaring its attributes (features and classes)
	 * 
	 * @param featuresSet the features
	 * @param classLabels the classes
	 * 
	 */
	public DataSet(Map<String,Integer> featuresSet, FastVector classLabels) throws Exception {
	
		this.featuresSet = featuresSet;
		this.classLabels = classLabels;
		
		try {
		
			//1) defining the attributes; basically each of the 
			//extracted features is a new attribute, e.g.
			//@attribute attr6 numeric
			//@attribute attr7 numeric
			//@attribute attr8 numeric
			FastVector attributes = new FastVector();
			
			for (Entry<String, Integer> entry : entriesSortedByValues(featuresSet)) {
		        String featureName = entry.getKey();
		        
		        //commentare per rimuovere feature distanza
		        //if (featureName.indexOf("distance:") != -1)
		        	//featureName = featureName.split(":")[0];
		        	
				//each of the extracted features is a new attribute
				Attribute attribute_i = new Attribute(featureName);
				//adding the attribute_i into the list of the attributes
				//System.err.println(attribute_i +  "\t" + featuresList.get(featureName));
				attributes.addElement(attribute_i);
				//logger.info("adding attribute_i:" + attributes.size());
			}
			
			// 2) defining the class attribute, e.g.
			//@attribute class {null,ENTAILMENT,NONENTAILMENT}
			
			Attribute attribute_class = new Attribute("class", classLabels);
			//logger.info("adding class attribute:" + attribute_class);
			attributes.addElement(attribute_class);
			
			//create the data set named 'dataset', e.g.
			//@relation dataset
			data = new Instances("dataset", attributes, 0);
			
			//the last attribute is the class
			//inputDataset.setClassIndex(featuresList.size());
			data.setClassIndex(data.numAttributes() - 1);
			//logger.info("data set:\n" + inputDataset);
		
		} catch (Exception e) {
			
			throw new Exception("Data Set initialization error:" + e.getMessage());
			
		} 
		
	}
	

	/**
	 * Adding data (i.e. examples) into the defined data set
	 * 
	 * @param examples the examples to be added
	 * @param annotation the class labels of the examples
	 * @param 
	 * 
	 */
	//binary features
	//private void fillDataSet(List<HashSet<String>> examples, List<String> annotation) 
	//weighted features
	protected void addExamples(List<HashMap<String,Integer>> examples, List<String> annotation) throws Exception {
		
		try {
		
			//creating an instance for each of the examples 
			for (int i = 0; i < examples.size(); i++) {
				
				//getting the example_i
				//bninary feature
				//HashSet<String> example_i = examples.get(i);
				
				//weighted features
				HashMap<String,Integer> example_i = examples.get(i);
				
				//logger.info("example_i:" + example_i);
				//an array of size(featuresList)+1 values 
				double[] initValues = new double[featuresSet.size() + 1];
				//creating a SPARSE instance i and initialize it so that 
				//its values are set to 0 
				Instance instance_i = new SparseInstance(1.0, initValues);//1.0 is the instance weight
				
				//if T and H are the same; no insertion, substitution or replacement of text portions
				boolean no_differences = true;
				
				//binary feature
				//Iterator<String> iterator_j = example_i.iterator();
				//weighted feature
				Iterator<String> iterator_j = example_i.keySet().iterator();
				
				while(iterator_j.hasNext()) {
					
					String feature_j = iterator_j.next();
					//logger.finer("feature j:" + feature_j);
					
					
					//coommnetare per rimuovere feature distanza
					/*
					if (feature_j.indexOf("distance:") != -1) {
						String new_feature_j = feature_j.split(":")[0];
						//System.err.println(feature_j);
						int featureIndex = featuresList.get(new_feature_j);
						//System.err.println(feature_j + "---");
						double weight = Double.parseDouble(feature_j.split(":")[1]);
						//System.err.println(weight);
						instance_i.setValue(featureIndex, weight);//1.0 is the feature weight
					}
					
					//commentare per rimuovere feature distanza
					else 
					*/
					
					if (featuresSet.containsKey(feature_j)) {
						
						//System.err.println(feature_j + "\t" +  featuresList.get(feature_j));
						int featureIndex = featuresSet.get(feature_j);
						//only the features with weight different from 0 are set
						
						//binary feature
						//instance_i.setValue(featureIndex, 1.0);//1.0 is the feature weight
						//weighted feature
						
						double weight = 1.0;
						//if (this.binaryFeature == false)
							// weight = example_i.get(feature_j).doubleValue(); 
						
						instance_i.setValue(featureIndex, weight);//1.0 is the feature weight
						//System.err.println("feature:" + feature_j + " "  + featureIndex + " weight:" + weight);
						
						//if there are other transformations than matching, then T and H are different
						if (feature_j.indexOf(Transformation.MATCH) == -1)
							no_differences = false;
					}
					
				}
				
				if (instance_i.numValues() == 0 || no_differences == true) {
					int featureIndex;
					featureIndex = featuresSet.get("fake_attribute");
					instance_i.setValue(featureIndex, 1.0);//1.0 is the feature weight
				}
				//the last value is that of the annotation class
				
				//System.err.println(classesList.size());
				//System.err.println("+++++++++++++:" + featuresList.size() + " " + classesList.indexOf(annotation.get(i)) + " " + annotation.get(i));
				
				instance_i.setValue(featuresSet.size(), classLabels.indexOf(annotation.get(i)));
				//adding the instance into the data set
				data.add(instance_i);
				
			}
		
		} catch (Exception e) {
			
			throw new Exception("Creating data set error:" + e.getMessage());
			
		} 
		
	}
	
	
	
	/**
	 * Save the data set in arff format to be used with the WEKA Explorer
	 */
	protected void saveDataSet(String dataSetName) throws Exception {
		
		try {
		
			BufferedWriter writer = null;
	    		writer = new BufferedWriter(new OutputStreamWriter(
	                  new FileOutputStream(dataSetName, false), "UTF-8"));

	    	PrintWriter printout = new PrintWriter(writer);
	    	printout.print(data);
	    	printout.close();
	    	writer.close();
		    	
	    } catch (Exception e) {
	    	
	    	throw new Exception("Saving data set error:" + e.getMessage());
	    	
	    }
		
	}
	
	/**
	 * sort
	 */
	static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
            new Comparator<Map.Entry<K,V>>() {
                @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                    int res = e1.getValue().compareTo(e2.getValue());
                    return res != 0 ? res : 1; // Special fix to preserve items with equal values
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
	
	
	
    
}
