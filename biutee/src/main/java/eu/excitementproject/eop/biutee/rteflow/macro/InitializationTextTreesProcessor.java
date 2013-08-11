package eu.excitementproject.eop.biutee.rteflow.macro;

import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.CACHE_SIZE_HYPOTHESIS_TEMPLATES;
import static eu.excitementproject.eop.transformations.utilities.Constants.HANDLE_LEXICAL_MULTI_WORD;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.plugin.InstanceBasedPlugin;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.plugin.PluginException;
import eu.excitementproject.eop.biutee.rteflow.document_sublayer.DocumentInitializer;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapEnvironment;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolInstances;
import eu.excitementproject.eop.biutee.rteflow.macro.multiword_namedentity_utils.MultiWordNamedEntityUtils;
import eu.excitementproject.eop.biutee.rteflow.micro.OperationsEnvironment;
import eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.Cache;
import eu.excitementproject.eop.common.utilities.CacheFactory;
import eu.excitementproject.eop.common.utilities.SealedObject;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.datastructures.CanonicalLemmaAndPos;
import eu.excitementproject.eop.transformations.datastructures.DsUtils;
import eu.excitementproject.eop.transformations.datastructures.LemmaAndPos;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.SubstitutionMultiWordUnderlyingFinder;
import eu.excitementproject.eop.transformations.operations.rules.BagOfRulesRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseWithNamedEntities;
import eu.excitementproject.eop.transformations.operations.rules.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.transformations.operations.rules.SetBagOfRulesRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.distsimnew.TemplatesFromTree;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;
import eu.excitementproject.eop.transformations.operations.rules.lexicalmw_utils.MultiWordRuleBaseCreator;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.InfoObservations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.AdvancedEqualities;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.ContentAncestorSetter;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.SelfTraceSetter;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;


/**
 * 
 * This class is used to support an implementation of {@link TextTreesProcessor}.
 * The task of {@link TextTreesProcessor} is to find a proof of the hypothesis by a list
 * of text-trees. In most cases this requires an initialization phase, which is common
 * to many strategies of finding-the-best-proof.
 * This class implements those initializations.
 * <P>
 * A common way to use this class is by using {@link AbstractTextTreesProcessor}, which
 * calls {@link #init()} before starting to process (to find the proof).
 * <P>
 * Note that some initialization is performed in {@link DocumentInitializer}.
 * 
 * @see TextTreesProcessor
 * @see AbstractTextTreesProcessor
 * @see DocumentInitializer
 * 
 * @author Asher Stern
 * @since May 25, 2011
 *
 */
public class InitializationTextTreesProcessor
{
	public InitializationTextTreesProcessor(String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees,
			ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier,
			Lemmatizer lemmatizer, OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment
			) throws TeEngineMlException
	{
		super();
		if (null==textText) throw new TeEngineMlException("Null textText");
		if (null==hypothesisText) throw new TeEngineMlException("Null hypothesisText");
		if (null==originalTextTrees) throw new TeEngineMlException("Null originalTextTrees");
		if (null==hypothesisTree) throw new TeEngineMlException("Null hypothesisTree");
		if (null==originalMapTreesToSentences) throw new TeEngineMlException("Null originalMapTreesToSentences");
		if (null==coreferenceInformation) throw new TeEngineMlException("Null coreferenceInformation");
		if (null==classifier) throw new TeEngineMlException("Null classifier");
		if (null==script) throw new TeEngineMlException("Null script");
		if (null==teSystemEnvironment) throw new TeEngineMlException("Null teSystemEnvironment");
		
		this.textText = textText;
		this.hypothesisText = hypothesisText;
		this.originalTextTrees = originalTextTrees;
		this.hypothesisTree = hypothesisTree;
		this.originalMapTreesToSentences = originalMapTreesToSentences;
		this.coreferenceInformation = coreferenceInformation;
		this.classifier = classifier;
		this.lemmatizer = lemmatizer;
		this.script = script;
		this.teSystemEnvironment = teSystemEnvironment;
		this.hybridGapMode = this.teSystemEnvironment.getGapToolBox().isHybridMode();
	}
	
	/**
	 * The surroundings context is required for some features' values that depend
	 * on whether a certain operation involves words that do not exist in the text at all,
	 * or words that do exist in the text, but may not in the current sentence, or
	 * no in the required location. It is used by {@link FeatureUpdate}.
	 * <P>
	 * If not set explicitly, than the assumption is that the surrounding context of
	 * the current sentence is all the text (this is a reasonable choice for regular
	 * T-H pairs. The context is all T).
	 * <BR>
	 * In data sets like RTE-SUM (rte6 main and above), not the whole document should be
	 * treat as "surrounding context". Another decision should be made. A reasonable
	 * decision in this case is the immediately preceding sentence, and the title. 
	 * 
	 *  
	 * @param surroundingsContext
	 * @throws TeEngineMlException
	 */
	public void setSurroundingsContext(List<ExtendedNode> surroundingsContext) throws TeEngineMlException
	{
		if (null==surroundingsContext) throw new TeEngineMlException("Null surroundingsContext");
		if (!surroundingsContext.containsAll(originalTextTrees))
			throw new TeEngineMlException("Caller\'s bug! The list of surroundings-context must contain all of the text trees.");
		this.surroundingsContext = surroundingsContext;
	}
	
	public void setGlobalPairInformation(GlobalPairInformation information)
	{
		this.globalPairInformation = information;
	}
	
	public void setRichInformationInTreeHistory(boolean richInformationInTreeHistory)
	{
		this.richInformationInTreeHistory = richInformationInTreeHistory;
	}

	/**
	 * Performs initializations required for finding the proof.
	 * This method is the main purpose of this class.
	 * 
	 * @throws TeEngineMlException
	 * @throws OperationException
	 * @throws TreeAndParentMapException
	 * @throws AnnotatorException 
	 */
	protected void init() throws TeEngineMlException, OperationException, TreeAndParentMapException, AnnotatorException
	{
		// the object sealedOriginalHypothesisTree is used as a key in the cache "cacheHypothesisToTemplates" (a static member in this class)
		sealedOriginalHypothesisTree = new SealedObject<ExtendedNode>(this.hypothesisTree);
		
		// Start with a small verification
		verifyTreesHaveArtificialRoot();
		
		// Now - start creating new trees based on the given ones.
		
		// The map "mapOriginalToInitialized" will hold a mapping of the nodes
		// from the nodes given in the constructor (the originalTextTrees), to the
		// new equivalent trees that are created during initialization.
		
		// !!!! UNCOMMENT IF YOU WANT THIS MAPPING !!!!
		// mapOriginalToInitialized = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
		
		if (mapOriginalToInitialized!=null)
			createMapToItselfForAllTrees();
		
		// sets "OriginalInfoTrace" - a field in AdditionalNodeInformation
		initTraces();
		
		// annotateTextAndHypothesis(); // THIS IS DONE IN THE DOCUMENT SUBLAYER!
		
		// sets "Content Ancestor" - a field in AdditionalNodeInformation
		refineTextAndHypothesis();
		
		// Copies the hypothesis parse tree as an BasicNode
		generateHypothesisBasicNode();
		
		
		
		// Creates a set with all the lemmas in the pair
		if (null==this.surroundingsContext)
		{
			this.wholeTextLemmas = wordsInPair(this.originalTextTrees);
		}
		else
		{
			this.wholeTextLemmas = wordsInPair(this.surroundingsContext);
		}
		if (logger.isDebugEnabled())
		{
			StringBuffer sb = new StringBuffer();
			boolean firstIteration = true;
			for (String word : wholeTextLemmas)
			{
				if (firstIteration) firstIteration=false; else sb.append(", ");
				sb.append(word);
			}
			logger.debug("The following words are in wholeTextLemmas:\n"+sb.toString());
		}
		
		
		// create the hypothesis "TreeAndParentMap<ExtendedInfo,ExtendedNode>"
		hypothesis = new TreeAndParentMap<ExtendedInfo,ExtendedNode>(this.hypothesisTree);
		
		// number of hypothesis nodes, to be used by initialFeatureVector()
		hypothesisNumberOfNodes = AbstractNodeUtils.treeToLinkedHashSet(this.hypothesisTree).size();
		
		// Creates a set of "LemmaAndPos" of all hypothesis tree's nodes
		// and set of lemmas (with out pos)
		this.hypothesisLemmas = new ImmutableSetWrapper<LemmaAndPos>(lemmasAndPosesInTree(this.hypothesisTree));
		ImmutableSet<CanonicalLemmaAndPos> hypothesisLemmasAndCanonicalPos = convertToCanonicalSet(hypothesisLemmas);
		this.hypothesisLemmasOnly = new ImmutableSetWrapper<String>(extractOnlyLemmas(this.hypothesisLemmas));
		this.hypothesisLemmasLowerCase = TreeUtilities.constructSetLemmasLowerCase(hypothesis);
		
		logger.info("creating hypothesis templates for DIRT-like resources...");
		hypothesisTemplates = getHypothesisTemplates();
		logger.info("creating hypothesis templates for DIRT-like resources done.");
		
		
		// Initialize this finder with the hypothesis
		this.substitutionMultiWordFinder = new SubstitutionMultiWordUnderlyingFinder(hypothesis);
		
		// Creating map from rule-base-name to RuleBase,
		// such that the rule base contains the lexical rules that their right-hand-side is multiword
		// and thus should be handled as a Rule, not LexicalRule.
		// SEE THE COMMENT OF THE FUNCTION createMapLexicalMultiWord()
		if (HANDLE_LEXICAL_MULTI_WORD)
		{
			logger.info("creating mapRuleBasesForLexicalMultiWord...");
			createMapLexicalMultiWord();
			logger.info("creating mapRuleBasesForLexicalMultiWord - done.");
		}
		else
		{
			logger.info("Not handling lexical multi word rules! HANDLE_LEXICAL_MULTI_WORD=false");
		}
		
		// Create a FeatureUpdate object.
		this.featureUpdate = new FeatureUpdate(this.wholeTextLemmas, teSystemEnvironment.getFeatureVectorStructureOrganizer(),teSystemEnvironment.getMleEstimation(), teSystemEnvironment.getParser());
		
		/// Creates a mechanism for handling named-entities that are represented by
		// several nodes in the parse tree (e.g. "Microsoft Corporation" is represented
		// by two nodes). This mechanism will be used by the on-the-fly transformation
		// of "substitute multi-word"
		if (Constants.HANDLE_MULTI_WORD_NAMED_ENTITIES)
		{
			logger.debug("Trying to create a rule base for multi-word named entities...");
			createMultiWordNamedEntityRuleBase();
			logger.debug("Done.");
		}
		
		// An InstanceBasedPlugin requires some calculation for each given T-H pair.
		try
		{
			for (InstanceBasedPlugin plugin : script.getInstanceBasedPlugins())
			{
				plugin.initForInstance(textText, hypothesisText, originalTextTrees, hypothesisTree, new ImmutableMapWrapper<ExtendedNode, String>(originalMapTreesToSentences), coreferenceInformation);
			}
		}
		catch(PluginException e)
		{
			throw new TeEngineMlException("Plugin initialization failed",e);
		}
		
		// Stores the parse-trees that were created during the initialization,
		// which will be used in the proof-construction. 
		originalTreesAfterInitialization = new OriginalTreesAfterInitialization(originalTextTrees, hypothesisTree, originalMapTreesToSentences,this.coreferenceInformation);

		if (teSystemEnvironment.getGapToolBox().isHybridMode())
		{
			gapTools = teSystemEnvironment.getGapToolBox().getGapToolsFactory().createInstances(hypothesis,this.classifier);
			gapEnvironment = new GapEnvironment<>();
		}
		else
		{
			gapTools = null;
			gapEnvironment = null;
		}
		
		
		// Stores many objects that were created during initialization.
		operationsEnvironment = new OperationsEnvironment(this.featureUpdate,this.hypothesis,this.hypothesisLemmas,hypothesisLemmasAndCanonicalPos,this.hypothesisLemmasOnly,this.hypothesisLemmasLowerCase,this.hypothesisNumberOfNodes,this.substitutionMultiWordFinder,this.lemmatizer,this.coreferenceInformation,this.mapRuleBasesForLexicalMultiWord,this.hypothesisTemplates, this.multiWordNamedEntityRuleBase, this.richInformationInTreeHistory, teSystemEnvironment.getAlignmentCriteria(),teSystemEnvironment.getStopWords(),teSystemEnvironment.getParser());
	}
	
	public OriginalTreesAfterInitialization getOriginalTreesAfterInitialization()
	{
		return originalTreesAfterInitialization;
	}

	protected void cleanUp()
	{
	}
	
	private ImmutableSet<String> getHypothesisTemplates() throws TeEngineMlException
	{
		ImmutableSet<String> ret = null;
		boolean foundInCache = false;
		if (cacheHypothesisToTemplates.containsKey(sealedOriginalHypothesisTree))
		{
			synchronized (cacheHypothesisToTemplates)
			{
				if (cacheHypothesisToTemplates.containsKey(sealedOriginalHypothesisTree)) // double-check
				{
					ret = cacheHypothesisToTemplates.get(sealedOriginalHypothesisTree);
					foundInCache = true;
					logger.debug("hypothesis templates found in cache");
				}
			}
		}
		if (!foundInCache)
		{
			TemplatesFromTree<ExtendedInfo, ExtendedNode> hypothesisTemplatesFromTree = new TemplatesFromTree<ExtendedInfo, ExtendedNode>(this.hypothesisTree);
			hypothesisTemplatesFromTree.createTemplate();
			ret = new ImmutableSetWrapper<String>(hypothesisTemplatesFromTree.getTemplates());
			synchronized(cacheHypothesisToTemplates)
			{
				cacheHypothesisToTemplates.put(sealedOriginalHypothesisTree, ret);
			}
		}
		if (logger.isDebugEnabled())
		{
			StringBuffer sb = new StringBuffer();
			sb.append("Number of hypothesis templates = "+ret.size());
			sb.append("\n");
			for (String template : ret)
			{
				sb.append(template);
				sb.append("\n");
			}
			logger.debug(sb.toString());
		}
		return ret;
	}
	
	private ImmutableSet<CanonicalLemmaAndPos> convertToCanonicalSet(ImmutableSet<LemmaAndPos> setLemmaAndPos) throws TeEngineMlException
	{
		Set<CanonicalLemmaAndPos> set = new LinkedHashSet<CanonicalLemmaAndPos>();
		for (LemmaAndPos lemmaAndPos : setLemmaAndPos)
		{
			set.add(
					new CanonicalLemmaAndPos(lemmaAndPos.getLemma(),lemmaAndPos.getPartOfSpeech())
					);
		}
		return new ImmutableSetWrapper<CanonicalLemmaAndPos>(set);
	}
	
	
	protected void generateHypothesisBasicNode()
	{
		TreeCopier<ExtendedInfo, ExtendedNode, Info, BasicNode> treeCopier =
			new TreeCopier<ExtendedInfo, ExtendedNode, Info, BasicNode>(
					hypothesisTree,
					new TreeCopier.InfoConverter<ExtendedNode, Info>()
					{
						public Info convert(ExtendedNode oi)
						{
							return oi.getInfo();
						}
					},
					new BasicNodeConstructor()
					);
		treeCopier.copy();
		hypothesisTreeAsBasicNode = treeCopier.getGeneratedTree();
	}
	
	
	/**
	 * The problem with many lexical rules is that many lexical rules have right hand side
	 * which is a multi word (e.g. "united states"). Those rules, when applied as lexical
	 * rules, will result in a node with a multi-word lemma (e.g. one node that its lemma is
	 * "united states").
	 * <BR>
	 * The problem here is that in the hypothesis - such a node cannot exist.
	 * The parser puts every word in its own node. There are no nodes with multi-word lemma
	 * (that's the case in EasyFirst. In Minipar many times there are multi words as lemma of 
	 * a single node, but not always).
	 * <BR>
	 * This function handles this problem. It creates a rule-base - a rule base that its rules'
	 * right hand sides are subtrees with nodes, such that each node has indeed only a single
	 * word lemma. Those rule-bases are stored in <code>mapRuleBasesForLexicalMultiWord</code>.
	 * Later, in the class {@link TreesGeneratorByOperations}, those rule bases will be used.
	 * 
	 * @see TreesGeneratorByOperations
	 * 
	 * @throws TreeAndParentMapException
	 * @throws TeEngineMlException
	 * @throws OperationException
	 */
	protected void createMapLexicalMultiWord() throws TreeAndParentMapException, TeEngineMlException, OperationException
	{
		mapRuleBasesForLexicalMultiWord =
			new LinkedHashMap<String, BagOfRulesRuleBase<Info,BasicNode>>(); 
		
		for (String ruleBaseName : teSystemEnvironment.getRuleBasesToRetrieveMultiWords())
		{
			if (logger.isDebugEnabled())
				logger.debug("Working on rule base: "+ruleBaseName);
			
			// TODO handle this exceptions in a better way.
			// Should not use exception mechanism here at all.
			boolean regularRuleBaseHasBeenFound = false;
			try
			{
				ByLemmaPosLexicalRuleBase<LexicalRule> ruleBase = 
					script.getByLemmaPosLexicalRuleBase(ruleBaseName);
				regularRuleBaseHasBeenFound=true;
				logger.debug("It is a regular lexical rule base");
				if (ruleBase instanceof RuleBaseWithNamedEntities)
				{
					throw new TeEngineMlException(
							"Rule bases which rely on named-entities should not be handled by this multi-word mechanism. " +
							"Please remove rule base \""+ruleBaseName+"\" from the list of multi-word lexical resources in your configuration.");
				}

				Set<RuleWithConfidenceAndDescription<Info, BasicNode>> setOfDetectedRules = createSetOfRulesForMultiWord(ruleBaseName,ruleBase, hypothesisTreeAsBasicNode);
				if (logger.isDebugEnabled()){logger.debug("Number of MW rules added for resource "+ruleBaseName+" is "+setOfDetectedRules.size());}
				
				SetBagOfRulesRuleBase<Info,BasicNode> createdRuleBase =
					SetBagOfRulesRuleBase.fromSetWithConfidenceAndDescription(setOfDetectedRules);
				
				mapRuleBasesForLexicalMultiWord.put(ruleBaseName, createdRuleBase);
			}
			catch(OperationException x)
			{
				if (regularRuleBaseHasBeenFound) throw x;
				logger.debug("Seems that it is a chain of lexical rules rule-base...");
				ByLemmaPosLexicalRuleBase<ChainOfLexicalRules> ruleBase = script.getMetaRuleBaseEnvelope(ruleBaseName).getChainOfLexicalRulesRuleBase();
				if (null==ruleBase)throw new TeEngineMlException("Could not find the rule base: "+ruleBaseName);
				logger.debug("Yes, it is a chain of lexical rules rule base");

				Set<RuleWithConfidenceAndDescription<Info, BasicNode>> setOfDetectedRules = createSetOfRulesForMultiWord(ruleBaseName,ruleBase, hypothesisTreeAsBasicNode);
				if (logger.isDebugEnabled()){logger.debug("Number of MW rules added for resource "+ruleBaseName+" is "+setOfDetectedRules.size());}
				SetBagOfRulesRuleBase<Info,BasicNode> createdRuleBase =
					SetBagOfRulesRuleBase.fromSetWithConfidenceAndDescription(setOfDetectedRules);
				
				mapRuleBasesForLexicalMultiWord.put(ruleBaseName, createdRuleBase);
			}
		}
	}
	
	protected <T extends LexicalRule> Set<RuleWithConfidenceAndDescription<Info, BasicNode>> createSetOfRulesForMultiWord(String ruleBaseName, ByLemmaPosLexicalRuleBase<T> ruleBase , BasicNode hypothesisTreeAsBasicNode) throws TreeAndParentMapException, TeEngineMlException, OperationException
	{
		Set<RuleWithConfidenceAndDescription<Info, BasicNode>> setRules = new LinkedHashSet<RuleWithConfidenceAndDescription<Info, BasicNode>>();

		if (logger.isDebugEnabled()){logger.debug("Finding rules for "+originalTextTrees.size()+" trees.");}
		int iterationIndex=0;
		for (ExtendedNode textTree : originalTextTrees)
		{
			if (logger.isDebugEnabled()){logger.debug("Finding rules for tree: #"+iterationIndex);}
			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
				new TreeAndParentMap<ExtendedInfo, ExtendedNode>(textTree);
			MultiWordRuleBaseCreator<T> creator =
				new MultiWordRuleBaseCreator<T>(hypothesisTreeAsBasicNode, treeAndParentMap, ruleBase, ruleBaseName, teSystemEnvironment.getStopWords());
			creator.create();
			setRules.addAll(creator.getSetRules());
			++iterationIndex;
		}
		
		if (logger.isDebugEnabled()){if (0==setRules.size())logger.debug("No rules were created for rule base \""+ruleBaseName+"\"");}
		
		return setRules;
	}
	
	
	/**
	 * Sets a value to {@link AdditionalNodeInformation}'s <code>contentAncestor</code> field.
	 * 
	 * @throws TeEngineMlException
	 */
	protected void refineTextAndHypothesis() throws TeEngineMlException
	{
		if (AdvancedEqualities.USE_ADVANCED_EQUALITIES)
		{
			BidirectionalMap<ExtendedNode, ExtendedNode> bidiFromOriginal = null;
			if (mapOriginalToInitialized!=null) bidiFromOriginal = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
			
			Map<ExtendedNode,String> refinedMapOriginalTreeToSentence = new LinkedHashMap<ExtendedNode, String>();
			List<ExtendedNode> refinedTextTrees = new ArrayList<ExtendedNode>(originalTextTrees.size());
			for (ExtendedNode originalTextTree : originalTextTrees)
			{
				// ExtendedNode refinedTree = ContentAncestorSetter.generateWithAncestorInformation(originalTextTree);
				ContentAncestorSetter setter = new ContentAncestorSetter(originalTextTree);
				setter.generate();
				ExtendedNode refinedTree = setter.getGeneratedTree();
				if (mapOriginalToInitialized!=null) DsUtils.BidiMapAddAll(bidiFromOriginal, setter.getNodesMap());

				refinedTextTrees.add(refinedTree);
				refinedMapOriginalTreeToSentence.put(refinedTree,this.originalMapTreesToSentences.get(originalTextTree));
			}
			originalTextTrees = refinedTextTrees;
			this.originalMapTreesToSentences = refinedMapOriginalTreeToSentence;
			if (mapOriginalToInitialized!=null) mapOriginalToInitialized = DsUtils.concatenateBidiMaps(mapOriginalToInitialized, bidiFromOriginal);
			
			hypothesisTree = ContentAncestorSetter.generateWithAncestorInformation(hypothesisTree);
		}
	}
	
	protected void initTraces() throws TeEngineMlException
	{
		if (Constants.TRACE_ORIGINAL_NODES)
		{
			logger.info("Setting initial trace information...");
			BidirectionalMap<ExtendedNode, ExtendedNode> bidiFromOriginal = null;
			if (mapOriginalToInitialized!=null) bidiFromOriginal = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
			List<ExtendedNode> newTextTrees = new ArrayList<ExtendedNode>(originalTextTrees.size());
			Map<ExtendedNode,String> newMapTreeToSentence = new LinkedHashMap<ExtendedNode, String>();
			for (ExtendedNode textTree : originalTextTrees)
			{
				SelfTraceSetter setter = new SelfTraceSetter(textTree);
				setter.set();
				ExtendedNode newTree = setter.getNewTree();
				newTextTrees.add(newTree);
				newMapTreeToSentence.put(newTree, originalMapTreesToSentences.get(textTree));
				if (mapOriginalToInitialized!=null) DsUtils.BidiMapAddAll(bidiFromOriginal, setter.getMapping());
			}
			originalTextTrees = newTextTrees;
			originalMapTreesToSentences = newMapTreeToSentence;
			if (mapOriginalToInitialized!=null) mapOriginalToInitialized = DsUtils.concatenateBidiMaps(mapOriginalToInitialized, bidiFromOriginal);
			
			logger.info("Setting initial trace information done.");
		}
	}
	
//	@Workaround
//	protected void annotateTextAndHypothesis() throws AnnotatorException, TeEngineMlException
//	{
//		logger.info("Annotating text and hypothesis...");
//		BidirectionalMap<ExtendedNode, ExtendedNode> bidiFromOriginal = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
//		SynchronizedAtomicAnnotator annotator = this.teSystemEnvironment.getTreeAnnotator();
//		Map<ExtendedNode,String> newMapOriginalTreeToSentence = new LinkedHashMap<ExtendedNode, String>();
//		List<ExtendedNode> annotatedTextTrees = new ArrayList<ExtendedNode>(originalTextTrees.size());
//		for (ExtendedNode originalTextTree : originalTextTrees)
//		{
//			// Here is a workaround for cases that annotator fails, but
//			// we want to continue.
//			// Note that this is a BAD PROGRAMMING PRACTICE, and thus
//			// the value of ANNOTATOR_FAILURE_IS_BLOCKING must be true!
//			boolean annotatorSucceeded = true;
//			AnnotatedTreeAndMap annotated = null;
//			try
//			{
//				annotated = annotator.annotate(originalTextTree);
//			}
//			catch(AnnotatorException e)
//			{
//				annotatorSucceeded=false;
//				if (ANNOTATOR_FAILURE_IS_BLOCKING)
//				{
//					throw e;
//				}
//				else
//				{
//					logger.error("Sentence Annotator FAILED!!! However, since ANNOTATOR_FAILURE_IS_BLOCKING is set to false, the processing continues. Exception is: ",e);
//				}
//			}
//			if (annotatorSucceeded)
//			{
//				ExtendedNode annotatedTree = annotated.getAnnotatedTree();
//				annotatedTextTrees.add(annotatedTree);
//				newMapOriginalTreeToSentence.put(annotatedTree,this.originalMapTreesToSentences.get(originalTextTree));
//				DsUtils.BidiMapAddAll(bidiFromOriginal, annotated.getMapOriginalToAnnotated());
//			}
//			else
//			{
//				BidirectionalMap<ExtendedNode, ExtendedNode> itself = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
//				mapItself(itself,originalTextTree);
//				DsUtils.BidiMapAddAll(bidiFromOriginal, itself);
//				annotatedTextTrees.add(originalTextTree);
//				newMapOriginalTreeToSentence.put(originalTextTree,this.originalMapTreesToSentences.get(originalTextTree));
//			}
//
//		}
//		originalTextTrees = annotatedTextTrees;
//		this.originalMapTreesToSentences = newMapOriginalTreeToSentence;
//		mapOriginalToInitialized = DsUtils.concatenateBidiMaps(mapOriginalToInitialized, bidiFromOriginal);
//
//		ExtendedNode hypothesisTreeBeforeAnnotation = hypothesisTree;
//		try
//		{
//			hypothesisTree = annotator.annotate(hypothesisTree).getAnnotatedTree();
//		}
//		catch(AnnotatorException e)
//		{
//			if (ANNOTATOR_FAILURE_IS_BLOCKING)
//			{
//				throw e;
//			}
//			else
//			{
//				// Here is a workaround for cases that annotator fails, but
//				// we want to continue.
//				// Note that this is a BAD PROGRAMMING PRACTICE, and thus
//				// the value of ANNOTATOR_FAILURE_IS_BLOCKING must be true!
//				hypothesisTree = hypothesisTreeBeforeAnnotation;
//				logger.error("Sentence Annotator FAILED!!! However, since ANNOTATOR_FAILURE_IS_BLOCKING is set to false, the processing continues. Exception is: ",e);
//			}
//		}
//
//		logger.info("Annotating text and hypothesis done.");
//	}
	
	protected static Set<String> wordsInTree(ExtendedNode tree)
	{
		Set<String> ret = new LinkedHashSet<String>();
		Set<ExtendedNode> setNodes = AbstractNodeUtils.treeToLinkedHashSet(tree);
		for (ExtendedNode node : setNodes)
		{
			if (InfoObservations.infoHasLemma(node.getInfo()))
			{
				String lemma = InfoGetFields.getLemma(node.getInfo());
				ret.add(lemma);
				ret.addAll(StringUtil.stringToWords(lemma));
			}
		}
		return ret;
	}
	
	
	/**
	 * Finds all the words (lemmas) in the pair's text.
	 * @param pairData
	 * @return
	 */
	protected static Set<String> wordsInPair(List<ExtendedNode> textTrees)
	{
		Set<String> ret = new LinkedHashSet<String>();
		for (ExtendedNode tree : textTrees)
		{
			ret.addAll(wordsInTree(tree));
		}
		return ret;
	}
	
	protected static Set<String> extractOnlyLemmas(Iterable<LemmaAndPos> lemmasAndPoses)
	{
		Set<String> ret = new LinkedHashSet<String>();
		for (LemmaAndPos lemmaAndPos : lemmasAndPoses)
		{
			if (lemmaAndPos.getLemma().length()>0)
			{
				ret.add(lemmaAndPos.getLemma());
				for (String word : StringUtil.stringToWords(lemmaAndPos.getLemma()))
				{
					ret.add(word);
				}
			}
		}
		return ret;
	}

	
	public static Set<LemmaAndPos> lemmasAndPosesInTree(ExtendedNode tree) throws TeEngineMlException
	{
		Set<LemmaAndPos> ret = new LinkedHashSet<LemmaAndPos>();
		Set<ExtendedNode> setNodes = AbstractNodeUtils.treeToLinkedHashSet(tree);
		for (ExtendedNode node : setNodes)
		{
			if (InfoObservations.infoHasLemma(node.getInfo()))
			{
				String lemma = InfoGetFields.getLemma(node.getInfo());
				PartOfSpeech pos = InfoGetFields.getPartOfSpeechObject(node.getInfo());
				ret.add(new LemmaAndPos(lemma, pos));
				for (String oneLemma : StringUtil.stringToWords(lemma))
				{
					ret.add(new LemmaAndPos(oneLemma, pos));
				}
			}
		}
		return ret;
	}
	
	protected Map<Integer, Double> initialFeatureVector() throws TeEngineMlException
	{
		try
		{
			InitialFeatureVectorUtility utility = new InitialFeatureVectorUtility(teSystemEnvironment.getFeatureVectorStructureOrganizer(),hypothesisTree,hypothesisNumberOfNodes);
			if (this.globalPairInformation!=null)
			{
				utility.setGlobalPairInformation(this.globalPairInformation);
			}
			return utility.initialFeatureVector();
		}
		catch(PluginAdministrationException e)
		{
			throw new TeEngineMlException("Failed to initialize feature vector",e);
		}
	}
	
	protected void createMultiWordNamedEntityRuleBase() throws TeEngineMlException
	{
		Set<RuleWithConfidenceAndDescription<Info, BasicNode>> setRules =
			new MultiWordNamedEntityUtils().setRulesOfNamedEntities(hypothesisTreeAsBasicNode);
		
		multiWordNamedEntityRuleBase = SetBagOfRulesRuleBase.fromSetWithConfidenceAndDescription(setRules);
	}
	
	private void createMapToItselfForAllTrees()
	{
		for (ExtendedNode tree : this.originalTextTrees)
		{
			mapItself(mapOriginalToInitialized,tree);
		}
	}
	
	private static void mapItself(BidirectionalMap<ExtendedNode, ExtendedNode> map, ExtendedNode tree)
	{
		for (ExtendedNode node : AbstractNodeUtils.treeToLinkedHashSet(tree))
		{
			map.put(node,node);
		}
	}
	
	private void verifyTreesHaveArtificialRoot() throws TeEngineMlException
	{
		for (ExtendedNode tree : originalTextTrees)
		{
			if (!TreeUtilities.isArtificialRoot(tree))
			{
				throw new TeEngineMlException("(one of) the text tree(s) have no artificial root. Did you parse without using PreprocessUtilities?");
			}
		}
		if (!TreeUtilities.isArtificialRoot(hypothesisTree))
		{
			throw new TeEngineMlException("The hypothesis tree has no artificial root. Did you parse without using PreprocessUtilities?");
		}

		
	}
	
	
	

	
	protected String textText;
	protected String hypothesisText;
	protected List<ExtendedNode> originalTextTrees;
	protected ExtendedNode hypothesisTree;
	protected Map<ExtendedNode, String> originalMapTreesToSentences;
	protected TreeCoreferenceInformation<ExtendedNode> coreferenceInformation;
	protected LinearClassifier classifier;
	protected Lemmatizer lemmatizer;
	protected OperationsScript<Info, BasicNode> script;
	protected TESystemEnvironment teSystemEnvironment;
	protected final boolean hybridGapMode;
	
	protected List<ExtendedNode> surroundingsContext = null;
	protected boolean richInformationInTreeHistory = false;
	
	protected GlobalPairInformation globalPairInformation=null;
	
	private SealedObject<ExtendedNode> sealedOriginalHypothesisTree = null;
	
	protected Set<String> wholeTextLemmas;
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesis;
	protected int hypothesisNumberOfNodes = 0;
	private ImmutableSet<LemmaAndPos> hypothesisLemmas;
	private ImmutableSet<String> hypothesisLemmasOnly;
	private Set<String> hypothesisLemmasLowerCase;
	private ImmutableSet<String> hypothesisTemplates;
	private SubstitutionMultiWordUnderlyingFinder substitutionMultiWordFinder = null;
	private FeatureUpdate featureUpdate;
	
	private BasicNode hypothesisTreeAsBasicNode; 
	
	private Map<String,BagOfRulesRuleBase<Info, BasicNode>> mapRuleBasesForLexicalMultiWord = null;
	
	private BagOfRulesRuleBase<Info, BasicNode> multiWordNamedEntityRuleBase = null;
	
	protected GapToolInstances<ExtendedInfo, ExtendedNode> gapTools = null;
	protected OperationsEnvironment operationsEnvironment;
	protected GapEnvironment<ExtendedInfo, ExtendedNode> gapEnvironment;
	protected OriginalTreesAfterInitialization originalTreesAfterInitialization = null;
	
	private static final Cache<SealedObject<ExtendedNode>, ImmutableSet<String>> cacheHypothesisToTemplates =
			new CacheFactory<SealedObject<ExtendedNode>, ImmutableSet<String>>().getCache(CACHE_SIZE_HYPOTHESIS_TEMPLATES);

	/**
	 * This field will hold a mapping from the {@link #originalTextTrees} nodes - to
	 * the nodes of the new trees created during initialization.
	 */
	private BidirectionalMap<ExtendedNode, ExtendedNode> mapOriginalToInitialized = null;
	
	private static final Logger logger = Logger.getLogger(InitializationTextTreesProcessor.class);
}
