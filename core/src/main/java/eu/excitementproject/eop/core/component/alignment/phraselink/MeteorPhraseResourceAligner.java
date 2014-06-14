package eu.excitementproject.eop.core.component.alignment.phraselink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil; 
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * This component annotates a JCas based on Meteor (or Meteor-like) paraphrase
 * resource. This class itself is not supposed to be used by end-users. 
 * A thin-wrapper per each language will be provided, that is using this class. 
 * 
 * Actual look up depends on MeteorPhraseTable class.  
 * annotate() adds alignment.Link/Target instances that link Token annotations. 
 * Phrase-to-Phrase link is represented by alignment.Targets that holds more than one Token. 
 * 
 * @author Tae-Gil Noh
 */
public class MeteorPhraseResourceAligner implements AlignmentComponent {

	public MeteorPhraseResourceAligner(String resourcePath, int maxPhraseLength) throws AlignmentComponentException
	{	
		// initialize private final variables 
		//logger = Logger.getLogger(this.getClass().toString()); 
		this.resourcePath = resourcePath; 
		this.maxPhraseLength = maxPhraseLength; 
		
		// load table. 
		try {
			this.table = new MeteorPhraseTable(resourcePath); 
		}
		catch (IOException e)
		{
			throw new AlignmentComponentException("Loading the paraphrase table with the following resource path have failed: " + resourcePath, e); 
		}
	}
	
	public void annotate(JCas aJCas) throws AlignmentComponentException 
	{
		if (aJCas == null)
			throw new AlignmentComponentException("annotate() got a null JCas object."); 
		
		JCas textView; 
		JCas hypoView; 
		try {
			textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
			hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
			// TODO language check. 
		}
		catch (CASException e)
		{
			throw new AlignmentComponentException(e); 
		}
		
		// get all candidates on Text view 
		List<String> phraseCandidatesInTextView = getPhraseCandidatesFromSOFA(textView, maxPhraseLength); 
		String hypoViewSofaText = hypoView.getDocumentText().toLowerCase(); 
		String textViewSofaText = textView.getDocumentText().toLowerCase(); 
		
		// for each candidate, query table. 
		for(String cand : phraseCandidatesInTextView)
		{
			List<MeteorPhraseTable.ScoredString> rhsAndProbList = table.lookupParaphrasesFor(cand);

			// if there is match (candidate is on table as LHS); 
			if (rhsAndProbList.size() > 0)
			{
				// check each RHS if that RHS does exists on Hypothesis view SOFA text. 
				for(MeteorPhraseTable.ScoredString tuple : rhsAndProbList)
				{
					String lhs = cand; 
					String rhs = tuple.getString(); 
					if (hypoViewSofaText.contains(rhs))
					{
						// Okay. rhs does exist in HYPOTHESIS view SOFA. 
						// Find out locations and annotate one (or more) phrase Links. 
						// (note that multiple link is only possible if same lhs or rhs occurrs multiple 
						// time on text or hypothesis) 
						List<Integer> rhsOccurrences = getOccurrencePoints(hypoViewSofaText, rhs); 												
						for(int rhsBegin : rhsOccurrences)
						{
							List<Integer> lhsOccurrences = getOccurrencePoints(textViewSofaText, lhs);
							for (int lhsBegin : lhsOccurrences)
							{
								addAlignmentLinksOnTokenLevel(textView, hypoView, lhsBegin, lhsBegin + lhs.length(), rhsBegin, rhsBegin + rhs.length()); 								
							}
						}
					}
				}			
			}
		}
		
		
		
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

	/**
	 * Utility method that adds alignment.Links on Token level. 
	 * Usage is about like this.
	 * 
	 * 
	 * example
	 * <PRE>
	 *                          1         2         3         4
	 *                012345678901234567890123456789012345678901234567890
	 * TEXTVIEW SOFA  He went there in person to dwell on the importance.  
	 * HYPOVIEW SOFA  He went there to explain the significance. 
	 * </PRE> 
	 * 
	 * <P> 
	 * And let's assume that we want to link "to dwell on the importance" (27 to 49 on TEXTVIEW)
	 * to "to explain the significance" (14 to 40 on HYPOVIEW). Then a call like the following will 
	 * make it happen. 
	 * 
	 * method(hypoview, textview, 27, 49, 14, 40)
	 * 
	 * With this call, all tokens that are covering TEXT SOFA text position 24 - 49 will be grouped in alignment.Group, 
	 * and all tokens that are covering HYPO SOFA text position 14 - 40 will be grouped in another alignment.Group
	 * and they will be linked by alignment.Link. 
	 * 
	 * NOTE 
	 *  
	 * @param viewFrom The view where tokens will be grouped as a target for alignment.link From. 
	 * @param viewTo   The view where tokens will be grouped as a target for alignment.link Target. 
	 * @param fromStart start position of Tokens in viewFrom
	 * @param fromEnd   end position of Tokens in viewFrom
	 * @param toStart   start position of Tokens in viewTo
	 * @param toEnd     end position of Tokens in viewTo
	 * @return Link    the successful call will return the newly generated Link instance. 
	 */
	public static Link addAlignmentLinksOnTokenLevel(JCas viewFrom, JCas viewTo, int fromStart, int fromEnd, int toStart, int toEnd)
	{
		Link newLink = null; 
		// TODO write. 
		logger.debug("got request to add link from TEXT -> HYPO group"); 
		logger.debug("TEXT group: " + fromStart + " to " + fromEnd + ":" + viewFrom.getDocumentText().substring(fromStart, fromEnd)); 
		logger.debug("HYPO group: " + toStart + " to " + toEnd +":" + viewTo.getDocumentText().substring(toStart, toEnd)); 
		//logger.debug("TEXT SOFA: " + viewFrom.getDocumentText()); 
		//logger.debug("HYPO SOFA: " + viewTo.getDocumentText()); 
		
		return newLink; 
	}
	
	public String getComponentName()
	{
		return this.getClass().getName(); 
	}
	
	public String getInstanceName()
	{
		return resourcePath; 
	}
	
	// a utility method 
	static List<Integer> getOccurrencePoints(String holder, String substring)
	{
		List<Integer> result = new ArrayList<Integer>(); 
		int searchFrom = 0; 
		int begin; 
		while((begin = holder.indexOf(substring, searchFrom)) > 0)
		{
			int end = begin + substring.length(); 
			result.add(begin); 
			searchFrom = end; 
		}
		return result; 
	}
	
	
	private final String resourcePath; 
	private final MeteorPhraseTable table; 
	private final int maxPhraseLength; 
	private final static Logger logger = Logger.getLogger(MeteorPhraseResourceAligner.class);
}
