package eu.excitementproject.eop.biutee.script;
import eu.excitementproject.eop.transformations.builtin_knowledge.KnowledgeResource;

/**
 * 
 * @author Asher Stern
 * @since Dec 4, 2012
 *
 */
public class ItemForKnowedgeResource
{
	public ItemForKnowedgeResource(KnowledgeResource knowledgeResource,
			SingleOperationItem singleOperationItem)
	{
		super();
		this.knowledgeResource = knowledgeResource;
		this.singleOperationItem = singleOperationItem;
	}
	
	
	public KnowledgeResource getKnowledgeResource()
	{
		return knowledgeResource;
	}
	public SingleOperationItem getSingleOperationItem()
	{
		return singleOperationItem;
	}


	private final KnowledgeResource knowledgeResource;
	private final SingleOperationItem singleOperationItem;
}
