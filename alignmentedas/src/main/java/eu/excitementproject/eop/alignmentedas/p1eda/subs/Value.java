package eu.excitementproject.eop.alignmentedas.p1eda.subs;

import java.io.Serializable;

/**
 * This class expresses a "value" that can be either a Boolean, a double number, or a nominal value. 
 * 
 * The main usage of this class is to be represented as one "feature value", or one "parameter value". 
 * 
 * 
 * @author Tae-Gil Noh
 *
 */
public class Value implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6413857523514908870L;

	public Value(double d)
	{
		dValue = d; 
		bValue = null; 
		nValue = null; 
		type = ValueType.DOUBLE; 
	}
	
	public Value(Enum<?> e)
	{
		dValue = null; 
		bValue = null; 
		nValue = e; 
		type = ValueType.NOMINAL; 
	}
	
	public Value(Boolean b)
	{
		dValue = null; 
		bValue = b; 
		nValue = null; 
		type = ValueType.BOOLEAN; 
	}
	
	// getters
	
	public Boolean getBooleanValue() throws ValueException
	{
		if (type != ValueType.BOOLEAN)
		{
			throw new ValueException("Boolean value requested, while the value is not a boolean");  
		}
		return bValue; 
	}
	
	public Double getDoubleValue() throws ValueException 
	{
		if (type != ValueType.DOUBLE)
		{
			throw new ValueException("Double value requested, while the value is not a double");  
		}
		return dValue; 
	}
	
	public Enum<?> getNominalValue() throws ValueException
	{
		if (type != ValueType.NOMINAL)
		{
			throw new ValueException("Nominal value requested, while the value is not a Nominal");  
		}
		return nValue; 
	}
	
	public ValueType getValueType()
	{
		return this.type; 
	}
	
	// overriding toString 
	@Override
	public String toString()
	{
		if (type == ValueType.BOOLEAN)
		{
			return bValue.toString(); 
		}
		else if (type == ValueType.DOUBLE)
		{
			return dValue.toString(); 
		}
		else //  (type == ValueType.NOMINAL)
		{
			return nValue.toString(); 
		}
	}
	
	// private data 
	
	private final ValueType type;  
	private final Double dValue; 
	private final Boolean bValue; 
	private final Enum<?> nValue; 
	
	public enum ValueType 
	{
		DOUBLE, BOOLEAN, NOMINAL
	}
		
}
