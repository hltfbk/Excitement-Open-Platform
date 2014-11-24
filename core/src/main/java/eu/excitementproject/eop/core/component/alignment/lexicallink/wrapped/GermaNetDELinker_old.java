package eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAligner;
//import eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAlignerFromLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;

/**
 *
 * A version that is kept to check bugs of new leixcal aligner. Will be removed later. 
 *
 * A lexical aligner class that links tokens based on GermaNet.  
 * This is a convenience class. The class utilizes GermaNetWrapper class and 
 * LexicalAligner class to make an aligner.  
 * @author Tae-Gil Noh
 *
 */
@Deprecated 
public class GermaNetDELinker_old implements AlignmentComponent {
	
	/**
	 * 
	 * @param germaNetPath
	 * @throws AlignmentComponentException
	 */
	public GermaNetDELinker_old(String germaNetPath) throws AlignmentComponentException {
		
		try 
		{
			// Filepath, weights  (causes, entails, hypernym, synonym, hyponym, and antonym)  
			// 0 weight will make that relation not added as alignment.link. 
			GermaNetWrapper lex = new GermaNetWrapper(germaNetPath, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0);  
			LexicalAligner theAligner = LexicalAlignerFactory.getLexicalAlignerFromLexicalResource(lex, 1, "8.0", true, null, null); 
			worker = theAligner; 
//			worker = new LexicalAlignerFromLexicalResource(lex); 
		}
		catch (ComponentException ee)
		{
			throw new AlignmentComponentException ("Underlying resource thrown an exception: " + ee.getMessage(), ee); 
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
//	private final LexicalAlignerFromLexicalResource worker; 

	public String getComponentName()
	{
		return this.getClass().getName(); 
	}
	
	public String getInstanceName()
	{
		return null; 
	}
	
	public void close() throws AlignmentComponentException, PairAnnotatorComponentException
	{
		worker.close(); 
	}

}
