package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.micro.OperationsEnvironment;
import eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.SingleOperationItem;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.datastructures.SingleItemSet;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Sep 2, 2011
 *
 */
public class KStagedStateCalculator implements StateCalculator<KStagedElement>
{
	public KStagedStateCalculator(OperationsScript<Info, BasicNode> script,
			OperationsEnvironment operationsEnvironment,
			LinearClassifier linearClassifier)
	{
		super();
		this.script = script;
		this.operationsEnvironment = operationsEnvironment;
		this.linearClassifier = linearClassifier;
	}

	public boolean isGoal(KStagedElement state)
	{
		return (0==state.getEvaluations().getMissingRelations());
	}

	public List<KStagedElement> generateChildren(KStagedElement state, Set<KStagedElement> closedList) throws KStagedAlgorithmException
	{
		try
		{
			TreeAndFeatureVector treeAndFeatureVector = new TreeAndFeatureVector(state.getTree(), state.getFeatureVector());
			Set<TreeAndFeatureVector> setTrees = new SingleItemSet<TreeAndFeatureVector>(treeAndFeatureVector);
			ImmutableList<SingleOperationItem> operations = script.getItemListForIteration(state.getIteration(), setTrees);
			TreesGeneratorByOperations generator = 
				new TreesGeneratorByOperations(
						treeAndFeatureVector,
						operations,
						this.script,
						state.getHistory(),
						operationsEnvironment
				);

			generator.generateTrees();
			Map<TreeAndFeatureVector,TreeHistory> historyMap = generator.getHistoryMap();
			Set<TreeAndFeatureVector> generatedTrees = generator.getGeneratedTrees();
			List<KStagedElement> listGenerated = new ArrayList<KStagedElement>(generatedTrees.size());
			for (TreeAndFeatureVector tree : generatedTrees)
			{
				TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree.getTree());
				// SingleTreeEvaluations generatedElementEvaluations = SingleTreeEvaluations.create(treeAndParentMap, operationsEnvironment.getHypothesis(), this.operationsEnvironment.getHypothesisLemmasLowerCase(), this.operationsEnvironment.getHypothesisNumberOfNodes());
				SingleTreeEvaluations generatedElementEvaluations = new AlignmentCalculator(operationsEnvironment.getAlignmentCriteria(), treeAndParentMap, operationsEnvironment.getHypothesis()).getEvaluations(this.operationsEnvironment.getHypothesisLemmasLowerCase(), this.operationsEnvironment.getHypothesisNumberOfNodes());
				double cost = -linearClassifier.getProduct(tree.getFeatureVector());
				KStagedElement generatedElement =
					new KStagedElement(
							tree.getTree(),
							historyMap.get(tree),
							tree.getFeatureVector(),
							state.getOriginalSentence(),
							cost,
							generatedElementEvaluations,
							state.getIteration()+1,
							state.getEvaluationsOfOriginalTree(),
							state.getCostOfOriginalTree()
					);

				listGenerated.add(generatedElement);
			}
			return listGenerated;
		}
		catch (TeEngineMlException e)
		{
			throw new KStagedAlgorithmException("Failed to generate trees.",e);
		} catch (TreeAndParentMapException e)
		{
			throw new KStagedAlgorithmException("Failed to generate trees.",e);
		} catch (ClassifierException e)
		{
			throw new KStagedAlgorithmException("Failed to generate trees.",e);
		} catch (OperationException e)
		{
			throw new KStagedAlgorithmException("Failed to generate trees.",e);
		} catch (ScriptException e)
		{
			throw new KStagedAlgorithmException("Failed to generate trees.",e);
		} catch (RuleBaseException e)
		{
			throw new KStagedAlgorithmException("Failed to generate trees.",e);
		}
	}
	

	
	private OperationsScript<Info, BasicNode> script;
	private OperationsEnvironment operationsEnvironment;
	private LinearClassifier linearClassifier;
}
