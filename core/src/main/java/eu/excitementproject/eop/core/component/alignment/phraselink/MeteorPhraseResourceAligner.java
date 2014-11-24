package eu.excitementproject.eop.core.component.alignment.phraselink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.uimafit.util.JCasUtil; 
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.Target;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * This component annotates a JCas based on Meteor (or Meteor-like) paraphrase
 * resource. This class itself is not supposed to be used by end-users. 
 * A thin-wrapper per each language + resource will be provided for end users, and that 
 * end user class would be extending this class. 
 * 
 * Actual look up depends on MeteorPhraseTable class.  
 * annotate() adds alignment.Link/Target instances that link Token annotations. 
 * Phrase-to-Phrase link is represented by alignment.Targets that holds more than one Token. 
 * 
 * The class has some static methods that might be useful for other aligners. For example, 
 * "addOneAlignmentLinkOnTokenLevel()" is public, and might be useful. 
 * 
 * All comparison / match is done on surface (Sofa text) level, and then the matching 
 * positive links are established on CAS Tokens. Also note that all comparisons are done 
 * as lower-case, as the underlying Meteor resource requires.  
 * 
 * TODO: groupLabel part is ignored in the class yet. To be added. 
 * 
 * Note: You can easily add a paraphrase linker, from any "Meteor-like paraphrase data file" in resource path. 
 * For an usage example, check MeteorPhraseLinkerEN class, which provides end-user component with Meteor English table. 
 * 
 * @author Tae-Gil Noh
 * @since June 2014
 */
public class MeteorPhraseResourceAligner implements AlignmentComponent {

	public MeteorPhraseResourceAligner(String resourcePath, int maxPhraseLength) throws AlignmentComponentException
	{	
		// initialize private final variables 
		//logger = Logger.getLogger(this.getClass().toString()); // we use a static instance  
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
		// intro log
		logger.info("annotate() called with a JCas with the following T and H;  ");   
		
		if (aJCas == null)
			throw new AlignmentComponentException("annotate() got a null JCas object."); 
		
		JCas textView; 
		JCas hypoView; 
		try {
			textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
			hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
			// note - language check should be done by class that extends this class 
		}
		catch (CASException e)
		{
			throw new AlignmentComponentException("Failed to access the Two views (TEXTVIEW, HYPOTHESISVIEW)", e); 
		}
		
		logger.info("TEXT: " + textView.getDocumentText()); 
		logger.info("HYPO: " + hypoView.getDocumentText());  
		
		int countAnnotatedLinks = 0; 
		
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
						// Okay. rhs seems to exist in HYPOTHESIS view SOFA. (as a token, or as a sub-token -> we will add only if it is a full Token)
						
						// Find out locations and annotate one (or more) phrase Links. 
						// (note that multiple link is only possible if same lhs or rhs occurrs multiple 
						// time on text or hypothesis) 
						List<Integer> rhsOccurrences = getOccurrencePoints(hypoViewSofaText, rhs); 												
						for(int rhsBegin : rhsOccurrences)
						{
							List<Integer> lhsOccurrences = getOccurrencePoints(textViewSofaText, lhs);
							for (int lhsBegin : lhsOccurrences)
							{
								try {
									// generate a new Link with Two Targets  
									Link aLink = addOneAlignmentLinkOnTokenLevel(textView, hypoView, lhsBegin, lhsBegin + lhs.length(), rhsBegin, rhsBegin + rhs.length(), Link.Direction.TtoH); 								
									
									// Do we have tokens for them? --- check aLink created. 
									// note that the link is created only RHS exist as a token in Hypothesis View. 
									if (aLink == null)
									{   // pass. Hypothesis Text does include RHS, but only as a sub-token. (say, rhs is "Jew", within token "Jewish". ) 
										continue; 
									}
									
									// add Meta-information on the Link. 
									// setStrength() .setAlignerID() .setAlignerVersion() .setLinkInfo(). (Also groupLabel, if using that)
									aLink.setStrength(tuple.getScore());
									aLink.setAlignerID(this.alignerID); 
									aLink.setAlignerVersion(this.alignerVersion); 
									aLink.setLinkInfo(this.linkInfo); 														
									countAnnotatedLinks ++; 
								}
								catch(CASException e)
								{
									throw new AlignmentComponentException("JCas access failed while adding Links", e); 
								}
							}
						}
					}
				}			
			}
		}
		
		// outro log 
		logger.info("annotate() added " + countAnnotatedLinks + " links to the CAS." ); 
		
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
	 * <P> 
	 * NOTE
	 *  - This method *does not* add any "meta-level" information, such as aligner ID, etc. Those has to be added by the caller on the returned new Link instance.   
	 *  - The caller must do after the call  .setStrength() .setAlignerID() .setAlignerVersion() .setLinkInfo(). (Also groupLabel, if using that) 
	 *  - (But this method does add the new Link to CAS annotation index)
	 * 
	 * @param viewFrom The view where tokens will be grouped as a target for alignment.link From. 
	 * @param viewTo   The view where tokens will be grouped as a target for alignment.link Target. 
	 * @param fromStart start position of Tokens in viewFrom
	 * @param fromEnd   end position of Tokens in viewFrom
	 * @param toStart   start position of Tokens in viewTo
	 * @param toEnd     end position of Tokens in viewTo
	 * @return Link    the successful call will return the newly generated Link instance. 
	 */
	public static Link addOneAlignmentLinkOnTokenLevel(JCas textView, JCas hypoView, int fromBegin, int fromEnd, int toBegin, int toEnd, Link.Direction dir) throws CASException
	{
		// declare what is being done on log ... 
		logger.debug("got request to add link from TEXT -> HYPO group"); 
		logger.debug("TEXT group: " + fromBegin + " to " + fromEnd + ":" + textView.getDocumentText().substring(fromBegin, fromEnd)); 
		logger.debug("HYPO group: " + toBegin + " to " + toEnd +":" + hypoView.getDocumentText().substring(toBegin, toEnd)); 
		//logger.debug("TEXT SOFA: " + viewFrom.getDocumentText()); 
		//logger.debug("HYPO SOFA: " + viewTo.getDocumentText()); 
		
		// prepared two alignment Targets
		// FROM side 
		//List<Token> tokens = JCasUtil.selectCovering(textView, Token.class, fromBegin, fromEnd); 
		List<Token> tokens = tokensBetween(textView, fromBegin, fromEnd); 
		Target textTarget = prepareOneTarget(textView, tokens); 
		
		// TO side 
		//tokens = JCasUtil.selectCovering(hypoView,  Token.class,  toBegin,  toEnd);  		
		tokens = tokensBetween(hypoView, toBegin, toEnd); 
		Target hypoTarget = prepareOneTarget(hypoView, tokens);
		
		if ((textTarget == null) || (hypoTarget == null))
		{
			logger.debug("no matching Tokens (probably rhs exist only as sub-token, not a full token). --- not making Link instance and returning null."); 
			return null; 
		}
		
		// Okay. we have two targets. Make one Link.
		Link theLink = new Link(hypoView); 
		theLink.setTSideTarget(textTarget); 
		theLink.setHSideTarget(hypoTarget); 
		theLink.setDirection(dir); 
	
		logger.debug("TSideTarget, "  + textTarget.getTargetAnnotations().size() + " tokens, covers: " + textTarget.getCoveredText()); 
		logger.debug("HSideTarget, "  + hypoTarget.getTargetAnnotations().size() + " tokens, covers: " + hypoTarget.getCoveredText()); 

		theLink.setBegin(hypoTarget.getBegin()); 
		theLink.setEnd(hypoTarget.getEnd());
		
		theLink.addToIndexes(); 
		
		// The caller must do after the call  
		// .setStrength()
		// .setAlignerID()
		// .setAlignerVersion()
		// .setLinkInfo(); 
		
		return theLink; 
	}
	
	// a utility method used by addAlignmentLinnksOnTokenLevel
	// Gets one View and a set of Tokens (of that view) and makes one alignment.Target 
	private static Target prepareOneTarget(JCas view, Collection<Token> tokens)
	{		
		int countTokens = tokens.size(); 
		if (countTokens == 0) // check; null means no Tokens for target.  
			return null; 
	
		Target aTarget = new Target(view); 

		FSArray annots = new FSArray(view, countTokens); 
		aTarget.setTargetAnnotations(annots); 
		Iterator<Token> itr = tokens.iterator(); 
		int begin = -1;  // I am using -1 as "not set yet". 
		int end=0; 
		for(int i=0; i < countTokens; i++)
		{
			Token t = itr.next(); 
			if (begin == -1) // if not set. 
				begin = t.getBegin(); 
			end = t.getEnd();  // we are assuming that collection tokens is ordered. 
			annots.set(i, t); 
		}
		
		aTarget.setBegin(begin); 
		aTarget.setEnd(end); 
		aTarget.addToIndexes(); 
		
		return aTarget; 
	}
	
	private static List<Token> tokensBetween(JCas aJCas, int from, int to)
	{
		List<Token> tokenList = new ArrayList<Token>(); 
		
		for (Token token: JCasUtil.select(aJCas, Token.class))
		{
			if ( (token.getBegin() >= from) && (token.getEnd() <= to))
			{
				tokenList.add(token); 
			}
		}
		return tokenList; 
	}
	
	public String getComponentName()
	{
		return this.getClass().getName(); 
	}
	
	public String getInstanceName()
	{
		return resourcePath; 
	}
	
	public void close() throws AlignmentComponentException
	{
		// nothing to close on this aligner. 
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

	// default link metadata, can be (or should be) overridden by subclasses. 
	protected String alignerID = "PhraseLink";
	protected String alignerVersion = "MeteorPhraseTable"; 
	protected String linkInfo = "paraphrase"; 

}
