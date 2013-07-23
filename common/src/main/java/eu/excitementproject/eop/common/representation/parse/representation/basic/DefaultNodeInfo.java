package eu.excitementproject.eop.common.representation.parse.representation.basic;


/**
 * This class is immutable, provided the underlying implementations of the type:
 * {@linkplain SyntacticInfo} is immutable as well.
 * <P>
 * To create a {@linkplain NodeInfo} with word, lemma, etc. - use the constructor.
 * <BR>
 * To create a {@linkplain NodeInfo} that represents a variable (X_1, X_2, etc.),
 * use the methods {@link #newVariableDefaultNodeInfo(Integer, SyntacticInfo)}
 * and {@link #newVariableDefaultNodeInfo(Integer, String, int, NamedEntity, SyntacticInfo)}.
 * 
 * @author Asher Stern
 *
 */
public class DefaultNodeInfo implements NodeInfo
{
	private static final long serialVersionUID = 952931834451105366L;
	
	public DefaultNodeInfo(String word, String lemma, int serial, NamedEntity namedEntity, SyntacticInfo syntacticInfo)
	{
		this.word = word;
		this.lemma = lemma;
		this.serial = serial;
		this.namedEntity = namedEntity;
		this.syntacticInfo = syntacticInfo;
		
	}
	
	public static DefaultNodeInfo newVariableDefaultNodeInfo(Integer variableId, String lemma, int serial, NamedEntity namedEntity, SyntacticInfo syntacticInfo)
	{
		DefaultNodeInfo ret = new DefaultNodeInfo(null, lemma, serial, namedEntity, syntacticInfo);
		ret.variableId = variableId;
		return ret;
	}
	
	public static DefaultNodeInfo newVariableDefaultNodeInfo(Integer variableId, SyntacticInfo syntacticInfo)
	{
		return newVariableDefaultNodeInfo(variableId,null,0,null,syntacticInfo);
	}
	
	public static DefaultNodeInfo duplicate(NodeInfo nodeInfo)
	{
		if (nodeInfo==null) return null;
		else
		{
			DefaultNodeInfo ret = new DefaultNodeInfo(nodeInfo.getWord(), nodeInfo.getWordLemma(), nodeInfo.getSerial(), nodeInfo.getNamedEntityAnnotation(), nodeInfo.getSyntacticInfo());
			ret.variableId = nodeInfo.getVariableId();
			return ret;
		}
		
	}
	

	public NamedEntity getNamedEntityAnnotation()
	{
		return this.namedEntity;
	}

	public int getSerial()
	{
		return this.serial;
	}

	public SyntacticInfo getSyntacticInfo()
	{
		return this.syntacticInfo;
	}

	public String getWord()
	{
		return this.word;
	}

	public String getWordLemma()
	{
		return this.lemma;
	}
	
	public boolean isVariable()
	{
		return (variableId!=null);
	}
	
	public Integer getVariableId()
	{
		return variableId;
	}


	public boolean isEqualTo(NodeInfo other)
	{
		if (this==other) return true;
		if (other==null) return false;
		
		
		
		if (lemma == null)
		{
			if (other.getWordLemma() != null)
				return false;
		} else if (!lemma.equals(other.getWordLemma()))
			return false;
		if (namedEntity == null)
		{
			if (other.getNamedEntityAnnotation() != null)
				return false;
		} else if (!namedEntity.equals(other.getNamedEntityAnnotation()))
			return false;
		if (syntacticInfo == null)
		{
			if (other.getSyntacticInfo() != null)
				return false;
		} else if (!syntacticInfo.equals(other.getSyntacticInfo()))
			return false;
		return true;
	}
	
	
	

	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getWordLemma() == null) ? 0 : getWordLemma().hashCode());
		result = prime * result
				+ ((getNamedEntityAnnotation() == null) ? 0 : getNamedEntityAnnotation().hashCode());
		result = prime * result + serial;
		result = prime * result
				+ ((getSyntacticInfo() == null) ? 0 : getSyntacticInfo().hashCode());
		result = prime * result
				+ ((getVariableId() == null) ? 0 : getVariableId().hashCode());
		result = prime * result + ((getWord() == null) ? 0 : getWord().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NodeInfo))
			return false;
		NodeInfo other = (NodeInfo) obj;
		if (getWordLemma() == null)
		{
			if (other.getWordLemma() != null)
				return false;
		} else if (!getWordLemma().equals(other.getWordLemma()))
			return false;
		if (getNamedEntityAnnotation() == null)
		{
			if (other.getNamedEntityAnnotation() != null)
				return false;
		} else if (!getNamedEntityAnnotation().equals(other.getNamedEntityAnnotation()))
			return false;
		if (getSerial() != other.getSerial())
			return false;
		if (getSyntacticInfo() == null)
		{
			if (other.getSyntacticInfo() != null)
				return false;
		} else if (!getSyntacticInfo().equals(other.getSyntacticInfo()))
			return false;
		if (getVariableId() == null)
		{
			if (other.getVariableId() != null)
				return false;
		} else if (!getVariableId().equals(other.getVariableId()))
			return false;
		if (getWord() == null)
		{
			if (other.getWord() != null)
				return false;
		} else if (!getWord().equals(other.getWord()))
			return false;
		return true;
	}





	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultNodeInfo [word=" + word + ", lemma=" + lemma
				+ ", serial=" + serial + ", syntacticInfo=" + syntacticInfo
				+ ", variableId=" + variableId + "]";
	}





	protected String word;
	protected String lemma;
	protected int serial;
	protected NamedEntity namedEntity;
	protected SyntacticInfo syntacticInfo;
	protected Integer variableId = null;


}
