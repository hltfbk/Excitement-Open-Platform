package eu.excitementproject.eop.transformations.operations.operations;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * {@link GenerationOperation} in which the generic types are {@link ExtendedInfo}
 * and {@link ExtendedNode}.
 * Note that {@link GenerationOperation#postProcess()} is implemented here, using the
 * class {@link OperationPostProcess}.
 * <P>
 * <B>Please read the JavaDoc comments of {@link GenerationOperation}</B>
 * 
 * @see GenerationOperation
 * 
 * @author Asher Stern
 * @since Dec 25, 2011
 *
 */
public abstract class GenerationOperationForExtendedNode extends GenerationOperation<ExtendedInfo, ExtendedNode>
{
	public GenerationOperationForExtendedNode(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree)
			throws OperationException
	{
		super(textTree, hypothesisTree);
	}

	@Override
	protected void postProcess() throws OperationException
	{
		try
		{
			OperationPostProcess postProcess = new OperationPostProcess(this);
			postProcess.postProcess();
			this.generatedTree = postProcess.getGeneratedTree();
			this.generatedTree.seal();
			this.mapOriginalToGenerated = postProcess.getMapOriginalToGenerated();
			this.affectedNodes = postProcess.getAffectedNodes();
		}
		catch(TeEngineMlException e)
		{
			throw new OperationException("post process failed",e);
		}
	}
	

}
