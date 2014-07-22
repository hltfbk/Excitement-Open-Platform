package eu.excitementproject.eop.core.component.alignment.phraselink;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;

/**
 * This is a surface level aligner that aligns "identical lemma sequences" found in 
 * TextView and HypothesisView. 
 * 
 * The module add Alignment.Link instances where its target holds token sequences (longer than 1 
 * tokens). The two token sequences (one in T, the other in H) are linked only if they have 
 * identical lemma sequences. 
 * 
 * The module is language-free (you can pass CAS with any language): it simply trust the 
 * annotated lemma annotations to identify "same word". 
 * 
 * Note that, the module will annotate only the "longest lemma sequence". That is, 
 * if the CAS has T: "I have a dog.", H: "She has a dog too". 
 * It module will add only *one link* that connects three tokens of T ([have a dog]) to 
 * three tokens of H ([has a dog]). It won't link (have -> has), or (dog -> dog). 
 * 
 * ( Also note that, the module does not annotate "function words only" sequences. That is 
 * it will add links between "to emphasize" -> "to emphasize", but not "to the" -> "to the". ) 
 * 
 * Naturally, the module depends on the existence of "Lemma" annotations. If there is no Lemma 
 * in the give CAS, it will raise an exception. 
 * 
 * @author Tae-Gil Noh
 *
 */
public class IdenticalLemmaPhraseLinker implements AlignmentComponent {

	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {
		
		// get Tview 
		// get Hview 
		
		// - outline of the algorithm 
		
		// Part one. finding longest lemma match from T - H. 
		// Okay, we have two list of tokens (that has access to lemma & pos)  
		// T and H. 
		// 
		// Okay, we strat on H sequence, pos = 0 (first word). 
		// we start finding "longest identical sequence" from the position this pos. 
		//   if we find the same lemma for pos, we try to find, lemma sequence of pos, pos+1 
		//   on T side. If found, we continue, if not, we stop, and record the match location.
		//   (if any match was recorded) 
		//   and update pos to (pos+ sequence length)(anything found) or (pos + 1) (nothing found)  

		// (data structure for match? simple num array? two numbers? or two arrays?)
		
		// Part two. annotating match with alignment.Link 
		// (before accept the link) check if the target is only consist of exclusion-only POSes. 
		// containsOnlyNonContentPOSes() - list of tokens? 
		
	}

	@Override
	public String getComponentName() {
		return this.getClass().getName(); // return class name as the component name 
	}

	@Override
	public String getInstanceName() {
		return null; // this module does not support multiple-instances (e.g. with different configurations) 
	}
	
	@SuppressWarnings("unused")
	private Boolean containsOnlyNonContentPOSes()
	{
		// TODO: write this once. 
		
		return false; 
	}
	
	// Punctuation, Preposition, Others, Conjunction, and Articles 
	@SuppressWarnings("unused")
	final private String[] nonContentPOSes = {"PUNC", "PP", "O", "CONJ", "ART"}; 
}
