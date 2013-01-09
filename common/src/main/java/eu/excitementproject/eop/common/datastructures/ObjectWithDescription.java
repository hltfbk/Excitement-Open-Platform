package eu.excitementproject.eop.common.datastructures;

/**
 * 
 * @author Asher Stern
 *
 * @param <T>
 */
public class ObjectWithDescription<T>
{
	public ObjectWithDescription(T object, String description)
	{
		super();
		this.object = object;
		this.description = description;
	}
	
	
	public T getObject()
	{
		return object;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
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
		ObjectWithDescription<?> other = (ObjectWithDescription<?>) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		return true;
	}




	private final T object;
	private final String description;
	

}
