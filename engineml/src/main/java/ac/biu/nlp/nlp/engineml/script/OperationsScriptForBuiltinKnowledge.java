package ac.biu.nlp.nlp.engineml.script;

import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.KNOWLEDGE_RESOURCES_MODULE_NAME;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.KNOWLEDGE_RESOURCES_PARAMETER_NAME;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.MANUAL_FILE_RULEBASE_DYNAMIC_PARAMETER_NAME;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.MANUAL_FILE_RULEBASE_FILE_PARAMETER_NAME;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.MANUAL_FILE_RULEBASE_USE_PARAMETER_NAME;

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

import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

import ac.biu.nlp.nlp.engineml.builtin_knowledge.ConstructorOfLexicalResourcesForChain;
import ac.biu.nlp.nlp.engineml.builtin_knowledge.KnowledgeResource;
import ac.biu.nlp.nlp.engineml.builtin_knowledge.LexicalResourcesFactory;
import ac.biu.nlp.nlp.engineml.datastructures.CanonicalLemmaAndPos;
import ac.biu.nlp.nlp.engineml.datastructures.LemmaAndPos;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRuleBaseCloseException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseEnvelope;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleWithConfidenceAndDescription;
import ac.biu.nlp.nlp.engineml.operations.rules.SetBagOfRulesRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.distsimnew.DirtDBRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.ChainOfLexicalRules;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.builder.BuilderSetOfWords;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.builder.SimpleLexicalChainRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.graphbased.PlisRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.manual.DummyRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.manual.FromTextFileRuleBase;
import ac.biu.nlp.nlp.engineml.rteflow.macro.InitializationTextTreesProcessor;
import ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;

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
	public OperationsScriptForBuiltinKnowledge(ConfigurationFile configurationFile)
	{
		super();
		this.configurationFile = configurationFile;
	}
	
	@Override
	public void init() throws OperationException
	{
		if (logger.isDebugEnabled()){logger.debug("Initializing operations script: "+this.getClass().getName());}
		
		try
		{
			knowledgeResourcesParams = configurationFile.getModuleConfiguration(KNOWLEDGE_RESOURCES_MODULE_NAME);
			List<KnowledgeResource> knowledgeResources = knowledgeResourcesParams.getEnumList(KnowledgeResource.class, KNOWLEDGE_RESOURCES_PARAMETER_NAME);
			createItemsForKnowledgeResources(knowledgeResources);
		}
		catch (ConfigurationException e)
		{
			throw new OperationException("Initialization failed. See nested exception.",e);
		}
		catch(RuleBaseException e)
		{
			throw new OperationException("Could not create a rule base.",e);
		}
		catch (ClassNotFoundException e)
		{
			throw new OperationException("Could not create a rule base.",e);
		}
		catch (TeEngineMlException e)
		{
			throw new OperationException("Could not create a rule base.",e);
		}
		catch (LexicalResourceException e)
		{
			throw new OperationException("Could not create a rule base.",e);
		}
		catch (IOException e)
		{
			throw new OperationException("Could not create a rule base.",e);
		}
		catch (SQLException e)
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
		// TODO: Clean also chain of lexical rules!
	}
	
	

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.script.OperationsScript#setHypothesisInformation(ac.biu.nlp.nlp.engineml.script.HypothesisInformation)
	 */
	@Override
	public void setHypothesisInformation(HypothesisInformation hypothesisInformation) throws TeEngineMlException 
	{
		// !!!!! NOTE !!!!!
		// If you are working on RTE-Sum (RTE 6,7) and it looks like that this method is called too many times,
		// then consider changing the value of Constants.USE_OLD_CONCURRENCY_IN_RTE_SUM to true.

		super.setHypothesisInformation(hypothesisInformation);	// this.hypothesisInformation = hypothesisInformation;
		logger.info("DefaultOperationScript.setHypothesisInformation()...");
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
			catch (LexicalResourceException e)
			{
				throw new TeEngineMlException("Failed to create chain of lexical rules",e);
			}
			catch (RuleBaseException e)
			{
				throw new TeEngineMlException("Failed to create chain of lexical rules",e);
			}
		}

		logger.info("DefaultOperationScript.setHypothesisInformation() DONE");
	}
	
	
	
	//////////////////// PROTECTED & PRIVATE //////////////////// 
	
	@SuppressWarnings("unchecked")
	private void createItemsForKnowledgeResources(List<KnowledgeResource> knowledgeResources) throws OperationException, ConfigurationException, RuleBaseException, TeEngineMlException, LexicalResourceException, SQLException, FileNotFoundException, IOException, ClassNotFoundException
	{
		items = new ArrayList<ItemForKnowedgeResource>(knowledgeResources.size());

		// Initialize rule bases.
		listDirtDbRuleBases = new LinkedList<DirtDBRuleBase>();

		ruleBasesEnvelopes = new LinkedHashMap<String, RuleBaseEnvelope<Info,BasicNode>>();
		byLemmaPosLexicalRuleBases = new LinkedHashMap<String, ByLemmaPosLexicalRuleBase<LexicalRule>>();
		metaRuleBasesEnvelopes = new LinkedHashMap<String, RuleBaseEnvelope<Info,BasicNode>>();

		LexicalResourcesFactory lexicalFactory = new LexicalResourcesFactory(configurationFile);
		for (KnowledgeResource resource : knowledgeResources)
		{
			logger.debug("Initializing resource: "+resource.getDisplayName());

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
				DirtDBRuleBase ruleBase = DirtDBRuleBase.fromConfigurationParams(resource.getDisplayName(), configurationFile.getModuleConfiguration(resource.getModuleName()));
				listDirtDbRuleBases.add(ruleBase);
				ruleBasesEnvelopes.put(resource.getDisplayName(),new RuleBaseEnvelope<Info, BasicNode>(ruleBase));
				items.add(new ItemForKnowedgeResource(resource, new SingleOperationItem(SingleOperationType.RULE_APPLICATION, resource.getDisplayName())));
				// otherIterationsList.add(new SingleOperationItem(SingleOperationType.RULE_APPLICATION, resource.getDisplayName()));
				handledOutsideOfSwitch=true;
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
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(resourceParams.getFile(ConfigurationParametersNames.SYNTACTIC_RULES_FILE)));
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
					throw new OperationException("Error reading the syntactic rules set from "+resourceParams.get(ConfigurationParametersNames.SYNTACTIC_RULES_FILE), e);	
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
				int depth = resourceParams.getInt(ConfigurationParametersNames.SIMPLE_LEXICAL_CHAIN_DEPTH_PARAMETER_NAME);
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
	


	
	protected ConfigurationFile configurationFile = null;
	protected ConfigurationParams knowledgeResourcesParams;	
	protected List<ItemForKnowedgeResource> items;

	protected List<DirtDBRuleBase> listDirtDbRuleBases;
	protected PlisRuleBase graphBasedLexicalChainRuleBase = null;
	protected BuilderSetOfWords simpleLexicalChainBuilder = null;
	protected SimpleLexicalChainRuleBase simpleLexicalChainRuleBase = null;

	private static final Logger logger = Logger.getLogger(OperationsScriptForBuiltinKnowledge.class);
}
