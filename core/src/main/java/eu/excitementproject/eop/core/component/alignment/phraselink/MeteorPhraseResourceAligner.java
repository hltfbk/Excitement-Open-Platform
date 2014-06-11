package eu.excitementproject.eop.core.component.alignment.phraselink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil; 
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

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

	/**
	 * This method is a helper utility that is required to look up Meteor Phrase tables. 
	 * 
	 * Basically, returns all possible phrase candidates up to N words in a List<String> 
	 * 
	 * The method uses Token annotation in JCas to generate possible candidates. Thus, 
	 * a tokenization annotator should have annotated this JCas before.  
	 * 
	 * @param JCas aJCas The view, that holds the sentence(s) to be analyzed. 
	 * @param int uptoN The maximum number of 
	 * @return 
	 */
	public static List<String> getPhraseCandidatesFromSOFA(JCas aJCas, int uptoN)
	{
		// sanity check 
		assert(aJCas !=null); 
		assert(uptoN > 0); 
		
		// list for result, 
		List<String> result = new ArrayList<String>(); 
		
		// ok. work. first; 
		// get all Tokens (by appearing orders...) 
		Collection<Token> t = JCasUtil.select(aJCas, Token.class); 
		Token[] tokens = t.toArray(new Token[t.size()]); 
		
		// then; 
		// for each Token, start uptoN process. 
		for(int i=0; i < tokens.length; i++)
		{
			for(int j=0; (j < uptoN) && (i+j < tokens.length); j++ )
			{
				Token leftEnd = tokens[i]; 
				Token rightEnd = tokens[i+j]; 
				String text = aJCas.getDocumentText().substring(leftEnd.getBegin(), rightEnd.getEnd()); 
				// and store in lower case. 
				result.add(text.toLowerCase()); 
			}			
		}
		
		// done 
		// all candidates are store here. 
		return result; 
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
