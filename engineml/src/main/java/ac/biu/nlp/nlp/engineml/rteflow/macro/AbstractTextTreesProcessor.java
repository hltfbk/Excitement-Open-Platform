package ac.biu.nlp.nlp.engineml.rteflow.macro;

import java.util.List;
import java.util.Map;

import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.systems.TESystemEnvironment;
import ac.biu.nlp.nlp.engineml.script.OperationsScript;
import ac.biu.nlp.nlp.engineml.script.ScriptException;
import ac.biu.nlp.nlp.engineml.utilities.ProgressFire;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

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
