package ac.biu.nlp.nlp.engineml.rteflow.macro.search.kstaged;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.alignment.AlignmentCalculator;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier;
import ac.biu.nlp.nlp.engineml.datastructures.SingleItemSet;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.SingleTreeEvaluations;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeAndFeatureVector;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeHistory;
import ac.biu.nlp.nlp.engineml.rteflow.micro.OperationsEnvironment;
import ac.biu.nlp.nlp.engineml.rteflow.micro.TreesGeneratorByOperations;
import ac.biu.nlp.nlp.engineml.script.OperationsScript;
import ac.biu.nlp.nlp.engineml.script.ScriptException;
import ac.biu.nlp.nlp.engineml.script.SingleOperationItem;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

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
