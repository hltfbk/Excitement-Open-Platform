package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

/**
 * 
 * @author Asher Stern
 * @since Dec 9, 2013
 *
 */
public class ArkrefMarker
{
	public ArkrefMarker(String entityId, String mentionId, boolean begin)
	{
		super();
		this.entityId = entityId;
		this.mentionId = mentionId;
		this.begin = begin;
	}
	
	
	
	public String getEntityId()
	{
		return entityId;
	}
	public String getMentionId()
	{
		return mentionId;
	}
	public boolean isBegin()
	{
		return begin;
	}



	private final String entityId;
	private final String mentionId;
	private final boolean begin; // true = begin. false = end.
}
