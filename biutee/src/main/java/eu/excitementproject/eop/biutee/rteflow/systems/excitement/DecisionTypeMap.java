package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEClassificationType;

/**
 * Maps between BIU's {@link RTEClassificationType} and EXCITEMENT's {@link DecisionLabel}.
 * @author Ofer Bronstein
 * @since March 2013
 */
public class DecisionTypeMap {

	public static DecisionLabel toDecisionLabel(RTEClassificationType decision) {
		switch (decision) {
		case ENTAILMENT:	return DecisionLabel.Entailment;
		case CONTRADICTION:	return DecisionLabel.Contradiction;
		case UNKNOWN:		return DecisionLabel.Unknown;
		default:			throw new IllegalArgumentException("Unsupported decision type: " + decision);
		}
	}
	
	public static RTEClassificationType toRTEClassificationType(DecisionLabel decision) {
		switch (decision) {
		case Entailment:	//fallthrough
		case Paraphrase:	//fallthrough
			return RTEClassificationType.ENTAILMENT;
		case Contradiction:	//fallthrough
			return RTEClassificationType.CONTRADICTION;
		case NonEntailment:	//fallthrough
		case Unknown:		//fallthrough
		case Abstain:		//fallthrough
			return RTEClassificationType.UNKNOWN;
		default: throw new IllegalArgumentException("Unsupported decision type: " + decision);
		}
	}
}