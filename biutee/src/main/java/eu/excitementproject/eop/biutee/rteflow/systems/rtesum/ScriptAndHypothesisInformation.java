package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;
import eu.excitementproject.eop.biutee.script.HypothesisInformation;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

/**
 * 
 * @author Asher Stern
 * @since Aug 8, 2012
 *
 */
public class ScriptAndHypothesisInformation
{
	public ScriptAndHypothesisInformation(
			OperationsScript<Info, BasicNode> script,
			HypothesisInformation hypothesisInformation)
	{
		super();
		this.script = script;
		this.hypothesisInformation = hypothesisInformation;
	}
	
	public OperationsScript<Info, BasicNode> getScript()
	{
		return script;
	}
	public HypothesisInformation getHypothesisInformation()
	{
		return hypothesisInformation;
	}



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((hypothesisInformation == null) ? 0 : hypothesisInformation
						.hashCode());
		result = prime * result + ((script == null) ? 0 : script.hashCode());
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
		ScriptAndHypothesisInformation other = (ScriptAndHypothesisInformation) obj;
		if (hypothesisInformation == null)
		{
			if (other.hypothesisInformation != null)
				return false;
		} else if (!hypothesisInformation.equals(other.hypothesisInformation))
			return false;
		if (script == null)
		{
			if (other.script != null)
				return false;
		} else if (!script.equals(other.script))
			return false;
		return true;
	}


	private final OperationsScript<Info, BasicNode> script;
	private final HypothesisInformation hypothesisInformation;
}
