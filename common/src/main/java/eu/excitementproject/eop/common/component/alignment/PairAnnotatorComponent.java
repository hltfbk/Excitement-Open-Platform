package eu.excitementproject.eop.common.component.alignment;

import org.apache.uima.jcas.JCas;
import eu.excitementproject.eop.common.component.Component;

/**
 *  The expressive power of CAS is not limited to generic annotations like lemma, 
 *  dependency, etc. They can also represent entailment-specific, or task-specific 
 *  data types. For example, alignment annotation between Text and Hypothesis can 
 *  be represented in CAS. This annotation, unlike generic LAP annotation, knows 
 *  about the task and the task-specific data, like Entailment pair, text side 
 *  and hypothesis side, and so on.
 *  
 *  This interface define a CORE component type that adds annotations within 
 *  CORE part of the platform (that is, not in LAP(preprocessing)).  
 *  An annotator component gets one CAS, analyzes the given CAS and add additional 
 *  information in the forms of annotations. The added annotation gives extra 
 *  information for EDAs and/or other components. 
 *  
 *  What is the difference between a AnnotatorComponent and a LAP component? 
 *   
 *  <ul>
 *  <li> LAP components annotator provides generic annotations, such as POS, NER 
 *       and syntactic structures. Annotator component provides task specific 
 *       (textual entailment specific) annotations. </li> 
 *  <li> LAP annotates each view separately, each as a text. Annotator component 
 *       annotates the given CAS as a T-H pair (or pairs). Annotator component 
 *       treats the CAS as entailment pair data, not just two sets of textual input. </li>
 *  <li> LAP components belong to LAP. They cannot access CORE components like 
 *       lexical or syntactic knowledge resources. Annotator component is part 
 *       of CORE, and actively utilizes other CORE components. </li> 
 *  </ul> 
 *  
 *  We also define sub-types of Annotator component, to identify annotator 
 *  components by their types and roles. For example, the AlignmentComponent is 
 *  the subtype of AnnotatorComponent. 
 *  
 * @author Tae-Gil Noh
 */

public interface PairAnnotatorComponent extends Component {
	
	/**
	 * The method gets one JCas. Once the annotate process is done, 
	 * the given JCas is enriched by the added layer of annotations. 
	 * If the annotator component was not able to add annotations 
	 * (for example, needed LAP annotation like lemma was missing, or 
	 * some failure of activating resources, etc), the component must 
	 * raise a proper exception. 
	 * 
	 * If the call is successfully returned, it always means the process 
	 * was successful --- even though there were no added annotations 
	 * (e.g. contradiction annotator, but there was no contradiction). 
	 */
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException; 
	
	/**
	 * The method indicates that the underlying annotator (or aligner) is 
	 * now no longer required --- this is an indication that the aligner and/or 
	 * its underlying resources can be released. (e.g. files closed, DB connection 
	 * close, etc). 
	 * 
	 * You can expect that every aligner users will call close() method at the 
	 * end of their usage. 
	 * 
	 * @throws PairAnnotatorComponentException
	 */
	public void close() throws PairAnnotatorComponentException; 
	
}
