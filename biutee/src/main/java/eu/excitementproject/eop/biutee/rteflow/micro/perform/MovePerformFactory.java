package eu.excitementproject.eop.biutee.rteflow.micro.perform;
import eu.excitementproject.eop.biutee.operations.updater.FeatureVectorUpdater;
import eu.excitementproject.eop.biutee.operations.updater.UpdaterForMove;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.Finder;
import eu.excitementproject.eop.transformations.operations.finders.MoveNodeOperationFinder;
import eu.excitementproject.eop.transformations.operations.operations.DuplicateAndMoveNodeOperation;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.operations.MoveNodeOperation;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseEnvelope;
import eu.excitementproject.eop.transformations.operations.specifications.MoveNodeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.PathFinder;


/**
 * 
 * @author Asher Stern
 * @since 2012
 *
 */
public class MovePerformFactory extends PerformFactory<MoveNodeSpecification>
{
	public MovePerformFactory(
			AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria)
	{
		super();
		this.alignmentCriteria = alignmentCriteria;
	}

	@Override
	public Finder<MoveNodeSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return new MoveNodeOperationFinder(text, hypothesis,alignmentCriteria);
	}

	@Override
	public Finder<MoveNodeSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			ByLemmaPosLexicalRuleBase<LexicalRule> lexicalRuleBase, String ruleBaseName)
			throws TeEngineMlException, OperationException
	{
		return null;
	}
	
	@Override
	public Finder<MoveNodeSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			String ruleBaseName) throws TeEngineMlException, OperationException
	{
		return null;
	}


	@Override
	public Finder<MoveNodeSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			RuleBaseEnvelope<Info, BasicNode> ruleBase, String ruleBaseName)
			throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public GenerationOperation<ExtendedInfo, ExtendedNode> getOperation(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			MoveNodeSpecification specification) throws TeEngineMlException,
			OperationException
	{
		if (!specification.isDuplicate())
		{
			return new MoveNodeOperation(text, hypothesis, specification.getTextNodeToMove(), specification.getTextNodeToBeParent(), specification.getNewEdgeInfo());
		}
		else
		{
			return new DuplicateAndMoveNodeOperation(text, hypothesis, specification.getTextNodeToMove(), specification.getTextNodeToBeParent(), specification.getNewEdgeInfo());
		}
	}

	@Override
	public FeatureVectorUpdater<MoveNodeSpecification> getUpdater(TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		if (this.text!=text)
		{
			this.pathFinder = new PathFinder(text);
			this.text = text;
		}
		return new UpdaterForMove(this.pathFinder);
	}

	private AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
	
	private PathFinder pathFinder = null;
	private TreeAndParentMap<ExtendedInfo, ExtendedNode> text = null;

}
