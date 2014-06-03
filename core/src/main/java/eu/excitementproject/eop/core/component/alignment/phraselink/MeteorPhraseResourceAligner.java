package eu.excitementproject.eop.core.component.alignment.phraselink;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;

/**
 * This component annotates a JCas based on Meteor (or Meteor-like) para-phrase
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

	public MeteorPhraseResourceAligner(String resourcePath)
	{		
		this.resourcePath = resourcePath; 
		this.table = new MeteorPhraseTable(resourcePath); 
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
