package eu.excitementproject.eop.alignmentedas.p1eda.insepctor;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.alignmentedas.p1eda.P1EDATemplate;
import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;
import eu.excitementproject.eop.alignmentedas.p1eda.inspector.InspectUtilsJCasAndLinks;
import eu.excitementproject.eop.alignmentedas.p1eda.inspector.InspectorForSimpleWordCoverageP1EDA;
import eu.excitementproject.eop.alignmentedas.p1eda.instances.SimpleWordCoverageEN;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.FeatureValue;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * A small code example to show how you can access JCas and some details about 
 * the EDA decision from P1EDA's process result. 
 *  
 * This example requires TreeTagger modules installed and ready to be run. 
 * 
 * @author Tae-Gil Noh
 *
 */
public class InspectorUsageExample {

	public static void main(String[] args) {
		
		
		// Set Log4J first ... 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);  // set INFO to hide Debug outs  

		//
		// Preparation of calling a P1EDA instance. 
		// 
		
		P1EDATemplate theEda = null; 
		LAP_ImplBase theLap = null; 
		try {
			// prepare lap and eda
			theLap = new TreeTaggerEN(); // TreeTagger pipeline 
			theEda = new SimpleWordCoverageEN("../core/src/main/resources/ontologies/EnglishWordNet-dict", "../core/src/main/resources/VerbOcean/verbocean.unrefined.2004-05-20.txt");
			theEda.initialize(new File("./src/test/resources/swc_en.cmodel"));  // init a pre-trained model from RTE3 English train 
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
			System.exit(1); 
		}
		
		// Now calling the EDA for a decision... 
		
		TEDecisionWithAlignment decision=null;  
		try 
		{
			// first get a THPair annotated in a JCas 
			JCas aJCas = theLap.generateSingleTHPairCAS("Sean Brown (born November 5, 1976 in Oshawa, Ontario, Canada) is a National Hockey League utility player and enforcer.", "Sean Brown plays in the National Hockey League.");
			decision = theEda.process(aJCas); 
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Failed somehow of calling LAP and EDA: please check TreeTagger is correctly installed, if you got an exception on that regard"); 
			System.exit(2); 
		}
		
		// Okay, we got a decision. Let's check what we have. 
		
		// Decision itself. 
		// TEDecisionWithAlignment is, an instance of TEDecision, so we can ask 
		// decision value and confidence. 
		System.out.println("The decision was: " + decision.getDecision().toString()); 
		System.out.println(" ... with confidence: " + decision.getConfidence()); 
		
		// You can get the JCas *with* alignment information from the decision data. 
		JCas alignedJCas = decision.getJCasWithAlignment(); 
		
		// This is the JCas is what you would love to pass to "CAS Visualizer" for demo. 
		// For now, let's see its content with "Text" based inspector methods. 
		try {
			String summaryTokenLevel = InspectUtilsJCasAndLinks.summarizeJCasWordLevel(alignedJCas);  
			String summaryLinks = InspectUtilsJCasAndLinks.summarizeAlignmentLinks(alignedJCas); 
			System.out.println(summaryTokenLevel);
			System.out.println(summaryLinks); 
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Somehow failed to look into JCas -- You shouldn't see this message"); 
			System.exit(3); 
		}
		
		// And you can get some additional information from the decision. 
		// For example, "internal coverage" belief of the P1EDA SimpleWordCoverage model ... 
		// ... and feature values that finally used for the classifications. 
		try {
			String coverageInformation = InspectorForSimpleWordCoverageP1EDA.coverageHistrogramP1EDASimpleWordCoverage(alignedJCas); 
			System.out.println(coverageInformation); 
			Vector<FeatureValue> fv = decision.getFeatureVector(); 
			System.out.println(fv); 			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Somehow failed to look into JCas -- You shouldn't see this message"); 
			System.exit(4); 
		}
		
		
	}
}

