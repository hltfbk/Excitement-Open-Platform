package eu.excitementproject.eop.transformations.representation;
import java.io.Serializable;

import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;
import eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;
import eu.excitementproject.eop.transformations.representation.srl_informations.SemanticRoleLabelSet;
import eu.excitementproject.eop.transformations.representation.srl_informations.SrlPredicateId;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.ContentAncestorSetter;

/**
 * 
 * <B> Any change in this class should be reflected
 * by {@link AdditionalInformationServices} as well</B>, since creation of
 * {@link AdditionalNodeInformation} is done by
 * {@link AdditionalInformationServices}'s methods<BR>
 * Note that the constructors are not public
 * 
 * <P>
 * 
 *                       <B>THIS CLASS IS IMMUTABLE</B>
 * <BR>
 * <P>
 * <B>PLEASE READ CAREFULLY</B><BR>
 * Adding a new field:
 * <OL>
 * <LI>Add the field</LI>
 * <LI>Add it to the default constructor</LI>
 * <LI>Add it to the all-fields constructor</LI>
 * <LI>Add it to the copy constructor</LI>
 * <LI>Add a special constructor for setting this field only</LI>
 * <LI>Add a set method to {@link AdditionalInformationServices}</LI>
 * </OL>
 * 
 * 
 * 
 * @author Asher Stern
 * @since Apr 6, 2011
 *
 */
public class AdditionalNodeInformation implements Serializable
{
	private static final long serialVersionUID = 7562113049184485599L;
	
	
	/**
	 * This is a label for {@link AdditionalNodeInformation}s pertaining to verbs ({@link SimplerCanonicalPosTag}{@code .VERB}) that says
	 * whether the verb's monotonicity value is UP or DOWN. This is useful cos if a verb is UP, you can calmly delete its modifiers and preserve
	 * entailment.  
	 * <br>For example: 
	 * <li>It {UP} was [just a false alarm] --> it {UP} was [an alarm] 
	 * 
	 * @author Amnon Lotan
	 * @since 31/05/2011
	 * 
	 */
	public enum Monotonicity {
		UP,
		DOWN,
	}
	
	
	
	// Constructors as follows:
	// 1. Default constructor
	// 2. Constructor with all fields
	// 3. Copy constructor (protected)
	// 4. For each field: constructor that copies an "original" AdditionalNodeInformation
	//    but sets that field to a new value (the original is unchanged. The create object
	//    is identical to the original except that field).
	
	/**
	 * Default constructor - all default
	 */
	AdditionalNodeInformation()
	{
		this.corefGroupId = null;
		this.uniqueIdForCoref = null;
		this.contentAncestor = null;
		this.predicateSignature = null;
		this.negationAndUncertainty = null;
		this.predTruth = null;
		this.clauseTruth = null;
		this.monotonicity = null;
		this.originalInfoTrace = null;
		this.srlSet = null;
		this.srlPredicateId = null;
	}
	
	/**
	 * Constructor to set all fields
	 * @param corefGroupId
	 * @param contentAncestor
	 * @param predType
	 * @param negationAndUncertainty
	 * @param predTruth
	 * @param clauseTruth
	 * @param monotonicity
	 * @param originalInfoTrace
	 * @param srlSet
	 */
	AdditionalNodeInformation(Integer corefGroupId, Integer uniqueIdForCoref, ExtendedInfo contentAncestor, 
			PredicateSignature predType, NegationAndUncertainty negationAndUncertainty, PredTruth predTruth, ClauseTruth clauseTruth, Monotonicity monotonicity,
			OriginalInfoTrace originalInfoTrace, SemanticRoleLabelSet srlSet, SrlPredicateId srlPredicateId)
	{
		this.corefGroupId = corefGroupId;
		this.uniqueIdForCoref = uniqueIdForCoref;
		this.contentAncestor = contentAncestor;
		this.predicateSignature = predType;
		this.negationAndUncertainty = negationAndUncertainty;
		this.predTruth = predTruth;
		this.clauseTruth = clauseTruth;
		this.monotonicity = monotonicity;
		this.originalInfoTrace = originalInfoTrace;
		this.srlSet = srlSet;
		this.srlPredicateId = srlPredicateId;
	}	

	/**
	 * Simple protected Copy constructor
	 * @param original
	 */
	protected AdditionalNodeInformation(AdditionalNodeInformation original)
	{
		if (original!=null)
		{
			// set ALL fields
			this.corefGroupId = original.corefGroupId;
			this.uniqueIdForCoref = original.uniqueIdForCoref;
			this.contentAncestor = original.contentAncestor;
			this.predicateSignature = original.predicateSignature;
			this.negationAndUncertainty = original.negationAndUncertainty;
			this.predTruth =  original.predTruth;
			this.clauseTruth =  original.clauseTruth;
			this.monotonicity = original.monotonicity;
			this.originalInfoTrace = original.getOriginalInfoTrace();
			this.srlSet = original.srlSet;
			this.srlPredicateId = original.srlPredicateId;
		}
		else
		{
			// set ALL fields as null
			this.corefGroupId = null;
			this.uniqueIdForCoref = null;
			this.contentAncestor = null;
			this.predicateSignature = null;
			this.negationAndUncertainty = null;
			this.predTruth = null;
			this.clauseTruth = null;
			this.monotonicity = null;
			this.originalInfoTrace = null;
			this.srlSet = null;
			this.srlPredicateId = null;
		}
	}
	
	/**
	 * Constructor to set corefGroupId
	 * @param corefGroupId
	 * @param original
	 */
	AdditionalNodeInformation(Integer corefGroupId, Integer uniqueIdForCoref, AdditionalNodeInformation original)
	{
		this(original);
		this.corefGroupId = corefGroupId;
		this.uniqueIdForCoref = uniqueIdForCoref;
	}
	
	/**
	 * Constructor to set contentAncestor
	 * @param contentAncestor
	 * @param original
	 */
	AdditionalNodeInformation(ExtendedInfo contentAncestor, AdditionalNodeInformation original)
	{
		this(original);
		this.contentAncestor = contentAncestor;
	}

	/**
	 * Constructor to set predicateType
	 * @param predicateType
	 * @param original
	 */
	AdditionalNodeInformation(PredicateSignature predicateType, AdditionalNodeInformation original)
	{
		this(original);
		this.predicateSignature = predicateType;
	}	
	
	/**
	 * Constructor to set negationAndUncertainty
	 * @param negationAndUncertainty
	 * @param original
	 */
	AdditionalNodeInformation(NegationAndUncertainty negationAndUncertainty, AdditionalNodeInformation original)
	{
		this(original);
		this.negationAndUncertainty = negationAndUncertainty;
	}
	
	/**
	 * Constructor to set predTruth
	 * @param predTruth
	 * @param original
	 */
	AdditionalNodeInformation(PredTruth predTruth, AdditionalNodeInformation original)
	{
		this(original);
		this.predTruth = predTruth;
	}
	
	/**
	 * Constructor to set clauseTruth
	 * @param clauseTruth
	 * @param original
	 */
	AdditionalNodeInformation(ClauseTruth clauseTruth, AdditionalNodeInformation original)
	{
		this(original);
		this.clauseTruth = clauseTruth;
	}
	
	/**
	 * Constructor to set monotonicity
	 * @param monotonicity
	 * @param original
	 */
	AdditionalNodeInformation(Monotonicity monotonicity, AdditionalNodeInformation original)
	{
		this(original);
		this.monotonicity = monotonicity;
	}
	
	AdditionalNodeInformation(OriginalInfoTrace originalInfoTrace, AdditionalNodeInformation original)
	{
		this(original);
		this.originalInfoTrace = originalInfoTrace;
	}

	AdditionalNodeInformation(SemanticRoleLabelSet srlSet, AdditionalNodeInformation original)
	{
		this(original);
		this.srlSet = srlSet;
	}

	AdditionalNodeInformation(SrlPredicateId srlPredicateId, AdditionalNodeInformation original)
	{
		this(original);
		this.srlPredicateId = srlPredicateId;
	}

	// Getters
	
	public Integer getCorefGroupId()
	{
		return corefGroupId;
	}
	public Integer getUniqueIdForCoref()
	{
		return uniqueIdForCoref;
	}

	public ExtendedInfo getContentAncestor()
	{
		return contentAncestor;
	}

	/**
	 * @return the negativity
	 */
	public PredTruth getPredTruth() {
		return predTruth;
	}

	/**
	 * @return the monotonicity
	 */
	public Monotonicity getMonotonicity() {
		return monotonicity;
	}

	/**
	 * @return the predicateType
	 */
	public PredicateSignature getPredicateSignature() {
		return predicateSignature;
	}

	/**
	 * @return the implicativity
	 */
	public ClauseTruth getClauseTruth() {
		return clauseTruth;
	}

	/**
	 * @return the negation
	 */
	public NegationAndUncertainty getNegationAndUncertainty() {
		return negationAndUncertainty;
	}
	
	
	public OriginalInfoTrace getOriginalInfoTrace()
	{
		return originalInfoTrace;
	}


	public SemanticRoleLabelSet getSrlSet()
	{
		return this.srlSet;
	}
	
	public SrlPredicateId getSrlPredicateId()
	{
		return srlPredicateId;
	}


	
	

	
	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clauseTruth == null) ? 0 : clauseTruth.hashCode());
		result = prime * result
				+ ((contentAncestor == null) ? 0 : contentAncestor.hashCode());
		result = prime * result
				+ ((corefGroupId == null) ? 0 : corefGroupId.hashCode());
		result = prime * result
				+ ((monotonicity == null) ? 0 : monotonicity.hashCode());
		result = prime
				* result
				+ ((negationAndUncertainty == null) ? 0
						: negationAndUncertainty.hashCode());
		result = prime
				* result
				+ ((originalInfoTrace == null) ? 0 : originalInfoTrace
						.hashCode());
		result = prime * result
				+ ((predTruth == null) ? 0 : predTruth.hashCode());
		result = prime
				* result
				+ ((predicateSignature == null) ? 0 : predicateSignature
						.hashCode());
		result = prime * result
				+ ((srlPredicateId == null) ? 0 : srlPredicateId.hashCode());
		result = prime * result + ((srlSet == null) ? 0 : srlSet.hashCode());
		result = prime
				* result
				+ ((uniqueIdForCoref == null) ? 0 : uniqueIdForCoref.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AdditionalNodeInformation other = (AdditionalNodeInformation) obj;
		if (clauseTruth != other.clauseTruth)
			return false;
		if (contentAncestor == null)
		{
			if (other.contentAncestor != null)
				return false;
		} else if (!contentAncestor.equals(other.contentAncestor))
			return false;
		if (corefGroupId == null)
		{
			if (other.corefGroupId != null)
				return false;
		} else if (!corefGroupId.equals(other.corefGroupId))
			return false;
		if (monotonicity != other.monotonicity)
			return false;
		if (negationAndUncertainty != other.negationAndUncertainty)
			return false;
		if (originalInfoTrace == null)
		{
			if (other.originalInfoTrace != null)
				return false;
		} else if (!originalInfoTrace.equals(other.originalInfoTrace))
			return false;
		if (predTruth != other.predTruth)
			return false;
		if (predicateSignature != other.predicateSignature)
			return false;
		if (srlPredicateId == null)
		{
			if (other.srlPredicateId != null)
				return false;
		} else if (!srlPredicateId.equals(other.srlPredicateId))
			return false;
		if (srlSet == null)
		{
			if (other.srlSet != null)
				return false;
		} else if (!srlSet.equals(other.srlSet))
			return false;
		if (uniqueIdForCoref == null)
		{
			if (other.uniqueIdForCoref != null)
				return false;
		} else if (!uniqueIdForCoref.equals(other.uniqueIdForCoref))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AdditionalNodeInformation [predicateType=" + predicateSignature
				+ ", clauseTruth=" + clauseTruth + ", NU="
				+ negationAndUncertainty + ", predTruth=" + predTruth + ", monotonicity="
				+ monotonicity + "]";
	}

	private Integer corefGroupId = null;
	private Integer uniqueIdForCoref = null;
	
	private PredicateSignature predicateSignature = null;
	private ClauseTruth clauseTruth = null;
	private NegationAndUncertainty negationAndUncertainty = null;

	/**
	 * This is a label for AdditionalNodeInformations pertaining to predicates 
	 * that says whether the predicate's action is performed, negated or uncertain. For non predicates it is undefined. 
	 */
	private PredTruth predTruth = null;
	
	private Monotonicity monotonicity = null;
	
	/**
	 * @see ContentAncestorSetter
	 */
	private ExtendedInfo contentAncestor = null;
	
	/**
	 * The {@link ExtendedInfo} of the original text tree from which this node was created.
	 */
	private OriginalInfoTrace originalInfoTrace = null;
	
	private SemanticRoleLabelSet srlSet = null;
	private SrlPredicateId srlPredicateId = null;
}
