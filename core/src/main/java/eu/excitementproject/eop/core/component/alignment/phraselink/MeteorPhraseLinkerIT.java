package eu.excitementproject.eop.core.component.alignment.phraselink;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * 
 * This class provides alignment.Link for phrases in the given JCas. 
 * From TextView phrases to Hypothesis phrases. 
 * 
 * (This is a step-1 aligner -- that is, lookup-aligner, adds all links, does not select/resolve the best one. 
 * It simply adds everything the underlying resource knows.) 
 * 
 * The underlying resource for this class: the Italian Paraphrase table, extracted from parallel corpus
 * by Viviana Antonela Nastase (http://hlt.fbk.eu/people/profile/nastase) 
 * 
 * A AlignmentAnnotator component, that will add Token level alignment.Links (the two Targets of 
 * the link instance hold one, or more tokens.) 
 *  
 * @author Tae-Gil Noh 
 * @since June 2014 
 *
 */
public class MeteorPhraseLinkerIT extends MeteorPhraseResourceAligner {
	
	public MeteorPhraseLinkerIT() throws AlignmentComponentException 
	{
		// zero configuration --- this component loads Meteor English resource and 
		// there is nothing to configure. 
		// Italian paraphrase table from Vivi's resource, here we set maximum length of phrase as 7 words (tokens)
		super("/vivi-paraphrase/data/paraphrase-it", 7);  // Note that this data file is already provided and added in CORE POM dependency. 
				
		// set language ID for language check 
		languageId = "IT"; 
		
		// override link metadata 
		this.alignerID = "MeteorPhraseLink";
		this.alignerVersion = "FBKViviItlianPP10"; 
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
