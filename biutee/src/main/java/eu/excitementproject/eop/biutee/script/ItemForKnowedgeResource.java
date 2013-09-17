package eu.excitementproject.eop.biutee.script;
import eu.excitementproject.eop.biutee.rteflow.macro.DefaultOperationScript;
import eu.excitementproject.eop.transformations.builtin_knowledge.KnowledgeResource;

/**
 * Wraps a {@link SingleOperationItem} which represents rule-application transformation
 * of a built-in knowledge resource.
 * <BR>
 * Actually, what matters is merely the {@link SingleOperationItem} (the {@link KnowledgeResource} field
 * is not used).
 * <P>
 * The flow is: in {@link OperationsScriptForBuiltinKnowledge}, the <code>init()</code>
 * method initializes built-in knowledge resources, stores the knowledge resources
 * in internal fields of {@link OperationsScript}, and also stores a list of
 * {@link ItemForKnowedgeResource} in a protected field, <code>items</code>.
 * Later, in {@link DefaultOperationScript#init()}, all the {@link SingleOperationItem}
 * of this list are added to the list of transformations to be applied in every search-iteration.
 *   
 * 
 * @author Asher Stern
 * @since Dec 4, 2012
 * 
 * @see OperationsScriptForBuiltinKnowledge
 * @see DefaultOperationScript
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
