package eu.excitementproject.eop.transformations.operations.operations;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.Finder;
import eu.excitementproject.eop.transformations.operations.finders.SubstitutionMultiWordUnderlyingFinder;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;

/**
 * A generation operation creates a new tree, based on an existing one.
 * A {@link GenerationOperation} is a <I>transformation</I>.
 * <P>
 * In addition to the generation of a new tree, it also stores a mapping from the
 * original tree's nodes to the new tree's nodes.
 * That mapping reflects: For a node in the new tree, which node in the original tree it is based on.
 * <BR>
 * For example: If the operation did nothing on a certain node, then that node was merely
 * copied as is to the new tree, so the mapping maps from that original node to the new node.
 * <BR>
 * Another example: If a new node was created by a rule application, and that new node is mapped
 * to a left hand side node in the rule, so the mapping will map the node in the original tree
 * that was matched for that left-hand-side node to the new node.
 * <BR>
 * Note that not every node in the new tree has a corresponding node in the original tree
 * that can be mapped to it.
 * <P>
 * ============================================<BR>
 * How to write a {@link GenerationOperation}:<BR>
 * <OL>
 * <LI>First - usually you need to inherit from {@link GenerationOperationForExtendedNode}. Not directly from {@link GenerationOperation}</LI>
 * <LI>In the constructor - usually you will need to get more information than
 * just the text parse tree and the hypothesis parse tree. Add the relevant information
 * to the constructor. Usually you can just add a third parameter to the constructor
 * which is the appropriate {@link Specification}.</LI>
 * <LI>Implement the method {@link #generateTheTree()}. In the implementation
 * you also have to set values to {@link #generatedTree}, {@link #mapOriginalToGenerated} and {@link #affectedNodes}.</LI>
 * <LI>If you want, you can assign value to {@link #mapOriginalToGenerated} in the method
 * {@link #generateMapOriginalToGenerated()}. Otherwise, you have to set the value
 * to {@link #mapOriginalToGenerated} in the method {@link #generateTheTree()}, and
 * the implementation of {@link #generateMapOriginalToGenerated()} will be empty.</LI>
 * <LI>If you do not inherit from {@link GenerationOperationForExtendedNode}, then
 * you will have to implement {@link #postProcess()}. See the comment of this method.</LI>
 * </OL>
 * ============================================<BR>
 * <P>
 * TODO: The rest of this JavaDoc comment is vague. (you can skip it if you find
 * that you cannot understand it, though it is important comment).<BR>
 * The policy of {@link AdditionalNodeInformation} in {@link GenerationOperation} is:<BR>
 * Content ancestor: always reset.<BR>
 * Truth value: if from text - do not change. Else - set to default.<BR>
 * Trace: by mapping only.<BR>
 * SRL: Do not change, unless move node.<BR>
 * <P>
 * Since the "always reset", and "by mapping only" is done <U>after</U>
 * the operation is performed, the only concern is about truth-values and SRL.
 * <BR>
 * Now, note the following observation:<BR>
 * Always, everything, is either from the text, or has no {@link AdditionalNodeInformation} of itself.
 * The only exception is {@link InsertNodeOperation}.<BR>
 * Let's look at them all:<BR>
 * <UL>
 * <LI>{@link DuplicateAndMoveNodeOperation} - from text</LI>
 * <LI>{@link ExtendedSubstitutionRuleApplicationOperation} - has no {@link AdditionalNodeInformation}</LI>
 * <LI>{@link InsertNodeOperation} - this is the only exception.</LI>
 * <LI>{@link IntroductionRuleApplicationOperation} - has no {@link AdditionalNodeInformation}</LI>
 * <LI>{@link MoveNodeOperation} - from text</LI>
 * <LI>{@link SubstituteNodeOperation}: used by the following:
 * <UL>
 * <LI>lexical rules - have no {@link AdditionalNodeInformation}</LI>
 * <LI>change predicate truth - everything comes from text except the truth-value which is what has to be changed.</LI>
 * <LI>multi-word - the {@link Finder}, {@link SubstitutionMultiWordUnderlyingFinder}, returns the {@link AdditionalNodeInformation} of the text.</LI>
 * <LI>flip pos - from the text.</LI>
 * </UL>
 * <LI>{@link SubstituteSubtreeOperation} - either parser antecedent or coreference - come from text
 * </UL>
 * The only exception is {@link InsertNodeOperation}. But, since it indeed
 * should be a copy of an hypothesis node, then I copy it as is.
 * 
 * @see GenerationOperationForExtendedNode
 * 
 * @author Asher Stern
 * @since 2011
 * 
 * @param <I>	type of the text node info
 * @param <N>	type of the text node
 * 
 * 
 */
public abstract class GenerationOperation<I extends Info, N extends AbstractNode<I, N>>
{
	public GenerationOperation(TreeAndParentMap<I,N> textTree,
			TreeAndParentMap<I,N> hypothesisTree) throws OperationException
	{
		super();
		if (null==textTree) throw new OperationException("null textTree");
		if (null==hypothesisTree) throw new OperationException("null hypothesisTree");
		this.textTree = textTree;
		this.hypothesisTree = hypothesisTree;
	}
	
	/**
	 * Generates the new tree
	 * @throws OperationException
	 */
	public final void generate() throws OperationException
	{
		generateTheTree();
		generateMapOriginalToGenerated();
		postProcess();
	}
	
	
	/**
	 * Returns the generated tree, created by {@link #generate()}.
	 * @return
	 * @throws OperationException
	 */
	public N getGeneratedTree() throws OperationException
	{
		if (null==this.generatedTree) throw new OperationException("Not generated.");
		return generatedTree;
	}

	/**
	 * Returns a mapping from the original tree's nodes to the new tree's nodes. Not every node
	 * in the new tree has a corresponding node in the original tree that can be mapped to it.
	 * See the comment at the beginning of the class for more information about the mapping.
	 * 
	 * @return A mapping from the original tree's nodes to the new tree's nodes.
	 * @throws OperationException
	 */
	public ValueSetMap<N, N> getMapOriginalToGenerated() throws OperationException
	{
		if (null==this.mapOriginalToGenerated)
			throw new OperationException("Internal BUG: Generation of map was not done.");
		return this.mapOriginalToGenerated;
	}
	
	
	
	public Set<N> getAffectedNodes() throws OperationException
	{
		if (affectedNodes!=null)
		{
			if (affectedNodes.size()>0)
			{
				return affectedNodes;
			}
		}
		throw new OperationException(
				"The set of affected nodes is either not-exist or empty. This means that" +
				"there is a bug in the GenerationOperation.\n" +
				"Note that each GenerationOperation must create a non-empty set of parse-tree-nodes" +
				"that were affected (modified or added) by the GenerationOperation.\n" +
				"This set should be stored in the field \"affectedNodes\" of the GenerationOperation."
				);
	}
	
	/**
	 * Indicates whether this operation will undoubtedly not be part of the
	 * proof.<BR>
	 * For example - the operation did not change the tree.
	 * @return
	 */
	public boolean discardTheGeneratedTree() throws OperationException
	{
		return false;
	}

	/**
	 * Generates the tree. Should be overridden by subclasses.
	 * @throws OperationException
	 */
	protected abstract void generateTheTree() throws OperationException;
	
	/**
	 * Creates the mapping that will be returned by {@link #getMapOriginalToGenerated()}.
	 * <BR>
	 * The map should be stored in the field {@link #mapOriginalToGenerated}.
	 * <P>
	 * Note that if you create this map (and fill it) in the method {@link #generateTheTree()},
	 * then you can write an empty-implementation of this method.
	 * @throws OperationException
	 */
	protected abstract void generateMapOriginalToGenerated() throws OperationException;

	/**
	 * Performs post-processing on the generated tree. The implementation
	 * must be like the implementation in
	 * {@link GenerationOperationForExtendedNode#postProcess()}
	 * 
	 * @see GenerationOperationForExtendedNode#postProcess()
	 * @throws OperationException
	 */
	protected abstract void postProcess() throws OperationException;
	
	
	protected TreeAndParentMap<I,N> textTree;
	protected TreeAndParentMap<I,N> hypothesisTree;
	
	protected N generatedTree = null;
	protected ValueSetMap<N, N> mapOriginalToGenerated = null;
	protected Set<N> affectedNodes = null;
	
}
