package ac.biu.nlp.nlp.engineml.rteflow.micro.perform;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.finders.Finder;
import ac.biu.nlp.nlp.engineml.operations.finders.IsA_ByCoreferenceFinder;
import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.IsAConstructionOperation;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseEnvelope;
import ac.biu.nlp.nlp.engineml.operations.specifications.IsASpecification;
import ac.biu.nlp.nlp.engineml.operations.updater.FeatureVectorUpdater;
import ac.biu.nlp.nlp.engineml.operations.updater.UpdaterForIsAByCoreference;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * 
 * @author Asher Stern
 * @since Sep 9, 2012
 *
 */
public class IsA_ByCoreferencePerformFactory extends PerformFactory<IsASpecification>
{
	public IsA_ByCoreferencePerformFactory(
			TreeCoreferenceInformation<ExtendedNode> coreferenceExtendedInformation)
	{
		super();
		this.coreferenceExtendedInformation = coreferenceExtendedInformation;
	}

	@Override
	public Finder<IsASpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return new IsA_ByCoreferenceFinder(this.coreferenceExtendedInformation, text);
	}

	@Override
	public Finder<IsASpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			ByLemmaPosLexicalRuleBase<LexicalRule> lexicalRuleBase,
			String ruleBaseName) throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public Finder<IsASpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			String ruleBaseName) throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public Finder<IsASpecification> getFinder(
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
			IsASpecification specification) throws TeEngineMlException,
			OperationException
	{
		return new IsAConstructionOperation(text, hypothesis, specification);
	}

	@Override
	public FeatureVectorUpdater<IsASpecification> getUpdater(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return updaterForIsAByCoreference;
	}

	private TreeCoreferenceInformation<ExtendedNode> coreferenceExtendedInformation;
	
	private static UpdaterForIsAByCoreference updaterForIsAByCoreference = new UpdaterForIsAByCoreference();
}
