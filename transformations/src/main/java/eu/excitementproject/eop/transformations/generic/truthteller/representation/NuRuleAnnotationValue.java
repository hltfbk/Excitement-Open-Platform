/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.representation;
import eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty;

/**
 * @author Amnon Lotan
 *
 * @since Jul 2, 2012
 */
public class NuRuleAnnotationValue extends RuleAnnotationValue<NegationAndUncertainty> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3878924379446457582L;

	static
	{
		for (NegationAndUncertainty nu : NegationAndUncertainty.values())
			permittedValues.add(nu.name());
	}
	/**
	 * Ctor
	 * @param value
	 * @throws AnnotationValueException
	 */
	public NuRuleAnnotationValue(String value) throws AnnotationValueException {
		super(value);
	}
	
	/**
	 * Ctor
	 * @throws AnnotationValueException 
	 */
	public NuRuleAnnotationValue(NegationAndUncertainty nu) throws AnnotationValueException {
		this(nu.name());
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.generic.annotation.representation.RuleAnnotationValue#flip(java.lang.Enum)
	 */
	@Override
	protected NegationAndUncertainty flip(NegationAndUncertainty value) throws AnnotationValueException {
		
		try {
			return value.addNegation();
		} catch (eu.excitementproject.eop.transformations.representation.annotations.AnnotationValueException e) {
			throw new AnnotationValueException("See nested", e);
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.generic.annotation.representation.RuleAnnotationValue#getEnum(java.lang.String)
	 */
	@Override
	protected NegationAndUncertainty getEnum(String value) throws AnnotationValueException {
		try {
			return NegationAndUncertainty.valueOf(value);
		} catch (Exception e) {
			throw new AnnotationValueException(value + " is not a valid NU value", e);
		}
	}

}
