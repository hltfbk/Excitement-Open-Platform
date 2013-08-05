package eu.excitementproject.eop.biutee.rteflow.macro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.TreeHistoryUtilities;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Aug 4, 2013
 *
 */
public abstract class AbstractFilterEnabledTextTreesProcessor extends AbstractTextTreesProcessor
{
	/**
	 * How many trees of the given text trees to process.
	 * If <=0 it means all.
	 */
	public static final int NUMBER_OF_TREES_TO_PROCESS = BiuteeConstants.FILTER_ENABELED_NUMBER_OF_TREES_TO_PROCESS;

	
	public AbstractFilterEnabledTextTreesProcessor(String textText,
			String hypothesisText, List<ExtendedNode> originalTextTrees,
			ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier, Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment) throws TeEngineMlException
	{
		super(textText, hypothesisText, originalTextTrees, hypothesisTree,
				originalMapTreesToSentences, coreferenceInformation, classifier,
				lemmatizer, script, teSystemEnvironment);
	}

	public TreeAndFeatureVector getBestTree() throws TeEngineMlException
	{
		return bestResult.getTree();
	}
	public String getBestTreeSentence() throws TeEngineMlException
	{
		return bestResult.getSentence();
	}
	public TreeHistory getBestTreeHistory() throws TeEngineMlException
	{
		return bestResult.getHistory();
	}


	@Override
	protected void processPair() throws ClassifierException,
			TreeAndParentMapException, TeEngineMlException, OperationException,
			ScriptException, RuleBaseException
	{
		
		logger.info("Processing T-H pair...");
		
		List<TreeAndIndex> treesToProcess = filterTreesByGap(originalTextTrees);
		numberOfTreesToBeProcessed = treesToProcess.size();
		
		prepareComputation();
		
		// computation
		ArrayList<TextTreesProcessingResult> results = new ArrayList<TextTreesProcessingResult>(treesToProcess.size());
		for (TreeAndIndex tree : treesToProcess)
		{
			logger.info("Processing sentence #"+tree.getIndex());
			//processTree(tree.getTree(),originalMapTreesToSentences.get(tree.getTree()));
			results.add(processSingleTree(tree));
		}
		if (results.size()==0)
			throw new TeEngineMlException("Bug: In "+AbstractFilterEnabledTextTreesProcessor.class.getSimpleName()+": results.size = 0");
		bestResult = findResultWithHighestConfidence(results);
		if (logger.isDebugEnabled())
		{
			logger.debug("Best result is:\nSentence = "+bestResult.getSentence()+
					"\nHistory = \n"+
					TreeHistoryUtilities.historyToString(bestResult.getHistory()));
		}
		logger.info("Processing T-H pair done.");
	}
	
	protected abstract void prepareComputation() throws TeEngineMlException;
	
	protected abstract TextTreesProcessingResult processSingleTree(TreeAndIndex tree) throws ClassifierException, TreeAndParentMapException, TeEngineMlException, OperationException, ScriptException, RuleBaseException;
	
	protected abstract double getHeuristicGap(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree, Map<Integer, Double> featureVector) throws GapException;
	
	
	protected TextTreesProcessingResult findResultWithHighestConfidence(List<TextTreesProcessingResult> results) throws ClassifierException
	{
		TextTreesProcessingResult bestResult = null;
		double bestConfidence = 0.0;
		for (TextTreesProcessingResult result : results)
		{
			double currentConfidence = this.classifier.classify(result.getTree().getFeatureVector());
			if (null==bestResult)
			{
				bestResult = result;
				bestConfidence = currentConfidence;
			}
			else
			{
				if (bestConfidence<currentConfidence)
				{
					bestResult = result;
					bestConfidence = currentConfidence;
				}
			}
		}
		return bestResult;
	}
	
	
	

	
	
	
	
	
	/**
	 * Returns a list of {@link TreeAndIndex} which contains the given trees, along
	 * with running indexes (0,1,2,...).
	 * <BR>
	 * Also, filters out some trees, if the constant NUMBER_OF_TREES_TO_PROCESS is a
	 * positive number. If so, the returned list contains only some of the given
	 * trees, where trees with high gap between them and the hypothesis are filtered
	 * out.
	 * 
	 * @param originalTrees
	 * @return
	 * @throws TreeAndParentMapException
	 * @throws TeEngineMlException
	 */
	@SuppressWarnings("unused")
	protected List<TreeAndIndex> filterTreesByGap(List<ExtendedNode> originalTrees) throws TreeAndParentMapException, TeEngineMlException
	{
		List<TreeAndIndex> ret = null;
		if (NUMBER_OF_TREES_TO_PROCESS<=0)
		{
			ret = new ArrayList<TreeAndIndex>(originalTrees.size());
			int treeIndex=0;
			for (ExtendedNode tree : originalTrees)
			{
				ret.add(new TreeAndIndex(tree, treeIndex));
				++treeIndex;
			}
		}
		else
		{
			Map<Integer,Double> initialFeatureVector = initialFeatureVector();			
			Map<TreeAndIndex, Double> mapTreesToGap = new LinkedHashMap<TreeAndIndex, Double>();
			int treeIndex=0;
			for (ExtendedNode tree : originalTrees)
			{
				TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree);
				mapTreesToGap.put(new TreeAndIndex(tree, treeIndex), getHeuristicGap(treeAndParentMap,initialFeatureVector));
				++treeIndex;
			}
			List<TreeAndIndex> sortedByGapTrees = Utils.getSortedByValue(mapTreesToGap);
			ret = new ArrayList<TreeAndIndex>(NUMBER_OF_TREES_TO_PROCESS);
			// add at least NUMBER_OF_TREES_TO_PROCESS trees. If the one-after-last tree has the same gap as last, add it too.
			boolean stop = false;
			TreeAndIndex previousTree = null;
			treeIndex=0;
			Iterator<TreeAndIndex> treesIterator = sortedByGapTrees.iterator();
			while ( (treesIterator.hasNext()) && (!stop) )
			{
				TreeAndIndex currentTree = treesIterator.next();
				if (treeIndex<NUMBER_OF_TREES_TO_PROCESS)
				{
					stop=false;
				}
				else
				{
					stop = true;
					if (previousTree!=null)
					{
						if (mapTreesToGap.get(previousTree).doubleValue()<mapTreesToGap.get(currentTree).doubleValue())
							stop = true;
						else if (mapTreesToGap.get(previousTree).doubleValue()==mapTreesToGap.get(currentTree).doubleValue())
							stop = false;
						else throw new TeEngineMlException("BUG");
					}
					else throw new TeEngineMlException("BUG");
				}
				if (!stop)
					ret.add(currentTree);
				previousTree=currentTree;
				treeIndex++;
			}
		}
		if ( (ret.size()<NUMBER_OF_TREES_TO_PROCESS) && (ret.size()<originalTrees.size()) )throw new TeEngineMlException("BUG");
		return ret;
	}

	protected int numberOfTreesToBeProcessed = 0;
	
	private TextTreesProcessingResult bestResult;
	private static final Logger logger = Logger.getLogger(AbstractFilterEnabledTextTreesProcessor.class);
}
