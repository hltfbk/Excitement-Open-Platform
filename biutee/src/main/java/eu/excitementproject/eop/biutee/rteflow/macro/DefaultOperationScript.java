package eu.excitementproject.eop.biutee.rteflow.macro;

import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.FIRST_ITERATION_IN_DEFAULT_OPERATION_SCRIPT;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.PluginRegistry;
import eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.biutee.script.ItemForKnowedgeResource;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.OperationsScriptForBuiltinKnowledgeAndPlugins;
import eu.excitementproject.eop.biutee.script.SingleOperationItem;
import eu.excitementproject.eop.biutee.script.SingleOperationType;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseEnvelope;
import eu.excitementproject.eop.transformations.utilities.Constants;


/**
 * 
 * This {@link OperationsScript} is "always do everything". All operations and
 * rule bases are activated in each "iteration" which is interpreted as an expansion
 * of a tree.
 * 
 * The iteration-index is the distance from the original tree, i.e. how many expansions
 * were done in the history of the current tree.
 * <P>
 * An important note about the flow (this is a somewhat
 * complicated issue):
 * When a rule application operation is performed, it should be reflected
 * by the feature vector that represents that operation.
 * This is done by {@link FeatureUpdate}'s methods.
 * In order to put the relevant value into the <B>correct</B>
 * feature, it has to know the feature index. The feature index
 * is an integer, mapped to the rule base name.
 * A mapping of rule-bases to the corresponding feature-indexes
 * is maintained by {@link FeatureVectorStructureOrganizer}. An instance
 * of {@link FeatureVectorStructureOrganizer} is given to {@link FeatureUpdate}
 * as a parameter, providing this mapping.
 * {@link FeatureVectorStructureOrganizer} builds this mapping by getting a list
 * (actually it is a {@link LinkedHashSet}) of the rule-bases used by the system.
 * This list, however, is stored in {@link OperationsScript}.
 * <BR>
 * To properly build and use the list of rule bases names, 
 * there are some points to be aware of:
 * <UL>
 * <LI>When adding knowledge resources, their names
 * are added as well, as keys to one of the maps:
 * byLemmaPosLexicalRuleBases, byLemmaLexicalRuleBases,
 * ruleBasesEnvelopes</LI>
 * <LI>When adding a knowledge resource that is a composition
 * of several knowledge resources (e.g. chain-of-lexical-rules)
 * All of the knowledge resources names must be added to the
 * envelope of that added composition. This is done by the method
 * {@link RuleBaseEnvelope#setRuleBasesNames(LinkedHashSet)}</LI>
 * <LI>In {@link OperationsScript}, the method {@link #getRuleBasesNames()}
 * automatically retrieves the rule bases names from the keys
 * of the maps mentioned above, and from all rule-bases-envelope that
 * were created for <B>meta rule bases</B></LI>
 * <LI>In the initialization of {@link FeatureUpdate}, it
 * calls the method {@link FeatureVectorStructureOrganizer#getRuleBasesFeatures()}, which
 * returns a map from rule-bases-names to feature indexes.</LI>
 * <LI>The rule-base-name is also available in {@link SingleOperationItem}
 * for each item that is a rule-base-application operation.</LI>
 * <LI>In {@link TreesGeneratorByOperations}, in each rule
 * application operation that is being performed, the
 * rule-base-name is retrieved (either) by the name
 * stored in {@link SingleOperationItem}, or from the
 * rule base envelope by the method {@link RuleBaseEnvelope#getRuleBasesNames()}.
 * That name is given to {@link FeatureUpdate}, and since that
 * name exists in the map that was constructed by {@link FeatureVectorStructureOrganizer}
 * - it knows to which feature it has to put the corresponding value.</LI>
 * </UL>
 * 
 * 
 * @author Asher Stern
 * @since May 2, 2011
 *
 */
@NotThreadSafe
public class DefaultOperationScript extends OperationsScriptForBuiltinKnowledgeAndPlugins
{
	public static final int NUMBER_OF_FIRST_GLOBAL_ITERATIONS_IN_LOCAL_CREATIVE = BiuteeConstants.NUMBER_OF_FIRST_GLOBAL_ITERATIONS_IN_LOCAL_CREATIVE_IN_DEFAULT_OPERATION_SCRIPT;

	public DefaultOperationScript(ConfigurationFile configurationFile, PARSER parser, PluginRegistry pluginRegistry, boolean hybridGapMode)
	{
		super(configurationFile,parser,pluginRegistry);
		this.hybridGapMode = hybridGapMode;
	}


	@Override
	public void init() throws OperationException
	{
		super.init();

		// The otherIterationsList will include all operations
		// except few time-consuming operations (currently -
		// only coreference).
		// firstIterationsList will include all operations (all
		// the operations in otherIterationsList and also the
		// additional few time-consuming operations).

		List<SingleOperationItem> otherIterationsList = new ArrayList<SingleOperationItem>();
		
		// Add the on-the-fly operations.
		
//		GlobalMessages.globalWarn("Use on-the-fly in Hybrid mode. This is an experimental setting.", logger);
//		otherIterationsList.add(new SingleOperationItem(SingleOperationType.UNJUSTIFIED_INSERTION));
//		otherIterationsList.add(new SingleOperationItem(SingleOperationType.UNJUSTIFIED_MOVE));

		if (!hybridGapMode)
		{
			otherIterationsList.add(new SingleOperationItem(SingleOperationType.UNJUSTIFIED_INSERTION));
			otherIterationsList.add(new SingleOperationItem(SingleOperationType.UNJUSTIFIED_MOVE));
		}
		
		otherIterationsList.add(new SingleOperationItem(SingleOperationType.MULTIWORD_SUBSTITUTION));
		otherIterationsList.add(new SingleOperationItem(SingleOperationType.FLIP_POS_SUBSTITUTION));

		// Add an operation that flips the predicate-truth annotation.
		if (!hybridGapMode)
		{
			if (Constants.APPLY_CHANGE_ANNOTATION)
			{
				otherIterationsList.add(new SingleOperationItem(SingleOperationType.CHANGE_PREDICATE_TRUTH));
			}
		}

		// Add all knowledge resources
		for (ItemForKnowedgeResource item : items)
		{
			otherIterationsList.add(item.getSingleOperationItem());
		}


		// Add the plug-ins to the list of operations to be applied.
		for (String pluginToApplyId : pluginsToApply)
		{
			otherIterationsList.add(new SingleOperationItem(SingleOperationType.PLUGIN_APPLICATION, null, pluginToApplyId));
		}




		// Add some time-consuming operations, which will be applied only
		// in a limited number of iterations (of the search algorithm).
		List<SingleOperationItem> firstIterationsList;
		firstIterationsList = new ArrayList<SingleOperationItem>();
		firstIterationsList.add(new SingleOperationItem(SingleOperationType.PARSER_ANTECEDENT_SUBSTITUTION));
		firstIterationsList.add(new SingleOperationItem(SingleOperationType.COREFERENCE_SUBSTITUTION));
		firstIterationsList.add(new SingleOperationItem(SingleOperationType.IS_A_COREFERENCE_CONSTRUCTION));
		for (SingleOperationItem item : otherIterationsList)
		{
			firstIterationsList.add(item);
		}

		firstIterationsImList = new ImmutableListWrapper<SingleOperationItem>(firstIterationsList);
		otherIterationsImList = new ImmutableListWrapper<SingleOperationItem>(otherIterationsList);
	}
	
	@Override
	public void cleanUp()
	{
		super.cleanUp();
	}









	@Override
	protected ImmutableList<SingleOperationItem> getItemListForIterationImpl(int iterationIndex, Set<TreeAndFeatureVector> trees)
	{
		if (iterationIndex<FIRST_ITERATION_IN_DEFAULT_OPERATION_SCRIPT)
			return firstIterationsImList;
		else
			return otherIterationsImList;
	}

	@Override
	protected ImmutableList<SingleOperationItem> getItemListForLocalCreativeIterationImpl(int globalIteration, int localIteration, Set<TreeAndFeatureVector> trees)
	{
		if (
				(globalIteration<NUMBER_OF_FIRST_GLOBAL_ITERATIONS_IN_LOCAL_CREATIVE)
				&&
				(localIteration==0)
				)
			return firstIterationsImList;
		else
			return otherIterationsImList;
	}





	protected ImmutableList<SingleOperationItem> otherIterationsImList;
	protected ImmutableList<SingleOperationItem> firstIterationsImList;
	
	protected final boolean hybridGapMode;


	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DefaultOperationScript.class);
}
