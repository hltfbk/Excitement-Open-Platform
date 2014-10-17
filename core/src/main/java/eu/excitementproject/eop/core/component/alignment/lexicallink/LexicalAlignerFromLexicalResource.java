package eu.excitementproject.eop.core.component.alignment.lexicallink;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;

/**
 * TODO full document
 * (what this modules does) 
 * (goal: use rich lexical resources as lexical aligner) 
 * 
 * (how it does what it does) 
 * 
 * 
 * @author Tae-Gil Noh
 *
 */
public class LexicalAlignerFromLexicalResource implements AlignmentComponent {

	public LexicalAlignerFromLexicalResource()  {
		// TODO 
		// once annotate line is outlined, fill up needed information 
		// starting from "LexicalResource" instance ... 
	}

	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {
		
		// TODO: start coding on this outline. 
		// 
		
		// do we accept phrases (the entry can be more than one lemma --- two or more lemmas)
		// do both. If no phrases, just first part. 
		
		// single-lemma route 
		// get H side lemmas as all possible candidates 
		// ready T side lemma as a Lemma list. 
		// 
		//   for all candidates 
		//     query getRulesForRight(lemma) 
		//     check each T side lemma list for applicable places
		//	     if found: add link. 
		//   (WE do check POS and Lemma for single lemma cases) 
		
		// phrase-checking route 
		// prepare T side lemma sequences as one string, so we can do quick look up of 
		// applicable or not. 
		// 
		// get H side lemma sequences as all possible phrase candidates 
		// for each candidates 
		//    query getRulesForRight(lemma-seq as one string)
		//    for the returned rules, string search on T-side lemma sequence  
		//       if match is there; 
		//       do real search on T side Lemma List, and link them.  
		
	}

	
	
	
	@Override
	public String getComponentName()
	{
		return this.getClass().getName(); 
	}

	@Override
	public String getInstanceName()
	{
		return null; 
	}

}
