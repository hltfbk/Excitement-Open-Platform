package ac.biu.nlp.nlp.engineml.rteflow.micro;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.alignment.AlignmentCriteria;
import ac.biu.nlp.nlp.engineml.datastructures.CanonicalLemmaAndPos;
import ac.biu.nlp.nlp.engineml.datastructures.LemmaAndPos;
import ac.biu.nlp.nlp.engineml.operations.finders.Finder;
import ac.biu.nlp.nlp.engineml.operations.finders.SubstitutionMultiWordUnderlyingFinder;
import ac.biu.nlp.nlp.engineml.operations.rules.BagOfRulesRuleBase;
import ac.biu.nlp.nlp.engineml.operations.specifications.Specification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.FeatureUpdate;
import ac.biu.nlp.nlp.engineml.rteflow.macro.InitializationTextTreesProcessor;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeHistory;
import ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

/**
 * A collection of several objects required to generate new trees from
 * a given text tree.
 * This class is used by {@link TreesGeneratorByOperations}.
 * 
 * @see InitializationTextTreesProcessor
 * 
 * @author Asher Stern
 * @since Aug 19, 2011
 *
 */
public class OperationsEnvironment
{
	/**
	 * Constructor with all fields
	 * 
	 * @param featureUpdate Used to update the feature vector assigned to
	 * a given tree.
	 * @param hypothesis The hypothesis tree.
	 * @param hypothesisLemmas The hypothesis lemmas with part-of-speech.
	 * @param hypothesisLemmasOnly The hypothesis lemmas
	 * @param substitutionMultiWordFinder A {@link Finder} that finds
	 * operations of multi-word to single-word that can be applied.
	 * This finder requires a long-time initialization, thus it should
	 * be constructed only once for a given text-hypothesis pair. Thus,
	 * this is the only {@linkplain Finder} that exists in {@link OperationsEnvironment}
	 * @param lemmatizer Lemmatizer
	 * @param coreferenceInformation A coreference-information, given as equivalence
	 * classes of tree-nodes.
	 * @param mapLexicalMultiWord A map from rule-base-name to a rule base
	 * that contains rules that their right-hand-side is a multi-word-expression,
	 * that exists in the hypothesis tree as several nodes. Those rule
	 * bases are actually lexical rule bases, but since the right-hand-side
	 * is a multi-word, and it cannot be found in the hypothesis tree as
	 * a node with the right-hand-side lexical, but only as several
	 * connected nodes, thus an appropriate rule base is created for
	 * those rules.
	 * See also {@link ConfigurationParametersNames#LEXICAL_RESOURCES_RETRIEVE_MULTIWORDS_PARAMETER_NAME} 
	 * @param hypothesisTemplates A set of strings in the form of DIRT
	 * template. The set contains all templates that can be extracted
	 * from the hypothesis.
	 * This set is helpful in the process of finding lexical-syntactic rules.
	 * @param multiWordNamedEntityRuleBase This is a rule base, that is
	 * actually not a real rule base. It captures substitutions of
	 * single-word to multi-word for named-entities. The features for
	 * applying "rules" from this rule base are the features of multi-word
	 * to single-word or single-word to multi-word.
	 * @param richInformationInTreeHistory if <tt>true<tt> the {@link TreeHistory}
	 * objects that will be built will contain rich information, not only
	 * the {@link Specification} of the operation. This should be set to
	 * <tt>true</tt> for GUI, but not for other systems.
	 */
	public OperationsEnvironment(
			FeatureUpdate featureUpdate,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			ImmutableSet<LemmaAndPos> hypothesisLemmas,
			ImmutableSet<CanonicalLemmaAndPos> hypothesisLemmasAndCanonicalPos,
			ImmutableSet<String> hypothesisLemmasOnly,
			Set<String> hypothesisLemmasLowerCase,
			int hypothesisNumberOfNodes,
			SubstitutionMultiWordUnderlyingFinder substitutionMultiWordFinder,
			Lemmatizer lemmatizer,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			Map<String, BagOfRulesRuleBase<Info, BasicNode>> mapLexicalMultiWord,
			ImmutableSet<String> hypothesisTemplates,
			BagOfRulesRuleBase<Info, BasicNode> multiWordNamedEntityRuleBase,
			boolean richInformationInTreeHistory,
			AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria,
			ImmutableSet<String> stopWords)
	{
		super();
		this.featureUpdate = featureUpdate;
		this.hypothesis = hypothesis;
		this.hypothesisLemmas = hypothesisLemmas;
		this.hypothesisLemmasAndCanonicalPos = hypothesisLemmasAndCanonicalPos;
		this.hypothesisLemmasOnly = hypothesisLemmasOnly;
		this.hypothesisLemmasLowerCase = hypothesisLemmasLowerCase;
		this.hypothesisNumberOfNodes = hypothesisNumberOfNodes;
		this.substitutionMultiWordFinder = substitutionMultiWordFinder;
		this.lemmatizer = lemmatizer;
		this.coreferenceInformation = coreferenceInformation;
		this.mapLexicalMultiWord = mapLexicalMultiWord;
		this.hypothesisTemplates = hypothesisTemplates;
		this.multiWordNamedEntityRuleBase = multiWordNamedEntityRuleBase;
		this.richInformationInTreeHistory = richInformationInTreeHistory;
		this.alignmentCriteria = alignmentCriteria;
		this.stopWords = stopWords;
	}

	
	
	
	public FeatureUpdate getFeatureUpdate()
	{
		return featureUpdate;
	}
	public TreeAndParentMap<ExtendedInfo, ExtendedNode> getHypothesis()
	{
		return hypothesis;
	}
	public ImmutableSet<LemmaAndPos> getHypothesisLemmas()
	{
		return hypothesisLemmas;
	}
	public ImmutableSet<CanonicalLemmaAndPos> getHypothesisLemmasAndCanonicalPos()
	{
		return hypothesisLemmasAndCanonicalPos;
	}
	public ImmutableSet<String> getHypothesisLemmasOnly()
	{
		return hypothesisLemmasOnly;
	}
	public SubstitutionMultiWordUnderlyingFinder getSubstitutionMultiWordFinder()
	{
		return substitutionMultiWordFinder;
	}
	public Lemmatizer getLemmatizer()
	{
		return lemmatizer;
	}
	public TreeCoreferenceInformation<ExtendedNode> getCoreferenceInformation()
	{
		return coreferenceInformation;
	}
	public Map<String, BagOfRulesRuleBase<Info, BasicNode>> getMapLexicalMultiWord()
	{
		return mapLexicalMultiWord;
	}
	public ImmutableSet<String> getHypothesisTemplates()
	{
		return hypothesisTemplates;
	}
	public BagOfRulesRuleBase<Info, BasicNode> getMultiWordNamedEntityRuleBase()
	{
		return multiWordNamedEntityRuleBase;
	}
	public Set<String> getHypothesisLemmasLowerCase()
	{
		return hypothesisLemmasLowerCase;
	}
	public int getHypothesisNumberOfNodes()
	{
		return hypothesisNumberOfNodes;
	}
	public boolean isRichInformationInTreeHistory()
	{
		return richInformationInTreeHistory;
	}
	public AlignmentCriteria<ExtendedInfo, ExtendedNode> getAlignmentCriteria()
	{
		return alignmentCriteria;
	}
	public ImmutableSet<String> getStopWords()
	{
		return stopWords;
	}










	private final FeatureUpdate featureUpdate;
	private final TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis;
	private final ImmutableSet<LemmaAndPos> hypothesisLemmas;
	private final ImmutableSet<CanonicalLemmaAndPos> hypothesisLemmasAndCanonicalPos;
	private final ImmutableSet<String> hypothesisLemmasOnly;
	private final Set<String> hypothesisLemmasLowerCase;
	private final int hypothesisNumberOfNodes;
	private final SubstitutionMultiWordUnderlyingFinder substitutionMultiWordFinder;
	private final Lemmatizer lemmatizer;
	private final TreeCoreferenceInformation<ExtendedNode> coreferenceInformation;
	private final Map<String,BagOfRulesRuleBase<Info, BasicNode>> mapLexicalMultiWord;
	private final ImmutableSet<String> hypothesisTemplates;
	private final BagOfRulesRuleBase<Info, BasicNode> multiWordNamedEntityRuleBase;
	private final boolean richInformationInTreeHistory;
	private final AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
	private final ImmutableSet<String> stopWords;
}
