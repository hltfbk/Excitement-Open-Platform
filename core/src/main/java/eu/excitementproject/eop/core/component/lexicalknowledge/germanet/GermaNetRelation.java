
package eu.excitementproject.eop.core.component.lexicalknowledge.germanet;

import eu.excitementproject.eop.common.component.lexicalknowledge.CanonicalRelationSpecifier;
import eu.excitementproject.eop.common.component.lexicalknowledge.OwnRelationSpecifier;
import eu.excitementproject.eop.common.component.lexicalknowledge.TERuleRelation;

public enum GermaNetRelation implements CanonicalRelationSpecifier, OwnRelationSpecifier<GermaNetRelation> {
	causes,
	entails,
	has_hypernym,
	has_hyponym,
	has_antonym,
	has_synonym;

	public String toGermaNetString() {
		switch (this) {
			case causes: return "causes";
			case entails: return "entails";
			case has_hypernym: return "has_hypernym";
			case has_hyponym: return "has_hyponym";
			case has_antonym: return "has_antonym";
			case has_synonym: return "has_synonym";
		}
		return null;
	}

	@Override
	public GermaNetRelation getOwnRelation() {
		return this;
	}

	@Override
	public TERuleRelation getCanonicalRelation() {
		switch (this) {
		case causes: return TERuleRelation.Entailment;
		case entails: return TERuleRelation.Entailment;
		case has_hypernym: return TERuleRelation.Entailment;
		case has_hyponym: return TERuleRelation.Entailment;
		case has_antonym: return TERuleRelation.NonEntailment;
		case has_synonym: return TERuleRelation.Entailment;
	}
		return null;
	}
}

