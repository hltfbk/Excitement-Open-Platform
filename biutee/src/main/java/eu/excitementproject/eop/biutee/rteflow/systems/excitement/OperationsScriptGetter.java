package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.operations.OperationException;

/**
 * 
 * @author Asher Stern
 * @since Jan 28, 2013
 *
 */
@NotThreadSafe
public class OperationsScriptGetter
{
	public OperationsScriptGetter(ScriptFactory factory)
	{
		super();
		this.factory = factory;
	}
	
	public OperationsScriptGetter(OperationsScript<Info, BasicNode> script)
	{
		super();
		this.script = script;
	}


	public OperationsScript<Info, BasicNode> getScript() throws OperationException
	{
		if (null==script)
		{
			script = factory.getDefaultScript();
			script.init();
		}
		return script;
	}
	
	public void cleanUp()
	{
		if (script!=null)
		{
			script.cleanUp();
		}
	}
	
	private ScriptFactory factory;
	private OperationsScript<Info, BasicNode> script = null;

}
