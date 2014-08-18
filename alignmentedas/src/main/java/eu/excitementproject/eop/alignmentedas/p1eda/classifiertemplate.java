package eu.excitementproject.eop.alignmentedas.p1eda;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * A test code to check how to represent (pluggable) classifier, via Weka, 
 * @author Gil
 *
 */
public class classifiertemplate {

	public static void main(String[] args) {
		
		// prepare data ... 
		
		 // Declare two numeric attributes
		 Attribute Attribute1 = new Attribute("firstNumeric");
		 Attribute Attribute2 = new Attribute("secondNumeric");
		 
		 // Declare a nominal attribute along with its values
		 FastVector fvNominalVal = new FastVector(3);
		 fvNominalVal.addElement("blue");
		 fvNominalVal.addElement("gray");
		 fvNominalVal.addElement("black");
		 Attribute Attribute3 = new Attribute("aNominal", fvNominalVal);
		 
		 // Declare the class attribute along with its values
		 FastVector fvClassVal = new FastVector(2);
		 fvClassVal.addElement("positive");
		 fvClassVal.addElement("negative");
		 Attribute ClassAttribute = new Attribute("theClass", fvClassVal);
		 
		 // Declare the feature vector
		 FastVector fvWekaAttributes = new FastVector(4);
		 fvWekaAttributes.addElement(Attribute1);    
		 fvWekaAttributes.addElement(Attribute2);    
		 fvWekaAttributes.addElement(Attribute3);    
		 fvWekaAttributes.addElement(ClassAttribute);
		 
		 //
		 //
		 //
		 
		 // Create an empty training set
		 Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);           
		 // Set class index (which of the attribute holds the class?) 
		 isTrainingSet.setClassIndex(3);		 
		 
		 Instance iExample = new Instance(4);
		 iExample.setValue((Attribute)fvWekaAttributes.elementAt(0), 1.0);      
		 iExample.setValue((Attribute)fvWekaAttributes.elementAt(1), 0.5);      
		 iExample.setValue((Attribute)fvWekaAttributes.elementAt(2), "gray");
		 iExample.setValue((Attribute)fvWekaAttributes.elementAt(3), "positive");

		 isTrainingSet.add(iExample);
		 
		 //
		 
		 // Create a naïve bayes classifier 
//		 Classifier cModel = (Classifier)new NaiveBayes();
		 Classifier cModel = (Classifier)new NaiveBayes();
		 String[] options = {"-K"}; 
		 try {
			 cModel.setOptions(options); 
		 }
		 catch(Exception e)
		 {
			 System.err.println("Exception at setting classifier option" + e.getMessage()); 
			 System.exit(1); 
		 }

		 try {
			 cModel.buildClassifier(isTrainingSet);
		 }
		 catch (Exception e)
		 {
			 System.err.println("Exception at training" + e.getMessage()); 
			 System.exit(1); 
		 }
		 
		 Instance newone = new Instance(4); 
		 newone.setDataset(isTrainingSet); 
		 newone.setValue(0, 0.5); 
		 newone.setValue(1, 0.1); 
		 newone.setValue((Attribute) fvWekaAttributes.elementAt(2), "blue"); 
		 
		 try {
			 double[] out = cModel.distributionForInstance(newone); 
			 System.out.println("dist for positive: " + out[0]); 
			 System.out.println("dist for negative: " + out[1]); 
		 }
		 catch (Exception e)
		 {
			 System.err.println("Exception at classification" + e.getMessage()); 
			 System.exit(1); 
		 }
		 
		 try {
			 weka.core.SerializationHelper.write("default.model", cModel);
		 }
		 catch (Exception e)
		 {
			 System.err.println("Exception at serialization" + e.getMessage()); 
			 System.exit(1); 
		 }
		 
		 Classifier cls = null; 
		 try {
			 cls = (Classifier) weka.core.SerializationHelper.read("default.model");
		 }
		 catch (Exception e)
		 {
			 System.err.println("Exception at deserialization" + e.getMessage()); 
			 System.exit(1); 
		 }
		 
		 try {
			 double[] out = cls.distributionForInstance(newone); 
			 System.out.println("dist for positive: " + out[0]); 
			 System.out.println("dist for negative: " + out[1]); 
		 }
		 catch (Exception e)
		 {
			 System.err.println("Exception at classification" + e.getMessage()); 
			 System.exit(1); 
		 }

	}

}
