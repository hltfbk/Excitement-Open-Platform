/**
 * 
 */
package eu.excitementproject.eop.transformations.operations.operations;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.utilities.InfoServices;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.rules.ExtendedInfoServices;

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
			SyntacticRule<Info, BasicNode> rule, BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree) throws OperationException {
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
