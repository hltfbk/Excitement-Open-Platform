package eu.excitementproject.eop.core.component.alignment.lexicallink;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;

/**
 * TODO full document
 * (what this modules does) 
 * (goal: use rich lexical resources as lexical aligner) 
 * 
 * (how it does what it does) 
 * 
 * 
 * @author Tae-Gil Noh
 *
 */
public class LexicalAlignerFromLexicalResource implements AlignmentComponent {

	public LexicalAlignerFromLexicalResource()  {
		// TODO 
		// once annotate line is outlined, fill up needed information 
		// starting from "LexicalResource" instance ... 
	}

	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {
		// TODO 
		// outline -- after check what we do on Meteor and outline what it should look like 
		
	}

	
	
	
	@Override
	public String getComponentName()
	{
		return this.getClass().getName(); 
	}

	@Override
	public String getInstanceName()
	{
		return null; 
	}

}
