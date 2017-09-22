package eu.excitementproject.eop.alignmentedas.p1eda.inspector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.LinkUtils;
import eu.excitement.type.alignment.Target;
import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * This class provides some static methods that will help you to check 
 * and see the contents of a JCas, mostly about its alignment.links 
 * 
 * @author Tae-Gil Noh 
 * 
 */
public class InspectUtilsJCasAndLinks {

	/**
	 * This method returns Entailment.Pair type's pairID value (string).  
	 * 
	 *  Note that this code assumes only one Entailment.Pair per JCas 
	 *  If that is not the case, the code will warn you, but still return the 
	 *  relevant value from the first Entailment.Pair instance. 
	 *  
	 * @param aJCas
	 * @return
	 * @throws LAPException
	 */
	public static String getTEPairID(JCas aJCas) throws LAPException
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
			throw new LAPException("Input CAS is not well-formed CAS as EOP EDA input: missing TE pair"); 
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
	 * @throws LAPException
	 */
	public static DecisionLabel getGoldLabel(JCas aJCas) throws LAPException 
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
			
			try {
				labelEnum = DecisionLabel.getLabelFor(labelString); 
			}
			catch (EDAException ee)
			{
				throw new LAPException("Input CAS is not well-formed CAS as EOP EDA input: unknown gold label string: "+ labelString);  
			}
			if (iter.hasNext())
			{
				logger.warn("This JCas has more than one TE Pairs: This P1EDA template only processes single-pair inputs. Any additional pairs are being ignored, and only the first Pair will be processed.");
			}
			return labelEnum; 
		}
		else
		{
			throw new LAPException("Input CAS is not well-formed CAS as EOP EDA input: missing TE pair"); 
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
	 * Note that, you can still give direction. If you set dir as "null", you will get 
	 * all links (regardless of their direction). If you set dir as "TtoH", you will get 
	 * "TtoH" links and (naturally) Bidirectional links. 
	 * 
	 * @param aJCas
	 * @return
	 */
	public static List<List<Link>>getCoveringLinksTokenLevel(JCas aJCas, Link.Direction dir) throws CASException
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
			List<Link> linksHoldingThisToken = LinkUtils.filterLinksWithTargetsIncluding(links, tok, dir);
			coveringLinks.add(linksHoldingThisToken); 
			logger.debug("getCoveringLinksWordLevel: found " + linksHoldingThisToken.size() + "links"); 
		}
		
		return coveringLinks; 
	}
		
	
	/**
	 * This utility method returns 4-lines summary of the given JCas T-H pairs. 
	 * 
	 * 2 lines of T-H as-is. (first line T, second line H) 
	 * 2 lines of T as lemma sequence (with canonical pos), H as lemma sequence (with canonical pos) 
	 * 
	 * 
	 * @param aJCas
	 * @return
	 * @throws CASException
	 */
	public static String summarizeJCasWordLevel(JCas aJCas) throws CASException 
	{
		String tSofaText; 
		String hSofaText; 
		String tLemmaSeq=""; 
		String hLemmaSeq=""; 
		
		JCas tView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
		JCas hView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW); 
		tSofaText = tView.getDocumentText(); 
		hSofaText = hView.getDocumentText(); 
		
		Collection<Token> tTokens = JCasUtil.select(tView, Token.class); 		
		Collection<Token> hTokens = JCasUtil.select(hView, Token.class); 
		
		for (Token t : tTokens)
		{
			String lemPos = ""; 
			Lemma l = t.getLemma(); 
			POS p = t.getPos(); 
			
			if (l != null)
			{
				lemPos += l.getValue(); 
			}
			
			if (p != null)
			{
				String s = p.getType().toString(); 	
				String typeString = s.substring(s.lastIndexOf(".") + 1); 	
				lemPos += "/" + typeString; 
			}	
			tLemmaSeq += lemPos + " "; 
		}
		
		for (Token t : hTokens)
		{
			String lemPos = ""; 
			Lemma l = t.getLemma(); 
			POS p = t.getPos(); 
			
			if (l != null)
			{
				lemPos += l.getValue(); 
			}
			
			if (p != null)
			{
				String s = p.getType().toString(); 	
				String typeString = s.substring(s.lastIndexOf(".") + 1); 	
				lemPos += "/" + typeString; 
			}	
			hLemmaSeq += lemPos + " "; 
		}
		
		
		DecisionLabel goldAnswer;
		String pairId;  
		
		try { 
			pairId = getTEPairID(aJCas); 
		}
		catch (LAPException e) // ill-formed CAS (no pair annotation). we simply ignore ID for such case. 
		{ 
			pairId = ""; 
		}
		
		try { goldAnswer=getGoldLabel(aJCas); } 
		catch (LAPException e) // this exception means gold string value was unknown string within CAS and conversion to Decision Label failed. (Won't really happen but), we will ignore such case in this printout 
		{ goldAnswer = null; }
		
		String part1 = "Pair ID: " + pairId + " ";
		if (goldAnswer != null)
		{
			part1 += "(gold answer: " + goldAnswer.toString() + ")"; 
		}
		part1 += "\n";
		String part2 = "T: " + tSofaText + "\n" + "H: " + hSofaText + "\n" + "TLemmaPos Sequence: " + tLemmaSeq + "\n" + "HLemmaPos Sequence: "+ hLemmaSeq; 
		return part1 + part2; 
	}
	
	/**
	 * This utility method generates one big string that summarizes all the alignment links 
	 * in the given JCas. 
	 * 
	 * For reader's convenience, it names each alignment.link instance a (index) number (as the order of 
	 * appearance in AnnotationIndex within JCas View). Note that this number, is artificial and does not 
	 * actually exist in JCas. However, the numbers can be used to identify "n-th" link in the JCas, etc. 
	 * 
	 * It will return a long, multi-line string with the following format. 
	 * 
	 * Link [n], info:[linker_version_info], strength: [strength num], direction: [direction]  
	 *  \t  Tside: {  [Annotations]  } 
	 *  \t  Hside: {  [Annotations]  } 
	 * Link [n+1] ... 
	 * 
	 * Each link will be summarized as 3 lines. Note that, Tside/Hside targets will be 
	 * displayed as: 
	 *   if it is a token: token value (its covered SOFA string) will be seen. 
	 *   if it is other than token: 
	 * 
	 * @param aJCas
	 * @return string as described. 
	 * @throws CASException
	 */
	public static String summarizeAlignmentLinks(JCas aJCas) throws CASException 
	{
		String result =""; 
		
		// get all links, with order... 
		List<Link> links = LinkUtils.selectLinksWith(aJCas, (String) null);
		
		// for each link, print out with the promised format ... 
		for (int i=0; i < links.size(); i++)
		{
			Link l = links.get(i); 
			String oneLinkSummary = summaryOutputSingleLink(l, i);	
			result += oneLinkSummary;
		}
		
		return result;
	}
	
	/**
	 * Generates summary two-liner for the given link. used in summarizeAlignmentLinks() 
	 * 
	 * @param l
	 * @return
	 */
	private static String summaryOutputSingleLink(Link l, int index)
	{
//	 	Link [n], info:[linker_version_info], strength: [strength num], direction: [direction]  
//		 \t  Tside: {  [Annotations]  } 
//		 \t  Hside: {  [Annotations]  } 

		String line1 = "Link " + index + ", info: " + l.getAlignerID() + "_" + l.getAlignerVersion() + "_" + l.getLinkInfo() +
				", strength: " + l.getStrength() + ", direction: " + l.getDirectionString() + "\n"; 
		String line2 = "  Tside: { " + summaryOutputLinkTarget(l.getTSideTarget()) + " }"; 
		String line3 = "  Hside: { " + summaryOutputLinkTarget(l.getHSideTarget()) + " }\n"; 
				
		return (line1 + line2 + line3); 
	}
	
	/**
	 * Generates summary one liner (as a set) for the given alignment.target. (used in summarizeAlignmentLinks() 
	 * 
	 * @param t one Target of link target. 
	 * @return
	 */
	private static String summaryOutputLinkTarget(Target t)
	{
		String summary = ""; 
		Collection<Annotation> annots = JCasUtil.select(t.getTargetAnnotations(), Annotation.class); 
		for (Annotation a : annots)
		{
			String s = a.getType().toString(); 	
			String typeString = s.substring(s.lastIndexOf(".") + 1); 
			
			if (typeString.equals("Token")) // hard coded. This summary utility treats Token as special one, and outputs some detail about it. (actual text) 
			{
				summary += "[" + a.getCoveredText() + "] "; 
			}
			else
			{
				// The annotation is not a token; simply output type name and address 
				summary += typeString + "(" + t.getAddress() + ") ";  // getAddress isn't really good idea but .. hmm. let's see. 
			}			
		}
		return summary; 
	}
	
	///
	///
	///

	// the logger 
	private static Logger logger = Logger.getLogger(InspectUtilsJCasAndLinks.class); 

}
