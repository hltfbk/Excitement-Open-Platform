package ac.biu.nlp.nlp.engineml.rteflow.macro.search.kstaged.localcreative;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.rteflow.macro.search.kstaged.KStagedAlgorithmException;
import ac.biu.nlp.nlp.engineml.rteflow.macro.search.kstaged.StateCalculator;
import ac.biu.nlp.nlp.engineml.rteflow.macro.search.local_creative.LocalCreativeTreeElement;
import ac.biu.nlp.nlp.engineml.rteflow.macro.search.local_creative.LookaheadChildrenGenerator;
import ac.biu.nlp.nlp.engineml.rteflow.micro.OperationsEnvironment;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.engineml.script.OperationsScript;
import ac.biu.nlp.nlp.engineml.script.ScriptException;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Oct 31, 2011
 *
 */
public class KStagedLocalCreativeStateCalculator implements StateCalculator<KStagedLocalCreativeElement>
{
	public KStagedLocalCreativeStateCalculator(
			OperationsScript<Info, BasicNode> script,
			OperationsEnvironment operationsEnvironment,
			LinearClassifier classifier)
	{
		super();
		this.script = script;
		this.operationsEnvironment = operationsEnvironment;
		this.classifier = classifier;
	}

	public boolean isGoal(KStagedLocalCreativeElement state)
			throws KStagedAlgorithmException
	{
		return (0==state.getGap());
	}

	public List<KStagedLocalCreativeElement> generateChildren(
			KStagedLocalCreativeElement state,
			Set<KStagedLocalCreativeElement> closedList)
			throws KStagedAlgorithmException
	{
		try
		{
			LookaheadChildrenGenerator generator = 
				new LookaheadChildrenGenerator(
						state,
						Constants.LOCAL_CREATIVE_NUMBER_OF_LOCAL_ITERATIONS,
						this.script,
						this.operationsEnvironment,
						this.classifier);
			
			generator.generate();
			List<LocalCreativeTreeElement> generatedElements = generator.getGeneratedElements();
			List<KStagedLocalCreativeElement> ret = new ArrayList<KStagedLocalCreativeElement>(generatedElements.size());
			for (LocalCreativeTreeElement generatedElement : generatedElements)
			{
				ret.add(new KStagedLocalCreativeElement(
						generatedElement.getTree(), generatedElement.getHistory(), generatedElement.getFeatureVector(), 0, generatedElement.getGlobalIteration()+1, generatedElement.getAffectedNodes(), generatedElement.getCost(), generatedElement.getGap(), state,state.getOriginalSentence()));
			}
			
			return ret;
		}
		catch (TeEngineMlException e)
		{
			throw new KStagedAlgorithmException("children generation failed",e);
		} catch (OperationException e)
		{
			throw new KStagedAlgorithmException("children generation failed",e);
		} catch (ScriptException e)
		{
			throw new KStagedAlgorithmException("children generation failed",e);
		} catch (RuleBaseException e)
		{
			throw new KStagedAlgorithmException("children generation failed",e);
		} catch (TreeAndParentMapException e)
		{
			throw new KStagedAlgorithmException("children generation failed",e);
		} catch (ClassifierException e)
		{
			throw new KStagedAlgorithmException("children generation failed",e);
		}
	}

	private OperationsScript<Info, BasicNode> script;
	private OperationsEnvironment operationsEnvironment;
	private LinearClassifier classifier;
}
