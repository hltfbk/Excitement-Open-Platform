package eu.excitementproject.eop.alignmentedas.p1eda.scorers;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.LinkUtils;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.core.component.alignment.phraselink.IdenticalLemmaPhraseLinker;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * (A Language Independent scorer) 
 * 
 * This is a very simple "alignment evaluator" which reports the following numbers; 
 * "number of covered word in H", "number of words in H".
 * 
 * calculateScores() method will return 4 numbers  
 *  ( count covered tokens , count all tokens, count covered content-tokens, count all content-tokens) 
 *  
 * CAS needs POS annotations and Token annotations, minimally. 
 * 
 * @author Tae-Gil Noh
 *
 */
public class SimpleWordCoverageCounter implements ScoringComponent {

	/**
	 *  Argument version: gets one linkID of alignment.Link. Then the module uses alignment.Link instances 
	 *  with that ID, to calculate "coverage". 
	 *  If null given, the module uses, *all* link instances to calculate coverage. 
	 *  
	 *  calculateScores will return 4 numbers  
	 *  ( count covered tokens , count all tokens, count covered content-tokens, count all content-tokens) 
	 *  
	 *  Content token means, tokens with POS other than "PUNC", "PP", "O", "CONJ", "ART"
	 */
	public SimpleWordCoverageCounter(String alignerID) {
		this.alignerIdToMatch = alignerID; 
	}

	
	@Override
	public Vector<Double> calculateScores(JCas aJCas)
			throws ScoringComponentException {
		
		Vector<Double> result = new Vector<Double>(4);  // this module always returns four numbers
		JCas hView = null; 
		
		// get the list of tokens 
		Collection<Token> allTokens = null; 
		try {
			hView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW); 
			allTokens = JCasUtil.select(hView, Token.class); 
		}
		catch (CASException ce)
		{
			throw new ScoringComponentException("Accessing CAS HView failed!", ce); 	
		}
		int countTokens = allTokens.size(); 
		int countContentTokens = allTokens.size(); // will be reduced within the loops, to reflect only content words. 
		int countCoveredTokens = 0; 
		int countCoveredContentTokens = 0; 
		
		logger.debug("calculateScore: count all tokens, HView: " + countTokens); 
		
		// get all Links with the ID 
		if (alignerIdToMatch == null)
		{
			logger.debug("calculateScore: all links are fetched");
		}
		else
		{
			logger.debug("calculateScore: links with ID \"" + this.alignerIdToMatch + "\" are fetched");			
		}

		List<Link> linksWithTheID = null; 
		try 
		{
			linksWithTheID = LinkUtils.selectLinksWith(aJCas, this.alignerIdToMatch);
		}
		catch(CASException ce)
		{
			throw new ScoringComponentException("Accessing CAS failed somehow!", ce); 	
		}
		
		logger.debug("calculateScore: total " + linksWithTheID.size() + " links fetched"); 

		// for each Token, check if this token is covered. 
		if (linksWithTheID.size() == 0)
		{
			// no need to count 
			countCoveredTokens = 0; 
		}
		else
		{
			for(Token tok : allTokens)
			{
				logger.debug("Checking Token " + tok.getCoveredText()); 

				Boolean nonContentToken = isNonContentToken(tok); 
				if (nonContentToken)
				{
					countContentTokens --; 
				}

				List<Link> linksHoldingThisToken = LinkUtils.filterLinksWithTargetsIncluding(linksWithTheID, tok, Link.Direction.TtoH);
				if (linksHoldingThisToken.size() != 0)
				{
					countCoveredTokens ++; 
					logger.debug("The token is covered by " + linksHoldingThisToken.size() + " link(s)."); 
					if (!nonContentToken)
					{
						countCoveredContentTokens++; 
					}
				}				
			}
		}
		
		// now the two numbers are ready. 
		result.add((double) countCoveredTokens); 
		result.add((double) countTokens); 
		result.add((double) countCoveredContentTokens); 
		result.add((double) countContentTokens); 
		
		return result;
	}
		
//	/** Maybe this need to go to LinkUtils 
//	 * TODO: export this method with "direction selection" option to LinkUtils 
//	 * 
//	 * @param fullList   The full list of Links
//	 * @param annot      The annotation that is being considered. 
//	 * @return
//	 */
//	public static <T extends Annotation> List<Link> filterLinksWithTargetsIncluding(List<Link> fullList, T annot)
//	{
//		List<Link> filteredList = new ArrayList<Link>(); 
//		
//		for (Link l : fullList)
//		{
//			Target tSideTarget = l.getTSideTarget(); 
//			Target hSideTarget = l.getHSideTarget(); 
//			
//			FSArray arr = null; 
//			arr = tSideTarget.getTargetAnnotations(); 
//			for (Annotation a : JCasUtil.select(arr, Annotation.class))
//			{
//				if (a == annot)
//				{
//					filteredList.add(l); 
//					break; 
//				}
//			}
//			
//			arr = hSideTarget.getTargetAnnotations(); 
//			for (Annotation a : JCasUtil.select(arr, Annotation.class))
//			{
//				if (a == annot)
//				{
//					// In this score component, we ignore HtoT case. (only TtoH and bidirection) 
//					// Hmm. possible better coding for this? 
//					if (l.getDirection() == Link.Direction.HtoT)
//						break; 
//					filteredList.add(l); 
//					break; 
//				}
//			}			
//		}
//		
//		return filteredList; 
//	}
	
	
	@Override
	public String getComponentName() {
		return getClass().getName(); 
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	/**
	 * This utility checks if the token is one of non-content token type. 
	 * (e.g. "PUNC", "PP", "O", "CONJ", "ART"). Actual definition of non content POSes
	 * are borrowed from a static definition set in IdenticalLemmaPhraseLinker. 
	 * 
	 * @param t The token to be checked. 
	 * @return
	 */
	private boolean isNonContentToken(Token t) throws ScoringComponentException 
	{
		
		POS p = t.getPos(); 
		if (p == null)
		{
			throw new ScoringComponentException("The module requires POS annotated for the Tokens, to check non-content words"); 
		}
		String s = p.getType().toString(); 	
		String typeString = s.substring(s.lastIndexOf(".") + 1); 
		//String logline = t.getCoveredText() + "/" + typeString + ", ";
		Boolean result = IdenticalLemmaPhraseLinker.isNonContentPos.containsKey(typeString); 
		logger.debug(t.getCoveredText() + "/" + typeString + ": isNonContentToken: " + result); 

		return result; 
	}
	
	private final String alignerIdToMatch; 
	private final static Logger logger = Logger.getLogger(SimpleWordCoverageCounter.class);


	
}
