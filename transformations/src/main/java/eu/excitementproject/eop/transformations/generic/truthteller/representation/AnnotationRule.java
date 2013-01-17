package eu.excitementproject.eop.transformations.generic.truthteller.representation;
import java.io.Serializable;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;


/**
 * A Rule is:
 * <OL>
 * <LI>left hand side</LI>
 * <LI>a mapping from (part of) the left hand nodes to {@link ExtendedInfo}s that hold their new annotations.</LI>
 * </OL>
 * <P>
 * Note that Rule does <B>not</B> implement equals() and hashCode(). I.e. each rule is
 * unique in the system. 
 * <P>
 * 
 * 
 * @author Amnon Lotan
 * @since Sep 18, 2011
 *
 */
public class AnnotationRule<N extends AbstractNode<? extends Info, N>, A> implements Serializable
{
	private static final long serialVersionUID = 2817957141706110612L;



	/**
	 * Ctor - null args are allowed in the first two args (some rules with special {@link RuleType}s are really strange)
	 * @param leftHandSide
	 * @param mapNodesToAnnotations
	 * @param ruleType
	 * @throws AnnotatorException 
	 */
	public AnnotationRule(N leftHandSide, Map<N, A> mapNodesToAnnotations,
			RuleType ruleType) throws AnnotatorException {
		super();
		this.leftHandSide = leftHandSide;
		this.mapNodesToAnnotations = mapNodesToAnnotations;
		if (ruleType == null)
			throw new AnnotatorException("got null ruleType");
		if (!ruleType.isAnnotation())
			throw new AnnotatorException("An annotation rule must get an annotation rule type. Got: " + ruleType);
		this.ruleType = ruleType;
	}

	public N getLeftHandSide()
	{
		return leftHandSide;
	}
	
	public Map<N, A> getMapLhsToAnnotations()
	{
		return mapNodesToAnnotations;
	}
	
	/**
	 * @return the ruleType
	 */
	public RuleType getRuleType() {
		return ruleType;
	}
	
	protected final N leftHandSide;
	
	/**
	 * map from LHS to replacement annotations
	 */
	protected final Map<N, A> mapNodesToAnnotations;
	
	protected final RuleType ruleType; 
}
