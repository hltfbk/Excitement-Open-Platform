package ac.biu.nlp.nlp.engineml.rteflow.micro.perform.dummy;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;

/**
 * 
 * @author Asher Stern
 * @since Jan 25, 2012
 *
 * @param <I>
 * @param <N>
 */
public final class DummyGenerationOperation<I extends Info, N extends AbstractNode<I, N>> extends GenerationOperation<I, N>
{
	public DummyGenerationOperation(TreeAndParentMap<I, N> textTree,
			TreeAndParentMap<I, N> hypothesisTree) throws OperationException
	{
		super(textTree, hypothesisTree);
		throw new OperationException("DummyGenerationOperation should not be created");
	}

	@Override
	protected void generateTheTree() throws OperationException
	{
		throw new OperationException("DummyGenerationOperation should not be created");
	}

	@Override
	protected void generateMapOriginalToGenerated() throws OperationException
	{
		throw new OperationException("DummyGenerationOperation should not be created");
	}

	@Override
	protected void postProcess() throws OperationException
	{
	}

}
