package ac.biu.nlp.nlp.engineml.rteflow.macro.search.kstaged;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.rteflow.macro.search.local_creative.LocalCreativeTreeElement;
import ac.biu.nlp.nlp.engineml.rteflow.macro.search.local_creative.LookaheadChildrenGenerator;
import ac.biu.nlp.nlp.engineml.rteflow.micro.OperationsEnvironment;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.engineml.script.OperationsScript;
import ac.biu.nlp.nlp.engineml.script.ScriptException;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * 
 * @author Asher Stern
 * @since Oct 2, 2011
 *
 */
public class KStagedLocalCreativeStateCalculator implements StateCalculator<KStagedElement>
{
	public KStagedLocalCreativeStateCalculator(OperationsScript<Info, BasicNode> script,
			OperationsEnvironment operationsEnvironment,
			LinearClassifier linearClassifier)
	{
		super();
		this.script = script;
		this.operationsEnvironment = operationsEnvironment;
		this.linearClassifier = linearClassifier;
	}

	
	
	
	public boolean isGoal(KStagedElement state) throws KStagedAlgorithmException
	{
		return (0==state.getEvaluations().getMissingRelations());
	}

	public List<KStagedElement> generateChildren(KStagedElement state,
			Set<KStagedElement> closedList) throws KStagedAlgorithmException
	{
		try
		{
			LocalCreativeTreeElement lcElement = 
				new LocalCreativeTreeElement(
						state.getTree(),
						state.getHistory(),
						state.getFeatureVector(),
						0,
						state.getIteration(),
						null,
						state.getCost(),
						SimpleKStagedComparator.getGap(state.getEvaluations())
				);

			LookaheadChildrenGenerator generator =
				new LookaheadChildrenGenerator(
						lcElement,
						Constants.LOCAL_CREATIVE_NUMBER_OF_LOCAL_ITERATIONS,
						this.script,
						this.operationsEnvironment,
						this.linearClassifier
				);
			
			generator.generate();
			List<LocalCreativeTreeElement> lcGeneratedElements = generator.getGeneratedElements();
			
			List<KStagedElement> ret = new ArrayList<KStagedElement>(lcGeneratedElements.size());
			
			for (LocalCreativeTreeElement lcGenerated : lcGeneratedElements)
			{
				KStagedElement kStagedElement = ElementsConverter.create(lcGenerated, state.getOriginalSentence(), state.getEvaluationsOfOriginalTree(), state.getCostOfOriginalTree(), this.operationsEnvironment);
				ret.add(kStagedElement);
			}

			return ret;
		}
		catch (TeEngineMlException e)
		{
			throw new KStagedAlgorithmException("Local Creative Failed",e);
		} catch (OperationException e)
		{
			throw new KStagedAlgorithmException("Local Creative Failed",e);
		} catch (ScriptException e)
		{
			throw new KStagedAlgorithmException("Local Creative Failed",e);
		} catch (RuleBaseException e)
		{
			throw new KStagedAlgorithmException("Local Creative Failed",e);
		} catch (TreeAndParentMapException e)
		{
			throw new KStagedAlgorithmException("Local Creative Failed",e);
		} catch (ClassifierException e)
		{
			throw new KStagedAlgorithmException("Local Creative Failed",e);
		}
	}
	

	private OperationsScript<Info, BasicNode> script;
	private OperationsEnvironment operationsEnvironment;
	private LinearClassifier linearClassifier;
}
