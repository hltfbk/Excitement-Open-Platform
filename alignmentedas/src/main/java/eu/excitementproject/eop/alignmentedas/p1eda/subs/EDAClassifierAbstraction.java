package eu.excitementproject.eop.alignmentedas.p1eda.subs;

import java.io.File;
import java.util.List;
import java.util.Vector;


/**
 * 
 * An abstract for classifier(s) that support TE decisions (EDAs). 
 * 
 * See "EDABinaryClassifierFromWeka" for an implementation example. 
 * 
 * @author Tae-Gil Noh
 *
 */
public interface EDAClassifierAbstraction {

	/**
	 * @param featureVector
	 * @return DecisionLabelWithConfidence 
	 */
	public DecisionLabelWithConfidence classifyInstance(Vector<FeatureValue> featureVector) throws ClassifierException; 
	
	/**
	 * @param goldData
	 */
	public void createClassifierModel(List<LabeledInstance> goldData) throws ClassifierException; 
	
	/**
	 * @param path
	 * @throws ClassifierException
	 */
	public void storeClassifierModel(File path) throws ClassifierException; 
	
	/**
	 * @param path
	 * @throws ClassifierException
	 */
	public void loadClassifierModel(File path) throws ClassifierException; 
	
	/**
	 * Evaluate currently trained classifier and returns a list of double values where it holds; 
	 * (accuracy, f1, prec, recall, true positive ratio, true negative ratio) 
	 * 
	 * @param goldData
	 * @throws ClassifierException
	 */
	public List<Double> evaluateClassifier(List<LabeledInstance> goldData) throws ClassifierException; 
}
