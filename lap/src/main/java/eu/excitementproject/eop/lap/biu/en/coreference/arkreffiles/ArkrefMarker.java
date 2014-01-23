package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

/**
 * Represents an XML-like tag which indicates beginning or ending of a mention span
 * in ArkRef output file.
 * Each begin-tag contains the entity-id and mention-id of this mention.
 * End-tag merely closes the last begin-tag, and indicates the end of the span. 
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
