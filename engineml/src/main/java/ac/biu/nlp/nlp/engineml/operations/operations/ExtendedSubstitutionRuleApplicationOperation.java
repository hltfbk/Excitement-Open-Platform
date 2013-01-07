/**
 * 
 */
package ac.biu.nlp.nlp.engineml.operations.operations;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.Rule;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNodeConstructor;
import ac.biu.nlp.nlp.engineml.utilities.InfoServices;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.rules.ExtendedInfoServices;
import ac.biu.nlp.nlp.general.BidirectionalMap;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeConstructor;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * This class performs the regular substitution rule application, where the rule uses {@link BasicNode} and {@link Info}, while the text tree uses 
 * {@link ExtendedNode} and {@link ExtendedInfo}. It replaces the left-hand-side instance in the given tree by the instantiation of the right hand side.
 *  
 * @author Amnon Lotan
 * @since 19/06/2011
 * 
 */
public class ExtendedSubstitutionRuleApplicationOperation extends 
	SubstitutionRuleApplicationOperation<ExtendedInfo, ExtendedNode, Info, BasicNode> {

	/**
	 * Ctor
	 * @param textTree
	 * @param hypothesisTree
	 * @param rule
	 * @param mapLhsToTree
	 * @throws OperationException
	 */
	public ExtendedSubstitutionRuleApplicationOperation(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree, TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			Rule<Info, BasicNode> rule, BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree) throws OperationException {
		super(textTree, hypothesisTree, rule, mapLhsToTree);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.operations.operations.SubstitutionRuleApplicationOperation#getNewNodeConstructor()
	 */
	@Override
	protected AbstractNodeConstructor<ExtendedInfo, ExtendedNode> getNewNodeConstructor() {
		return new ExtendedNodeConstructor();
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.operations.operations.SubstitutionRuleApplicationOperation#getNewInfoServices()
	 */
	@Override
	protected InfoServices<ExtendedInfo, Info> getNewInfoServices() {
		return new ExtendedInfoServices();
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
