package ac.biu.nlp.nlp.engineml.operations.operations;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;

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
			this.mapOriginalToGenerated = postProcess.getMapOriginalToGenerated();
			this.affectedNodes = postProcess.getAffectedNodes();
		}
		catch(TeEngineMlException e)
		{
			throw new OperationException("post process failed",e);
		}
	}
	

}
