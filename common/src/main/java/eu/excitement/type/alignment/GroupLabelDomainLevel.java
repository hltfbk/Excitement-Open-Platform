package eu.excitement.type.alignment;

/** 
 * This Enum is defined to show the "group" of a specific alignment.Link instance. 
 * This file defines "Domain Level" semantic group labels. By domain level, we assume different granularity, 
 * or different source: such as "lexical level" grouping, or "syntactic level" grouping, or "predicate level" grouping, 
 * They are different to generic "inference level", and we call such as domain (domain of syntactic, domain of lexical, etc).  
 * 
 * The enum values are used in Link class (the class that represents alignment.Link CAS type). 
 * See addGroupLabel() methods and getGroupLabelDomainLevel() methods in Link class; they are the main users of the 
 * enum defined here. 
 * 
 * NOTE: using semantic group label is optional - but it is recommended that an aligner should add (at least) 
 * inference-level semantic labels. 
 * 
 * NOTE: the enum class can be extended to reflect new domain (e.g. predicate level relations in the future, etc)  
 * 
 * @author Tae-Gil Noh
 * @since September 2014 
 */

// Note: the following list has been first defined and proposed by BIU.
// (although they are tuned a bit after some discussion) 
// See the following Google Doc to check the rational for the domain level 
// definitions, and how they are mapped to actual ontologies such as WordNet
// and VerbOcean.  
// http://goo.gl/xlUm3h


public enum GroupLabelDomainLevel {
	SYNONYM,
	HYPERNYM,
	HYPONYM,
	MERONYM,
	HOLONYM,
	CAUSE,
	DERIVATIONALLY_RELATED,
	HAPPENES_BEFORE, 
	ANTONYM, 
	SAME_PREDICATE_TRUTH,
	OPPOSITE_PREDICATE_TRUTH,		
	SEMANTICALLY_RELATED, 
}
