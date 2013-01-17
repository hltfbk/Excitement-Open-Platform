package eu.excitementproject.eop.transformations.datastructures;

/**
 * Combines a boolean and a string.
 * 
 * @author Asher Stern
 * @since May 22, 2012
 *
 */
public class BooleanAndString
{
	public BooleanAndString(boolean booleanValue, String string)
	{
		super();
		this.booleanValue = booleanValue;
		this.string = string;
	}
	
	
	public String getString()
	{
		return string;
	}

	public boolean getBooleanValue()
	{
		return this.booleanValue;
	}


	private final boolean booleanValue;
	private final String string;
}
