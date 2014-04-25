
package eu.excitementproject.eop.common.component.alignment;

/**
 * Alignment component is a sub-type of annotator component. 
 * The component interface does not add any new method. This (empty) 
 * interface is defined as an independent interface mainly to serve 
 * the purpose of identifying alignment components among alignment components.
 * 
 * All components that implement AlignmentComponent must annotate according 
 * to the specific CAS types that are designed for alignment annotations within EOP.
 * You can find the types in /desc/type/AlignmentTypes.xml (at src/main/resources) 
 * 
 * @author Tae-Gil Noh
 */
public interface AlignmentComponent extends AnnotatorComponent {
	
	// the interface adds no new methods
	
}
