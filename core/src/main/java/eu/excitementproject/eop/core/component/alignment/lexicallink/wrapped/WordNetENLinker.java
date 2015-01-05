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
 * A wrapper (convenience) code that makes LexicalAlignerFromLexicalResource 
 * 
 * @author Tae-Gil Noh
 *
 */
public class WordNetENLinker implements AlignmentComponent {
	
//	/**
//	 * Note that, default WordNet path requires EOP source (refers to /resource as file path) 
//	 * 
//	 * So, please note that this default constructor *will not* work when you use 
//	 * EOP as a library (in Jar). This default constructor is provided for 
//	 * convenience of EOP code developers who runs their experiment with EOP sources.
//	 * 
//	 * If you are using EOP as library; use WordNetENLinker(path) constructor. 
//	 * @throws AlignmentComponentException
//	 */
//	public WordNetENLinker() throws AlignmentComponentException
//	{
//		this(defaultWNPath); 
//	}
	
	/**
	 * This is a convenient constructor that uses known "default relation" (for Entailment)
	 * to formalize an aligner based on WordNet. The constructor requires one argument --- path to WordNet directory.  
	 * 
	 * @param wordNetPath needs to point WordNet directory (that holds indexes and data files) 
	 * @throws AlignmentComponentException
	 */
	public WordNetENLinker(String wordNetPath) throws AlignmentComponentException
	{
		this(wordNetPath, new HashSet<WordNetRelation>(Arrays.asList(defaultEntailingRelations)), defaultMaxPhrase); 
	}
	
	/**
	 * Full constructor. check default constants for default values; and use other constructors if default is good enough. 
	 * 
	 * @param wordNetPath  Where is wordnet? (path string) 
	 * @param entailingRelationSet  What relations of WordNet will be treated as "entailing" relations? 
	 * @param maxPhraseLen  Upto which number of words would be considered as possible "multi-word" expressions? 
	 * @throws AlignmentComponentException
	 */
	public WordNetENLinker(String wordNetPath, Set<WordNetRelation> entailingRelationSet, int maxPhraseLen) throws AlignmentComponentException {
		try {
			LexicalResource<WordnetRuleInfo> wordNet = new WordnetLexicalResource(new File(wordNetPath), true, true, entailingRelationSet, 2); 
			worker = new LexicalAlignerFromLexicalResource(wordNet, true, maxPhraseLen, null, null, null); 		
		} 
		catch (LexicalResourceException e)
		{
			throw new AlignmentComponentException ("failed to initialize WordNet LexicalResource: " + e.getMessage()); 
		} 
		catch (AlignmentComponentException ae)
		{
			throw new AlignmentComponentException ("failed to initialize lexical aligner: " + ae.getMessage()); 
		}

	}
	
	public void annotate(JCas aJCas) throws AlignmentComponentException
	{
		worker.annotate(aJCas); 
	}
	
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

	
	private final LexicalAlignerFromLexicalResource worker; 
	
	// Default path. Note that this path won't work when EOP is in Jar. 
	// The default path is only provided as convenience of using within development process. 
	// private static final String defaultWNPath = "../core/src/main/resources/ontologies/EnglishWordNet-dict"; 
	
	// Default "Relation". 
	private static final WordNetRelation[] defaultEntailingRelations = new WordNetRelation[] { WordNetRelation.SYNONYM, WordNetRelation.DERIVATIONALLY_RELATED, WordNetRelation.HYPERNYM, WordNetRelation.INSTANCE_HYPERNYM, WordNetRelation.MEMBER_HOLONYM, WordNetRelation.PART_HOLONYM, WordNetRelation.ENTAILMENT, WordNetRelation.SUBSTANCE_MERONYM }; 
	
	// Default max multi-word expression length 
	private static final int defaultMaxPhrase = 1; 

}
