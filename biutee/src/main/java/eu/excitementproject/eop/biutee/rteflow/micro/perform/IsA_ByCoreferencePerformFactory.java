package eu.excitementproject.eop.biutee.rteflow.micro.perform;
import eu.excitementproject.eop.biutee.operations.updater.FeatureVectorUpdater;
import eu.excitementproject.eop.biutee.operations.updater.UpdaterForIsAByCoreference;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.Finder;
import eu.excitementproject.eop.transformations.operations.finders.IsA_ByCoreferenceFinder;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.operations.IsAConstructionOperation;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseEnvelope;
import eu.excitementproject.eop.transformations.operations.specifications.IsASpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Sep 9, 2012
 *
 */
public class IsA_ByCoreferencePerformFactory extends PerformFactory<IsASpecification>
{
	public IsA_ByCoreferencePerformFactory(
			TreeCoreferenceInformation<ExtendedNode> coreferenceExtendedInformation,
			PARSER parser, boolean collapseMode)
	{
		super();
		this.coreferenceExtendedInformation = coreferenceExtendedInformation;
		this.parser = parser;
		this.collapseMode = collapseMode;
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
		return new IsAConstructionOperation(text, hypothesis, specification, parser, collapseMode);
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
	private final PARSER parser;
	private final boolean collapseMode;
	
	private static UpdaterForIsAByCoreference updaterForIsAByCoreference = new UpdaterForIsAByCoreference();
}
