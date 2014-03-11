package eu.excitementproject.eop.biutee.script;

import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.TRANSFORMATIONS_MODULE_NAME;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.KNOWLEDGE_RESOURCES_PARAMETER_NAME;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.MANUAL_FILE_RULEBASE_DYNAMIC_PARAMETER_NAME;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.MANUAL_FILE_RULEBASE_FILE_PARAMETER_NAME;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.MANUAL_FILE_RULEBASE_USE_PARAMETER_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.macro.InitializationTextTreesProcessor;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResource;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResourceCloseException;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.component.syntacticknowledge.SimilarityStorageBasedDIRTSyntacticResource;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.distsim.redis.RedisRunException;
import eu.excitementproject.eop.distsim.storage.ElementTypeException;
import eu.excitementproject.eop.transformations.builtin_knowledge.ConstructorOfLexicalResourcesForChain;
import eu.excitementproject.eop.transformations.builtin_knowledge.KnowledgeResource;
import eu.excitementproject.eop.transformations.builtin_knowledge.LexicalResourcesFactory;
import eu.excitementproject.eop.transformations.datastructures.CanonicalLemmaAndPos;
import eu.excitementproject.eop.transformations.datastructures.LemmaAndPos;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRuleBaseCloseException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseEnvelope;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.SetBagOfRulesRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.distsimnew.DirtDBRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.builder.BuilderSetOfWords;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.builder.SimpleLexicalChainRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.graphbased.PlisRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.manual.DummyRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.manual.FromTextFileRuleBase;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames;


/**
 * Creates and initializes all the built-in knowledge resources.
 * The initialized knowledge resources can then be retrieved from the
 * protected field {@link #items}.
 * 
 * @author Asher Stern
 * @since Dec 4, 2012
 *
 */
@NotThreadSafe
public abstract class OperationsScriptForBuiltinKnowledge extends OperationsScript<Info, BasicNode>
{
	public OperationsScriptForBuiltinKnowledge(ConfigurationFile configurationFile,PARSER parser)
	{
		super();
		this.configurationFile = configurationFile;
		this.parser = parser;
	}
	
	@Override
	public void init() throws OperationException
	{
		if (logger.isDebugEnabled()){logger.debug("Initializing operations script: "+this.getClass().getName());}
		
		try
		{
			knowledgeResourcesParams = configurationFile.getModuleConfiguration(TRANSFORMATIONS_MODULE_NAME);
			List<KnowledgeResource> knowledgeResources = knowledgeResourcesParams.getEnumList(KnowledgeResource.class, KNOWLEDGE_RESOURCES_PARAMETER_NAME);
			createItemsForKnowledgeResources(knowledgeResources);
		}
		catch (ConfigurationException e)
		{
			throw new OperationException("Initialization failed. See nested exception.",e);
		}
		catch(RuleBaseException|ClassNotFoundException|TeEngineMlException
				|LexicalResourceException|IOException|SQLException e)
		{
			throw new OperationException("Could not create a rule base.",e);
		}
	}


	@Override
	public void cleanUp()
	{
		if (listDirtDbRuleBases!=null)
		{
			for (DirtDBRuleBase ruleBase : listDirtDbRuleBases)
			{
				ruleBase.terminate();
			}
		}
		if (byLemmaPosLexicalRuleBases!=null)
		{
			for (Map.Entry<String, ByLemmaPosLexicalRuleBase<LexicalRule>> entryRuleBase : byLemmaPosLexicalRuleBases.entrySet())
			{
				try
				{
					entryRuleBase.getValue().close();
				}
				catch (LexicalRuleBaseCloseException e)
				{
					logger.error("Cannot close a lexical rule base, but program continues to run",e);
				}
			}
		}
		if (byLemmaLexicalRuleBases!=null)
		{
			for (Map.Entry<String,ByLemmaLexicalRuleBase> entryRuleBase : byLemmaLexicalRuleBases.entrySet())
			{
				try
				{
					entryRuleBase.getValue().close();
				}
				catch (LexicalRuleBaseCloseException e)
				{
					logger.error("Cannot close a lexical rule base, but program continues to run",e);
				}

			}
		}
		if (excitementSyntacticResources!=null)
		{
			for (SyntacticResource<?, ?> resource : excitementSyntacticResources)
			{
				String name = "Unknown resource name";
				try{name = resource.getComponentName();}catch(Throwable t){}
				try
				{
					if (logger.isDebugEnabled()){logger.debug("Closing syntactic resource "+name);}
					resource.close();
				}
				catch(SyntacticResourceCloseException | RuntimeException e)
				{
					logger.error("The syntactic resource \""+name+"\" could not be closed. However, this does not block the program, but only logged as an error.\n",e);
				}
			}
		}
		// TODO: Clean also chain of lexical rules!
	}
	
	

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.script.OperationsScript#setHypothesisInformation(ac.biu.nlp.nlp.engineml.script.HypothesisInformation)
	 */
	@Override
	public void setHypothesisInformation(HypothesisInformation hypothesisInformation) throws TeEngineMlException 
	{
		super.setHypothesisInformation(hypothesisInformation);	// this.hypothesisInformation = hypothesisInformation;
		if (logger.isDebugEnabled()){logger.debug(CLASS_NAME+".setHypothesisInformation()...");} 
		if (graphBasedLexicalChainRuleBase != null)
		{
			// Creates a set of "LemmaAndPos" of all hypothesis tree's nodes
			List<LemmaAndPos> hypothesisLemmas = new Vector<LemmaAndPos>( InitializationTextTreesProcessor.lemmasAndPosesInTree(hypothesisInformation.getHypothesisTree()) );
			try {	graphBasedLexicalChainRuleBase.setHypothesis(hypothesisLemmas);			}
			catch (Exception e) { throw new TeEngineMlException("Error raised when trying to analize the current hypothesys", e);		}
		}

		if (simpleLexicalChainBuilder!=null)
		{
			ImmutableSet<LemmaAndPos> hypothesisLemmas = new ImmutableSetWrapper<LemmaAndPos>(InitializationTextTreesProcessor.lemmasAndPosesInTree(hypothesisInformation.getHypothesisTree()));
			try
			{
				simpleLexicalChainBuilder.createRuleBase(hypothesisLemmas);
				Map<CanonicalLemmaAndPos, ImmutableSet<ChainOfLexicalRules>> createdRules = simpleLexicalChainBuilder.getRulesForRuleBase();
				if (logger.isDebugEnabled())
				{
					logger.debug("Number of rules in the simple lexical chain: "+createdRules.keySet().size());
					for (CanonicalLemmaAndPos lap : createdRules.keySet())
					{
						for (ChainOfLexicalRules chainof : createdRules.get(lap))
						{
							logger.debug("Rule: "+chainof.getLhsLemma()+" ==> "+chainof.getRhsLemma());
						}
					}
				}
				simpleLexicalChainRuleBase.setRules(createdRules, this);
			}
			catch (LexicalResourceException | RuleBaseException e)
			{
				throw new TeEngineMlException("Failed to create chain of lexical rules",e);
			}
		}

		if (logger.isDebugEnabled()){logger.debug(CLASS_NAME+".setHypothesisInformation() DONE");}
	}
	
	
	
	//////////////////// PROTECTED & PRIVATE //////////////////// 
	
	@SuppressWarnings("unchecked")
	private void createItemsForKnowledgeResources(List<KnowledgeResource> knowledgeResources) throws OperationException, ConfigurationException, RuleBaseException, TeEngineMlException, LexicalResourceException, SQLException, FileNotFoundException, IOException, ClassNotFoundException
	{
		items = new ArrayList<ItemForKnowedgeResource>(knowledgeResources.size());

		// Initialize rule bases.
		listDirtDbRuleBases = new LinkedList<DirtDBRuleBase>();
		excitementSyntacticResources = new LinkedList<>();

		ruleBasesEnvelopes = new LinkedHashMap<String, RuleBaseEnvelope<Info,BasicNode>>();
		byLemmaPosLexicalRuleBases = new LinkedHashMap<String, ByLemmaPosLexicalRuleBase<LexicalRule>>();
		metaRuleBasesEnvelopes = new LinkedHashMap<String, RuleBaseEnvelope<Info,BasicNode>>();

		LexicalResourcesFactory lexicalFactory = new LexicalResourcesFactory(configurationFile);
		for (KnowledgeResource resource : knowledgeResources)
		{
			logger.info("Initializing resource: "+resource.getDisplayName());

			// get the module name for the configuration file
			String moduleName = resource.getInfrastructureModuleName();
			if (null==moduleName)
			{
				moduleName = resource.getModuleName();
			}
			if (null==moduleName) throw new OperationException("No module name declared for resource: "+resource.getDisplayName());
			if (moduleName.trim().length()==0) throw new OperationException("No module name (empty module name) declared for resource: "+resource.getDisplayName());

			// get the relevant module of the configuration file
			ConfigurationParams resourceParams = configurationFile.getModuleConfiguration(moduleName);

			boolean handledOutsideOfSwitch = false;
			if (resource.isDirtLikeDb())
			{
				// Handle all DIRT-like resources (orig-dirt, binary, Unary, Binc, Framenet, etc.)
				DirtDBRuleBase ruleBase = DirtDBRuleBase.fromConfigurationParams(resource.getDisplayName(), configurationFile.getModuleConfiguration(resource.getModuleName()),parser);
				listDirtDbRuleBases.add(ruleBase);
				ruleBasesEnvelopes.put(resource.getDisplayName(),new RuleBaseEnvelope<Info, BasicNode>(ruleBase));
				items.add(new ItemForKnowedgeResource(resource, new SingleOperationItem(SingleOperationType.RULE_APPLICATION, resource.getDisplayName())));
				// otherIterationsList.add(new SingleOperationItem(SingleOperationType.RULE_APPLICATION, resource.getDisplayName()));
				handledOutsideOfSwitch=true;
			}
			else if (resource.isExcitementDIRTlike())
			{
				try
				{
					SimilarityStorageBasedDIRTSyntacticResource excitementSyntacticResource = new SimilarityStorageBasedDIRTSyntacticResource(resourceParams);
					excitementSyntacticResources.add(excitementSyntacticResource);
					RuleBaseEnvelope<Info,BasicNode> syntacticResourceEnvelope = new RuleBaseEnvelope<Info,BasicNode>(excitementSyntacticResource);
					ruleBasesEnvelopes.put(resource.getDisplayName(), syntacticResourceEnvelope);
					items.add(new ItemForKnowedgeResource(resource, new SingleOperationItem(SingleOperationType.RULE_APPLICATION, resource.getDisplayName())));
					handledOutsideOfSwitch=true;
				}
				catch (ElementTypeException | RedisRunException e)
				{
					throw new OperationException("Failed to initialize syntactic resource. See nested exception.",e);
				}
			}
			else
			{
				// Handle lexical resources
				ByLemmaPosLexicalRuleBase<LexicalRule> lexicalRuleBase = lexicalFactory.createByLemmaPosLexicalRuleBase(resource);
				// LexicalResource<? extends RuleInfo> infrastructureLexicalResource = lexicalFactory.createLexicalResource(resource);
				if (lexicalRuleBase!=null)
				{
					//LexicalResourceWrapper lexicalResourceWrapper = new LexicalResourceWrapper(infrastructureLexicalResource);
					byLemmaPosLexicalRuleBases.put(resource.getDisplayName(), lexicalRuleBase);
					items.add(new ItemForKnowedgeResource(resource,new SingleOperationItem(SingleOperationType.LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION_2D, resource.getDisplayName())));
					// otherIterationsList.add(new SingleOperationItem(SingleOperationType.LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION_2D, resource.getDisplayName()));
					handledOutsideOfSwitch = true;
				}
			}

			// Handle all others
			switch(resource)
			{
			case SYNTACTIC:
				Set<RuleWithConfidenceAndDescription<Info, BasicNode>> rulesWithConfidenceAndDescription;
				try {	
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(resourceParams.getFile(TransformationsConfigurationParametersNames.SYNTACTIC_RULES_FILE)));
					try
					{
						rulesWithConfidenceAndDescription = (Set<RuleWithConfidenceAndDescription<Info, BasicNode>>) ois.readObject();
					}
					finally
					{
						ois.close();
					}
				}
				catch (Exception e) { 
					throw new OperationException("Error reading the syntactic rules set from "+resourceParams.get(TransformationsConfigurationParametersNames.SYNTACTIC_RULES_FILE), e);	
				}
				SetBagOfRulesRuleBase<Info, BasicNode> setBagOfRulesRuleBase = SetBagOfRulesRuleBase.fromSetWithConfidenceAndDescription(rulesWithConfidenceAndDescription);
				RuleBaseEnvelope<Info,BasicNode> syntacticRuleBase = new RuleBaseEnvelope<Info, BasicNode>(setBagOfRulesRuleBase);
				ruleBasesEnvelopes.put(resource.getDisplayName(), syntacticRuleBase);
				items.add(new ItemForKnowedgeResource(resource,new SingleOperationItem(SingleOperationType.RULE_APPLICATION, resource.getDisplayName())));
				// otherIterationsList.add(new SingleOperationItem(SingleOperationType.RULE_APPLICATION, resource.getDisplayName()));
				break;

			case MANUAL:
				boolean useManual = resourceParams.getBoolean(MANUAL_FILE_RULEBASE_USE_PARAMETER_NAME);
				if (useManual)
				{
					File rulesFile = new File(resourceParams.get(MANUAL_FILE_RULEBASE_FILE_PARAMETER_NAME));
					boolean dynamicMode = resourceParams.getBoolean(MANUAL_FILE_RULEBASE_DYNAMIC_PARAMETER_NAME);
					FromTextFileRuleBase manualRuleBase = new FromTextFileRuleBase(rulesFile, dynamicMode);
					ruleBasesEnvelopes.put(resource.getDisplayName(),new RuleBaseEnvelope<Info, BasicNode>(manualRuleBase));
				}
				else
				{
					logger.debug("\"manual\" resource is not used.");
					ruleBasesEnvelopes.put(resource.getDisplayName(),new RuleBaseEnvelope<Info, BasicNode>(new DummyRuleBase()));
				}
				items.add(new ItemForKnowedgeResource(resource,new SingleOperationItem(SingleOperationType.RULE_APPLICATION, resource.getDisplayName())));
				// otherIterationsList.add(new SingleOperationItem(SingleOperationType.RULE_APPLICATION, resource.getDisplayName()));
				break;

			case LEXICAL_CHAIN_BY_GRAPH:
				logger.info("Building LexicalChainRuleBase...");
				if (logger.isDebugEnabled())logger.debug("The display name for the LexicalChainRuleBase is: "+resource.getDisplayName());
				ConfigurationFile configurationFile = new ConfigurationFile(this.configurationFile.getConfFile());
				ConfigurationParams configurationParams = configurationFile.getModuleConfiguration(resource.getModuleName());
				graphBasedLexicalChainRuleBase = new PlisRuleBase(configurationParams);
				RuleBaseEnvelope<Info, BasicNode> lexicalChainEnvelope = createEnvelopeForLexicalChain(graphBasedLexicalChainRuleBase, graphBasedLexicalChainRuleBase.getRuleBasesNames());

				this.metaRuleBasesEnvelopes.put(resource.getDisplayName(), lexicalChainEnvelope);

				items.add(new ItemForKnowedgeResource(resource,new SingleOperationItem(SingleOperationType.META_RULE_APPLICATION, resource.getDisplayName())));
				// otherIterationsList.add(new SingleOperationItem(SingleOperationType.META_RULE_APPLICATION, resource.getDisplayName()));
				logger.info("Adding LexicalChainRuleBase - done.");
				break;

			case SIMPLE_LEXICAL_CHAIN:
				logger.info("Creating rule base for lexical chain");
				if (logger.isDebugEnabled()){logger.info("Creating SimpleLexicalChainRuleBase");}
				int depth = resourceParams.getInt(TransformationsConfigurationParametersNames.SIMPLE_LEXICAL_CHAIN_DEPTH_PARAMETER_NAME);
				ConstructorOfLexicalResourcesForChain consturcotOfRules = 
						new ConstructorOfLexicalResourcesForChain(this.configurationFile, resourceParams);
				this.simpleLexicalChainBuilder = new BuilderSetOfWords(consturcotOfRules.constructResources(),depth);
				simpleLexicalChainRuleBase = new SimpleLexicalChainRuleBase(this);
				RuleBaseEnvelope<Info, BasicNode> simpleLexicalChainEnvelope =
						createEnvelopeForLexicalChain(simpleLexicalChainRuleBase, simpleLexicalChainBuilder.getUnsortedSetOfRuleBasesNames());
				this.metaRuleBasesEnvelopes.put(resource.getDisplayName(), simpleLexicalChainEnvelope);
				items.add(new ItemForKnowedgeResource(resource,new SingleOperationItem(SingleOperationType.META_RULE_APPLICATION, resource.getDisplayName())));
				// otherIterationsList.add(new SingleOperationItem(SingleOperationType.META_RULE_APPLICATION, resource.getDisplayName()));
				break;

			default:
			{
				if (!handledOutsideOfSwitch)
					throw new OperationException("Unknown resource: "+resource.name());
			}
			};
		} // end of for
	}

	
	private static RuleBaseEnvelope<Info,BasicNode> createEnvelopeForLexicalChain(ByLemmaPosLexicalRuleBase<ChainOfLexicalRules> ruleBase, Set<String> unsortedSetOfRuleBases) throws RuleBaseException
	{

		RuleBaseEnvelope<Info, BasicNode> lexicalChainEnvelope =
				new RuleBaseEnvelope<Info, BasicNode>(ruleBase);

		List<String> listLCRuleBasesNames = new ArrayList<String>(unsortedSetOfRuleBases.size());
		listLCRuleBasesNames.addAll(unsortedSetOfRuleBases);
		Collections.sort(listLCRuleBasesNames);
		LinkedHashSet<String> lexicalChaingRuleBasesNames = new LinkedHashSet<String>();
		for (String lcRuleBaseName : listLCRuleBasesNames)
		{
			lexicalChaingRuleBasesNames.add(lcRuleBaseName);
		}
		if (logger.isDebugEnabled())
		{
			logger.debug("Adding the following rule bases names:");
			for (String lcRuleBaseName : lexicalChaingRuleBasesNames)
			{
				logger.debug(lcRuleBaseName);
			}
		}

		lexicalChainEnvelope.setRuleBasesNames(lexicalChaingRuleBasesNames);
		
		return lexicalChainEnvelope;
	}
	


	protected final PARSER parser;
	protected ConfigurationFile configurationFile = null;
	protected ConfigurationParams knowledgeResourcesParams;	
	protected List<ItemForKnowedgeResource> items;

	protected List<DirtDBRuleBase> listDirtDbRuleBases;
	protected List<SyntacticResource<?,?>> excitementSyntacticResources = null;
	protected PlisRuleBase graphBasedLexicalChainRuleBase = null;
	protected BuilderSetOfWords simpleLexicalChainBuilder = null;
	protected SimpleLexicalChainRuleBase simpleLexicalChainRuleBase = null;

	private static final String CLASS_NAME = OperationsScriptForBuiltinKnowledge.class.getSimpleName();
	private static final Logger logger = Logger.getLogger(OperationsScriptForBuiltinKnowledge.class);
}
