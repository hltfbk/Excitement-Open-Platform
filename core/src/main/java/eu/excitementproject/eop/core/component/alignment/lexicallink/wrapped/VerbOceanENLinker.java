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
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.RelationType;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.VerbOceanLexicalResource;

/**
 *
 * A lexical aligner class that links tokens based on VerbOcean. 
 * Convenience class. The class utilizes VerbOcean class and LexicalAligner class to make 
 * the aligner. 
 * 
 * @author Tae-Gil Noh
 *
 */
public class VerbOceanENLinker implements AlignmentComponent {

	/**
	 * 
	 * Default constructor with no param. Will initiated VerbOcean with default params 
	 * note that this won't work when you use EOP as library. 
	 * In such a case, use the other constructor (with path, and allowed relation type) 
	 * 
	 */
	public VerbOceanENLinker() throws AlignmentComponentException
	{
		this(new File(verbOceanDefaultPath), defaultRelations); 
	}
	
	
	/**
	 * Main constructor. 
	 * 
	 * @param wordNetPath verbOcean text file
	 * @param allowedRelationTypes VerbOcean relation types that you want to be added as alignment.Links  
	 * @throws AlignmentComponentException 
	 */
	public VerbOceanENLinker(File verbOceanFile, Set<RelationType> allowedRelationTypes) throws AlignmentComponentException {
		
		try 
		{
			VerbOceanLexicalResource lex = new VerbOceanLexicalResource(1.0, verbOceanFile, allowedRelationTypes); 
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
	
	// const, default values. Woudln't work when within Jar!  
	private static final String verbOceanDefaultPath = "../core/src/main/resources/VerbOcean/verbocean.unrefined.2004-05-20.txt"; 
	private static final HashSet<RelationType> defaultRelations = new HashSet<RelationType>(Arrays.asList(RelationType.STRONGER_THAN)); 


	public String getComponentName()
	{
		return this.getClass().getName(); 
	}
	
	public String getInstanceName()
	{
		return null; 
	}
}
