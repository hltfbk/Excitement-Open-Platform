package eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAligner;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
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
	 * 
	 * Default parameters. note that this won't work when you use EOP as library. 
	 * 
	 */
	public WordNetITLinker(File wordNetITPath) throws AlignmentComponentException
	{
		this(wordNetITPath, false, false, defaultRelations, 1); 
	}
	
//	@SuppressWarnings("rawtypes")
//	LexicalResource resource = new WordnetLexicalResource(new File(path), false, false, relations, 3);
	
	/**
	 * 
	 * @param wordNetPath
	 * @throws AlignmentComponentException
	 */
	public WordNetITLinker(File wordNetITPath, boolean useFirstSenseLeftOnly, boolean useFirstSenseRightOnly, Set<WordNetRelation> allowedRelationTypes, int chainingLength) throws AlignmentComponentException {
		
		try 
		{
			WordnetLexicalResource lex = new WordnetLexicalResource(wordNetITPath, false, false, defaultRelations, 1); 
			LexicalAligner theAligner = LexicalAlignerFactory.getLexicalAlignerFromLexicalResource(lex, 1, "1.0", true, null, null); 
			worker = theAligner; 
		}
		catch (LexicalResourceException e)
		{
			throw new AlignmentComponentException ("Underlying resource thrown an exception: " + e.getMessage(), e); 
		}
	}
	
	public void annotate(JCas aJCas) throws AlignmentComponentException
	{
		worker.annotate(aJCas); 
	}
	
	// private variable 
	private final LexicalAligner worker; 
	
	private static final HashSet<WordNetRelation> defaultRelations = new HashSet<WordNetRelation>(Arrays.asList(WordNetRelation.SYNONYM)); 


	public String getComponentName()
	{
		return this.getClass().getName(); 
	}
	
	public String getInstanceName()
	{
		return null; 
	}
}
