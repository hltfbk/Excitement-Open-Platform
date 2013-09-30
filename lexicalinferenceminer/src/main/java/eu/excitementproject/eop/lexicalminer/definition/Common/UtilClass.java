package eu.excitementproject.eop.lexicalminer.definition.Common;

import java.io.FileNotFoundException;

import java.text.Normalizer;
import java.util.regex.Pattern;

import javax.naming.ConfigurationException;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.InitException;

/**
 * The class is used for a mix of help-functions...
 * @author mirond
 *
 */
public class UtilClass {
	
	private static boolean initiated=false;
	private static int maximumLemmaLength;// the maximum allowed length of right or left lemma
	private static int minimumLemmaLength;
	private static boolean useDeAccent;
	
	/**
	 * The function checks if the POS is a kind of noun (or proper noun).
	 * @param pos
	 * @return true if it's a kind of noun (or proper name)
	 * 			false otherwise 
	 */
    private static volatile UtilClass instance = null;
    


    public static UtilClass getInstance() throws InitException {
            if (instance == null) {
                    synchronized (UtilClass .class){
                    	if (!initiated)
                    		throw new InitException("To use the UtilClass you should first call the init function with ConfigurationParams (\"General\" module)");
                            if (instance == null) {
                                    instance = new UtilClass ();
                            }
                  }
            }
            return instance;
    }
    
    public static void init(ConfigurationParams params) throws eu.excitementproject.eop.common.utilities.configuration.ConfigurationException
    {
    	maximumLemmaLength=params.getInt("max_term_length");
    	minimumLemmaLength=params.getInt("min_term_length");
    	useDeAccent=params.getBoolean("useDeAccent");    	
    	initiated=true;
    }
	
	public static boolean isANoun(PartOfSpeech pos)
	{
		if (pos != null && pos.getCanonicalPosTag().equals(CanonicalPosTag.N))
		{
			return true;
		}

		return false;
	}
	
	/*
	 * initialization made by the static init method
	 */
	private UtilClass(){};
	
	
	// return true if the given rule is a valid rule
	public static boolean isValidRule(String leftLemma, String rightLemma) throws FileNotFoundException, ConfigurationException
	{
		
		// although the string should be lemmas the therefore not empty, we check the length here
		if (leftLemma==null || rightLemma==null || leftLemma.length()==0 || rightLemma.length()==0)
			return false;
		
		
		
		String lowerLeft=leftLemma.toLowerCase();
		String lowerRight=rightLemma.toLowerCase();
		

		if (leftLemma.length()>maximumLemmaLength || rightLemma.length()>maximumLemmaLength ||
				leftLemma.length()<minimumLemmaLength || rightLemma.length()<minimumLemmaLength)
			return false;

		lowerLeft=lowerLeft.replace("(","");
		lowerLeft=lowerLeft.replace(")","");
		lowerLeft=lowerLeft.replace("\"","");
		lowerLeft=lowerLeft.trim();
		
		lowerRight=lowerRight.replace(")","");
		lowerRight=lowerRight.replace(")","");
		lowerRight=lowerRight.replace("\"","");
		lowerRight=lowerRight.trim();
		
		if (lowerLeft.equals(lowerRight)) // dog -> dog   - is not a valid rule
			return false;
		
		if (lowerLeft.startsWith(lowerRight)) // andre agassi -> andre   - is not a valid rule
			return false; 
		
		// test if one of the lemmas is stop word
		if (StopwordsDictionary.getInstance().isStopWord(lowerLeft) || 
				StopwordsDictionary.getInstance().isStopWord(lowerRight))
			return false;
		
		// no limitation found - return true
		return true;
	}
	
	
	 static Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	/*
	 * Remove accent letters to their known equal in English
	 * for example: e with upper line become regular e
	 */
	public String deAccent(String str) {
		if (useDeAccent)
	    {
			String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 	   
			return pattern.matcher(nfdNormalizedString).replaceAll("");
	    }
		else
		{
			return str;
		}
	}
}
