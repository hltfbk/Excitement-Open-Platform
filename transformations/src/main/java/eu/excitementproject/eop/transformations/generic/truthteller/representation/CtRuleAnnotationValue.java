/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.representation;
import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;

/**
 * @author Amnon Lotan
 *
 * @since Jul 2, 2012
 */
public class CtRuleAnnotationValue extends RuleAnnotationValue<ClauseTruth> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1979153269844092710L;

	static
	{
		for (ClauseTruth ct : ClauseTruth.values())
			permittedValues.add(ct.name());
	}
	/**
	 * Ctor
	 * @param value
	 * @throws AnnotationValueException
	 */
	public CtRuleAnnotationValue(String value) throws AnnotationValueException {
		super(value);
	}
	
	/**
	 * Ctor
	 * @throws AnnotationValueException 
	 */
	public CtRuleAnnotationValue(ClauseTruth ct) throws AnnotationValueException {
		this(ct.name());
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.generic.annotation.representation.RuleAnnotationValue#flip(java.lang.Enum)
	 */
	@Override
	protected ClauseTruth flip(ClauseTruth value) throws AnnotationValueException {
		
		try {
			return value.flip();
		} catch (eu.excitementproject.eop.transformations.representation.annotations.AnnotationValueException e) {
			throw new AnnotationValueException("See nested", e);
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.generic.annotation.representation.RuleAnnotationValue#getEnum(java.lang.String)
	 */
	@Override
	protected ClauseTruth getEnum(String value) throws AnnotationValueException {
		try {
			return ClauseTruth.valueOf(value);
		} catch (Exception e) {
			throw new AnnotationValueException(value + " is not a valid CT value", e);
		}
	}


}
