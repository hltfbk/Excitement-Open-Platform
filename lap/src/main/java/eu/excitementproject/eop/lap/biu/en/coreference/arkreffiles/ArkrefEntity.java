package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

/**
 * 
 * @author Asher Stern
 * @since Dec 9, 2013
 *
 */
public class ArkrefEntity
{
	public ArkrefEntity(String mentionId, String entityId)
	{
		super();
		this.mentionId = mentionId;
		this.entityId = entityId;
	}
	
	
	
	public String getMentionId()
	{
		return mentionId;
	}
	public String getEntityId()
	{
		return entityId;
	}
	
	



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result
				+ ((mentionId == null) ? 0 : mentionId.hashCode());
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
		ArkrefEntity other = (ArkrefEntity) obj;
		if (entityId == null)
		{
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (mentionId == null)
		{
			if (other.mentionId != null)
				return false;
		} else if (!mentionId.equals(other.mentionId))
			return false;
		return true;
	}





	private final String mentionId;
	private final String entityId;
}
