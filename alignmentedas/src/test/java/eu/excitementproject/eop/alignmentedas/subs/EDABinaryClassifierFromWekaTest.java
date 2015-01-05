package eu.excitementproject.eop.alignmentedas.subs;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import eu.excitementproject.eop.alignmentedas.p1eda.classifiers.EDABinaryClassifierFromWeka;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.DecisionLabelWithConfidence;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.EDAClassifierAbstraction;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.FeatureValue;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.LabeledInstance;
import eu.excitementproject.eop.common.DecisionLabel;

public class EDABinaryClassifierFromWekaTest {

	@SuppressWarnings("deprecation")
	@Test
	public void test() {		
		// prepare a training set 
		List<LabeledInstance> trainingData = new ArrayList<LabeledInstance>(); 
		
		Vector<FeatureValue> fv1 = new Vector<FeatureValue>(); 
		fv1.addElement(new FeatureValue(1.0)); 
		fv1.addElement(new FeatureValue(0.5)); 
		fv1.addElement(new FeatureValue(MyColor.gray)); 
		
		LabeledInstance ins1 = new LabeledInstance(DecisionLabel.Entailment, fv1); 
		trainingData.add(ins1); 
		
		
		// init one and ask it to train ... 
		EDAClassifierAbstraction classifier = null; 
		try {
			classifier = new EDABinaryClassifierFromWeka(); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		try {
			classifier.createClassifierModel(trainingData); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// classify an instance ... 
		
		Vector<FeatureValue> fv2 = new Vector<FeatureValue>(); 
		fv2.addElement(new FeatureValue(0.5)); 
		fv2.addElement(new FeatureValue(0.1)); 
		fv2.addElement(new FeatureValue(MyColor.blue)); 

		try {
			DecisionLabelWithConfidence result = classifier.classifyInstance(fv2); 
			System.out.println(result.getLabel().toString()); 
			System.out.println(result.getConfidence()); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// evaluate classifier ... 
		try {
			List<Double> eval = classifier.evaluateClassifier(trainingData); 
			System.out.println("acc: " + eval.get(0)); 
			System.out.println("f1 :" + eval.get(1)); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 			
		}

		
		// store model, 
		File f = new File("target/default1.model"); 
		try {
			classifier.storeClassifierModel(f); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 			
		}
		
		// load model on a new instance ... 
		// and ask again ... 
		EDAClassifierAbstraction classifier2 = null; 
		try {
			classifier2 = new EDABinaryClassifierFromWeka(); 
			classifier2.loadClassifierModel(f); 
			DecisionLabelWithConfidence result = classifier.classifyInstance(fv2); 
			System.out.println(result.getLabel().toString()); 
			System.out.println(result.getConfidence()); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		f.delete(); 
		
	}

	public enum MyColor 
	{
		blue,
		gray,
		black
	}

}
