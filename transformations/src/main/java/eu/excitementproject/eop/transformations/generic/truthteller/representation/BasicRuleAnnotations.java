/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.representation;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation.Monotonicity;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;

/**
 * Default implementation for {@link RuleAnnotations}
 * 
 * <b>MUTABLE</b>
 * 
 * @author Amnon Lotan
 *
 * @since Jul 2, 2012
 */
public class BasicRuleAnnotations implements RuleAnnotations {


	/**
	 * 
	 */
	private static final long serialVersionUID = -881792426993727551L;
	private PredicateSignature sig;
	private NuRuleAnnotationValue nu;
	private CtRuleAnnotationValue ct;
	private PtRuleAnnotationValue pt;
	private Monotonicity monotonicity;
	/**
	 * Ctor
	 * @param sig
	 * @param nu
	 * @param ct
	 * @param pt
	 * @param monotonicity
	 */
	public BasicRuleAnnotations(PredicateSignature sig, NuRuleAnnotationValue nu, CtRuleAnnotationValue ct, PtRuleAnnotationValue pt, Monotonicity monotonicity) {
		super();
		this.sig = sig;
		this.nu = nu;
		this.ct = ct;
		this.pt = pt;
		this.monotonicity = monotonicity;
	}
	
	/**
	 * Ctor without monotonicity, which is not fully supported in the system yet
	 * @param sig
	 * @param nu
	 * @param ct
	 * @param pt
	 * @param monotonicity
	 */
	public BasicRuleAnnotations(PredicateSignature sig, NuRuleAnnotationValue nu, CtRuleAnnotationValue ct, PtRuleAnnotationValue pt) {
		super();
		this.sig = sig;
		this.nu = nu;
		this.ct = ct;
		this.pt = pt;
		this.monotonicity = null;
	}

	/**
	 * @return the sig
	 */
	public PredicateSignature getPredicateSignature() {
		return sig;
	}
	/**
	 * @param sig the sig to set
	 */
	public void setSig(PredicateSignature sig) {
		this.sig = sig;
	}
	/**
	 * @return the nu
	 */
	public NuRuleAnnotationValue getNegationAndUncertainty() {
		return nu;
	}
	/**
	 * @param nu the nu to set
	 */
	public void setNu(NuRuleAnnotationValue nu) {
		this.nu = nu;
	}
	/**
	 * @return the ct
	 */
	public CtRuleAnnotationValue getClauseTruth() {
		return ct;
	}
	/**
	 * @param ct the ct to set
	 */
	public void setCt(CtRuleAnnotationValue ct) {
		this.ct = ct;
	}
	/**
	 * @return the pt
	 */
	public PtRuleAnnotationValue getPredTruth() {
		return pt;
	}
	/**
	 * @param pt the pt to set
	 */
	public void setPt(PtRuleAnnotationValue pt) {
		this.pt = pt;
	}
	/**
	 * @return the monotonicity
	 */
	public Monotonicity getMonotonicity() {
		return monotonicity;
	}
	/**
	 * @param monotonicity the monotonicity to set
	 */
	public void setMonotonicity(Monotonicity monotonicity) {
		this.monotonicity = monotonicity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasicRuleAnnotations [sig=" + sig + ", nu=" + nu + ", ct=" + ct
				+ ", pt=" + pt + ", monotonicity=" + monotonicity + "]";
	}

	
	
	
}
