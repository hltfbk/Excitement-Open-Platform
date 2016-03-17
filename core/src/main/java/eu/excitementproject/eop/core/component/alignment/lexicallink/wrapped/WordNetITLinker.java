package eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped;


import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAlignerFromLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

/**
 *
 * A lexical aligner class that links tokens based on Italian WordNet. 
 * This is a convenience class. The class utilizes WordNetLexicalResource class 
 * and LexicalAligner class to make the aligner. 
 * 
 * @author Tae-Gil Noh
 *
 */
public class WordNetITLinker implements AlignmentComponent {


	/**
	 * @param wordNetPath
	 * @throws AlignmentComponentException
	 */
	public WordNetITLinker(String wordNetPath) throws AlignmentComponentException
	{
		this(wordNetPath, defaultEntailingRelations, defaultMaxPhrase); 
	}
	
	/**
	 * Full constructor. check default constants for default values; and use other constructors if default is good enough. 
	 * 
	 * @param wordNetPath  Where is wordnet? (path string) 
	 * @param entailingRelationSet  What relations of WordNet will be treated as "entailing" relations? 
	 * @param maxPhraseLen  Upto which number of words would be considered as possible "multi-word" expressions? 
	 * @throws AlignmentComponentException
	 */
	public WordNetITLinker(String wordNetPath, Set<WordNetRelation> entailingRelationSet, int maxPhraseLen) throws AlignmentComponentException {		
		try {
			LexicalResource<WordnetRuleInfo> wordNet = new WordnetLexicalResource(new File(wordNetPath), true, true, entailingRelationSet, 2); 
			worker = new LexicalAlignerFromLexicalResource(wordNet, true, maxPhraseLen, null, null, null); 		
		} 
		catch (LexicalResourceException e)
		{
			throw new AlignmentComponentException("failed to initialize WordNet LexicalResource: " + e.getMessage()); 
		} 
		catch (AlignmentComponentException ae)
		{
			throw new AlignmentComponentException("failed to initialize lexical aligner: " + ae.getMessage()); 
		}
	}
	
//	@SuppressWarnings("rawtypes")
//	LexicalResource resource = new WordnetLexicalResource(new File(path), false, false, relations, 3);
		
	public void annotate(JCas aJCas) throws AlignmentComponentException
	{
		worker.annotate(aJCas); 
	}
	
	// private variable 
	private final LexicalAlignerFromLexicalResource worker; 
	
	private static final HashSet<WordNetRelation> defaultEntailingRelations = new HashSet<WordNetRelation>(Arrays.asList(WordNetRelation.SYNONYM)); 

	// Default max multi-word expression length 
	private static final int defaultMaxPhrase = 1; 


	public String getComponentName()
	{
		return this.getClass().getName(); 
	}
	
	public String getInstanceName()
	{
		return null; 
	}
	
	public void close() throws AlignmentComponentException
	{
		worker.close(); 
	}

}
