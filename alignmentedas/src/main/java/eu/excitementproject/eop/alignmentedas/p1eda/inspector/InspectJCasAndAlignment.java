package eu.excitementproject.eop.alignmentedas.p1eda.inspector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.LinkUtils;
import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * This class provides some static methods that will help you to check 
 * and see the contents of a JCas, mostly about its alignment.links 
 * 
 * @author Tae-Gil Noh 
 * 
 */
public class InspectJCasAndAlignment {

	/**
	 * This method returns Entailment.Pair type's pairID value (string).  
	 * 
	 *  Note that this code assumes only one Entailment.Pair per JCas 
	 *  If that is not the case, the code will warn you, but still return the 
	 *  relevant value from the first Entailment.Pair instance. 
	 *  
	 * @param aJCas
	 * @return
	 * @throws EDAException
	 */
	public static String getTEPairID(JCas aJCas) throws EDAException
	{
		String id = null; 
		
		// check entailment pair, 
		FSIterator<TOP> iter = aJCas.getJFSIndexRepository().getAllIndexedFS(Pair.type); 
		if (iter.hasNext())
		{
			Pair p = (Pair) iter.next(); 
			id = p.getPairID(); 
			
			if (iter.hasNext())
			{
				logger.warn("This JCas has more than one TE Pairs: This P1EDA template only processes single-pair inputs. Any additional pairs are being ignored, and only the first Pair will be processed.");
			}
			return id; 
		}
		else
		{
			throw new EDAException("Input CAS is not well-formed CAS as EOP EDA input: missing TE pair"); 
		}
	}

	/**
	 * This utility method gets the decision label from the jCas. 
	 * (the value can be null, if it has not been set in JCas. )
	 * 
	 *  Note that this code assumes only one Entailment.Pair (where decision label is in) 
	 *  per JCas. If that is not the case, the code will warn you, but still return the 
	 *  relevant value from the first Entailment.Pair instance. 
	 * 
	 * @param aJCas
	 * @return
	 * @throws EDAException
	 */
	public static DecisionLabel getGoldLabel(JCas aJCas) throws EDAException 
	{
		String labelString; 
		DecisionLabel labelEnum; 
		
		FSIterator<TOP> iter = aJCas.getJFSIndexRepository().getAllIndexedFS(Pair.type); 
		if (iter.hasNext())
		{
			Pair p = (Pair) iter.next(); 
			labelString = p.getGoldAnswer(); 
			
			if (labelString == null) // there is no gold answer annotated in this Pair
				return null; 
			
			labelEnum = DecisionLabel.getLabelFor(labelString); 
			
			if (iter.hasNext())
			{
				logger.warn("This JCas has more than one TE Pairs: This P1EDA template only processes single-pair inputs. Any additional pairs are being ignored, and only the first Pair will be processed.");
			}
			return labelEnum; 
		}
		else
		{
			throw new EDAException("Input CAS is not well-formed CAS as EOP EDA input: missing TE pair"); 
		}
	}

	/**
	 * This method gets a JCas with alignment.Links, and reports links (only that) covers 
	 * each of H-side units (here, words).  
	 * 
	 * Returns a list where its length is number of tokens in Hypothesis side, and 
	 * each of the list element holds a set of Links where each set holds alignment.Link 
	 * instances that covers that token on Hypothesis side.
	 * 
	 * @param aJCas
	 * @return
	 */
	public static List<List<Link>>getCoveringLinksTokenLevel(JCas aJCas) throws CASException
	{
		ArrayList<List<Link>> coveringLinks = new ArrayList<List<Link>>(); 

		// First, get the list of H tokens 		
		JCas hView = null; 		
		Collection<Token> allTokens = null; 
		hView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW); 
		allTokens = JCasUtil.select(hView, Token.class); 
		
		int countTokens = allTokens.size(); 
		logger.debug("getCoveringLinksWordLevel: count all tokens, HView: " + countTokens); 
		
		// get all links 
		List<Link> links = LinkUtils.selectLinksWith(aJCas, Token.class);
		logger.debug("getCoveringLinksWordLevel: total " + links.size() + " links fetched"); 

		// from first H token to last, fill the sets. 		
		for (Token tok : allTokens)
		{
			logger.debug("getCoveringLinksWordLevel: Checking Token " + tok.getCoveredText()); 
			List<Link> linksHoldingThisToken = LinkUtils.filterLinksWithTargetsIncluding(links, tok, Link.Direction.TtoH);
			coveringLinks.add(linksHoldingThisToken); 
			logger.debug("getCoveringLinksWordLevel: found " + linksHoldingThisToken.size() + "links"); 
		}
		
		return coveringLinks; 
	}
		

	// the logger 
	private static Logger logger = Logger.getLogger(InspectJCasAndAlignment.class); 

}
