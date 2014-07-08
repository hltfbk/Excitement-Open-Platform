package eu.excitement.type.alignment;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * Some utility methods for alignment.Link type and related codes. 
 * 
 * ** UNDER CONSTRUCTION ** 
 * 
 * @author Tae-Gil Noh
 * @since June, 2014 
 * 
 */
public class LinkUtils {

	/**
	 * This method checks the alignment.Link instances of the given JCas. 
	 * If something is missing / not correct, it will throw an exception.
	 * Use this method within the development process of an AlignerComponent,
	 * (to check your aligner output is acceptable) or of an EDA. 
	 * 
	 * (Consider this as a minimal checker that will check the conventions 
	 * on link instances that EOP developers have all agreed upon.) 
	 * 
	 * Note that the prober code will some detail about the content that it checks 
	 * on log4j as DEBUG.  
	 * 
	 * Note that, no link makes no exception. (A possible output from an PairAnnotator). 
	 * It only check the link instances.  
	 * 
	 * @param aJCas JCas with EOP views and Links. 
	 */
	public static void probeLinksInCAS(JCas aJCas) throws CASException 
	{
		// TODO work on this once 
		
	}
	
	/**
	 * This utility method fetches alignment.Link instances that links the give 
	 * "Type" of annotations. More specifically, the method returns all link 
	 * instances that connects Targets, which holds the give "type". 
	 * 
	 * For example, a call with type=Token.class will return all Link instances 
	 * where either of its TSideTarget or HSideTarget holds "Token" annotation. 
	 * 
	 * The method will return all link instances, if one of its Targets hold 
	 * the given the type.  
	 * 
	 * @param jCas the JCas with EOP views 
	 * @param type target annotation class. 
	 * @return a List<Link> that holds all links that satisfy the condition. If none satisfy the condition, it will return an empty List. 
	 */
	public static <T extends TOP> List<Link> selectLinksWith(JCas aJCas, Class<T> type) throws CASException 
	{
		List<Link> resultList = new ArrayList<Link>(); 
		
		JCas hypoView = aJCas.getView("HypothesisView");
		// get Links that satisfy the condition by iterating all Links just once. 
		
		for (Link l : JCasUtil.select(hypoView, Link.class)) 
		{
			// is this link holds type object in either of its target? 
			Target tt = l.getTSideTarget(); 
			Target ht = l.getHSideTarget(); 
			
			if (JCasUtil.select(tt.getTargetAnnotations(), type).size() > 0)
			{
				// T side target does hold at least one of type instance. 
				resultList.add(l); 
				continue; // no need to check h side target 
			}
			
			if (JCasUtil.select(ht.getTargetAnnotations(), type).size() > 0)
			{
				// H side target does hold at least one of type instance. 
				resultList.add(l); 
			}
		}
		return resultList; 
	}
	
	
	public static List<Link> selectLinksWith(JCas aJCas, String alignerID, String versionID, String linkInfo)
	{
		// get links with those names; 
		// "null" means "don't care".
		// TODO work on this once 
		return null; 
	}
	
//	public static List<Link> selectLinksWith(String fullID)
//	{
//		// get links where link.getID() == fullID
//		// TODO work on this once 
//		return null; 
//	}
	
	/**
	 * Utility class that is useful to see what surface level (token level) Links are added in the given CAS.
	 * This method iterates all Links that includes tokens within their targets, and shows them (only tokens! 
	 * does not show other items in Target. ) --- Thus, the method is not generic enough to be used to check 
	 * Link (and targets) that links more than tokens. 
	 *   
	 * @param aJCas
	 * @param os
	 */
	public static void dumpTokenLevelLinks(JCas aJCas, PrintStream ps) throws CASException
	{
		// get all links that connects Tokens... 
		
		List<Link> tokenLevelLinks = selectLinksWith(aJCas, Token.class);

		ps.println("The CAS has " + tokenLevelLinks.size() + " Link instances in it.");

		int linkNum = 0; 
		for(Link l : tokenLevelLinks)
		{
			// output to the give output stream
			ps.print("Link " + linkNum);
			// The link information 
			ps.println(" (" + l.getDirectionString() + ", " + l.getID() + ", " + l.getStrength() + ")");
			linkNum++; 
			
			Target tside = l.getTSideTarget(); 
			Target hside = l.getHSideTarget(); 
			
			// T side target has n tokens... TEXT(begin,end) TEXT(begin, end) ... 
			Collection<Token> tokens = JCasUtil.select(tside.getTargetAnnotations(), Token.class); 
			ps.print("\t TSide target has " + tokens.size() + " token(s): ");
			for (Token t: tokens)
			{
				ps.print(t.getCoveredText() + "(" + t.getBegin() + "," + t.getEnd() + ") ");
			}
			ps.println(""); 
			
			// H side target has m tokens ... 
			tokens = JCasUtil.select(hside.getTargetAnnotations(), Token.class); 
			ps.print("\t HSide target has " + tokens.size() + " token(s): ");
			for (Token t: tokens)
			{
				ps.print(t.getCoveredText() + "(" + t.getBegin() + "," + t.getEnd() + ") ");
			}
			ps.println(""); 
						
		}
	}	
}
