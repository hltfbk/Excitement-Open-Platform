package eu.excitementproject.eop.biutee.rteflow.micro;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.PluginException;
import eu.excitementproject.eop.biutee.rteflow.macro.FeatureUpdate;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.old_beam_search.BeamSearchTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.old_beam_search.TreeAndHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.search.old_beam_search.TreeAndOperationItem;
import eu.excitementproject.eop.biutee.rteflow.micro.perform.PerformFactory;
import eu.excitementproject.eop.biutee.rteflow.micro.perform.PerformFactoryFactory;
import eu.excitementproject.eop.biutee.script.RuleBasesAndPluginsContainer;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.SingleOperationItem;
import eu.excitementproject.eop.biutee.script.SingleOperationType;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.Finder;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseEnvelope;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * This class performs the operations on a given tree.

 * <P>
 * Typically, every implementation of {@link TextTreesProcessor} has to call
 * this class to perform the operations.
 * <P>
 * <B>Normal usage:</B>
 * <OL>
 * <LI>Call the constructor {@link #TreesGeneratorByOperations(TreeAndFeatureVector, ImmutableList, RuleBasesContainer, TreeHistory, OperationsEnvironment)}</LI>
 * <LI>Optionally call {@link #setAffectedNodes(Set)}</LI>
 * <LI>Call {@link #generateTrees()}</LI>
 * <LI>Collect the results by calling {@link #getGeneratedTrees()}, and {@link #getHistoryMap()}</LI>
 * </OL>
 * 
 * @see TextTreesProcessor
 * 
 * 
 * @author Asher Stern
 * @since Apr 4, 2011
 *
 */
public class TreesGeneratorByOperations
{
	/////////////////////////////// PUBLIC /////////////////////////////////
	

	
	/**
	 * Constructor with the parse-tree on which the operations should
	 * be applied.
	 * @param textTree The parse-tree - which might be either the original parse tree
	 * of the text or a parse-tree that was generated earlier by applying
	 * some operations on the parse-tree of the original text.
	 * @param operations A list of types-of-operations to be applied on the given tree.
	 * @param script A container of rule-bases, used to hold the rule-bases required
	 * by some of the operations.
	 * @param treeHistory Contains the "history" of the given parse-tree. This history
	 * is a list of all operations that were already applied on this given parse-tree.
	 * @param operationsEnvironment Contains several objects necessary to apply
	 * operations on tree, corresponding to the current text-hypothesis pair.
	 * 
	 * @throws TeEngineMlException if an argument that should not be null is null,
	 * or any illegal value of argument, or any other error.
	 */
	public TreesGeneratorByOperations(
			TreeAndFeatureVector textTree,
			ImmutableList<SingleOperationItem> operations,
			RuleBasesAndPluginsContainer<Info, BasicNode> script,
			TreeHistory treeHistory, OperationsEnvironment operationsEnvironment) throws TeEngineMlException
	{
		if (null==operationsEnvironment) throw new TeEngineMlException("Null operationsEnvironment");
		if (null==textTree)throw new TeEngineMlException("Null textTree");
		if (null==operations)throw new TeEngineMlException("Null operations");
		if (null==script)throw new TeEngineMlException("Null script");
		if (null==operationsEnvironment.getFeatureUpdate())throw new TeEngineMlException("Null featureUpdate");
		if (null==operationsEnvironment.getHypothesis())throw new TeEngineMlException("Null hypothesis");
		if (null==operationsEnvironment.getHypothesisLemmas())throw new TeEngineMlException("Null hypothesisLemmas");
		if (null==operationsEnvironment.getHypothesisLemmasOnly())throw new TeEngineMlException("Null hypothesisLemmasOnly");
		if (null==operationsEnvironment.getSubstitutionMultiWordFinder())throw new TeEngineMlException("Null substitutionMultiWordFinder");
		if (null==operationsEnvironment.getLemmatizer())throw new TeEngineMlException("Null lemmatizer");
		if (null==operationsEnvironment.getCoreferenceInformation())throw new TeEngineMlException("Null coreferenceInformation");
		if (null==treeHistory)throw new TeEngineMlException("Null treeHistory");
		if (null==operationsEnvironment.getHypothesisTemplates())throw new TeEngineMlException("Null hypothesisTemplates");
		if (null==operationsEnvironment.getMapLexicalMultiWord())
		{
			if (Constants.HANDLE_LEXICAL_MULTI_WORD)
				throw new TeEngineMlException("Null mapLexicalMultiWord");
		}
		if (null==operationsEnvironment.getMultiWordNamedEntityRuleBase())
		{
			if (Constants.HANDLE_MULTI_WORD_NAMED_ENTITIES)
				throw new TeEngineMlException("Null multiWordNamedEntityRuleBase");
		}

		this.textTree = textTree;
		this.operations = operations;
		this.ruleBasesContainer = script;
		this.treeHistory = treeHistory;
		this.operationsEnvironment = operationsEnvironment;
		
		this.featureUpdate = operationsEnvironment.getFeatureUpdate();
		this.hypothesis = operationsEnvironment.getHypothesis();
		this.historyWithTreeInformation = operationsEnvironment.isRichInformationInTreeHistory();
		
		this.performFactoryFactory = new PerformFactoryFactory(this.operationsEnvironment,this.ruleBasesContainer);

		this.historyMap = new LinkedHashMap<TreeAndFeatureVector, TreeHistory>();
	}
	

	
	

	/**
	 * a cache of generated-parse-trees. If an operation has to be applied on the given
	 * tree, and the cache already contains the tree that is the result of this operation-application,
	 * then there is no need to apply the operation, but only to add the cached resulting tree
	 * to the set of returned (generated) trees.
	 * <P>
	 * Note that this method is used only by {@link BeamSearchTextTreesProcessor}, so actually
	 * it is no longer used, and thus the member-variable {@link #cache} is no longer used.
	 * @param cache
	 * @throws TeEngineMlException
	 */
	public void setCache(Map<TreeAndOperationItem, Set<TreeAndHistory>> cache) throws TeEngineMlException
	{
		if (affectedNodes!=null) throw new TeEngineMlException("Cache should not be used when AffectedNodes are used.");
		if (null==cache) throw new TeEngineMlException("Null cache - BUG.");
		this.cache = cache;
//		if (logger.isDebugEnabled())
//		{
//			logger.debug(this.getClass().getSimpleName()+": Cache was set. Cache size = "+this.cache.keySet().size());
//		}
	}
	
	/**
	 * Sets the set of "affected nodes" corresponding to the given tree (the tree given
	 * in the constructor).
	 * <P>
	 * If this method is called, then not all trees will be generated, but only
	 * trees that cannot be generated if one of the "affected nodes" is missing.
	 * <P>
	 * This concept is called "local-lookahead". It is used by the LLGS algorithm,
	 * see {@link LocalCreativeTextTreesProcessor}.
	 * 
	 * @param affectedNodes subset of nodes of the given tree (given in the constructor),
	 * which are the nodes affected by a previous operation applied on the given tree.
	 *  
	 * @throws TeEngineMlException
	 */
	public void setAffectedNodes(Set<ExtendedNode> affectedNodes) throws TeEngineMlException
	{
		if (this.cache!=null) throw new TeEngineMlException("AffectedNodes should not be used when cache is used.");
		if (null==affectedNodes) throw new TeEngineMlException("Null affectedNodes");
		this.affectedNodes = affectedNodes;
		this.filterSpecifications = new FilterSpecifications(this.textTree.getTree(), this.affectedNodes);
	}
	
	

	/**
	 * Generates the trees.<BR>
	 * The fields <code>generatedTrees</code> and <code>generatedTreesAsMap</code>
	 * will contain the generated trees.<BR>
	 * The generated trees can then be
	 * fetched by the methods {@link #getGeneratedTrees()}
	 * and {@link #getGeneratedTreesAsMap()}.
	 * @throws TeEngineMlException
	 * @throws OperationException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 * @throws TreeAndParentMapException
	 */
	public void generateTrees() throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException
	{
		this.generatedTrees = new LinkedHashSet<TreeAndFeatureVector>();
		this.generatedTreesAsMap = new LinkedHashMap<TreeAndOperationItem, Set<TreeAndHistory>>();
		this.mapGeneratedByOperation = new LinkedHashMap<SingleOperationItem, Set<TreeAndFeatureVector>>();
		this.mapAffectedNodes = new LinkedHashMap<ExtendedNode, Set<ExtendedNode>>();
		TreeAndParentMap<ExtendedInfo,ExtendedNode> textTreeAndParentMap = new TreeAndParentMap<ExtendedInfo,ExtendedNode>(textTree.getTree());
		
		// For each operation-item, generated the trees. 
		for (SingleOperationItem item : operations)
		{
			Set<TreeAndFeatureVector> setGenerated = null;
			
			// Note: Ignore all of this "cache" code. It no longer runs. Just jump to the
			// call to generateUsingPerformerFactory() (see few lines below).
			
			// If the trees to be generate already exist in the cache, just
			// take them from the cache.
			boolean foundInCache = false;
			TreeAndOperationItem keyOfCurrent = new TreeAndOperationItem(this.textTree, item);
			if (cache!=null){if( cache.containsKey(keyOfCurrent))
			{
				foundInCache = true;
				setGenerated = new LinkedHashSet<TreeAndFeatureVector>();
				Set<TreeAndHistory> setCachedGenerated = cache.get(keyOfCurrent);
				for (TreeAndHistory treeAndHistory : setCachedGenerated)
				{
					setGenerated.add(treeAndHistory.getTree());
					this.historyMap.put(treeAndHistory.getTree(), treeAndHistory.getHistory());
				}
				this.generatedTreesAsMap.put(keyOfCurrent, setCachedGenerated);
				debugFoundInCacheCount++;
				debugGeneratedFoundInCacheCount += setCachedGenerated.size();
			}}
			if (!foundInCache)
			{
				try
				{
					setGenerated = generateUsingPerformerFactory(item,textTree, textTreeAndParentMap);
					if (null==setGenerated) throw new TeEngineMlException("Unsupported operation item: "+item.getType().name());

					// The following few lines of code are no longer relevant,
					// since they are required only to BeamSearchTextTreesProcessor,
					// which is no longer used.
					// The following lines are required to return a new cache,
					// to be used later when again a TreeGeneratorByOperations will
					// be created.
					Set<TreeAndHistory> setGeneratedWithHistory = new LinkedHashSet<TreeAndHistory>();
					for (TreeAndFeatureVector tree : setGenerated)
					{
						TreeHistory history = this.historyMap.get(tree);
						if (null==history) throw new TeEngineMlException("Null history for generated tree");
						setGeneratedWithHistory.add(new TreeAndHistory(tree, history));
					}
					this.generatedTreesAsMap.put(keyOfCurrent, setGeneratedWithHistory);
					// end of lines of code required to return a new cache
					// --
				}
				catch (PluginException e)
				{
					throw new OperationException("Plugin failed.",e);
				}
			}
			mapGeneratedByOperation.put(item, setGenerated);
			generatedTrees.addAll(setGenerated);
		} // end of for loop (for each operation-item)
	}
	
	
	
	/**
	 * Returns the output of this {@link TreesGeneratorByOperations} - which is the
	 * newly created trees.
	 * @return The newly created trees.
	 */
	public Set<TreeAndFeatureVector> getGeneratedTrees()
	{
		return generatedTrees;
	}
	
	/**
	 * This is too the output of the {@link TreesGeneratorByOperations} -
	 * now not the set of trees (as in {@link #getGeneratedTrees()}), but as a map
	 * from each generated tree to the {@link TreeHistory} object that describes <b>all</b>
	 * the operations that were applied on this tree starting from the original text tree.
	 * @return
	 */
	public Map<TreeAndFeatureVector,TreeHistory> getHistoryMap()
	{
		return this.historyMap;
	}
	
	/**
	 * Used only by {@link BeamSearchTextTreesProcessor}.
	 * @return
	 */
	public Map<TreeAndOperationItem, Set<TreeAndHistory>> getGeneratedTreesAsMap()
	{
		return generatedTreesAsMap;
	}
	
	/**
	 * Used only by {@link BeamSearchTextTreesProcessor}.
	 * @return
	 */
	public int getDebugFoundInCacheCount()
	{
		return debugFoundInCacheCount;
	}
	
	/**
	 * Used only by {@link BeamSearchTextTreesProcessor}.
	 * @return
	 */
	public int getDebugGeneratedFoundInCacheCount()
	{
		return debugGeneratedFoundInCacheCount;
	}
	
	/**
	 * Used only by {@link BeamSearchTextTreesProcessor}.
	 * @return
	 * @throws TeEngineMlException
	 */
	public Map<SingleOperationItem, Set<TreeAndFeatureVector>> getMapGeneratedByOperation() throws TeEngineMlException
	{
		if (null==mapGeneratedByOperation)throw new TeEngineMlException("Null. Not generated!");
		return mapGeneratedByOperation;
	}
	
	/**
	 * Returns the set of affected nodes for each newly-generated tree.
	 * This is required for the local-lookahead when a newly-created tree will
	 * be given as the tree in the constructor of a new {@link TreesGeneratorByOperations}.
	 * <BR>
	 * It is used by LLGS - implemented in {@link LocalCreativeTextTreesProcessor}.
	 * @return
	 * @throws TeEngineMlException
	 */
	public Map<ExtendedNode, Set<ExtendedNode>> getMapAffectedNodes() throws TeEngineMlException
	{
		if (this.cache!=null) throw new TeEngineMlException("When using cache - the mapping is undefined.");
		if (mapAffectedNodes==null) throw new TeEngineMlException("Null. Map of affected nodes was not created (seems that generateTrees() was not called).");
		return mapAffectedNodes;
	}
	
	////////////////////////////// PRIVATE /////////////////////////////

	
	
	/**
	 * For the given type of operation to be applied, this function retrieves the
	 * appropriate {@link PerformFactory} and then uses it to perform all the operations
	 * of this type.
	 * @param item
	 * @param textTree
	 * @param textTreeAndParentMap
	 * @return
	 * @throws TeEngineMlException
	 * @throws OperationException
	 * @throws PluginException 
	 */
	private Set<TreeAndFeatureVector> generateUsingPerformerFactory(SingleOperationItem item, TreeAndFeatureVector textTree, TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap) throws TeEngineMlException, OperationException, PluginException
	{
		String errorIfPluginThrowsRuntimeException = SingleOperationType.PLUGIN_APPLICATION.equals(item.getType())?"Plugin has thrown a runtime-exception":null;
		List<PerformFactory<? extends Specification>> listPerformFactories = this.performFactoryFactory.getFactory(item);
		
		if (listPerformFactories!=null)
		{
			Set<TreeAndFeatureVector> generated = null;
			if (listPerformFactories.size()==1)
			{
				generated = generateUsingGivenPerformFactory(item, listPerformFactories.iterator().next(), textTree, textTreeAndParentMap,errorIfPluginThrowsRuntimeException);
			}
			else
			{
				generated = new LinkedHashSet<TreeAndFeatureVector>();
				for (PerformFactory<? extends Specification> performerFactory : listPerformFactories)
				{
					generated.addAll(generateUsingGivenPerformFactory(item, performerFactory, textTree, textTreeAndParentMap,errorIfPluginThrowsRuntimeException));
				}
			}
			return generated;
		}
		else
		{
			return null; // so the caller will throw an exception.
		}
	}

	/**
	 * Given a {@link PerformFactory}, this function performs the operations
	 * that can be performed by this {@linkplain PerformFactory}.
	 * 
	 * @param <T> Type of {@link Specification} used by the given {@link PerformFactory}.
	 * @param item
	 * @param performFactory
	 * @param textTree
	 * @param textTreeAndParentMap
	 * @return
	 * @throws TeEngineMlException
	 * @throws OperationException
	 */
	private <T extends Specification> Set<TreeAndFeatureVector> generateUsingGivenPerformFactory(SingleOperationItem item, PerformFactory<T> performFactory, TreeAndFeatureVector textTree, TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap, String errorIfRuntimeThrown) throws TeEngineMlException, OperationException
	{
		try
		{
			// "ret" is the set of returned trees - which are all the trees that can be
			// generated by applying operations that can be applied by the given
			// PerformFactory.
			Set<TreeAndFeatureVector> ret = new LinkedHashSet<TreeAndFeatureVector>();

			// Get a finder to find the operations that can be applied.
			Finder<T> finder = getFinder(item, performFactory, textTree, textTreeAndParentMap);
			if (null==finder) throw new TeEngineMlException("Could not get a finder.");

			// Try to improve run-time: Let the finder know what is going to be filtered -
			// so the finder will not even try to find operations that will be anyhow filtered.
			if ( (filterSpecifications!=null) && (this.affectedNodes!=null) )
			{
				finder.optionallyOptimizeRuntimeByAffectedNodes(this.affectedNodes);
			}

			// Find all the operations that can be applied.
			finder.find();
			Set<T> specs = finder.getSpecs();

			// If we are in local-lookahead mode, filter the operations that can be applied
			// such that only the operations that fit the local-lookahead will be applied,
			// and all the rest are discarded.
			// "Local-lookahead" is an algorithmic component which is part of the algorithm
			// "LLGS", implemented in LocalCreativeTextTreesProcessor.
			if (filterSpecifications!=null)specs=filterSpecifications.filterSpecifications(specs);

			// For each specification - which describes a single operation
			// that can be applied - apply it.
			for (T spec : specs)
			{
				try
				{
					// Take the GenerationOperation object that can apply the operation
					GenerationOperation<ExtendedInfo, ExtendedNode> operation =
							performFactory.getOperation(textTreeAndParentMap,this.hypothesis,spec);

					// apply the operation - a new tree is created.
					operation.generate();

					if (operation.discardTheGeneratedTree())
					{
						if (logger.isDebugEnabled())
						{
							logger.debug("An operation "+operation.getClass().getSimpleName()+" has declared that it should be discarded. The generated tree will not be added to the set of generated trees.");
						}
					}
					else
					{
						// This is the new tree that was created right now.
						ExtendedNode generatedTree = generatedTreeFromOperation(operation);

						// Store the set of affected nodes of this tree. Used by LLGS (LocalCreativeTextTreesProcessor)
						mapAffectedNodes.put(generatedTree, operation.getAffectedNodes());

						// Create a new feature vector that describes this newly created tree.
						Map<Integer,Double> featureVector = performFactory.getUpdater(textTreeAndParentMap,this.hypothesis).updateFeatureVector(textTree.getFeatureVector(), this.featureUpdate, textTreeAndParentMap, this.hypothesis, operation, spec);

						// Create an object that holds the tree and the feature-vector - it will be
						// returned by getGeneratedTrees() method.
						TreeAndFeatureVector retTree = new TreeAndFeatureVector(generatedTree, featureVector);

						// Perform any post-processing required. Currently - only create a
						// new TreeHistory object for this newly created tree.
						postProcessOfTreeGeneration(textTree, spec, retTree,operation.getMapOriginalToGenerated());

						// Add the newly created tree to the set of generated trees that will be returned by
						// this function.
						ret.add(retTree);
					}
				}
				catch(RuntimeException rx)
				{
					String specString = "(cannot write spec string)";
					try{specString = spec.toString();}catch(Throwable t){}

					throw new TeEngineMlException("A runtime exception has been thrown by the operation." +
							(errorIfRuntimeThrown!=null?"\n"+errorIfRuntimeThrown:"")+
							"\nSpecification is: "+specString,rx);

				}
			}
			
			return ret;
		}
		catch(RuntimeException rx)
		{
			if (errorIfRuntimeThrown!=null) throw new TeEngineMlException(errorIfRuntimeThrown,rx);
			else throw rx;
		}
	}

	
//	private <T extends Specification> Set<TreeAndFeatureVector> generateUsingGivenPerformFactory(SingleOperationItem item, PerformFactory<T> performFactory, TreeAndFeatureVector textTree, TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap, String errorIfRuntimeThrown) throws TeEngineMlException, OperationException
//	{
//		if (null==errorIfRuntimeThrown)
//		{
//			return generateUsingGivenPerformFactory(item, performFactory, textTree, textTreeAndParentMap);
//		}
//		else
//		{
//			try
//			{
//				return generateUsingGivenPerformFactory(item, performFactory, textTree, textTreeAndParentMap);
//			}
//			catch(RuntimeException e)
//			{
//				throw new OperationException(errorIfRuntimeThrown,e);
//			}
//		}
//	}
	
	private <T extends Specification> Finder<T>  getFinder(SingleOperationItem item, PerformFactory<T> performFactory, TreeAndFeatureVector textTree, TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap) throws TeEngineMlException, OperationException
	{
		Finder<T> finder = null; 
		if (SingleOperationType.LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION==item.getType())
		{
			ByLemmaPosLexicalRuleBase<LexicalRule> ruleBase =
				this.ruleBasesContainer.getByLemmaPosLexicalRuleBase(item.getRuleBaseName());
			finder = performFactory.getFinder(textTreeAndParentMap, this.hypothesis,ruleBase, item.getRuleBaseName());
			if (null==finder)
			{
				finder = performFactory.getFinder(textTreeAndParentMap, this.hypothesis, item.getRuleBaseName());
			}
		}
		else if (SingleOperationType.LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION_2D==item.getType())
		{
			ByLemmaPosLexicalRuleBase<LexicalRule> ruleBase =
				this.ruleBasesContainer.getByLemmaPosLexicalRuleBase(item.getRuleBaseName());
			finder = performFactory.getFinder(textTreeAndParentMap, this.hypothesis,ruleBase, item.getRuleBaseName());
			if (null==finder)
			{
				finder = performFactory.getFinder(textTreeAndParentMap, this.hypothesis, item.getRuleBaseName());
			}
		}
		else if (SingleOperationType.RULE_APPLICATION==item.getType())
		{
			RuleBaseEnvelope<Info, BasicNode> ruleBase =
				this.ruleBasesContainer.getRuleBaseEnvelope(item.getRuleBaseName());
			finder = performFactory.getFinder(textTreeAndParentMap, this.hypothesis,ruleBase, item.getRuleBaseName());
			if (null==finder)
			{
				finder = performFactory.getFinder(textTreeAndParentMap, this.hypothesis, item.getRuleBaseName());
			}
		}
		else if (SingleOperationType.META_RULE_APPLICATION==item.getType())
		{
			RuleBaseEnvelope<Info, BasicNode> ruleBase =
				this.ruleBasesContainer.getMetaRuleBaseEnvelope(item.getRuleBaseName());
			finder = performFactory.getFinder(textTreeAndParentMap, this.hypothesis,ruleBase, item.getRuleBaseName());
			if (null==finder)
			{
				finder = performFactory.getFinder(textTreeAndParentMap, this.hypothesis, item.getRuleBaseName());
			}
		}
		else
		{
			finder = performFactory.getFinder(textTreeAndParentMap, this.hypothesis);
		}
		return finder;
		
	}


	
	
	/**
	 * Merely creates a new {@link TreeHistory} for the generated tree, and put this
	 * newly created {@link TreeHistory} in the map {@link #historyMap}.
	 * 
	 * @param originalTree
	 * @param specification
	 * @param generatedTree
	 * @param originalToGeneratedMapping
	 * @throws TeEngineMlException
	 */
	private void postProcessOfTreeGeneration(TreeAndFeatureVector originalTree, Specification specification, TreeAndFeatureVector generatedTree, ValueSetMap<ExtendedNode, ExtendedNode> originalToGeneratedMapping) throws TeEngineMlException
	{
		TreeHistory generatedTreeHistory;
		generatedTreeHistory = new TreeHistory(treeHistory);
		
		if (this.historyWithTreeInformation)
		{
			Set<ExtendedNode> affectedNodes = mapAffectedNodes.get(generatedTree.getTree());
			if (null==affectedNodes) throw new TeEngineMlException("BUG: affectedNodes is null");
			generatedTreeHistory.addComponent(
					new TreeHistoryComponent(specification,generatedTree.getFeatureVector(),affectedNodes,generatedTree.getTree()));
		}
		else
		{
			if (BiuteeConstants.ADD_FEATURE_VECTOR_TO_HISTORY)
			{
				generatedTreeHistory.addSpecificationAndVector(specification, generatedTree.getFeatureVector());
			}
			else
			{
				generatedTreeHistory.addSpecification(specification);
			}
		}
		
		historyMap.put(generatedTree,generatedTreeHistory);
		
	}
	
	private ExtendedNode generatedTreeFromOperation(GenerationOperation<ExtendedInfo, ExtendedNode> operation) throws TeEngineMlException, OperationException
	{
		return operation.getGeneratedTree();	
	}

	
	

	
	
	
	private TreeAndFeatureVector textTree;
	private ImmutableList<SingleOperationItem> operations;
	private OperationsEnvironment operationsEnvironment;
	
	private RuleBasesAndPluginsContainer<Info, BasicNode> ruleBasesContainer;
	private FeatureUpdate featureUpdate;
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesis;
	
	/**
	 * if <tt>true</tt> than the {@link TreeHistory} created for each generated tree
	 * contains more information than usual. Inparticular - it contains also
	 * the tree itself and the affected nodes.
	 * This mode is good for GUI.
	 */
	private boolean historyWithTreeInformation = false;
	
	private Map<TreeAndFeatureVector,TreeHistory> historyMap;
	private TreeHistory treeHistory = null;
	
	
	


	private Set<TreeAndFeatureVector> generatedTrees;
	private Map<TreeAndOperationItem, Set<TreeAndHistory>> generatedTreesAsMap; // used for later caching.
	
	/**
	 * The cache stores generated trees for a given tree, such that
	 * there is no need to generate those generated trees, since they were
	 * already generated earlier, and they are stored in the cache.<BR>
	 * *****************************************************************<BR>
	 * <B>More concretely:</B> Given a tree (let's call it "source") and
	 * an operation item ( {@link SingleOperationItem} ), there are several trees
	 * that can be generated by applying the operation-item on "source". So,
	 * the key of this map, {@link TreeAndOperationItem}, stores "source" and
	 * the operation-item, and the value of this map is a set of the several trees
	 * that can be generated by applying the operation-item on "source".<BR>
	 * Note: currently only {@link BeamSearchTextTreesProcessor} uses this caching
	 * mechanism.
	 * <P>
	 * Note also that the cache is null by default, and not used.
	 * Only if {@link #setCache(Map)} is called (right after the constructor), the cache is used.
	 */
	private Map<TreeAndOperationItem, Set<TreeAndHistory>> cache=null;
	private int debugFoundInCacheCount = 0;
	private int debugGeneratedFoundInCacheCount = 0;
	
	private Map<SingleOperationItem,Set<TreeAndFeatureVector>> mapGeneratedByOperation;

	/**
	 * if not <code>null</code> - it means that only operations that are to be
	 * done on nodes that are in the set "affectedNodes" should be performed.
	 * In other words: if affectedNodes is not <code>null</code>, then if, for
	 * example, a rule can be applied on the tree, but non of the matched nodes
	 * (the nodes in the tree that match the rule's left-hand-side) are
	 * in "affectedNodes" - than that rule will not be applied. Only if at least
	 * one of the nodes mapped to the left-hand-side also exists in "affectedNodes",
	 * then the rule will be applied.
	 */
	private Set<ExtendedNode> affectedNodes = null;
	private FilterSpecifications filterSpecifications = null;
	
	private Map<ExtendedNode,Set<ExtendedNode>> mapAffectedNodes = null;
	
	private PerformFactoryFactory performFactoryFactory = null;
	
	
	private static final Logger logger = Logger.getLogger(TreesGeneratorByOperations.class);
	
	
//	/////////////////// TO BE DELETED ///////////////////
//	public static Map<String, Long> counterGenerated;
//	public static void addToCounterGenerated(String ruleBaseName, long count)
//	{
//		Long current = counterGenerated.get(ruleBaseName);
//		long nativeCurrent = 0;
//		if (current!=null) {nativeCurrent = current.longValue();}
//		counterGenerated.put(ruleBaseName, nativeCurrent+count);
//	}
//	public static void printGeneratedStatistics()
//	{
//		StringBuilder sb = new StringBuilder();
//		sb.append("Generated statistics:\n");
//		for (String ruleBaseName : counterGenerated.keySet())
//		{
//			sb.append(ruleBaseName).append(": ").append(counterGenerated.get(ruleBaseName)).append("\n");
//		}
//		logger.info(sb.toString());
//	}
}
