/**
 * 
 */
package eu.excitementproject.eop.alignmentedas.p1eda;

import java.util.Vector;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.lap.LAPException;
import static eu.excitementproject.eop.lap.PlatformCASProber.probeCas; 

/**
 * This is a template, abstract class for P1 EDA. 
 * 
 * 
 * @author Tae-Gil Noh
 *
 */
/**
 * @author tailblues
 *
 */
public abstract class P1EDATemplate implements EDABasic<TEDecisionWithAlignment> {

		
	public TEDecisionWithAlignment process(JCas eopJCas) throws EDAException 
	{
		// Here's the basic step of P1 EDA's process(), outlined by the template.  
		// Note that, the template assumes that you override each of the step-methods. 
		// (although you are free to override any, including this process()). 
		
		// Step 0. check JCas: a correct one with all needed annotations? 
		checkInputJCas(eopJCas); 
		
		// Step 1. add alignments. The method will add various alignment.Link instances
		// Once this step is properly called, the JCas holds alignment.Link data in it. 
		addAlignments(eopJCas); 
		
		
		// Step 2. (this is an optional step.) The method will interact / output 
		// the added alignment links for debug / analysis purpose. (for Tracer)  
		visualizeAlignments(eopJCas); 
		
		// Step 3. 
		// 
		Vector<Double> featureValues = evaluateAlignments(); 
		
		// Step 4. (this is also an optional step.) 
		//
		visualizeEdaInternals(); 
		
		// Step 5. 
		// Classification. 
		TEDecisionWithAlignment decision = classifyEntailment(featureValues); 
		
				
		return decision; 
	}
	
	public void initialize(CommonConfig conf)
	{
		// TODO 
	}

	public void startTraining(CommonConfig conf)
	{
		// TODO 
	}
	
	public void shutdown()
	{
		// TODO 
	}
			
	
	/*
	 * Mandatory methods (steps) that should be overridden. 
	 */
	
	public abstract void addAlignments(JCas input); 
		
	public abstract Vector<Double> evaluateAlignments(); 

	
	/* 
	 *  Optional methods (steps) that can be overridden.    
	 */  
	
	public void visualizeAlignments(JCas CASWithAlignments)
	{
		// Template default is doing nothing. 
	}
	
	public void visualizeEdaInternals() 
	{
		// Template default is doing nothing. 
	}
	
	/*
	 * 	Optional methods (steps) that can be overridden. --- but these 
	 *  methods provide default functionalities. You can extend if you want. 
	 */
	
	/**
	 * This method will be used to check input CAS for P1EDA flow. 
	 * As is, it will do the basic check of CAS as EOP CAS input, via PlatformCASProber.probeCAS(). 
	 * 
	 * If you want to do additional checks, such as existence of specific LAP annotations, 
	 * You can extend this class to do the checks. 
	 * 
	 * EXTENSION of this method is optional, and not mandatory.  
	 * 
	 * @param input JCas that is given to your EDA  
	 * @throws EDAException If the given JCas was not well-formed for your EDA, you should throw this exception
	 */
	public void checkInputJCas(JCas input) throws EDAException 
	{
		try {
			probeCas(input, null); 
		}
		catch (LAPException e)
		{
			throw new EDAException("Input CAS is not well-formed CAS as EOP EDA input.", e); 
		}
	}

	public TEDecisionWithAlignment classifyEntailment(Vector<Double> fValues)
	{
		// TODO. check existing classifier abstract & Weka tool. 
		// provide default classifier. 
		return null; 
	}

}
