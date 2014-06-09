package eu.excitementproject.eop.biutee.rteflow.macro;
import java.util.List;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.rteflow.document_sublayer.DocumentInitializer;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapDescription;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * Given list of parse-trees, created from the text, and a parse-tree created
 * from the hypothesis, this class applies generation-operations until
 * the hypothesis tree is generated (i.e. a proofs is found).
 * <BR>
 * After the process, the best tree (i.e. the tree that was generated,
 * and is identical to the hypothesis, using the lowest cost operations) can be
 * retrieved by {@link #getBestTree()}.
 * <P>
 * There are several implementations of this interface. However, most of them
 * share common properties, especially they share several initializations.
 * In any normal case, implementation can be a sub-class of {@link AbstractTextTreesProcessor}.
 * Even for the cases in which {@link AbstractTextTreesProcessor} cannot be used, it
 * is recommended that the implementation will be a sub-class of {@link InitializationTextTreesProcessor}.
 * <P>
 * Note also that some initializations are performed in {@link DocumentInitializer},
 * so don't jump from pre-processing to {@link TextTreesProcessor}, but rather
 * initialize your document in {@link DocumentInitializer}, and then proceed to
 * {@link TextTreesProcessor} (which, as noted, should begin with {@link InitializationTextTreesProcessor#init()}).
 *  
 * 
 * 
 * @see InitializationTextTreesProcessor
 * @see AbstractTextTreesProcessor
 * 
 * @author Asher Stern
 * @since Apr 5, 2011
 *
 */
public interface TextTreesProcessor
{
	public void process() throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, TreeAndParentMapException, AnnotatorException;
	
	public TreeAndFeatureVector getBestTree() throws TeEngineMlException;
	public String getBestTreeSentence() throws TeEngineMlException;
	public TreeHistory getBestTreeHistory() throws TeEngineMlException;
	public GapDescription getGapDescription() throws TeEngineMlException;
	
	/**
	 * This method can be called before calling {@link #process()}, if the
	 * caller wants to set a global information about the pair.
	 * <P>
	 * Global information is used to set values for features that do not
	 * represent the proof, but represent some characteristics about the pair. For example,
	 * task-name ("IE", "IR", "QA", "SUM"), which was available in old RTE data-sets can be
	 * given.
	 * @param information
	 */
	public void setGlobalPairInformation(GlobalPairInformation information);
	
	/**
	 * This method, if called, should be called before calling the method {@link #process()}.
	 * This method sets a list of trees that are considered as "exist in pair" for the given
	 * (T,H) pair.<BR>
	 * <B>Note</B> that the processed tree itself must be part of this list.
	 * 
	 * 
	 * @param surroundingsContext
	 * @throws TeEngineMlException
	 */
	public void setSurroundingsContext(List<ExtendedNode> surroundingsContext) throws TeEngineMlException;
}
