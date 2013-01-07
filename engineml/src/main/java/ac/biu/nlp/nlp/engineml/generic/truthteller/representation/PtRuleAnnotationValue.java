/**
 * 
 */
package ac.biu.nlp.nlp.engineml.generic.truthteller.representation;

import ac.biu.nlp.nlp.engineml.representation.annotations.PredTruth;

/**
 * A {@link RuleAnnotationValue} with {@link PredTruth}
 * @author Amnon Lotan
 *
 * @since Jul 2, 2012
 */
public class PtRuleAnnotationValue extends RuleAnnotationValue<PredTruth> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8814209469836656064L;

	static
	{
		for (PredTruth pt : PredTruth.values())
			permittedValues.add(pt.name());
	}
	/**
	 * Ctor
	 * @param value
	 * @throws AnnotationValueException
	 */
	public PtRuleAnnotationValue(String value) throws AnnotationValueException {
		super(value);
	}
	
	/**
	 * Ctor
	 * @throws AnnotationValueException 
	 */
	public PtRuleAnnotationValue(PredTruth pt) throws AnnotationValueException {
		this(pt.name());
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.generic.annotation.representation.RuleAnnotationValue#flip(java.lang.Enum)
	 */
	@Override
	protected PredTruth flip(PredTruth value) throws AnnotationValueException {
		
		try {
			return value.flip();
		} catch (ac.biu.nlp.nlp.engineml.representation.annotations.AnnotationValueException e) {
			throw new AnnotationValueException("See nested", e);
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.generic.annotation.representation.RuleAnnotationValue#getEnum(java.lang.String)
	 */
	@Override
	protected PredTruth getEnum(String value) throws AnnotationValueException {
		try {
			return PredTruth.valueOf(value);
		} catch (Exception e) {
			throw new AnnotationValueException(value + " is not a valid PT value", e);
		}
	}
}