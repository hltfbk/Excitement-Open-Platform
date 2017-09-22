package eu.excitementproject.eop.alignmentedas.p1eda.scorers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;

//import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.NP;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.V;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.LinkUtils;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * (A Language Independent scorer) 
 * It would be ideal, if we can count coverage of "main verb", or "predicates" 
 * 
 * Since that is not possible with simple "lexical" level alignments, here we 
 * try to check "verbs". We try to exclude all auxiliary verbs .. but that isn't easily
 * possible in canonical POSes. 
 * 
 * So in this simple, language independent module, we only try "verb coverage". 
 * 
 * The scorer always returns two numbers. They are; 
 * ( number of Verbs covered in H side, number of verbs in H side ) 
 * 
 * @author Tae-Gil Noh
 *
 */
public class SimpleVerbCoverageCounter implements ScoringComponent {

	public SimpleVerbCoverageCounter() {
	}

	@Override
	public Vector<Double> calculateScores(JCas aJCas)
			throws ScoringComponentException {
		Vector<Double> result = new Vector<Double>(); 
		JCas hView = null; 

		// get all Verb Tokens. 
		Collection<Token> verbTokens = new ArrayList<Token>(); 
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
			 
			if (p.getTypeIndexID() == V.typeIndexID)
			{
				verbTokens.add(t); 				
			}
		}
		

		int countVTokens = verbTokens.size(); 
		int countCoveredVTokens = 0; 
		
		logger.debug("calculateScore: count verb tokens, HView: " + countVTokens); 

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
			countCoveredVTokens = 0; 
		}
		else
		{
			for(Token tok : verbTokens)
			{
				logger.debug("Checking Token " + tok.getCoveredText()); 

				List<Link> linksHoldingThisToken = LinkUtils.filterLinksWithTargetsIncluding(links, tok, Link.Direction.TtoH);
				if (linksHoldingThisToken.size() != 0)
				{
					countCoveredVTokens ++; 
					logger.debug("The token is covered by " + linksHoldingThisToken.size() + " link(s)."); 
				}				
			}
		}

		// Okay. Now we have the two numbers. Return them as is. 
		result.add((double) countCoveredVTokens); 
		result.add((double) countVTokens); 

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

	
	// TODO: 
	// Keep list of common auxiliary verbs (let's say, stop verbs), and ignore them in the counting. 
	// And this list, is overriden by extension of this base case; thus language-specific versions can 
	// be built from this class as super... 
	
}
