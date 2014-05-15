package eu.excitementproject.eop.biutee.rteflow.macro;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapDescription;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.ProgressFire;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * Implementation of {@link TextTreesProcessor}.
 * Process a list of text-trees - finds a low-cost proof of the hypothesis by those
 * test-trees.
 * <P>
 * This class first performs an initialization, by the function {@link #init()}, then proceeds
 * to find the proof, by the function {@link #processPair()}.
 * The initialization is defined by the super class {@link InitializationTextTreesProcessor}.
 *  
 * 
 * @author Asher Stern
 * @since Apr 12, 2011
 *
 */
public abstract class AbstractTextTreesProcessor extends InitializationTextTreesProcessor implements TextTreesProcessor
{
	public AbstractTextTreesProcessor(String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees,
			ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier, Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment
			) throws TeEngineMlException
	{
		super(textText, hypothesisText, originalTextTrees, hypothesisTree, originalMapTreesToSentences,
				coreferenceInformation, classifier, lemmatizer, script,
				teSystemEnvironment);
	}
	
	

	/**
	 * Used only by GUI
	 * @param percentageFire
	 */
	public void setProgressFire(ProgressFire percentageFire)
	{
		this.progressFire = percentageFire;
	}
	

	public void process() throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, TreeAndParentMapException, AnnotatorException
	{
		try
		{
			init();
			processPair();
		}
		finally
		{
			cleanUp();
		}
	}
	
	@Override
	public GapDescription getGapDescription() throws TeEngineMlException
	{
		if (teSystemEnvironment.getGapToolBox().isHybridMode())
		{
			throw new TeEngineMlException("getGapDescription() is not implemented for this processor.");
		}
		else
		{
			return null;
		}
	}
	
	
	@Override
	protected void init() throws TeEngineMlException, OperationException, TreeAndParentMapException, AnnotatorException
	{
		super.init();
		if (teSystemEnvironment.getGapToolBox().isHybridMode())
		{
			if (!capableForHybridGapMode())
			{
				throw new TeEngineMlException("This processor cannot work in hybrid gap mode.\n"
						+ "Either change the configuration file to use pure transformation mode, or use another processor.");
			}
		}
	}

	

	/**
	 * This method must be overridden by a processor that is capable of working in hybrid gap mode.
	 * @return
	 */
	protected boolean capableForHybridGapMode()
	{
		return false;
	}
	
	/**
	 * The actual work is done here.
	 * 
	 * @throws ClassifierException
	 * @throws TreeAndParentMapException
	 * @throws TeEngineMlException
	 * @throws OperationException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 */
	protected abstract void processPair() throws ClassifierException, TreeAndParentMapException, TeEngineMlException, OperationException, ScriptException, RuleBaseException;
	
	protected ProgressFire progressFire = null;
}
