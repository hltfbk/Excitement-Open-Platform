package ac.biu.nlp.nlp.engineml.rteflow.micro.perform;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.engineml.alignment.AlignmentCriteria;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.finders.Finder;
import ac.biu.nlp.nlp.engineml.operations.finders.MoveNodeOperationFinder;
import ac.biu.nlp.nlp.engineml.operations.operations.DuplicateAndMoveNodeOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.MoveNodeOperation;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseEnvelope;
import ac.biu.nlp.nlp.engineml.operations.specifications.MoveNodeSpecification;
import ac.biu.nlp.nlp.engineml.operations.updater.FeatureVectorUpdater;
import ac.biu.nlp.nlp.engineml.operations.updater.UpdaterForMove;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.PathFinder;


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
