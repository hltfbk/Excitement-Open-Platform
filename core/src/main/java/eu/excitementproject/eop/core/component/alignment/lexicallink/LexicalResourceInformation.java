package eu.excitementproject.eop.core.component.alignment.lexicallink;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

/**
 * Contains general information about a lexical resource,
 * such as the version, and whether to use lemma or surface level
 * tokens to search for rules.
 * 
 * @author Vered Shwartz
 * @since 25/06/2014
 */
public class LexicalResourceInformation {

	// Private Members
	private String version;
	private boolean useLemma;
	private PartOfSpeech leftSidePOS, rightSidePOS;

	// Properties
	
	public String getVersion() {
		return version;
	}

	public boolean useLemma() {
		return useLemma;
	}
	
	public PartOfSpeech getRightSidePOS() {
		return rightSidePOS;
	}
	
	public PartOfSpeech getLeftSidePOS() {
		return leftSidePOS;
	}
	
	// Constructors
	
	public LexicalResourceInformation(String version, boolean useLemma, 
			PartOfSpeech leftSidePOS, PartOfSpeech rightSidePOS) {
		super();
		this.version = version;
		this.useLemma = useLemma;
		this.leftSidePOS = leftSidePOS;
		this.rightSidePOS = rightSidePOS;
	}
}
