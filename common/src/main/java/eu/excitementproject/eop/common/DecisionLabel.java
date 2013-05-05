package eu.excitementproject.eop.common;

/**
 * <P>
 * This Enum class represents the decision labels for entailment decision, 
 * as defined in the specification 1.1, Section 4.2.1.6. 
 * It is represented with a hierarchical enum.  
 * </P>
 * 
 * The hierarchy it currently holds is:  
 * <pre>
 * - Entailment --------- Paraphrase  
 * - NonEntailment ------ Contradiction
 *                 |--- Unknown 
 * - Abstain
 * </pre>
 * 
 * Note that to extend this enum type, <B> you must keep it in sync </B> with the UIMA type <code>entailment.Decision</code> 
 * (that of Spec 3.3.4) 
 * 
 * @author Gil 
 * 
 */

public enum DecisionLabel {

	Entailment(null),
	NonEntailment(null),
	Abstain(null),
	Paraphrase(Entailment),
	Contradiction(NonEntailment),
	Unknown(NonEntailment), 
	;
	
	/**
	 * Compares the decision label to another decision label. 
	 * The method checks a "is-a" relationship. For example; 
	 * if it is a paraphrase (say, <code>a=DecisionLabel.Paraphrase;</code>), both  
	 * <code>a.is(DecisionLabel.Paraphrase)</code> and <code>a.is(DecisionLabel.Entailment)</code> 
	 * returns true.  
	 * 
	 * @param e The DecisionLabel to be compared. 
	 * @return a boolean: true if the label is one of e (is-a); false otherwise. 
	 */	
	public boolean is(DecisionLabel e) {
	if (e == null) {
		return false;
	}
	for(DecisionLabel t = this; t != null; t=t.parent)
		if (e == t) {
			return true;
		}
		return false;
	}
	
	private DecisionLabel(DecisionLabel parent) {
		this.parent = parent;
	}
	
	/**
	 * Returns the DecisionLabel with a name that equals the input string, ignoring case.
	 * @param s
	 * @return
	 * @throws IllegalArgumentException if input string does not correspond to any enum value
	 */
	public static DecisionLabel getLabelFor(String s) {
		for (DecisionLabel label : DecisionLabel.values()) {
			if (label.name().equalsIgnoreCase(s)) {
				return label;				
			}
		}
		throw new IllegalArgumentException(String.format("The string '%s' does not correspond to any constant in the enum %s", s, DecisionLabel.class.getSimpleName()));
	}

	private DecisionLabel parent = null;
}
