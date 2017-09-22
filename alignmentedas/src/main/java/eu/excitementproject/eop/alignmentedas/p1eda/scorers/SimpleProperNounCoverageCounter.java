package eu.excitementproject.eop.alignmentedas.p1eda.scorers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.NP;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.LinkUtils;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * (A Language Independent scorer) 
 * 
 * A simple, POS-based coverage feature extractor that extracts 
 * how much of Hypothesis named entities are covered. The decision 
 * is done purely based on canonical POS type NP (proper noun), and 
 * not based on proper NER. The good thing about this is that this 
 * would work for any language that properly supports canonical-POS. 
 * 
 * So use this as generic, simple approximation feature. 
 * 
 * The scorer always returns two numbers. They are; 
 *  ( number of Proper Nouns covered in H side, number of proper nouns in H side ) 
 * 
 * @author Tae-Gil Noh
 *
 */
public class SimpleProperNounCoverageCounter implements ScoringComponent {

	public SimpleProperNounCoverageCounter() {
	}

	@Override
	public Vector<Double> calculateScores(JCas aJCas)
			throws ScoringComponentException {
		
		Vector<Double> result = new Vector<Double>(); 
		JCas hView = null; 

		// get all NP Tokens. 
		Collection<Token> propNounTokens = new ArrayList<Token>(); 
		Collection<Token> allTokens = null; 
		try {
			hView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW); 
			allTokens = JCasUtil.select(hView, Token.class); 
		}
		catch (CASException ce)
		{
			throw new ScoringComponentException("Accessing CAS HView failed!", ce); 	
		}
		
		for (Token t : allTokens)
		{
			POS p = t.getPos(); 
			if (p == null)
			{
				throw new ScoringComponentException("Cannot proceed, this scoring component requires POS annotated"); 
			}
			 
			if (p.getTypeIndexID() == NP.typeIndexID)
			{
				propNounTokens.add(t); 				
			}
		}
		

		int countPNTokens = propNounTokens.size(); 
		int countCoveredPNTokens = 0; 
		
		logger.debug("calculateScore: count propNoun tokens, HView: " + countPNTokens); 

		List<Link> links = null; 
		try
		{
			links = LinkUtils.selectLinksWith(aJCas, (String) null); 
		}
		catch(CASException ce)
		{
			throw new ScoringComponentException("Accessing CAS failed somehow!", ce); 	
		}
		
		logger.debug("calculateScore: total " + links.size() + " links fetched"); 
		
		// for each Token, check if this token is covered. 
		if (links.size() == 0)
		{
			// no need to count 
			countCoveredPNTokens = 0; 
		}
		else
		{
			for(Token tok : propNounTokens)
			{
				logger.debug("Checking Token " + tok.getCoveredText()); 

				List<Link> linksHoldingThisToken = LinkUtils.filterLinksWithTargetsIncluding(links, tok, Link.Direction.TtoH);
				if (linksHoldingThisToken.size() != 0)
				{
					countCoveredPNTokens ++; 
					logger.debug("The token is covered by " + linksHoldingThisToken.size() + " link(s)."); 
				}				
			}
		}

		// Okay. Now we have the two numbers. Return them as is. 
		result.add((double) countCoveredPNTokens); 
		result.add((double) countPNTokens); 

		return result; 
	}

	
	@Override
	public String getComponentName() {
		return getClass().getName(); 
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	// privates 	
	private final static Logger logger = Logger.getLogger(SimpleProperNounCoverageCounter.class);

}
