package ac.biu.nlp.nlp.engineml.representation.srl;

import java.io.Serializable;
import java.util.Set;

import ac.biu.nlp.nlp.general.immutable.ImmutableSet;
import ac.biu.nlp.nlp.general.immutable.ImmutableSetWrapper;

/**
 * 
 * @author Asher Stern
 * @since Dec 27, 2011
 *
 */
public class SemanticRoleLabelSet implements Serializable
{
	private static final long serialVersionUID = 5690960805707731907L;
	
	
	public SemanticRoleLabelSet(
			ImmutableSet<SemanticRoleLabelByString> srlByString,
			ImmutableSet<SemanticRoleLabelById> srlByNode)
	{
		super();
		this.srlByString = srlByString;
		this.srlById = srlByNode;
	}
	
	
	public SemanticRoleLabelSet(
			Set<SemanticRoleLabelByString> srlByString,
			Set<SemanticRoleLabelById> srlByNode)
	{
		this(new ImmutableSetWrapper<SemanticRoleLabelByString>(srlByString),new ImmutableSetWrapper<SemanticRoleLabelById>(srlByNode));
	}
	
	public SemanticRoleLabelSet(
			ImmutableSet<SemanticRoleLabelByString> srlByString,
			Set<SemanticRoleLabelById> srlByNode)
	{
		this(srlByString,new ImmutableSetWrapper<SemanticRoleLabelById>(srlByNode));
	}
	

	
	public ImmutableSet<SemanticRoleLabelByString> getSrlByString()
	{
		return srlByString;
	}


	public ImmutableSet<SemanticRoleLabelById> getSrlById()
	{
		return srlById;
	}
	
	




	private final ImmutableSet<SemanticRoleLabelByString> srlByString;
	private final ImmutableSet<SemanticRoleLabelById> srlById;
}

