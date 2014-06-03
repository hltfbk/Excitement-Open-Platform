package eu.excitementproject.eop.core.component.alignment.phraselink;

import java.io.IOException;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;

/**
 * This component annotates a JCas based on Meteor (or Meteor-like) paraphrase
 * resource. 
 * 
 * Actual look up depends on MeteorPhraseTable class.  
 * 
 * annotate() adds alignment.Link/Target instances that link Token annotations. 
 * Phrase-to-Phrase link is represented by alignment.Targets that holds more than one Token. 
 * 
 * @author Tae-Gil Noh
 */
public class MeteorPhraseResourceAligner implements AlignmentComponent {

	public MeteorPhraseResourceAligner(String resourcePath) throws AlignmentComponentException
	{		
		this.resourcePath = resourcePath; 
		
		try {
			this.table = new MeteorPhraseTable(resourcePath); 
		}
		catch (IOException e)
		{
			throw new AlignmentComponentException("Loading the paraphrase table with the following resource path have failed: " + resourcePath, e); 
		}
	}
	
	public void annotate(JCas aJCas)
	{
		
	}
	
	public String getComponentName()
	{
		return this.getClass().getName(); 
	}
	
	public String getInstanceName()
	{
		return resourcePath; 
	}
	
	private final String resourcePath; 
	@SuppressWarnings("unused")
	private final MeteorPhraseTable table; 
}
