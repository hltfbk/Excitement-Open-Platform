package eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
//import eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAligner;
import eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAlignerFromLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.transDm.GermanTransDmResource;

/**
 *
 * A lexical aligner class that links tokens based on TransDM German resource.  
 * This is a convenience class. The class utilizes GermanTransDMResource class and 
 * LexicalAligner class to make an aligner.  
 * @author Tae-Gil Noh
 *
 */
public class GermanTransDMDELinker implements AlignmentComponent {

	/**
	 * 
	 * Default parameters. note that this won't work when you use EOP as library. 
	 * 
	 */
	public GermanTransDMDELinker() throws AlignmentComponentException
	{
		this("all"); 
	}
	
	
	/**
	 * 
	 * @param wordNetPath
	 * @throws AlignmentComponentException
	 */
	public GermanTransDMDELinker(String simMeasureChoice) throws AlignmentComponentException {
		
		try 
		{
			GermanTransDmResource lex = new GermanTransDmResource(simMeasureChoice);  
//			LexicalAligner theAligner = LexicalAlignerFactory.getLexicalAlignerFromLexicalResource(lex, 1, "1.3", true, null, null); 
//			worker = theAligner; 
			worker = new LexicalAlignerFromLexicalResource(lex); 
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
//	private final LexicalAligner worker; 
	private final LexicalAlignerFromLexicalResource worker; 

	public String getComponentName()
	{
		return this.getClass().getName(); 
	}
	
	public String getInstanceName()
	{
		return null; 
	}
	
	public void close() throws AlignmentComponentException
	{
		worker.close(); 
	}

}
