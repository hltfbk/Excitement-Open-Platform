
package eu.excitementproject.eop.core.component.lexicalknowledge.germanet;

public enum GermaNetRelation {
	causes,
	entails,
	has_hypernym,
	has_antonym,
	has_synonym;

	public String toGermaNetString() {
		switch (this) {
			case causes: return "causes";
			case entails: return "entails";
			case has_hypernym: return "has_hypernym";
			case has_antonym: return "has_antonym";
			case has_synonym: return "has_synonym";
		}
		return null;
	}
}
