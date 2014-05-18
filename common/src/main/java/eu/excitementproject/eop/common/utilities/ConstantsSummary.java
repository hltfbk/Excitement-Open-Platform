package eu.excitementproject.eop.common.utilities;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides summary about a constants class.
 * Provides a string that contains all constants declared in the class
 * and their values.
 * 
 * @author Asher Stern
 * @since 31/08/2010
 *
 */
public class ConstantsSummary
{
	public ConstantsSummary(Class<?> clazz)
	{
		this.clazz = clazz;
	}
	
	public String getSummary()
	{
		Exception exception = null;
		Set<String> ProblematicFields = new HashSet<String>();
		StringBuffer buffer = new StringBuffer();
		try
		{
			buffer.append("Fields of class: ");
			buffer.append(this.clazz.getName());
			buffer.append("\n");
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields)
			{
				try
				{
					Object objField = field.get(null);
					buffer.append(field.getName());
					buffer.append(" = ");
					buffer.append(objField==null? "null": objField.toString());
					buffer.append("\n");
				}
				catch(NullPointerException e)
				{}
				catch(IllegalAccessException e)
				{
					ProblematicFields.add(field.getName());
				}
				
			}
		}
		catch(Exception e)
		{
			exception = e;
		}
		if (exception!=null)
		{
			buffer.append("\n");
			buffer.append("\nWARNING: could not retrieve all fields due to exception:\n");
			buffer.append(exception.toString());
			buffer.append("\n");
		}
		if (ProblematicFields.size()>0)
		{
			buffer.append("\n");
			for (String fieldName : ProblematicFields)
			{
				buffer.append("could not get the value of field: ");
				buffer.append(fieldName);
				buffer.append("\n");
			}
		}
		return buffer.toString();
	}
	


	private Class<?> clazz;
}
