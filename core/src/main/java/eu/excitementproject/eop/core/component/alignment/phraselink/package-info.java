/**
 * 
 * This package holds various phrase-level aligners that adds Alignment.Link instances 
 * that connects phrases (including single words) on TEXTVIEW and HYPOTHESISVIEW. 
 * 
 * Currently (as of July 2014) it holds the following two types. 
 * - a set of phrase aligners from Meteor-like paraphrase tables: MeteorPhraseLinker[XX]. @see MeteorPhraseResourceAligner.  
 * - A phrase aligner that aligns based on Lemma-identity (links are added on the same sequence of lemmas). @see IdenticalLemmaPhraseLinker.
 * 
 * @author Tae-Gil Noh 
 * @since June 2014 
 */
package eu.excitementproject.eop.core.component.alignment.phraselink;