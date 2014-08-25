package eu.excitementproject.eop.alignmentedas.p1eda.subs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDAException;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


/**
 * 
 * An implementation of "EDAClassifierAbstraction", based on Weka. 
 * 
 * You can use any of the Weka's Classifiers 
 * (e.g. Classifier classes that support Weka's Classifier interface, "distributionForInstance") 
 *
 * You can change the underlying classifier (that is supported in Weka)
 * by extend/override prepareWekaClassifierInstance(). 
 * 
 * Note that, this class only supports binary classifications only; Any LabeledInstance given 
 * to the training step that is not DecisionLabel.Entailment DecisionLabel.NonEntailment will cause 
 * the implementation to throw an exception. 
 * 
 * @author Tae-Gil Noh 
 *
 */
public class EDABinaryClassifierFromWeka implements EDAClassifierAbstraction {

	public EDABinaryClassifierFromWeka() //throws ClassifierException,  {
		throws EDAException {
		
		try {
			prepareWekaClassifierInstance();
		}
		catch (ClassifierException ce)
		{
			throw new EDAException("Underlying classifier failed to initialize.", ce); 
		}
	}
	
	/**
	 * Extend/Override this method to change the classifier & its option. 
	 * 
	 * @throws ClassifierException
	 */
	protected void prepareWekaClassifierInstance() throws ClassifierException 
	{
		modelReady = false; 
		classifier = (Classifier)new NaiveBayes();
		String[] options = {"-K"}; 
		try 
		{
			classifier.setOptions(options); 
		} 
		catch (Exception e)
		{
			throw new ClassifierException("Unable to set classifier options", e); 
		}		
	}

	@Override
	public DecisionLabelWithConfidence classifyInstance(
			Vector<FeatureValue> featureVector) throws ClassifierException {

		if (!modelReady) 
		{
			throw new ClassifierException("The classifier is not ready for classification; either training, or loading model should be done before calling classify"); 
		}

		DecisionLabelWithConfidence result = null; 

		// Prepare Feature information (e.g. which index has what feature? required for Weka) 
		Instances attributeInfo = null; 
		try {
			attributeInfo = buildAttributeInfo(featureVector); 
		}
		catch (ValueException ve)
		{
			throw new ClassifierException("Reading a FeatureValue failed for some reason - must be a code bug! ", ve); 
		}

		// Make a new instance, with the given feature vector. 
		Instance anInstance = null; 
		try {
			anInstance = new Instance(featureVector.size()); 
			anInstance.setDataset(attributeInfo); 
			for(int i=0; i < featureVector.size(); i++)
			{
				FeatureValue f = featureVector.get(i); 
				Attribute attr = attributeInfo.attribute(i); 

				switch (f.getValueType())
				{
				case NOMINAL: 
					anInstance.setValue(attr, f.getNominalValue().toString()); 
					break;
				case BOOLEAN:
					anInstance.setValue(attr, f.getBooleanValue().toString()); 
					break;
				case DOUBLE:
					anInstance.setValue(attr, f.getDoubleValue()); 
				}
			}		 
		}
		catch (ValueException ve)
		{
			throw new ClassifierException("Reading a FeatureValue failed for some reason - must be a code bug! ", ve); 
		}

		// Okay, classify the newly prepared instance. 
		double[] dist = null; 
		try {
			dist = classifier.distributionForInstance(anInstance); 
		}
		catch (Exception e)
		{
			throw new ClassifierException("Underlying Weka classifier throws an exception", e); 
		}

		if (dist[0] > dist[1])
		{
			result = new DecisionLabelWithConfidence(DecisionLabel.Entailment, dist[0]);  
		}
		else
		{
			result = new DecisionLabelWithConfidence(DecisionLabel.NonEntailment, dist[1]); 
		}

		return result;
	}

	@Override
	public void createClassifierModel(List<LabeledInstance> goldData)
			throws ClassifierException {

		// Okay; first, convert the goldData into "Weka" training data (Instances). 
		// So we can train a Weka Classifier instance prepared within this wrapper class. 
		Instances trainingData = null; 
		try {
		  trainingData = buildTrainingDataSet(goldData); 
			
		} catch (ValueException ve)
		{
			throw new ClassifierException("Failed to read FeatureValue of training data. Must be a bug in code", ve); 
		}
		
		// Okay, pass trainingData to build classifier. 
		
		try {
			classifier.buildClassifier(trainingData);
		}	
		catch (Exception e)
		{
			throw new ClassifierException("Underlying Weka Classifier throws exception at training time...", e); 
		}
		
		// Okay. training is done and the classifier is ready for classification. 
		modelReady = true; 
	}

	@Override
	public void storeClassifierModel(File path) throws ClassifierException {
		if (!modelReady) 
		{
			throw new ClassifierException("The classifier is not ready for classification; either training, or loading model should be done before calling classify"); 
		}
		
		// Okay, store the model in the given path. 
		try {
			weka.core.SerializationHelper.write(path.getAbsolutePath(), classifier); 
		}
		catch (Exception e)
		{
			throw new ClassifierException("Serializing the trainined Weka classifier model failed, Weka serializationHelper raised an exception: ", e); 
		}

	}

	@Override
	public void loadClassifierModel(File path) throws ClassifierException {

		if (!path.exists())
		{
			throw new ClassifierException("Unable to load trained classifier model; Model file " + path.toString() + " does not exist"); 
		}
		
		try {
			classifier = (Classifier) weka.core.SerializationHelper.read(path.getAbsolutePath());
		}
		catch (Exception e)
		{
			
		}

		modelReady = true; 
	}

	@Override
	public List<Double> evaluateClassifier(List<LabeledInstance> goldData) 
			throws ClassifierException {
		
		// DECIDED: TODOconsider: make it solely as cross-validation? or option for cross-validation? 
		// This code does "as-is" evaluation. 
		// To do cross-validation of the given goldData, support other doCrossValidationEvaluation()
		
		Instances trainingData = null; 
		Evaluation eTest = null; 
		try {
		  trainingData = buildTrainingDataSet(goldData); 
			
		} catch (ValueException ve)
		{
			throw new ClassifierException("Failed to read FeatureValue of training data. Must be a bug in code", ve); 
		}
		catch (Exception e)
		{
			throw new ClassifierException("Underlying Weka Classifier Evaluator throws an exception", e); 
		}
		
		try {
			eTest = new Evaluation(trainingData); 
			eTest.evaluateModel(classifier, trainingData); 
		}
		catch (Exception e)
		{
			throw new ClassifierException("Underlying Weka Classifier Evaluator throws an exception", e); 
		}
		
		// DCODE - as log debug?  
		// System.out.println(eTest.toSummaryString()); 
		
		double tp = eTest.weightedTruePositiveRate(); 
		double tn = eTest.weightedTrueNegativeRate(); 
		double prec = eTest.weightedPrecision(); 
		double rec = eTest.weightedRecall(); 
		double f1 = eTest.weightedFMeasure(); 
		double accuracy = (eTest.correct()) / (eTest.incorrect() + eTest.correct()); 
		
		List<Double> evalResult = new ArrayList<Double>(); 
		evalResult.add(accuracy); 
		evalResult.add(f1); 
		evalResult.add(prec); 
		evalResult.add(rec); 
		evalResult.add(tp); 
		evalResult.add(tn); 

		return evalResult; 
	}
	
	/**
	 * @param vec
	 * @return
	 * @throws ValueException
	 */
	private Instances buildAttributeInfo(Vector<FeatureValue> vec) throws ValueException 
	{
		
		FastVector fvWekaAttributes = new FastVector(vec.size());

		for (int i=0; i < vec.size(); i++)
		{
			FeatureValue f = vec.get(i); 
			Attribute attr = null; 

			switch(f.getValueType())
			{
			case BOOLEAN:
				// build boolean attribute; 
				// we build a Weka nominal attribute with "true" and "false" 
				FastVector fvBooleanVal = new FastVector(2);
				fvBooleanVal.addElement("true");
				fvBooleanVal.addElement("false"); 
				attr = new Attribute(Integer.toString(i) + "_aBoolean", fvBooleanVal);
				fvWekaAttributes.addElement(attr); 
				break;
				
			case NOMINAL:
				// build nominal attribute, from enums 
				Enum<?> e = f.getNominalValue(); 
				Enum<?>[] elist = e.getClass().getEnumConstants(); 
				FastVector fvNominalVal = new FastVector(elist.length); 
				for(int j=0; j < elist.length; j++)
				{
					fvNominalVal.addElement(elist[j].toString());
				}
				attr = new Attribute(Integer.toString(i) + "_aNominal", fvNominalVal);
				fvWekaAttributes.addElement(attr); 
				break;
				
			case DOUBLE: 
				// build double (numeric) attribute 
				attr = new Attribute(Integer.toString(i) + "_aNumeric"); 
				fvWekaAttributes.addElement(attr); 
				break; 
			}
		}
		
		Instances attributeTable = new Instances("table", fvWekaAttributes, 10); 
		return attributeTable; 

	}
	
	private Instances buildTrainingDataSet(List<LabeledInstance> gold) throws ValueException, ClassifierException
	{
		
		// Let's first prepare attribute (feature) header, from the first feature vector  
		Vector<FeatureValue> vec = gold.get(0).getFeatureVector(); 
		int featureSize = vec.size(); 
		FastVector fvWekaAttributes = new FastVector(featureSize + 1);  // + 1 for label column 
		

		// for each value type, prepare attribute column accordingly ... 
		for (int i=0; i < vec.size(); i++)
		{
			FeatureValue f = vec.get(i); 
			Attribute attr = null; 

			switch(f.getValueType())
			{
			case BOOLEAN:
				// build boolean attribute; 
				// we build a Weka nominal attribute with "true" and "false" 
				FastVector fvBooleanVal = new FastVector(2);
				fvBooleanVal.addElement("true");
				fvBooleanVal.addElement("false"); 
				attr = new Attribute(Integer.toString(i) + "_aBoolean", fvBooleanVal);
				fvWekaAttributes.addElement(attr); 
				break;
				
			case NOMINAL:
				// build nominal attribute, from enums 
				Enum<?> e = f.getNominalValue(); 
				Enum<?>[] elist = e.getClass().getEnumConstants(); 
				FastVector fvNominalVal = new FastVector(elist.length); 
				for(int j=0; j < elist.length; j++)
				{
					fvNominalVal.addElement(elist[j].toString());
				}
				attr = new Attribute(Integer.toString(i) + "_aNominal", fvNominalVal);
				fvWekaAttributes.addElement(attr); 
				break;
				
			case DOUBLE: 
				// build double (numeric) attribute 
				attr = new Attribute(Integer.toString(i) + "_aNumeric"); 
				fvWekaAttributes.addElement(attr); 
				break; 
			}
		}
		
		// finally, add "class (decision label)" column 
		// This class, limits it as ENTAILMENT NONENTAILMENT only. 
		// (Binary classification only) 
		FastVector fvClassVal = new FastVector(2);
		fvClassVal.addElement(DecisionLabel.Entailment.toString());
		fvClassVal.addElement(DecisionLabel.NonEntailment.toString());
		Attribute ClassAttribute = new Attribute("theClass", fvClassVal);
		fvWekaAttributes.addElement(ClassAttribute);
			
		// okay, prepare an empty instances table with fvWekaAttributes 
		Instances trainingSet = new Instances("trainingData", fvWekaAttributes, gold.size()); 
		trainingSet.setClass(ClassAttribute); 
		
		// Table Ready. now populate each and every LabeledInstance into trainingSet 
		for (LabeledInstance inst : gold)
		{
			Vector<FeatureValue> featureVector = inst.getFeatureVector(); 
			DecisionLabel goldLabel = inst.getLabel(); 
			
			// two sanity checks 
			// if DecisionLabel is other then Entailment / NonEntailment, raise exception 
			if ((goldLabel != DecisionLabel.Entailment) && (goldLabel != DecisionLabel.NonEntailment))
			{
				throw new ClassifierException("Sorry, this classifier abstract only treats binary classification... "); 
			}
			
			// prepare an instance with feature values ... 
			Instance anInstance = new Instance(featureSize + 1); 
			anInstance.setDataset(trainingSet); 
			for(int i=0; i < featureSize; i++)
			{
				FeatureValue f = featureVector.get(i); 
				Attribute attr = trainingSet.attribute(i); 
				
				switch (f.getValueType())
				{
				case NOMINAL: 
					anInstance.setValue(attr, f.getNominalValue().toString()); 
					break;
				case BOOLEAN:
					anInstance.setValue(attr, f.getBooleanValue().toString()); 
					break;
				case DOUBLE:
					anInstance.setValue(attr, f.getDoubleValue()); 
				}
			}		 
			// and finally add class label 
			anInstance.setValue(ClassAttribute, inst.getLabel().toString()); 
			
			// Okay this instance is ready. Put it in the training set. 
			trainingSet.add(anInstance); 
		}
		return trainingSet; 
	}
	
	// private data 
	
	private Classifier classifier; 
	private Boolean modelReady; 
}
