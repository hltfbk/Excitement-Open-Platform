package eu.excitementproject.eop.core.component.alignment.phraselink;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * 
 * This class provides alignment.Link for phrases in the given JCas. 
 * From TextView phrases to Hypothesis phrases. 
 * (This is a step-1 aligner -- that is, lookup-aligner, adds all links, does not select/resolve the best one. 
 * It simply adds everything the underlying resource knows.) 
 * The resource it is based on is: the English Meteor Paraphrase table. (for the moment, from Meteor 1.5 release). 
 * 
 * A AlignmentAnnotator component, that will add Token level alignment.Links (the two Targets of 
 * the link instance hold one, or more tokens.) 
 * 
 * 
 * @author Tae-Gil Noh 
 * @since June 2014 
 *
 */
public class MeteorPhraseLinkerEN extends MeteorPhraseResourceAligner {
	
	public MeteorPhraseLinkerEN() throws AlignmentComponentException 
	{
		// zero configuration --- this component loads Meteor English resource and 
		// there is nothing to configure. 
		// English paraphrase table from Meteor 1.5, where the maximum length phrase has 7 words
		super("/meteor-1.5/data/paraphrase-en", 7);  // Note that this data file is already provided and added in CORE POM dependency. 
				
		// set language ID for language check 
		languageId = "EN"; 
		
		// override link metadata 
		this.alignerID = "MeteorPhraseLink";
		this.alignerVersion = "MeteorEnglishPP15"; 
		this.linkInfo = "paraphrase"; 
	}

	public void annotate(JCas aJCas) throws AlignmentComponentException 
	{
		// language check 
		String tViewLangId; // language Ids in two views of the given CAS
		String hViewLangId; 
		try 
		{
			tViewLangId = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW).getDocumentLanguage();
			hViewLangId = aJCas.getView(LAP_ImplBase.TEXTVIEW).getDocumentLanguage(); 
		}
		catch(CASException e)
		{
			throw new AlignmentComponentException("Accessing text/hypothesis view failed: CAS object might not be a correct one."); 
		}
		
		if (! ( languageId.equalsIgnoreCase(tViewLangId) && languageId.equalsIgnoreCase(hViewLangId)) )
		{
			throw new AlignmentComponentException("Language ID mismatch: this component provides service for " + languageId + ", but received a JCas with " + tViewLangId + "/" + hViewLangId);
		}
		
		// call super, which does the actual work. 
		super.annotate(aJCas); 
	}
	
	private final String languageId; 
	
}
