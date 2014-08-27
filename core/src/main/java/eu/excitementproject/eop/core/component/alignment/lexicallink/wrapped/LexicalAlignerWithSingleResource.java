package eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;

/**
 * This is a slightly simplified (and more convenient in some ways)
 * version of LexicalAlinger. 
 * 
 * Usage is quite simple; provide a lexical resource, you get an aligner based on 
 * that lexical resource. 
 * 
 * The benefit comes from that we separate "initialization of lexical resource" 
 * from "lexical aligner". This, was a hindrance for LexicalAligner, where we 
 * always rely on XML configuration file. --- This, plus, poor status of CommonConfig 
 * Implementation (e.g. config is not updatable, programmatically) limited easy way to add new 
 * lexical resource programmatically (e.g. without configurations)  
 * 
 * @author Tae-Gil Noh, by adopting codes of LexicalAlinger. 
 *
 */
public class LexicalAlignerWithSingleResource  {
	
	// TODO --- maybe after Rome Meeting... after discussing the need with Vered 
	@SuppressWarnings("rawtypes")
	public LexicalAlignerWithSingleResource(LexicalResource underlyingResource) {

	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private LexicalResource underlyingResource; 
	
}
