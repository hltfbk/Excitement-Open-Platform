/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.representation;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;
import eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;


/**
 * This class holds any of the possible values a user may enter for a specific annotation type in the  {@link RuleAnnotations} records in {@link AnnotationRule}.
 * It is meant to be extended by a class that populates the {@value #permittedValues} with all the values in the
 * generic Enum parameter class. To these we add the {@link #FLIP_ANNOTATION_LBL}, which means the user wants to flip the exisitng annotation value matched in the tree.
 * <b>IMMUTABLE</b>
 * @author Amnon Lotan
 *
 * @param <A> One of the annotation enums, like {@link NegationAndUncertainty}, {@link ClauseTruth} {@link PredTruth}, or theoretically
 * even {@link PredicateSignature}.
 * 
 * @since Jul 1, 2012
 */
public abstract class RuleAnnotationValue<A extends Enum<A>> implements Serializable
{
	private static final long serialVersionUID = 5560622798518279251L;

	/**
	 * This annotation value in an {@link AnnotationRule}'s mapped annotations causes TruthTeller to flip the tree node's existing value from + to -, and - to +. 
	 */
	public static final String FLIP_ANNOTATION_LBL = "FLIP";
	
	protected static final Set<String> permittedValues = new LinkedHashSet<String>();
	static
	{
		permittedValues.add(FLIP_ANNOTATION_LBL);
	}
	private final String value;

	private final A enumValue;	// the enum to match the string value, if any

	/**
	 * Ctor
	 * @param value the given value must not be null
	 * @throws AnnotationValueException
	 */
	public RuleAnnotationValue(String value) throws AnnotationValueException {
		if (value == null)
			throw new AnnotationValueException("got null value");
		value = value.toUpperCase();	
		if (!permittedValues.contains(value))
			throw new AnnotationValueException("invalid annotation value: " + value +", these are the permitted values: " + permittedValues);
		this.value = value;
		this.enumValue = !value.equals(FLIP_ANNOTATION_LBL) ?	getEnum(value) : null;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @return the permittedValues
	 */
	public static Set<String> getPermittedValues() {
		return permittedValues;
	}	
	
	/**
	 * For annotations instantiation: return the merge between this rule-annotation value, and a regular annotation value, presumably from a text tree
	 * @param otherValue
	 * @return
	 * @throws AnnotationValueException 
	 */
	public A mergeAnnotation(A otherValue) throws AnnotationValueException
	{
		if (otherValue == null)
			return enumValue;
		if (enumValue == null)
			return flip(otherValue);
		return enumValue;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value;
	}
	
	///////////////////////////////////////////// PROTECTED ABSTRACT	///////////////////////////////////////////////////
	
	/**
	 * Return a P for a N, an N for a P, or return it as is
	 * @param otherValue
	 * @return
	 * @throws AnnotationValueException 
	 */
	protected abstract A flip(A value) throws AnnotationValueException ;
	
	/**
	 * @param value
	 * @return
	 * @throws AnnotationValueException 
	 */
	protected abstract A getEnum(String value) throws AnnotationValueException ;
	
	
	
}
