package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged.localcreative;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged.KStagedAlgorithmException;
import eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged.StateCalculator;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTreeElement;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LookaheadChildrenGenerator;
import eu.excitementproject.eop.biutee.rteflow.micro.OperationsEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


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
						BiuteeConstants.LOCAL_CREATIVE_NUMBER_OF_LOCAL_ITERATIONS,
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
