package eu.excitement.type.alignment;

/**
 * 
 * This Enum is defined to show the "group" of a specific alignment.Link instance. 
 * This file defines "Inference Level" semantic group labels. 
 * 
 * For example, is this link means Entailment relation establishing between the items that are linked? 
 * (such as, synonyms linked by a lexical aligner). 
 * 
 * This enum type defines "inference level" (or top-level) semantic group, such as "aligned targets are having relationship 
 * that is ..."
 * 
 * The enum values are used in Link class (the class that represents alignment.Link CAS type). 
 * See addGroupLabel() methods and getGroupLabelInferenceLevel() methods in Link class; they are the main users of the 
 * enums defined here. 
 * 
 * NOTE: the enum class might be extended to reflect new top level inference relations for the future
 * alignment.Links. But this enum should be kept stable, simple, and common enough; so, the semantic label 
 * would be actually used (both on annotating side and consumer (EDA) side). 
 * 
 * @author Tae-Gil Noh
 * @since September 2014 
 */
public enum GroupLabelInferenceLevel {
	LOCAL_CONTRADICTION, 
	LOCAL_ENTAILMENT, 
	LOCAL_SIMILARITY
}
