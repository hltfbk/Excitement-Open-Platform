package eu.excitementproject.eop.transformations.operations.rules.lexicalchain.graphbased;

/**
 * @author Asher Stern
 * @since Jan 2, 2013
 */
public class MutableInteger {
	public MutableInteger(int value)
	{
		super();
		this.value = value;
	}
	
	public MutableInteger()
	{
		this(0);
	}
	
	

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}



	private int value = 0;
}
