package eu.excitementproject.eop.core.component.alignment.lexicallink;

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

	// Properties
	
	public String getVersion() {
		return version;
	}

	public boolean useLemma() {
		return useLemma;
	}

	// Constructors
	
	public LexicalResourceInformation(String version, boolean useLemma) {
		super();
		this.version = version;
		this.useLemma = useLemma;
	}
}
