package eu.excitementproject.eop.common.utilities.datasets.rtepairs;

import java.io.Serializable;

/**
 * Immutable class that contains:
 * <UL>
 * <LI>text</LI>
 * <LI>hypothesis</LI>
 * <LI>id</LI>
 * <LI>classification type (i.e. whether the text entails the hypothesis)</LI>
 * <LI>additional info (i.e. task)</LI>
 * </UL>
 * 
 * @author Asher Stern
 *
 */
public class TextHypothesisPair implements Serializable
{
	private static final long serialVersionUID = 1505986162500036461L;
	///////////////////// PUBLIC PART ///////////////////
	
	// public constructors

	
	public TextHypothesisPair(String text, String hypothesis, Integer id,
			String additionalInfo) {
		super();
		this.text = text;
		this.hypothesis = hypothesis;
		this.id = id;
		this.additionalInfo = additionalInfo;
	}


	public TextHypothesisPair(String text, String hypothesis, Integer id,
			Boolean booleanClassificationType, String additionalInfo) {
		super();
		this.text = text;
		this.hypothesis = hypothesis;
		this.id = id;
		this.booleanClassificationType = booleanClassificationType;
		this.additionalInfo = additionalInfo;
	}
	
	
	public TextHypothesisPair(String text, String hypothesis, Integer id,
			RTEClassificationType classificationType, String additionalInfo) {
		super();
		this.text = text;
		this.hypothesis = hypothesis;
		this.id = id;
		this.classificationType = classificationType;
		this.additionalInfo = additionalInfo;
	}
	
	// getters
	
	public String getText() {
		return text;
	}
	public String getHypothesis() {
		return hypothesis;
	}
	public Integer getId() {
		return id;
	}
	public RTEClassificationType getClassificationType()
	{
		if (classificationType!=null)
			return classificationType;
		else if (booleanClassificationType!=null)
		{
			if (booleanClassificationType.booleanValue()==true)
				return RTEClassificationType.ENTAILMENT;
			else
				return RTEClassificationType.UNKNOWN;
		}
		else
			return null;
	}
	
	public Boolean getBooleanClassificationType()
	{
		if (booleanClassificationType!=null)
			return booleanClassificationType;
		else if (classificationType!=null)
		{
			if (classificationType==RTEClassificationType.ENTAILMENT)
				return true;
			else
				return false;
		}
		else
			return null;
	}
	
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	
	
	// equals() and hashCode() methods
	
	@Override
	public int hashCode() {
		if (hashCodeSet) return hashCodeValue;
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((additionalInfo == null) ? 0 : additionalInfo.hashCode());
		result = prime
				* result
				+ ((booleanClassificationType == null) ? 0
						: booleanClassificationType.hashCode());
		result = prime
				* result
				+ ((classificationType == null) ? 0 : classificationType
						.hashCode());
		result = prime * result
				+ ((hypothesis == null) ? 0 : hypothesis.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		hashCodeValue = result;
		hashCodeSet = true;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextHypothesisPair other = (TextHypothesisPair) obj;
		if (additionalInfo == null) {
			if (other.additionalInfo != null)
				return false;
		} else if (!additionalInfo.equals(other.additionalInfo))
			return false;
		if (booleanClassificationType == null) {
			if (other.booleanClassificationType != null)
				return false;
		} else if (!booleanClassificationType
				.equals(other.booleanClassificationType))
			return false;
		if (classificationType == null) {
			if (other.classificationType != null)
				return false;
		} else if (!classificationType.equals(other.classificationType))
			return false;
		if (hypothesis == null) {
			if (other.hypothesis != null)
				return false;
		} else if (!hypothesis.equals(other.hypothesis))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}




	////////////////// PROTECTED AND PRIVATE //////////////////////////


	protected String text;
	protected String hypothesis;
	protected Integer id;
	protected RTEClassificationType classificationType;
	protected Boolean booleanClassificationType;
	protected String additionalInfo;
	
	
	transient private boolean hashCodeSet = false;
	transient private int hashCodeValue = 0;

}
