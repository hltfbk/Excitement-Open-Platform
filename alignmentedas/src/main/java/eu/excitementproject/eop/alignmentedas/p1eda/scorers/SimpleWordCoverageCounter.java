package eu.excitementproject.eop.alignmentedas.p1eda.scorers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.LinkUtils;
import eu.excitement.type.alignment.Target;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;

/**
 * This is a very simple "alignment evaluator" which reports two numbers; 
 * "number of covered content word in H", "number of content words in H".
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
	 *  calculateScores will return 2 numbers  
	 *  ( count covered tokens , count all tokens ) 
	 *  
	 *   TODO CONSIDER, extend this to include "count covered content-word tokens, count all content-word"
	 *    tokens ) and return 4 numbers instead of 2. 
	 */
	public SimpleWordCoverageCounter(String alignerID) {
		this.alignerIdToMatch = alignerID; 
	}

	
	@Override
	public Vector<Double> calculateScores(JCas aJCas)
			throws ScoringComponentException {
		
		Vector<Double> result = new Vector<Double>(2); 
		
		// get the list of tokens 
		Collection<Token> allTokens = JCasUtil.select(aJCas, Token.class); 
		int countTokens = allTokens.size(); 
		
		// get all Links with the ID 
		List<Link> linksWithTheID = null; 
		try 
		{
			linksWithTheID = LinkUtils.selectLinksWith(aJCas, this.alignerIdToMatch);
		}
		catch(CASException ce)
		{
			throw new ScoringComponentException("Accessing CAS failed somehow!", ce); 	
		}

		// for each Token, check if this token is covered. 
		int countCoveredTokens = 0; 
		if (linksWithTheID.size() == 0)
		{
			// no need to count 
			countCoveredTokens = 0; 
		}
		else
		{
			for(Token tok : allTokens)
			{
				List<Link> linksHoldingThisToken = filterLinksWithTargetsIncluding(linksWithTheID, tok);
				if (linksHoldingThisToken.size() != 0)
					countCoveredTokens ++; 
			}
		}
		
		// now the two numbers are ready. 
		result.add((double) countCoveredTokens); 
		result.add((double) countTokens); 
		
		return result;
	}
	
	
	/** Maybe this need to go to LinkUtils 
	 * @param fullList   The full list of Links
	 * @param annot      The annotation that is being considered. 
	 * @return
	 */
	public static <T extends Annotation> List<Link> filterLinksWithTargetsIncluding(List<Link> fullList, T annot)
	{
		List<Link> filteredList = new ArrayList<Link>(); 
		
		for (Link l : fullList)
		{
			Target tSideTarget = l.getTSideTarget(); 
			Target hSideTarget = l.getHSideTarget(); 
			
			FSArray arr = null; 
			arr = tSideTarget.getTargetAnnotations(); 
			for (Annotation a : JCasUtil.select(arr, Annotation.class))
			{
				if (a == annot)
				{
					filteredList.add(l); 
					break; 
				}
			}
			
			arr = hSideTarget.getTargetAnnotations(); 
			for (Annotation a : JCasUtil.select(arr, Annotation.class))
			{
				if (a == annot)
				{
					filteredList.add(l); 
					break; 
				}
			}			
		}
		
		return filteredList; 
	}
	
	
	@Override
	public String getComponentName() {
		return getClass().getName(); 
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	private final String alignerIdToMatch; 
	
	
	
}
