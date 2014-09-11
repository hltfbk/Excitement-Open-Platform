package eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAligner;
import eu.excitementproject.eop.core.component.lexicalknowledge.derivbase.DerivBaseResource;

/**
 *
 * A lexical aligner class that links tokens based on DerivBase German resource.  
 * This is a convenience class. The class utilizes DerivBaseResource class and 
 * LexicalAligner class to make an aligner.  
 * @author Tae-Gil Noh
 *
 */
public class DerivBaseDELinker implements AlignmentComponent {

	/**
	 * 
	 * Default parameters. note that this won't work when you use EOP as library. 
	 * 
	 */
	public DerivBaseDELinker() throws AlignmentComponentException
	{
		this(true, 20); 
	}
	
	
	/**
	 * 
	 * @param wordNetPath
	 * @throws AlignmentComponentException
	 */
	public DerivBaseDELinker(boolean useDerivBaseScore, Integer derivSteps) throws AlignmentComponentException {
		
		try 
		{
			DerivBaseResource lex = new DerivBaseResource(useDerivBaseScore, derivSteps); 
			LexicalAligner theAligner = LexicalAlignerFactory.getLexicalAlignerFromLexicalResource(lex, 1, "1.3", true, null, null); 
			worker = theAligner; 
		}
		catch (ComponentException e)
		{
			throw new AlignmentComponentException ("Underlying resource thrown an exception: " + e.getMessage(), e); 
		}
		catch (ConfigurationException ce)
		{
			throw new AlignmentComponentException ("Underlying resource thrown an exception: " + ce.getMessage(), ce); 
		}

		
		
	}
	
	public void annotate(JCas aJCas) throws AlignmentComponentException
	{
		worker.annotate(aJCas); 
	}
	
	// private variable 
	private final LexicalAligner worker; 

	public String getComponentName()
	{
		return this.getClass().getName(); 
	}
	
	public String getInstanceName()
	{
		return null; 
	}
}
