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
		
		// TODO outline algorithm. 

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
	
	private Boolean containsOnlyNonContentPOSes()
	{
		// TODO: write this once. 
		
		return false; 
	}
	
	// Punctuation, Preposition, Others, Conjunction, and Articles 
	final private String[] nonContentPOSes = {"PUNC", "PP", "O", "CONJ", "ART"}; 
}
