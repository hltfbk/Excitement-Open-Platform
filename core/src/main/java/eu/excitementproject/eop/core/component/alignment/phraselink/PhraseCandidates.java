package eu.excitementproject.eop.core.component.alignment.phraselink;

import java.util.List;

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * A Utility class that holds utility methods for getting (extracting) 
 * "phrase" candidates from the given view (of a JCas) 
 * 
 * @author Tae-Gil Noh
 *
 */

// TODO add JavaDocs 

public class PhraseCandidates {

	public static List<Token> getAllTokenSequencesWithBrute(JCas aView)
	{
		return getAllTokenSequencesWithBrute(aView, defaultLen); 
	}
	
	public static List<Token> getAllTokenSequencesWithBrute(JCas aView, int upToLength)
	{
		// TODO write the code 
		return null; 
	}
	
	public static List<String> getSOFAStringSequenceWithBrute(JCas aView)
	{
		return getSOFAStringSequenceWithBrute(aView, defaultLen);
	}
	
	public static List<String> getSOFAStringSequenceWithBrute(JCas aView, int upToLength)
	{
		// TODO write the code 
		return null; 
	}
	
	
	private static final int defaultLen = 5; 
}
