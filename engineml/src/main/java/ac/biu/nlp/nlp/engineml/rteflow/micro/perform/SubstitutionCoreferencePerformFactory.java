package ac.biu.nlp.nlp.engineml.rteflow.micro.perform;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.finders.Finder;
import ac.biu.nlp.nlp.engineml.operations.finders.SubstitutionCoreferenceFinder;
import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.SubstituteSubtreeOperation;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseEnvelope;
import ac.biu.nlp.nlp.engineml.operations.specifications.SubstitutionSubtreeSpecification;
import ac.biu.nlp.nlp.engineml.operations.updater.FeatureVectorUpdater;
import ac.biu.nlp.nlp.engineml.operations.updater.UpdaterForSubstitutionCoreference;
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
 * @since Jan 25, 2012
 *
 */
public class SubstitutionCoreferencePerformFactory extends PerformFactory<SubstitutionSubtreeSpecification>
{
	public SubstitutionCoreferencePerformFactory(TreeCoreferenceInformation<ExtendedNode> coreferenceInformation)
	{
		this.coreferenceInformation = coreferenceInformation;
	}

	@Override
	public Finder<SubstitutionSubtreeSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return new SubstitutionCoreferenceFinder(text,this.coreferenceInformation);
	}

	@Override
	public  Finder<SubstitutionSubtreeSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			ByLemmaPosLexicalRuleBase<LexicalRule> lexicalRuleBase, String ruleBaseName)
			throws TeEngineMlException, OperationException
	{
		return null;
	}
	
	@Override
	public Finder<SubstitutionSubtreeSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			String ruleBaseName) throws TeEngineMlException, OperationException
	{
		return null;
	}


	@Override
	public Finder<SubstitutionSubtreeSpecification> getFinder(
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
			SubstitutionSubtreeSpecification specification)
			throws TeEngineMlException, OperationException
	{
		return new SubstituteSubtreeOperation(text, hypothesis, specification.getSubtreeToRemove(), specification.getSubtreeToAdd(),null);
	}

	@Override
	public FeatureVectorUpdater<SubstitutionSubtreeSpecification> getUpdater(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return updaterForSubstitutionCoreference;
	}
	
	private TreeCoreferenceInformation<ExtendedNode> coreferenceInformation;

	private static UpdaterForSubstitutionCoreference updaterForSubstitutionCoreference = new UpdaterForSubstitutionCoreference();
}
