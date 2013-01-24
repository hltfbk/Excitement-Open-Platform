package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
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
						BiuteeConstants.LOCAL_CREATIVE_NUMBER_OF_LOCAL_ITERATIONS,
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
