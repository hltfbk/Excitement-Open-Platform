package eu.excitementproject.eop.alignmentedas.p1eda.inspector;

//import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.LinkUtils;
import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.FeatureValue;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * 
 * This class holds (mostly) static methods to look into a result of P1EDA SimpleWordCovereage configurations. 
 * 
 * P1EDA returns TEDecisionWithAlignment type, which holds JCas (that holds all alignments and annotations of T-H), 
 * and feature values used in the configuration. 
 * 
 * The utility methods in this class uses those two returned data type to look-into why the EDA 
 * decided the decision. 
 * 
 * @author Tae-Gil Noh 
 *
 */

public class InspectorForSimpleWordCoverageP1EDA {

	/**
	 * Okay, this is a method designed to inspect a decision on a T-H pair, from a P1EDA. 
	 * Pass TEDecisionWithAlignment object, the method will output various useful information 
	 * to the STDOUT. Hopefully, the information would help you understand why the EDA made 
	 * such a decision --- and improve aligners, features, and so on for the EDA. 
	 * 
	 * More specifically, it outputs the following informations (in this order, as texts). 
	 * 
	 * - a. (short) summary of JCas content & annotations. 
	 * - b. (indexed) list of alignment.Link in the JCas. 
	 * - c. (per H-words) coverage histogram. (how much this word is covered from T, (reconstructed) EDA's internal belief.) 
	 * - d. (per H-words) covering links. (evidences for the coverage histogram, which links cover this word?) 
	 * - e. (a feature vector) feature value that represents the pair, as used by EDA's underlying classifier. 
	 * 
	 * TODO MAYBE - f. some info for classifier model? for better understanding effect of feature values? 
	 * 
	 * @param decision
	 */
	public static void inspectDecision(TEDecisionWithAlignment decision, PrintStream out)
	{
		// a. get JCas, output JCas summary 
		// calling summarizeJCasWordLevel 
		String CASsummary; 
		try {
			CASsummary = InspectUtilsJCasAndLinks.summarizeJCasWordLevel(decision.getJCasWithAlignment()); 
		}
		catch (CASException ce)
		{
			logger.error("inspectDecision: failed to read JCas, unable to summarize JCas."); 
			return; 
		}
		out.println("* Summary of the TH-pair in lexical level"); 
		out.println(CASsummary); 
				
		// b. output alignment.Link summary 
		// calling summarizeAlignmentLinks
		String linkSummary; 
		try {
			linkSummary = InspectUtilsJCasAndLinks.summarizeAlignmentLinks(decision.getJCasWithAlignment()); 
		}
		catch (CASException ce)
		{
			logger.error("inspectDecision: failed to read JCas, unable to summarize JCas."); 
			return; 
		}
		out.println("* Summary of the all alignment Links in the JCas"); 
		out.println(linkSummary); 
		
		// c & d:  coverage histogram and covering links 
		String coverageInfo; 
		try {
			coverageInfo = coverageHistrogramP1EDASimpleWordCoverage(decision.getJCasWithAlignment());
		}
		catch (CASException ce)
		{
			logger.error("inspectDecision: failed to read JCas, unable to make coverage information"); 
			return; 
		}
		out.println("* Summary of the all Word Coverage from the Links");  
		out.println(coverageInfo); 
		
		// e. output feature vector, as-is. 
		Vector<FeatureValue> featureList = decision.getFeatureVector(); 
		String featureLine=featureList.toString(); 
		out.println("* List of feature values used by the EDA"); 
		out.println(featureLine.toString()); 
	}

	/**
	 * 
	 * Use this method to "emulate" internal belief of the P1EDA SimpleWordCoverage mode, 
	 * and get the "coverage" per each word, and "covering links" that contributed to the belief.  
	 * 
	 * @return returns a multi-line string where each line shows each word, coverage value (always 0/1 for SWC model), and covering links (as evidences) 
	 */
	public static String coverageHistrogramP1EDASimpleWordCoverage(JCas aJCas) throws CASException
	{
		// First, get the list of H tokens 		
		JCas hView = null; 		
		Collection<Token> allTokens = null; 
		hView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW); 
		allTokens = JCasUtil.select(hView, Token.class); 		
		List<Token> tokenList = new ArrayList<Token>(allTokens); 
		List<List<Link>> coveringLinks = InspectUtilsJCasAndLinks.getCoveringLinksTokenLevel(aJCas, Link.Direction.TtoH); 
		HashMap<Link, Integer> linkIndex = makeLinkIndex(aJCas); 
		
		if (tokenList.size() != coveringLinks.size())
		{
			logger.error("Internal integrity failure: different counts on the same underlying JCas.(You should never see this message)"); 
		}
		
		// from the string, token per line. 
		logger.debug("coverageHistogram, forming strings with " + tokenList.size() + " tokens"); 

		String result = ""; 
		for(int i=0; i < tokenList.size(); i++)
		{
			String line = ""; 
			Token theToken = tokenList.get(i); 
			List<Link> linksOnToken = coveringLinks.get(i);
			
			// Token info 
			String token = theToken.getCoveredText(); 
			String lemPos = ""; 
			{	// get lemma/pos 
				Lemma l = theToken.getLemma();  POS p = theToken.getPos(); 
				if (l != null)
					lemPos += l.getValue(); 
				
			
				if (p != null)
				{
					String s = p.getType().toString(); 	
					String typeString = s.substring(s.lastIndexOf(".") + 1); 	
					lemPos += "/" + typeString; 
				}	
			}
			line += token + " (" + lemPos + "),";
			
			// coverage belief of the SWC model  
			if (linksOnToken.size() > 0)
			{
				line += " 1.0, { "; 
			}
			else
			{
				line += " 0.0, "; 
			}
			
			// covering links ... 
			for (Link l : linksOnToken)
			{
				Integer id = linkIndex.get(l); 
				line += "Link-" + id.toString() + " "; 
			}
			if (linksOnToken.size() > 0)
			{	
				line += "}"; 
			}
			result += (line + "\n"); 
		}
		return result; 
	}
	
	/**
	 * Get all the links, and assigns an ID (integer number) for each Link, where 
	 * 
	 * @return
	 */
	private static HashMap<Link, Integer> makeLinkIndex(JCas aJCas) throws CASException
	{
		HashMap<Link, Integer> linkIdMap = new HashMap<Link, Integer>(); 
		List<Link> allLinks = LinkUtils.selectLinksWith(aJCas, (String) null);

		Integer id = 0; 
		for(Link l : allLinks)
		{
			linkIdMap.put(l, id); 
			id++; 
		}		
		return linkIdMap; 
	}
	
	@SuppressWarnings("unused")
	private List<Double> coveragePerWord(List<List<Link>> coveringLinks) 
	{
		//TODO 
		return null; 
	}
	
	// logger
	private static Logger logger = Logger.getLogger(InspectorForSimpleWordCoverageP1EDA.class); 

}
