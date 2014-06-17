package eu.excitement.type.alignment;

import java.io.OutputStream;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

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
	 * @return
	 */
	public static <T extends TOP> List<Link> selectLinksWith(JCas aJCas, Class<T> type)
	{
		// TODO work on this once 
		return null; 
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
	
	public static void dumpTokenLevelLinks(JCas aJCas, OutputStream os)
	{
		// utility class that is useful to see what Links are added in the given CAS  
		// TODO work on this once. 
		
	}
}
