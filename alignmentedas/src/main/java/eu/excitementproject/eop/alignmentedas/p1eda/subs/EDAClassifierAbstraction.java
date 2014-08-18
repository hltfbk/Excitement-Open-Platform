package eu.excitementproject.eop.alignmentedas.p1eda.subs;

import java.io.File;
import java.util.List;
import java.util.Vector;


/**
 * 
 * An abstract for classifier(s) that support TE decisions (EDAs). 
 * 
 * See "EDAClassifierFromWeka" for a wrapped (implemented) example.  
 * 
 * @author Tae-Gil Noh
 *
 */
public interface EDAClassifierAbstraction {

	/**
	 * @param featureVector
	 * @return DecisionLabelWithConfidence 
	 */
	public DecisionLabelWithDistribution classifyInstance(Vector<FeatureValue> featureVector) throws ClassifierException; 
	
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
	 * @param goldData
	 * @throws ClassifierException
	 * TODO update it to return value(s). 
	 */
	public void evaluateClassifier(List<LabeledInstance> goldData) throws ClassifierException; 
}
